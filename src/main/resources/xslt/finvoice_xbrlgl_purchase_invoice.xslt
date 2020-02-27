<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" 
xmlns:fn="http://www.w3.org/2005/02/xpath-functions"
xmlns:iso4217="http://www.xbrl.org/2003/iso4217" 
xmlns:link="http://www.xbrl.org/2003/linkbase"
xmlns:map="http://www.nsg.org/map" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

<!--All namespace declarations reagrding the instance document above-->

<xsl:output method="xml" encoding="utf-8"  indent="yes"/>

<xsl:decimal-format name="dkk" decimal-separator="," grouping-separator="&#160;"/>
<!--
<xsl:decimal-format name='base' decimal-separator=',' grouping-separator='.' minus-sign='-' />
 -->
<!-- string for default namespace uri and schema location -->
  <xsl:variable name="ns" select="'http://www.w3.org/2001/XMLSchema-instance'"/>
  <xsl:variable name="schemaLoc" select="'http://www.xbrl.org/int/gl/plt/2016-12-01 ../../XBRL-GL-REC-2017-01-01-fi_draft/gl/plt/case-c-b-m-u-t-s-r/gl-plt-fi-all-2017-01-01.xsd'"/>

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
<xbrli:xbrl xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.xbrl.org/int/gl/plt/2016-12-01 ../../../XBRL-GL-PWD-2016-12-01-fi-2018-11-06/gl/plt/case-c-b-m-u-t-s-r/gl-plt-fi-all-2017-01-01.xsd" xmlns:xbrll="http://www.xbrl.org/2003/linkbase"   xmlns:iso4217="http://www.xbrl.org/2003/iso4217" xmlns:gl-cor-fi="http://www.xbrl.org/int/gl/cor/fi/2017-01-01" xmlns:gl-plt="http://www.xbrl.org/int/gl/plt/2016-12-01"
            xmlns:gl-cor="http://www.xbrl.org/int/gl/cor/2016-12-01"
            xmlns:iso639="http://www.xbrl.org/2005/iso639"
            xmlns:gl-taf="http://www.xbrl.org/int/gl/taf/2016-12-01"
            xmlns:gl-muc="http://www.xbrl.org/int/gl/muc/2016-12-01"
            xmlns:xbrli="http://www.xbrl.org/2003/instance"
            xmlns:gl-srcd="http://www.xbrl.org/int/gl/srcd/2016-12-01"
            xmlns:gl-rapko="http://www.xbrl.org/int/gl/rapko/2015-07-01"
            xmlns:gl-bus="http://www.xbrl.org/int/gl/bus/2016-12-01" xmlns:xlink="http://www.w3.org/1999/xlink">
  <xbrll:schemaRef xlink:type="simple" xlink:href="../../../XBRL-GL-PWD-2016-12-01-fi-2018-11-06/gl/plt/case-c-b-m-u-t-s-r/gl-plt-fi-all-2017-01-01.xsd" xlink:arcrole="http://www.w3.org/1999/xlink/properties/linkbase"/>
    <!--Link to account mappings doc between the nordic countries to facilitate automated account postings in the NSG reference implementation-->
<xsl:variable name="ac_map" select="document('account_mapping.xml')"/> 

<!--link to product category codes - VAT rates mappings (required to calculate the VAT in foreign trade -->
<xsl:variable name="upc_map" select="document('upc_VAT_mapping.xml')"/> 

<!--link to currency country mapping - required to calculate to amounts in home currency when creating the accounting entries in foreign trade -->
<xsl:variable name="cur_map" select="document('currency_country_mapping.xml')"/> 

<!--getting the currency factor that is used to multiple all the monetary amounts in the accounting entries. In domestic trade the factor shall always be 1 but in foreign trade a simple fixed currency rate mapping doc is provided to calculate the currency transformation.-->
<xsl:variable name="buyer_cc" select="//BuyerPartyDetails/BuyerPostalAddressDetails/CountryCode/text()"/>
<xsl:variable name="seller_cur" select="//InvoiceTotalVatIncludedAmount/@AmountCurrencyIdentifier"/>
<!--[@source=$seller_cur and @targetCountry=$buyer_cc]-->
<xsl:variable name="cur_factor" select="$cur_map//map:rate[@source=$seller_cur and @targetCountry=$buyer_cc][1]"/>
<xsl:variable name="target_cur" select="$cur_map//map:rate[@source=$seller_cur and @targetCountry=$buyer_cc][1]/@targetCurrency"/>
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
  <xbrli:unit id="{$target_cur}">
    <xbrli:measure>iso4217:<xsl:value-of select="$target_cur"/></xbrli:measure>
</xbrli:unit>
  
  <!--For each found AmountCurrencyIdentifier, create unit-->
<xsl:for-each select="distinct-values(//@AmountCurrencyIdentifier[not(.=$target_cur)])">
<xsl:variable name="value" select="."/>
<xbrli:unit id="{$value}">
    <xbrli:measure>iso4217:<xsl:value-of select="."/></xbrli:measure>
</xbrli:unit>
</xsl:for-each>

<!--accounting entries-->
<gl-cor:accountingEntries>

<!--document information tuple-->
<gl-cor:documentInfo>
<!--generating journal entries --><gl-cor:entriesType contextRef="now">entries</gl-cor:entriesType>
<!--current time--><gl-cor:creationDate contextRef="now"><xsl:value-of select="current-date()"/></gl-cor:creationDate>
<gl-bus:creator contextRef="now">NSG reference implementation</gl-bus:creator>
<!--BT-15--><xsl:variable name="value" select="//InvoiceFreeText"/><xsl:if test="string($value)"><gl-cor:entriesComment contextRef="now"><xsl:value-of select="$value"/></gl-cor:entriesComment></xsl:if>
<gl-bus:sourceApplication contextRef="now">NSG reference implementation application</gl-bus:sourceApplication>
<gl-muc:defaultCurrency contextRef="now"><xsl:value-of select="$target_cur"/></gl-muc:defaultCurrency>
</gl-cor:documentInfo>

<!--entity information tuple-->
<gl-cor:entityInformation>


<xsl:variable name="tuple" select="//BuyerPartyDetails"/>
<!--Organization Identifiers tuples-->

<gl-bus:organizationIdentifiers>
<!--BT-45--><xsl:variable name="value" select="$tuple/BuyerPartyIdentifier"/><xsl:if test="string($value)"><gl-bus:organizationIdentifier contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationIdentifier><gl-bus:organizationDescription contextRef="now">ytunnus</gl-bus:organizationDescription></xsl:if>
</gl-bus:organizationIdentifiers>

<gl-bus:organizationIdentifiers>
<!--BT-46--><xsl:variable name="value" select="$tuple/BuyerOrganisationTaxCode"/><xsl:if test="string($value)"><gl-bus:organizationIdentifier contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationIdentifier><gl-bus:organizationDescription contextRef="now">alvtunnus</gl-bus:organizationDescription></xsl:if>

</gl-bus:organizationIdentifiers>

<!--Organization Address tuple-->
<gl-bus:organizationAddress>
<!--BT-48--><xsl:variable name="value" select="$tuple/BuyerPostalAddressDetails/BuyerStreetName[1]"/>
<xsl:if test="string($value)"><gl-bus:organizationAddressStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressStreet></xsl:if>
<!--BT-48--><xsl:variable name="value" select="$tuple/BuyerPostalAddressDetails/BuyerStreetName[2]"/>
<!--BT-48--><xsl:variable name="value2" select="$tuple/BuyerPostalAddressDetails/BuyerStreetName[3]"/>
<xsl:if test="string($value)"><gl-bus:organizationAddressStreet2 contextRef="now"><xsl:value-of select="concat($value, ', ', $value2)"/></gl-bus:organizationAddressStreet2></xsl:if>
<!--BT-49--><xsl:variable name="value" select="$tuple/BuyerPostalAddressDetails/BuyerTownName"/><xsl:if test="string($value)"><gl-bus:organizationAddressCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressCity></xsl:if>
<!--BT-51--><xsl:variable name="value" select="$tuple/BuyerPostalAddressDetails/CountryName"/><xsl:if test="string($value)"><gl-bus:organizationAddressStateOrProvince contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressStateOrProvince></xsl:if>
<!--BT-50--><xsl:variable name="value" select="$tuple/BuyerPostalAddressDetails/BuyerPostCodeIdentifier"/><xsl:if test="string($value)"><gl-bus:organizationAddressZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressZipOrPostalCode></xsl:if>
<!--BT-52--><xsl:variable name="value" select="$tuple/BuyerPostalAddressDetails/CoutryCode"/><xsl:if test="string($value)"><gl-bus:organizationAddressCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressCountry></xsl:if>
</gl-bus:organizationAddress>
<gl-bus:contactInformation>
<!--BT-9--><xsl:variable name="value" select="//BuyerReferenceIdentifier"/><xsl:if test="string($value)"><gl-bus:contactAttentionLine contextRef="now"><xsl:value-of select="$value"/></gl-bus:contactAttentionLine></xsl:if>
<gl-bus:contactPhone>
<!--BT-54--><xsl:variable name="value" select="//InvoiceRecipientPhoneNumberIdentifier"/><xsl:if test="string($value)"><gl-cor:identifierContactPhoneNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactPhoneNumber></xsl:if>
</gl-bus:contactPhone>
<gl-bus:contactEMail>
<!--BT-55--><xsl:variable name="value" select="//InvoiceRecipientEmailAddressIdentifier"/><xsl:if test="string($value)"><gl-cor:identifierContactEmailAddress contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactEmailAddress></xsl:if>
</gl-bus:contactEMail>
</gl-bus:contactInformation>

</gl-cor:entityInformation>




<!--Entry Header tuple-->
<gl-cor:entryHeader>
<gl-cor:enteredBy contextRef="now">NSG example person</gl-cor:enteredBy>
<gl-cor:sourceJournalID contextRef="now">pj</gl-cor:sourceJournalID>
<gl-bus:sourceJournalDescription contextRef="now">journal</gl-bus:sourceJournalDescription>
<gl-cor:entryComment contextRef="now">purchases</gl-cor:entryComment>
<gl-cor:entryDetail>

<!-- For NSG 3 reference implementation the buyer's country code serves as a identifier for the standardized chart of accounts to be used (which countrie's CoA) (mainAccountTypeDescription)-->
<!-- For NSG 3 reference implementation default accounts are used on Header type entries where all are considered as entries of the "Trade creditors, short term account"-->

