package no.nsg.repository.dbo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.w3c.dom.*;

import java.sql.*;
import java.util.NoSuchElementException;


@JsonIgnoreProperties({"id"}) /* Default serialization insists on appending this lowercase id element?!? We do not want it */
public class EntryDbo {
    public static final int UNINITIALIZED = 0;

    @JsonIgnore
    private int _id;

    @JsonIgnore
    private int _id_businessdocument;

    @JsonIgnore
    private int _id_journal;

    @JsonIgnore
    private int _id_account;

    @JsonIgnore
    private int _id_currency;

    private Float amount;

    private Float vat;


    public EntryDbo() {
        this._id = UNINITIALIZED;
        this._id_businessdocument = BusinessDocumentDbo.UNINITIALIZED;
        this._id_journal = JournalDbo.UNINITIALIZED;
        this._id_account = AccountDbo.UNINITIALIZED;
        this._id_currency = CurrencyDbo.UNINITIALIZED;
    }

    public EntryDbo(final Connection connection, final int id) throws SQLException {
        if (id== UNINITIALIZED) {
            throw new NoSuchElementException();
        }

        final String sql = "SELECT _id_businessdocument, _id_journal, _id_account, _id_currency, amount, vat FROM nsg.entry WHERE _id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new NoSuchElementException();
            }

            this._id = id;

            set_BusinessDocumentId(rs.getInt("_id_businessdocument"));
            if (rs.wasNull()) {
                set_BusinessDocumentId(BusinessDocumentDbo.UNINITIALIZED);
            }

            set_JournalId(rs.getInt("_id_journal"));
            if (rs.wasNull()) {
                set_JournalId(JournalDbo.UNINITIALIZED);
            }

            set_AccountId(rs.getInt("_id_account"));
            if (rs.wasNull()) {
                set_AccountId(AccountDbo.UNINITIALIZED);
            }

            set_CurrencyId(rs.getInt("_id_currency"));
            if (rs.wasNull()) {
                set_CurrencyId(CurrencyDbo.UNINITIALIZED);
            }

            setAmount(rs.getFloat("amount"));
            if (rs.wasNull()) {
                setAmount(null);
            }

