package no.nsg.controller;

import no.nsg.repository.DocumentType;
import no.nsg.repository.dbo.BusinessDocumentDbo;
import no.nsg.repository.invoice.InvoiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.xml.sax.SAXException;
//import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@Controller
public class InvoicesApiControllerImpl implements no.nsg.generated.invoice_api.InvoicesApi {
    private static Logger LOGGER = LoggerFactory.getLogger(InvoicesApiControllerImpl.class);

    @Autowired
    private InvoiceManager invoiceManager;


    class Invoice {
        public final String documentid;
        public final byte[] original;
        Invoice(final String documentid, final byte[] original) {
            this.documentid = documentid;
            this.original = original;
        }
    }


    InvoicesApiControllerImpl() {}

    /*
     * generated API implementation
     */

    @Override
    public ResponseEntity<Void> createInvoice(HttpServletRequest httpServletRequest, HttpServletResponse response, String companyId, String body) {
        BusinessDocumentDbo persistedInvoice;
        try {
            /*
            // If we do not get the invoice xml as string (as in, Spring Boot deserializes it to something else),
            // the http body can be fetched like this:
            ContentCachingRequestWrapper requestCacheWrapperObject = (ContentCachingRequestWrapper) httpServletRequest;
            String invoiceOriginal = new String(requestCacheWrapperObject.getContentAsByteArray(), requestCacheWrapperObject.getCharacterEncoding());
             */

            final String contentType = httpServletRequest.getContentType();
            DocumentType.Type documentType = DocumentType.fromMimeType(contentType);
            if (documentType == null) {
                throw new IllegalArgumentException("Please set Content-Type:-header to a supported MIME type: "+DocumentType.getDocumentMimeTypes());
            }

            persistedInvoice = invoiceManager.createInvoice(companyId, documentType, body);
        } catch (IllegalArgumentException| SAXException e) {
            LOGGER.error("POST_CREATEINVOICE failed to persist invoice: " + e.getMessage());
            try {
                response.sendError(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
            } catch (IOException e2) {
            }
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } catch (NoSuchElementException e) {
            LOGGER.error("POST_CREATEINVOICE failed to persist invoice");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LOGGER.error("POST_CREATEINVOICE failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (persistedInvoice==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/invoices/{id}").buildAndExpand(persistedInvoice.getDocumentid()).toUri();
            return ResponseEntity.created(location).build();
        }
    }

    @Override
    public ResponseEntity<Object> getInvoiceById(HttpServletRequest httpServletRequest, HttpServletResponse response, String companyId, String id) {
        Invoice returnValue = null;
        try {
            BusinessDocumentDbo invoice = invoiceManager.getInvoiceById(id);
            if (invoice != null) {
                returnValue = new Invoice(invoice.getDocumentid(), invoice.getOriginal());
            }
        } catch (Exception e) {
            LOGGER.error("GET_GETINVOICE failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (returnValue==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(returnValue, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<Object> getInvoices(HttpServletRequest httpServletRequest, HttpServletResponse response, String companyId) {
        List<? extends Object> returnValue;
        try {
            final String accept = httpServletRequest.getHeader("Accept");
            if ("application/xml".equalsIgnoreCase(accept)) {
                returnValue = getInvoiceBodies(companyId);
                response.setContentType("application/xml");
            } else if ("application/json".equalsIgnoreCase(accept)) {
                returnValue = getInvoiceIds(companyId);
                response.setContentType("application/json");
            } else {
                throw new IllegalArgumentException("Please set Accept:-header to either \"application/json\" or \"application/xml\"");
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("GET_GETINVOICES failed: " + e.getMessage());
            try {
                response.sendError(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
            } catch (IOException e2) {
            }
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            LOGGER.error("GET_GETINVOICES failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (returnValue==null || returnValue.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(returnValue, HttpStatus.OK);
        }
    }

    public List<String> getInvoiceIds(final String companyId) throws SQLException {
        List<String> returnValue = new ArrayList<>();
        List<BusinessDocumentDbo> invoices = invoiceManager.getInvoices(companyId);
        for (BusinessDocumentDbo invoice : invoices) {
            returnValue.add(invoice.getDocumentid());
        }
        return returnValue;
    }

    public List<Invoice> getInvoiceBodies(final String companyId) throws SQLException {
        List<Invoice> returnValue = new ArrayList<>();
        List<BusinessDocumentDbo> invoices = invoiceManager.getInvoices(companyId);
        for (BusinessDocumentDbo invoice : invoices) {
            returnValue.add(new Invoice(invoice.getDocumentid(), invoice.getOriginal()));
        }
        return returnValue;
    }

}
