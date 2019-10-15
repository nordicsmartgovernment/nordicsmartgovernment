package no.nsg.repository.bankstatement;

import no.nsg.repository.ConnectionManager;
import no.nsg.repository.DocumentType;
import no.nsg.repository.dbo.BusinessDocumentDbo;
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


    public BusinessDocumentDbo createBankstatement(final String bankstatementOriginalXml) throws UnknownFormatConversionException, SQLException, IOException, SAXException {
        BusinessDocumentDbo bankstatement;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                bankstatement = new BusinessDocumentDbo();
                bankstatement.setDocumenttype(DocumentType.Type.BANKSTATEMENT);
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

    public BusinessDocumentDbo getBankstatementById(final String id) throws SQLException {
        BusinessDocumentDbo bankstatement = null;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                try {
                    bankstatement = new BusinessDocumentDbo(connection, BusinessDocumentDbo.findInternalId(connection, id));
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

    public List<BusinessDocumentDbo> getBankstatements() throws SQLException {
        List<BusinessDocumentDbo> bankstatements = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection()) {
            final String sql = "SELECT _id FROM nsg.businessdocument WHERE documenttype=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, DocumentType.toInt(DocumentType.Type.BANKSTATEMENT));

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    try {
                        bankstatements.add(new BusinessDocumentDbo(connection, rs.getInt("_id")));
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
