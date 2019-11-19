package no.nsg.controller;

import no.nsg.repository.ConnectionManager;
import no.nsg.spring.TestPrincipal;
import no.nsg.testcategories.ServiceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;


@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {InvoicesApiControllerTest.Initializer.class})
@Category(ServiceTest.class)
public class InvoicesApiControllerTest {

    @Mock
    HttpServletRequest httpServletRequestMock;

    @Autowired
    InvoicesApiControllerImpl invoicesApiController;

    @Autowired
    ConnectionManager connectionManager;

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
        }
    }

    @Before
    public void before() throws IOException {
        connectionManager.waitUntilSyntheticDataIsImported();
    }

    @Test
    public void happyDay()
    {
        Assert.assertTrue(true);
    }

    @Test
    public void createFinvoiceTest() throws IOException, NoSuchAlgorithmException {
        Principal principal = new TestPrincipal("");

        String original = resourceAsString("finvoice/Finvoice.xml", StandardCharsets.UTF_8);
        String originalChecksum = sha256Checksum(original.getBytes(StandardCharsets.UTF_8));

        ResponseEntity<Void> createResponse = invoicesApiController.createInvoice(principal, httpServletRequestMock, original);
        Assert.assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        URI location = createResponse.getHeaders().getLocation();
        String[] paths = location.getPath().split("/");
        String createdId = paths[paths.length-1];

        ResponseEntity<Object> response2 = invoicesApiController.getInvoiceById(principal, httpServletRequestMock, createdId);
        Assert.assertTrue(response2.getStatusCode() == HttpStatus.OK);
        InvoicesApiControllerImpl.Invoice returnedInvoice = (InvoicesApiControllerImpl.Invoice) response2.getBody();
        String returnedInvoiceChecksum = sha256Checksum(returnedInvoice.original);
        Assert.assertEquals(originalChecksum, returnedInvoiceChecksum);
    }

    @Test
    public void createInvoiceTest() throws IOException {
        ResponseEntity<Void> response = invoicesApiController.createInvoice(new TestPrincipal("983294"), httpServletRequestMock, resourceAsString("ubl/Invoice_base-example.xml", StandardCharsets.UTF_8));
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void getInvoicesTest() {
        ResponseEntity<List<Object>> response = invoicesApiController.getInvoices(new TestPrincipal(""), httpServletRequestMock);
        Assert.assertTrue(response.getStatusCode()==HttpStatus.OK || response.getStatusCode()==HttpStatus.NO_CONTENT);
    }

    @Test
    public void getInvoiceByIdTest() throws IOException {
        Principal principal = new TestPrincipal("123456785");

        ResponseEntity<Void> createResponse = invoicesApiController.createInvoice(principal, httpServletRequestMock, resourceAsString("ubl/ehf-2-faktura-1.xml", StandardCharsets.UTF_8));
        Assert.assertTrue(createResponse.getStatusCode() == HttpStatus.CREATED);
        URI location = createResponse.getHeaders().getLocation();
        String[] paths = location.getPath().split("/");
        String createdId = paths[paths.length-1];

        ResponseEntity<Object> response = invoicesApiController.getInvoiceById(principal, httpServletRequestMock, createdId);
        Assert.assertTrue(response.getStatusCode() == HttpStatus.OK);

        InvoicesApiControllerImpl.Invoice invoice = (InvoicesApiControllerImpl.Invoice) response.getBody();
        Assert.assertEquals(createdId, invoice.documentid);
    }

    private static String resourceAsString(final String resource, final Charset charset) throws IOException {
        InputStream resourceStream = InvoicesApiControllerTest.class.getClassLoader().getResourceAsStream(resource);

        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceStream, charset))) {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private String sha256Checksum(final byte[] content) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(content);

        StringBuffer sb = new StringBuffer();
        for (int i=0; i<hash.length; i++) {
            sb.append(String.format("%02X", hash[i]));
        }
        return sb.toString();
    }
}
