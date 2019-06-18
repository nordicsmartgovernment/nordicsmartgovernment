package no.nsg.repository.dbo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.*;
import java.util.NoSuchElementException;


@JsonIgnoreProperties({"id"}) /* Default serialization insists on appending this lowercase id element?!? We do not want it */
public class TransactionDbo {
    public static final int UNINITIALIZED = 0;

    @JsonIgnore
    private int _id;

    @JsonIgnore
    private int _companyid;


    public TransactionDbo(final CompanyDbo companyDbo) {
        this._id = UNINITIALIZED;
        set_CompanyId(companyDbo == null ? CompanyDbo.UNINITIALIZED : companyDbo.get_id());
    }

    public TransactionDbo(final Connection connection, final int _id) throws SQLException {
        if (_id == UNINITIALIZED) {
            throw new NoSuchElementException();
        }

        final String sql = "SELECT _companyid FROM nsg.transaction WHERE _id=?";
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
        }
    }

    public int get_id() {
        return this._id;
    }

    public void set_CompanyId(int _companyId) {
        this._companyid = _companyId;
    }

    public void persist(final Connection connection) throws SQLException {
        if (get_id() == UNINITIALIZED) {
            final String sql = "INSERT INTO nsg.transaction (_companyid) VALUES (?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                if (_companyid == CompanyDbo.UNINITIALIZED) {
                    stmt.setNull(1, Types.INTEGER);
                } else {
                    stmt.setInt(1, _companyid);
                }

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this._id = rs.getInt(1);
                }
            }
        } else {
            final String sql = "UPDATE nsg.transaction SET _companyid=? WHERE _id=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                if (_companyid == CompanyDbo.UNINITIALIZED) {
                    stmt.setNull(1, Types.INTEGER);
                } else {
                    stmt.setInt(1, _companyid);
                }
                stmt.setInt(2, get_id());

                stmt.executeUpdate();
            }
        }
    }
}
