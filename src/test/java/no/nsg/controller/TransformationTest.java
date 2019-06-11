package no.nsg.controller;

import net.sf.saxon.s9api.*;
import no.nsg.repository.TransformationManager;
import no.nsg.testcategories.UnitTest;
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
        transformationManager.transform(getResourceAsStream("finvoice/Finvoice.xml"), TransformationManager.FINVOICE_TO_XBRL, baos);
        LOGGER.info(baos.toString(StandardCharsets.UTF_8.name()));
    }

    @Test
    public void finvoiceTransformTest() throws SaxonApiException, UnsupportedEncodingException {
        transformationManager.transform(getResourceAsStream("finvoice/Finvoice.xml"), TransformationManager.FINVOICE_TO_XBRL, new ByteArrayOutputStream());
        transformationManager.transform(getResourceAsStream("finvoice/finvoice 75 myynti.xml"), TransformationManager.FINVOICE_TO_XBRL, new ByteArrayOutputStream());
        transformationManager.transform(getResourceAsStream("finvoice/finvoice 76 myynti.xml"), TransformationManager.FINVOICE_TO_XBRL, new ByteArrayOutputStream());
        transformationManager.transform(getResourceAsStream("finvoice/finvoice 77 myynti.xml"), TransformationManager.FINVOICE_TO_XBRL, new ByteArrayOutputStream());
        transformationManager.transform(getResourceAsStream("finvoice/finvoice 78 myynti.xml"), TransformationManager.FINVOICE_TO_XBRL, new ByteArrayOutputStream());
    }

    @Test
    public void ublTransformTest() throws SaxonApiException, UnsupportedEncodingException {
        transformationManager.transform(getResourceAsStream("ubl/Invoice_base-example.xml"), TransformationManager.UBL_TO_XBRL, new ByteArrayOutputStream());
        transformationManager.transform(getResourceAsStream("ubl/ehf-2-faktura-1.xml"), TransformationManager.UBL_TO_XBRL, new ByteArrayOutputStream());
        transformationManager.transform(getResourceAsStream("ubl/ehf-3-faktura-1.xml"), TransformationManager.UBL_TO_XBRL, new ByteArrayOutputStream());
    }
}
