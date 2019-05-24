package no.nsg.controller;

import no.nsg.generated.invoice_model.Invoice;
import no.nsg.repository.InvoiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
public class InvoicesApiControllerImpl implements no.nsg.generated.invoice_api.InvoicesApi {
    private static Logger LOGGER = LoggerFactory.getLogger(InvoicesApiControllerImpl.class);

    @Autowired
    private InvoiceManager invoiceManager;

    @GetMapping(value="/ping", produces={"text/plain"})
    public ResponseEntity<String> getPing() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping(value="/ready")
    public ResponseEntity getReady() {
        return ResponseEntity.ok().build();
    }

    /*
     * generated API implementation
     */

    @Override
    public ResponseEntity<Void> createInvoice(HttpServletRequest httpServletRequest, String body) {
        Object persistedInvoice;
        try {
            /*
            // If we do not get the invoice xml as string (as in, Spring Boot deserializes it to something else),
            // the http body can be fetched like this:
            ContentCachingRequestWrapper requestCacheWrapperObject = (ContentCachingRequestWrapper) httpServletRequest;
            String invoiceOriginal = new String(requestCacheWrapperObject.getContentAsByteArray(), requestCacheWrapperObject.getCharacterEncoding());
             */
            persistedInvoice = invoiceManager.createInvoice(body);
        } catch (Exception e) {
            LOGGER.error("POST_CREATEINVOICE failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (persistedInvoice==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @Override
    public ResponseEntity<Invoice> getInvoiceById(HttpServletRequest httpServletRequest, String id) {
        Invoice invoice;
        try {
            invoice = invoiceManager.getInvoiceById(id);
        } catch (Exception e) {
            LOGGER.error("GET_GETINVOICE failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (invoice==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(invoice, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<List<Invoice>> getInvoices(HttpServletRequest httpServletRequest) {
        List<Invoice> invoices;
        try {
            invoices = invoiceManager.getInvoices();
        } catch (Exception e) {
            LOGGER.error("GET_GETINVOICES failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (invoices==null || invoices.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(invoices, HttpStatus.OK);
        }
    }

}
