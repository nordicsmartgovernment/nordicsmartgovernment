package no.nsg.repository.dbo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.w3c.dom.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@JsonIgnoreProperties({"id"}) /* Default serialization insists on appending this lowercase id element?!? We do not want it */
public class DocumentRowDbo {
    public static final int UNINITIALIZED = 0;

    @JsonIgnore
    private int _id;

    @JsonIgnore
    private int _documentid;

    @JsonIgnore
    private String account;

    @JsonIgnore
    private Float amount;

    @JsonIgnore
    private Float vat;

    @JsonIgnore
    private String currency;


    public DocumentRowDbo() {
        this._id = UNINITIALIZED;
        set_DocumentId(DocumentDbo.UNINITIALIZED);
    }

    public DocumentRowDbo(final Document document, final Node node) {
        this._id = UNINITIALIZED;
        set_DocumentId(DocumentDbo.UNINITIALIZED);

        if (!(node instanceof Element)) {
            throw new RuntimeException("Node is not an Element!");
        }

        NodeList  nodes = ((Element)node).getElementsByTagName("gl-cor:accountMainID");
        if (nodes.getLength() > 0) {
            Node accountValueNode = nodes.item(0).getFirstChild();
            if (accountValueNode != null) {
                setAccount(accountValueNode.getTextContent());
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
                    setCurrency(currency);
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

    public DocumentRowDbo(final Connection connection, final int _id) throws SQLException {
        if (_id == UNINITIALIZED) {
            throw new NoSuchElementException();
        }

        final String sql = "SELECT _documentid, account, amount, vat, currency FROM nsg.documentrow WHERE _id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, _id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new NoSuchElementException();
            }

            this._id = _id;
            set_DocumentId(rs.getInt("_companyid"));
            if (rs.wasNull()) {
                set_DocumentId(CompanyDbo.UNINITIALIZED);
            }

            setAccount(rs.getString("account"));

            setAmount(rs.getFloat("amount"));
            if (rs.wasNull()) {
                setAmount(null);
            }

            setVat(rs.getFloat("vat"));
            if (rs.wasNull()) {
                setVat(null);
            }

            setCurrency(rs.getString("currency"));
        }
    }

    public int get_id() {
        return this._id;
    }

    public void set_DocumentId(final int _documentid) {
        this._documentid = _documentid;
    }

    public int get_DocumentId() {
        return this._documentid;
    }

    public void setAccount(final String account) {
        this.account = account;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAmount(final Float amount) {
        this.amount = amount;
    }

    public Float getAmount() {
        return this.amount;
    }

    public void setVat(final Float vat) {
        this.vat = vat;
    }

    public Float getVat() {
        return this.vat;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return this.currency;
    }

    public List<DocumentRowDbo> getDocumentRows(final Connection connection, final int documentId) throws SQLException {
        if (_id == UNINITIALIZED) {
            throw new NoSuchElementException();
        }

        List<DocumentRowDbo> documentRows = new ArrayList<>();
        final String sql = "SELECT _id FROM nsg.documentrow WHERE _documentid=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, documentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                documentRows.add(new DocumentRowDbo(connection, rs.getInt("_id")));
            }
        }
        return documentRows;
    }

    public static void deleteDocumentRows(final Connection connection, final int documentId) throws SQLException {
        final String sql = "DELETE FROM nsg.documentrow WHERE _documentid=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, documentId);
            stmt.executeUpdate();
        }
    }

    public void persist(final Connection connection) throws SQLException {
        if (get_id() == UNINITIALIZED) {
            final String sql = "INSERT INTO nsg.documentrow (_documentid, account, amount, vat, currency) VALUES (?,?,?,?,?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                if (get_DocumentId() == CompanyDbo.UNINITIALIZED) {
                    stmt.setNull(1, Types.INTEGER);
                } else {
                    stmt.setInt(1, get_DocumentId());
                }

                stmt.setString(2, getAccount());

                if (getAmount() == null) {
                    stmt.setNull(3, Types.FLOAT);
                } else {
                    stmt.setFloat(3, getAmount());
                }

                if (getVat() == null) {
                    stmt.setNull(4, Types.FLOAT);
                } else {
                    stmt.setFloat(4, getVat());
                }

                stmt.setString(5, getCurrency());

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this._id = rs.getInt(1);
                }
            }
        } else {
            final String sql = "UPDATE nsg.transaction SET _documentid=?, account=?, amount=?, vat=?, currency=? WHERE _id=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                if (get_DocumentId() == DocumentDbo.UNINITIALIZED) {
                    stmt.setNull(1, Types.INTEGER);
                } else {
                    stmt.setInt(1, get_DocumentId());
                }

                stmt.setString(2, getAccount());

                if (getAmount() == null) {
                    stmt.setNull(3, Types.FLOAT);
                } else {
                    stmt.setFloat(3, getAmount());
                }

                if (getVat() == null) {
                    stmt.setNull(4, Types.FLOAT);
                } else {
                    stmt.setFloat(4, getVat());
                }

                stmt.setString(5, getCurrency());

                stmt.setInt(6, get_id());

                stmt.executeUpdate();
            }
        }
    }

}