<!--Account tuples-->
<!-- The finnish acount to be used is "290601" (Raportointikoodisto, Standard business reporting code set chart of accounts-->
<gl-cor:account>
<gl-cor:accountMainID contextRef="now">290601</gl-cor:accountMainID><gl-cor:mainAccountTypeDescription contextRef="now">FI</gl-cor:mainAccountTypeDescription><xsl:comment>See the Finnish chart of accounts here, also in swedish: https://koodistot.suomi.fi/extension;registryCode=sbr-fi-code-lists;schemeCode=MC-2019-1;extensionCode=MC65</xsl:comment>
<xsl:comment>
Swedish standard chart of accounts: https://docs.google.com/spreadsheets/d/1MOZ_AlbkiQqFNCCQDjOucVxsElZgc_5yCKKV_KrrHjg/edit?usp=sharing
Norwegian standard chart of accounts:
https://drive.google.com/file/d/1oPllLYGpmKKPnF1IXHxsHltanrjevWuu/view?usp=sharing
Danish standard chart of accounts:
https://docs.google.com/spreadsheets/d/14KIXWaZEED6UNL4XhSVFtzEwqhCTxNAMJjpalSOyB8A/edit?usp=sharing
Icelandic standard chart of accounts:
https://drive.google.com/open?id=18m-0i6DfcAmV1KKZcp-5XRt8mQq-HEsD
</xsl:comment>
</gl-cor:account>

<!--For NSG 3 reference implementation. Add other countries accounting references too for the short term trade creditors.-->
<xsl:for-each select="$ac_map//gl-cor:account[gl-cor:mainAccountTypeDescription/text() = 'FI' and gl-cor:accountMainID/text()='290601']/parent::gl-cor:entryDetail/child::gl-cor:account">
<xsl:if test="gl-cor:mainAccountTypeDescription/text() != 'FI'">
 <gl-cor:account>
		 <gl-cor:accountMainID contextRef="now"><xsl:value-of select="./gl-cor:accountMainID/text()"/></gl-cor:accountMainID>
		 <gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="./gl-cor:mainAccountTypeDescription/text()"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>
</xsl:if>
</xsl:for-each>


<!--BT-122--><xsl:variable name="value" select="//InvoiceTotalVatIncludedAmount"/><xsl:if test="string($value)"><gl-cor:amount contextRef="now" unitRef="{$target_cur}" decimals="2"><xsl:value-of select="format-number((number(replace($value,',','.'))*(-1)*$cur_factor), '0.00')"/></gl-cor:amount></xsl:if>

<xsl:choose>
  <xsl:when test="not(exists(//DeliveryDate[1]))">
<!--BT-2--><xsl:variable name="value" select="//InvoiceDate"/><xsl:if test="string($value)"><gl-cor:postingDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:postingDate></xsl:if>
  </xsl:when>
  <xsl:otherwise>
    <!--BT-2--><xsl:variable name="value" select="//DeliveryDate[1]"/><xsl:if test="string($value)"><gl-cor:postingDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:postingDate></xsl:if>
  </xsl:otherwise>
</xsl:choose>

<!--Identifier refrence tuples-->
<!--Buyer party-->
<!--BT-43--><xsl:variable name="value" select="//SellersBuyerIdentifier"/><xsl:if test="string($value)">
<gl-cor:identifierReference>
<gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode>
<!--BT-42--><xsl:variable name="value" select="//BuyerPartyDetails/BuyerOrganisationName[1]"/><xsl:if test="string($value)">
<gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription><gl-cor:identifierType contextRef="now">FI-B</gl-cor:identifierType></xsl:if>
</gl-cor:identifierReference>
</xsl:if>

<!--Seller Party-->
<xsl:for-each select="//SellerPartyDetails">
<gl-cor:identifierReference>
<!--BT-27--><xsl:variable name="value" select="./SellerCode"/><xsl:if test="string($value)"><gl-cor:identifierExternalReference><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode></gl-cor:identifierExternalReference></xsl:if>
<gl-cor:identifierExternalReference>
<!--BT-28--><xsl:variable name="value" select="./SellerPartyIdentifier"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">ytunnus</gl-cor:identifierAuthority><xsl:comment>Business ID</xsl:comment></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<!--BT-30--><xsl:variable name="value" select="./SellerOrganisationTaxCode"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">alvtunnus</gl-cor:identifierAuthority><xsl:comment>VAT ID</xsl:comment></xsl:if>
</gl-cor:identifierExternalReference>
<!--SellerAccountDetails-->
<xsl:for-each select="//SellerAccountDetails">
<gl-cor:identifierExternalReference>
<!--BT-81--><xsl:variable name="value" select="./SellerAccountID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">IBAN</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<!--BT-82--><xsl:variable name="value" select="./SellerBic"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">BIC</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
</xsl:for-each>

<xsl:variable name="value" select="//SellerInformationDetails/SellerTaxRegistrationText"/><xsl:if test="string($value)">
<gl-cor:identifierExternalReference>
<!--BT-81--><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode>
</gl-cor:identifierExternalReference>
</xsl:if>

<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>

<!--BT-25--><xsl:variable name="value" select="//SellerPartyDetails/SellerOrganisationName[1]"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>

<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-S</gl-cor:identifierType></xsl:if>

<!--Seller Address Details-->
<gl-bus:identifierAddress>
<!--BT-33--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerStreetName[1]"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!--BT-34--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerStreetName[2]"/>
<!--BT-34--><xsl:variable name="value2" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerStreetName[3]"/>
<xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="concat($value, ', ', $value2)"/></gl-bus:identifierAddressStreet2></xsl:if>
<!--BT-35--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerTownName"/><xsl:if test="string($value)"><gl-bus:identifierCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCity></xsl:if>
<!--BT-38--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/CountryCode"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!--BT-36--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerPostCodeIdentifier"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
</gl-bus:identifierAddress>
<!--Seller contact information-->
<gl-cor:identifierContactInformationStructure>
<!--BT-39--><xsl:variable name="value" select="//SellerContactPersonName"/><xsl:if test="string($value)"><gl-cor:identifierContactAttentionLine contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactAttentionLine></xsl:if>
<gl-cor:identifierContactPhone>
<!--BT-40--><xsl:variable name="value" select="//SellerPhoneNumberIdentifier"/><xsl:if test="string($value)"><gl-cor:identifierContactPhoneNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactPhoneNumber></xsl:if>
</gl-cor:identifierContactPhone>
<gl-cor:identifierContactEmail>
<!--BT-41--><xsl:variable name="value" select="//SellerEmailAddressIdentifier"/><xsl:if test="string($value)"><gl-cor:identifierContactEmailAddress contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactEmailAddress></xsl:if>
</gl-cor:identifierContactEmail>
</gl-cor:identifierContactInformationStructure>
</gl-cor:identifierReference>
</xsl:for-each>
<!--end of Seller Party-->

<!--Payee party details (EpiDetails in Finvoice)-->
<xsl:for-each select="//EpiPartyDetails">
<gl-cor:identifierReference>
<gl-cor:identifierExternalReference>
<!--BT-58--><xsl:variable name="value" select="//EpiBei"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">ytunnus</gl-cor:identifierAuthority><xsl:comment>Business ID</xsl:comment></xsl:if>
</gl-cor:identifierExternalReference>
<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>
<!--BT-56--><xsl:variable name="value" select="//EpiNameAddressDetails"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>

<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-P</gl-cor:identifierType></xsl:if>
</gl-cor:identifierReference>
</xsl:for-each>
<!--End of Payee Party-->

<!--Any Party Details-->
<xsl:for-each select="//AnyPartyDetails">
<gl-cor:identifierReference>
<gl-cor:identifierExternalReference>
<!--BT-60--><xsl:variable name="value" select="./AnyPartyOrganisationTaxCode"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">alvtunnus</gl-cor:identifierAuthority><xsl:comment>VAT ID</xsl:comment></xsl:if>
</gl-cor:identifierExternalReference>
<!--BT-59--><xsl:variable name="value" select="./AnyPartyOrganisationName[1]"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>
<gl-cor:identifierType contextRef="now">O</gl-cor:identifierType>
<!--Any Party Address Details-->
<gl-bus:identifierAddress>
<!--BT-61--><xsl:variable name="value" select="./AnyPartyPostalAddressDetails/AnyPartyStreetName"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!--BT-62--><xsl:variable name="value" select="./AnyPartyPostalAddressDetails/AnyPartyStreetName"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<!--BT-63--><xsl:variable name="value" select="./AnyPartyPostalAddressDetails/AnyPartyTownName"/><xsl:if test="string($value)"><gl-bus:identifierCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCity></xsl:if>
<!--BT-64--><xsl:variable name="value" select="./AnyPartyPostalAddressDetails/AnyPartyPostCodeIdentifier"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
<!--BT-66--><xsl:variable name="value" select="./AnyPartyPostalAddressDetails/CountryCode"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
</gl-bus:identifierAddress>
</gl-cor:identifierReference>
</xsl:for-each>

<!--Delivery Details-->
<xsl:for-each select="//DeliveryPartyDetailsType">
<gl-cor:identifierReference>
<!--BT-59--><xsl:variable name="value" select="./DeliveryOrganisationName[1]"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>
<!--Delivery Address Details-->
<gl-bus:identifierAddress>
<!--BT-70--><xsl:variable name="value" select="./DeliveryPostalAddressDetails/DeliveryStreetName"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!--BT-71--><xsl:variable name="value" select="./DeliveryPostalAddressDetails/DeliveryStreetName"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<!--BT-72--><xsl:variable name="value" select="//DeliveryTownName"/><xsl:if test="string($value)"><gl-bus:identifierCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCity></xsl:if>
<!--BT-73--><xsl:variable name="value" select="./DeliveryPostalAddressDetails/DeliveryPostCodeIdentifier"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
<!--BT-75--><xsl:variable name="value" select="./DeliveryPostalAddressDetails/CountryCode"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!--BT-68--><xsl:variable name="value" select="//DeliverySiteCode"/><xsl:if test="string($value)"><gl-bus:identifierAddressLocationIdentifier contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressLocationIdentifier></xsl:if>
</gl-bus:identifierAddress>
<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-D</gl-cor:identifierType></xsl:if>
</gl-cor:identifierReference>
</xsl:for-each>

<!--Country of origin-->
<xsl:variable name="value" select="//CNOriginCountryCode"/>
<xsl:if test="string($value)">
<gl-cor:identifierReference>
<gl-cor:identifierType contextRef="now">O</gl-cor:identifierType>
<!--Country of origin Address Details-->
<gl-bus:identifierAddress>
<!--BT-125--><xsl:if test="string($value)"><gl-bus:identifierAddressPurpose contextRef="now">o</gl-bus:identifierAddressPurpose><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
</gl-bus:identifierAddress>
</gl-cor:identifierReference>
</xsl:if>


<!-- Alkuperäinen lähdeasiakirja on lasku -->
<!--BT-1--><xsl:variable name="value" select="//InvoiceNumber"/><xsl:if test="string($value)">
<gl-cor:documentType contextRef="now">invoice</gl-cor:documentType></xsl:if>
<xsl:variable name="value" select="//InvoiceTypeCode"/><xsl:if test="string($value)">
<gl-cor:invoiceType contextRef="now"><xsl:value-of select="$value"/></gl-cor:invoiceType></xsl:if>
<xsl:variable name="value" select="//InvoiceNumber"/><xsl:if test="string($value)">
<!--
<gl-cor:documentNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentNumber>
-->
</xsl:if>

<!--BT-76--><xsl:variable name="value" select="//EpiRemittanceInfoIdentifier"/><xsl:if test="string($value)"><gl-cor:documentReference contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentReference></xsl:if>

<!--BT-2--><xsl:variable name="value" select="//InvoiceDate"/><xsl:if test="string($value)"><gl-cor:documentDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:documentDate></xsl:if>

<gl-cor:detailComment contextRef="now">Header</gl-cor:detailComment>

<!--BT-69--><xsl:variable name="value" select="//DeliveryDate"/><xsl:if test="string($value)"><gl-cor:shipReceivedDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:shipReceivedDate></xsl:if>


<!--BT-7--><xsl:variable name="value" select="//InvoiceDueDate"/><xsl:if test="string($value)"><gl-cor:maturityDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:maturityDate></xsl:if>

<!--BT-18--><xsl:variable name="value" select="concat(//PaymentTermsFreeText[1], ',',//PaymentTermsFreeText[2])"/><xsl:if test="string($value)!=','"><gl-cor:terms contextRef="now"><xsl:value-of select="$value"/></gl-cor:terms></xsl:if>

<xsl:variable name="value" select="//OriginalInvoiceNumber"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">credit-note</gl-taf:originatingDocumentType>
<!--BT-21--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="//AgreementIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">contract</gl-taf:originatingDocumentType>
<!--BT-10--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="//OrderIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">order-customer</gl-taf:originatingDocumentType>
<!--BT-11--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>


<xsl:variable name="value" select="//OrderConfirmationIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">order-vendor</gl-taf:originatingDocumentType>
<!--BT-12--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>



<!--BT-14--><xsl:variable name="value" select="//DeliveryNoteIdentifier"/><xsl:if test="string($value)"><gl-taf:originatingDocumentStructure><gl-taf:originatingDocumentType contextRef="now">despatch-advice</gl-taf:originatingDocumentType><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber></gl-taf:originatingDocumentStructure></xsl:if>


<xsl:variable name="value" select="//EpiRemittanceInfoIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">finance-charge</gl-taf:originatingDocumentType>
<gl-taf:originatingDocumentIdentifierCode contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentIdentifierCode>
</gl-taf:originatingDocumentStructure>
</xsl:if>

</gl-cor:entryDetail>



<xsl:variable name="seller_cc" select="//SellerPartyDetails/SellerPostalAddressDetails/CountryCode/text()"/>
<!--For NSG reference implementation the assumption is made that all invoice rows have the same tax category-->
<xsl:variable name="tax_c" select="//InvoiceDetails/VatSpecificationDetails[1]/VatCode/text()"/>

<!--Entry Header VAT tuple-->
<!-- NSG POC Header VAT rowa-->
<!--CASE1: Vat category = 'S' (standard rate), domestic sales-->
<xsl:if test="$buyer_cc=$seller_cc and $tax_c='S'">
<!--creating one entry per tax catecory given in the invoice-->
<xsl:for-each select="//VatSpecificationDetails">
<gl-cor:entryDetail>
<!-- For NSG 3 reference implementation the buyer's country code serves as a identifier for the standardized chart of accounts to be used (which countrie's CoA) (mainAccountTypeDescription)-->
<!--Account tuples-->
<!-- The finnish account to be used for short term VAT payables is "17621" (Raportointikoodisto, Standard business reporting code set chart of accounts-->
<gl-cor:account>
<gl-cor:accountMainID contextRef="now">17621</gl-cor:accountMainID><gl-cor:mainAccountTypeDescription contextRef="now">FI</gl-cor:mainAccountTypeDescription><xsl:comment>See the Finnish chart of accounts here, also in swedish: https://koodistot.suomi.fi/extension;registryCode=sbr-fi-code-lists;schemeCode=MC-2019-1;extensionCode=MC65</xsl:comment>
<xsl:comment>
Swedish standard chart of accounts: https://docs.google.com/spreadsheets/d/1MOZ_AlbkiQqFNCCQDjOucVxsElZgc_5yCKKV_KrrHjg/edit?usp=sharing
Norwegian standard chart of accounts:
https://drive.google.com/file/d/1oPllLYGpmKKPnF1IXHxsHltanrjevWuu/view?usp=sharing
Danish standard chart of accounts:
https://docs.google.com/spreadsheets/d/14KIXWaZEED6UNL4XhSVFtzEwqhCTxNAMJjpalSOyB8A/edit?usp=sharing
Icelandic standard chart of accounts:
https://drive.google.com/open?id=18m-0i6DfcAmV1KKZcp-5XRt8mQq-HEsD
</xsl:comment>
</gl-cor:account>

<!--For NSG 3 reference implementation. Add other countries accounting references too for the short term trade VAT receivables.-->
<xsl:for-each select="$ac_map//gl-cor:account[gl-cor:mainAccountTypeDescription/text() = 'FI' and gl-cor:accountMainID/text()='17621']/parent::gl-cor:entryDetail/child::gl-cor:account">
<xsl:if test="gl-cor:mainAccountTypeDescription/text() != 'FI'">
 <gl-cor:account>
		 <gl-cor:accountMainID contextRef="now"><xsl:value-of select="./gl-cor:accountMainID/text()"/></gl-cor:accountMainID>
		 <gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="./gl-cor:mainAccountTypeDescription/text()"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>
</xsl:if>
</xsl:for-each>

<!--For the Header VAT type entry the amount is the total VAT included amount for the invoice-->
<!--For the voucher entries to sum up to 0 the Header entries are multiplied with *(-1) where as the entries per invoice row and the VAT Header are positive numbers. The accounts will be automatically balanced.-->
<!--BT-122--><xsl:variable name="value" select="./VatRateAmount"/><xsl:if test="string($value)"><gl-cor:amount contextRef="now" unitRef="{$target_cur}" decimals="2"><xsl:value-of select="format-number((number(replace($value,',','.')) * $cur_factor), '0.00')"/></gl-cor:amount></xsl:if>

<xsl:choose>
  <xsl:when test="not(exists(//DeliveryDate[1]))">
<!--BT-2--><xsl:variable name="value" select="//InvoiceDate"/><xsl:if test="string($value)"><gl-cor:postingDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:postingDate></xsl:if>
  </xsl:when>
  <xsl:otherwise>
    <!--BT-2--><xsl:variable name="value" select="//DeliveryDate[1]"/><xsl:if test="string($value)"><gl-cor:postingDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:postingDate></xsl:if>
  </xsl:otherwise>
</xsl:choose>

<!--Identifier refrence tuples-->
<!--Buyer party-->
<!--BT-43--><xsl:variable name="value" select="//SellersBuyerIdentifier"/><xsl:if test="string($value)">
<gl-cor:identifierReference>
<gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode>
<!--BT-42--><xsl:variable name="value" select="//BuyerPartyDetails/BuyerOrganisationName[1]"/><xsl:if test="string($value)">
<gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription><gl-cor:identifierType contextRef="now">FI-B</gl-cor:identifierType></xsl:if>
</gl-cor:identifierReference>
</xsl:if>

<!--Seller Party-->
<xsl:for-each select="//SellerPartyDetails">
<gl-cor:identifierReference>
<!--BT-27--><xsl:variable name="value" select="./SellerCode"/><xsl:if test="string($value)"><gl-cor:identifierExternalReference><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode></gl-cor:identifierExternalReference></xsl:if>
<gl-cor:identifierExternalReference>
<!--BT-28--><xsl:variable name="value" select="./SellerPartyIdentifier"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">ytunnus</gl-cor:identifierAuthority><xsl:comment>Business ID</xsl:comment></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<!--BT-30--><xsl:variable name="value" select="./SellerOrganisationTaxCode"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">alvtunnus</gl-cor:identifierAuthority><xsl:comment>VAT ID</xsl:comment></xsl:if>
</gl-cor:identifierExternalReference>
<!--SellerAccountDetails-->
<xsl:for-each select="//SellerAccountDetails">
<gl-cor:identifierExternalReference>
<!--BT-81--><xsl:variable name="value" select="./SellerAccountID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">IBAN</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<!--BT-82--><xsl:variable name="value" select="./SellerBic"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">BIC</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
</xsl:for-each>

<xsl:variable name="value" select="//SellerInformationDetails/SellerTaxRegistrationText"/><xsl:if test="string($value)">
<gl-cor:identifierExternalReference>
<!--BT-81--><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode>
</gl-cor:identifierExternalReference>
</xsl:if>

<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>

<!--BT-25--><xsl:variable name="value" select="//SellerPartyDetails/SellerOrganisationName[1]"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>

<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-S</gl-cor:identifierType></xsl:if>

<!--Seller Address Details-->
<gl-bus:identifierAddress>
<!--BT-33--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerStreetName[1]"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!--BT-34--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerStreetName[2]"/>
<!--BT-34--><xsl:variable name="value2" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerStreetName[3]"/>
<xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="concat($value, ', ', $value2)"/></gl-bus:identifierAddressStreet2></xsl:if>
<!--BT-35--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerTownName"/><xsl:if test="string($value)"><gl-bus:identifierCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCity></xsl:if>
<!--BT-38--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/CountryCode"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!--BT-36--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerPostCodeIdentifier"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
</gl-bus:identifierAddress>
<!--Seller contact information-->
<gl-cor:identifierContactInformationStructure>
<!--BT-39--><xsl:variable name="value" select="//SellerContactPersonName"/><xsl:if test="string($value)"><gl-cor:identifierContactAttentionLine contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactAttentionLine></xsl:if>
<gl-cor:identifierContactPhone>
<!--BT-40--><xsl:variable name="value" select="//SellerPhoneNumberIdentifier"/><xsl:if test="string($value)"><gl-cor:identifierContactPhoneNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactPhoneNumber></xsl:if>
</gl-cor:identifierContactPhone>
<gl-cor:identifierContactEmail>
<!--BT-41--><xsl:variable name="value" select="//SellerEmailAddressIdentifier"/><xsl:if test="string($value)"><gl-cor:identifierContactEmailAddress contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactEmailAddress></xsl:if>
</gl-cor:identifierContactEmail>
</gl-cor:identifierContactInformationStructure>
</gl-cor:identifierReference>
</xsl:for-each>
<!--end of Seller Party-->

<!--Payee party details (EpiDetails in Finvoice)-->
<xsl:for-each select="//EpiPartyDetails">
<gl-cor:identifierReference>
<gl-cor:identifierExternalReference>
<!--BT-58--><xsl:variable name="value" select="//EpiBei"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">ytunnus</gl-cor:identifierAuthority><xsl:comment>Business ID</xsl:comment></xsl:if>
</gl-cor:identifierExternalReference>
<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>
<!--BT-56--><xsl:variable name="value" select="//EpiNameAddressDetails"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>

<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-P</gl-cor:identifierType></xsl:if>
</gl-cor:identifierReference>
</xsl:for-each>
<!--End of Payee Party-->



<!-- Alkuperäinen lähdeasiakirja on lasku -->
<!--BT-1--><xsl:variable name="value" select="//InvoiceNumber"/><xsl:if test="string($value)">
<gl-cor:documentType contextRef="now">invoice</gl-cor:documentType></xsl:if>
<xsl:variable name="value" select="//InvoiceTypeCode"/><xsl:if test="string($value)">
<gl-cor:invoiceType contextRef="now"><xsl:value-of select="$value"/></gl-cor:invoiceType></xsl:if>
<xsl:variable name="value" select="//InvoiceNumber"/><xsl:if test="string($value)">
<!--
<gl-cor:documentNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentNumber>
-->
</xsl:if>

<!--BT-76--><xsl:variable name="value" select="//EpiRemittanceInfoIdentifier"/><xsl:if test="string($value)"><gl-cor:documentReference contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentReference></xsl:if>

<!--BT-2--><xsl:variable name="value" select="//InvoiceDate"/><xsl:if test="string($value)"><gl-cor:documentDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:documentDate></xsl:if>

<gl-cor:detailComment contextRef="now">Header VAT</gl-cor:detailComment>

<!--BT-69--><xsl:variable name="value" select="//DeliveryDate"/><xsl:if test="string($value)"><gl-cor:shipReceivedDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:shipReceivedDate></xsl:if>


<!--BT-7--><xsl:variable name="value" select="//InvoiceDueDate"/><xsl:if test="string($value)"><gl-cor:maturityDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:maturityDate></xsl:if>

<!--BT-18--><xsl:variable name="value" select="concat(//PaymentTermsFreeText[1], ',',//PaymentTermsFreeText[2])"/><xsl:if test="string($value)!=','"><gl-cor:terms contextRef="now"><xsl:value-of select="$value"/></gl-cor:terms></xsl:if>

<gl-cor:taxes>
<!--BT-144--><xsl:variable name="value" select="./VatRateAmount"/><xsl:if test="string($value)"><gl-cor:taxAmount contextRef="now" unitRef="{$target_cur}" decimals="2"><xsl:value-of select="number(replace($value,',','.'))*$cur_factor"/></gl-cor:taxAmount></xsl:if>
<!--BT-143--><xsl:variable name="value" select="./VatRatePercent"/><xsl:if test="string($value)"><gl-cor:taxPercentageRate contextRef="now" unitRef="NotUsed" decimals="0"><xsl:value-of select="replace($value,',','.')"/></gl-cor:taxPercentageRate></xsl:if>
<!--BT-142--><xsl:variable name="value" select="./VatCode"/><xsl:if test="string($value)"><gl-cor:taxCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:taxCode></xsl:if>
<!--BT-144--><xsl:variable name="value" select="./VatFreeText"/><xsl:if test="string($value)"><gl-cor:taxCommentExemption contextRef="now"><xsl:value-of select="$value"/></gl-cor:taxCommentExemption></xsl:if>
</gl-cor:taxes>



<xsl:variable name="value" select="//OriginalInvoiceNumber"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">credit-note</gl-taf:originatingDocumentType>
<!--BT-21--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="//AgreementIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">contract</gl-taf:originatingDocumentType>
<!--BT-10--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="//OrderIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">order-customer</gl-taf:originatingDocumentType>
<!--BT-11--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>


<xsl:variable name="value" select="//OrderConfirmationIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">order-vendor</gl-taf:originatingDocumentType>
<!--BT-12--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>



<!--BT-14--><xsl:variable name="value" select="//DeliveryNoteIdentifier"/><xsl:if test="string($value)"><gl-taf:originatingDocumentStructure><gl-taf:originatingDocumentType contextRef="now">despatch-advice</gl-taf:originatingDocumentType><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber></gl-taf:originatingDocumentStructure></xsl:if>


<xsl:variable name="value" select="//EpiRemittanceInfoIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">finance-charge</gl-taf:originatingDocumentType>
<gl-taf:originatingDocumentIdentifierCode contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentIdentifierCode>
</gl-taf:originatingDocumentStructure>
</xsl:if>

</gl-cor:entryDetail>
</xsl:for-each>

</xsl:if>
<!--Entry Header VAT tuple for CASE1 ends-->


<!--CASE2: Vat category = 'AE' or 'K' or 'G' (reverse VAT), trade with-in EU-->
<xsl:if test="$buyer_cc!=$seller_cc and ($tax_c='AE' or $tax_c='K' or $tax_c='G')">
<!--creating one entry per tax catecory given in the invoice-->
<xsl:for-each select="//InvoiceRow">

<!--The amount of VAT to be applied for the invoice is calculated by using the product's UPC code and the mapping provided to retrieve the correct VAT rate-->
<xsl:variable name="value" select="./RowVatExcludedAmount"/>
<xsl:variable name="upc_code" select="./ArticleGroupIdentifier/text()"/>
<xsl:variable name="vat" select="$upc_map//map:Map[child::map:UPC/text()=$upc_code]/map:VatRate[@country=$buyer_cc]"/>
<!--In the NSG reference implementation for each purchased product (with provided product category) two entryDetail-groups are created; one for VAT debtors and one for creditors that balances to zero. -->
<!--Entry for VAT receivables-->
<gl-cor:entryDetail>
<!-- For NSG 3 reference implementation the buyer's country code serves as a identifier for the standardized chart of accounts to be used (which countrie's CoA) (mainAccountTypeDescription)-->
<!-- For NSG 3 reference implementation default accounts are used on Header VAT type entries where all are considered as entries of the "VAT receivables, short term"-->

