package no.nsg.repository.dbo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nsg.repository.TransformationManager;

import java.sql.*;
import java.util.NoSuchElementException;


@JsonIgnoreProperties({"id"}) /* Default serialization insists on appending this lowercase id element?!? We do not want it */
public class TransactionDbo {
    public static final int UNINITIALIZED = 0;

    public static final int NO_DIRECTION = 0;
    public static final int OUTBOUND_DIRECTION = 1;
    public static final int INBOUND_DIRECTION = 2;


    @JsonIgnore
    private int _id;

    @JsonIgnore
    private int _companyid;

    @JsonIgnore
    private DocumentInfo transactionInfo = new DocumentInfo();



    public TransactionDbo(final CompanyDbo companyDbo) {
        this._id = UNINITIALIZED;
        set_CompanyId(companyDbo == null ? CompanyDbo.UNINITIALIZED : companyDbo.get_id());
    }

    public TransactionDbo(final Connection connection, final int _id) throws SQLException {
        if (_id == UNINITIALIZED) {
            throw new NoSuchElementException();
        }

        final String sql = "SELECT _companyid, direction, account, amount, vat, currency, transactiontime FROM nsg.transaction WHERE _id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, _id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new NoSuchElementException();
            }

            this._id = _id;
            set_CompanyId(rs.getInt("_companyid"));
            if (rs.wasNull()) {
                set_CompanyId(CompanyDbo.UNINITIALIZED);
            }

            Integer tmpDirection;
            tmpDirection = rs.getInt("direction");
            if (rs.wasNull()) {
                tmpDirection = null;
            }

            this.transactionInfo.setDirection(intToDirection(tmpDirection));
            this.transactionInfo.setAccount(rs.getString("account"));
            this.transactionInfo.setAmount(rs.getFloat("amount"));
            if (rs.wasNull()) {
                this.transactionInfo.setAmount(null);
            }

            this.transactionInfo.setVat(rs.getFloat("vat"));
            if (rs.wasNull()) {
                this.transactionInfo.setVat(null);
            }

            this.transactionInfo.setCurrency(rs.getString("currency"));

            Timestamp tmpTime = rs.getTimestamp("transactiontime");
            if (!rs.wasNull()) {
                this.transactionInfo.setTransactionTime(tmpTime.toLocalDateTime());
            } else {
                this.transactionInfo.setTransactionTime(null);
            }
        }
    }

    public int get_id() {
        return this._id;
    }

    public void set_CompanyId(int _companyId) {
        this._companyid = _companyId;
    }

    public void setTransactionInfo(final DocumentInfo transactionInfo) {
        this.transactionInfo = transactionInfo;
    }

    public DocumentInfo getTransactionInfo() {
        return this.transactionInfo;
    }

    public void persist(final Connection connection) throws SQLException {
        if (get_id() == UNINITIALIZED) {
            final String sql = "INSERT INTO nsg.transaction (_companyid, direction, account, amount, vat, currency, transactiontime) VALUES (?,?,?,?,?,?,?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                if (_companyid == CompanyDbo.UNINITIALIZED) {
                    stmt.setNull(1, Types.INTEGER);
                } else {
                    stmt.setInt(1, _companyid);
                }

                Integer tmpDirection = directionToInt(getTransactionInfo().getDirection());
                if (tmpDirection == null) {
                    stmt.setNull(2, Types.INTEGER);
                } else {
                    stmt.setInt(2, tmpDirection);
                }

                stmt.setString(3, getTransactionInfo().getAccount());

                if (getTransactionInfo().getAmount() == null) {
                    stmt.setNull(4, Types.FLOAT);
                } else {
                    stmt.setFloat(4, getTransactionInfo().getAmount());
                }

                if (getTransactionInfo().getVat() == null) {
                    stmt.setNull(5, Types.FLOAT);
                } else {
                    stmt.setFloat(5, getTransactionInfo().getVat());
                }

                stmt.setString(6, getTransactionInfo().getCurrency());

                if (getTransactionInfo().getTransactionTime() == null) {
                    stmt.setNull(7, Types.TIMESTAMP);
                } else {
                    stmt.setTimestamp(7, Timestamp.valueOf(getTransactionInfo().getTransactionTime()));
                }

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this._id = rs.getInt(1);
                }
            }
        } else {
            final String sql = "UPDATE nsg.transaction SET _companyid=?, direction=?, account=?, amount=?, vat=?, currency=?, transactiontime=? WHERE _id=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                if (_companyid == CompanyDbo.UNINITIALIZED) {
                    stmt.setNull(1, Types.INTEGER);
                } else {
                    stmt.setInt(1, _companyid);
                }

                Integer tmpDirection = directionToInt(getTransactionInfo().getDirection());
                if (tmpDirection == null) {
                    stmt.setNull(2, Types.INTEGER);
                } else {
                    stmt.setInt(2, tmpDirection);
                }

                stmt.setString(3, getTransactionInfo().getAccount());

                if (getTransactionInfo().getAmount() == null) {
                    stmt.setNull(4, Types.FLOAT);
                } else {
                    stmt.setFloat(4, getTransactionInfo().getAmount());
                }

                if (getTransactionInfo().getVat() == null) {
                    stmt.setNull(5, Types.FLOAT);
                } else {
                    stmt.setFloat(5, getTransactionInfo().getVat());
                }

                stmt.setString(6, getTransactionInfo().getCurrency());

                if (getTransactionInfo().getTransactionTime() == null) {
                    stmt.setNull(7, Types.TIMESTAMP);
                } else {
                    stmt.setTimestamp(7, Timestamp.valueOf(getTransactionInfo().getTransactionTime()));
                }

                stmt.setInt(8, get_id());

                stmt.executeUpdate();
            }
        }
    }

    private TransformationManager.Direction intToDirection(final Integer direction) {
        if (direction == null) {
            return null;
        }

        switch(direction) {
            case INBOUND_DIRECTION: return TransformationManager.Direction.PURCHASE;
            case OUTBOUND_DIRECTION: return TransformationManager.Direction.SALES;
            default: return TransformationManager.Direction.DOESNT_MATTER;
        }
    }

    private Integer directionToInt(final TransformationManager.Direction direction) {
        if (direction == null) {
            return null;
        }

        switch(direction) {
            case PURCHASE: return INBOUND_DIRECTION;
            case SALES: return OUTBOUND_DIRECTION;
            default: return NO_DIRECTION;
        }
    }

}
