package no.nsg.repository.document;

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
import java.util.*;


@Component
public class DocumentManager {

    @Autowired
    private ConnectionManager connectionManager;


    public BusinessDocumentDbo createDocument(final String companyId, final String transactionId, final DocumentType.Type documentType, final String documentOriginalXml) throws UnknownFormatConversionException, SQLException, IOException, SAXException {
        BusinessDocumentDbo document;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                document = createDocument(companyId, transactionId, documentType, documentOriginalXml, false, connection);
                connection.commit();
            } catch (Exception e) {
                try {
                    connection.rollback();
                    throw e;
                } catch (SQLException e2) {
                    throw e2;
                }
            }
        }
        return document;
    }

    public BusinessDocumentDbo createDocument(final String companyId, final String transactionId, final DocumentType.Type documentType, final String documentOriginalXml, final boolean isSynthetic, final Connection connection) throws UnknownFormatConversionException, SQLException, IOException, SAXException {
        BusinessDocumentDbo document = new BusinessDocumentDbo();
        if (isSynthetic) {
            document.setIsSynthetic();
        }
        document.setOriginalFromString(documentType, companyId, documentOriginalXml); //Will also set direction
        document.connectToTransaction(connection, transactionId);
        document.persist(connection);
        return document;
    }

    public BusinessDocumentDbo getDocumentByGuid(final String id) throws SQLException {
        BusinessDocumentDbo document = null;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                try {
                    document = getDocumentByGuid(id, connection);
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
        return document;
    }

    public BusinessDocumentDbo getDocumentByGuid(final String id, final Connection connection) throws SQLException, IOException {
        return new BusinessDocumentDbo(connection, BusinessDocumentDbo.findInternalId(connection, id));
    }

    public List<BusinessDocumentDbo> getDocuments(final String companyId, Set<DocumentType.Type> documentTypes) throws SQLException {
        try (Connection connection = connectionManager.getConnection()) {
            try {
                List<BusinessDocumentDbo> documents = getDocuments(connection, companyId, documentTypes);
                connection.commit();
                return documents;
            } catch (SQLException e) {
                try {
                    connection.rollback();
                    throw e;
                } catch (SQLException e2) {
                    throw e2;
                }
            }
        }
    }

    public List<BusinessDocumentDbo> getDocuments(final Connection connection, final String companyId, Set<DocumentType.Type> documentTypes) throws SQLException {
        List<BusinessDocumentDbo> documents = new ArrayList<>();

        String sql = "SELECT d._id "
                    +"FROM nsg.businessdocument d, nsg.transaction t, nsg.transactionset ts, nsg.company c "
                    +"WHERE d._id_transaction=t._id "
                    +"AND t._id_transactionset=ts._id "
                    +"AND ts._id_company=c._id "
                    +"AND c.orgno=? ";

        if (documentTypes!=null && !documentTypes.isEmpty()) {
            StringBuilder documentFilter = new StringBuilder();
            for (DocumentType.Type documentType : documentTypes) {
                if (documentFilter.length() == 0) {
                    documentFilter.append("AND d.documenttype IN (");
                } else {
                    documentFilter.append(',');
                }
                documentFilter.append(DocumentType.toInt(documentType));
            }
            documentFilter.append(") ");
            sql += documentFilter.toString();
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, companyId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                try {
                    documents.add(new BusinessDocumentDbo(connection, rs.getInt("_id")));
                } catch (NoSuchElementException|IOException e) {
                }
            }
            connection.commit();
        } catch (Exception e) {
            throw e;
        }

        return documents;
    }
}
