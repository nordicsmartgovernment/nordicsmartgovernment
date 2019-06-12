package no.nsg.repository.invoice;

import no.nsg.repository.ConnectionManager;
import no.nsg.repository.dbo.DocumentDbo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UnknownFormatConversionException;


@Component
public class InvoiceManager {

    @Autowired
    private ConnectionManager connectionManager;


    public Object createInvoice(final String invoiceOriginalXml) throws UnknownFormatConversionException, SQLException, IOException, SAXException {
        DocumentDbo invoice;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                invoice = new DocumentDbo();
                invoice.setDocumenttype(DocumentDbo.DOCUMENTTYPE_INVOICE);
                invoice.setOriginalFromString(invoiceOriginalXml);
                invoice.persist(connection);
                connection.commit();
            } catch (SQLException | SAXException e) {
                try {
                    connection.rollback();
                    throw e;
                } catch (SQLException e2) {
                    throw e2;
                }
            }
        }
        return invoice;
    }

    public Object getInvoiceById(final String id) throws SQLException {
        DocumentDbo invoice = null;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                try {
                    invoice = new DocumentDbo(connection, DocumentDbo.findInternalId(connection, id));
                } catch (NoSuchElementException|NumberFormatException|IOException e) {
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
        return invoice;
    }

    public List<Object> getInvoices() throws SQLException {
        List<Object> invoices = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection()) {
            final String sql = "SELECT _id FROM nsg.document WHERE documenttype=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, DocumentDbo.DOCUMENTTYPE_INVOICE);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    try {
                        invoices.add(new DocumentDbo(connection, rs.getInt("_id")));
                    } catch (NoSuchElementException|IOException e) {
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
