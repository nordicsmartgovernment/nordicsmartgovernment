package no.nsg.controller;

import no.nsg.repository.ConnectionManager;
import no.nsg.spring.TestPrincipal;
import no.nsg.testcategories.ServiceTest;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;


@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {TransactionsApiControllerTest.Initializer.class})
@Category(ServiceTest.class)
public class TransactionsApiControllerTest {
    private static Logger LOGGER = LoggerFactory.getLogger(TransactionsApiControllerTest.class);

    @Mock
    HttpServletRequest httpServletRequestMock;

    @Autowired
    TransactionsApiControllerImpl transactionsApiController;

    @Autowired
    InvoicesApiControllerImpl invoicesApiController;

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
        ResponseEntity<List<String>> response = transactionsApiController.getTransactions(new TestPrincipal(""), httpServletRequestMock, null, null, null);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        List<String> responseBody = response.getBody();
        Assert.assertNotNull(responseBody);
    }

    @Test
    public void getOrganizationTransactionsTest() {
        ResponseEntity<List<String>> response = transactionsApiController.getTransactions(new TestPrincipal(""), httpServletRequestMock, null, "20202020", null);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        List<String> responseBody = response.getBody();
        Assert.assertNotNull(responseBody);
    }

    @Test
    public void getInboundTransactionsTest() {
        ResponseEntity<List<String>> response = transactionsApiController.getTransactions(new TestPrincipal(""), httpServletRequestMock, null, null, "incoming");
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        List<String> responseBody = response.getBody();
        Assert.assertNotNull(responseBody);
    }

    @Test
    public void getOutboundTransactionsTest() {
        ResponseEntity<List<String>> response = transactionsApiController.getTransactions(new TestPrincipal(""), httpServletRequestMock, null, null, "outgoing");
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        List<String> responseBody = response.getBody();
        Assert.assertNotNull(responseBody);
    }

    @Test
    public void getOrganizationInboundTransactionsTest() {
        ResponseEntity<List<String>> response = transactionsApiController.getTransactions(new TestPrincipal(""), httpServletRequestMock, null, "20202020", "incoming");
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        List<String> responseBody = response.getBody();
        Assert.assertNotNull(responseBody);
    }

    @Test
    public void patchTransactionByIdTest() throws IOException {
        ResponseEntity<Void> createResponse = invoicesApiController.createInvoice(new TestPrincipal(""), httpServletRequestMock, resourceAsString("finvoice/finvoice 75 myynti.xml", StandardCharsets.UTF_8));
        Assert.assertTrue(createResponse.getStatusCode() == HttpStatus.CREATED);
        URI location = createResponse.getHeaders().getLocation();
        String[] paths = location.getPath().split("/");
        String createdId = paths[paths.length-1];

        String patchXml = "<diff xmlns:xbrli=\"http://www.xbrl.org/2003/instance\">\n" +
                            "<replace sel=\"xbrli:xbrl/xbrli:context/xbrli:entity/xbrli:identifier/text()\">Patched!</replace>\n" +
                          "</diff>";
        ResponseEntity<Object> response = transactionsApiController.patchTransactionById(new TestPrincipal(""), httpServletRequestMock, createdId, patchXml);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Object responseBody = response.getBody();
        Assert.assertNotNull(responseBody);
        String xbrl = (String)responseBody;
        Assert.assertTrue(xbrl.contains("Patched!"));
    }

    private void initializeInvoiceData() throws IOException {
        if (!hasInitializedInvoiceData) {
            hasInitializedInvoiceData = true;

            connectionManager.waitUntilSyntheticDataIsImported();

            invoicesApiController.createInvoice(new TestPrincipal(""), httpServletRequestMock, resourceAsString("finvoice/Finvoice.xml", StandardCharsets.UTF_8));
            invoicesApiController.createInvoice(new TestPrincipal(""), httpServletRequestMock, resourceAsString("finvoice/finvoice 75 myynti.xml", StandardCharsets.UTF_8));
            invoicesApiController.createInvoice(new TestPrincipal(""), httpServletRequestMock, resourceAsString("finvoice/finvoice 76 myynti.xml", StandardCharsets.UTF_8));
            invoicesApiController.createInvoice(new TestPrincipal(""), httpServletRequestMock, resourceAsString("finvoice/finvoice 77 myynti.xml", StandardCharsets.UTF_8));
            invoicesApiController.createInvoice(new TestPrincipal(""), httpServletRequestMock, resourceAsString("finvoice/finvoice 78 myynti.xml", StandardCharsets.UTF_8));
            invoicesApiController.createInvoice(new TestPrincipal("983294"), httpServletRequestMock, resourceAsString("ubl/Invoice_base-example.xml", StandardCharsets.UTF_8));
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
