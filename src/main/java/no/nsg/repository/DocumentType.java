package no.nsg.repository;


public class DocumentType {

    public enum Type {
        INVOICE,
        BANKSTATEMENT,
        PURCHASE_INVOICE,
        SALES_INVOICE,
        CASH_MEMO,
        PAYROLL_SLIP,
        RECEIPT
    }

    public static int toInt(final Type type) {
        switch (type) {
            default:               return 0;
            case INVOICE:          return 1;
            case BANKSTATEMENT:    return 2;
            case PURCHASE_INVOICE: return 3;
            case SALES_INVOICE:    return 4;
            case CASH_MEMO:        return 5;
            case PAYROLL_SLIP:     return 6;
            case RECEIPT:          return 7;
        }
    }

    public static Type fromInteger(final Integer type) {
        if (type == null) {
            return null;
        }

        switch (type) {
            default: return null;
            case 1: return Type.INVOICE;
            case 2: return Type.BANKSTATEMENT;
            case 3: return Type.PURCHASE_INVOICE;
            case 4: return Type.SALES_INVOICE;
            case 5: return Type.CASH_MEMO;
            case 6: return Type.PAYROLL_SLIP;
            case 7: return Type.RECEIPT;
        }
    }

}
