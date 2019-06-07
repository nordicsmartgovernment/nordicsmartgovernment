package no.nsg.controller;

import no.nsg.repository.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

//import org.springframework.web.util.ContentCachingRequestWrapper;


@Controller
public class TransactionApiControllerImpl implements no.nsg.generated.transaction_api.TransactionsApi {
    private static Logger LOGGER = LoggerFactory.getLogger(TransactionApiControllerImpl.class);

    @Autowired
    private TransactionManager transactionManager;

    @GetMapping(value="transactions/ping", produces={"text/plain"})
    public ResponseEntity<String> getPing() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping(value="transactions//ready")
    public ResponseEntity getReady() {
        return ResponseEntity.ok().build();
    }

    /*
     * generated API implementation
     */

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @Override
    public ResponseEntity<Object> getTransactionById(HttpServletRequest httpServletRequest, String id) {
        Object transaction;
        try {
            transaction = transactionManager.getTransactionById(id);
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
    public ResponseEntity<List<Object>> getTransactions(HttpServletRequest httpServletRequest) {
        List<Object> transactions;
        try {
            transactions = transactionManager.getTransactions();
        } catch (Exception e) {
            LOGGER.error("GET_GETTRANSACTIONS failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (transactions==null || transactions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        }
    }
}
