package no.nsg.repository.dbo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nsg.repository.TransformationManager;

import java.sql.*;
import java.time.LocalDateTime;
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
    private TransformationManager.Direction direction = TransformationManager.Direction.DOESNT_MATTER;

    @JsonIgnore
    private LocalDateTime transactionTime;



    public TransactionDbo(final CompanyDbo companyDbo) {
        this._id = UNINITIALIZED;
        set_CompanyId(companyDbo == null ? CompanyDbo.UNINITIALIZED : companyDbo.get_id());
    }

    public TransactionDbo(final Connection connection, final int _id) throws SQLException {
        if (_id == UNINITIALIZED) {
            throw new NoSuchElementException();
        }

        final String sql = "SELECT _companyid, direction, transactiontime FROM nsg.transaction WHERE _id=?";
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

            setDirection(intToDirection(tmpDirection));

            Timestamp tmpTime = rs.getTimestamp("transactiontime");
            if (!rs.wasNull()) {
                setTransactionTime(tmpTime.toLocalDateTime());
            } else {
                setTransactionTime(null);
            }
        }
    }

    public int get_id() {
        return this._id;
    }

    public void set_CompanyId(final int _companyId) {
        this._companyid = _companyId;
    }

    public int get_CompanyId() {
        return this._companyid;
    }

    public void setDirection(final TransformationManager.Direction direction) {
        this.direction = direction;
    }

    public TransformationManager.Direction getDirection() {
        return direction;
    }

    public void setTransactionTime(final LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public LocalDateTime getTransactionTime() {
        return this.transactionTime;
    }

    public void persist(final Connection connection) throws SQLException {
        if (get_id() == UNINITIALIZED) {
            final String sql = "INSERT INTO nsg.transaction (_companyid, direction, transactiontime) VALUES (?,?,?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                if (get_CompanyId() == CompanyDbo.UNINITIALIZED) {
                    stmt.setNull(1, Types.INTEGER);
                } else {
                    stmt.setInt(1, get_CompanyId());
                }

                Integer tmpDirection = directionToInt(getDirection());
                if (tmpDirection == null) {
                    stmt.setNull(2, Types.INTEGER);
                } else {
                    stmt.setInt(2, tmpDirection);
                }

                if (getTransactionTime() == null) {
                    stmt.setNull(3, Types.TIMESTAMP);
                } else {
                    stmt.setTimestamp(3, Timestamp.valueOf(getTransactionTime()));
                }

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this._id = rs.getInt(1);
                }
            }
        } else {
            final String sql = "UPDATE nsg.transaction SET _companyid=?, direction=?, transactiontime=? WHERE _id=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                if (get_CompanyId() == CompanyDbo.UNINITIALIZED) {
                    stmt.setNull(1, Types.INTEGER);
                } else {
                    stmt.setInt(1, get_CompanyId());
                }

                Integer tmpDirection = directionToInt(getDirection());
                if (tmpDirection == null) {
                    stmt.setNull(2, Types.INTEGER);
                } else {
                    stmt.setInt(2, tmpDirection);
                }

                if (getTransactionTime() == null) {
                    stmt.setNull(3, Types.TIMESTAMP);
                } else {
                    stmt.setTimestamp(3, Timestamp.valueOf(getTransactionTime()));
                }

                stmt.setInt(4, get_id());

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