<!--Account tuples-->
<!-- The finnish account to be used for short term VAT receivables is "17621" (Raportointikoodisto, Standard business reporting code set chart of accounts-->

<gl-cor:account>
<gl-cor:accountMainID contextRef="now">17621</gl-cor:accountMainID><gl-cor:mainAccountTypeDescription contextRef="now">FI</gl-cor:mainAccountTypeDescription><xsl:comment>See the Finnish chart of accounts here, also in swedish: https://koodistot.suomi.fi/extension;registryCode=sbr-fi-code-lists;schemeCode=MC-2019-1;extensionCode=MC65</xsl:comment>
<xsl:comment>
Swedish standard chart of accounts: https://docs.google.com/spreadsheets/d/1MOZ_AlbkiQqFNCCQDjOucVxsElZgc_5yCKKV_KrrHjg/edit?usp=sharing
Norwegian standard chart of accounts:
https://drive.google.com/file/d/1oPllLYGpmKKPnF1IXHxsHltanrjevWuu/view?usp=sharing
Danish standard chart of accounts:
https://docs.google.com/spreadsheets/d/14KIXWaZEED6UNL4XhSVFtzEwqhCTxNAMJjpalSOyB8A/edit?usp=sharing
Icelandic standard chart of accounts:
https://drive.google.com/open?id=18m-0i6DfcAmV1KKZcp-5XRt8mQq-HEsD
</xsl:comment>
</gl-cor:account>

<!--For NSG 3 reference implementation. Add other countries accounting references too for the short term trade VAT receivables.-->
<xsl:for-each select="$ac_map//gl-cor:account[gl-cor:mainAccountTypeDescription/text() = 'FI' and gl-cor:accountMainID/text()='17621']/parent::gl-cor:entryDetail/child::gl-cor:account">
<xsl:if test="gl-cor:mainAccountTypeDescription/text() != 'FI'">
 <gl-cor:account>
		 <gl-cor:accountMainID contextRef="now"><xsl:value-of select="./gl-cor:accountMainID/text()"/></gl-cor:accountMainID>
		 <gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="./gl-cor:mainAccountTypeDescription/text()"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>
</xsl:if>
</xsl:for-each>

<xsl:if test="exists($value) and string($upc_code) and exists($vat)"><gl-cor:amount contextRef="now" unitRef="{$target_cur}" decimals="2"><xsl:value-of select="format-number((number(replace($value,',','.')) *number($vat[1]) div 100 * $cur_factor), '0.00')"/></gl-cor:amount></xsl:if>

<xsl:choose>
  <xsl:when test="not(exists(//DeliveryDate[1]))">
<!--BT-2--><xsl:variable name="value" select="//InvoiceDate"/><xsl:if test="string($value)"><gl-cor:postingDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:postingDate></xsl:if>
  </xsl:when>
  <xsl:otherwise>
    <!--BT-2--><xsl:variable name="value" select="//DeliveryDate[1]"/><xsl:if test="string($value)"><gl-cor:postingDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:postingDate></xsl:if>
  </xsl:otherwise>
</xsl:choose>

<!--Identifier refrence tuples-->
<!--Buyer party-->
<!--BT-43--><xsl:variable name="value" select="//SellersBuyerIdentifier"/><xsl:if test="string($value)">
<gl-cor:identifierReference>
<gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode>
<!--BT-42--><xsl:variable name="value" select="//BuyerPartyDetails/BuyerOrganisationName[1]"/><xsl:if test="string($value)">
<gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription><gl-cor:identifierType contextRef="now">FI-B</gl-cor:identifierType></xsl:if>
</gl-cor:identifierReference>
</xsl:if>

<!--Seller Party-->
<xsl:for-each select="//SellerPartyDetails">
<gl-cor:identifierReference>
<!--BT-27--><xsl:variable name="value" select="./SellerCode"/><xsl:if test="string($value)"><gl-cor:identifierExternalReference><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode></gl-cor:identifierExternalReference></xsl:if>
<gl-cor:identifierExternalReference>
<!--BT-28--><xsl:variable name="value" select="./SellerPartyIdentifier"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">ytunnus</gl-cor:identifierAuthority><xsl:comment>Business ID</xsl:comment></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<!--BT-30--><xsl:variable name="value" select="./SellerOrganisationTaxCode"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">alvtunnus</gl-cor:identifierAuthority><xsl:comment>VAT ID</xsl:comment></xsl:if>
</gl-cor:identifierExternalReference>
<!--SellerAccountDetails-->
<xsl:for-each select="//SellerAccountDetails">
<gl-cor:identifierExternalReference>
<!--BT-81--><xsl:variable name="value" select="./SellerAccountID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">IBAN</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<!--BT-82--><xsl:variable name="value" select="./SellerBic"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">BIC</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
</xsl:for-each>

<xsl:variable name="value" select="//SellerInformationDetails/SellerTaxRegistrationText"/><xsl:if test="string($value)">
<gl-cor:identifierExternalReference>
<!--BT-81--><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode>
</gl-cor:identifierExternalReference>
</xsl:if>

<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>

<!--BT-25--><xsl:variable name="value" select="//SellerPartyDetails/SellerOrganisationName[1]"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>

<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-S</gl-cor:identifierType></xsl:if>

<!--Seller Address Details-->
<gl-bus:identifierAddress>
<!--BT-33--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerStreetName[1]"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!--BT-34--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerStreetName[2]"/>
<!--BT-34--><xsl:variable name="value2" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerStreetName[3]"/>
<xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="concat($value, ', ', $value2)"/></gl-bus:identifierAddressStreet2></xsl:if>
<!--BT-35--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerTownName"/><xsl:if test="string($value)"><gl-bus:identifierCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCity></xsl:if>
<!--BT-38--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/CountryCode"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!--BT-36--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerPostCodeIdentifier"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
</gl-bus:identifierAddress>
<!--Seller contact information-->
<gl-cor:identifierContactInformationStructure>
<!--BT-39--><xsl:variable name="value" select="//SellerContactPersonName"/><xsl:if test="string($value)"><gl-cor:identifierContactAttentionLine contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactAttentionLine></xsl:if>
<gl-cor:identifierContactPhone>
<!--BT-40--><xsl:variable name="value" select="//SellerPhoneNumberIdentifier"/><xsl:if test="string($value)"><gl-cor:identifierContactPhoneNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactPhoneNumber></xsl:if>
</gl-cor:identifierContactPhone>
<gl-cor:identifierContactEmail>
<!--BT-41--><xsl:variable name="value" select="//SellerEmailAddressIdentifier"/><xsl:if test="string($value)"><gl-cor:identifierContactEmailAddress contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactEmailAddress></xsl:if>
</gl-cor:identifierContactEmail>
</gl-cor:identifierContactInformationStructure>
</gl-cor:identifierReference>
</xsl:for-each>
<!--end of Seller Party-->

<!--Payee party details (EpiDetails in Finvoice)-->
<xsl:for-each select="//EpiPartyDetails">
<gl-cor:identifierReference>
<gl-cor:identifierExternalReference>
<!--BT-58--><xsl:variable name="value" select="//EpiBei"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">ytunnus</gl-cor:identifierAuthority><xsl:comment>Business ID</xsl:comment></xsl:if>
</gl-cor:identifierExternalReference>
<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>
<!--BT-56--><xsl:variable name="value" select="//EpiNameAddressDetails"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>

<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-P</gl-cor:identifierType></xsl:if>
</gl-cor:identifierReference>
</xsl:for-each>
<!--End of Payee Party-->



<!-- Alkuperäinen lähdeasiakirja on lasku -->
<!--BT-1--><xsl:variable name="value" select="//InvoiceNumber"/><xsl:if test="string($value)">
<gl-cor:documentType contextRef="now">invoice</gl-cor:documentType></xsl:if>
<xsl:variable name="value" select="//InvoiceTypeCode"/><xsl:if test="string($value)">
<gl-cor:invoiceType contextRef="now"><xsl:value-of select="$value"/></gl-cor:invoiceType></xsl:if>
<xsl:variable name="value" select="//InvoiceNumber"/><xsl:if test="string($value)">
<!--
<gl-cor:documentNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentNumber>
-->
</xsl:if>

<!--BT-76--><xsl:variable name="value" select="//EpiRemittanceInfoIdentifier"/><xsl:if test="string($value)"><gl-cor:documentReference contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentReference></xsl:if>

<!--BT-2--><xsl:variable name="value" select="//InvoiceDate"/><xsl:if test="string($value)"><gl-cor:documentDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:documentDate></xsl:if>

<gl-cor:detailComment contextRef="now">Header VAT</gl-cor:detailComment>

<!--BT-69--><xsl:variable name="value" select="//DeliveryDate"/><xsl:if test="string($value)"><gl-cor:shipReceivedDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:shipReceivedDate></xsl:if>


<!--BT-7--><xsl:variable name="value" select="//InvoiceDueDate"/><xsl:if test="string($value)"><gl-cor:maturityDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:maturityDate></xsl:if>

<!--BT-18--><xsl:variable name="value" select="concat(//PaymentTermsFreeText[1], ',',//PaymentTermsFreeText[2])"/><xsl:if test="string($value)!=','"><gl-cor:terms contextRef="now"><xsl:value-of select="$value"/></gl-cor:terms></xsl:if>

<gl-cor:taxes>
<!--BT-144--><xsl:variable name="value" select="./VatRateAmount"/><xsl:if test="string($value)"><gl-cor:taxAmount contextRef="now" unitRef="{$target_cur}" decimals="2"><xsl:value-of select="number(replace($value,',','.'))*$cur_factor"/></gl-cor:taxAmount></xsl:if>
<!--BT-143--><xsl:variable name="value" select="./VatRatePercent"/><xsl:if test="string($value)"><gl-cor:taxPercentageRate contextRef="now" unitRef="NotUsed" decimals="0"><xsl:value-of select="replace($value,',','.')"/></gl-cor:taxPercentageRate></xsl:if>
<!--BT-142--><xsl:variable name="value" select="./VatCode"/><xsl:if test="string($value)"><gl-cor:taxCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:taxCode></xsl:if>
<!--BT-144--><xsl:variable name="value" select="./VatFreeText"/><xsl:if test="string($value)"><gl-cor:taxCommentExemption contextRef="now"><xsl:value-of select="$value"/></gl-cor:taxCommentExemption></xsl:if>
</gl-cor:taxes>



<xsl:variable name="value" select="//OriginalInvoiceNumber"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">credit-note</gl-taf:originatingDocumentType>
<!--BT-21--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="//AgreementIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">contract</gl-taf:originatingDocumentType>
<!--BT-10--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="//OrderIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">order-customer</gl-taf:originatingDocumentType>
<!--BT-11--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>


<xsl:variable name="value" select="//OrderConfirmationIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">order-vendor</gl-taf:originatingDocumentType>
<!--BT-12--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>



<!--BT-14--><xsl:variable name="value" select="//DeliveryNoteIdentifier"/><xsl:if test="string($value)"><gl-taf:originatingDocumentStructure><gl-taf:originatingDocumentType contextRef="now">despatch-advice</gl-taf:originatingDocumentType><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber></gl-taf:originatingDocumentStructure></xsl:if>


<xsl:variable name="value" select="//EpiRemittanceInfoIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">finance-charge</gl-taf:originatingDocumentType>
<gl-taf:originatingDocumentIdentifierCode contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentIdentifierCode>
</gl-taf:originatingDocumentStructure>
</xsl:if>

</gl-cor:entryDetail>

<!--Entry for VAT payables-->
<gl-cor:entryDetail>
<!-- For NSG 3 reference implementation the buyer's country code serves as a identifier for the standardized chart of accounts to be used (which countrie's CoA) (mainAccountTypeDescription)-->
<!-- For NSG 3 reference implementation default accounts are used on Header VAT type entries where all are considered as entries of the "VAT receivables, short term"-->

