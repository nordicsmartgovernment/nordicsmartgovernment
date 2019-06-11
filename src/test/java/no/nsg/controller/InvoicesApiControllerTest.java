package no.nsg.controller;

import no.nsg.testcategories.ServiceTest;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
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
import java.sql.SQLException;
import java.util.List;


@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {InvoicesApiControllerTest.Initializer.class})
@Category(ServiceTest.class)
public class InvoicesApiControllerTest {
    private static Logger LOGGER = LoggerFactory.getLogger(InvoicesApiControllerTest.class);


    static final String EXAMPLE_INVOICE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
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
                                          "</Invoice>";

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
    public void createInvoiceTest() throws SQLException {
        ResponseEntity<Void> response = invoicesApiController.createInvoice(httpServletRequestMock, EXAMPLE_INVOICE);
        Assert.assertNotEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
        Assert.assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void getInvoicesTest() {
        ResponseEntity<List<Object>> response = invoicesApiController.getInvoices(httpServletRequestMock);
        Assert.assertNotEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
        Assert.assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}
