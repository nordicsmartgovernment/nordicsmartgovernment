<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" 
xmlns:fn="http://www.w3.org/2005/02/xpath-functions"
xmlns:iso4217="http://www.xbrl.org/2003/iso4217" 
xmlns:link="http://www.xbrl.org/2003/linkbase" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" 
xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
xpath-default-namespace="urn:oasis:names:specification:ubl:schema:xsd:Order-2" xmlns:udt="urn:un:unece:uncefact:data:draft:UnqualifiedDataTypesSchemaModule:2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-2" xmlns:map="http://www.nsg.org/map">

<!--All namespace declarations reagrding the instance document above-->

<!--<xsl:output method="xml" encoding="utf-8"  indent="yes"/>-->
<xsl:output omit-xml-declaration="yes" indent="yes"/>
  <xsl:strip-space elements="*"/>

<!--Lookup file for account ids-->
 <!--
   <xsl:template match="*">
    <xsl:element name="Invoice">
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template>
  -->

<xsl:decimal-format name="dkk" decimal-separator="," grouping-separator="&#160;"/>
<!--
<xsl:decimal-format name='base' decimal-separator=',' grouping-separator='.' minus-sign='-' />
 -->
<!-- string for default namespace uri and schema location -->
  <xsl:variable name="ns" select="'urn:oasis:names:specification:ubl:schema:xsd:Order-2'"/>
  <xsl:variable name="schemaLoc" select="'urn:oasis:names:specification:ubl:schema:xsd:Order-2 ../Skeemat/UBL-Order-2.1.xsd'"/>

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
 <!--<xsl:variable name="lookupDoc" select="document('account_mapping.xml')" />-->
<xbrli:xbrl xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.xbrl.org/int/gl/plt/2016-12-01 ../../../XBRL-GL-PWD-2016-12-01-fi-2018-11-06/gl/plt/case-c-b-m-u-t-s-r/gl-plt-fi-all-2017-01-01.xsd" xmlns:xbrll="http://www.xbrl.org/2003/linkbase"   xmlns:iso4217="http://www.xbrl.org/2003/iso4217" xmlns:gl-cor-fi="http://www.xbrl.org/int/gl/cor/fi/2017-01-01" xmlns:gl-plt="http://www.xbrl.org/int/gl/plt/2016-12-01"
            xmlns:gl-cor="http://www.xbrl.org/int/gl/cor/2016-12-01"
            xmlns:iso639="http://www.xbrl.org/2005/iso639"
            xmlns:gl-taf="http://www.xbrl.org/int/gl/taf/2016-12-01"
            xmlns:gl-muc="http://www.xbrl.org/int/gl/muc/2016-12-01"
            xmlns:xbrli="http://www.xbrl.org/2003/instance"
            xmlns:gl-srcd="http://www.xbrl.org/int/gl/srcd/2016-12-01"
            xmlns:gl-bus="http://www.xbrl.org/int/gl/bus/2016-12-01" xmlns:xlink="http://www.w3.org/1999/xlink">
  <xbrll:schemaRef xlink:type="simple" xlink:href="../../../XBRL-GL-PWD-2016-12-01-fi-2018-11-06/gl/plt/case-c-b-m-u-t-s-r/gl-plt-fi-all-2017-01-01.xsd" xlink:arcrole="http://www.w3.org/1999/xlink/properties/linkbase"/>
    <!--Link to account mappings doc between the nordic countries to facilitate automated account postings in the NSG reference implementation-->
<xsl:variable name="ac_map" select="document('account_mapping.xml')"/> 

<!--link to Universal product codes - VAT rates mappings (required to calculate the VAT in foreign trade -->
<xsl:variable name="upc_map" select="document('upc_VAT_mapping.xml')"/> 

  <xbrli:context id="now">
    <xbrli:entity>
      <xbrli:identifier scheme="http://www.xbrl.org/xbrlgl/sample">SAMPLE</xbrli:identifier>
    </xbrli:entity>
    <xbrli:period>
      <xbrli:instant><xsl:value-of select="current-date()"/></xbrli:instant>
    </xbrli:period>
  </xbrli:context>
  <xbrli:unit id="NotUsed">
    <xbrli:measure>pure</xbrli:measure>
  </xbrli:unit>
  
  <!--For each found AmountCurrencyIdentifier, create unit-->
<xsl:for-each select="distinct-values(//@currencyID)">
<xsl:variable name="value" select="."/>
<xbrli:unit id="{$value}">
    <xbrli:measure>iso4217:<xsl:value-of select="."/></xbrli:measure>
</xbrli:unit>
</xsl:for-each>

<!--accounting entries-->
<gl-cor:accountingEntries>

<!--document information tuple-->
<gl-cor:documentInfo>
<!--generating entries --><gl-cor:entriesType contextRef="now">entries</gl-cor:entriesType>
<!--current time--><gl-cor:creationDate contextRef="now"><xsl:value-of select="current-date()"/></gl-cor:creationDate>
<!--BT-15--><xsl:variable name="value" select="//cbc:Note"/><xsl:if test="string($value)"><gl-cor:entriesComment contextRef="now"><xsl:value-of select="$value"/></gl-cor:entriesComment></xsl:if>

</gl-cor:documentInfo>

<!--entity information tuple-->
<gl-cor:entityInformation>

<!--Organization Identifiers-->

<gl-bus:organizationIdentifiers>
<!--BT-45--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID"/><xsl:if test="string($value)"><gl-bus:organizationIdentifier contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationIdentifier><gl-bus:organizationDescription contextRef="now">ytunnus</gl-bus:organizationDescription></xsl:if>

</gl-bus:organizationIdentifiers>

<gl-bus:organizationIdentifiers>
<!--BT-46--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID"/><xsl:if test="string($value)"><gl-bus:organizationIdentifier contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationIdentifier><gl-bus:organizationDescription contextRef="now">alvtunnus</gl-bus:organizationDescription></xsl:if>


</gl-bus:organizationIdentifiers>

<!--Organization Address tuple-->
<gl-bus:organizationAddress>

<!--BT-42--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PartyName/cbc:Name"/><xsl:if test="string($value)"><gl-bus:organizationAddressName contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressName><gl-bus:organizationAddressPurpose contextRef="now">billing</gl-bus:organizationAddressPurpose></xsl:if>
<!--BT-47--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:StreetName"/><xsl:if test="string($value)"><gl-bus:organizationAddressStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressStreet></xsl:if>
<!--BT-48--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName"/><xsl:if test="string($value)"><gl-bus:organizationAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressStreet2></xsl:if>
<!--BT-49--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:CityName"/><xsl:if test="string($value)"><gl-bus:organizationAddressCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressCity></xsl:if>
<!--BT-51--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity"/><xsl:if test="string($value)"><gl-bus:organizationAddressStateOrProvince contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressStateOrProvince></xsl:if>
<!--BT-50--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:PostalZone"/><xsl:if test="string($value)"><gl-bus:organizationAddressZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressZipOrPostalCode></xsl:if>
<!--BT-52--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/><xsl:if test="string($value)"><gl-bus:organizationAddressCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressCountry></xsl:if>
</gl-bus:organizationAddress>
<gl-bus:contactInformation>
<!--BT-9--><xsl:variable name="value" select="//Order/cbc:BuyerReference"/>
<!--BT-53--><xsl:variable name="value2" select="//Order/cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Name"/>
<xsl:if test="string($value) or string($value2)"><gl-bus:contactAttentionLine contextRef="now"><xsl:value-of select="concat($value, ', ', $value2)"/></gl-bus:contactAttentionLine></xsl:if>

<gl-bus:contactPhone>
<!--BT-54-->
<xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Telephone"/>
<xsl:if test="string($value)">
<gl-bus:contactPhoneNumber contextRef="now">
<xsl:value-of select="$value"/>
</gl-bus:contactPhoneNumber>
</xsl:if>
</gl-bus:contactPhone>
<gl-bus:contactEMail>
<!--BT-55--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:ElectronicMail"/><xsl:if test="string($value)"><gl-bus:contactEmailAddress contextRef="now"><xsl:value-of select="$value"/></gl-bus:contactEmailAddress></xsl:if>
</gl-bus:contactEMail>
</gl-bus:contactInformation>

</gl-cor:entityInformation>

<gl-cor:entryHeader>
<!--puchase invoice, entry from purchase journal-->
<gl-cor:sourceJournalID contextRef="now">sj</gl-cor:sourceJournalID>
<!--BT-119--><xsl:variable name="value" select="./cbc:Note"/><xsl:if test="string($value)"><gl-cor:entryComment contextRef="now"><xsl:value-of select="$value"/></gl-cor:entryComment></xsl:if>
<!--BT-118--><xsl:variable name="value" select="./cbc:ID"/><xsl:if test="string($value)"><gl-cor:entryNumberCounter contextRef="now" unitRef="NotUsed" decimals="0"><xsl:value-of select="$value"/></gl-cor:entryNumberCounter></xsl:if>

<!--Entry "Header" tuple-->
<gl-cor:entryDetail>

<!-- For NSG 3 reference implementation the buyer's country code serves as a identifier for the standardized chart of accounts to be used (which countrie's CoA) (mainAccountTypeDescription)-->
<!-- For NSG 3 reference implementation default accounts are used on Header type entries where all are considered as entries of the "Trade debtors, short term account"-->

