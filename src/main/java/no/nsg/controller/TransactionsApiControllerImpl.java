package no.nsg.controller;

import com.github.dnault.xmlpatch.PatchException;
import no.nsg.repository.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

//import org.springframework.web.util.ContentCachingRequestWrapper;


@Controller
public class TransactionsApiControllerImpl implements no.nsg.generated.transaction_api.TransactionsApi {
    private static Logger LOGGER = LoggerFactory.getLogger(TransactionsApiControllerImpl.class);

    @Autowired
    private TransactionManager transactionManager;


    /*
     * generated API implementation
     */

    @Override
    public ResponseEntity<Object> getTransactionById(Principal principal, HttpServletRequest httpServletRequest, String transactionId) {
        String transaction;
        try {
            transaction = transactionManager.getTransactionDocument(transactionId);
        } catch (Exception e) {
            LOGGER.error("GET_GETTRANSACTION failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (transaction==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<List<String>> getTransactions(Principal principal, HttpServletRequest httpServletRequest, String filterDocumentId, String filterOrganizationId, String finterInvoiceType) {
        List<String> transactionIds;
        try {
            transactionIds = transactionManager.getTransactionIds(filterDocumentId, filterOrganizationId, finterInvoiceType);
        } catch (Exception e) {
            LOGGER.error("GET_GETTRANSACTIONS failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (transactionIds==null || transactionIds.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(transactionIds, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<Object> patchTransactionById(Principal principal, HttpServletRequest httpServletRequest, String id, String patchXml) {
        Object transaction;
        try {
            transaction = transactionManager.patchTransactionById(id, patchXml);
        } catch (PatchException e) {
            LOGGER.error("PATCH_PATCHTRANSACTION patch failed:", e);
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            LOGGER.error("PATCH_PATCHTRANSACTION failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (transaction==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        }
    }

}
