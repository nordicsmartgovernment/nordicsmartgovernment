package no.nsg.repository;

import net.sf.saxon.s9api.*;
import org.springframework.stereotype.Component;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


@Component
public class TransformationManager {

    public static final String FINVOICE_TO_XBRL = "xslt/finvoice_to_xbrl.xslt";
    public static final String UBL_TO_XBRL      = "xslt/ubl_2_1_to_xbrl.xslt";

    private Map<String, Xslt30Transformer> xsltCache = new HashMap<>();
    private static Processor processor = null;
    private static XsltCompiler compiler = null;


    private Xslt30Transformer getStylesheetExecutable(final String xslt) throws SaxonApiException {
        if (processor == null) {
            processor = new Processor(false);
            compiler = processor.newXsltCompiler();
        }

        if (!xsltCache.containsKey(xslt)) {
            InputStream xsltStream = getClass().getClassLoader().getResourceAsStream("xslt/finvoice_to_xbrl.xslt");
            if (xsltStream == null) {
                throw new IllegalArgumentException("xslt '"+xslt+"' not found");
            }
            xsltCache.put(xslt, compiler.compile(new StreamSource(xsltStream)).load30());
        }

        return xsltCache.get(xslt);
    }

    public void transform(final InputStream xmlStream, final String xslt, final OutputStream outputStream) throws SaxonApiException {
        Xslt30Transformer transformer = getStylesheetExecutable(xslt);
        Source source = new StreamSource(xmlStream);
        Serializer destination = processor.newSerializer(outputStream);
        transformer.transform(source, destination);
    }

}
