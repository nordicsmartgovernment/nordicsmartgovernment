package no.nsg.repository.dbo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.sf.saxon.s9api.SaxonApiException;
import no.nsg.repository.TransformationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.NoSuchElementException;


@Component
@JsonIgnoreProperties({"id"}) /* Default serialization insists on appending this lowercase id element?!? We do not want it */
public class DocumentDbo {
    private static Logger LOGGER = LoggerFactory.getLogger(DocumentDbo.class);

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
    private int _transactionid;

    @JsonIgnore
    private int documenttype;

    private String documentid;

    private byte[] original;
    private String xbrl;


    public DocumentDbo() {
        this._id = UNINITIALIZED;
        _transactionid = TransactionDbo.UNINITIALIZED;
    }

    public DocumentDbo(final TransactionDbo transactionDbo) {
        this._id = UNINITIALIZED;
        set_TransactionId(transactionDbo == null ? TransactionDbo.UNINITIALIZED : transactionDbo.get_id());
    }

    public DocumentDbo(final Connection connection, final int _id) throws SQLException, IOException {
        if (_id == UNINITIALIZED) {
            throw new NoSuchElementException();
        }

        final String sql = "SELECT _transactionid, documenttype, documentid, original, xbrl FROM nsg.document WHERE _id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, _id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new NoSuchElementException();
            }

            this._id = _id;
            set_TransactionId(rs.getInt("_transactionid"));
            if (rs.wasNull()) {
                set_TransactionId(TransactionDbo.UNINITIALIZED);
            }

            setDocumenttype(rs.getInt("documenttype"));
            setDocumentid(rs.getString("documentid"));
            setOriginalAndXbrl(rs.getBinaryStream("original"), rs.getCharacterStream("xbrl"));
        }
    }

    public int get_id() {
        return this._id;
    }

    public void set_TransactionId(final int _transactionid) {
        this._transactionid = _transactionid;
    }

    public int getDocumenttype() {
        return documenttype;
    }

    public void setDocumentid(final String documentid) {
        this.documentid = documentid;
    }

    public String getDocumentid() {
        return documentid;
    }

    public void setDocumenttype(final int documenttype) {
        this.documenttype = documenttype;
    }

    public byte[] getOriginal() {
        return original;
    }

    public void setOriginalFromString(final String original, final TransformationManager.Direction direction) throws IOException, SAXException {
        this.original = original.getBytes(StandardCharsets.UTF_8);
        transformXbrlFromOriginal(getDocumentFormat(original), direction);
        setDocumentid(getDocumentidFromXBRL());
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
        Document document = getXbrlAsDocument();
        if (document != null) {
            NodeList nodes = document.getElementsByTagName("gl-cor:identifierAuthorityCode");
            if (nodes.getLength() > 0) {
                Node child = nodes.item(0).getFirstChild();
                if (child != null) {
                    return child.getTextContent();
                }
            }
        }
        return null;
    }

    private String getDocumentidFromXBRL() throws IOException, SAXException {
        Document document = getXbrlAsDocument();
        if (document != null) {
            NodeList nodes = document.getElementsByTagName("gl-cor:documentNumber");
            if (nodes.getLength() > 0) {
                Node child = nodes.item(0).getFirstChild();
                if (child != null) {
                    return child.getTextContent();
                }
            }
        }
        return null;
    }

    private Document getXbrlAsDocument() throws IOException, SAXException {
        if (xbrl == null) {
            return null;
        }

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = builderFactory.newDocumentBuilder();
            Document document;
            try (ByteArrayInputStream bais = new ByteArrayInputStream(getXbrl().getBytes(StandardCharsets.UTF_8))) {
                document = builder.parse(bais);
            }

            document.getDocumentElement().normalize();
            return document;
        } catch (ParserConfigurationException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    private int initializeTransaction(final Connection connection) throws SQLException, IOException, SAXException {
        if (_transactionid != TransactionDbo.UNINITIALIZED) {
            return _transactionid;
        }

        String orgnr = getOrgnrFromXBRL();
        if (orgnr == null) {
            LOGGER.info("Couldn't find XBRL organizationIdentifier");
            return TransactionDbo.UNINITIALIZED;
        }

        CompanyDbo companyDbo = CompanyDbo.getOrCreateByOrgno(connection, orgnr);
        TransactionDbo transactionDbo = new TransactionDbo(companyDbo);
        transactionDbo.persist(connection);
        return transactionDbo.get_id();
    }

    public void persist(final Connection connection) throws SQLException, IOException, SAXException {
        if (_transactionid == TransactionDbo.UNINITIALIZED) {
            if ((_transactionid = initializeTransaction(connection)) == TransactionDbo.UNINITIALIZED) {
                throw new NoSuchElementException();
            }
        }

        if (get_id() == UNINITIALIZED) {
            final String sql = "INSERT INTO nsg.document (_transactionid, documenttype, documentid, original, xbrl) " +
                                      "VALUES (?,?,?,?,?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                 ByteArrayInputStream originalBais = new ByteArrayInputStream(getOriginal());
                 Reader xbrlReader = new StringReader(xbrl)) {
                stmt.setInt(1, _transactionid);
                stmt.setInt(2, getDocumenttype());
                stmt.setString(3, getDocumentid());
                stmt.setBinaryStream(4, originalBais, originalBais.available());
                stmt.setCharacterStream(5, xbrlReader);

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this._id = rs.getInt(1);
                }
            }
        } else {
            final String sql = "UPDATE nsg.document SET _transactionid=?, documenttype=?, documentid=?, original=?, xbrl=?) "+
                                                "WHERE _id=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ByteArrayInputStream originalBais = new ByteArrayInputStream(getOriginal());
                 Reader xbrlReader = new StringReader(xbrl)) {
                stmt.setInt(1, _transactionid);
                stmt.setInt(2, getDocumenttype());
                stmt.setString(3, getDocumentid());
                stmt.setBinaryStream(4, originalBais, originalBais.available());
                stmt.setCharacterStream(5, xbrlReader);
                stmt.setInt(6, get_id());

                stmt.executeUpdate();
            }
        }
    }

    public static int findInternalId(final Connection connection, final String id) throws SQLException {
        final String sql = "SELECT _id FROM nsg.document WHERE documentid=?";
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
