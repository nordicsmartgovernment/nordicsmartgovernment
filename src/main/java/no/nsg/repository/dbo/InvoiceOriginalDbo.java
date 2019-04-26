package no.nsg.repository.dbo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.StringReader;
import java.sql.*;


public class InvoiceOriginalDbo {
    public static final int UNINITIALIZED = 0;

    @JsonIgnore
    private int _id;

    private String invoice;


    public InvoiceOriginalDbo() {
        this._id = UNINITIALIZED;
    }

    public int getInternalId() {
        return this._id;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public void persist(final Connection connection) throws SQLException {
        if (getInternalId() == UNINITIALIZED) {
            final String sql = "INSERT INTO nsg.invoiceoriginal (invoice) VALUES (?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setCharacterStream(1, new StringReader(invoice));

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this._id = rs.getInt(1);
                }
            }
        } else {
            final String sql = "UPDATE nsg.invoiceoriginal SET invoice=? WHERE _id=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setCharacterStream(1, new StringReader(invoice));

                stmt.executeUpdate();
            }
        }
    }

}
