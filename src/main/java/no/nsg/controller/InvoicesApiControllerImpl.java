package no.nsg.controller;

import no.nsg.generated.model.Invoice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


@Controller
public class InvoicesApiControllerImpl implements no.nsg.generated.api.InvoicesApi {

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
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Invoice> getInvoiceById(String id) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Invoice>> getInvoices() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
