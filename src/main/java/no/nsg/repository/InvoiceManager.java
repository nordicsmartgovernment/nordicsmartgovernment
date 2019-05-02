package no.nsg.repository;

import no.nsg.generated.model.Invoice;
import no.nsg.repository.dbo.InvoiceDbo;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@Component
public class InvoiceManager {


    public Invoice createInvoice(final Invoice newInvoice) throws SQLException {
        InvoiceDbo newInvoiceDbo;
        try (Connection connection = ConnectionManager.getConnection()) {
            try {
                if (newInvoice==null) {
                    return null;
                }

                newInvoiceDbo = new InvoiceDbo(newInvoice);
                newInvoiceDbo.persist(connection);
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

        return newInvoiceDbo;
    }

    public Invoice getInvoiceById(String id) {
        return null;
    }

    public List<Invoice> getInvoices() throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        try (Connection connection = ConnectionManager.getConnection()) {
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
        return invoices;
    }

}
