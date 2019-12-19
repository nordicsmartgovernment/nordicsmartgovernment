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
        SALES_ORDER,
        ORDER_CHANGE,
        CATALOGUE_ITEM,
        ORDER_RESPONSE_SIMPLE,
        ORDER_RESPONSE,
        OTHER
    }

    public static boolean hasDirection(final Type type) {
        return isSales(type) || isPurchase(type);
    }

    public static boolean isInvoice(final Type type) {
        return (type!=null && (type==Type.PURCHASE_INVOICE || type==Type.SALES_INVOICE));
    }

    public static boolean isOrder(final Type type) {
        return (type!=null && (type==Type.PURCHASE_ORDER || type==Type.SALES_ORDER));
    }

    public static boolean isSales(final Type type) {
        return (type!=null && (type==Type.SALES_INVOICE || type==Type.SALES_ORDER));
    }

    public static boolean isPurchase(final Type type) {
        return (type!=null && (type==Type.PURCHASE_INVOICE || type==Type.PURCHASE_ORDER));
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
            case SALES_ORDER:              return 24;
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
            case 24: return Type.SALES_ORDER;
        }
    }

    public static Type fromMimeType(final String mimeType) {
        if (mimeType == null) {
            return null;
        }

        if (MimeType.NSG_OTHER.equalsIgnoreCase(mimeType)) {
            return Type.OTHER;
        } else if (MimeType.NSG_BANKSTATEMENT.equalsIgnoreCase(mimeType)) {
            return Type.BANK_STATEMENT;
        } else if (MimeType.NSG_PURCHASE_INVOICE.equalsIgnoreCase(mimeType)) {
            return Type.PURCHASE_INVOICE;
        } else if (MimeType.NSG_SALES_INVOICE.equalsIgnoreCase(mimeType)) {
            return Type.SALES_INVOICE;
        } else if (MimeType.NSG_CASH_MEMO.equalsIgnoreCase(mimeType)) {
            return Type.CASH_MEMO;
        } else if (MimeType.NSG_PAYROLL_SLIP.equalsIgnoreCase(mimeType)) {
            return Type.PAYROLL_SLIP;
        } else if (MimeType.NSG_RECEIPT.equalsIgnoreCase(mimeType)) {
            return Type.RECEIPT;
        } else if (MimeType.NSG_CREDIT_NOTE.equalsIgnoreCase(mimeType)) {
            return Type.CREDIT_NOTE;
        } else if (MimeType.NSG_DEBIT_NOTE.equalsIgnoreCase(mimeType)) {
            return Type.DEBIT_NOTE;
        } else if (MimeType.NSG_STATEMENT_OF_ACCOUNT.equalsIgnoreCase(mimeType)) {
            return Type.STATEMENT_OF_ACCOUNT;
        } else if (MimeType.NSG_REMINDER.equalsIgnoreCase(mimeType)) {
            return Type.REMINDER;
        } else if (MimeType.NSG_CATALOGUE_REQUEST.equalsIgnoreCase(mimeType)) {
            return Type.CATALOGUE_REQUEST;
        } else if (MimeType.NSG_SPECIFICATION_UPDATE.equalsIgnoreCase(mimeType)) {
            return Type.SPECIFICATION_UPDATE;
        } else if (MimeType.NSG_ORDER_CANCELLATION.equalsIgnoreCase(mimeType)) {
            return Type.ORDER_CANCELLATION;
        } else if (MimeType.NSG_CATALOGUE.equalsIgnoreCase(mimeType)) {
            return Type.CATALOGUE;
        } else if (MimeType.NSG_CATALOGUE_PRICING_UPDATE.equalsIgnoreCase(mimeType)) {
            return Type.CATALOGUE_PRICING_UPDATE;
        } else if (MimeType.NSG_APPLICATION_RESPONSE.equalsIgnoreCase(mimeType)) {
            return Type.APPLICATION_RESPONSE;
        } else if (MimeType.NSG_CATALOGUE_DELETION.equalsIgnoreCase(mimeType)) {
            return Type.CATALOGUE_DELETION;
        } else if (MimeType.NSG_PURCHASE_ORDER.equalsIgnoreCase(mimeType)) {
            return Type.PURCHASE_ORDER;
        } else if (MimeType.NSG_SALES_ORDER.equalsIgnoreCase(mimeType)) {
            return Type.SALES_ORDER;
        } else if (MimeType.NSG_ORDER_CHANGE.equalsIgnoreCase(mimeType)) {
            return Type.ORDER_CHANGE;
        } else if (MimeType.NSG_CATALOGUE_ITEM.equalsIgnoreCase(mimeType)) {
            return Type.CATALOGUE_ITEM;
        } else if (MimeType.NSG_ORDER_RESPONSE_SIMPLE.equalsIgnoreCase(mimeType)) {
            return Type.ORDER_RESPONSE_SIMPLE;
        } else if (MimeType.NSG_ORDER_RESPONSE.equalsIgnoreCase(mimeType)) {
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
            case OTHER:                    return MimeType.NSG_OTHER;
            case BANK_STATEMENT:           return MimeType.NSG_BANKSTATEMENT;
            case PURCHASE_INVOICE:         return MimeType.NSG_PURCHASE_INVOICE;
            case SALES_INVOICE:            return MimeType.NSG_SALES_INVOICE;
            case CASH_MEMO:                return MimeType.NSG_CASH_MEMO;
            case PAYROLL_SLIP:             return MimeType.NSG_PAYROLL_SLIP;
            case RECEIPT:                  return MimeType.NSG_RECEIPT;
            case CREDIT_NOTE:              return MimeType.NSG_CREDIT_NOTE;
            case DEBIT_NOTE:               return MimeType.NSG_DEBIT_NOTE;
            case STATEMENT_OF_ACCOUNT:     return MimeType.NSG_STATEMENT_OF_ACCOUNT;
            case REMINDER:                 return MimeType.NSG_REMINDER;
            case CATALOGUE_REQUEST:        return MimeType.NSG_CATALOGUE_REQUEST;
            case SPECIFICATION_UPDATE:     return MimeType.NSG_SPECIFICATION_UPDATE;
            case ORDER_CANCELLATION:       return MimeType.NSG_ORDER_CANCELLATION;
            case CATALOGUE:                return MimeType.NSG_CATALOGUE;
            case CATALOGUE_PRICING_UPDATE: return MimeType.NSG_CATALOGUE_PRICING_UPDATE;
            case APPLICATION_RESPONSE:     return MimeType.NSG_APPLICATION_RESPONSE;
            case CATALOGUE_DELETION:       return MimeType.NSG_CATALOGUE_DELETION;
            case PURCHASE_ORDER:           return MimeType.NSG_PURCHASE_ORDER;
            case SALES_ORDER:              return MimeType.NSG_SALES_ORDER;
            case ORDER_CHANGE:             return MimeType.NSG_ORDER_CHANGE;
            case CATALOGUE_ITEM:           return MimeType.NSG_CATALOGUE_ITEM;
            case ORDER_RESPONSE_SIMPLE:    return MimeType.NSG_ORDER_RESPONSE_SIMPLE;
            case ORDER_RESPONSE:           return MimeType.NSG_ORDER_RESPONSE;
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
