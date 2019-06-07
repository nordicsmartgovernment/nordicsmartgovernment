package no.nsg.repository.transaction;

import no.nsg.repository.ConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Component
public class TransactionManager {

    @Autowired
    private ConnectionManager connectionManager;


    public Object getTransactionById(final String id) throws SQLException {
        Object transaction = null;
        /*
        try (Connection connection = connectionManager.getConnection()) {
            try {
                try {
                    invoice = new InvoiceDbo(connection, InvoiceDbo.findInternalId(connection, id));
                } catch (NoSuchElementException |NumberFormatException e) {
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
         */
        return transaction;
    }

    public List<Object> getTransactions() throws SQLException {
        List<Object> transactions = new ArrayList<>();
        /*
        try (Connection connection = connectionManager.getConnection()) {
            final String sql = "SELECT _id FROM invoice";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    try {
                        invoices.add(new InvoiceDbo(connection, rs.getInt("_id")));
                    } catch (NoSuchElementException e) {
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
         */
        return transactions;
    }
}