<!--Account tuples-->
<!-- The finnish account to be used for short term VAT payables is "2920451" (goods) and "2920461" (services) (Raportointikoodisto, Standard business reporting code set chart of accounts-->
<!--TODO for the intra community trade we still need to figure out where to get the info if it's a goods or service. For the moment all are handled as goods-->

<gl-cor:account>
<gl-cor:accountMainID contextRef="now">2920451</gl-cor:accountMainID><gl-cor:mainAccountTypeDescription contextRef="now">FI</gl-cor:mainAccountTypeDescription><xsl:comment>See the Finnish chart of accounts here, also in swedish: https://koodistot.suomi.fi/extension;registryCode=sbr-fi-code-lists;schemeCode=MC-2019-1;extensionCode=MC65</xsl:comment>
<xsl:comment>
Swedish standard chart of accounts: https://docs.google.com/spreadsheets/d/1MOZ_AlbkiQqFNCCQDjOucVxsElZgc_5yCKKV_KrrHjg/edit?usp=sharing
Norwegian standard chart of accounts:
https://drive.google.com/file/d/1oPllLYGpmKKPnF1IXHxsHltanrjevWuu/view?usp=sharing
Danish standard chart of accounts:
https://docs.google.com/spreadsheets/d/14KIXWaZEED6UNL4XhSVFtzEwqhCTxNAMJjpalSOyB8A/edit?usp=sharing
Icelandic standard chart of accounts:
https://drive.google.com/open?id=18m-0i6DfcAmV1KKZcp-5XRt8mQq-HEsD
</xsl:comment>
</gl-cor:account>

<!--For NSG 3 reference implementation. Add other countries accounting references too for the short term trade VAT receivables.-->
<xsl:for-each select="$ac_map//gl-cor:account[gl-cor:mainAccountTypeDescription/text() = 'FI' and gl-cor:accountMainID/text()='2920451']/parent::gl-cor:entryDetail/child::gl-cor:account">
<xsl:if test="gl-cor:mainAccountTypeDescription/text() != 'FI'">
 <gl-cor:account>
		 <gl-cor:accountMainID contextRef="now"><xsl:value-of select="./gl-cor:accountMainID/text()"/></gl-cor:accountMainID>
		 <gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="./gl-cor:mainAccountTypeDescription/text()"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>
</xsl:if>
</xsl:for-each>

<xsl:if test="exists($value) and string($upc_code) and exists($vat)"><gl-cor:amount contextRef="now" unitRef="{$target_cur}" decimals="2"><xsl:value-of select="format-number((number(replace($value,',','.')) *number(replace($vat[1],',','.')) div 100 * (-1) * $cur_factor), '0.00')"/></gl-cor:amount></xsl:if>

<xsl:choose>
  <xsl:when test="not(exists(//DeliveryDate[1]))">
<!--BT-2--><xsl:variable name="value" select="//InvoiceDate"/><xsl:if test="string($value)"><gl-cor:postingDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:postingDate></xsl:if>
  </xsl:when>
  <xsl:otherwise>
    <!--BT-2--><xsl:variable name="value" select="//DeliveryDate[1]"/><xsl:if test="string($value)"><gl-cor:postingDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:postingDate></xsl:if>
  </xsl:otherwise>
</xsl:choose>

<!--Identifier refrence tuples-->
<!--Buyer party-->
<!--BT-43--><xsl:variable name="value" select="//SellersBuyerIdentifier"/><xsl:if test="string($value)">
<gl-cor:identifierReference>
<gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode>
<!--BT-42--><xsl:variable name="value" select="//BuyerPartyDetails/BuyerOrganisationName[1]"/><xsl:if test="string($value)">
<gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription><gl-cor:identifierType contextRef="now">FI-B</gl-cor:identifierType></xsl:if>
</gl-cor:identifierReference>
</xsl:if>

<!--Seller Party-->
<xsl:for-each select="//SellerPartyDetails">
<gl-cor:identifierReference>
<!--BT-27--><xsl:variable name="value" select="./SellerCode"/><xsl:if test="string($value)"><gl-cor:identifierExternalReference><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode></gl-cor:identifierExternalReference></xsl:if>
<gl-cor:identifierExternalReference>
<!--BT-28--><xsl:variable name="value" select="./SellerPartyIdentifier"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">ytunnus</gl-cor:identifierAuthority><xsl:comment>Business ID</xsl:comment></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<!--BT-30--><xsl:variable name="value" select="./SellerOrganisationTaxCode"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">alvtunnus</gl-cor:identifierAuthority><xsl:comment>VAT ID</xsl:comment></xsl:if>
</gl-cor:identifierExternalReference>
<!--SellerAccountDetails-->
<xsl:for-each select="//SellerAccountDetails">
<gl-cor:identifierExternalReference>
<!--BT-81--><xsl:variable name="value" select="./SellerAccountID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">IBAN</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<!--BT-82--><xsl:variable name="value" select="./SellerBic"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">BIC</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
</xsl:for-each>

<xsl:variable name="value" select="//SellerInformationDetails/SellerTaxRegistrationText"/><xsl:if test="string($value)">
<gl-cor:identifierExternalReference>
<!--BT-81--><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode>
</gl-cor:identifierExternalReference>
</xsl:if>

<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>

<!--BT-25--><xsl:variable name="value" select="//SellerPartyDetails/SellerOrganisationName[1]"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>

<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-S</gl-cor:identifierType></xsl:if>

<!--Seller Address Details-->
<gl-bus:identifierAddress>
<!--BT-33--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerStreetName[1]"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!--BT-34--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerStreetName[2]"/>
<!--BT-34--><xsl:variable name="value2" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerStreetName[3]"/>
<xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="concat($value, ', ', $value2)"/></gl-bus:identifierAddressStreet2></xsl:if>
<!--BT-35--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerTownName"/><xsl:if test="string($value)"><gl-bus:identifierCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCity></xsl:if>
<!--BT-38--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/CountryCode"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!--BT-36--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerPostCodeIdentifier"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
</gl-bus:identifierAddress>
<!--Seller contact information-->
<gl-cor:identifierContactInformationStructure>
<!--BT-39--><xsl:variable name="value" select="//SellerContactPersonName"/><xsl:if test="string($value)"><gl-cor:identifierContactAttentionLine contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactAttentionLine></xsl:if>
<gl-cor:identifierContactPhone>
<!--BT-40--><xsl:variable name="value" select="//SellerPhoneNumberIdentifier"/><xsl:if test="string($value)"><gl-cor:identifierContactPhoneNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactPhoneNumber></xsl:if>
</gl-cor:identifierContactPhone>
<gl-cor:identifierContactEmail>
<!--BT-41--><xsl:variable name="value" select="//SellerEmailAddressIdentifier"/><xsl:if test="string($value)"><gl-cor:identifierContactEmailAddress contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactEmailAddress></xsl:if>
</gl-cor:identifierContactEmail>
</gl-cor:identifierContactInformationStructure>
</gl-cor:identifierReference>
</xsl:for-each>
<!--end of Seller Party-->

<!--Payee party details (EpiDetails in Finvoice)-->
<xsl:for-each select="//EpiPartyDetails">
<gl-cor:identifierReference>
<gl-cor:identifierExternalReference>
<!--BT-58--><xsl:variable name="value" select="//EpiBei"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">ytunnus</gl-cor:identifierAuthority><xsl:comment>Business ID</xsl:comment></xsl:if>
</gl-cor:identifierExternalReference>
<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>
<!--BT-56--><xsl:variable name="value" select="//EpiNameAddressDetails"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>

<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-P</gl-cor:identifierType></xsl:if>
</gl-cor:identifierReference>
</xsl:for-each>
<!--End of Payee Party-->



<!-- Alkuperäinen lähdeasiakirja on lasku -->
<!--BT-1--><xsl:variable name="value" select="//InvoiceNumber"/><xsl:if test="string($value)">
<gl-cor:documentType contextRef="now">invoice</gl-cor:documentType></xsl:if>
<xsl:variable name="value" select="//InvoiceTypeCode"/><xsl:if test="string($value)">
<gl-cor:invoiceType contextRef="now"><xsl:value-of select="$value"/></gl-cor:invoiceType></xsl:if>
<xsl:variable name="value" select="//InvoiceNumber"/><xsl:if test="string($value)">
<!--
<gl-cor:documentNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentNumber>
-->
</xsl:if>

<!--BT-76--><xsl:variable name="value" select="//EpiRemittanceInfoIdentifier"/><xsl:if test="string($value)"><gl-cor:documentReference contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentReference></xsl:if>

<!--BT-2--><xsl:variable name="value" select="//InvoiceDate"/><xsl:if test="string($value)"><gl-cor:documentDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:documentDate></xsl:if>

<gl-cor:detailComment contextRef="now">Header VAT</gl-cor:detailComment>

<!--BT-69--><xsl:variable name="value" select="//DeliveryDate"/><xsl:if test="string($value)"><gl-cor:shipReceivedDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:shipReceivedDate></xsl:if>


<!--BT-7--><xsl:variable name="value" select="//InvoiceDueDate"/><xsl:if test="string($value)"><gl-cor:maturityDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:maturityDate></xsl:if>

<!--BT-18--><xsl:variable name="value" select="concat(//PaymentTermsFreeText[1], ',',//PaymentTermsFreeText[2])"/><xsl:if test="string($value)!=','"><gl-cor:terms contextRef="now"><xsl:value-of select="$value"/></gl-cor:terms></xsl:if>

<gl-cor:taxes>
<!--BT-144--><xsl:variable name="value" select="./VatRateAmount"/><xsl:if test="string($value)"><gl-cor:taxAmount contextRef="now" unitRef="{$target_cur}" decimals="2"><xsl:value-of select="number(replace($value,',','.'))*$cur_factor"/></gl-cor:taxAmount></xsl:if>
<!--BT-143--><xsl:variable name="value" select="./VatRatePercent"/><xsl:if test="string($value)"><gl-cor:taxPercentageRate contextRef="now" unitRef="NotUsed" decimals="0"><xsl:value-of select="replace($value,',','.')"/></gl-cor:taxPercentageRate></xsl:if>
<!--BT-142--><xsl:variable name="value" select="./VatCode"/><xsl:if test="string($value)"><gl-cor:taxCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:taxCode></xsl:if>
<!--BT-144--><xsl:variable name="value" select="./VatFreeText"/><xsl:if test="string($value)"><gl-cor:taxCommentExemption contextRef="now"><xsl:value-of select="$value"/></gl-cor:taxCommentExemption></xsl:if>
</gl-cor:taxes>



<xsl:variable name="value" select="//OriginalInvoiceNumber"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">credit-note</gl-taf:originatingDocumentType>
<!--BT-21--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="//AgreementIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">contract</gl-taf:originatingDocumentType>
<!--BT-10--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="//OrderIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">order-customer</gl-taf:originatingDocumentType>
<!--BT-11--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>


<xsl:variable name="value" select="//OrderConfirmationIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">order-vendor</gl-taf:originatingDocumentType>
<!--BT-12--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>



<!--BT-14--><xsl:variable name="value" select="//DeliveryNoteIdentifier"/><xsl:if test="string($value)"><gl-taf:originatingDocumentStructure><gl-taf:originatingDocumentType contextRef="now">despatch-advice</gl-taf:originatingDocumentType><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber></gl-taf:originatingDocumentStructure></xsl:if>


<xsl:variable name="value" select="//EpiRemittanceInfoIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">finance-charge</gl-taf:originatingDocumentType>
<gl-taf:originatingDocumentIdentifierCode contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentIdentifierCode>
</gl-taf:originatingDocumentStructure>
</xsl:if>

</gl-cor:entryDetail>

</xsl:for-each>

</xsl:if>
<!--Entry Header VAT tuple for CASE2 ends-->

<!--BG-23-->
<!--one per each InvoiceRow-->
<xsl:for-each select="//InvoiceRow">
<!--Entry Detail tuples-->
<gl-cor:entryDetail>
<!--Account tuple-->

<!--first choice is to use invoice row level accounting references. If not given, the accounting reference for the entire invoice will be used--> 
<xsl:variable name="value" select="./RowAccountDimensionText/text()"/>
<xsl:variable name="value2" select="//AccountDimensionText/text()"/>
<!-- For NSG 3 reference implementation the buyer's country code serves as a identifier for the standardized chart of accounts to be used (which countrie's CoA) (mainAccountTypeDescription)-->
<xsl:variable name="cc" select="//BuyerPartyDetails/BuyerPostalAddressDetails/CountryCode/text()"/>
<gl-cor:account>
<!--BT-124--><!--BT-17-->
<xsl:choose><xsl:when test="string($value)"><gl-cor:accountMainID contextRef="now"><xsl:value-of select="$value"/></gl-cor:accountMainID></xsl:when><xsl:when test="string($value2)"><gl-cor:accountMainID contextRef="now"><xsl:value-of select="$value2"/></gl-cor:accountMainID></xsl:when><xsl:otherwise></xsl:otherwise></xsl:choose>
<xsl:if test="string($value)"></xsl:if><gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="$cc"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>

