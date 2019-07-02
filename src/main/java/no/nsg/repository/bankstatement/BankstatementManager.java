package no.nsg.repository.bankstatement;

import no.nsg.repository.ConnectionManager;
import no.nsg.repository.dbo.DocumentDbo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UnknownFormatConversionException;


@Component
public class BankstatementManager {

    @Autowired
    private ConnectionManager connectionManager;


    public Object createBankstatement(final String bankstatementOriginalXml) throws UnknownFormatConversionException, SQLException, IOException, SAXException {
        DocumentDbo bankstatement;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                bankstatement = new DocumentDbo();
                bankstatement.setDocumenttype(DocumentDbo.DOCUMENTTYPE_BANKSTATEMENT);
                bankstatement.setOriginalFromString(bankstatementOriginalXml);
                bankstatement.persist(connection);
                connection.commit();
            } catch (SQLException | SAXException e) {
                try {
                    connection.rollback();
                    throw e;
                } catch (SQLException e2) {
                    throw e2;
                }
            }
        }
        return bankstatement;
    }

    public DocumentDbo getBankstatementById(final String id) throws SQLException {
        DocumentDbo bankstatement = null;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                try {
                    bankstatement = new DocumentDbo(connection, DocumentDbo.findInternalId(connection, id));
                } catch (NoSuchElementException|NumberFormatException|IOException e) {
                }
                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                    throw e;
                } catch (SQLException e2) {
                    throw e2;
                }
            }
        }
        return bankstatement;
    }

    public List<DocumentDbo> getBankstatements() throws SQLException {
        List<DocumentDbo> bankstatements = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection()) {
            final String sql = "SELECT _id FROM nsg.document WHERE documenttype=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, DocumentDbo.DOCUMENTTYPE_BANKSTATEMENT);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    try {
                        bankstatements.add(new DocumentDbo(connection, rs.getInt("_id")));
                    } catch (NoSuchElementException|IOException e) {
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                    throw e;
                } catch (SQLException e2) {
                    throw e2;
                }
            }
        }
        return bankstatements;
    }
}
