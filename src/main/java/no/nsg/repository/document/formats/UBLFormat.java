package no.nsg.repository.document.formats;

import no.nsg.repository.TransformationManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class UBLFormat implements DocumentFormat {

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

}
