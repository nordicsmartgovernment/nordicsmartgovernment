<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn="http://www.w3.org/2005/02/xpath-functions"
                xmlns:iso4217="http://www.xbrl.org/2003/iso4217"
                xmlns:link="http://www.xbrl.org/2003/linkbase"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:gl-cor-fi="http://www.xbrl.org/int/gl/cor/fi/2017-01-01" xmlns:gl-plt="http://www.xbrl.org/int/gl/plt/2016-12-01"
                xmlns:gl-cor="http://www.xbrl.org/int/gl/cor/2016-12-01"
                xmlns:iso639="http://www.xbrl.org/2005/iso639"
                xmlns:gl-taf="http://www.xbrl.org/int/gl/taf/2016-12-01"
                xmlns:gl-muc="http://www.xbrl.org/int/gl/muc/2016-12-01"
                xmlns:xbrli="http://www.xbrl.org/2003/instance"
                xmlns:gl-srcd="http://www.xbrl.org/int/gl/srcd/2016-12-01"
                xmlns:gl-rapko="http://www.xbrl.org/int/gl/rapko/2015-07-01"
                xmlns:gl-bus="http://www.xbrl.org/int/gl/bus/2016-12-01"
                xpath-default-namespace="urn:StandardAuditFile-Taxation-Financial:NO">

    <!--All namespace declarations reagrding the instance document above-->

    <xsl:output method="xml" encoding="utf-8"  indent="yes"/>


    <!-- string for default namespace uri and schema location -->
    <xsl:variable name="ns" select="'urn:StandardAuditFile-Taxation-Financial:NO'"/>
    <xsl:variable name="schemaLoc" select="'urn:StandardAuditFile-Taxation-Financial:NO Norwegian_SAF-T_Financial_Schema_v_1.10.xsd'"/>

    <!-- template for root element -->
    <!-- adds default namespace and schema location -->
    <xsl:template match="/*" priority="1">
        <xsl:element name="{local-name()}" namespace="{$ns}">
            <xsl:attribute name="xsi:schemaLocation"
                           namespace="http://www.w3.org/2001/XMLSchema-instance">
                <xsl:value-of select="$schemaLoc"/>
            </xsl:attribute>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:element>
    </xsl:template>



    <xsl:template match='/'>

        <AuditFile xmlns="urn:StandardAuditFile-Taxation-Financial:NO" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="urn:StandardAuditFile-Taxation-Financial:NO Norwegian_SAF-T_Financial_Schema_v_1.10.xsd">

            <Header>
                <AuditFileVersion>1.10</AuditFileVersion>
                <xsl:variable name="value" select="//gl-bus:organizationAddressCountry"/><xsl:if test="string($value)"><AuditFileCountry><xsl:value-of select="$value"/></AuditFileCountry></xsl:if>

                <xsl:variable name="value" select="//gl-cor:creationDate"/><xsl:if test="string($value)"><AuditFileDateCreated><xsl:value-of select="$value"/></AuditFileDateCreated></xsl:if>
                <xsl:variable name="value" select="//gl-bus:creator"/><xsl:if test="string($value)"><SoftwareCompanyName><xsl:value-of select="$value"/></SoftwareCompanyName></xsl:if>
                <xsl:variable name="value" select="//gl-bus:sourceApplication"/><xsl:if test="string($value)"><SoftwareID><xsl:value-of select="$value"/></SoftwareID></xsl:if>
                <xsl:variable name="value" select="substring(//gl-bus:sourceApplication,1,18)"/><xsl:if test="string($value)"><SoftwareVersion><xsl:value-of select="$value"/></SoftwareVersion></xsl:if>

                <Company>
                    <xsl:variable name="value" select="//gl-bus:organizationIdentifier[following-sibling::gl-bus:organizationDescription='ytunnus']"/><xsl:if test="string($value)"><RegistrationNumber><xsl:value-of select="$value"/></RegistrationNumber></xsl:if>
                    <xsl:variable name="value" select="//gl-bus:organizationAddressName"/><xsl:if test="string($value)"><Name><xsl:value-of select="$value"/></Name></xsl:if>
                    <Address>
                        <xsl:variable name="value" select="//gl-bus:organizationAddressStreet"/><xsl:if test="string($value)"><StreetName><xsl:value-of select="$value"/></StreetName></xsl:if>
                        <xsl:variable name="value" select="//gl-bus:organizationBuildingNumber"/><xsl:if test="string($value)"><Number><xsl:value-of select="$value"/></Number></xsl:if>
                        <xsl:variable name="value" select="//gl-bus:organizationAddressStreet2"/><xsl:if test="string($value)"><AdditionalAddressDetail><xsl:value-of select="$value"/></AdditionalAddressDetail></xsl:if>

                        <xsl:variable name="value" select="//gl-bus:organizationAddressCity"/><xsl:if test="string($value)"><City><xsl:value-of select="$value"/></City></xsl:if>
                        <xsl:variable name="value" select="//gl-bus:organizationAddressZipOrPostalCode"/><xsl:if test="string($value)"><PostalCode><xsl:value-of select="$value"/></PostalCode></xsl:if>
                        <xsl:variable name="value" select="//gl-bus:organizationAddressStateOrProvince"/><xsl:if test="string($value)"><Region><xsl:value-of select="$value"/></Region></xsl:if>
                        <xsl:variable name="value" select="//gl-bus:organizationAddressCountry"/><xsl:if test="string($value)"><Country><xsl:value-of select="$value"/></Country></xsl:if>
                        <xsl:variable name="value" select="//gl-bus:organizationAddressPurpose"/><xsl:if test="string($value)"><AddressType><xsl:value-of select="$value"/></AddressType></xsl:if>


                    </Address>
                    <Contact>
                        <ContactPerson>
                            <xsl:variable name="value" select="//gl-bus:contactFirstName"/><xsl:if test="string($value)"><FirstName><xsl:value-of select="$value"/></FirstName></xsl:if>
                            <xsl:variable name="value" select="//gl-bus:contactSuffix"/><xsl:if test="string($value)"><LastNamePrefix><xsl:value-of select="$value"/></LastNamePrefix></xsl:if>
                            <xsl:variable name="value" select="//gl-bus:contactLastName"/><xsl:if test="string($value)"><LastName><xsl:value-of select="$value"/></LastName></xsl:if>
                            <xsl:variable name="value" select="//gl-bus:contactPositionRole"/><xsl:if test="string($value)"><Salutation><xsl:value-of select="$value"/></Salutation></xsl:if>
                            <xsl:variable name="value" select="//gl-bus:contactPrefix"/><xsl:if test="string($value)"><OtherTitles><xsl:value-of select="$value"/></OtherTitles></xsl:if>

                        </ContactPerson>
                        <xsl:variable name="value" select="//gl-bus:phoneNumber"/><xsl:if test="string($value)"><Telephone><xsl:value-of select="$value"/></Telephone></xsl:if>
                        <xsl:variable name="value" select="//gl-bus:entityFaxNumber"/><xsl:if test="string($value)"><Fax><xsl:value-of select="$value"/></Fax></xsl:if>
                        <xsl:variable name="value" select="//gl-bus:entityEmailAddress"/><xsl:if test="string($value)"><Email><xsl:value-of select="$value"/></Email></xsl:if>
                        <xsl:variable name="value" select="//gl-bus:webSiteURL"/><xsl:if test="string($value)"><Website><xsl:value-of select="$value"/></Website></xsl:if>
                        <xsl:variable name="value" select="//gl-bus:phoneNumber"/><xsl:if test="string($value)"><MobilePhone><xsl:value-of select="$value"/></MobilePhone></xsl:if>


                    </Contact>
                    <TaxRegistration>
                        <xsl:variable name="value" select="//gl-bus:organizationIdentifier[following-sibling::gl-bus:organizationDescription='alvtunnus']"/><xsl:if test="string($value)"><TaxRegistrationNumber><xsl:value-of select="$value"/></TaxRegistrationNumber>
                        <TaxAuthority>Skatteetaten</TaxAuthority></xsl:if>
                    </TaxRegistration>
                    <BankAccount>
                        <xsl:variable name="value" select="//gl-bus:organizationIdentifier[following-sibling::gl-bus:organizationDescription='IBAN']"/><xsl:if test="string($value)"><IBANNumber><xsl:value-of select="$value"/></IBANNumber></xsl:if>
                        <xsl:variable name="value" select="//gl-bus:organizationIdentifier[following-sibling::gl-bus:organizationDescription='BIC']"/><xsl:if test="string($value)"><BIC><xsl:value-of select="$value"/></BIC></xsl:if>
                    </BankAccount>

                </Company>

                <xsl:variable name="value" select="//gl-muc:defaultCurrency"/><xsl:if test="string($value)"><DefaultCurrencyCode><xsl:value-of select="$value"/></DefaultCurrencyCode></xsl:if>

                <xsl:variable name="value" select="//gl-cor:entriesComment"/><xsl:if test="string($value)"><HeaderComment><xsl:value-of select="$value"/></HeaderComment></xsl:if>


                <TaxAccountingBasis>A</TaxAccountingBasis>
                <xsl:variable name="value" select="//gl-bus:organizationIdentifier[following-sibling::gl-bus:organizationDescription='alvtunnus']"/><xsl:if test="string($value)"><TaxEntity><xsl:value-of select="$value"/></TaxEntity></xsl:if>

            </Header>


            <GeneralLedgerEntries>
                <xsl:variable name="value" select="count(//gl-cor:entryDetail)"/><xsl:if test="string($value)"><NumberOfEntries><xsl:value-of select="$value"/></NumberOfEntries></xsl:if>

                <xsl:variable name="value" select="sum(//gl-cor:amount[following-sibling::gl-cor:debitCreditCode='D' or following-sibling::gl-cor:debitCreditCode='Debit'])"/><xsl:if test="string($value)"><TotalDebit><xsl:value-of select="$value"/></TotalDebit></xsl:if>
                <xsl:variable name="value" select="sum(//gl-cor:amount[following-sibling::gl-cor:debitCreditCode='C' or following-sibling::gl-cor:debitCreditCode='Credit'])"/><xsl:if test="string($value)"><TotalCredit><xsl:value-of select="$value"/></TotalCredit></xsl:if>
                <Journal>
                    <xsl:variable name="value" select="//gl-cor:sourceJournalID[1]"/><xsl:if test="string($value)"><JournalID><xsl:value-of select="$value"/></JournalID></xsl:if>

                    <xsl:variable name="value" select="//gl-bus:sourceJournalDescription[1]"/><xsl:if test="string($value)"><Description><xsl:value-of select="$value"/></Description></xsl:if>

                    <Type>GL</Type>

                    <!--one Transaction per each gl-cor:entryHeader-->
                    <xsl:for-each select="//gl-cor:entryHeader">
                        <Transaction>
                            <xsl:variable name="value" select="./gl-cor:entryDetail[1]/gl-cor:documentNumber"/><xsl:if test="string($value)"><TransactionID><xsl:value-of select="$value"/></TransactionID></xsl:if>

                            <xsl:variable name="value" select="./gl-bus:postingCode"/><xsl:if test="string($value)"><Period><xsl:value-of select="$value"/></Period></xsl:if>

                            <xsl:variable name="value" select="//gl-bus:fiscalYearStart"/><xsl:if test="string($value)"><PeriodYear><xsl:value-of select="year-from-date($value)"/></PeriodYear></xsl:if>

                            <xsl:variable name="value" select="./gl-cor:entryDetail[1]/gl-cor:documentDate"/><xsl:if test="string($value)"><TransactionDate><xsl:value-of select="$value"/></TransactionDate></xsl:if>

                            <xsl:variable name="value" select="./gl-cor:enteredBy"/><xsl:if test="string($value)"><SourceID><xsl:value-of select="$value"/></SourceID></xsl:if>
                            <xsl:variable name="value" select="./gl-cor:entryType"/><xsl:if test="string($value)"><TransactionType><xsl:value-of select="$value"/></TransactionType></xsl:if>
                            <xsl:variable name="value" select="./gl-cor:entryComment"/><xsl:if test="string($value)"><Description><xsl:value-of select="$value"/></Description></xsl:if>
                            <xsl:variable name="value" select="./gl-bus:batchID"/><xsl:if test="string($value)"><BatchID><xsl:value-of select="$value"/></BatchID></xsl:if>
                            <xsl:variable name="value" select="./gl-cor:postedDate"/><xsl:if test="string($value)"><SystemEntryDate><xsl:value-of select="$value"/></SystemEntryDate></xsl:if>
                            <xsl:variable name="value" select="./gl-cor:entryDetail[1]/gl-cor:postingDate"/><xsl:if test="string($value)"><GLPostingDate><xsl:value-of select="$value"/></GLPostingDate></xsl:if>
                            <xsl:variable name="value" select="./gl-cor:entryNumber"/><xsl:if test="string($value)"><SystemID><xsl:value-of select="$value"/></SystemID></xsl:if>

                            <!--One Line per each entryDetail-->
                            <xsl:for-each select="./gl-cor:entryDetail">
                                <Line>

                                    <xsl:variable name="value" select="./gl-cor:lineNumber"/><xsl:if test="string($value)"><RecordID><xsl:value-of select="$value"/></RecordID></xsl:if>
                                    <xsl:variable name="value" select="./gl-cor:account[1]/gl-cor:accountMainID"/><xsl:if test="string($value)"><AccountID><xsl:value-of select="$value"/></AccountID></xsl:if>

                                    <!--One Analysis per each accountSub-->
                                    <xsl:for-each select="./gl-cor:account[1]/gl-cor:accountSub">
                                        <Analysis>
                                            <xsl:variable name="value" select="./gl-cor:accountSubType"/><xsl:if test="string($value)"><AnalysisType><xsl:value-of select="$value"/></AnalysisType></xsl:if>
                                            <xsl:variable name="value" select="./gl-cor:accountSubID"/><xsl:if test="string($value)"><AnalysisID><xsl:value-of select="$value"/></AnalysisID></xsl:if>
                                        </Analysis>
                                    </xsl:for-each>

                                    <xsl:variable name="value" select="./gl-cor:documentNumber"/><xsl:if test="string($value)"><SourceDocumentID><xsl:value-of select="$value"/></SourceDocumentID></xsl:if>
                                    <xsl:variable name="value" select="./gl-cor:identifierCode[following-sibling::gl-cor:identifierType='C' or following-sibling::gl-cor:identifierType='FI-B']"/><xsl:if test="string($value)"><CustomerID><xsl:value-of select="$value"/></CustomerID></xsl:if>
                                    <xsl:variable name="value" select="./gl-cor:identifierCode[following-sibling::gl-cor:identifierType='V' or following-sibling::gl-cor:identifierType='FI-S']"/><xsl:if test="string($value)"><SupplierID><xsl:value-of select="$value"/></SupplierID></xsl:if>
                                    <xsl:variable name="value" select="./gl-cor:detailComment"/><xsl:if test="string($value)"><Description><xsl:value-of select="$value"/></Description></xsl:if>

                                    <!--todo tässä pitää käsitellä vielä sandboxin tapaus jossa pelataan vain etumerkeillä-->
                                    <xsl:variable name="value" select="./gl-cor:amount"/><xsl:if test="number($value) and $value[following-sibling::gl-cor:debitCreditCode='D' or following-sibling::gl-cor:debitCreditCode='Debit']"><DebitAmount><Amount><xsl:value-of select="$value"/></Amount>
                                    <xsl:if test="./gl-muc:amountCurrency!=//gl-muc:defaultCurrency">
                                        <xsl:variable name="value" select="./gl-muc:amountCurrency"/><xsl:if test="string($value)"><CurrencyCode><xsl:value-of select="$value"/></CurrencyCode></xsl:if><xsl:variable name="value" select="./gl-muc:amountOriginalAmount"/><xsl:if test="string($value)"><CurrencyAmount><xsl:value-of select="$value"/></CurrencyAmount></xsl:if>
                                        <xsl:variable name="value" select="./gl-muc:amountOriginalExchangeRate"/><xsl:if test="string($value)"><ExchangeRate><xsl:value-of select="$value"/></ExchangeRate></xsl:if>
                                    </xsl:if>
                                </DebitAmount></xsl:if>


                                    <xsl:variable name="value" select="./gl-cor:amount"/><xsl:if test="number($value) and $value[following-sibling::gl-cor:debitCreditCode='C' or following-sibling::gl-cor:debitCreditCode='Credit']"><CreditAmount><Amount><xsl:value-of select="$value"/></Amount>
                                    <xsl:if test="./gl-muc:amountCurrency!=//gl-muc:defaultCurrency">
                                        <xsl:variable name="value" select="./gl-muc:amountCurrency"/><xsl:if test="string($value)"><CurrencyCode><xsl:value-of select="$value"/></CurrencyCode></xsl:if><xsl:variable name="value" select="./gl-muc:amountOriginalAmount"/><xsl:if test="string($value)"><CurrencyAmount><xsl:value-of select="$value"/></CurrencyAmount></xsl:if>
                                        <xsl:variable name="value" select="./gl-muc:amountOriginalExchangeRate"/><xsl:if test="string($value)"><ExchangeRate><xsl:value-of select="$value"/></ExchangeRate></xsl:if>
                                    </xsl:if>
                                </CreditAmount></xsl:if>




                                    <!--One TaxInformation per each gl-cor:taxes-->
                                    <xsl:for-each select="./gl-cor:taxes">
                                        <TaxInformation>
                                            <xsl:variable name="value" select="./gl-cor:taxTableCode"/><xsl:if test="string($value)"><TaxType><xsl:value-of select="$value"/></TaxType></xsl:if>
                                            <xsl:variable name="value" select="./gl-cor:taxCode"/><xsl:if test="string($value)"><TaxCode><xsl:value-of select="$value"/></TaxCode></xsl:if>
                                            <xsl:variable name="value" select="./gl-cor:taxPercentageRate"/><xsl:if test="string($value)"><TaxPercentage><xsl:value-of select="$value"/></TaxPercentage></xsl:if>
                                            <xsl:variable name="value" select="./gl-cor:taxBasis"/><xsl:if test="string($value)"><TaxBase><xsl:value-of select="$value"/></TaxBase></xsl:if>
                                            <xsl:variable name="value" select="./gl-cor:taxBasis"/><xsl:if test="string($value)"><TaxBaseDescription><xsl:value-of select="$value"/></TaxBaseDescription></xsl:if>
                                            <xsl:variable name="value" select="./gl-cor:taxAmount"/><xsl:if test="string($value)"><TaxAmount><Amount><xsl:value-of select="$value"/></Amount></TaxAmount></xsl:if>
                                            <xsl:variable name="value" select="./gl-cor:taxCommentExemption"/><xsl:if test="string($value)"><TaxExemptionReason><xsl:value-of select="$value"/></TaxExemptionReason></xsl:if>
                                        </TaxInformation>
                                    </xsl:for-each>

                                    <xsl:variable name="value" select="./gl-cor:documentReference"/><xsl:if test="string($value)"><ReferenceNumber><xsl:value-of select="$value"/></ReferenceNumber></xsl:if>
                                    <xsl:variable name="value" select="./gl-cor:maturityDate"/><xsl:if test="string($value)"><DueDate><xsl:value-of select="$value"/></DueDate></xsl:if>
                                    <xsl:variable name="value" select="./gl-bus:measurable[1]/gl-bus:measurableQuantity"/><xsl:if test="string($value)"><Quantity><xsl:value-of select="$value"/></Quantity></xsl:if>
                                    <xsl:variable name="value" select="./gl-taf:originatingDocumentStructure/gl-taf:originatingDocumentNumber"/><xsl:if test="string($value)"><CrossReference><xsl:value-of select="$value"/></CrossReference></xsl:if>

                                    <xsl:variable name="value" select="./gl-cor:postedDate"/><xsl:if test="string($value)"><SystemEntryTime><xsl:value-of select="$value"/></SystemEntryTime></xsl:if>


                                </Line>
                            </xsl:for-each>


                        </Transaction>
                    </xsl:for-each>
                </Journal>
            </GeneralLedgerEntries>
        </AuditFile>
    </xsl:template>





</xsl:stylesheet>