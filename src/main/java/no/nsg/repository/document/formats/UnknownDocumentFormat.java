package no.nsg.repository.document.formats;

import org.w3c.dom.Document;


public class UnknownDocumentFormat implements DocumentFormat {

    @Override
    public String getDocumentSupplier(final Document parsedDocument) {
        return null;
    }

    @Override
    public String getDocumentCustomer(final Document parsedDocument) {
        return null;
    }

}
