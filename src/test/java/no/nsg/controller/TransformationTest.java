package no.nsg.controller;

import net.sf.saxon.s9api.*;
import no.nsg.repository.TransformationManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


@RunWith(SpringRunner.class)
public class TransformationTest {

    TransformationManager transformationManager = new TransformationManager();

    private InputStream getResourceAsStream(final String resource) {
        return getClass().getClassLoader().getResourceAsStream(resource);
    }

    @Test
    public void finvoiceHappydayTransformTest() throws SaxonApiException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformationManager.transform(getResourceAsStream("finvoice/Finvoice.xml"), TransformationManager.FINVOICE_TO_XBRL, baos);
        baos.toString("UTF-8");
    }

    @Test
    public void finvoiceTransformTest() throws SaxonApiException, UnsupportedEncodingException {
        transformationManager.transform(getResourceAsStream("finvoice/Finvoice.xml"), TransformationManager.FINVOICE_TO_XBRL, new ByteArrayOutputStream());
        transformationManager.transform(getResourceAsStream("finvoice/finvoice 75 myynti.xml"), TransformationManager.FINVOICE_TO_XBRL, new ByteArrayOutputStream());
        transformationManager.transform(getResourceAsStream("finvoice/finvoice 76 myynti.xml"), TransformationManager.FINVOICE_TO_XBRL, new ByteArrayOutputStream());
        transformationManager.transform(getResourceAsStream("finvoice/finvoice 77 myynti.xml"), TransformationManager.FINVOICE_TO_XBRL, new ByteArrayOutputStream());
        transformationManager.transform(getResourceAsStream("finvoice/finvoice 78 myynti.xml"), TransformationManager.FINVOICE_TO_XBRL, new ByteArrayOutputStream());
    }
}