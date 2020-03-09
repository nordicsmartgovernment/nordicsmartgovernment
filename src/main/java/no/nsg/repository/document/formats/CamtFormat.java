package no.nsg.repository.document.formats;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class CamtFormat extends DocumentFormat {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:sszzz");


    @Override
    public String getDocumentSupplier(Document parsedDocument) {
        return null;
    }

    @Override
    public String getDocumentCustomer(Document parsedDocument) {
        return null;
    }

    @Override
    public LocalDateTime getTransactionTime(final Document parsedDocument) {
        if (parsedDocument != null) {
            Node child = parsedDocument.getElementsByTagName("CreDtTm").item(0);
            if (child != null) {
                try {
                    return LocalDate.parse(child.getTextContent(), dateFormatter).atStartOfDay();
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        }
        return null;
    }

}
