package no.nsg.repository.transaction;

import net.sf.saxon.s9api.SaxonApiException;
import no.nsg.repository.ConnectionManager;
import no.nsg.repository.TransformationManager;
import no.nsg.repository.dbo.BusinessDocumentDbo;
import no.nsg.repository.dbo.TransactionDbo;
import no.nsg.repository.document.formats.DocumentFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;


@Component
public class TransactionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManager.class);

    @Autowired
    private ConnectionManager connectionManager;


    public TransactionDbo getTransactionById(final int id) throws SQLException {
        TransactionDbo transaction = null;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                try {
                    transaction = TransactionManager.getTransactionById(id, connection);
                } catch (NoSuchElementException|NumberFormatException e) {
                }
                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                    throw e;
                } catch (SQLException e2) {
                    throw e2;
                }
            }
        }
        return transaction;
    }

    public static TransactionDbo getTransactionById(final int id, final Connection connection) throws SQLException {
        return new TransactionDbo(connection, id);
    }

    public TransactionDbo getTransactionByGuid(final String guid) throws SQLException {
        TransactionDbo transaction = null;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                try {
                    transaction = getTransactionByGuid(guid, connection);
                } catch (NoSuchElementException|NumberFormatException e) {
                }
                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                    throw e;
                } catch (SQLException e2) {
                    throw e2;
                }
            }
        }
        return transaction;
    }

    public TransactionDbo getTransactionByGuid(final String guid, final Connection connection) throws SQLException {
        return TransactionManager.getTransactionById(TransactionDbo.findInternalId(connection, guid), connection);
    }

    public List<String> getTransactionOwners() throws SQLException {
        List<String> companyIds = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection()) {
            final String sql = "SELECT DISTINCT c.orgno "
                              +"FROM nsg.company c, nsg.transactionset ts, nsg.transaction t "
                              +"WHERE t._id_transactionset=ts._id "
                              +"AND ts._id_company=c._id "
                              +"ORDER BY c.orgno";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    companyIds.add(rs.getString("orgno"));
                }
                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                    throw e;
                } catch (SQLException e2) {
                    throw e2;
                }
            }
        }
        return companyIds;
    }

    public List<String> getTransactionIds(final String companyId, final LocalDate filterStartDate, final LocalDate filterEndDate,
                                          final String filterDocumentId, final String filterInvoiceType) throws SQLException {
        List<String> transactionIds = new ArrayList<>();

        try (Connection connection = connectionManager.getConnection()) {
            String filterCompanyId = "";
            if (companyId!=null) {
                filterCompanyId = "AND c.orgno=? ";
            }

            String startDateFilter = "";
            if (filterStartDate!=null) {
                startDateFilter = "AND (t.transactiontime>=? OR t.transactiontime IS NULL) ";
            }

            String endDateFilter = "";
            if (filterEndDate!=null) {
                endDateFilter = "AND (t.transactiontime<=? OR t.transactiontime IS NULL) ";
            }

            String documentFilter = "";
            if (filterDocumentId!=null) {
                documentFilter = "AND d.documentid=? ";
            }

            String invoiceTypeFilter = "";
            if ("incoming".equalsIgnoreCase(filterInvoiceType) || "outgoing".equalsIgnoreCase(filterInvoiceType)) {
                invoiceTypeFilter = "AND t.direction=? ";
            } else if (filterInvoiceType!=null && !"all".equalsIgnoreCase(filterInvoiceType)) {
                throw new IllegalArgumentException("Please filter invoice type using \"incoming\", \"outgoing\" or \"all\"");
            }

            final String sql = "SELECT DISTINCT t.transactionid "
                    +"FROM nsg.businessdocument d, nsg.transaction t, nsg.transactionset ts, nsg.company c "
                    +"WHERE d._id_transaction=t._id "
                    +"AND t._id_transactionset=ts._id "
                    +"AND ts._id_company=c._id "
                    +filterCompanyId
                    +startDateFilter
                    +endDateFilter
                    +documentFilter
                    +invoiceTypeFilter+
                    "ORDER BY t.transactionid;";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                int i = 0;
                if (!filterCompanyId.isEmpty()) {
                    stmt.setString(++i, companyId);
                }
                if (!documentFilter.isEmpty()) {
                    stmt.setString(++i, filterDocumentId);
                }
                if (!startDateFilter.isEmpty()) {
                    stmt.setDate(++i, Date.valueOf(filterStartDate));
                }
                if (!endDateFilter.isEmpty()) {
                    stmt.setDate(++i, Date.valueOf(filterEndDate));
                }
                if (!invoiceTypeFilter.isEmpty()) {
                    int direction = TransactionDbo.NO_DIRECTION;
                    if ("incoming".equalsIgnoreCase(filterInvoiceType)) {
                        direction = TransactionDbo.INBOUND_DIRECTION;
                    } else if ("outgoing".equalsIgnoreCase(filterInvoiceType)) {
                        direction = TransactionDbo.OUTBOUND_DIRECTION;
                    }
                    stmt.setInt(++i, direction);
                }

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    transactionIds.add(rs.getString("transactionid"));
                }
                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                    throw e;
                } catch (SQLException e2) {
                    throw e2;
                }
            }
        }

        return transactionIds;
    }

    public String getTransactionDocumentAsXbrlGl(final String transactionId) throws SQLException, IOException, SAXException {
        return getTransactionDocumentAsXbrlGl(Collections.singletonList(transactionId));
    }

    public String getTransactionDocumentAsXbrlGl(final List<String> transactionIds) throws SQLException, IOException, SAXException {
        Document transactionDocument = null;
        Node accountingEntry = null;

        try (Connection connection = connectionManager.getConnection()) {

            final String sql = "SELECT d.xbrl, c.orgno, t.transactionid, d.documentid "
                              +"FROM nsg.businessdocument d, nsg.transaction t, nsg.transactionset ts, nsg.company c "
                              +"WHERE d._id_transaction=t._id "
                              +"AND t._id_transactionset=ts._id "
                              +"AND ts._id_company=c._id "
                              +"AND t.transactionid = ANY(?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setObject(1, transactionIds.toArray(new String[0]));

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    if (transactionDocument == null) {
                        transactionDocument = createEmptyXbrlGlDocument(rs.getString("orgno"));
                        accountingEntry = transactionDocument.getElementsByTagNameNS(TransformationManager.GL_COR_NS,"accountingEntries").item(0);
                    }

                    String xbrl = readerToString(rs.getCharacterStream("xbrl"));
                    if (xbrl != null) {
                        String companyId = rs.getString("orgno");
                        String transactionId = rs.getString("transactionid");
                        String documentId = rs.getString("documentid");
                        String location = BusinessDocumentDbo.getLocationString(companyId, transactionId, documentId);

                        Document document = BusinessDocumentDbo.parseDocument(xbrl);

                        //Iterate all entryHeaders, and append each and every one of them to the result document
                        NodeList documentEntryHeaders = document.getElementsByTagNameNS(TransformationManager.GL_COR_NS, "entryHeader");
                        for (int headerIndex=0; documentEntryHeaders!=null && headerIndex<documentEntryHeaders.getLength(); headerIndex++) {
                            Node entryHeader = documentEntryHeaders.item(headerIndex);

                            NodeList documentEntryDetails = ((Element)entryHeader).getElementsByTagNameNS(TransformationManager.GL_COR_NS, "entryDetail");
                            for (int detailIndex=0; documentEntryDetails!=null && detailIndex<documentEntryDetails.getLength(); detailIndex++) {
                                Node entryDetail = documentEntryDetails.item(detailIndex);

                                NodeList documentNumbers = ((Element)entryDetail).getElementsByTagNameNS(TransformationManager.GL_COR_NS, "documentNumber");
                                Node documentNumber = (documentNumbers!=null && documentNumbers.getLength()>0) ? documentNumbers.item(0) : null;

                                if (documentNumber == null) {
                                    documentNumber = document.createElementNS(TransformationManager.GL_COR_NS, "gl-cor:documentNumber");
                                    entryDetail.appendChild(documentNumber);
                                }

                                documentNumber.setTextContent(location);
                            }

                            accountingEntry.appendChild(transactionDocument.importNode(entryHeader, true));
                        }
                    }
                }
                connection.commit();

                //In case we didn't find any documents, return an empty XBRL-GL document
                if (transactionDocument == null) {
                    transactionDocument = createEmptyXbrlGlDocument(null);
                }
            } catch (SQLException e) {
                try {
                    connection.rollback();
                    throw e;
                } catch (SQLException e2) {
                    throw e2;
                }
            }
        }
        return BusinessDocumentDbo.documentToString(transactionDocument);
    }

    private Document createEmptyXbrlGlDocument(final String companyId) throws IOException, SAXException {
        String transactionXbrl =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<xbrli:xbrl xmlns:xbrli=\"http://www.xbrl.org/2003/instance\" xmlns:fn=\"http://www.w3.org/2005/02/xpath-functions\" xmlns:gl-bus=\"http://www.xbrl.org/int/gl/bus/2015-03-25\" xmlns:gl-cor=\"http://www.xbrl.org/int/gl/cor/2016-12-01\" xmlns:gl-cor-fi=\"http://www.xbrl.org/int/gl/cor/fi/2017-01-01\" xmlns:gl-muc=\"http://www.xbrl.org/int/gl/muc/2015-03-25\" xmlns:gl-plt=\"http://www.xbrl.org/int/gl/plt/2015-03-25\" xmlns:gl-rapko=\"http://www.xbrl.org/int/gl/rapko/2015-07-01\" xmlns:gl-taf=\"http://www.xbrl.org/int/gl/taf/2015-03-25\" xmlns:iso4217=\"http://www.xbrl.org/2003/iso4217\" xmlns:iso639=\"http://www.xbrl.org/2005/iso639\" xmlns:ix=\"http://www.xbrl.org/2008/inlineXBRL\" xmlns:ixt=\"http://www.xbrl.org/inlineXBRL/transformation/2010-04-20\" xmlns:link=\"http://www.xbrl.org/2003/linkbase\" xmlns:xbrll=\"http://www.xbrl.org/2003/linkbase\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.xbrl.org/int/gl/plt/2015-03-25 ../../../taxonomies/XBRL-GL-REC-2017-01-01-fi/gl/plt/case-c-b-m-u-t-s-r/gl-plt-fi-all-2017-01-01.xsd\">" +
                        " <gl-cor:accountingEntries>\n" +
                        "  <gl-cor:documentInfo>\n" +
                        "   <gl-cor:entriesType contextRef=\"now\">journal</gl-cor:entriesType>\n" +
                        "   <gl-cor:creationDate contextRef=\"now\">2019-07-12+03:00</gl-cor:creationDate>\n" +
                        "   <gl-muc:defaultCurrency contextRef=\"now\">DKK</gl-muc:defaultCurrency>\n" +
                        "  </gl-cor:documentInfo>\n" +
                        "  <gl-cor:entityInformation>\n" +
                        "   <gl-bus:organizationIdentifiers>\n";

        if (companyId!=null) {
            transactionXbrl += "    <gl-bus:organizationIdentifier contextRef=\"now\">" + companyId + "</gl-bus:organizationIdentifier>\n";
        }

        transactionXbrl +=
                        "   </gl-bus:organizationIdentifiers>\n" +
                        "   <gl-bus:organizationAddress/>\n" +
                        "   <gl-bus:contactInformation>\n" +
                        "    <gl-bus:contactPhone/>\n" +
                        "    <gl-bus:contactFax/>\n" +
                        "    <gl-bus:contactEMail/>\n" +
                        "   </gl-bus:contactInformation>\n" +
                        "  </gl-cor:entityInformation>\n" +
                        " </gl-cor:accountingEntries>\n" +
                        "</xbrli:xbrl>\n";
        return BusinessDocumentDbo.parseDocument(transactionXbrl);
    }

    public String getTransactionDocumentAsSafT(final String transactionId) throws SQLException, IOException, SAXException {
        return getTransactionDocumentAsSafT(Collections.singletonList(transactionId));
    }

    public String getTransactionDocumentAsSafT(final List<String> transactionIds) throws SQLException, IOException, SAXException {
        String xbrl = getTransactionDocumentAsXbrlGl(transactionIds);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            TransformationManager.transform(new ByteArrayInputStream(xbrl.getBytes(StandardCharsets.UTF_8)), DocumentFormat.Format.XBRL_GL_TO_SAF_T, baos);
            return baos.toString(StandardCharsets.UTF_8.name());
        } catch (SaxonApiException e) {
            LOGGER.info("Failed converting to SAF-T");
            return null;
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Converting to SAF-T using unsupported encoding");
            return null;
        }
    }

    public void putTransactionByDocumentGuid(final String documentGuid, final String xbrlDocument) throws SQLException, IOException, SAXException {
        try (Connection connection = connectionManager.getConnection()) {
            int documentId = BusinessDocumentDbo.findInternalId(connection, documentGuid);
            if (documentId == BusinessDocumentDbo.UNINITIALIZED) {
                connection.commit();
                throw new NoSuchElementException();
            }
            BusinessDocumentDbo original = new BusinessDocumentDbo(connection, documentId);
            original.setXbrl(xbrlDocument);
            original.persist(connection);
            connection.commit();
        }
    }

    private String readerToString(final Reader xbrlReader) {
        if (xbrlReader == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[10 * 1024];
        int length;
        try {
            while ((length = xbrlReader.read(buffer)) != -1) {
                sb.append(buffer, 0, length);
            }
        } catch (IOException e) {
            return null;
        }
        return sb.toString();
    }
}
