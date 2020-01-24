package no.nsg.repository.dbo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.NoSuchElementException;


@JsonIgnoreProperties({"id"}) /* Default serialization insists on appending this lowercase id element?!? We do not want it */
public class JournalDbo {
    public static final int UNINITIALIZED = 0;
    private static final Logger LOGGER = LoggerFactory.getLogger(JournalDbo.class);

    @JsonIgnore
    private int _id;


    public JournalDbo() {
        this._id = UNINITIALIZED;
    }

    public JournalDbo(final Connection connection, final int id) throws SQLException {
        if (id == UNINITIALIZED) {
            throw new NoSuchElementException();
        }

        final String sql = "SELECT * FROM nsg.company WHERE _id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new NoSuchElementException();
            }

            this._id = id;
        }
    }

    public int get_id() {
        return this._id;
    }

    public void persist(final Connection connection) throws SQLException {
        if (get_id() == UNINITIALIZED) {
            final String sql = "INSERT INTO nsg.journal () " +
                                      "VALUES ()";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this._id = rs.getInt(1);
                }
            }
        } else {
            final String sql = "UPDATE nsg.company SET _id=_id WHERE _id=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, get_id());

                if (stmt.executeUpdate() == 0) {
                    LOGGER.error("JournalDbo executeUpdate returned 0");
                }
            }
        }
    }
}