<!--For NSG 3 reference implementation. Add other countries accounting references.-->
<xsl:for-each select="$ac_map//gl-cor:account[gl-cor:mainAccountTypeDescription/text() = $cc and (gl-cor:accountMainID/text()=$value or gl-cor:accountMainID/text()=$value2)]/parent::gl-cor:entryDetail/child::gl-cor:account">
<xsl:if test="gl-cor:mainAccountTypeDescription/text() != $cc and (gl-cor:accountMainID/text()!=$value or gl-cor:accountMainID/text()!=$value2)">
 <gl-cor:account>
		 <gl-cor:accountMainID contextRef="now"><xsl:value-of select="./gl-cor:accountMainID/text()"/></gl-cor:accountMainID>
		 <gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="./gl-cor:mainAccountTypeDescription/text()"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>
</xsl:if>
</xsl:for-each>



<!--BT-122--><xsl:variable name="value" select="./RowVatExcludedAmount"/><xsl:if test="string($value)"><gl-cor:amount contextRef="now" unitRef="{$target_cur}" decimals="2"><xsl:value-of select="format-number(number(replace($value,',','.'))*$cur_factor, '0.00')"/></gl-cor:amount></xsl:if>


<!--BT-8--><xsl:variable name="value" select="//InvoiceTotalVatIncludedAmount/@AmountCurrencyIdentifier"/><xsl:if test="string($value)"><gl-muc:amountOriginalCurrency contextRef="now"><xsl:value-of select="$value"/></gl-muc:amountOriginalCurrency></xsl:if>

