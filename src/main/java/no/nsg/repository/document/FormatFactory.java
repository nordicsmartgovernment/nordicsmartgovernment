package no.nsg.repository.document;

import no.nsg.repository.DocumentType;
import no.nsg.repository.document.formats.DocumentFormat;
import no.nsg.repository.document.formats.FinvoiceFormat;
import no.nsg.repository.document.formats.UBLFormat;
import no.nsg.repository.document.formats.UnknownDocumentFormat;


public class FormatFactory {

    public static DocumentFormat create(DocumentFormat.Format format) {
        switch (format) {
            case CAMT_053_001_08:           return new UnknownDocumentFormat();
            case FINVOICE:                  return new FinvoiceFormat();
            case FINVOICE_PURCHASE_INVOICE: return new FinvoiceFormat();
            case FINVOICE_PURCHASE_RECEIPT: return new FinvoiceFormat();
            case FINVOICE_SALES_INVOICE:    return new FinvoiceFormat();
            case FINVOICE_SALES_RECEIPT:    return new FinvoiceFormat();
            case UBL_2_1_PURCHASE_INVOICE:  return new UBLFormat();
            case UBL_2_1_SALES_INVOICE:     return new UBLFormat();
            case UBL_2_1_SALES_ORDER:       return new UBLFormat();
            case UBL_2_1:                   return new UBLFormat();
            case UBL_2_1_PURCHASE_ORDER:    return new UBLFormat();
            default:                        return new UnknownDocumentFormat();
        }
    }

    public static DocumentFormat.Format guessFormat(final DocumentType.Type documentType, final String document) {
        if (document.contains("<Finvoice ")) {
            return DocumentFormat.Format.FINVOICE;
        } else if (document.contains("xmlns=\"urn:iso:std:iso:20022:tech:xsd:camt.053.")) {
            return DocumentFormat.Format.CAMT_053_001_08;
        } else if (document.contains("xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\"") || document.contains("<Invoice ")) {
            return DocumentFormat.Format.UBL_2_1;
        } else {
            return DocumentFormat.Format.OTHER;
        }
    }

}
