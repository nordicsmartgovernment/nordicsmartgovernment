package no.nsg.repository;


public class DocumentType {

    public enum Type {
        INVOICE,
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
        ORDER_RESPONSE
    }

    //Very explicit mapping from type to/from int. Mapped enums should NEVER get a new value! (they exist as int in database)
    public static int toInt(final Type type) {
        switch (type) {
            default:                       return  0;
            case INVOICE:                  return  1;
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
            case  1: return Type.INVOICE;
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

}
