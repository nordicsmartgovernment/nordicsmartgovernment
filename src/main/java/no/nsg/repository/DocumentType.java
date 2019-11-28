package no.nsg.repository;


public class DocumentType {

    public enum Type {
        BANK_STATEMENT,
        PURCHASE_INVOICE,
        SALES_INVOICE,
        CASH_MEMO,
        PAYROLL_SLIP,
        RECEIPT,
        CREDIT_NOTE,
        DEBIT_NOTE,
        STATEMENT_OF_ACCOUNT,
        REMINDER,
        CATALOGUE_REQUEST,
        SPECIFICATION_UPDATE,
        ORDER_CANCELLATION,
        CATALOGUE,
        CATALOGUE_PRICING_UPDATE,
        APPLICATION_RESPONSE,
        CATALOGUE_DELETION,
        PURCHASE_ORDER,
        ORDER_CHANGE,
        CATALOGUE_ITEM,
        ORDER_RESPONSE_SIMPLE,
        ORDER_RESPONSE,
        OTHER
    }

    //Very explicit mapping from type to/from int. Mapped enums should NEVER get a new value! (they exist as int in database)
    public static int toInt(final Type type) {
        switch (type) {
            default:                       return  0;
            case OTHER:                    return  1;
            case BANK_STATEMENT:           return  2;
            case PURCHASE_INVOICE:         return  3;
            case SALES_INVOICE:            return  4;
            case CASH_MEMO:                return  5;
            case PAYROLL_SLIP:             return  6;
            case RECEIPT:                  return  7;
            case CREDIT_NOTE:              return  8;
            case DEBIT_NOTE:               return  9;
            case STATEMENT_OF_ACCOUNT:     return 10;
            case REMINDER:                 return 11;
            case CATALOGUE_REQUEST:        return 12;
            case SPECIFICATION_UPDATE:     return 13;
            case ORDER_CANCELLATION:       return 14;
            case CATALOGUE:                return 15;
            case CATALOGUE_PRICING_UPDATE: return 16;
            case APPLICATION_RESPONSE:     return 17;
            case CATALOGUE_DELETION:       return 18;
            case PURCHASE_ORDER:           return 19;
            case ORDER_CHANGE:             return 20;
            case CATALOGUE_ITEM:           return 21;
            case ORDER_RESPONSE_SIMPLE:    return 22;
            case ORDER_RESPONSE:           return 23;
        }
    }

    public static Type fromInteger(final Integer type) {
        if (type == null) {
            return null;
        }

        switch (type) {
            default: return null;
            case  1: return Type.OTHER;
            case  2: return Type.BANK_STATEMENT;
            case  3: return Type.PURCHASE_INVOICE;
            case  4: return Type.SALES_INVOICE;
            case  5: return Type.CASH_MEMO;
            case  6: return Type.PAYROLL_SLIP;
            case  7: return Type.RECEIPT;
            case  8: return Type.CREDIT_NOTE;
            case  9: return Type.DEBIT_NOTE;
            case 10: return Type.STATEMENT_OF_ACCOUNT;
            case 11: return Type.REMINDER;
            case 12: return Type.CATALOGUE_REQUEST;
            case 13: return Type.SPECIFICATION_UPDATE;
            case 14: return Type.ORDER_CANCELLATION;
            case 15: return Type.CATALOGUE;
            case 16: return Type.CATALOGUE_PRICING_UPDATE;
            case 17: return Type.APPLICATION_RESPONSE;
            case 18: return Type.CATALOGUE_DELETION;
            case 19: return Type.PURCHASE_ORDER;
            case 20: return Type.ORDER_CHANGE;
            case 21: return Type.CATALOGUE_ITEM;
            case 22: return Type.ORDER_RESPONSE_SIMPLE;
            case 23: return Type.ORDER_RESPONSE;
        }
    }

