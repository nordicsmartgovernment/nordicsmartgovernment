package no.nsg.repository.dbo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.nsg.generated.model.Invoice;

import java.sql.*;


public class InvoiceDbo extends Invoice {
    public static final int UNINITIALIZED = 0;

    @JsonIgnore
    private int _id;

    @JsonIgnore
    private int _invoiceoriginalid;


    public InvoiceDbo() {
        super();
        this._id = UNINITIALIZED;
    }

    public int getInternalId() {
        return this._id;
    }

    public void setInvoiceOriginalId(int invoiceOriginalId) {
        this._invoiceoriginalid = invoiceOriginalId;
    }

    public void persist(final Connection connection) throws SQLException {
        if (getInternalId() == UNINITIALIZED) {
            final String sql = "INSERT INTO nsg.invoice (_invoiceoriginalid, " +
                                                        "customizationid, profileid, id, issuedate, duedate, invoicetypecode, " +
                                                        "documentcurrencycode, accountingcost, buyerreference) " +
                                      "VALUES (?,?,?,?,?,?,?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                if (_invoiceoriginalid == InvoiceOriginalDbo.UNINITIALIZED) {
                    stmt.setNull(1, Types.INTEGER);
                } else {
                    stmt.setInt(1, _invoiceoriginalid);
                }
                stmt.setString(2, getCustomizationID());
                stmt.setString(3, getProfileID());
                stmt.setString(4, getId());
                stmt.setDate(5, Date.valueOf(getIssueDate()));
                stmt.setString(6, getDueDate());
                stmt.setString(7, getInvoiceTypeCode());
                stmt.setString(8, getDocumentCurrencyCode());
                stmt.setString(9, getAccountingCost());
                stmt.setString(10, getBuyerReference());

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this._id = rs.getInt(1);
                }
            }
        } else {
            final String sql = "UPDATE nsg.invoice SET _invoiceoriginalid=?, " +
                                                      "customizationid=?, profileid=?, id=?, issuedate=?, duedate=?, invoicetypecode=?, " +
                                                      "documentcurrencycode=?, accountingcost=?, buyerreference=?) " +
                                                "WHERE _id=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                if (_invoiceoriginalid == InvoiceOriginalDbo.UNINITIALIZED) {
                    stmt.setNull(1, Types.INTEGER);
                } else {
                    stmt.setInt(1, _invoiceoriginalid);
                }
                stmt.setString(2, getCustomizationID());
                stmt.setString(3, getProfileID());
                stmt.setString(4, getId());
                stmt.setDate(5, Date.valueOf(getIssueDate()));
                stmt.setString(6, getDueDate());
                stmt.setString(7, getInvoiceTypeCode());
                stmt.setString(8, getDocumentCurrencyCode());
                stmt.setString(9, getAccountingCost());
                stmt.setString(10, getBuyerReference());
                stmt.setInt(11, getInternalId());

                stmt.executeUpdate();
            }
        }
    }
}
