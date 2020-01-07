package no.nsg.repository.document.formats;

import org.w3c.dom.Document;


public interface DocumentFormat {

    enum Format {
        CAMT_053_001_08,
        FINVOICE,
        FINVOICE_PURCHASE_INVOICE,
        FINVOICE_PURCHASE_RECEIPT,
        FINVOICE_SALES_INVOICE,
        FINVOICE_SALES_RECEIPT,
        UBL_2_1_PURCHASE_INVOICE,
        UBL_2_1_SALES_INVOICE,
        UBL_2_1_SALES_ORDER,
        UBL_2_1,
        UBL_2_1_PURCHASE_ORDER,
        XBRL_GL_TO_SAF_T,
        OTHER
    }

    String getDocumentSupplier(final Document parsedDocument);
    String getDocumentCustomer(final Document parsedDocument);

}
