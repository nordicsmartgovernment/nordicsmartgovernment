package no.nsg.repository;

import net.sf.saxon.Configuration;
import net.sf.saxon.s9api.*;
import no.nsg.repository.document.formats.DocumentFormat;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


@Component
public class TransformationManager {

    private static final String XSLT_BASE = "xslt/";


    public static String xsltFor(final DocumentFormat.Format documentFormat) {
        if (documentFormat == null) {
            return null;
        }

        switch (documentFormat) {
            default: return null;
            case CAMT_053_001_08:           return XSLT_BASE+"camt_053_001_08_to_xbrlgl.xslt";
            case FINVOICE_SALES_INVOICE:    return XSLT_BASE+"finvoice_xbrlgl_sales_invoice.xslt";
            case FINVOICE_PURCHASE_INVOICE: return XSLT_BASE+"finvoice_xbrlgl_purchase_invoice.xslt";
            case FINVOICE_SALES_RECEIPT:    return XSLT_BASE+"finvoice_xbrlgl_sales_receipt.xslt";
            case FINVOICE_PURCHASE_RECEIPT: return XSLT_BASE+"finvoice_xbrlgl_purchase_receipt.xslt";
            case FINVOICE_INVOICE:          return XSLT_BASE+"finvoice_to_xbrl.xslt";
            case FINVOICE_RECEIPT:          return XSLT_BASE+"finvoice_to_xbrl.xslt";
            case FINVOICE:                  return XSLT_BASE+"finvoice_to_xbrl.xslt";
            case UBL_2_1_SALES_INVOICE:     return XSLT_BASE+"ubl_2_1_sales_invoice_xbrl_gl.xslt";
            case UBL_2_1_PURCHASE_INVOICE:  return XSLT_BASE+"ubl_2_1_xbrl_gl.xslt";
            case UBL_2_1_SALES_ORDER:       return XSLT_BASE+"ubl_2_1_sales_order_xbrl_gl.xslt";
            case UBL_2_1_PURCHASE_ORDER:    return XSLT_BASE+"ubl_2_1_xbrl_gl_purchase_order.xslt";
            case UBL_2_1:                   return XSLT_BASE+"ubl_2_1_xbrl_gl.xslt";
            case XBRL_GL_TO_SAF_T:          return XSLT_BASE+"xbrl_gl_to_saf_t.xslt";
        }
    }

    public enum Direction {
        DOESNT_MATTER,
        DONT_KNOW,
        PURCHASE, //inbound
        SALES     //outbound
    }

    public static final String CAC_NS    = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
    public static final String CBC_NS    = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
    public static final String GL_COR_NS = "http://www.xbrl.org/int/gl/cor/2016-12-01";
    public static final String XBRLI_NS  = "http://www.xbrl.org/2003/instance";

    private static Map<String, Xslt30Transformer> xsltCache = null;
    private static Processor processor = null;
    private static XsltCompiler compiler = null;


    private static Xslt30Transformer getStylesheetExecutable(final String xslt) throws SaxonApiException {
        if (xsltCache == null) {
            xsltCache = new HashMap<>();
        }

        if (processor == null) {
            processor = new Processor(false);
            Configuration config = processor.getUnderlyingConfiguration();
            config.setURIResolver((href, base)
                                    -> new SAXSource(new InputSource(TransformationManager.class.getClassLoader().getResourceAsStream(XSLT_BASE+href))));
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

    public static void transform(final InputStream xmlStream, final DocumentFormat.Format format, final OutputStream outputStream) throws SaxonApiException {
        transform(xmlStream, xsltFor(format), outputStream);
    }

    public static synchronized void transform(final InputStream xmlStream, final String xslt, final OutputStream outputStream) throws SaxonApiException {
        if (xslt==null || xslt.isEmpty()) {
            throw new RuntimeException("Uninitialized/unknown XSLT format");
        }

        Xslt30Transformer transformer = getStylesheetExecutable(xslt);
        Source source = new StreamSource(xmlStream);
        Serializer destination = processor.newSerializer(outputStream);
        transformer.transform(source, destination);
    }

}
