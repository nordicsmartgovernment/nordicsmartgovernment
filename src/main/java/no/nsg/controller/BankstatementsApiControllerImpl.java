package no.nsg.controller;

import no.nsg.repository.bankstatement.BankstatementManager;
import no.nsg.repository.dbo.DocumentDbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@Controller
public class BankstatementsApiControllerImpl implements no.nsg.generated.bankstatements_api.BankStatementsApi {
    private static Logger LOGGER = LoggerFactory.getLogger(BankstatementsApiControllerImpl.class);

    @Autowired
    private BankstatementManager bankstatementManager;


    class Bankstatement {
        public final String documentid;
        public final byte[] original;
        Bankstatement(final String documentid, final byte[] original) {
            this.documentid = documentid;
            this.original = original;
        }
    }


    BankstatementsApiControllerImpl() {}

    @GetMapping(value="bankStatements/ping", produces={"text/plain"})
    public ResponseEntity<String> getPing() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping(value="bankStatements/ready")
    public ResponseEntity getReady() {
        return ResponseEntity.ok().build();
    }

    /*
     * generated API implementation
     */

    @Override
    public ResponseEntity<Void> createBankStatement(HttpServletRequest httpServletRequest, String body) {
        Object persistedBankstatement;
        try {
            /*
            // If we do not get the bankstatement xml as string (as in, Spring Boot deserializes it to something else),
            // the http body can be fetched like this:
            ContentCachingRequestWrapper requestCacheWrapperObject = (ContentCachingRequestWrapper) httpServletRequest;
            String bankstatementOriginal = new String(requestCacheWrapperObject.getContentAsByteArray(), requestCacheWrapperObject.getCharacterEncoding());
             */
            persistedBankstatement = bankstatementManager.createBankstatement(body);
        } catch (NoSuchElementException e) {
            LOGGER.error("POST_CREATEBANKSTATEMENT failed to persist bankstatement");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LOGGER.error("POST_CREATEBANKSTATEMENT failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (persistedBankstatement==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @Override
    public ResponseEntity<Object> getBankStatementById(HttpServletRequest httpServletRequest, String id) {
        Bankstatement returnValue = null;
        try {
            DocumentDbo bankstatement = bankstatementManager.getBankstatementById(id);
            if (bankstatement != null) {
                returnValue = new Bankstatement(bankstatement.getDocumentid(), bankstatement.getOriginal());
            }
        } catch (Exception e) {
            LOGGER.error("GET_GETBANKSTATEMENT failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (returnValue==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(returnValue, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<List<Object>> getBankStatements(HttpServletRequest httpServletRequest) {
        List<Object> returnValue = new ArrayList<>();
        try {
            List<DocumentDbo> bankstatements = bankstatementManager.getBankstatements();
            for (DocumentDbo bankstatement : bankstatements) {
                returnValue.add(new Bankstatement(bankstatement.getDocumentid(), bankstatement.getOriginal()));
            }
        } catch (Exception e) {
            LOGGER.error("GET_GETBANKSTATEMENTS failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (returnValue==null || returnValue.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(returnValue, HttpStatus.OK);
        }
    }

}
