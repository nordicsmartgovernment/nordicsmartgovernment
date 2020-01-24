package no.nsg.repository.document.formats;

import org.w3c.dom.Document;

import java.time.LocalDateTime;


public class UnknownDocumentFormat extends DocumentFormat {

    @Override
    public String getDocumentSupplier(final Document parsedDocument) {
        return null;
    }

    @Override
    public String getDocumentCustomer(final Document parsedDocument) {
        return null;
    }

    @Override
    public LocalDateTime getTransactionTime(final Document parsedDocument) {
        return null;
    }
}