<!--Account tuples-->
<!-- The finnish acount to be used is "17001" (Raportointikoodisto, Standard business reporting code set chart of accounts-->
<gl-cor:account>
<gl-cor:accountMainID contextRef="now">17001</gl-cor:accountMainID><gl-cor:mainAccountTypeDescription contextRef="now">FI</gl-cor:mainAccountTypeDescription>
</gl-cor:account>

<!--For NSG 3 reference implementation. Add other countries accounting references too for the short term trade debtors.-->
<xsl:for-each select="$ac_map//gl-cor:account[gl-cor:mainAccountTypeDescription/text() = 'FI' and gl-cor:accountMainID/text()='17001']/parent::gl-cor:entryDetail/child::gl-cor:account">
<xsl:if test="gl-cor:mainAccountTypeDescription/text() != 'FI'">
 <gl-cor:account>
		 <gl-cor:accountMainID contextRef="now"><xsl:value-of select="./gl-cor:accountMainID/text()"/></gl-cor:accountMainID>
		 <gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="./gl-cor:mainAccountTypeDescription/text()"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>
</xsl:if>
</xsl:for-each>

<!--For the Header type entry the amount is the total VAT included amount for the invoice-->
<!--For the voucher entries to sum up to 0 the Header entries are positive numbers where as the entries per invoice row and the VAT Header are multiplied with *(-1)(negitive numbers). The accounts will be automatically balanced.-->
<!--BT-122--><xsl:variable name="value" select="//cac:AnticipatedMonetaryTotal/cbc:TaxInclusiveAmount"/><xsl:if test="string($value)"><gl-cor:amount contextRef="now" unitRef="{$value/@currencyID}" decimals="2"><xsl:value-of select="$value"/></gl-cor:amount></xsl:if>


<!--Identifier refrence tuples-->
<!--Buyer Party-->
<gl-cor:identifierReference>
<!--BT-43--><xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID"/><xsl:if test="string($value)"><gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode></xsl:if>
<gl-cor:identifierExternalReference>
<xsl:variable name="value" select="/Order/cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID"/>
<xsl:if test="string($value)">
<gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode>
<gl-cor:identifierAuthority contextRef="now">ytunnus</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<xsl:variable name="value" select="/Order/cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID"/>
<xsl:if test="string($value)">
<gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode>
<gl-cor:identifierAuthority contextRef="now">alvtunnus</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>


<!--BT-42--><xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PartyName/cbc:Name"/><xsl:if test="string($value)">
<gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription><gl-cor:identifierType contextRef="now">FI-B</gl-cor:identifierType></xsl:if>

<!--Buyer Address Details-->
<gl-bus:identifierAddress>
<xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:StreetName"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:CityName"/><xsl:if test="string($value)"><gl-bus:identifierCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCity></xsl:if>
<xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:PostalZone"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
</gl-bus:identifierAddress>

</gl-cor:identifierReference>


<!--Seller Party-->
<gl-cor:identifierReference>
<!--BT-27-->
<xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID"/>
<xsl:if test="string($value)"><gl-cor:identifierExternalReference>
<gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode></gl-cor:identifierExternalReference>
</xsl:if>

<gl-cor:identifierExternalReference>
 <!--BT-28-->
<xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID"/>
<xsl:if test="string($value)">
<gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode>
<gl-cor:identifierAuthority contextRef="now">ytunnus</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>

<gl-cor:identifierExternalReference>
 <!--BT-30--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority contextRef="now">alvtunnus</gl-cor:identifierAuthority></xsl:if>

</gl-cor:identifierExternalReference>
<!--SellerAccountDetails-->

<gl-cor:identifierExternalReference>
<!--BT-81--><xsl:variable name="value" select="//Order/cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:ID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority contextRef="now">IBAN</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<!--BT-82--><xsl:variable name="value" select="//Order/cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cbc:ID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority contextRef="now">BIC</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<!--TODO Tyypitys FB authority pitää miettiä uudelleen-->
<!--BT-83--><xsl:variable name="value" select="//Order/cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cbc:ID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority contextRef="now">FB</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>


<xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID/Order/cac:SellerSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:ID"/><xsl:if test="string($value)">
<gl-cor:identifierExternalReference>
<!--BT-31--><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode>
</gl-cor:identifierExternalReference>
</xsl:if>


<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>

<!--BT-25--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>


<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-S</gl-cor:identifierType></xsl:if>

<!--Seller Address Details-->
<gl-bus:identifierAddress>
<!--BT-33--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:StreetName"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!--BT-34--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<!--BT-35--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:CityName"/><xsl:if test="string($value)"><gl-bus:identifierCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCity></xsl:if>
<!--BT-38--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!--BT-36--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:PostalZone"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
</gl-bus:identifierAddress>
<!--Seller contact information-->
<gl-cor:identifierContactInformationStructure>
<!--BT-39--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/Contact/cbc:Name"/><xsl:if test="string($value)"><gl-cor:identifierContactAttentionLine contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactAttentionLine></xsl:if>
<gl-cor:identifierContactPhone>
<!--BT-40--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/Contact/cbc:Telephone"/><xsl:if test="string($value)"><gl-cor:identifierContactPhoneNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactPhoneNumber></xsl:if>
</gl-cor:identifierContactPhone>
<gl-cor:identifierContactEmail>
<!--BT-41--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/Contact/cbc:ElectronicMail"/><xsl:if test="string($value)"><gl-cor:identifierContactEmailAddress contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactEmailAddress></xsl:if>
</gl-cor:identifierContactEmail>
</gl-cor:identifierContactInformationStructure>
</gl-cor:identifierReference>

<!--end of Seller Party-->

<!--Payee party details -->
<gl-cor:identifierReference>
<!--BT-57--><xsl:variable name="value" select="//Order/cac:PayeeParty/cac:PartyIdentification/cbc:ID"/><xsl:if test="string($value)"><gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode></xsl:if>

<gl-cor:identifierExternalReference>
 <!--BT-58--><xsl:variable name="value" select="//Order/cac:PayeeParty/cac:PartyLegalEntity/cbc:CompanyID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority contextRef="now">ytunnus</gl-cor:identifierAuthority></xsl:if>

</gl-cor:identifierExternalReference>
<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>
<!--BT-56--><xsl:variable name="value" select="//Order/cac:PayeeParty/cac:PartyName/cbc:Name"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>
<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-P</gl-cor:identifierType></xsl:if>
</gl-cor:identifierReference>
<!--End of Payee Party-->



<!--TaxRepresentativeParty Details-->
<gl-cor:identifierReference>
<!--BT-59--><xsl:variable name="value" select="//Order/cac:TaxRepresentativeParty/cac:PartyName/cbc:Name"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>
<gl-cor:identifierExternalReference>
 <!--BT-60--><xsl:variable name="value" select="//Order/cac:TaxRepresentativeParty/cac:PartyTaxScheme/cbc:CompanyID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority contextRef="now">alvtunnus</gl-cor:identifierAuthority></xsl:if>

</gl-cor:identifierExternalReference>

<!--Any Party Address Details-->
<gl-bus:identifierAddress>
<!--BT-61--><xsl:variable name="value" select="//Order/cac:TaxRepresentativeParty/cac:PostalAddress/cbc:Streetname"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!--BT-62--><xsl:variable name="value" select="//Order/cac:TaxRepresentativeParty/cac:PostalAddress/cac:AddressLine/cbc:Line"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<!--BT-63--><xsl:variable name="value" select="//Order/cac:TaxRepresentativeParty/cac:PostalAddress/cbc:CityName"/><xsl:if test="string($value)"><gl-bus:identifierCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCity></xsl:if>

<!--BT-66--><xsl:variable name="value" select="//Order/cac:TaxRepresentativeParty/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!--BT-64--><xsl:variable name="value" select="//Order/cac:TaxRepresentativeParty/cac:PostalAddress/cbc:PostalZone"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>

</gl-bus:identifierAddress>
<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">O</gl-cor:identifierType></xsl:if>
</gl-cor:identifierReference>

<!--DeliveryParty Details-->

<gl-cor:identifierReference>
<!--BT-67--><xsl:variable name="value" select="//Order/cac:Delivery/cac:DeliveryParty/cac:PartyName/cbc:Name"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>
<!--Delivery Address Details-->
<gl-bus:identifierAddress>
<!--BT-70--><xsl:variable name="value" select="//Order/cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:StreetName"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!--BT-71--><xsl:variable name="value" select="//Order/cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AdditionalStreetName"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<!--BT-72--><xsl:variable name="value" select="//Order/cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CityName"/><xsl:if test="string($value)"><gl-bus:identifierCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCity></xsl:if>
<!--BT-75--><xsl:variable name="value" select="//Order/cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:IdentificationCode"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!--BT-73--><xsl:variable name="value" select="//Order/cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PostalZone"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>

<!--BT-68--><xsl:variable name="value" select="//Order/cac:Delivery/cac:DeliveryLocation/cbc:ID"/><xsl:if test="string($value)"><gl-bus:identifierAddressLocationIdentifier contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressLocationIdentifier></xsl:if>
</gl-bus:identifierAddress>
<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-D</gl-cor:identifierType></xsl:if>
</gl-cor:identifierReference>

<!--Country of origin-->
<gl-cor:identifierReference>
<xsl:variable name="value" select="./cac:Item/OriginCountry/cbc:IdentificationCode"/>
<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">O</gl-cor:identifierType></xsl:if>
<!--Country of origin Address Details-->
<gl-bus:identifierAddress>
<!--BT-125--><xsl:variable name="value" select="./cac:Item/OriginCountry/cbc:IdentificationCode"/><xsl:if test="string($value)"><gl-bus:identifierAddressPurpose contextRef="now">o</gl-bus:identifierAddressPurpose><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
</gl-bus:identifierAddress>
</gl-cor:identifierReference>

<!-- Original document is an invoice -->
<!--BT-3--><xsl:variable name="value" select="//Order/cbc:InvoiceTypeCode"/><xsl:if test="string($value)"><gl-cor:documentType contextRef="now">invoice</gl-cor:documentType></xsl:if>
<!--BT-1-->
<!--
<xsl:variable name="value" select="//Order/cbc:ID"/><xsl:if test="string($value)"><gl-cor:documentNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentNumber></xsl:if>
-->

<!--BT-76--><xsl:variable name="value" select="//Order/cac:PaymentMeans/cbc:PaymentID"/><xsl:if test="string($value)"><gl-cor:documentReference contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentReference></xsl:if>

<!--BT-2--><xsl:variable name="value" select="//Order/cbc:IssueDate"/><xsl:if test="string($value)"><gl-cor:documentDate contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentDate></xsl:if>
<!--TODO vapauta kun tyypistys on laadittu, huom saattaa olla väärässä paikassa-->
<!--<xsl:variable name="value" select="//InvoiceTypeCode"/><xsl:if test="string($value)"><gl-cor:invoiceType contextRef="now"><xsl:value-of select="$value"/></gl-cor:invoiceType></xsl:if>-->




<!--BT-77--><xsl:variable name="value" select="//Order/cac:PaymentMeans/cbc:PaymentMeansCode"/><xsl:if test="string($value)"><gl-bus:paymentMethod contextRef="now"><xsl:value-of select="$value"/></gl-bus:paymentMethod></xsl:if>

<!--For the NSG reference implementation a "Header" type entry is created for the total amount for trade debtors / creditors-->
<gl-cor:detailComment contextRef="now">Header</gl-cor:detailComment>

<!--BT-69--><xsl:variable name="value" select="//Order/cac:Delivery/cbc:ActualDeliveryDate"/><xsl:if test="string($value)"><gl-cor:shipReceivedDate contextRef="now"><xsl:value-of select="$value"/></gl-cor:shipReceivedDate></xsl:if>

<!--BT-7--><xsl:variable name="value" select="//Order/cbc:InvoiceDueDate"/><xsl:if test="string($value)"><gl-cor:maturityDate contextRef="now"><xsl:value-of select="$value"/></gl-cor:maturityDate></xsl:if>

<!--BT-18--><xsl:variable name="value" select="//Order/cac:PaymentTerms/cbc:Note"/><xsl:if test="string($value)"><gl-cor:terms contextRef="now"><xsl:value-of select="$value"/></gl-cor:terms></xsl:if>

<xsl:variable name="value" select="//Order/cac:BillingReference/cac:InvoiceDocumentReference/cbc:ID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">credit-note</gl-taf:originatingDocumentType>
<!--BT-21--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
<xsl:variable name="value" select="//Order/cac:BillingReference/cac:InvoiceDocumentReference/cbc:IssueDate"/><xsl:if test="string($value)"><gl-taf:originatingDocumentDate contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentDate></xsl:if>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="//Order/cac:ContractDocumentReference/cbc:ID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">contract</gl-taf:originatingDocumentType>
<!--BT-10--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="//Order/cac:OrderReference/cbc:ID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<!--BT-11--><gl-taf:originatingDocumentType contextRef="now">order-customer</gl-taf:originatingDocumentType><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber></gl-taf:originatingDocumentStructure></xsl:if>


<!--BT-12--><xsl:variable name="value" select="//Order/cac:OrderReference/cbc:SalesOrderID"/><xsl:if test="string($value)"><gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">order-vendor</gl-taf:originatingDocumentType><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber></gl-taf:originatingDocumentStructure></xsl:if>

<!--BT-14--><xsl:variable name="value" select="//Order/cac:DespatchDocumentReference/cbc:ID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">despatch-advice</gl-taf:originatingDocumentType>
<gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<!--BT-13--><xsl:variable name="value" select="//Order/cac:ReceiptDocumentReference/cbc:ID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">receive-advice</gl-taf:originatingDocumentType>
<gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="//Order/cac:InvoiceLine/OrderLineReference/cbc:LineID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType>order-customer</gl-taf:originatingDocumentType>
<gl-taf:originatingDocumentNumber><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>


<xsl:variable name="value" select="//cac:PaymentMeans/cbc:PaymentID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentIdentifierCode contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentIdentifierCode>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:for-each select="//Order/cac:AdditionalDocumentReference">
<gl-srcd:richTextComment>
<!--BT-115--><xsl:variable name="value" select="./cbc:DocumentType"/><xsl:if test="string($value)"><gl-srcd:richTextCommentDescription contextRef="now"><xsl:value-of select="$value"/></gl-srcd:richTextCommentDescription></xsl:if>
<!--BT-117--><xsl:variable name="value" select="./cac:Attachment/cbc:EmbeddedDocumentBinaryObject"/><xsl:if test="string($value)"><gl-srcd:richTextCommentContent contextRef="now"><xsl:value-of select="$value"/></gl-srcd:richTextCommentContent></xsl:if>
<!--BT-116--><xsl:variable name="value" select="./cac:Attachment/cac:ExternalReference/cbc:URI"/><xsl:if test="string($value)"><gl-srcd:richTextCommentLocator contextRef="now"><xsl:value-of select="$value"/></gl-srcd:richTextCommentLocator></xsl:if>
</gl-srcd:richTextComment>
</xsl:for-each>

</gl-cor:entryDetail>
<!--Entry Header tuple ends-->


<!--Header VAT entries (created automatically for the NSG reference implementation-->
<xsl:variable name="buyer_cc" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode/text()"/>
<xsl:variable name="seller_cc" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode/text()"/>
<!--For NSG reference implementation the assumption is made that all invoice rows have the same tax category-->
<xsl:variable name="tax_c" select="//cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:ID/text()"/>


<!--creating one entry per tax catecory given in the invoice-->
<xsl:for-each select="//cac:TaxTotal">
<!--Entry Header VAT tuple-->
<gl-cor:entryDetail>
<!-- For NSG 3 reference implementation the buyer's country code serves as a identifier for the standardized chart of accounts to be used (which countrie's CoA) (mainAccountTypeDescription)-->

<!--Account tuples-->
<!-- The finnish account to be used for short term VAT payables is "2920411" (Raportointikoodisto, Standard business reporting code set chart of accounts-->
<gl-cor:account>
<gl-cor:accountMainID contextRef="now">2920411</gl-cor:accountMainID><gl-cor:mainAccountTypeDescription contextRef="now">FI</gl-cor:mainAccountTypeDescription>
</gl-cor:account>

<!--For NSG 3 reference implementation. Add other countries accounting references too for the short term trade VAT payables.-->
<xsl:for-each select="$ac_map//gl-cor:account[gl-cor:mainAccountTypeDescription/text() = 'FI' and gl-cor:accountMainID/text()='2920411']/parent::gl-cor:entryDetail/child::gl-cor:account">
<xsl:if test="gl-cor:mainAccountTypeDescription/text() != 'FI'">
 <gl-cor:account>
		 <gl-cor:accountMainID contextRef="now"><xsl:value-of select="./gl-cor:accountMainID/text()"/></gl-cor:accountMainID>
		 <gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="./gl-cor:mainAccountTypeDescription/text()"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>
</xsl:if>
</xsl:for-each>

<!--For the Header VAT type entry the amount is the total VAT included amount for the invoice-->
<!--BT-122--><xsl:variable name="value" select="./cbc:TaxAmount"/>
<xsl:if test="string($value)"><gl-cor:amount contextRef="now" unitRef="{$value/@currencyID}" decimals="2"><xsl:value-of select="$value*(-1)"/></gl-cor:amount></xsl:if>


<!--Identifier refrence tuples-->
<!--Buyer Party-->
<gl-cor:identifierReference>
<!--BT-43--><xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID"/><xsl:if test="string($value)">
<gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode>
</xsl:if>
<!--BT-42--><xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PartyName/cbc:Name"/><xsl:if test="string($value)">
<gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription><gl-cor:identifierType contextRef="now">FI-B</gl-cor:identifierType></xsl:if>


<!--Buyer Address Details-->
<gl-bus:identifierAddress>
<xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:StreetName"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:CityName"/><xsl:if test="string($value)"><gl-bus:identifierCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCity></xsl:if>
<xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:PostalZone"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
</gl-bus:identifierAddress>
</gl-cor:identifierReference>


<!--Seller Party-->
<gl-cor:identifierReference>
<!--BT-27-->
<xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID"/>
<xsl:if test="string($value)"><gl-cor:identifierExternalReference>
<gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode></gl-cor:identifierExternalReference>
</xsl:if>

<gl-cor:identifierExternalReference>
 <!--BT-28-->
<xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID"/>
<xsl:if test="string($value)">
<gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode>
<gl-cor:identifierAuthority contextRef="now">ytunnus</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>

<gl-cor:identifierExternalReference>
 <!--BT-30--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority contextRef="now">alvtunnus</gl-cor:identifierAuthority></xsl:if>

</gl-cor:identifierExternalReference>
<!--SellerAccountDetails-->

<gl-cor:identifierExternalReference>
<!--BT-81--><xsl:variable name="value" select="//Order/cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:ID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority contextRef="now">IBAN</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<!--BT-82--><xsl:variable name="value" select="//Order/cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cbc:ID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority contextRef="now">BIC</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<!--TODO Tyypitys FB authority pitää miettiä uudelleen-->
<!--BT-83--><xsl:variable name="value" select="//Order/cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cbc:ID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority contextRef="now">FB</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>


<xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID/Order/cac:SellerSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:ID"/><xsl:if test="string($value)">
<gl-cor:identifierExternalReference>
<!--BT-31--><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode>
</gl-cor:identifierExternalReference>
</xsl:if>


<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>

<!--BT-25--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>


<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-S</gl-cor:identifierType></xsl:if>

<!--Seller Address Details-->
<gl-bus:identifierAddress>
<!--BT-33--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:StreetName"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!--BT-34--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<!--BT-35--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:CityName"/><xsl:if test="string($value)"><gl-bus:identifierCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCity></xsl:if>
<!--BT-38--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!--BT-36--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:PostalZone"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
</gl-bus:identifierAddress>
<!--Seller contact information-->
<gl-cor:identifierContactInformationStructure>
<!--BT-39--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/Contact/cbc:Name"/><xsl:if test="string($value)"><gl-cor:identifierContactAttentionLine contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactAttentionLine></xsl:if>
<gl-cor:identifierContactPhone>
<!--BT-40--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/Contact/cbc:Telephone"/><xsl:if test="string($value)"><gl-cor:identifierContactPhoneNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactPhoneNumber></xsl:if>
</gl-cor:identifierContactPhone>
<gl-cor:identifierContactEmail>
<!--BT-41--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/Contact/cbc:ElectronicMail"/><xsl:if test="string($value)"><gl-cor:identifierContactEmailAddress contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactEmailAddress></xsl:if>
</gl-cor:identifierContactEmail>
</gl-cor:identifierContactInformationStructure>
</gl-cor:identifierReference>
<!--end of Seller Party-->

<!-- Original document is an invoice -->
<!--BT-3--><xsl:variable name="value" select="//Order/cbc:InvoiceTypeCode"/><xsl:if test="string($value)"><gl-cor:documentType contextRef="now">invoice</gl-cor:documentType></xsl:if>
<!--BT-1-->
<!--
<xsl:variable name="value" select="//Order/cbc:ID"/><xsl:if test="string($value)"><gl-cor:documentNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentNumber></xsl:if>
-->

<!--BT-76--><xsl:variable name="value" select="//Order/cac:PaymentMeans/cbc:PaymentID"/><xsl:if test="string($value)"><gl-cor:documentReference contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentReference></xsl:if>

<!--BT-2--><xsl:variable name="value" select="//Order/cbc:IssueDate"/><xsl:if test="string($value)"><gl-cor:documentDate contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentDate></xsl:if>
<!--TODO vapauta kun tyypistys on laadittu, huom saattaa olla väärässä paikassa-->
<!--<xsl:variable name="value" select="//InvoiceTypeCode"/><xsl:if test="string($value)"><gl-cor:invoiceType contextRef="now"><xsl:value-of select="$value"/></gl-cor:invoiceType></xsl:if>-->


<!--For the NSG reference implementation a "Header" type entry is created for the total amount for trade debtors / creditors-->
<gl-cor:detailComment contextRef="now">Header VAT</gl-cor:detailComment>


<xsl:variable name="value" select="//Order/cac:BillingReference/cac:InvoiceDocumentReference/cbc:ID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">credit-note</gl-taf:originatingDocumentType>
<!--BT-21--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
<xsl:variable name="value" select="//Order/cac:BillingReference/cac:InvoiceDocumentReference/cbc:IssueDate"/><xsl:if test="string($value)"><gl-taf:originatingDocumentDate contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentDate></xsl:if>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="//Order/cac:ContractDocumentReference/cbc:ID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">contract</gl-taf:originatingDocumentType>
<!--BT-10--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="//Order/cac:OrderReference/cbc:ID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<!--BT-11--><gl-taf:originatingDocumentType contextRef="now">order-customer</gl-taf:originatingDocumentType><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber></gl-taf:originatingDocumentStructure></xsl:if>


<!--BT-12--><xsl:variable name="value" select="//Order/cac:OrderReference/cbc:SalesOrderID"/><xsl:if test="string($value)"><gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">order-vendor</gl-taf:originatingDocumentType><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber></gl-taf:originatingDocumentStructure></xsl:if>

<!--BT-14--><xsl:variable name="value" select="//Order/cac:DespatchDocumentReference/cbc:ID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">despatch-advice</gl-taf:originatingDocumentType>
<gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<!--BT-13--><xsl:variable name="value" select="//Order/cac:ReceiptDocumentReference/cbc:ID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">receive-advice</gl-taf:originatingDocumentType>
<gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="//Order/cac:InvoiceLine/OrderLineReference/cbc:LineID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType>order-customer</gl-taf:originatingDocumentType>
<gl-taf:originatingDocumentNumber><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>


<xsl:variable name="value" select="//cac:PaymentMeans/cbc:PaymentID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentIdentifierCode contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentIdentifierCode>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:for-each select="//Order/cac:AdditionalDocumentReference">
<gl-srcd:richTextComment>
<!--BT-115--><xsl:variable name="value" select="./cbc:DocumentType"/><xsl:if test="string($value)"><gl-srcd:richTextCommentDescription contextRef="now"><xsl:value-of select="$value"/></gl-srcd:richTextCommentDescription></xsl:if>
<!--BT-117--><xsl:variable name="value" select="./cac:Attachment/cbc:EmbeddedDocumentBinaryObject"/><xsl:if test="string($value)"><gl-srcd:richTextCommentContent contextRef="now"><xsl:value-of select="$value"/></gl-srcd:richTextCommentContent></xsl:if>
<!--BT-116--><xsl:variable name="value" select="./cac:Attachment/cac:ExternalReference/cbc:URI"/><xsl:if test="string($value)"><gl-srcd:richTextCommentLocator contextRef="now"><xsl:value-of select="$value"/></gl-srcd:richTextCommentLocator></xsl:if>
</gl-srcd:richTextComment>
</xsl:for-each>

</gl-cor:entryDetail>
</xsl:for-each>
<!--Entry Header VAT tuple ends-->


<!--BG-23-->
<!--one per each InvoiceLine-->
<xsl:for-each select="//Order/cac:OrderLine/cac:LineItem">
<!--Entry Detail tuples-->
<gl-cor:entryDetail>


<!--Account tuples-->
<!-- The finnish account to be used is "3000xx" (Raportointikoodisto, Standard business reporting code set chart of accounts-->
<gl-cor:account>
<gl-cor:accountMainID contextRef="now">3000xx</gl-cor:accountMainID><gl-cor:mainAccountTypeDescription contextRef="now">FI</gl-cor:mainAccountTypeDescription>
</gl-cor:account>

<!--For NSG 3 reference implementation. Add other countries accounting references too for the general sales.-->
<xsl:for-each select="$ac_map//gl-cor:account[gl-cor:mainAccountTypeDescription/text() = 'FI' and gl-cor:accountMainID/text()='3000xx']/parent::gl-cor:entryDetail/child::gl-cor:account">
<xsl:if test="gl-cor:mainAccountTypeDescription/text() != 'FI'">
 <gl-cor:account>
		 <gl-cor:accountMainID contextRef="now"><xsl:value-of select="./gl-cor:accountMainID/text()"/></gl-cor:accountMainID>
		 <gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="./gl-cor:mainAccountTypeDescription/text()"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>
</xsl:if>
</xsl:for-each>


<!--BT-122--><xsl:variable name="value" select="./cbc:LineExtensionAmount"/><xsl:if test="string($value)"><gl-cor:amount contextRef="now" unitRef="{$value/@currencyID}" decimals="2"><xsl:value-of select="$value*(-1)"/></gl-cor:amount></xsl:if>


<gl-cor-fi:priceDetails>
<xsl:variable name="value" select="./cac:Price/cbc:PriceAmount"/><xsl:if test="string($value)"><gl-cor-fi:netPrice contextRef="now" unitRef="{$value/@currencyID}" decimals="2"><xsl:value-of select="$value"/></gl-cor-fi:netPrice></xsl:if>
<xsl:variable name="value" select="./cac:Price/cbc:BaseQuantity"/><xsl:if test="string($value)"><gl-cor-fi:priceBaseQuantity contextRef="now" unitRef="NotUsed" decimals="0"><xsl:value-of select="$value"/></gl-cor-fi:priceBaseQuantity></xsl:if>
<xsl:variable name="value" select="./cac:Price/cbc:BaseQuantity/@unitCode"/><xsl:if test="string($value)"><gl-cor-fi:priceBaseQuantityUnitOfMeasure contextRef="now"><xsl:value-of select="$value"/></gl-cor-fi:priceBaseQuantityUnitOfMeasure></xsl:if>
<xsl:variable name="value" select="./cac:Price/cac:AllowanceCharge/cbc:BaseAmount"/><xsl:if test="string($value)"><gl-cor-fi:grossPrice contextRef="now" unitRef="{$value/@currencyID}" decimals="2"><xsl:value-of select="$value"/></gl-cor-fi:grossPrice></xsl:if>
<xsl:variable name="value" select="./cac:Price/cac:AllowanceCharge/cbc:Amount"/><xsl:if test="string($value)"><gl-cor-fi:priceDiscount contextRef="now" unitRef="{$value/@currencyID}" decimals="2"><xsl:value-of select="$value"/></gl-cor-fi:priceDiscount></xsl:if>
</gl-cor-fi:priceDetails>


<!--BT-8--><xsl:variable name="value" select="//Order/cbc:DocumentCurrencyCode"/><xsl:if test="string($value)"><gl-muc:amountOriginalCurrency contextRef="now"><xsl:value-of select="$value"/></gl-muc:amountOriginalCurrency></xsl:if>




<!--Identifier refrence tuples-->
<!--Buyer Party-->
<gl-cor:identifierReference>
<!--BT-43--><xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID"/><xsl:if test="string($value)">
<gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode>
</xsl:if>
<!--BT-42--><xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PartyName/cbc:Name"/><xsl:if test="string($value)">
<gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription><gl-cor:identifierType contextRef="now">FI-B</gl-cor:identifierType></xsl:if>


<!--Buyer Address Details-->
<gl-bus:identifierAddress>
<xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:StreetName"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:CityName"/><xsl:if test="string($value)"><gl-bus:identifierCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCity></xsl:if>
<xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<xsl:variable name="value" select="//Order/cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:PostalZone"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
</gl-bus:identifierAddress>
</gl-cor:identifierReference>


<!--Seller Party-->
<gl-cor:identifierReference>
<!--BT-27-->
<xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID"/>
<xsl:if test="string($value)"><gl-cor:identifierExternalReference>
<gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode></gl-cor:identifierExternalReference>
</xsl:if>

<gl-cor:identifierExternalReference>
 <!--BT-28-->
<xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID"/>
<xsl:if test="string($value)">
<gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode>
<gl-cor:identifierAuthority contextRef="now">ytunnus</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>

<gl-cor:identifierExternalReference>
 <!--BT-30--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority contextRef="now">alvtunnus</gl-cor:identifierAuthority></xsl:if>

</gl-cor:identifierExternalReference>
<!--SellerAccountDetails-->

<gl-cor:identifierExternalReference>
<!--BT-81--><xsl:variable name="value" select="//Order/cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:ID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority contextRef="now">IBAN</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<!--BT-82--><xsl:variable name="value" select="//Order/cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cbc:ID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority contextRef="now">BIC</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<!--TODO Tyypitys FB authority pitää miettiä uudelleen-->
<!--BT-83--><xsl:variable name="value" select="//Order/cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cbc:ID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority contextRef="now">FB</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>


<xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID/Order/cac:SellerSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:ID"/><xsl:if test="string($value)">
<gl-cor:identifierExternalReference>
<!--BT-31--><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode>
</gl-cor:identifierExternalReference>
</xsl:if>


<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>

<!--BT-25--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>


<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-S</gl-cor:identifierType></xsl:if>

<!--Seller Address Details-->
<gl-bus:identifierAddress>
<!--BT-33--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:StreetName"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!--BT-34--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<!--BT-35--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:CityName"/><xsl:if test="string($value)"><gl-bus:identifierCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCity></xsl:if>
<!--BT-38--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!--BT-36--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:PostalZone"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
</gl-bus:identifierAddress>
<!--Seller contact information-->
<gl-cor:identifierContactInformationStructure>
<!--BT-39--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/Contact/cbc:Name"/><xsl:if test="string($value)"><gl-cor:identifierContactAttentionLine contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactAttentionLine></xsl:if>
<gl-cor:identifierContactPhone>
<!--BT-40--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/Contact/cbc:Telephone"/><xsl:if test="string($value)"><gl-cor:identifierContactPhoneNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactPhoneNumber></xsl:if>
</gl-cor:identifierContactPhone>
<gl-cor:identifierContactEmail>
<!--BT-41--><xsl:variable name="value" select="//Order/cac:SellerSupplierParty/cac:Party/Contact/cbc:ElectronicMail"/><xsl:if test="string($value)"><gl-cor:identifierContactEmailAddress contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactEmailAddress></xsl:if>
</gl-cor:identifierContactEmail>
</gl-cor:identifierContactInformationStructure>
</gl-cor:identifierReference>

<!--end of Seller Party-->

<!--Payee party details -->
<gl-cor:identifierReference>
<!--BT-57--><xsl:variable name="value" select="//Order/cac:PayeeParty/cac:PartyIdentification/cbc:ID"/><xsl:if test="string($value)"><gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode></xsl:if>

<gl-cor:identifierExternalReference>
 <!--BT-58--><xsl:variable name="value" select="//Order/cac:PayeeParty/cac:PartyLegalEntity/cbc:CompanyID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority contextRef="now">ytunnus</gl-cor:identifierAuthority></xsl:if>

</gl-cor:identifierExternalReference>
<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>
<!--BT-56--><xsl:variable name="value" select="//Order/cac:PayeeParty/cac:PartyName/cbc:Name"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>
<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-P</gl-cor:identifierType></xsl:if>
</gl-cor:identifierReference>
<!--End of Payee Party-->



<!--TaxRepresentativeParty Details-->
<gl-cor:identifierReference>
<!--BT-59--><xsl:variable name="value" select="//Order/cac:TaxRepresentativeParty/cac:PartyName/cbc:Name"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>
<gl-cor:identifierExternalReference>
 <!--BT-60--><xsl:variable name="value" select="//Order/cac:TaxRepresentativeParty/cac:PartyTaxScheme/cbc:CompanyID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority contextRef="now">alvtunnus</gl-cor:identifierAuthority></xsl:if>

</gl-cor:identifierExternalReference>

<!--Any Party Address Details-->
<gl-bus:identifierAddress>
<!--BT-61--><xsl:variable name="value" select="//Order/cac:TaxRepresentativeParty/cac:PostalAddress/cbc:Streetname"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!--BT-62--><xsl:variable name="value" select="//Order/cac:TaxRepresentativeParty/cac:PostalAddress/cac:AddressLine/cbc:Line"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<!--BT-63--><xsl:variable name="value" select="//Order/cac:TaxRepresentativeParty/cac:PostalAddress/cbc:CityName"/><xsl:if test="string($value)"><gl-bus:identifierCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCity></xsl:if>

<!--BT-66--><xsl:variable name="value" select="//Order/cac:TaxRepresentativeParty/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!--BT-64--><xsl:variable name="value" select="//Order/cac:TaxRepresentativeParty/cac:PostalAddress/cbc:PostalZone"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>

</gl-bus:identifierAddress>
<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">O</gl-cor:identifierType></xsl:if>
</gl-cor:identifierReference>

<!--DeliveryParty Details-->

<gl-cor:identifierReference>
<!--BT-67--><xsl:variable name="value" select="//Order/cac:Delivery/cac:DeliveryParty/cac:PartyName/cbc:Name"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>
<!--Delivery Address Details-->
<gl-bus:identifierAddress>
<!--BT-70--><xsl:variable name="value" select="//Order/cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:StreetName"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!--BT-71--><xsl:variable name="value" select="//Order/cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AdditionalStreetName"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<!--BT-72--><xsl:variable name="value" select="//Order/cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CityName"/><xsl:if test="string($value)"><gl-bus:identifierCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCity></xsl:if>
<!--BT-75--><xsl:variable name="value" select="//Order/cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:IdentificationCode"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!--BT-73--><xsl:variable name="value" select="//Order/cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PostalZone"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>

<!--BT-68--><xsl:variable name="value" select="//Order/cac:Delivery/cac:DeliveryLocation/cbc:ID"/><xsl:if test="string($value)"><gl-bus:identifierAddressLocationIdentifier contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressLocationIdentifier></xsl:if>
</gl-bus:identifierAddress>
<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-D</gl-cor:identifierType></xsl:if>
</gl-cor:identifierReference>

<!--Country of origin-->
<gl-cor:identifierReference>
<xsl:variable name="value" select="./cac:Item/OriginCountry/cbc:IdentificationCode"/>
<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">O</gl-cor:identifierType></xsl:if>
<!--Country of origin Address Details-->
<gl-bus:identifierAddress>
<!--BT-125--><xsl:variable name="value" select="./cac:Item/OriginCountry/cbc:IdentificationCode"/><xsl:if test="string($value)"><gl-bus:identifierAddressPurpose contextRef="now">o</gl-bus:identifierAddressPurpose><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
</gl-bus:identifierAddress>
</gl-cor:identifierReference>

<!-- Alkuperäinen lähdeasiakirja on lasku -->
<!--BT-3--><xsl:variable name="value" select="//Order/cbc:InvoiceTypeCode"/><xsl:if test="string($value)"><gl-cor:documentType contextRef="now">invoice</gl-cor:documentType></xsl:if>
<!--BT-1-->
<!--
<xsl:variable name="value" select="//Order/cbc:ID"/><xsl:if test="string($value)"><gl-cor:documentNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentNumber></xsl:if>
-->

<!--BT-76--><xsl:variable name="value" select="//Order/cac:PaymentMeans/cbc:PaymentID"/><xsl:if test="string($value)"><gl-cor:documentReference contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentReference></xsl:if>

<!--BT-2--><xsl:variable name="value" select="//Order/cbc:IssueDate"/><xsl:if test="string($value)"><gl-cor:documentDate contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentDate></xsl:if>
<!--TODO vapauta kun tyypistys on laadittu, huom saattaa olla väärässä paikassa-->
<!--<xsl:variable name="value" select="//InvoiceTypeCode"/><xsl:if test="string($value)"><gl-cor:invoiceType contextRef="now"><xsl:value-of select="$value"/></gl-cor:invoiceType></xsl:if>-->


<!--BT-77--><xsl:variable name="value" select="//Order/cac:PaymentMeans/cbc:PaymentMeansCode"/><xsl:if test="string($value)"><gl-bus:paymentMethod contextRef="now"><xsl:value-of select="$value"/></gl-bus:paymentMethod></xsl:if>




<!--BT-69--><xsl:variable name="value" select="//Order/cac:Delivery/cbc:ActualDeliveryDate"/><xsl:if test="string($value)"><gl-cor:shipReceivedDate contextRef="now"><xsl:value-of select="$value"/></gl-cor:shipReceivedDate></xsl:if>

<!--BT-7--><xsl:variable name="value" select="//Order/cbc:InvoiceDueDate"/><xsl:if test="string($value)"><gl-cor:maturityDate contextRef="now"><xsl:value-of select="$value"/></gl-cor:maturityDate></xsl:if>

<!--BT-18--><xsl:variable name="value" select="//Order/cac:PaymentTerms/cbc:Note"/><xsl:if test="string($value)"><gl-cor:terms contextRef="now"><xsl:value-of select="$value"/></gl-cor:terms></xsl:if>


<xsl:variable name="item" select="./cac:Item"/>
<gl-bus:measurable>
<!--BT-148--><xsl:variable name="value" select="./cac:Item/cac:StandardItemIdentification/cbc:ID"/><xsl:if test="string($value)"><gl-bus:measurableID contextRef="now"><xsl:value-of select="$value"/></gl-bus:measurableID></xsl:if>
<!--BT-147--><xsl:variable name="value" select="./cac:Item/cac:SellersItemIdentification/cbc:ID"/><xsl:if test="string($value)"><gl-bus:measurableIDOther contextRef="now"><xsl:value-of select="$value"/></gl-bus:measurableIDOther></xsl:if>
<!--BT-145--><xsl:variable name="value" select="./cac:Item/cbc:Name"/><xsl:if test="string($value)"><gl-bus:measurableDescription contextRef="now"><xsl:value-of select="$value"/></gl-bus:measurableDescription></xsl:if>
<!--BT-120--><xsl:variable name="value" select="./cbc:InvoicedQuantity"/><xsl:if test="string($value)"><gl-bus:measurableQuantity contextRef="now" unitRef="NotUsed" decimals="0"><xsl:value-of select="$value"/></gl-bus:measurableQuantity></xsl:if>
<!--BT-121--><xsl:variable name="value" select="./cbc:InvoicedQuantity/@unitCode"/><xsl:if test="string($value)"><gl-bus:measurableUnitOfMeasure contextRef="now"><xsl:value-of select="$value"/></gl-bus:measurableUnitOfMeasure></xsl:if>
<!--BT-126--><xsl:variable name="value" select="./cac:InvoicePeriod/cbc:StartDate"/><xsl:if test="string($value)"><gl-bus:measurableStartDateTime contextRef="now"><xsl:value-of select="$value"/></gl-bus:measurableStartDateTime></xsl:if>
<!--BT-127--><xsl:variable name="value" select="./cac:InvoicePeriod/cbc:EndDate"/><xsl:if test="string($value)"><gl-bus:measurableEndDateTime contextRef="now"><xsl:value-of select="$value"/></gl-bus:measurableEndDateTime></xsl:if>
</gl-bus:measurable>

<!--BT-156--><xsl:variable name="value" select="./cac:Item/cac:BuyersItemIdentification/cbc:ID"/><xsl:if test="string($value)">
<gl-bus:measurable>
<gl-bus:measurableIDOther contextRef="now"><xsl:value-of select="$value"/></gl-bus:measurableIDOther>
</gl-bus:measurable>
</xsl:if>

<xsl:for-each select="$item/cac:CommodityClassification">
<gl-bus:measurable>
<!--BT-149--><xsl:variable name="value" select="./cbc:ItemClassificationCode"/><xsl:if test="string($value)"><gl-bus:measurableQualifier contextRef="now"><xsl:value-of select="$value"/></gl-bus:measurableQualifier></xsl:if>
</gl-bus:measurable>
</xsl:for-each>

<gl-cor:taxes>
<!--BT-144--><xsl:variable name="value" select="./cac:TaxTotal/cbc:TaxAmount"/><xsl:if test="string($value)"><gl-cor:taxAmount contextRef="now" unitRef="{$value/@currencyID}" decimals="2"><xsl:value-of select="$value"/></gl-cor:taxAmount></xsl:if>
<!--BT-143--><xsl:variable name="value" select="./cac:Item/cac:ClassifiedTaxCategory/cbc:Percent"/><xsl:if test="string($value)"><gl-cor:taxPercentageRate  contextRef="now" unitRef="DKK" decimals="2"><xsl:value-of select="$value"/></gl-cor:taxPercentageRate></xsl:if>
<!--BT-142--><xsl:variable name="value" select="./cac:Item/cac:ClassifiedTaxCategory/cbc:ID"/><xsl:if test="string($value)"><gl-cor:taxCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:taxCode></xsl:if>
<!--BT-144--><xsl:variable name="value" select="./cac:Item/cac:ClassifiedTaxCategory/cbc:TaxExemptionReason"/><xsl:if test="string($value)"><gl-cor:taxCommentExemption contextRef="now"><xsl:value-of select="$value"/></gl-cor:taxCommentExemption></xsl:if>
</gl-cor:taxes>



<xsl:variable name="value" select="//Order/cac:BillingReference/cac:InvoiceDocumentReference/cbc:ID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">credit-note</gl-taf:originatingDocumentType>
<!--BT-21--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
<xsl:variable name="value" select="//Order/cac:BillingReference/cac:InvoiceDocumentReference/cbc:IssueDate"/><xsl:if test="string($value)"><gl-taf:originatingDocumentDate contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentDate></xsl:if>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="//Order/cac:ContractDocumentReference/cbc:ID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">contract</gl-taf:originatingDocumentType>
<!--BT-10--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="//Order/cac:OrderReference/cbc:ID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<!--BT-11--><gl-taf:originatingDocumentType contextRef="now">order-customer</gl-taf:originatingDocumentType><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber></gl-taf:originatingDocumentStructure></xsl:if>


<!--BT-12--><xsl:variable name="value" select="//Order/cac:OrderReference/cbc:SalesOrderID"/><xsl:if test="string($value)"><gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">order-vendor</gl-taf:originatingDocumentType><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber></gl-taf:originatingDocumentStructure></xsl:if>

<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">despatch-advice</gl-taf:originatingDocumentType>
<!--BT-14--><xsl:variable name="value" select="//Order/cac:DespatchDocumentReference/cbc:ID"/><xsl:if test="string($value)"><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber></xsl:if>
</gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">receive-advice</gl-taf:originatingDocumentType>
<!--BT-13--><xsl:variable name="value" select="//Order/cac:ReceiptDocumentReference/cbc:ID"/><xsl:if test="string($value)"><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber></xsl:if>
</gl-taf:originatingDocumentStructure>

<xsl:variable name="value" select="//Order/cac:InvoiceLine/OrderLineReference/cbc:LineID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType>order-customer</gl-taf:originatingDocumentType>
<gl-taf:originatingDocumentNumber><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>


<xsl:variable name="value" select="//cac:PaymentMeans/cbc:PaymentID"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentIdentifierCode contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentIdentifierCode>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:for-each select="//Order/cac:AdditionalDocumentReference">
<gl-srcd:richTextComment>
<!--BT-115--><xsl:variable name="value" select="./cbc:DocumentType"/><xsl:if test="string($value)"><gl-srcd:richTextCommentDescription contextRef="now"><xsl:value-of select="$value"/></gl-srcd:richTextCommentDescription></xsl:if>
<!--BT-117--><xsl:variable name="value" select="./cac:Attachment/cbc:EmbeddedDocumentBinaryObject"/><xsl:if test="string($value)"><gl-srcd:richTextCommentContent contextRef="now"><xsl:value-of select="$value"/></gl-srcd:richTextCommentContent></xsl:if>
<!--BT-116--><xsl:variable name="value" select="./cac:Attachment/cac:ExternalReference/cbc:URI"/><xsl:if test="string($value)"><gl-srcd:richTextCommentLocator contextRef="now"><xsl:value-of select="$value"/></gl-srcd:richTextCommentLocator></xsl:if>


</gl-srcd:richTextComment>
</xsl:for-each>

</gl-cor:entryDetail>
</xsl:for-each>
</gl-cor:entryHeader>




</gl-cor:accountingEntries>
 
</xbrli:xbrl>
</xsl:template>
 
 
 
 
 
</xsl:stylesheet>