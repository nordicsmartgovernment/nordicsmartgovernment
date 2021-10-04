package no.nsg.controller;

import no.nsg.repository.ConnectionManager;
import no.nsg.repository.MimeType;
import no.nsg.utils.EmbeddedPostgresSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;


@Tag("ServiceTest")
public class TransactionsApiControllerTest extends EmbeddedPostgresSetup {
    private static Logger LOGGER = LoggerFactory.getLogger(TransactionsApiControllerTest.class);

    @Mock
    HttpServletRequest httpServletRequestMock;

    @Mock
    HttpServletResponse httpServletResponseMock;

    @Autowired
    TransactionsApi transactionsApiController;

    @Autowired
    DocumentApi documentApiController;

    @Autowired
    ConnectionManager connectionManager;

    private boolean hasInitializedInvoiceData = false;

    @BeforeEach
    public void before() throws IOException {
        initializeInvoiceData();
    }

    @Test
    public void happyDay()
    {
        Assertions.assertTrue(true);
    }

    @Test
    public void getTransactionsTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn(MimeType.JSON);
        ResponseEntity<Object> response = transactionsApiController.getTransactions(httpServletRequestMock, httpServletResponseMock, null, null, null, null, null);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Object responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
    }

    @Test
    public void getOrganizationTransactionsTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn(MimeType.JSON);
        ResponseEntity<Object> response = transactionsApiController.getTransactions(httpServletRequestMock, httpServletResponseMock, "20202020", null, null, null, null);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Object responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
    }

    @Test
    public void getOrganizationTransactionsAsXbrlTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn(MimeType.XBRL_GL);
        ResponseEntity<Object> response = transactionsApiController.getTransactions(httpServletRequestMock, httpServletResponseMock, "20202020", null, null, null, null);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Object responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
    }

    @Test
    public void getTransactionsStartingFromTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn(MimeType.JSON);
        final LocalDate startDate = LocalDate.of(2019, 1, 1);
        ResponseEntity<Object> response = transactionsApiController.getTransactions(httpServletRequestMock, httpServletResponseMock, null, startDate, null, null, null);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Object responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
    }

    @Test
    public void getTransactionsEndingFromTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn(MimeType.JSON);
        final LocalDate endDate = LocalDate.of(2020, 12, 31);
        ResponseEntity<Object> response = transactionsApiController.getTransactions(httpServletRequestMock, httpServletResponseMock, null, null, endDate, null, null);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Object responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
    }

    @Test
    public void getTransactionsBetweenStartEndFromTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn(MimeType.JSON);
        final LocalDate startDate = LocalDate.of(2019, 1, 1);
        final LocalDate endDate = LocalDate.of(2020, 12, 31);
        ResponseEntity<Object> response = transactionsApiController.getTransactions(httpServletRequestMock, httpServletResponseMock, null, startDate, endDate, null, null);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Object responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
    }

    @Test
    public void getInboundTransactionsTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn(MimeType.JSON);
        ResponseEntity<Object> response = transactionsApiController.getTransactions(httpServletRequestMock, httpServletResponseMock, null, null, null, null, "incoming");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Object responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
    }

    @Test
    public void getOutboundTransactionsTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn(MimeType.JSON);
        ResponseEntity<Object> response = transactionsApiController.getTransactions(httpServletRequestMock, httpServletResponseMock, null, null, null, null, "outgoing");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Object responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
    }

    @Test
    public void getOrganizationInboundTransactionsTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn(MimeType.JSON);
        ResponseEntity<Object> response = transactionsApiController.getTransactions(httpServletRequestMock, httpServletResponseMock, "20202020", null, null, null, "incoming");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Object responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
    }

    @Test
    public void patchTransactionByIdTest() throws IOException {
        final String companyId = "2372513-5";
        Mockito.when(httpServletRequestMock.getContentType()).thenReturn(MimeType.NSG_SALES_INVOICE);
        ResponseEntity<Void> createResponse = documentApiController.createDocument(httpServletRequestMock, httpServletResponseMock, companyId, resourceAsString("finvoice/finvoice 75 myynti.xml", StandardCharsets.UTF_8));
        Assertions.assertSame(createResponse.getStatusCode(), HttpStatus.CREATED);
        URI location = createResponse.getHeaders().getLocation();
        String[] paths = location.getPath().split("/");
        String createdTransactionId = paths[paths.length-2];
        String createdDocumentId = paths[paths.length-1];

        String xbrlDocument = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                              "<xbrli:xbrl xmlns:xbrli=\"http://www.xbrl.org/2003/instance\"/>";
        ResponseEntity<Void> response = transactionsApiController.putTransactionByDocumentId(httpServletRequestMock, httpServletResponseMock, companyId, createdTransactionId, createdDocumentId, xbrlDocument);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void initializeInvoiceData() throws IOException {
        final String customerId = "983294";
        Mockito.when(httpServletRequestMock.getContentType()).thenReturn(MimeType.NSG_SALES_INVOICE);

        if (!hasInitializedInvoiceData) {
            hasInitializedInvoiceData = true;

            connectionManager.waitUntilSyntheticDataIsImported();

            documentApiController.createDocument(httpServletRequestMock, httpServletResponseMock, customerId, resourceAsString("finvoice/Finvoice.xml", StandardCharsets.UTF_8));
            documentApiController.createDocument(httpServletRequestMock, httpServletResponseMock, customerId, resourceAsString("finvoice/finvoice 75 myynti.xml", StandardCharsets.UTF_8));
            documentApiController.createDocument(httpServletRequestMock, httpServletResponseMock, customerId, resourceAsString("finvoice/finvoice 76 myynti.xml", StandardCharsets.UTF_8));
            documentApiController.createDocument(httpServletRequestMock, httpServletResponseMock, customerId, resourceAsString("finvoice/finvoice 77 myynti.xml", StandardCharsets.UTF_8));
            documentApiController.createDocument(httpServletRequestMock, httpServletResponseMock, customerId, resourceAsString("finvoice/finvoice 78 myynti.xml", StandardCharsets.UTF_8));
            documentApiController.createDocument(httpServletRequestMock, httpServletResponseMock, customerId, resourceAsString("ubl/Invoice_base-example.xml", StandardCharsets.UTF_8));
        }
    }

    private static String resourceAsString(final String resource, final Charset charset) throws IOException {
        InputStream resourceStream = TransactionsApiControllerTest.class.getClassLoader().getResourceAsStream(resource);

        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceStream, charset))) {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
