package no.nsg.controller;

import no.nsg.repository.DocumentType;
import no.nsg.repository.MimeType;
import no.nsg.repository.dbo.BusinessDocumentDbo;
import no.nsg.repository.document.DocumentManager;
import no.nsg.repository.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.*;

//import org.springframework.web.util.ContentCachingRequestWrapper;


@Controller
@RestControllerAdvice
public class DocumentApiControllerImpl implements no.nsg.generated.document_api.DocumentApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentApiControllerImpl.class);

    @Autowired
    private DocumentManager documentManager;

    @Autowired
    private TransactionManager transactionManager;


    static class Document {
        public final String documentid;
        public final byte[] original;
        Document(final String documentid, final byte[] original) {
            this.documentid = documentid;
            this.original = original;
        }
    }


    DocumentApiControllerImpl() {}

    @Override
    public ResponseEntity<Void> createDocument(HttpServletRequest httpServletRequest, HttpServletResponse response, String companyId, String body) {
        return createDocumentInTransaction(httpServletRequest, response, companyId, null, body);
    }

    @Override
    public ResponseEntity<Void> createDocumentInTransaction(HttpServletRequest httpServletRequest, HttpServletResponse response, String companyId, String id, String body) {
        BusinessDocumentDbo persistedDocument;
        try {
            /*
            // If we do not get the document xml as string (as in, Spring Boot deserializes it to something else),
            // the http body can be fetched like this:
            ContentCachingRequestWrapper requestCacheWrapperObject = (ContentCachingRequestWrapper) httpServletRequest;
            String documentOriginal = new String(requestCacheWrapperObject.getContentAsByteArray(), requestCacheWrapperObject.getCharacterEncoding());
             */

            final String contentType = httpServletRequest.getContentType();
            DocumentType.Type documentType = DocumentType.fromMimeType(contentType);
            if (documentType == null) {
                throw new IllegalArgumentException("Please set Content-Type:-header to a supported MIME type: "+DocumentType.getDocumentMimeTypes());
            }

            persistedDocument = documentManager.createDocument(companyId, id, documentType, body);
        } catch (IllegalArgumentException| SAXException e) {
            LOGGER.error("POST_CREATEDOCUMENT failed to persist document: " + e.getMessage());
            try {
                response.sendError(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
            } catch (IOException e2) {
            }
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } catch (NoSuchElementException e) {
            LOGGER.error("POST_CREATEDOCUMENT failed to persist document");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LOGGER.error("POST_CREATEDOCUMENT failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (persistedDocument==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            URI location = persistedDocument.getLocation(ServletUriComponentsBuilder.fromCurrentRequest(), transactionManager);
            return ResponseEntity.created(location).build();
        }
    }

    @Override
    public ResponseEntity<Object> getDocumentById(HttpServletRequest httpServletRequest, HttpServletResponse response, String companyId, String transactionId, String id) {
        Document returnValue = null;
        try {
            BusinessDocumentDbo document = documentManager.getDocumentByGuid(id);
            if (document != null) {
                response.setContentType(DocumentType.toMimeType(document.getDocumenttype()));
                returnValue = new Document(document.getDocumentid(), document.getOriginal());
            }
        } catch (Exception e) {
            LOGGER.error("GET_GETDOCUMENT failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (returnValue==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(returnValue, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<Object> getDocuments(HttpServletRequest httpServletRequest, HttpServletResponse response, String companyId, String documentTypesAsString) {
        List<?> returnValue;
        try {
            // Parse wanted document types
            Set<DocumentType.Type> documentTypes = null;
            if (documentTypesAsString != null) {
                documentTypes = new HashSet<>();
                String[] documentTypeArray = documentTypesAsString.split(",");
                for (String s : documentTypeArray) {
                    DocumentType.Type documentType = DocumentType.fromMimeType(s);
                    if (documentType != null) {
                        documentTypes.add(documentType);
                    }
                }
            }

            final String accept = httpServletRequest.getHeader("Accept");
            if (MimeType.XML.equalsIgnoreCase(accept)) {
                returnValue = getDocumentBodies(companyId, documentTypes);
                response.setContentType(MimeType.XML);
            } else if (MimeType.JSON.equalsIgnoreCase(accept)) {
                returnValue = getDocumentIds(companyId, documentTypes);
                response.setContentType(MimeType.JSON);
            } else {
                throw new IllegalArgumentException("Please set Accept:-header to either \""+MimeType.JSON+"\" or \""+MimeType.XML+"\"");
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("GET_GETDOCUMENTS failed: " + e.getMessage());
            try {
                response.sendError(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
            } catch (IOException e2) {
            }
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            LOGGER.error("GET_GETDOCUMENTS failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (returnValue==null || returnValue.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(returnValue, HttpStatus.OK);
        }
    }

    public List<String> getDocumentIds(final String companyId, Set<DocumentType.Type> documentTypes) throws SQLException {
        List<String> returnValue = new ArrayList<>();
        List<BusinessDocumentDbo> invoices = documentManager.getDocuments(companyId, documentTypes);
        for (BusinessDocumentDbo invoice : invoices) {
            returnValue.add(invoice.getDocumentid());
        }
        return returnValue;
    }

    public List<Document> getDocumentBodies(final String companyId, Set<DocumentType.Type> documentTypes) throws SQLException {
        List<Document> returnValue = new ArrayList<>();
        List<BusinessDocumentDbo> invoices = documentManager.getDocuments(companyId, documentTypes);
        for (BusinessDocumentDbo invoice : invoices) {
            returnValue.add(new Document(invoice.getDocumentid(), invoice.getOriginal()));
        }
        return returnValue;
    }

}
