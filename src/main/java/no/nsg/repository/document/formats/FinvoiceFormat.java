package no.nsg.repository.document.formats;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class FinvoiceFormat extends DocumentFormat {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");


    @Override
    public String getDocumentSupplier(Document parsedDocument) {
        if (parsedDocument != null) {
            Node child = parsedDocument.getElementsByTagName("SellerPartyDetails").item(0);
            if (child instanceof Element) {
                child = ((Element) child).getElementsByTagName("SellerPartyIdentifier").item(0);
                if (child != null) {
                    return child.getTextContent();
                }
            }
        }
        return null;
    }

    @Override
    public String getDocumentCustomer(Document parsedDocument) {
        if (parsedDocument != null) {
            Node child = parsedDocument.getElementsByTagName("BuyerPartyDetails").item(0);
            if (child instanceof Element) {
                child = ((Element) child).getElementsByTagName("BuyerPartyIdentifier").item(0);
                if (child != null) {
                    return child.getTextContent();
                }
            }
        }
        return null;
    }

    @Override
    public LocalDateTime getTransactionTime(final Document parsedDocument) {
        if (parsedDocument != null) {
            Node child = parsedDocument.getElementsByTagName("InvoiceDetails").item(0);
            if (child instanceof Element) {
                child = ((Element) child).getElementsByTagName("InvoiceDate").item(0);
                if (child != null) {
                    try {
                        return LocalDate.parse(child.getTextContent(), dateFormatter).atStartOfDay();
                    } catch (DateTimeParseException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

}
