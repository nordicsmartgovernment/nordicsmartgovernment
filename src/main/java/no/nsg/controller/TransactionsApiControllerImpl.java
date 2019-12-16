package no.nsg.controller;

import com.github.dnault.xmlpatch.PatchException;
import no.nsg.repository.MimeType;
import no.nsg.repository.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

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
    public ResponseEntity<Object> getTransactionById(HttpServletRequest httpServletRequest, HttpServletResponse response, String companyId, String transactionId) {
        String transaction;
        try {
            final String accept = httpServletRequest.getHeader("Accept");
            if (MimeType.XBRL_GL.equalsIgnoreCase(accept)) {
                transaction = transactionManager.getTransactionDocumentAsXbrlGl(transactionId);
                response.setContentType(MimeType.XBRL_GL);
            } else if (MimeType.SAF_T.equalsIgnoreCase(accept)) {
                transaction = transactionManager.getTransactionDocumentAsSafT(transactionId);
                response.setContentType(MimeType.SAF_T);
            } else {
                throw new IllegalArgumentException("Please set Accept:-header to either \""+MimeType.SAF_T+"\" or \""+MimeType.XBRL_GL+"\"");
            }
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
    public ResponseEntity<Object> getTransactions(HttpServletRequest httpServletRequest, HttpServletResponse response, String companyId, String filterDocumentId, String finterInvoiceType) {
        Object returnValue;
        boolean noContent = false;
        try {
            List<String> transactionIds = transactionManager.getTransactionIds(companyId, filterDocumentId, finterInvoiceType);

            final String accept = httpServletRequest.getHeader("Accept");
            if (MimeType.XBRL_GL.equalsIgnoreCase(accept)) {
                returnValue = transactionManager.getTransactionDocumentAsXbrlGl(transactionIds);
                response.setContentType(MimeType.XBRL_GL);
            } else if (MimeType.SAF_T.equalsIgnoreCase(accept)) {
                    returnValue = transactionManager.getTransactionDocumentAsSafT(transactionIds);
                    response.setContentType(MimeType.SAF_T);
            } else if (MimeType.JSON.equalsIgnoreCase(accept)) {
                returnValue = transactionIds;
                noContent = (transactionIds==null || transactionIds.isEmpty());
                response.setContentType(MimeType.JSON);
            } else {
                throw new IllegalArgumentException("Please set Accept:-header to either \""+MimeType.JSON+"\", \""+MimeType.SAF_T+"\" or \""+MimeType.XBRL_GL+"\"");
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("GET_GETTRANSACTIONS failed: " + e.getMessage());
            try {
                response.sendError(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
            } catch (IOException e2) {
            }
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            LOGGER.error("GET_GETTRANSACTIONS failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (returnValue==null || noContent) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(returnValue, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<Void> putTransactionById(HttpServletRequest httpServletRequest, HttpServletResponse response, String companyId, String id, String body) {
        try {
            transactionManager.putTransactionById(id, body);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (PatchException| SAXException | IllegalArgumentException e) {
            LOGGER.error("PATCH_PATCHTRANSACTION patch failed:", e);
            try {
                response.sendError(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
            } catch (IOException e2) {
            }
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            LOGGER.error("PATCH_PATCHTRANSACTION failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
