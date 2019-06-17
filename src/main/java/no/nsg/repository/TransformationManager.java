package no.nsg.repository;

import net.sf.saxon.s9api.*;
import no.nsg.repository.dbo.DocumentDbo;
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
    public static final String UBL_TO_XBRL_GL   = "xslt/ubl_2_1_to_xbrl_gl.xslt";

    private static Map<String, Xslt30Transformer> xsltCache = null;
    private static Processor processor = null;
    private static XsltCompiler compiler = null;


    private static Xslt30Transformer getStylesheetExecutable(final String xslt) throws SaxonApiException {
        if (xsltCache == null) {
            xsltCache = new HashMap<>();
        }

        if (processor == null) {
            processor = new Processor(false);
            compiler = processor.newXsltCompiler();
        }

        if (!xsltCache.containsKey(xslt)) {
            InputStream xsltStream = TransformationManager.class.getClassLoader().getResourceAsStream(xslt);
            if (xsltStream == null) {
                throw new IllegalArgumentException("xslt '"+xslt+"' not found");
            }
            xsltCache.put(xslt, compiler.compile(new StreamSource(xsltStream)).load30());
        }

        return xsltCache.get(xslt);
    }

    public static void transform(final InputStream xmlStream, final DocumentDbo.DocumentFormat format, final OutputStream outputStream) throws SaxonApiException {
        String xsltFile = null;
        switch (format) {
            case UML:
                xsltFile = UBL_TO_XBRL_GL;
                break;

            case FINVOICE:
                xsltFile = FINVOICE_TO_XBRL;
                break;

            case UNKNOWN:
            default:
                break;
        }
        transform(xmlStream, xsltFile, outputStream);
    }

    public static void transform(final InputStream xmlStream, final String xslt, final OutputStream outputStream) throws SaxonApiException {
        Xslt30Transformer transformer = getStylesheetExecutable(xslt);
        Source source = new StreamSource(xmlStream);
        Serializer destination = processor.newSerializer(outputStream);
        transformer.transform(source, destination);
    }

}