<xsl:choose>
  <xsl:when test="not(exists(//DeliveryDate[1]))">
<!--BT-2--><xsl:variable name="value" select="//InvoiceDate"/><xsl:if test="string($value)"><gl-cor:postingDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:postingDate></xsl:if>
  </xsl:when>
  <xsl:otherwise>
    <!--BT-2--><xsl:variable name="value" select="//DeliveryDate[1]"/><xsl:if test="string($value)"><gl-cor:postingDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:postingDate></xsl:if>
  </xsl:otherwise>
</xsl:choose>

<!--Identifier refrence tuples-->
<!--Buyer party-->
<!--BT-43--><xsl:variable name="value" select="//SellersBuyerIdentifier"/><xsl:if test="string($value)">
<gl-cor:identifierReference>
<gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode>
<!--BT-42--><xsl:variable name="value" select="//BuyerPartyDetails/BuyerOrganisationName[1]"/><xsl:if test="string($value)">
<gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription><gl-cor:identifierType contextRef="now">FI-B</gl-cor:identifierType></xsl:if>
</gl-cor:identifierReference>
</xsl:if>

<!--Seller Party-->
<xsl:for-each select="//SellerPartyDetails">
<gl-cor:identifierReference>
<!--BT-27--><xsl:variable name="value" select="./SellerCode"/><xsl:if test="string($value)"><gl-cor:identifierExternalReference><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode></gl-cor:identifierExternalReference></xsl:if>
<gl-cor:identifierExternalReference>
<!--BT-28--><xsl:variable name="value" select="./SellerPartyIdentifier"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">ytunnus</gl-cor:identifierAuthority><xsl:comment>Business ID</xsl:comment></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<!--BT-30--><xsl:variable name="value" select="./SellerOrganisationTaxCode"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">alvtunnus</gl-cor:identifierAuthority><xsl:comment>VAT ID</xsl:comment></xsl:if>
</gl-cor:identifierExternalReference>
<!--SellerAccountDetails-->
<xsl:for-each select="//SellerAccountDetails">
<gl-cor:identifierExternalReference>
<!--BT-81--><xsl:variable name="value" select="./SellerAccountID"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">IBAN</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<!--BT-82--><xsl:variable name="value" select="./SellerBic"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">BIC</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
</xsl:for-each>