    public static Type fromMimeType(final String mimeType) {
        if (mimeType == null) {
            return null;
        }

        if ("application/vnd.nordicsmartgovernment.other".equalsIgnoreCase(mimeType)) {
            return Type.OTHER;
        } else if ("application/vnd.nordicsmartgovernment.bank-statement".equalsIgnoreCase(mimeType)) {
            return Type.BANK_STATEMENT;
        } else if ("application/vnd.nordicsmartgovernment.purchase-invoice".equalsIgnoreCase(mimeType)) {
            return Type.PURCHASE_INVOICE;
        } else if ("application/vnd.nordicsmartgovernment.sales-invoice".equalsIgnoreCase(mimeType)) {
            return Type.SALES_INVOICE;
        } else if ("application/vnd.nordicsmartgovernment.cash-memo".equalsIgnoreCase(mimeType)) {
            return Type.CASH_MEMO;
        } else if ("application/vnd.nordicsmartgovernment.payroll-slip".equalsIgnoreCase(mimeType)) {
            return Type.PAYROLL_SLIP;
        } else if ("application/vnd.nordicsmartgovernment.receipt".equalsIgnoreCase(mimeType)) {
            return Type.RECEIPT;
        } else if ("application/vnd.nordicsmartgovernment.credit-note".equalsIgnoreCase(mimeType)) {
            return Type.CREDIT_NOTE;
        } else if ("application/vnd.nordicsmartgovernment.debit-note".equalsIgnoreCase(mimeType)) {
            return Type.DEBIT_NOTE;
        } else if ("application/vnd.nordicsmartgovernment.statement-of-account".equalsIgnoreCase(mimeType)) {
            return Type.STATEMENT_OF_ACCOUNT;
        } else if ("application/vnd.nordicsmartgovernment.reminder".equalsIgnoreCase(mimeType)) {
            return Type.REMINDER;
        } else if ("application/vnd.nordicsmartgovernment.catalogue-request".equalsIgnoreCase(mimeType)) {
            return Type.CATALOGUE_REQUEST;
        } else if ("application/vnd.nordicsmartgovernment.specification-update".equalsIgnoreCase(mimeType)) {
            return Type.SPECIFICATION_UPDATE;
        } else if ("application/vnd.nordicsmartgovernment.order-cancellation".equalsIgnoreCase(mimeType)) {
            return Type.ORDER_CANCELLATION;
        } else if ("application/vnd.nordicsmartgovernment.catalogue".equalsIgnoreCase(mimeType)) {
            return Type.CATALOGUE;
        } else if ("application/vnd.nordicsmartgovernment.catalogue-pricing-update".equalsIgnoreCase(mimeType)) {
            return Type.CATALOGUE_PRICING_UPDATE;
        } else if ("application/vnd.nordicsmartgovernment.application-response".equalsIgnoreCase(mimeType)) {
            return Type.APPLICATION_RESPONSE;
        } else if ("application/vnd.nordicsmartgovernment.catalogue-deletion".equalsIgnoreCase(mimeType)) {
            return Type.CATALOGUE_DELETION;
        } else if ("application/vnd.nordicsmartgovernment.purchase-order".equalsIgnoreCase(mimeType)) {
            return Type.PURCHASE_ORDER;
        } else if ("application/vnd.nordicsmartgovernment.order-change".equalsIgnoreCase(mimeType)) {
            return Type.ORDER_CHANGE;
        } else if ("application/vnd.nordicsmartgovernment.catalogue-item".equalsIgnoreCase(mimeType)) {
            return Type.CATALOGUE_ITEM;
        } else if ("application/vnd.nordicsmartgovernment.order-response-simple".equalsIgnoreCase(mimeType)) {
            return Type.ORDER_RESPONSE_SIMPLE;
        } else if ("application/vnd.nordicsmartgovernment.order-response".equalsIgnoreCase(mimeType)) {
            return Type.ORDER_RESPONSE;
        } else {
            return null;
        }
    }

    public static String toMimeType(final Type type) {
        if (type == null) {
            return null;
        }

        switch (type) {
            default: return null;
            case OTHER:                    return "application/vnd.nordicsmartgovernment.other";
            case BANK_STATEMENT:           return "application/vnd.nordicsmartgovernment.bank-statement";
            case PURCHASE_INVOICE:         return "application/vnd.nordicsmartgovernment.purchase-invoice";
            case SALES_INVOICE:            return "application/vnd.nordicsmartgovernment.sales-invoice";
            case CASH_MEMO:                return "application/vnd.nordicsmartgovernment.cash-memo";
            case PAYROLL_SLIP:             return "application/vnd.nordicsmartgovernment.payroll-slip";
            case RECEIPT:                  return "application/vnd.nordicsmartgovernment.receipt";
            case CREDIT_NOTE:              return "application/vnd.nordicsmartgovernment.credit-note";
            case DEBIT_NOTE:               return "application/vnd.nordicsmartgovernment.debit-note";
            case STATEMENT_OF_ACCOUNT:     return "application/vnd.nordicsmartgovernment.statement-of-account";
            case REMINDER:                 return "application/vnd.nordicsmartgovernment.reminder";
            case CATALOGUE_REQUEST:        return "application/vnd.nordicsmartgovernment.catalogue-request";
            case SPECIFICATION_UPDATE:     return "application/vnd.nordicsmartgovernment.specification-update";
            case ORDER_CANCELLATION:       return "application/vnd.nordicsmartgovernment.order-cancellation";
            case CATALOGUE:                return "application/vnd.nordicsmartgovernment.catalogue";
            case CATALOGUE_PRICING_UPDATE: return "application/vnd.nordicsmartgovernment.catalogue-pricing-update";
            case APPLICATION_RESPONSE:     return "application/vnd.nordicsmartgovernment.application-response";
            case CATALOGUE_DELETION:       return "application/vnd.nordicsmartgovernment.catalogue-deletion";
            case PURCHASE_ORDER:           return "application/vnd.nordicsmartgovernment.purchase-order";
            case ORDER_CHANGE:             return "application/vnd.nordicsmartgovernment.order-change";
            case CATALOGUE_ITEM:           return "application/vnd.nordicsmartgovernment.catalogue-item";
            case ORDER_RESPONSE_SIMPLE:    return "application/vnd.nordicsmartgovernment.order-response-simple";
            case ORDER_RESPONSE:           return "application/vnd.nordicsmartgovernment.order-response";
        }
    }

    public static String getDocumentMimeTypes() {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        Type type;
        while ((type=fromInteger(i++)) != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append('"');
            sb.append(toMimeType(type));
            sb.append('"');
        }
        return sb.toString();
    }

}
