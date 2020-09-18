package no.nsg.repository;


public class DocumentType {

    public enum Type {
        OTHER                    (1,  MimeType.NSG_OTHER),
        BANK_STATEMENT           (2,  MimeType.NSG_BANKSTATEMENT),
        PURCHASE_INVOICE         (3,  MimeType.NSG_PURCHASE_INVOICE),
        SALES_INVOICE            (4,  MimeType.NSG_SALES_INVOICE),
        CASH_MEMO                (5,  MimeType.NSG_CASH_MEMO),
        PAYROLL_SLIP             (6,  MimeType.NSG_PAYROLL_SLIP),
        PURCHASE_RECEIPT         (7,  MimeType.NSG_PURCHASE_RECEIPT),
        SALES_RECEIPT            (8,  MimeType.NSG_SALES_RECEIPT),
        CREDIT_NOTE              (9,  MimeType.NSG_CREDIT_NOTE),
        DEBIT_NOTE               (10, MimeType.NSG_DEBIT_NOTE),
        STATEMENT_OF_ACCOUNT     (11, MimeType.NSG_STATEMENT_OF_ACCOUNT),
        REMINDER                 (12, MimeType.NSG_REMINDER),
        CATALOGUE_REQUEST        (13, MimeType.NSG_CATALOGUE_REQUEST),
        SPECIFICATION_UPDATE     (14, MimeType.NSG_SPECIFICATION_UPDATE),
        ORDER_CANCELLATION       (15, MimeType.NSG_ORDER_CANCELLATION),
        CATALOGUE                (16, MimeType.NSG_CATALOGUE),
        CATALOGUE_PRICING_UPDATE (17, MimeType.NSG_CATALOGUE_PRICING_UPDATE),
        APPLICATION_RESPONSE     (18, MimeType.NSG_APPLICATION_RESPONSE),
        CATALOGUE_DELETION       (19, MimeType.NSG_CATALOGUE_DELETION),
        PURCHASE_ORDER           (20, MimeType.NSG_PURCHASE_ORDER),
        ORDER_CHANGE             (21, MimeType.NSG_ORDER_CHANGE),
        CATALOGUE_ITEM           (22, MimeType.NSG_CATALOGUE_ITEM),
        ORDER_RESPONSE_SIMPLE    (23, MimeType.NSG_ORDER_RESPONSE_SIMPLE),
        ORDER_RESPONSE           (24, MimeType.NSG_ORDER_RESPONSE),
        SALES_ORDER              (25, MimeType.NSG_SALES_ORDER),
        IMAGE_JPG                (26, MimeType.IMAGE_JPG),
        IMAGE_PNG                (27, MimeType.IMAGE_PNG),
        DOCUMENT_PDF             (28, MimeType.DOCUMENT_PDF),
        XBRL_GL                  (29, MimeType.XBRL_GL);

        private final int dbInt;
        private final String mimetype;

        Type(int dbInt, String mimetype) {
            this.dbInt = dbInt;

            this.mimetype = mimetype;
        }

        public String getMimetype() {
            return mimetype;
        }

        public int getDbInt() {
            return dbInt;
        }
    }

    public static boolean hasDirection(final Type type) {
        return isSales(type) || isPurchase(type);
    }

    public static boolean isInvoice(final Type type) {
        return (type == Type.PURCHASE_INVOICE || type == Type.SALES_INVOICE);
    }

    public static boolean isOrder(final Type type) {
        return (type == Type.PURCHASE_ORDER || type == Type.SALES_ORDER);
    }

    public static boolean isSales(final Type type) {
        return (type == Type.SALES_INVOICE || type == Type.SALES_ORDER);
    }

    public static boolean isPurchase(final Type type) {
        return (type == Type.PURCHASE_INVOICE || type == Type.PURCHASE_ORDER);
    }

    public static boolean isOther(final Type type) {
        return (type == Type.OTHER || type == Type.IMAGE_JPG || type == Type.IMAGE_PNG || type == Type.DOCUMENT_PDF || type == Type.XBRL_GL);
    }

    //Very explicit mapping from type to/from int. Mapped enums should NEVER get a new value! (they exist as int in database)
    public static int toInt(final Type type) {
        return type.getDbInt();
    }

    public static Type fromInteger(final Integer typeNum) {
        if (typeNum == null) {
            return null;
        }
        for (Type type : Type.values()) {
            if(type.getDbInt() == typeNum) {
                return type;
            }
        }
        return null;
    }

    public static Type fromMimeType(final String mimeType) {
        if (mimeType == null) {
            return null;
        }
        for (Type type : Type.values()) {
            if(type.getMimetype().equalsIgnoreCase(mimeType)) {
                return type;
            }
        }
        return null;
    }

    public static String toMimeType(final Type type) {
        if (type == null) {
            return null;
        }
        return type.getMimetype();
    }

    public static String getDocumentMimeTypes() {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        Type type;
        while ((type = fromInteger(i++)) != null) {
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
