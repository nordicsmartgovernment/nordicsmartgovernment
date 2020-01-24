package no.nsg.repository.dbo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.NoSuchElementException;


@JsonIgnoreProperties({"id"}) /* Default serialization insists on appending this lowercase id element?!? We do not want it */
public class TransactionSetDbo {
    public static final int UNINITIALIZED = 0;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionSetDbo.class);

    public static final String DEFAULT_NAME = "default";


    @JsonIgnore
    private int _id;

    @JsonIgnore
    private int _id_company;

    private String name;


    public TransactionSetDbo() {
        this(null);
    }

    public TransactionSetDbo(final CompanyDbo companyDbo) {
        this(companyDbo, DEFAULT_NAME);
    }

    public TransactionSetDbo(final CompanyDbo companyDbo, final String name) {
        this._id = UNINITIALIZED;
        set_CompanyId(companyDbo == null ? CompanyDbo.UNINITIALIZED : companyDbo.get_id());
        setName(name);
    }

    public TransactionSetDbo(final Connection connection, final int id) throws SQLException {
        if (id == UNINITIALIZED) {
            throw new NoSuchElementException();
        }

        final String sql = "SELECT _id_company, name FROM nsg.transactionset WHERE _id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new NoSuchElementException();
            }

            this._id = id;
            set_CompanyId(rs.getInt("_id_company"));
            setName(rs.getString("name"));
        }
    }

    public int get_id() {
        return this._id;
    }

    private void set_CompanyId(final int _companyId) {
        this._id_company = _companyId;
    }

    private int get_CompanyId() {
        return this._id_company;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static TransactionSetDbo getOrCreateByCompanyAndName(final Connection connection, final CompanyDbo company, final String name) throws SQLException {
        try {
            int id = TransactionSetDbo.findByCompanyAndName(connection, company, name);
            return new TransactionSetDbo(connection, id);
        } catch (NoSuchElementException e) {
            TransactionSetDbo newTransactionSet = new TransactionSetDbo();
            newTransactionSet.set_CompanyId(company.get_id());
            newTransactionSet.setName(name);
            newTransactionSet.persist(connection);
            return newTransactionSet;
        }
    }

    public static int findByCompanyAndName(final Connection connection, final CompanyDbo company, final String name) throws SQLException {
        final String sql = "SELECT _id FROM nsg.transactionset WHERE _id_company=? AND name=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, company.get_id());
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("_id");
            }
        }
        throw new NoSuchElementException();
    }

    public void persist(final Connection connection) throws SQLException {
        if (get_id() == UNINITIALIZED) {
            final String sql = "INSERT INTO nsg.transactionset (_id_company, name) " +
                                      "VALUES (?,?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, get_CompanyId());
                stmt.setString(2, getName());

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this._id = rs.getInt(1);
                }
            }
        } else {
            final String sql = "UPDATE nsg.transactionset SET _id_company=?, name=? WHERE _id=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, get_CompanyId());
                stmt.setString(2, getName());
                stmt.setInt(3, get_id());

                if (stmt.executeUpdate() == 0) {
                    LOGGER.error("TransactionSetDbo executeUpdate returned 0");
                }
            }
        }
    }
}