<xsl:variable name="value" select="//SellerInformationDetails/SellerTaxRegistrationText"/><xsl:if test="string($value)">
<gl-cor:identifierExternalReference>
<!--BT-81--><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode>
</gl-cor:identifierExternalReference>
</xsl:if>

<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>

<!--BT-25--><xsl:variable name="value" select="//SellerPartyDetails/SellerOrganisationName[1]"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>

<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-S</gl-cor:identifierType></xsl:if>

<!--Seller Address Details-->
<gl-bus:identifierAddress>
<!--BT-33--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerStreetName[1]"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!--BT-34--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerStreetName[2]"/>
<!--BT-34--><xsl:variable name="value2" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerStreetName[3]"/>
<xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="concat($value, ', ', $value2)"/></gl-bus:identifierAddressStreet2></xsl:if>
<!--BT-35--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerTownName"/><xsl:if test="string($value)"><gl-bus:identifierCity contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCity></xsl:if>
<!--BT-38--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/CountryCode"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!--BT-36--><xsl:variable name="value" select="//SellerPartyDetails/SellerPostalAddressDetails/SellerPostCodeIdentifier"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
</gl-bus:identifierAddress>
<!--Seller contact information-->
<gl-cor:identifierContactInformationStructure>
<!--BT-39--><xsl:variable name="value" select="//SellerContactPersonName"/><xsl:if test="string($value)"><gl-cor:identifierContactAttentionLine contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactAttentionLine></xsl:if>
<gl-cor:identifierContactPhone>
<!--BT-40--><xsl:variable name="value" select="//SellerPhoneNumberIdentifier"/><xsl:if test="string($value)"><gl-cor:identifierContactPhoneNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactPhoneNumber></xsl:if>
</gl-cor:identifierContactPhone>
<gl-cor:identifierContactEmail>
<!--BT-41--><xsl:variable name="value" select="//SellerEmailAddressIdentifier"/><xsl:if test="string($value)"><gl-cor:identifierContactEmailAddress contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierContactEmailAddress></xsl:if>
</gl-cor:identifierContactEmail>
</gl-cor:identifierContactInformationStructure>
</gl-cor:identifierReference>
</xsl:for-each>
<!--end of Seller Party-->

