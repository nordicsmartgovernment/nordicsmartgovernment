package no.nsg.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import no.nsg.generated.model.Invoice;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;


@RunWith(SpringRunner.class)
public class InvoicesApiControllerTest {


    @Test
    public void happyDay()
    {
        Assert.assertTrue(true);
    }

    @Test
    public void createInvoiceTest() {
        InvoicesApiControllerImpl invoicesApiController = new InvoicesApiControllerImpl();
        Invoice invoice = new Invoice();
        ResponseEntity<Void> response = invoicesApiController.createInvoice(invoice);
        Assert.assertNotEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
    }

    @Test
    public void getInvoicesTest() {
        InvoicesApiControllerImpl invoicesApiController = new InvoicesApiControllerImpl();
        ResponseEntity<List<Invoice>> response = invoicesApiController.getInvoices();
        Assert.assertNotEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
    }

    @Test
    public void testDeserialize() throws IOException {
        ObjectMapper xmlMapper = new XmlMapper();
        xmlMapper.findAndRegisterModules();

        Invoice invoice = xmlMapper.readValue("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Invoice>\n" +
                "  <CustomizationID>customizationID</CustomizationID>\n" +
                "  <ProfileID>profileID</ProfileID>\n" +
                "  <ID>id</ID>\n" +
                "  <IssueDate>2019-01-01</IssueDate>\n" +
                "  <DueDate>dueDate</DueDate>\n" +
                "  <InvoiceTypeCode>invoiceTypeCode</InvoiceTypeCode>\n" +
                "  <DocumentCurrencyCode>documentCurrencyCode</DocumentCurrencyCode>\n" +
                "  <AccountingCost>accountingCost</AccountingCost>\n" +
                "  <BuyerReference>buyerReference</BuyerReference>\n" +
                "</Invoice>", Invoice.class);

        Assert.assertNotNull(invoice);
    }

}
