package no.nsg.repository.document.formats;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class FinvoiceFormat extends DocumentFormat {

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

}
