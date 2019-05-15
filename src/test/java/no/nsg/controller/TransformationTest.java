package no.nsg.controller;

import net.sf.saxon.s9api.*;
import no.nsg.repository.TransformationManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;


@RunWith(SpringRunner.class)
public class TransformationTest {

    TransformationManager transformationManager = new TransformationManager();

    @Test
    public void finvoiceTransformTest() throws SaxonApiException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        transformationManager.transform(getClass().getClassLoader().getResourceAsStream("openAPI/examples/Finvoice.xml"),
                                        TransformationManager.FINVOICE_TO_XBRL,
                                        baos);

        baos.toString("UTF-8");
    }

}
