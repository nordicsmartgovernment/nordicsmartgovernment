package no.nsg.controller;

import no.nsg.repository.ConnectionManager;
import no.nsg.testcategories.ServiceTest;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {TransactionsApiControllerTest.Initializer.class})
@Category(ServiceTest.class)
public class TransactionsApiControllerTest {
    private static Logger LOGGER = LoggerFactory.getLogger(TransactionsApiControllerTest.class);

    @Mock
    HttpServletRequest httpServletRequestMock;

    @Mock
    HttpServletResponse httpServletResponseMock;

    @Autowired
    TransactionsApiControllerImpl transactionsApiController;

    @Autowired
    DocumentApiControllerImpl documentApiController;

    @Autowired
    ConnectionManager connectionManager;

    private boolean hasInitializedInvoiceData = false;


    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:latest")
            .withDatabaseName("integration-tests-db")
            .withUsername("testuser")
            .withPassword("testpassword");

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "postgres.nsg.db_url=" + postgreSQLContainer.getJdbcUrl(),
                    "postgres.nsg.dbo_user=" + postgreSQLContainer.getUsername(),
                    "postgres.nsg.dbo_password=" + postgreSQLContainer.getPassword(),
                    "postgres.nsg.user=" + postgreSQLContainer.getUsername(),
                    "postgres.nsg.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());

            LOGGER.info("JDBC: " + postgreSQLContainer.getJdbcUrl());
        }
    }

    @Before
    public void before() throws IOException {
        initializeInvoiceData();
    }

    @Test
    public void happyDay()
    {
        Assert.assertTrue(true);
    }

    @Test
    public void getTransactionsTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/json");
        ResponseEntity<Object> response = transactionsApiController.getTransactions(httpServletRequestMock, httpServletResponseMock, null, null, null);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Object responseBody = response.getBody();
        Assert.assertNotNull(responseBody);
    }

    @Test
    public void getOrganizationTransactionsTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/json");
        ResponseEntity<Object> response = transactionsApiController.getTransactions(httpServletRequestMock, httpServletResponseMock, "20202020", null, null);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Object responseBody = response.getBody();
        Assert.assertNotNull(responseBody);
    }

    @Test
    public void getOrganizationTransactionsAsXbrlTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xbrl-instance+xml");
        ResponseEntity<Object> response = transactionsApiController.getTransactions(httpServletRequestMock, httpServletResponseMock, "20202020", null, null);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Object responseBody = response.getBody();
        Assert.assertNotNull(responseBody);
    }

    @Test
    public void getInboundTransactionsTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/json");
        ResponseEntity<Object> response = transactionsApiController.getTransactions(httpServletRequestMock, httpServletResponseMock, null, null, "incoming");
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Object responseBody = response.getBody();
        Assert.assertNotNull(responseBody);
    }

    @Test
    public void getOutboundTransactionsTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/json");
        ResponseEntity<Object> response = transactionsApiController.getTransactions(httpServletRequestMock, httpServletResponseMock, null, null, "outgoing");
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Object responseBody = response.getBody();
        Assert.assertNotNull(responseBody);
    }

    @Test
    public void getOrganizationInboundTransactionsTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/json");
        ResponseEntity<Object> response = transactionsApiController.getTransactions(httpServletRequestMock, httpServletResponseMock, "20202020", null, "incoming");
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Object responseBody = response.getBody();
        Assert.assertNotNull(responseBody);
    }

    @Test
    public void patchTransactionByIdTest() throws IOException {
        final String companyId = "2372513-5";
        Mockito.when(httpServletRequestMock.getContentType()).thenReturn("application/vnd.nordicsmartgovernment.sales-invoice");
        ResponseEntity<Void> createResponse = documentApiController.createDocument(httpServletRequestMock, httpServletResponseMock, companyId, resourceAsString("finvoice/finvoice 75 myynti.xml", StandardCharsets.UTF_8));
        Assert.assertTrue(createResponse.getStatusCode() == HttpStatus.CREATED);
        URI location = createResponse.getHeaders().getLocation();
        String[] paths = location.getPath().split("/");
        String createdId = paths[paths.length-1];

        String xbrlDocument = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                              "<xbrli:xbrl xmlns:xbrli=\"http://www.xbrl.org/2003/instance\"/>";
        ResponseEntity<Void> response = transactionsApiController.putTransactionById(httpServletRequestMock, httpServletResponseMock, companyId, createdId, xbrlDocument);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void initializeInvoiceData() throws IOException {
        final String customerId = "983294";
        Mockito.when(httpServletRequestMock.getContentType()).thenReturn("application/vnd.nordicsmartgovernment.sales-invoice");

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
