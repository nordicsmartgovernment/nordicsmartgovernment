package no.nsg.controller;

import no.nsg.repository.dbo.BusinessDocumentDbo;
import no.nsg.repository.invoice.InvoiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
//import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

//force build 87878787

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

    @GetMapping(value="invoices/ping", produces={"text/plain"})
    public ResponseEntity<String> getPing() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping(value="invoices/ready")
    public ResponseEntity getReady() {
        return ResponseEntity.ok().build();
    }

    /*
     * generated API implementation
     */

    @Override
    public ResponseEntity<Void> createInvoice(Principal principal, HttpServletRequest httpServletRequest, String body) {
        BusinessDocumentDbo persistedInvoice;
        try {
            /*
            // If we do not get the invoice xml as string (as in, Spring Boot deserializes it to something else),
            // the http body can be fetched like this:
            ContentCachingRequestWrapper requestCacheWrapperObject = (ContentCachingRequestWrapper) httpServletRequest;
            String invoiceOriginal = new String(requestCacheWrapperObject.getContentAsByteArray(), requestCacheWrapperObject.getCharacterEncoding());
             */
            persistedInvoice = invoiceManager.createInvoice(principal.getName(), body);
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
    public ResponseEntity<Object> getInvoiceById(Principal principal, HttpServletRequest httpServletRequest, String id) {
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
    public ResponseEntity<List<Object>> getInvoices(Principal principal, HttpServletRequest httpServletRequest) {
        List<Object> returnValue = new ArrayList<>();
        try {
            List<BusinessDocumentDbo> invoices = invoiceManager.getInvoices();
            for (BusinessDocumentDbo invoice : invoices) {
                returnValue.add(new Invoice(invoice.getDocumentid(), invoice.getOriginal()));
            }
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

}
