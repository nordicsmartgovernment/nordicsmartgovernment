package no.nsg.repository.dbo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nsg.generated.model.Invoice;

import java.sql.*;
import java.util.NoSuchElementException;


@JsonIgnoreProperties({"id"}) /* Default serialization insists on appending this lowercase id element?!? We do not want it */
public class InvoiceDbo extends Invoice {
    public static final int UNINITIALIZED = 0;

    @JsonIgnore
    private int _id;

    @JsonIgnore
    private int _invoiceoriginalid;


    public InvoiceDbo() {
        super();
        this._id = UNINITIALIZED;
        _invoiceoriginalid = InvoiceOriginalDbo.UNINITIALIZED;
    }

    public InvoiceDbo(final Invoice invoice, final InvoiceOriginalDbo invoiceOriginal) {
        super();
        this._id = UNINITIALIZED;
        set_InvoiceOriginalId(invoiceOriginal == null ? InvoiceOriginalDbo.UNINITIALIZED : invoiceOriginal.get_id());
        setCustomizationID(invoice.getCustomizationID());
        setProfileID(invoice.getProfileID());
        setID(invoice.getID());
        setIssueDate(invoice.getIssueDate());
        setDueDate(invoice.getDueDate());
        setInvoiceTypeCode(invoice.getInvoiceTypeCode());
        setDocumentCurrencyCode(invoice.getDocumentCurrencyCode());
        setAccountingCost(invoice.getAccountingCost());
        setBuyerReference(invoice.getBuyerReference());
    }

    public InvoiceDbo(final Connection connection, final int _id) throws SQLException {
        if (_id == UNINITIALIZED) {
            throw new NoSuchElementException();
        }

        final String sql = "SELECT _invoiceoriginalid, customizationid, profileid, id, issuedate, duedate, invoicetypecode, "+
                                  "documentcurrencycode, accountingcost, buyerreference FROM invoice WHERE _id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, _id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new NoSuchElementException();
            }

            this._id = _id;
            set_InvoiceOriginalId(rs.getInt("_invoiceoriginalid"));
            if (rs.wasNull()) {
                set_InvoiceOriginalId(InvoiceOriginalDbo.UNINITIALIZED);
            }

            setCustomizationID(rs.getString("customizationid"));
            setProfileID(rs.getString("profileid"));
            setID(rs.getString("id"));
            Date issueDate = rs.getDate("issuedate");
            if (!rs.wasNull() && issueDate != null) {
                setIssueDate(issueDate.toLocalDate());
            }
            setDueDate(rs.getString("duedate"));
            setInvoiceTypeCode(rs.getString("invoicetypecode"));
            setDocumentCurrencyCode(rs.getString("documentcurrencycode"));
            setAccountingCost(rs.getString("accountingcost"));
            setBuyerReference(rs.getString("buyerreference"));
        }
    }

    public static int findInternalId(final Connection connection, final String id) throws SQLException {
        final String sql = "SELECT _id FROM invoice WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("_id");
            }
        }
        throw new NoSuchElementException();
    }

    public int get_id() {
        return this._id;
    }

    public void set_InvoiceOriginalId(int _invoiceOriginalId) {
        this._invoiceoriginalid = _invoiceOriginalId;
    }

    public void persist(final Connection connection) throws SQLException {
        if (get_id() == UNINITIALIZED) {
            final String sql = "INSERT INTO nsg.invoice (_invoiceoriginalid, " +
                                                        "customizationid, profileid, id, issuedate, duedate, invoicetypecode, " +
                                                        "documentcurrencycode, accountingcost, buyerreference) " +
                                      "VALUES (?,?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                if (_invoiceoriginalid == InvoiceOriginalDbo.UNINITIALIZED) {
                    stmt.setNull(1, Types.INTEGER);
                } else {
                    stmt.setInt(1, _invoiceoriginalid);
                }
                stmt.setString(2, getCustomizationID());
                stmt.setString(3, getProfileID());
                stmt.setString(4, getID());
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
                stmt.setString(4, getID());
                stmt.setDate(5, Date.valueOf(getIssueDate()));
                stmt.setString(6, getDueDate());
                stmt.setString(7, getInvoiceTypeCode());
                stmt.setString(8, getDocumentCurrencyCode());
                stmt.setString(9, getAccountingCost());
                stmt.setString(10, getBuyerReference());
                stmt.setInt(11, get_id());

                stmt.executeUpdate();
            }
        }
    }
}
