package no.nsg.controller;

import no.nsg.generated.model.Invoice;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
public class InvoicesApiControllerTest {


    @Test
    public void happyDay()
    {
        Assert.assertTrue(true);
    }

    @Test
    public void getInvoicesTest() {
        InvoicesApiControllerImpl invoicesApiController = new InvoicesApiControllerImpl();
        ResponseEntity<List<Invoice>> response = invoicesApiController.getInvoices();
        Assert.assertNotEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
    }

}