            setVat(rs.getFloat("vat"));
            if (rs.wasNull()) {
                setVat(null);
            }
        }
    }

    public EntryDbo(final Document document, final Node node) {
        this._id = UNINITIALIZED;
        set_BusinessDocumentId(BusinessDocumentDbo.UNINITIALIZED);
        set_JournalId(JournalDbo.UNINITIALIZED);
        set_CurrencyId(JournalDbo.UNINITIALIZED);

        if (!(node instanceof Element)) {
            throw new RuntimeException("Node is not an Element!");
        }

        NodeList nodes = ((Element)node).getElementsByTagName("gl-cor:accountMainID");
        if (nodes.getLength() > 0) {
            Node accountValueNode = nodes.item(0).getFirstChild();
            if (accountValueNode != null) {
                set_AccountId(AccountDbo.findByAccount(accountValueNode.getTextContent()));
            }
        }

        nodes = ((Element)node).getElementsByTagName("gl-cor:amount");
        if (nodes.getLength() > 0) {
            Node amountNode = nodes.item(0);
            Node amountValueNode = amountNode.getFirstChild();
            if (amountValueNode != null) {
                setAmount(Float.parseFloat(amountValueNode.getTextContent()));
                String currency = getUnitReferenceValue(document, amountNode);
                if (currency != null) {
                    set_CurrencyId(CurrencyDbo.findByCurrency(currency));
                }
            }
        }

        nodes = ((Element)node).getElementsByTagName("gl-cor:taxAmount");
        if (nodes.getLength() > 0) {
            Node vatNode = nodes.item(0);
            Node vatValueNode = vatNode.getFirstChild();
            if (vatValueNode != null) {
                setVat(Float.parseFloat(vatValueNode.getTextContent()));
            }
        }
    }

    private String getUnitReferenceValue(final Document document, final Node node) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            Node referenceNode = attributes.getNamedItem("unitRef");
            if (referenceNode != null) {
                String referenceValue = referenceNode.getTextContent();
                NodeList unitNodes = document.getElementsByTagName("xbrli:unit");
                for (int i=0; i<unitNodes.getLength(); i++) {
                    Node unitNode = unitNodes.item(i);
                    NamedNodeMap unitAttributes = unitNode.getAttributes();
                    if (unitAttributes != null) {
                        Node idNode = unitAttributes.getNamedItem("id");
                        if (idNode!=null && referenceValue.equals(idNode.getTextContent())) {
                            return idNode.getTextContent();
                        }
                    }
                }
            }
        }
        return null;
    }

    public int get_id() {
        return this._id;
    }

    public void set_BusinessDocumentId(final int _id_businessdocument) {
        this._id_businessdocument = _id_businessdocument;
    }

    private int get_BusinessDocumentId() {
        return this._id_businessdocument;
    }

    private void set_JournalId(final int _id_journal) {
        this._id_journal = _id_journal;
    }

    private int get_JournalId() {
        return this._id_journal;
    }

    private void set_AccountId(final Integer _id_account) {
        this._id_account = _id_account != null ? _id_account : AccountDbo.UNINITIALIZED;
    }

    private int get_AccountId() {
        return this._id_account;
    }

    private void set_CurrencyId(final Integer _id_currency) {
        this._id_currency = _id_currency != null ? _id_currency : CurrencyDbo.UNINITIALIZED;
    }

    private int get_CurrencyId() {
        return this._id_currency;
    }

    public void setAmount(final Float amount) {
        this.amount = amount;
    }

    private Float getAmount() {
        return this.amount;
    }

    public void setVat(final Float vat) {
        this.vat = vat;
    }

    private Float getVat() {
        return this.vat;
    }

    public static void deleteDocumentRows(final Connection connection, final BusinessDocumentDbo businessDocumentDbo) throws SQLException {
        final String sql = "DELETE FROM nsg.entry WHERE _id_businessdocument=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, businessDocumentDbo.get_id());
            stmt.executeUpdate();
        }
    }

    public void persist(final Connection connection) throws SQLException {
        if (get_id() == UNINITIALIZED) {
            final String sql = "INSERT INTO nsg.entry (_id_businessdocument, _id_journal, _id_account, _id_currency, amount, vat) " +
                                      "VALUES (?,?,?,?,?,?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                if (get_BusinessDocumentId() != BusinessDocumentDbo.UNINITIALIZED) {
                    stmt.setInt(1, get_BusinessDocumentId());
                } else {
                    stmt.setNull(1, Types.INTEGER);
                }

                if (get_JournalId() != JournalDbo.UNINITIALIZED) {
                    stmt.setInt(2, get_JournalId());
                } else {
                    stmt.setNull(2, Types.INTEGER);
                }

                if (get_AccountId() != AccountDbo.UNINITIALIZED) {
                    stmt.setInt(3, get_AccountId());
                } else {
                    stmt.setNull(3, Types.INTEGER);
                }

                if (get_CurrencyId() != CurrencyDbo.UNINITIALIZED) {
                    stmt.setInt(4, get_CurrencyId());
                } else {
                    stmt.setNull(4, Types.INTEGER);
                }

                if (getAmount() != null) {
                    stmt.setFloat(5, getAmount());
                } else {
                    stmt.setNull(5, Types.FLOAT);
                }

                if (getVat() != null) {
                    stmt.setFloat(6, getVat());
                } else {
                    stmt.setNull(6, Types.FLOAT);
                }

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this._id = rs.getInt(1);
                }
            }
        } else {
            final String sql = "UPDATE nsg.entry SET _id_businessdocument=?, _id_journal=?, _id_account=?, _id_currency=?, amount=?, vat=? " +
                                "WHERE _id=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {

                if (get_BusinessDocumentId() != BusinessDocumentDbo.UNINITIALIZED) {
                    stmt.setInt(1, get_BusinessDocumentId());
                } else {
                    stmt.setNull(1, Types.INTEGER);
                }

                if (get_JournalId() != JournalDbo.UNINITIALIZED) {
                    stmt.setInt(2, get_JournalId());
                } else {
                    stmt.setNull(2, Types.INTEGER);
                }

                if (get_AccountId() != AccountDbo.UNINITIALIZED) {
                    stmt.setInt(3, get_AccountId());
                } else {
                    stmt.setNull(3, Types.INTEGER);
                }

                if (get_CurrencyId() != CurrencyDbo.UNINITIALIZED) {
                    stmt.setInt(4, get_CurrencyId());
                } else {
                    stmt.setNull(4, Types.INTEGER);
                }

                if (getAmount() != null) {
                    stmt.setFloat(5, getAmount());
                } else {
                    stmt.setNull(5, Types.FLOAT);
                }

                if (getVat() != null) {
                    stmt.setFloat(6, getVat());
                } else {
                    stmt.setNull(6, Types.FLOAT);
                }

                stmt.setInt(7, get_id());

                stmt.executeUpdate();
            }
        }
    }
}
