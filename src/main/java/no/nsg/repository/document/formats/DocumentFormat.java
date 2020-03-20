package no.nsg.repository.document.formats;

import org.w3c.dom.Document;

import java.time.LocalDateTime;


public abstract class DocumentFormat {

    public enum Format {
        CAMT_053_001_08,
        FINVOICE,
        FINVOICE_INVOICE,
        FINVOICE_PURCHASE_INVOICE,
        FINVOICE_SALES_INVOICE,
        FINVOICE_RECEIPT,
        FINVOICE_PURCHASE_RECEIPT,
        FINVOICE_SALES_RECEIPT,
        UBL_2_1_PURCHASE_INVOICE,
        UBL_2_1_SALES_INVOICE,
        UBL_2_1_SALES_ORDER,
        UBL_2_1,
        UBL_2_1_PURCHASE_ORDER,
        XBRL_GL,
        XBRL_GL_TO_SAF_T,
        OTHER
    }

    public abstract String getDocumentSupplier(final Document parsedDocument);
    public abstract String getDocumentCustomer(final Document parsedDocument);
    public abstract LocalDateTime getTransactionTime(final Document parsedDocument);

    public static boolean isInvoice(final DocumentFormat.Format format) {
        return (format == Format.FINVOICE_INVOICE ||
                format == Format.FINVOICE_PURCHASE_INVOICE ||
                format == Format.FINVOICE_SALES_INVOICE ||
                format == Format.UBL_2_1_PURCHASE_INVOICE ||
                format == Format.UBL_2_1_SALES_INVOICE ||
                format == Format.UBL_2_1);
    }

    public static boolean isOrder(final DocumentFormat.Format format) {
        return (format == Format.UBL_2_1_SALES_ORDER ||
                format == Format.UBL_2_1 ||
                format == Format.UBL_2_1_PURCHASE_ORDER);
    }

}
