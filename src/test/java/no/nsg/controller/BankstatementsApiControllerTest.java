package no.nsg.controller;

import no.nsg.repository.ConnectionManager;
import no.nsg.repository.MimeType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = {BankstatementsApiControllerTest.Initializer.class})
@Tag("ServiceTest")
@Testcontainers
public class BankstatementsApiControllerTest {

    @Mock
    HttpServletRequest httpServletRequestMock;

    @Mock
    HttpServletResponse httpServletResponseMock;

    @Autowired
    DocumentApi bankstatementsApiController;

    @Autowired
    ConnectionManager connectionManager;

    @Container
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

    @BeforeEach
    public void before() throws IOException {
        connectionManager.waitUntilSyntheticDataIsImported();
    }

    @Test
    public void happyDay()
    {
        Assertions.assertTrue(true);
    }

    @Disabled //Resource is imported in getBankstatementByIdTest below
    @Test
    public void createBankstatementTest() throws IOException {
        final String companyId = "todo";
        ResponseEntity<Void> createResponse = bankstatementsApiController.createDocument(httpServletRequestMock, httpServletResponseMock, companyId, resourceAsString("camt/NSG.2.xml", StandardCharsets.UTF_8));
        Assertions.assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
    }

    @Test
    public void createAnotherBankstatementTest() throws IOException {
        final String companyId = "todo";

        Mockito.when(httpServletRequestMock.getContentType()).thenReturn(MimeType.NSG_BANKSTATEMENT);
        ResponseEntity<Void> response = bankstatementsApiController.createDocument(httpServletRequestMock, httpServletResponseMock, companyId, resourceAsString("camt/bank_statement1.xml", StandardCharsets.UTF_8));
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void createInvalidBankstatementTest() throws IOException {
        final String companyId = "983294";

        Mockito.when(httpServletRequestMock.getContentType()).thenReturn(MimeType.NSG_BANKSTATEMENT);
        ResponseEntity<Void> response = bankstatementsApiController.createDocument(httpServletRequestMock, httpServletResponseMock, companyId, resourceAsString("ubl/Invoice_base-example.xml", StandardCharsets.UTF_8));
        Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
    }

    @Test
    public void getBankstatementsTest() {
        final String companyId = "todo";
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn(MimeType.JSON);
        ResponseEntity<Object> response = bankstatementsApiController.getDocuments(httpServletRequestMock, httpServletResponseMock, companyId, MimeType.NSG_BANKSTATEMENT);
        Assertions.assertTrue(response.getStatusCode()==HttpStatus.OK || response.getStatusCode()==HttpStatus.NO_CONTENT);
    }

    @Test
    public void getBankstatementByIdTest() throws IOException {
        final String companyId = "todo";

        Mockito.when(httpServletRequestMock.getContentType()).thenReturn(MimeType.NSG_BANKSTATEMENT);
        ResponseEntity<Void> createResponse = bankstatementsApiController.createDocument(httpServletRequestMock, httpServletResponseMock, companyId, resourceAsString("camt/NSG.1.xml", StandardCharsets.UTF_8));
        Assertions.assertSame(createResponse.getStatusCode(), HttpStatus.CREATED);
        URI location = createResponse.getHeaders().getLocation();
        String[] paths = location.getPath().split("/");
        String createdTransactionId = paths[paths.length-2];
        String createdDocumentId = paths[paths.length-1];

        ResponseEntity<Object> response = bankstatementsApiController.getDocumentById(httpServletRequestMock, httpServletResponseMock, companyId, createdTransactionId, createdDocumentId);
        Assertions.assertSame(response.getStatusCode(), HttpStatus.OK);

        DocumentApi.Document bankstatement = (DocumentApi.Document) response.getBody();
        Assertions.assertEquals(createdDocumentId, bankstatement.documentid);

        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn(MimeType.JSON);
        ResponseEntity<Object> responseDocumentsByTransaction = bankstatementsApiController.getDocumentsByTransactionId(httpServletRequestMock, httpServletResponseMock, companyId, createdTransactionId);
        Assertions.assertSame(responseDocumentsByTransaction.getStatusCode(), HttpStatus.OK);
        ArrayList<String> documentArray = (ArrayList<String>) responseDocumentsByTransaction.getBody();
        Assertions.assertEquals(1, documentArray.size());
        Assertions.assertEquals(createdDocumentId, documentArray.get(0));
    }

    private static String resourceAsString(final String resource, final Charset charset) throws IOException {
        InputStream resourceStream = BankstatementsApiControllerTest.class.getClassLoader().getResourceAsStream(resource);

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
