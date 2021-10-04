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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


@Tag("ServiceTest")
public class OrderApiControllerTest extends EmbeddedPostgresSetup {

    @Mock
    HttpServletRequest httpServletRequestMock;

    @Mock
    HttpServletResponse httpServletResponseMock;

    @Autowired
    DocumentApi orderApiController;

    @Autowired
    ConnectionManager connectionManager;

    @BeforeEach
    public void before() throws IOException {
        connectionManager.waitUntilSyntheticDataIsImported();
    }

    @Test
    public void happyDay()
    {
        Assertions.assertTrue(true);
    }

    @Test
    public void createPeppolSalesOrderTest() throws IOException {
        final String companyId = "175 269 2355";
        Mockito.when(httpServletRequestMock.getContentType()).thenReturn(MimeType.NSG_SALES_ORDER);
        ResponseEntity<Void> response = orderApiController.createDocument(httpServletRequestMock, httpServletResponseMock, companyId, resourceAsString("ubl/UBL-Order-2.0-Example.xml", StandardCharsets.UTF_8));
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void createPeppolPurchaseOrderTest() throws IOException {
        final String companyId = "not_set";
        Mockito.when(httpServletRequestMock.getContentType()).thenReturn(MimeType.NSG_PURCHASE_ORDER);
        ResponseEntity<Void> response = orderApiController.createDocument(httpServletRequestMock, httpServletResponseMock, companyId, resourceAsString("ubl/UBL-Order-2.0-Example.xml", StandardCharsets.UTF_8));
        Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
    }

    private static String resourceAsString(final String resource, final Charset charset) throws IOException {
        InputStream resourceStream = OrderApiControllerTest.class.getClassLoader().getResourceAsStream(resource);

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
