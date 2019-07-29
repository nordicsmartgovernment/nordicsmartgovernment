package no.nsg.repository.invoice;

import no.nsg.repository.ConnectionManager;
import no.nsg.repository.TransformationManager;
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
        Object invoice;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                invoice = createInvoice(invoiceOriginalXml, connection);
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

    public Object createInvoice(final String invoiceOriginalXml, final Connection connection) throws UnknownFormatConversionException, SQLException, IOException, SAXException {
        DocumentDbo invoice = new DocumentDbo();
        invoice.setDocumenttype(DocumentDbo.DOCUMENTTYPE_INVOICE);
        invoice.setOriginalFromString(invoiceOriginalXml, TransformationManager.Direction.PURCHASE); //TODO: Don't hardcode direction
        invoice.persist(connection);
        return invoice;
    }

    public DocumentDbo getInvoiceById(final String id) throws SQLException {
        DocumentDbo invoice = null;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                try {
                    invoice = getInvoiceById(id, connection);
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

    public DocumentDbo getInvoiceById(final String id, final Connection connection) throws SQLException, IOException {
        return new DocumentDbo(connection, DocumentDbo.findInternalId(connection, id));
    }

    public List<DocumentDbo> getInvoices() throws SQLException {
        List<DocumentDbo> invoices = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection()) {
            try {
                invoices = getInvoices(connection);
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

    public List<DocumentDbo> getInvoices(final Connection connection) throws SQLException {
        List<DocumentDbo> invoices = new ArrayList<>();

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
        } catch (Exception e) {
            throw e;
        }

        return invoices;
    }
}
