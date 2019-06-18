package no.nsg.repository.dbo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.*;
import java.util.NoSuchElementException;


@JsonIgnoreProperties({"id"}) /* Default serialization insists on appending this lowercase id element?!? We do not want it */
public class CompanyDbo {
    public static final int UNINITIALIZED = 0;

    @JsonIgnore
    private int _id;

    private String orgno;


    public CompanyDbo() {
        this._id = UNINITIALIZED;
    }

    public CompanyDbo(final Connection connection, final int _id) throws SQLException {
        if (_id == UNINITIALIZED) {
            throw new NoSuchElementException();
        }

        final String sql = "SELECT orgno FROM nsg.company WHERE _id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, _id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new NoSuchElementException();
            }

            this._id = _id;
            this.orgno = rs.getString("orgno");
        }
    }

    public int get_id() {
        return this._id;
    }

    public void setOrgno(final String orgno) {
        this.orgno = orgno;
    }

    public static CompanyDbo getOrCreateByOrgno(final Connection connection, final String orgno) throws SQLException {
        try {
            int id = findByOrgno(connection, orgno);
            return new CompanyDbo(connection, id);
        } catch (NoSuchElementException e) {
            CompanyDbo newCompany = new CompanyDbo();
            newCompany.setOrgno(orgno);
            newCompany.persist(connection);
            return newCompany;
        }
    }

    public static int findByOrgno(final Connection connection, final String orgno) throws SQLException {
        final String sql = "SELECT _id FROM nsg.company WHERE orgno=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, orgno);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("_id");
            }
        }
        throw new NoSuchElementException();
    }

    public void persist(final Connection connection) throws SQLException {
        if (get_id() == UNINITIALIZED) {
            final String sql = "INSERT INTO nsg.company (orgno) " +
                                      "VALUES (?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, this.orgno);

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this._id = rs.getInt(1);
                }
            }
        } else {
            final String sql = "UPDATE nsg.company SET orgno=? WHERE _id=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, this.orgno);
                stmt.setInt(2, get_id());

                stmt.executeUpdate();
            }
        }
    }
}
