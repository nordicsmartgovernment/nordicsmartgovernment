package no.nsg.controller;

import no.nsg.testcategories.ServiceTest;
import org.junit.Assert;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
                    "postgres.nsg.host=" + postgreSQLContainer.getContainerIpAddress()+":"+postgreSQLContainer.getMappedPort(5432),
                    "postgres.nsg.db=" + postgreSQLContainer.getDatabaseName(),
                    "postgres.nsg.dbo_user=" + postgreSQLContainer.getUsername(),
                    "postgres.nsg.dbo_password=" + postgreSQLContainer.getPassword(),
                    "postgres.nsg.user=" + postgreSQLContainer.getUsername(),
                    "postgres.nsg.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Test
    public void happyDay()
    {
        Assert.assertTrue(true);
    }

    @Test
    public void createFinvoiceTest() throws IOException {
        ResponseEntity<Void> response = invoicesApiController.createInvoice(httpServletRequestMock, resourceAsString("finvoice/Finvoice.xml", StandardCharsets.UTF_8));
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void createInvoiceTest() throws IOException {
        ResponseEntity<Void> response = invoicesApiController.createInvoice(httpServletRequestMock, resourceAsString("ubl/Invoice_base-example.xml", StandardCharsets.UTF_8));
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void getInvoicesTest() {
        ResponseEntity<List<Object>> response = invoicesApiController.getInvoices(httpServletRequestMock);
        Assert.assertTrue(response.getStatusCode()==HttpStatus.OK || response.getStatusCode()==HttpStatus.NO_CONTENT);
    }

    @Test
    public void getInvoiceByIdTest() throws IOException {
        ResponseEntity<Object> response = invoicesApiController.getInvoiceById(httpServletRequestMock, "TOSL108");
        Assert.assertTrue(response.getStatusCode() == HttpStatus.NO_CONTENT);

        invoicesApiController.createInvoice(httpServletRequestMock, resourceAsString("ubl/ehf-2-faktura-1.xml", StandardCharsets.UTF_8));
        response = invoicesApiController.getInvoiceById(httpServletRequestMock, "TOSL108");
        Assert.assertTrue(response.getStatusCode() == HttpStatus.OK);

        InvoicesApiControllerImpl.Invoice invoice = (InvoicesApiControllerImpl.Invoice) response.getBody();
        Assert.assertEquals("TOSL108", invoice.documentid);
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
}