<!--Payee party details (EpiDetails in Finvoice)-->
<xsl:for-each select="//EpiPartyDetails">
<gl-cor:identifierReference>
<gl-cor:identifierExternalReference>
<!--BT-58--><xsl:variable name="value" select="//EpiBei"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">ytunnus</gl-cor:identifierAuthority><xsl:comment>Business ID</xsl:comment></xsl:if>
</gl-cor:identifierExternalReference>
<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>
<!--BT-56--><xsl:variable name="value" select="//EpiNameAddressDetails"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>

<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-P</gl-cor:identifierType></xsl:if>
</gl-cor:identifierReference>
</xsl:for-each>
<!--End of Payee Party-->



<!-- Alkuperäinen lähdeasiakirja on lasku -->
<!--BT-1--><xsl:variable name="value" select="//InvoiceNumber"/><xsl:if test="string($value)">
<gl-cor:documentType contextRef="now">invoice</gl-cor:documentType></xsl:if>
<xsl:variable name="value" select="//InvoiceTypeCode"/><xsl:if test="string($value)">
<gl-cor:invoiceType contextRef="now"><xsl:value-of select="$value"/></gl-cor:invoiceType></xsl:if>
<xsl:variable name="value" select="//InvoiceNumber"/><xsl:if test="string($value)">
<!--
<gl-cor:documentNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentNumber>
-->
</xsl:if>

<!--BT-76--><xsl:variable name="value" select="//EpiRemittanceInfoIdentifier"/><xsl:if test="string($value)"><gl-cor:documentReference contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentReference></xsl:if>

<!--BT-2--><xsl:variable name="value" select="//InvoiceDate"/><xsl:if test="string($value)"><gl-cor:documentDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:documentDate></xsl:if>

<gl-cor:detailComment contextRef="now">Row</gl-cor:detailComment>

<!--BT-69--><xsl:variable name="value" select="//DeliveryDate"/><xsl:if test="string($value)"><gl-cor:shipReceivedDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:shipReceivedDate></xsl:if>


<!--BT-7--><xsl:variable name="value" select="//InvoiceDueDate"/><xsl:if test="string($value)"><gl-cor:maturityDate contextRef="now"><xsl:value-of select="concat(substring($value,1,4),'-',substring($value,5,2),'-',substring($value,7,2))"/></gl-cor:maturityDate></xsl:if>

<!--BT-18--><xsl:variable name="value" select="concat(//PaymentTermsFreeText[1], ',',//PaymentTermsFreeText[2])"/><xsl:if test="string($value)!=','"><gl-cor:terms contextRef="now"><xsl:value-of select="$value"/></gl-cor:terms></xsl:if>


<gl-bus:measurable>
<!--BT-148--><xsl:variable name="value" select="./EanCode"/><xsl:if test="string($value)"><gl-bus:measurableID contextRef="now"><xsl:value-of select="$value"/></gl-bus:measurableID></xsl:if>
<!--BT-147--><xsl:variable name="value" select="./ArticleIdentifier"/><xsl:if test="string($value)"><gl-bus:measurableIDOther contextRef="now"><xsl:value-of select="$value"/></gl-bus:measurableIDOther><gl-bus:measurableDescription contextRef="now">Item Seller's identifier</gl-bus:measurableDescription></xsl:if>
<!--BT-145--><xsl:variable name="value" select="./ArticleName"/><xsl:if test="string($value)"><gl-bus:measurableDescription contextRef="now"><xsl:value-of select="$value"/></gl-bus:measurableDescription></xsl:if>
<!--BT-120--><xsl:variable name="value" select="./InvoicedQuantity"/><xsl:if test="string($value)"><gl-bus:measurableQuantity contextRef="now" unitRef="NotUsed" decimals="0"><xsl:value-of select="replace($value,',','.')"/></gl-bus:measurableQuantity></xsl:if>
<!--BT-120--><xsl:variable name="value" select="./InvoicedQuantity/@QuantityUnitCode"/><xsl:if test="string($value)"><gl-bus:measurableUnitOfMeasure contextRef="now"><xsl:value-of select="$value"/></gl-bus:measurableUnitOfMeasure></xsl:if>
<!--BT-149--><xsl:variable name="value" select="./ArticleGroupIdentifier"/><xsl:if test="string($value)"><gl-bus:measurableQualifier contextRef="now"><xsl:value-of select="$value"/></gl-bus:measurableQualifier></xsl:if><xsl:comment>In NSG produced data often used for UNSPSC = United Nations Standard Products and Services Code</xsl:comment>
<!--BT-126--><xsl:variable name="value" select="./StartDate"/><xsl:if test="string($value)"><gl-bus:measurableStartDateTime contextRef="now"><xsl:value-of select="$value"/></gl-bus:measurableStartDateTime></xsl:if>
<!--BT-127--><xsl:variable name="value" select="./EndDate"/><xsl:if test="string($value)"><gl-bus:measurableEndDateTime contextRef="now"><xsl:value-of select="$value"/></gl-bus:measurableEndDateTime></xsl:if>
</gl-bus:measurable>

<!--BT-156--><xsl:variable name="value" select="./BuyerArticleIdentifier"/><xsl:if test="string($value)">
<gl-bus:measurable>
<gl-bus:measurableIDOther contextRef="now"><xsl:value-of select="$value"/></gl-bus:measurableIDOther>
<gl-bus:measurableDescription contextRef="now">Item Buyer's identifier</gl-bus:measurableDescription>
</gl-bus:measurable>
</xsl:if>

<gl-cor:taxes>
<!--BT-144--><xsl:variable name="value" select="./RowVatAmount"/><xsl:if test="string($value)"><gl-cor:taxAmount contextRef="now" unitRef="{$target_cur}" decimals="2"><xsl:value-of select="number(replace($value,',','.'))*$cur_factor"/></gl-cor:taxAmount></xsl:if>
<!--BT-143--><xsl:variable name="value" select="./RowVatRatePercent"/><xsl:if test="string($value)"><gl-cor:taxPercentageRate contextRef="now" unitRef="NotUsed" decimals="0"><xsl:value-of select="replace($value,',','.')"/></gl-cor:taxPercentageRate></xsl:if>
<!--BT-142--><xsl:variable name="value" select="./RowVatCode"/><xsl:if test="string($value)"><gl-cor:taxCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:taxCode></xsl:if>
<!--BT-144--><xsl:variable name="value" select="./RowFreeText"/><xsl:if test="string($value)"><gl-cor:taxCommentExemption contextRef="now"><xsl:value-of select="$value"/></gl-cor:taxCommentExemption></xsl:if>
</gl-cor:taxes>



<xsl:variable name="value" select="//OriginalInvoiceNumber"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">credit-note</gl-taf:originatingDocumentType>
<!--BT-21--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="//AgreementIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">contract</gl-taf:originatingDocumentType>
<!--BT-10--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="//OrderIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">order-customer</gl-taf:originatingDocumentType>
<!--BT-11--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>


<xsl:variable name="value" select="//OrderConfirmationIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">order-vendor</gl-taf:originatingDocumentType>
<!--BT-12--><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>



<!--BT-14--><xsl:variable name="value" select="//DeliveryNoteIdentifier"/><xsl:if test="string($value)"><gl-taf:originatingDocumentStructure><gl-taf:originatingDocumentType contextRef="now">despatch-advice</gl-taf:originatingDocumentType><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber></gl-taf:originatingDocumentStructure></xsl:if>


<xsl:variable name="value" select="//EpiRemittanceInfoIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">finance-charge</gl-taf:originatingDocumentType>
<gl-taf:originatingDocumentIdentifierCode contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentIdentifierCode>
</gl-taf:originatingDocumentStructure>
</xsl:if>

<xsl:variable name="value" select="./RowIdentifier"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType>order-customer</gl-taf:originatingDocumentType>
<gl-taf:originatingDocumentNumber><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>


</gl-cor:entryDetail>


</xsl:for-each>
</gl-cor:entryHeader>




</gl-cor:accountingEntries>
 
</xbrli:xbrl>
</xsl:template>
 
 
 
 
 
</xsl:stylesheet>