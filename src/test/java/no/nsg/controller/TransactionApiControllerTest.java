package no.nsg.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RunWith(SpringRunner.class)
public class TransactionApiControllerTest {

    @Mock
    HttpServletRequest httpServletRequestMock;

    @Test
    public void happyDay()
    {
        Assert.assertTrue(true);
    }

    @Test
    public void getTransactionsTest() {
        TransactionApiControllerImpl transactionApiController = new TransactionApiControllerImpl();
        ResponseEntity<List<Object>> response = transactionApiController.getTransactions(httpServletRequestMock);
        Assert.assertNotEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
    }

    @Test
    public void getTransactionByIdTest() {
        final String id = "1";
        TransactionApiControllerImpl transactionApiController = new TransactionApiControllerImpl();
        ResponseEntity<Object> response = transactionApiController.getTransactionById(httpServletRequestMock, id);
        Assert.assertNotEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
    }

}
