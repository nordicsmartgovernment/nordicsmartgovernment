package no.nsg.repository.document;

import no.nsg.repository.DocumentType;
import no.nsg.repository.document.formats.*;

import static no.nsg.repository.document.formats.DocumentFormat.Format.*;


public class FormatFactory {

    public static DocumentFormat create(DocumentFormat.Format format) {
        switch (format) {
            case CAMT_053_001_08:
                return new CamtFormat();

            case FINVOICE_PURCHASE_INVOICE:
            case FINVOICE_PURCHASE_RECEIPT:
            case FINVOICE_SALES_INVOICE:
            case FINVOICE_SALES_RECEIPT:
                return new FinvoiceFormat();

            case UBL_2_1_SALES_INVOICE:
            case UBL_2_1_PURCHASE_INVOICE:
                return new UBLInvoiceFormat();

            case UBL_2_1_SALES_ORDER:
            case UBL_2_1_PURCHASE_ORDER:
                return new UBLOrderFormat();

            default:
                return new UnknownDocumentFormat();
        }
    }

    public static DocumentFormat.Format guessFormat(final DocumentType.Type documentType, final String document) {
        if (document.contains("<Finvoice ")) {
            if (document.contains("<InvoiceTypeCode>REC")) {
                return mapToPurchaseOrSale(documentType, FINVOICE_SALES_RECEIPT, FINVOICE_PURCHASE_RECEIPT);
            } else {
                return mapToPurchaseOrSale(documentType, FINVOICE_SALES_INVOICE, FINVOICE_PURCHASE_INVOICE);
            }
        } else if (document.contains("xmlns=\"urn:iso:std:iso:20022:tech:xsd:camt.053.")) {
            return DocumentFormat.Format.CAMT_053_001_08;
        } else if (document.contains("xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\"") || document.contains("<Invoice ")) {
            return mapToPurchaseOrSale(documentType, UBL_2_1_SALES_INVOICE, UBL_2_1_PURCHASE_INVOICE);
        } else if (document.contains("xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Order-2\"") || document.contains("<Order ")) {
            return mapToPurchaseOrSale(documentType, UBL_2_1_SALES_ORDER, UBL_2_1_PURCHASE_ORDER);
        } else if (document.contains("<xbrli:xbrl")) {
            return DocumentFormat.Format.XBRL_GL;
        } else {
            return DocumentFormat.Format.OTHER;
        }

    }

    private static DocumentFormat.Format mapToPurchaseOrSale(DocumentType.Type documentType, DocumentFormat.Format salesFormat, DocumentFormat.Format purchaseFormat) {
        if (DocumentType.Type.SALES_RECEIPT == documentType) {
            return salesFormat;
        } else if (DocumentType.Type.PURCHASE_RECEIPT == documentType) {
            return purchaseFormat;
        }
        return OTHER;
    }

    public static boolean isCompatible(final DocumentType.Type documentType, final DocumentFormat.Format documentFormat) {
        if (DocumentType.isInvoice(documentType)) {
            return DocumentFormat.isInvoice(documentFormat);
        } else if (DocumentType.isOrder(documentType)) {
            return DocumentFormat.isOrder(documentFormat);
        } else {
            return (!DocumentFormat.isInvoice(documentFormat) && !DocumentFormat.isOrder(documentFormat));
        }
    }

}
