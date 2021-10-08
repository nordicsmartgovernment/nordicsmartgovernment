package no.nsg.repository.document;

import no.nsg.repository.DocumentType;
import no.nsg.repository.document.formats.*;

import static no.nsg.repository.document.formats.DocumentFormat.Format.*;


public class FormatFactory {

    public static DocumentFormat create(DocumentFormat.Format format) {
        DocumentFormat documentFormat = new UnknownDocumentFormat();
        switch (format) {
            case CAMT_053_001_08: {
                documentFormat = new CamtFormat();
                break;
            }

            case FINVOICE_PURCHASE_INVOICE:
            case FINVOICE_PURCHASE_RECEIPT:
            case FINVOICE_SALES_INVOICE:
            case FINVOICE_SALES_RECEIPT: {
                documentFormat = new FinvoiceFormat();
                break;
            }

            case UBL_2_1_SALES_INVOICE:
            case UBL_2_1_PURCHASE_INVOICE: {
                documentFormat = new UBLInvoiceFormat();
                break;
            }

            case UBL_2_1_SALES_ORDER:
            case UBL_2_1_PURCHASE_ORDER: {
                documentFormat = new UBLOrderFormat();
                break;
            }
        }
        return documentFormat;
    }

    public static DocumentFormat.Format guessFormat(final DocumentType.Type documentType, final String document) {
        DocumentFormat.Format format = null;
        if (document.contains("<Finvoice ")) {
            if (document.contains("<InvoiceTypeCode>REC")) {
                if (DocumentType.Type.SALES_RECEIPT == documentType) {
                    format = FINVOICE_SALES_RECEIPT;
                } else if (DocumentType.Type.PURCHASE_RECEIPT == documentType) {
                    format = FINVOICE_PURCHASE_RECEIPT;
                }
            } else {
                if (DocumentType.Type.SALES_INVOICE == documentType) {
                    format = FINVOICE_SALES_INVOICE;
                } else if (DocumentType.Type.PURCHASE_INVOICE == documentType) {
                    format = FINVOICE_PURCHASE_INVOICE;
                }
            }
        } else if (document.contains("xmlns=\"urn:iso:std:iso:20022:tech:xsd:camt.053.")) {
            format = DocumentFormat.Format.CAMT_053_001_08;
        } else if (document.contains("xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\"") || document.contains("<Invoice ")) {
            if (DocumentType.Type.SALES_INVOICE == documentType) {
                format = DocumentFormat.Format.UBL_2_1_SALES_INVOICE;
            } else if (DocumentType.Type.PURCHASE_INVOICE == documentType) {
                format = DocumentFormat.Format.UBL_2_1_PURCHASE_INVOICE;
            }
        } else if (document.contains("xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Order-2\"") || document.contains("<Order ")) {
            if (DocumentType.Type.SALES_ORDER == documentType) {
                format = DocumentFormat.Format.UBL_2_1_SALES_ORDER;
            } else if (DocumentType.Type.PURCHASE_ORDER == documentType) {
                format = DocumentFormat.Format.UBL_2_1_PURCHASE_ORDER;
            }
        } else if (document.contains("<xbrli:xbrl")) {
            format = DocumentFormat.Format.XBRL_GL;
        } else {
            format = DocumentFormat.Format.OTHER;
        }

        if (format == null) {
            throw new IllegalArgumentException("Could not find document format from type" + documentType.name());
        }

        return format;
    }

    public static boolean isCompatible(final DocumentType.Type documentType, final DocumentFormat.Format documentFormat) {
        boolean isCompatible;
        if (DocumentType.isInvoice(documentType)) {
            isCompatible =  DocumentFormat.isInvoice(documentFormat);
        } else if (DocumentType.isOrder(documentType)) {
            isCompatible =  DocumentFormat.isOrder(documentFormat);
        } else {
            isCompatible =  (!DocumentFormat.isInvoice(documentFormat) && !DocumentFormat.isOrder(documentFormat));
        }
        return isCompatible;
    }

}
