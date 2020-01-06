package no.nsg.repository.dbo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;


@JsonIgnoreProperties({"id"}) /* Default serialization insists on appending this lowercase id element?!? We do not want it */
public class AccountDbo {
    public static final int UNINITIALIZED = 0;

    @JsonIgnore
    private int _id;

    private String account;

    private static Map<String,Integer> accountCache = null;


    public AccountDbo() {
        this._id = UNINITIALIZED;
    }

    public AccountDbo(final Connection connection, final int id) throws SQLException {
        AccountDbo.initializeAccountCache(connection);

        if (id == UNINITIALIZED) {
            throw new NoSuchElementException();
        }

        final String sql = "SELECT account FROM nsg.account WHERE _id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new NoSuchElementException();
            }

            this._id = id;
            setAccount(rs.getString("account"));
        }
    }

    public int get_id() {
        return this._id;
    }

    public void setAccount(final String account) {
        this.account = account;
    }

    public String getAccount() {
        return this.account;
    }

    public static Integer findByAccount(final String account) {
        return accountCache.get(account);
    }

    public static synchronized void initializeAccountCache(final Connection connection) throws SQLException {
        if (accountCache == null) {
            accountCache = new HashMap<>();

            final String sql = "SELECT _id, account FROM nsg.account";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    accountCache.put(rs.getString("account"),
                                     rs.getInt("_id"));
                }
            }
        }
    }

    private static synchronized void putAccountInCache(final Connection connection, final String account, final Integer id) throws SQLException {
        AccountDbo.initializeAccountCache(connection);

        Iterator<Map.Entry<String, Integer>> iterator = accountCache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            if (entry.getValue().equals(id)) {
                iterator.remove();
            }
        }

        accountCache.put(account, id);
    }

    public void persist(final Connection connection) throws SQLException {
        if (get_id() == UNINITIALIZED) {
            final String sql = "INSERT INTO nsg.account (account) " +
                                      "VALUES (?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, getAccount());

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this._id = rs.getInt(1);
                }

                AccountDbo.putAccountInCache(connection, getAccount(), get_id());
            }
        } else {
            final String sql = "UPDATE nsg.account SET account=? WHERE _id=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, getAccount());
                stmt.setInt(2, get_id());

                stmt.executeUpdate();

                AccountDbo.putAccountInCache(connection, getAccount(), get_id());
            }
        }
    }
}
