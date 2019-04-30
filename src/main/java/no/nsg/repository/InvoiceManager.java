package no.nsg.repository;

import no.nsg.generated.model.Invoice;
import no.nsg.repository.dbo.InvoiceDbo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class InvoiceManager {

    private static InvoiceManager instance = null;


    public static InvoiceManager getInstance() {
        if (InvoiceManager.instance == null) {
            InvoiceManager.instance = new InvoiceManager();
        }
        return InvoiceManager.instance;
    }

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

    public List<Invoice> getInvoices() {
        return null;
    }

}
