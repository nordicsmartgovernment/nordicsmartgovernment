package no.nsg.repository.invoice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import no.nsg.generated.invoice_model.Invoice;
import no.nsg.repository.ConnectionManager;
import no.nsg.repository.dbo.invoice.InvoiceDbo;
import no.nsg.repository.dbo.invoice.InvoiceOriginalDbo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    private enum Format {
        INVOICE,
        FINVOICE
    }

    @Autowired
    private ConnectionManager connectionManager;

    private static ObjectMapper xmlMapper = null;

    private ObjectMapper getXmlMapper() {
        if (xmlMapper == null) {
            xmlMapper = new XmlMapper();
            xmlMapper.findAndRegisterModules();
        }
        return xmlMapper;
    }

    public Object createInvoice(final String invoiceOriginalXml) throws UnknownFormatConversionException, SQLException, IOException {
        Object invoice = deserializeInvoice(invoiceOriginalXml);
        if (invoice == null) {
            throw new UnknownFormatConversionException("Unknown invoice format");
        }

        InvoiceDbo newInvoiceDbo;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                InvoiceOriginalDbo newInvoiceOriginalDbo = new InvoiceOriginalDbo(invoiceOriginalXml);
                newInvoiceOriginalDbo.persist(connection);

                if (invoice instanceof Invoice) {
                    newInvoiceDbo = new InvoiceDbo((Invoice)invoice, newInvoiceOriginalDbo);
                    newInvoiceDbo.persist(connection);
                } else if (invoice instanceof Finvoice) {

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

    public Invoice getInvoiceById(final String id) throws SQLException {
        Invoice invoice = null;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                try {
                    invoice = new InvoiceDbo(connection, InvoiceDbo.findInternalId(connection, id));
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
        return invoice;
    }

    public List<Invoice> getInvoices() throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
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
        return invoices;
    }

    private Format detectFormat(final String invoiceOriginalXml) {
        if (invoiceOriginalXml.contains("<Finvoice ")) { //TODO: Proper detection
            return Format.FINVOICE;
        } else {
            return Format.INVOICE;
        }
    }

    private Object deserializeInvoice(final String invoiceOriginalXml) throws IOException {
        Format format = detectFormat(invoiceOriginalXml);
        if (format == Format.INVOICE) {
            return getXmlMapper().readValue(invoiceOriginalXml, Invoice.class);
        } else if (format == Format.FINVOICE) {
            return getXmlMapper().readValue(invoiceOriginalXml, Finvoice.class);
        } else {
            return null;
        }
    }
}
