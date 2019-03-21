package no.nsg.controller;


public class RestAPI {

    public static final String BASE = "/nordicsmartgovernment/v1/";

    public static final String POST_CREATE_DOCUMENT = "purchase_management/document";
    public static final String GET_DOCUMENT         = "purchase_management/document/{document_id}";

    public static final String GET_SEARCH_ENTRIES   = "bookkeeping/entry";
    public static final String POST_CREATE_ENTRY    = "bookkeeping/entry";
    public static final String GET_ENTRY            = "bookkeeping/entry/{entry_id}";
    public static final String PATCH_UPDATE_ENTRY   = "bookkeeping/entry/{entry_id}";

    public static final String POST_CREATE_INVOICE  = "syntetic_business_document_generator_service/invoice";
    public static final String PATCH_PAY_INVOICE    = "syntetic_business_document_generator_service/invoice/{invoice_id}";

}
