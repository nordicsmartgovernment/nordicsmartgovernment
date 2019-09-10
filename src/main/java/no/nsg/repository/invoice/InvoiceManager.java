package no.nsg.repository.invoice;

import no.nsg.repository.ConnectionManager;
import no.nsg.repository.dbo.BusinessDocumentDbo;
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


    public Object createInvoice(final String companyId, final String invoiceOriginalXml) throws UnknownFormatConversionException, SQLException, IOException, SAXException {
        Object invoice;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                invoice = createInvoice(companyId, invoiceOriginalXml, connection);
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

    public Object createInvoice(final String companyId, final String invoiceOriginalXml, final Connection connection) throws UnknownFormatConversionException, SQLException, IOException, SAXException {
        BusinessDocumentDbo invoice = new BusinessDocumentDbo();
        invoice.setDocumenttype(BusinessDocumentDbo.DOCUMENTTYPE_INVOICE);
        invoice.setOriginalFromString(companyId, invoiceOriginalXml);
        invoice.persist(connection);
        return invoice;
    }

    public BusinessDocumentDbo getInvoiceById(final String id) throws SQLException {
        BusinessDocumentDbo invoice = null;
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

    public BusinessDocumentDbo getInvoiceById(final String id, final Connection connection) throws SQLException, IOException {
        return new BusinessDocumentDbo(connection, BusinessDocumentDbo.findInternalId(connection, id));
    }

    public List<BusinessDocumentDbo> getInvoices() throws SQLException {
        List<BusinessDocumentDbo> invoices = new ArrayList<>();
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

    public List<BusinessDocumentDbo> getInvoices(final Connection connection) throws SQLException {
        List<BusinessDocumentDbo> invoices = new ArrayList<>();

        final String sql = "SELECT _id FROM nsg.businessdocument WHERE documenttype=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, BusinessDocumentDbo.DOCUMENTTYPE_INVOICE);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                try {
                    invoices.add(new BusinessDocumentDbo(connection, rs.getInt("_id")));
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
