package no.nsg.controller;

import net.sf.saxon.s9api.*;
import no.nsg.repository.TransformationManager;
import no.nsg.repository.document.formats.DocumentFormat;
import no.nsg.testcategories.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;


@RunWith(SpringRunner.class)
@Category(UnitTest.class)
public class TransformationTest {
    private static Logger LOGGER = LoggerFactory.getLogger(TransformationTest.class);

    TransformationManager transformationManager = new TransformationManager();

    private InputStream getResourceAsStream(final String resource) {
        return getClass().getClassLoader().getResourceAsStream(resource);
    }

    @Test
    public void finvoiceHappydayTransformTest() throws SaxonApiException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformationManager.transform(getResourceAsStream("finvoice/Finvoice.xml"), DocumentFormat.Format.FINVOICE, baos);
    }

    @Test
    public void finvoiceTransformTest() throws SaxonApiException, UnsupportedEncodingException {
        transformationManager.transform(getResourceAsStream("finvoice/Finvoice.xml"), DocumentFormat.Format.FINVOICE, new ByteArrayOutputStream());
        transformationManager.transform(getResourceAsStream("finvoice/finvoice 75 myynti.xml"), DocumentFormat.Format.FINVOICE, new ByteArrayOutputStream());
        transformationManager.transform(getResourceAsStream("finvoice/finvoice 76 myynti.xml"), DocumentFormat.Format.FINVOICE, new ByteArrayOutputStream());
        transformationManager.transform(getResourceAsStream("finvoice/finvoice 77 myynti.xml"), DocumentFormat.Format.FINVOICE, new ByteArrayOutputStream());
        transformationManager.transform(getResourceAsStream("finvoice/finvoice 78 myynti.xml"), DocumentFormat.Format.FINVOICE, new ByteArrayOutputStream());
    }

    @Test
    public void ublTransformTest() throws SaxonApiException, UnsupportedEncodingException {
        transformationManager.transform(getResourceAsStream("ubl/Invoice_base-example.xml"), DocumentFormat.Format.UBL_2_1_PURCHASE_INVOICE, new ByteArrayOutputStream());
        transformationManager.transform(getResourceAsStream("ubl/ehf-2-faktura-1.xml"), DocumentFormat.Format.UBL_2_1_PURCHASE_INVOICE, new ByteArrayOutputStream());
        transformationManager.transform(getResourceAsStream("ubl/ehf-3-faktura-1.xml"), DocumentFormat.Format.UBL_2_1_PURCHASE_INVOICE, new ByteArrayOutputStream());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformationManager.transform(getResourceAsStream("ubl/test_purchase_invoice_for_company_id_12345.xml"), DocumentFormat.Format.UBL_2_1_PURCHASE_INVOICE, baos);
        String result = baos.toString(StandardCharsets.UTF_8.name());
        Assert.assertTrue(result.contains("<gl-cor:identifierAuthorityCode "));
        Assert.assertTrue(result.contains("<gl-cor:documentNumber "));
    }
}
