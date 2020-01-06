package no.nsg.repository.dbo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.*;
import java.util.*;


@JsonIgnoreProperties({"id"}) /* Default serialization insists on appending this lowercase id element?!? We do not want it */
public class CurrencyDbo {
    public static final int UNINITIALIZED = 0;

    @JsonIgnore
    private int _id;

    private String currency;

    private static Map<String,Integer> currencyCache = null;


    public CurrencyDbo() {
        this._id = UNINITIALIZED;
    }

    public CurrencyDbo(final Connection connection, final int id) throws SQLException {
        CurrencyDbo.initializeCurrencyCache(connection);

        if (id == UNINITIALIZED) {
            throw new NoSuchElementException();
        }

        final String sql = "SELECT currency FROM nsg.currency WHERE _id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new NoSuchElementException();
            }

            this._id = id;
            setCurrency(rs.getString("currency"));
        }
    }

    public int get_id() {
        return this._id;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return this.currency;
    }

    public static Integer findByCurrency(final String currency) {
        return currencyCache.get(currency);
    }

    public static synchronized void initializeCurrencyCache(final Connection connection) throws SQLException {
        if (currencyCache == null) {
            currencyCache = new HashMap<>();

            final String sql = "SELECT _id, currency FROM nsg.currency";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    currencyCache.put(rs.getString("currency"),
                            rs.getInt("_id"));
                }
            }
        }
    }

    private static synchronized void putCurrencyInCache(final Connection connection, final String currency, final Integer id) throws SQLException {
        CurrencyDbo.initializeCurrencyCache(connection);

        Iterator<Map.Entry<String, Integer>> iterator = currencyCache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            if (entry.getValue().equals(id)) {
                iterator.remove();
            }
        }

        currencyCache.put(currency, id);
    }

    public void persist(final Connection connection) throws SQLException {
        if (get_id() == UNINITIALIZED) {
            final String sql = "INSERT INTO nsg.currency (currency) " +
                                      "VALUES (?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, getCurrency());

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this._id = rs.getInt(1);
                }

                CurrencyDbo.putCurrencyInCache(connection, getCurrency(), get_id());
            }
        } else {
            final String sql = "UPDATE nsg.currency SET currency=? WHERE _id=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, getCurrency());
                stmt.setInt(2, get_id());

                stmt.executeUpdate();

                CurrencyDbo.putCurrencyInCache(connection, getCurrency(), get_id());
            }
        }
    }
}
