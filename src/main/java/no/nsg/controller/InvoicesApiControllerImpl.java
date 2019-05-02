package no.nsg.controller;

import no.nsg.generated.model.Invoice;
import no.nsg.repository.InvoiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


@Controller
public class InvoicesApiControllerImpl implements no.nsg.generated.api.InvoicesApi {
    private static Logger LOGGER = LoggerFactory.getLogger(InvoicesApiControllerImpl.class);

    @Autowired
    private InvoiceManager invoiceManager;

    @RequestMapping(value="/ping", method=RequestMethod.GET, produces={"text/plain"})
    public ResponseEntity<String> getPing() {
        return ResponseEntity.ok("pong");
    }

    @RequestMapping(value="/ready", method=RequestMethod.GET)
    public ResponseEntity getReady() {
        return ResponseEntity.ok().build();
    }

    /*
     * generated API implementation
     */

    @Override
    public ResponseEntity<Void> createInvoice(Invoice invoice) {
        Invoice persistedInvoice;
        try {
            persistedInvoice = invoiceManager.createInvoice(invoice);
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
    public ResponseEntity<Invoice> getInvoiceById(String id) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Invoice>> getInvoices() {
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
            return new ResponseEntity<>(invoices, HttpStatus.CREATED);
        }
    }

}
