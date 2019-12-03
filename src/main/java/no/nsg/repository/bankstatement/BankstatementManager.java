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


    public BusinessDocumentDbo createBankstatement(final String companyId, final DocumentType.Type documentType, final String bankstatementOriginalXml, final boolean isSynthetic) throws UnknownFormatConversionException, SQLException, IOException, SAXException {
        BusinessDocumentDbo bankstatement;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                bankstatement = new BusinessDocumentDbo();
                if (isSynthetic) {
                    bankstatement.setIsSynthetic();
                }
                bankstatement.setDocumenttype(documentType);
                bankstatement.setOriginalFromString(companyId, bankstatementOriginalXml);
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

    public List<BusinessDocumentDbo> getBankstatements(final String companyId) throws SQLException {
        List<BusinessDocumentDbo> bankstatements = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection()) {
            final String sql = "SELECT d._id "
                              +"FROM nsg.businessdocument d, nsg.transaction t, nsg.transactionset ts, nsg.company c "
                              +"WHERE d._id_transaction=t._id "
                              +"AND t._id_transactionset=ts._id "
                              +"AND ts._id_company=c._id "
                              +"AND d.documenttype=?"
                              +"AND c.orgno=?";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, DocumentType.toInt(DocumentType.Type.BANK_STATEMENT));
                stmt.setString(2, companyId);

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
