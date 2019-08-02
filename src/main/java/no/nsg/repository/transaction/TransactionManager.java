package no.nsg.repository.transaction;

import no.nsg.repository.ConnectionManager;
import no.nsg.repository.dbo.TransactionDbo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
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


    public Object getTransactionById(final String id) throws SQLException {
        String transaction = null;

        try (Connection connection = connectionManager.getConnection()) {
            final String sql = "SELECT d.xbrl FROM nsg.document d, nsg.transaction t WHERE d._transactionid=t._id AND d.documentid=?;";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, id);
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
                              +"FROM nsg.document d, nsg.transaction t, nsg.company c "
                              +"WHERE d._transactionid=t._id "
                                +"AND t._companyid=c._id "
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
