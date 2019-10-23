package no.nsg.repository.transaction;

import no.nsg.repository.ConnectionManager;
import no.nsg.repository.TransformationManager;
import no.nsg.repository.dbo.BusinessDocumentDbo;
import no.nsg.repository.dbo.TransactionDbo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Component
public class TransactionManager {

    @Autowired
    private ConnectionManager connectionManager;


    public List<String> getTransactionIds(final String filterDocumentId, final String filterOrganizationId, final String filterInvoiceType) throws SQLException {
        List<String> transactionIds = new ArrayList<>();

        try (Connection connection = connectionManager.getConnection()) {
            String documentFilter = "";
            if (filterDocumentId!=null) {
                documentFilter = "AND d.documentid=? ";
            }

            String organizationFilter = "";
            if (filterOrganizationId!=null) {
                organizationFilter = "AND c.orgno=? ";
            }

            String invoiceTypeFilter = "";
            if (filterInvoiceType!=null && ("incoming".equals(filterInvoiceType) || "outgoing".equals(filterInvoiceType))) {
                invoiceTypeFilter = "AND t.direction=? ";
            }

            final String sql = "SELECT t.transactionid "
                    +"FROM nsg.businessdocument d, nsg.transaction t, nsg.transactionset ts, nsg.company c "
                    +"WHERE d._id_transaction=t._id "
                    +"AND t._id_transactionset=ts._id "
                    +"AND ts._id_company=c._id "
                    +documentFilter
                    +organizationFilter
                    +invoiceTypeFilter+";";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                int i = 0;
                if (!documentFilter.isEmpty()) {
                    stmt.setString(++i, filterDocumentId);
                }
                if (!organizationFilter.isEmpty()) {
                    stmt.setString(++i, filterOrganizationId);
                }
                if (!invoiceTypeFilter.isEmpty()) {
                    int direction = TransactionDbo.NO_DIRECTION;
                    if ("incoming".equals(filterInvoiceType)) {
                        direction = TransactionDbo.INBOUND_DIRECTION;
                    } else if ("outgoing".equals(filterInvoiceType)) {
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

    public Object getTransactionByDocumentId(final String documentId) throws SQLException {
        String transaction = null;

        try (Connection connection = connectionManager.getConnection()) {
            final String sql = "SELECT d.xbrl FROM nsg.businessdocument d, nsg.transaction t WHERE d._id_transaction=t._id AND d.documentid=?;";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, documentId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    transaction = readerToString(rs.getCharacterStream("xbrl"));
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

    public String getTransactionDocument(final String transactionId) throws SQLException, IOException, SAXException {
        Document transactionDocument = null;
        Node accountingEntry = null;

        try (Connection connection = connectionManager.getConnection()) {

            final String sql = "SELECT d.xbrl, c.orgno "
                              +"FROM nsg.businessdocument d, nsg.transaction t, nsg.transactionset ts, nsg.company c "
                              +"WHERE d._id_transaction=t._id "
                              +"AND t._id_transactionset=ts._id "
                              +"AND ts._id_company=c._id "
                              +"AND t.transactionid=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, transactionId);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    if (transactionDocument == null) {
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
                                "   <gl-bus:organizationIdentifiers>\n" +
                                "    <gl-bus:organizationIdentifier contextRef=\"now\">"+rs.getString("orgno")+"</gl-bus:organizationIdentifier>\n" +
                                "   </gl-bus:organizationIdentifiers>\n" +
                                "   <gl-bus:organizationAddress>\n" +
                                "    <gl-bus:organizationAddressName contextRef=\"now\">{companyName}</gl-bus:organizationAddressName> \n" +
                                "   </gl-bus:organizationAddress>\n" +
                                "   <gl-bus:contactInformation>\n" +
                                "    <gl-bus:contactPhone/>\n" +
                                "    <gl-bus:contactFax/>\n" +
                                "    <gl-bus:contactEMail/>\n" +
                                "   </gl-bus:contactInformation>\n" +
                                "  </gl-cor:entityInformation>\n" +
                                " </gl-cor:accountingEntries>\n" +
                                "</xbrli:xbrl>\n";
                        transactionDocument = BusinessDocumentDbo.parseDocument(transactionXbrl);
                        accountingEntry = transactionDocument.getElementsByTagNameNS(TransformationManager.GL_COR_NS, "accountingEntries").item(0);
                    }

                    String xbrl = readerToString(rs.getCharacterStream("xbrl"));
                    if (xbrl != null) {
                        Document document = BusinessDocumentDbo.parseDocument(xbrl);
                        NodeList documentEntryHeaders = document.getElementsByTagNameNS(TransformationManager.GL_COR_NS, "entryHeader");
                        for (int headerIndex=0; headerIndex<documentEntryHeaders.getLength(); headerIndex++) {
                            Node entryHeader = documentEntryHeaders.item(headerIndex);
                            accountingEntry.appendChild(transactionDocument.importNode(entryHeader, true));
                        }
                    }
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
        return BusinessDocumentDbo.documentToString(transactionDocument);
    }

    public List<Object> getTransactions(final String filterOrganizationId, final String filterInvoiceType) throws SQLException {
        List<Object> transactions = new ArrayList<>();

        try (Connection connection = connectionManager.getConnection()) {
            String organizationFilter = "";
            if (filterOrganizationId!=null) {
                organizationFilter = "AND c.orgno=? ";
            }

            String invoiceTypeFilter = "";
            if (filterInvoiceType!=null && ("incoming".equals(filterInvoiceType) || "outgoing".equals(filterInvoiceType))) {
                invoiceTypeFilter = "AND t.direction=? ";
            }

            final String sql = "SELECT d.xbrl "
                              +"FROM nsg.businessdocument d, nsg.transaction t, nsg.transactionset ts, nsg.company c "
                              +"WHERE d._id_transaction=t._id "
                               +"AND t._id_transactionset=ts._id "
                               +"AND ts._id_company=c._id "
                                     +organizationFilter
                                     +invoiceTypeFilter+";";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                int i = 0;
                if (!organizationFilter.isEmpty()) {
                    stmt.setString(++i, filterOrganizationId);
                }
                if (!invoiceTypeFilter.isEmpty()) {
                    int direction = TransactionDbo.NO_DIRECTION;
                    if ("incoming".equals(filterInvoiceType)) {
                        direction = TransactionDbo.INBOUND_DIRECTION;
                    } else if ("outgoing".equals(filterInvoiceType)) {
                        direction = TransactionDbo.OUTBOUND_DIRECTION;
                    }
                    stmt.setInt(++i, direction);
                }

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String xbrl = readerToString(rs.getCharacterStream("xbrl"));
                    if (xbrl != null) {
                        transactions.add(xbrl);
                    }
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

        return transactions;
    }

    public String patchTransactionById(final String id, final String patchXml) throws SQLException, IOException, SAXException {
        String patchedXbrl;

        try (Connection connection = connectionManager.getConnection()) {
            int documentId = BusinessDocumentDbo.findInternalId(connection, id);
            if (documentId == BusinessDocumentDbo.UNINITIALIZED) {
                connection.commit();
                return null;
            }
            BusinessDocumentDbo original = new BusinessDocumentDbo(connection, documentId);
            patchedXbrl = original.patchXbrl(patchXml);
            original.persist(connection);
            connection.commit();
        }

        return patchedXbrl;
    }

    private String readerToString(final Reader xbrlReader) {
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
