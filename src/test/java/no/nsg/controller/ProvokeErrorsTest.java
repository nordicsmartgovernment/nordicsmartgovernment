package no.nsg.controller;

import no.nsg.repository.ConnectionManager;
import no.nsg.repository.MimeType;
import no.nsg.repository.transaction.TransactionManager;
import no.nsg.utils.EmbeddedPostgresSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;


@Tag("ServiceTest")
public class ProvokeErrorsTest extends EmbeddedPostgresSetup {
    private static Logger LOGGER = LoggerFactory.getLogger(ProvokeErrorsTest.class);

    @Mock
    HttpServletRequest httpServletRequestMock;

    @Mock
    HttpServletResponse httpServletResponseMock;

    @InjectMocks
    TransactionsApi transactionsApiController; //For injecting transactionManager mock

    @Autowired
    DocumentApi documentApiController;

    @Autowired
    ConnectionManager connectionManager;

    @Mock
    TransactionManager transactionManager;

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
    public void getTransactionOwnersProvokeErrorTest1() throws SQLException {
        Mockito.when(transactionManager.getTransactionOwners()).thenThrow(new RuntimeException("Mock"));

        ResponseEntity<List<String>> response = transactionsApiController.getTransactionOwners(httpServletRequestMock, httpServletResponseMock);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void getTransactionOwnersProvokeErrorTest2() throws SQLException {
        Mockito.when(transactionManager.getTransactionOwners()).thenReturn(null);

        ResponseEntity<List<String>> response = transactionsApiController.getTransactionOwners(httpServletRequestMock, httpServletResponseMock);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void patchTransactionByIdProvokeErrorTest() throws SQLException, IOException, SAXException {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn(MimeType.XBRL_GL);
        Mockito.when(transactionManager.getTransactionDocumentAsXbrlGl((String) Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new IllegalArgumentException("Mock"));
        ResponseEntity<Object> response2 = transactionsApiController.getTransactionById(httpServletRequestMock, httpServletResponseMock, null, null);
        Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE, response2.getStatusCode());
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
        InputStream resourceStream = ProvokeErrorsTest.class.getClassLoader().getResourceAsStream(resource);

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
