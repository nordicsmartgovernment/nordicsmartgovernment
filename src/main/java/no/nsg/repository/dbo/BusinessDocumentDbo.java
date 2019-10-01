package no.nsg.repository.dbo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.dnault.xmlpatch.Patcher;
import net.sf.saxon.s9api.SaxonApiException;
import no.nsg.repository.TransformationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
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
    private static Logger LOGGER = LoggerFactory.getLogger(BusinessDocumentDbo.class);

    public enum DocumentFormat {
        UNKNOWN,
        UML_INVOICE,
        CAMT053,
        FINVOICE
    }

    public static final int UNINITIALIZED = 0;

    public static final int DOCUMENTTYPE_INVOICE = 1;
    public static final int DOCUMENTTYPE_BANKSTATEMENT = 2;

    @JsonIgnore
    private int _id;

    @JsonIgnore
    private int _id_transaction;

    @JsonIgnore
    private int _id_journal;

    @JsonIgnore
    private Integer documenttype;

    private String documentid;

    private byte[] original;

    private String xbrl;

    @JsonIgnore
    private TransformationManager.Direction tmpDirection = null; //Not persisted - only for forwarding info from EntryDbo to TrasactionDbo

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

        final String sql = "SELECT _id_transaction, _id_journal, documenttype, documentid, original, xbrl FROM nsg.businessdocument WHERE _id=?";
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

            setDocumenttype(rs.getInt("documenttype"));
            if (rs.wasNull()) {
                setDocumenttype(null);
            }

            setDocumentid(rs.getString("documentid"));
            setOriginalAndXbrl(rs.getBinaryStream("original"), rs.getCharacterStream("xbrl"));
        }
    }

    public int get_id() {
        return this._id;
    }

    public void set_TransactionId(final int _id_transaction) {
        this._id_transaction = _id_transaction;
    }

    private int get_TransactionId() {
        return this._id_transaction;
    }

    public void set_JournalId(final int _id_journal) {
        this._id_journal = _id_journal;
    }

    private int get_JournalId() {
        return this._id_journal;
    }

    public Integer getDocumenttype() {
        return documenttype;
    }

    public void setDocumenttype(final Integer documenttype) {
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

    public void setOriginalFromString(final String original) throws IOException, SAXException {
        setOriginalFromString(null, original);
    }

    public void setOriginalFromString(final String companyId, final String original) throws IOException, SAXException {
        this.original = original.getBytes(StandardCharsets.UTF_8);
        BusinessDocumentDbo.DocumentFormat documentFormat = getDocumentFormat(original);
        setDirectionFromDocument(companyId, documentFormat, original);
        transformXbrlFromOriginal(documentFormat, tmpDirection);
        insertDocumentIdInXbrlDocument();
    }

    private void insertDocumentIdInXbrlDocument() throws IOException, SAXException {
        patchXbrl("<diff xmlns:xbrli=\"http://www.xbrl.org/2003/instance\" xmlns:gl-cor=\"http://www.xbrl.org/int/gl/cor/2015-03-25\">" +
                    "<add pos=\"prepend\" sel=\"xbrli:xbrl/gl-cor:accountingEntries/gl-cor:documentInfo\">\n         <gl-cor:uniqueID>"+getDocumentid()+"</gl-cor:uniqueID></add>" +
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

    public String getXbrl() {
        return xbrl;
    }

    private void transformXbrlFromOriginal(DocumentFormat documentFormat, final TransformationManager.Direction direction) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            TransformationManager.transform(new ByteArrayInputStream(this.original), documentFormat, direction, baos);
            this.xbrl = baos.toString(StandardCharsets.UTF_8.name());
        } catch (SaxonApiException e) {
            LOGGER.info("Invoice failed converting to XBRL");
            this.xbrl = null;
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Converting to XBRL using unsupported encoding");
        }
    }

    private DocumentFormat getDocumentFormat(final String document) {
        if (document.contains("<Finvoice ")) {
            return DocumentFormat.FINVOICE;
        } else if (document.contains("xmlns=\"urn:iso:std:iso:20022:tech:xsd:camt.053.")) {
            return DocumentFormat.CAMT053;
        } else if (document.contains("xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\"") || document.contains("<Invoice ")) {
            return DocumentFormat.UML_INVOICE;
        } else {
            return DocumentFormat.UNKNOWN;
        }
    }

    private String getOrgnrFromXBRL() throws IOException, SAXException {
        Document parsedDocument = parseDocument(getXbrl());
        if (parsedDocument != null) {
            NodeList nodes = parsedDocument.getElementsByTagName("gl-cor:identifierAuthorityCode");
            if (nodes.getLength() > 0) {
                Node child = nodes.item(0).getFirstChild();
                if (child != null) {
                    return child.getTextContent();
                }
            }
        }
        return null;
    }

    private void setDirectionFromDocument(final String companyId, final BusinessDocumentDbo.DocumentFormat documentFormat, final String document) throws IOException, SAXException {
        if (documentFormat != BusinessDocumentDbo.DocumentFormat.UML_INVOICE) {
            tmpDirection = TransformationManager.Direction.DOESNT_MATTER;
            return;
        }

        String supplier="", customer="";

        Document parsedDocument = parseDocument(document);
        if (parsedDocument != null) {
            Node child = parsedDocument.getElementsByTagName("cac:AccountingSupplierParty").item(0);
            if (child instanceof Element) {
                child = ((Element)child).getElementsByTagName("cac:PartyLegalEntity").item(0);
                if (child instanceof Element) {
                    child = ((Element)child).getElementsByTagName("cbc:CompanyID").item(0);
                    if (child != null) {
                        supplier = child.getTextContent();
                        if (companyId.equalsIgnoreCase(child.getTextContent())) {
                            tmpDirection = TransformationManager.Direction.SALES;
                            return;
                        }
                    }
                }
            }

            child = parsedDocument.getElementsByTagName("cac:AccountingCustomerParty").item(0);
            if (child instanceof Element && tmpDirection!=TransformationManager.Direction.SALES) {
                child = ((Element)child).getElementsByTagName("cac:PartyLegalEntity").item(0);
                if (child instanceof Element) {
                    child = ((Element)child).getElementsByTagName("cbc:CompanyID").item(0);
                    if (child != null) {
                        customer = child.getTextContent();
                        if (companyId.equalsIgnoreCase(child.getTextContent())) {
                            tmpDirection = TransformationManager.Direction.PURCHASE;
                            return;
                        }
                    }
                }
            }
        }

        throw new RuntimeException("customerId was neither supplier:"+supplier+" nor customer:"+customer);
    }

    public String patchXbrl(final String patchXml) throws IOException, SAXException {
        ByteArrayInputStream originalIS = new ByteArrayInputStream(getXbrl().getBytes(StandardCharsets.UTF_8));
        ByteArrayInputStream diffIS = new ByteArrayInputStream(patchXml.getBytes(StandardCharsets.UTF_8));
        OutputStream result = new ByteArrayOutputStream();
        Patcher.patch(originalIS, diffIS, result);
        this.xbrl = result.toString();
        parseXbrl();
        return getXbrl();
    }

    private void parseXbrl() throws IOException, SAXException {
        entryRows.clear();
        removeOldEntries = (get_id()!=BusinessDocumentDbo.UNINITIALIZED); //Remove old entries unless there can't possibly be any

        Document parsedDocument = parseDocument(getXbrl());
        if (parsedDocument != null) {
            NodeList nodes = parsedDocument.getElementsByTagName("gl-cor:entryDetail");
            for (int i=0; i<nodes.getLength(); i++) {
                entryRows.add(new EntryDbo(parsedDocument, nodes.item(i)));
            }
        }
    }

    private static Document parseDocument(final String document) throws IOException, SAXException {
        if (document == null) {
            return null;
        }

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
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

    private TransactionDbo getOrInitializeTransaction(final Connection connection, final String transactionSetName) throws SQLException, IOException, SAXException {
        if (get_TransactionId() != TransactionDbo.UNINITIALIZED) {
            return new TransactionDbo(connection, get_TransactionId());
        }

        String orgnr = getOrgnrFromXBRL();
        if (orgnr == null) {
            LOGGER.info("Couldn't find XBRL organizationIdentifier");
            return null;
        }

        TransactionDbo transactionDbo = TransactionDbo.create(connection, orgnr, transactionSetName);
        set_TransactionId(transactionDbo.get_id());
        return transactionDbo;
    }

    public void persist(final Connection connection) throws SQLException, IOException, SAXException {
        TransactionDbo transactionDbo = getOrInitializeTransaction(connection, TransactionSetDbo.DEFAULT_NAME);
        if (transactionDbo==null | transactionDbo.get_id()==TransactionDbo.UNINITIALIZED) {
            throw new NoSuchElementException();
        }

        if (get_id() == UNINITIALIZED) {
            final String sql = "INSERT INTO nsg.businessdocument (_id_transaction, _id_journal, documenttype, documentid, original, xbrl) " +
                                      "VALUES (?,?,?,?,?,?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                 ByteArrayInputStream originalBais = new ByteArrayInputStream(getOriginal());
                 Reader xbrlReader = new StringReader(xbrl)) {

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
                    stmt.setInt(3, getDocumenttype());
                } else {
                    stmt.setNull(3, Types.INTEGER);
                }

                stmt.setString(4, getDocumentid());
                stmt.setBinaryStream(5, originalBais, originalBais.available());
                stmt.setCharacterStream(6, xbrlReader);

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this._id = rs.getInt(1);
                }
            }
        } else {
            final String sql = "UPDATE nsg.businessdocument SET _id_transaction=?, _id_journal=?, documenttype=?, documentid=?, original=?, xbrl=? "+
                                                "WHERE _id=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ByteArrayInputStream originalBais = new ByteArrayInputStream(getOriginal());
                 Reader xbrlReader = new StringReader(xbrl)) {

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
                    stmt.setInt(3, getDocumenttype());
                } else {
                    stmt.setNull(3, Types.INTEGER);
                }

                stmt.setString(4, getDocumentid());
                stmt.setBinaryStream(5, originalBais, originalBais.available());
                stmt.setCharacterStream(6, xbrlReader);
                stmt.setInt(7, get_id());

                stmt.executeUpdate();
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
}
