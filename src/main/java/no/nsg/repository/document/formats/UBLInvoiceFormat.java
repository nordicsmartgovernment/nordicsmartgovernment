package no.nsg.repository.document.formats;

import no.nsg.repository.TransformationManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class UBLInvoiceFormat extends DocumentFormat {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Override
    public String getDocumentSupplier(final Document parsedDocument) {
        if (parsedDocument != null) {
            Node child = parsedDocument.getElementsByTagNameNS(TransformationManager.CAC_NS, "AccountingSupplierParty").item(0);
            if (child instanceof Element) {
                child = ((Element) child).getElementsByTagNameNS(TransformationManager.CAC_NS, "PartyLegalEntity").item(0);
                if (child instanceof Element) {
                    child = ((Element) child).getElementsByTagNameNS(TransformationManager.CBC_NS, "CompanyID").item(0);
                    if (child != null) {
                        return child.getTextContent();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String getDocumentCustomer(final Document parsedDocument) {
        if (parsedDocument != null) {
            Node child = parsedDocument.getElementsByTagNameNS(TransformationManager.CAC_NS, "AccountingCustomerParty").item(0);
            if (child instanceof Element) {
                child = ((Element) child).getElementsByTagNameNS(TransformationManager.CAC_NS, "PartyLegalEntity").item(0);
                if (child instanceof Element) {
                    child = ((Element) child).getElementsByTagNameNS(TransformationManager.CBC_NS, "CompanyID").item(0);
                    if (child != null) {
                        return child.getTextContent();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public LocalDateTime getTransactionTime(final Document parsedDocument) {
        if (parsedDocument != null) {
            Node child = parsedDocument.getElementsByTagNameNS(TransformationManager.CBC_NS, "IssueDate").item(0);
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
