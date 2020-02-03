package no.nsg.repository.dbo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.dnault.xmlpatch.Patcher;
import net.sf.saxon.s9api.SaxonApiException;
import no.nsg.repository.DocumentType;
import no.nsg.repository.TransformationManager;
import no.nsg.repository.document.FormatFactory;
import no.nsg.repository.document.formats.DocumentFormat;
import no.nsg.repository.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


@Component
@JsonIgnoreProperties({"id"}) /* Default serialization insists on appending this lowercase id element?!? We do not want it */
public class BusinessDocumentDbo {
    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessDocumentDbo.class);

    public static final int UNINITIALIZED = 0;

    @JsonIgnore
    private int _id;

    @JsonIgnore
    private int _id_transaction;

    @JsonIgnore
    private int _id_journal;

    @JsonIgnore
    private DocumentType.Type documenttype;

    private String documentid;

    private byte[] original;

    private String xbrl;

    @JsonIgnore
    private boolean isSynthetic = false;

    @JsonIgnore
    private TransformationManager.Direction tmpDirection = null; //Not persisted - only for forwarding info from EntryDbo to TrasactionDbo
    @JsonIgnore
    private String companyId = null; //Not persisted - only for forwarding info from EntryDbo to TrasactionDbo
    @JsonIgnore
    private String referencedCompanyId = null; //Not persisted - only for forwarding info from EntryDbo to TrasactionDbo

    @JsonIgnore
    private LocalDateTime tmpTransactionTime = null; //Not persisted - only for forwarding info from DocumentRowDbo to TrasactionDbo

    @JsonIgnore
    List<EntryDbo> entryRows = new ArrayList<>();
    @JsonIgnore
    boolean removeOldEntries = false;


    public BusinessDocumentDbo() {
        this._id = UNINITIALIZED;
        generateDocumentid();
        set_TransactionId(TransactionDbo.UNINITIALIZED);
        set_JournalId(JournalDbo.UNINITIALIZED);
    }

    public BusinessDocumentDbo(final TransactionDbo transactionDbo) {
        this._id = UNINITIALIZED;
        generateDocumentid();
        set_TransactionId(transactionDbo == null ? TransactionDbo.UNINITIALIZED : transactionDbo.get_id());
        set_JournalId(JournalDbo.UNINITIALIZED);
    }

    public BusinessDocumentDbo(final JournalDbo journalDbo) {
        this._id = UNINITIALIZED;
        generateDocumentid();
        set_TransactionId(TransactionDbo.UNINITIALIZED);
        set_JournalId(journalDbo == null ? JournalDbo.UNINITIALIZED : journalDbo.get_id());
    }

    public BusinessDocumentDbo(final Connection connection, final int id) throws SQLException, IOException {
        if (id == UNINITIALIZED) {
            throw new NoSuchElementException();
        }

        final String sql = "SELECT _id_transaction, _id_journal, documenttype, documentid, original, xbrl, issynthetic FROM nsg.businessdocument WHERE _id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new NoSuchElementException();
            }

            this._id = id;

            set_TransactionId(rs.getInt("_id_transaction"));

            set_JournalId(rs.getInt("_id_journal"));
            if (rs.wasNull()) {
                set_JournalId(TransactionDbo.UNINITIALIZED);
            }

            setDocumenttype(DocumentType.fromInteger(rs.getInt("documenttype")));
            if (rs.wasNull()) {
                setDocumenttype(null);
            }

            setDocumentid(rs.getString("documentid"));
            setOriginalAndXbrl(rs.getBinaryStream("original"), rs.getCharacterStream("xbrl"));

            isSynthetic = rs.getBoolean("issynthetic");
        }
    }

    public int get_id() {
        return this._id;
    }

    public void setCompanyId(final String companyId) {
        this.companyId = companyId;
    }

    public void set_TransactionId(final int _id_transaction) {
        this._id_transaction = _id_transaction;
    }

    private int get_TransactionId() {
        return this._id_transaction;
    }

    private TransactionDbo getTransaction(final TransactionManager transactionManager) throws SQLException {
        if (get_TransactionId() == TransactionDbo.UNINITIALIZED) {
            return null;
        }
        return transactionManager.getTransactionById(get_TransactionId());
    }

    public void set_JournalId(final int _id_journal) {
        this._id_journal = _id_journal;
    }

    private int get_JournalId() {
        return this._id_journal;
    }

    public DocumentType.Type getDocumenttype() {
        return documenttype;
    }

    private void setDocumenttype(final DocumentType.Type documenttype) {
        this.documenttype = documenttype;
    }

    private void setDocumentid(final String documentid) {
        this.documentid = documentid;
    }

    private void generateDocumentid() {
        if (getDocumentid() == null) {
            this.documentid = UUID.randomUUID().toString();
        }
    }

    public String getDocumentid() {
        return documentid;
    }

    public byte[] getOriginal() {
        return original;
    }

    public void setOriginalFromString(final DocumentType.Type documentType, final String companyId, final String transactionId, final String original) throws IOException, SAXException {
        setCompanyId(companyId);
        this.original = original.getBytes(StandardCharsets.UTF_8);
        setDocumenttype(documentType);
        DocumentFormat.Format documentFormat = FormatFactory.guessFormat(documentType, original);
        if (!FormatFactory.isCompatible(documentType, documentFormat)) {
            throw new IllegalArgumentException("Document format seems to not match given document type");
        }

        setDirectionAndTransactionTimeFromDocument(documentType, documentFormat, original);
        if (documentFormat != DocumentFormat.Format.OTHER) {
            transformXbrlFromOriginal(documentFormat);
            insertDocumentIdInXbrlDocument(BusinessDocumentDbo.getLocationString(this.companyId, transactionId, getDocumentid()));
        }
    }

    private void insertDocumentIdInXbrlDocument(final String documentId) {
        patchXbrl("<diff xmlns:xbrli=\"http://www.xbrl.org/2003/instance\" xmlns:gl-cor=\"http://www.xbrl.org/int/gl/cor/2016-12-01\">" +
                    "<add pos=\"prepend\" sel=\"xbrli:xbrl/gl-cor:accountingEntries/gl-cor:documentInfo\">\n         <gl-cor:uniqueID>"+documentId+"</gl-cor:uniqueID></add>" +
                  "</diff>");
    }

    private void setOriginalAndXbrl(final InputStream original, final Reader xbrl) throws IOException {
        try (ByteArrayOutputStream bs = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[10 * 1024];
            int length;
            while ((length = original.read(buffer)) != -1) {
                bs.write(buffer, 0, length);
            }
            this.original = bs.toByteArray();
        }

        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[10 * 1024];
        int length;
        while ((length = xbrl.read(buffer)) != -1) {
            sb.append(buffer, 0, length);
        }
        this.xbrl = sb.toString();
    }

    public void setXbrl(final String xbrl) throws IOException, SAXException {
        this.xbrl = xbrl;
        parseXbrl();
    }

    public String getXbrl() {
        return xbrl;
    }

    public void setIsSynthetic() {
        this.isSynthetic = true;
    }

    private void transformXbrlFromOriginal(final DocumentFormat.Format documentFormat) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            TransformationManager.transform(new ByteArrayInputStream(this.original), documentFormat, baos);
            this.xbrl = baos.toString(StandardCharsets.UTF_8.name());
        } catch (SaxonApiException e) {
            LOGGER.info("Invoice failed converting to XBRL");
            this.xbrl = null;
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Converting to XBRL using unsupported encoding");
        }
    }

    private void setDirectionAndTransactionTimeFromDocument(final DocumentType.Type documentType, final DocumentFormat.Format documentFormat, final String document) throws IOException, SAXException {
        if (DocumentType.isOther(documentType)) {
            return;
        }

        Document parsedDocument = parseDocument(document);
        DocumentFormat docType = FormatFactory.create(documentFormat);

        tmpTransactionTime = docType.getTransactionTime(parsedDocument);

        if (!DocumentType.hasDirection(documentType)) {
            tmpDirection = TransformationManager.Direction.DOESNT_MATTER;
            return;
        }

        if (this.companyId == null) {
            tmpDirection = TransformationManager.Direction.DONT_KNOW;
            return;
        }

        String supplier = docType.getDocumentSupplier(parsedDocument);
        String customer = docType.getDocumentCustomer(parsedDocument);

        if (DocumentType.isSales(documentType)) {
            if (this.companyId.equalsIgnoreCase(supplier)) {
                tmpDirection = TransformationManager.Direction.SALES;
                referencedCompanyId = customer;
            } else {
                throw new IllegalArgumentException("customerId (" + this.companyId + ") was not supplier (" + supplier + ")");
            }
        } else if (DocumentType.isPurchase(documentType)) {
            if (this.companyId.equalsIgnoreCase(customer)) {
                tmpDirection = TransformationManager.Direction.PURCHASE;
                referencedCompanyId = supplier;
            } else {
                throw new IllegalArgumentException("customerId (" + this.companyId + ") was not customer (" + customer + ")");
            }
        } else {
            throw new IllegalArgumentException("Unexpected document type for direction detection: "+DocumentType.toMimeType(documentType));
        }
    }

    public String patchXbrl(final String patchXml) {
        ByteArrayInputStream originalIS = new ByteArrayInputStream(getXbrl().getBytes(StandardCharsets.UTF_8));
        ByteArrayInputStream diffIS = new ByteArrayInputStream(patchXml.getBytes(StandardCharsets.UTF_8));
        OutputStream result = new ByteArrayOutputStream();
        try {
            Patcher.patch(originalIS, diffIS, result);
            this.xbrl = result.toString();
            parseXbrl();
            return getXbrl();
        } catch (Exception e) {
            LOGGER.info("Patching failed: "+e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }

    private void parseXbrl() throws IOException, SAXException {
        entryRows.clear();
        removeOldEntries = (get_id()!=BusinessDocumentDbo.UNINITIALIZED); //Remove old entries unless there can't possibly be any

        Document parsedDocument = parseDocument(getXbrl());
        if (parsedDocument != null) {
            NodeList nodes = parsedDocument.getElementsByTagNameNS(TransformationManager.GL_COR_NS, "entryDetail");
            for (int i=0; i<nodes.getLength(); i++) {
                entryRows.add(new EntryDbo(parsedDocument, nodes.item(i)));
            }
        }
    }

    public static Document parseDocument(final String document) throws IOException, SAXException {
        if (document == null) {
            return null;
        }

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder builder;
        try {
            builder = builderFactory.newDocumentBuilder();
            Document parsedDocument;
            try (ByteArrayInputStream bais = new ByteArrayInputStream(document.getBytes(StandardCharsets.UTF_8))) {
                parsedDocument = builder.parse(bais);
            }

            parsedDocument.getDocumentElement().normalize();
            return parsedDocument;
        } catch (ParserConfigurationException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public static String documentToString(final Document document) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (TransformerException e) {
            LOGGER.info("documentToString failed: " + e.getMessage());
            return null;
        }
    }

    public TransactionDbo connectToTransaction(final Connection connection, final String transactionId) throws SQLException {
        TransactionDbo transactionDbo;

        if (transactionId==null) {
            transactionDbo = TransactionDbo.create(connection, this.companyId, TransactionSetDbo.DEFAULT_NAME);
        } else {
            Integer tmpId = TransactionDbo.findByTransactionId(connection, transactionId);
            if (tmpId == null || tmpId == TransactionDbo.UNINITIALIZED) {
                throw new NoSuchElementException();
            }
            transactionDbo = new TransactionDbo(connection, tmpId);
        }

        set_TransactionId(transactionDbo.get_id());
        return transactionDbo;
    }

    public void persist(final Connection connection) throws SQLException, IOException {
        TransactionDbo transactionDbo;
        try {
            transactionDbo = new TransactionDbo(connection, get_TransactionId());
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Document is not connected to an existing transaction");
        }

        if (get_id() == UNINITIALIZED) {
            final String sql = "INSERT INTO nsg.businessdocument (_id_transaction, _id_journal, documenttype, documentid, original, xbrl, issynthetic) " +
                                      "VALUES (?,?,?,?,?,?,?)";
            final String xbrl = getXbrl();
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                 ByteArrayInputStream originalBais = new ByteArrayInputStream(getOriginal());
                 Reader xbrlReader = (xbrl == null) ? null : new StringReader(xbrl)) {

                if (get_TransactionId() != TransactionDbo.UNINITIALIZED) {
                    stmt.setInt(1, get_TransactionId());
                } else {
                    stmt.setNull(1, Types.INTEGER);
                }

                if (get_JournalId() != JournalDbo.UNINITIALIZED) {
                    stmt.setInt(2, get_JournalId());
                } else {
                    stmt.setNull(2, Types.INTEGER);
                }

                if (getDocumenttype() != null) {
                    stmt.setInt(3, DocumentType.toInt(getDocumenttype()));
                } else {
                    stmt.setNull(3, Types.INTEGER);
                }

                stmt.setString(4, getDocumentid());
                stmt.setBinaryStream(5, originalBais, originalBais.available());

                if (xbrlReader != null) {
                    stmt.setCharacterStream(6, xbrlReader);
                }else {
                    stmt.setNull(6, Types.CLOB);
                }

                stmt.setBoolean(7, this.isSynthetic);

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this._id = rs.getInt(1);
                }
            }
        } else {
            final String sql = "UPDATE nsg.businessdocument SET _id_transaction=?, _id_journal=?, documenttype=?, documentid=?, original=?, xbrl=?, issynthetic=? "+
                                                "WHERE _id=?";
            final String xbrl = getXbrl();
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ByteArrayInputStream originalBais = new ByteArrayInputStream(getOriginal());
                 Reader xbrlReader = (xbrl == null) ? null : new StringReader(xbrl)) {

                if (get_TransactionId() != TransactionDbo.UNINITIALIZED) {
                    stmt.setInt(1, get_TransactionId());
                } else {
                    stmt.setNull(1, Types.INTEGER);
                }

                if (get_JournalId() != JournalDbo.UNINITIALIZED) {
                    stmt.setInt(2, get_JournalId());
                } else {
                    stmt.setNull(2, Types.INTEGER);
                }

                if (getDocumenttype() != null) {
                    stmt.setInt(3, DocumentType.toInt(getDocumenttype()));
                } else {
                    stmt.setNull(3, Types.INTEGER);
                }

                stmt.setString(4, getDocumentid());
                stmt.setBinaryStream(5, originalBais, originalBais.available());

                if (xbrlReader != null) {
                    stmt.setCharacterStream(6, xbrlReader);
                }else {
                    stmt.setNull(6, Types.CLOB);
                }

                stmt.setBoolean(7, this.isSynthetic);

                stmt.setInt(8, get_id());

                if (stmt.executeUpdate() == 0) {
                    LOGGER.error("BusinessDocumentDbo executeUpdate returned 0");
                }
            }
        }

        //Remove old rows if we've got a new document
        if (removeOldEntries) {
            EntryDbo.deleteDocumentRows(connection, this);
            removeOldEntries = false;
        }

        //Persist any new document rows
        for (EntryDbo entryDbo : this.entryRows) {
            if (entryDbo.get_id() == EntryDbo.UNINITIALIZED) {
                entryDbo.set_BusinessDocumentId(get_id());
                entryDbo.persist(connection);
            }
        }

        //Update transaction if direction or transaction time is changed
        boolean modifiedTransaction = false;
        if (tmpDirection!=null && transactionDbo.getDirection()!=tmpDirection) {
            transactionDbo.setDirection(tmpDirection);
            modifiedTransaction = true;
        }

        transactionDbo.set_ReferencedCompanyId(CompanyDbo.findByOrgno(connection, referencedCompanyId));

        if (tmpTransactionTime!=null && transactionDbo.getTransactionTime()!=tmpTransactionTime) {
            transactionDbo.setTransactionTime(tmpTransactionTime);
            modifiedTransaction = true;
        }
        if (modifiedTransaction) {
            transactionDbo.persist(connection);
        }
    }

    public static int findInternalId(final Connection connection, final String id) throws SQLException {
        final String sql = "SELECT _id FROM nsg.businessdocument WHERE documentid=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("_id");
            }
        }
        throw new NoSuchElementException();
    }

    public URI getLocation(final TransactionManager transactionManager) {
        TransactionDbo transaction;
        try {
            transaction = getTransaction(transactionManager);
            return new URI(BusinessDocumentDbo.getLocationString(this.companyId, transaction.getTransactionid(), getDocumentid()));
        } catch (Exception e) {
            throw new RuntimeException("GetLocation failed: "+e.getMessage());
        }
    }

    public static String getLocationString(final String companyId, final String transactionId, final String documentId) throws UnsupportedEncodingException {
        return "/document/" + URLEncoder.encode(companyId, "utf-8") + "/" + transactionId + "/" + documentId;
    }
}
