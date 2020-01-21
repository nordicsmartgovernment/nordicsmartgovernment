<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" 
xmlns:ix="http://www.xbrl.org/2008/inlineXBRL" 
xmlns:ixt="http://www.xbrl.org/inlineXBRL/transformation/2010-04-20" 
xmlns:fn="http://www.w3.org/2005/02/xpath-functions"
xmlns:iso4217="http://www.xbrl.org/2003/iso4217" 
xmlns:link="http://www.xbrl.org/2003/linkbase" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns="urn:iso:std:iso:20022:tech:xsd:camt.053.001.02"
xpath-default-namespace="urn:iso:std:iso:20022:tech:xsd:camt.053.001.08"
>

<!--All namespace declarations reagrding the instance document above-->

<xsl:output method="xml" encoding="utf-8"  indent="yes"/>

<xsl:decimal-format name="dkk" decimal-separator="," grouping-separator="&#160;"/>
<!--
<xsl:decimal-format name='base' decimal-separator=',' grouping-separator='.' minus-sign='-' />
 -->
<!-- string for default namespace uri and schema location -->
 <!-- <xsl:variable name="ns" select="'http://www.w3.org/2001/XMLSchema-instance'"/>-->
  <xsl:variable name="ns" select="'urn:iso:std:iso:20022:tech:xsd:camt.053.001.08'"/>
  
  <xsl:variable name="schemaLoc" select="'urn:iso:std:iso:20022:tech:xsd:camt.053.001.08 camt.053.001.08.xsd'"/>

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
<xbrli:xbrl xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.xbrl.org/int/gl/plt/2016-12-01 ../../../taxonomies/XBRL-GL-REC-2017-01-01-fi/gl/plt/case-c-b-m-u-t-s-r/gl-plt-fi-all-2017-01-01.xsd" xmlns:gl-plt="http://www.xbrl.org/int/gl/plt/2016-12-01" xmlns:xbrll="http://www.xbrl.org/2003/linkbase" xmlns:gl-cor="http://www.xbrl.org/int/gl/cor/2016-12-01" xmlns:gl-rapko="http://www.xbrl.org/int/gl/rapko/2015-07-01" xmlns:iso639="http://www.xbrl.org/2005/iso639" xmlns:gl-taf="http://www.xbrl.org/int/gl/taf/2016-12-01" xmlns:gl-muc="http://www.xbrl.org/int/gl/muc/2016-12-01" xmlns:xbrli="http://www.xbrl.org/2003/instance" xmlns:iso4217="http://www.xbrl.org/2003/iso4217" xmlns:gl-bus="http://www.xbrl.org/int/gl/bus/2016-12-01" xmlns:xlink="http://www.w3.org/1999/xlink">
  <xbrll:schemaRef xlink:type="simple" xlink:href="../../../taxonomies/XBRL-GL-REC-2017-01-01-fi/gl/plt/case-c-b-m-u-t-s-r/gl-plt-fi-all-2017-01-01.xsd" xlink:arcrole="http://www.w3.org/1999/xlink/properties/linkbase"/>
  
    <!--Link to account mappings doc between the nordic countries to facilitate automated account postings in the NSG reference implementation-->
<xsl:variable name="ac_map" select="document('account_mapping.xml')"/>
  
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
  
  <!--For each found "Ccy" element or attribut, create unit  //Ccy/text() and //@Ccy-->
<xsl:for-each select="distinct-values(//@Ccy)">
<xsl:variable name="value" select="."/>
<xbrli:unit id="{$value}">
    <xbrli:measure>iso4217:<xsl:value-of select="."/></xbrli:measure>
</xbrli:unit>
</xsl:for-each>


<!--accounting entries-->
<gl-cor:accountingEntries>

<!--document information tuple-->
<gl-cor:documentInfo>
<!--generating entries -->
<gl-cor:entriesType contextRef="now">entries</gl-cor:entriesType>



<!--current time--><gl-cor:creationDate contextRef="now"><xsl:value-of select="current-date()"/></gl-cor:creationDate>

<!-- ISO Index:  1.2.11  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Ccy"/><xsl:if test="string($value)"><gl-muc:defaultCurrency contextRef="now"><xsl:value-of select="$value"/></gl-muc:defaultCurrency></xsl:if>




</gl-cor:documentInfo>

<!--entity information tuple-->
<gl-cor:entityInformation>
<!--Organization Identifiers tuples-->
<gl-bus:organizationIdentifiers>
<!-- ISO Index:  9.1.16  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/GrpHdr/MsgRcpt/Id/OrgId/Othr/Id"/><xsl:if test="string($value)"><gl-bus:organizationIdentifier contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationIdentifier></xsl:if>
</gl-bus:organizationIdentifiers>

<!--Organization Address tuple-->
<gl-bus:organizationAddress>
<!-- ISO Index:  1.2.14  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Ownr/Nm"/><xsl:if test="string($value)"><gl-bus:organizationAddressName contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressName></xsl:if>

<!-- ISO Index:  1.2.16  -->

<xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Ownr/PstlAdr/AdrTp"/><xsl:if test="string($value)"><gl-bus:organizationAddressPurpose contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressPurpose></xsl:if>
<!-- ISO Index:  1.2.17  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Ownr/PstlAdr/Dept"/><xsl:if test="string($value)"><gl-bus:organizationAddressDescription contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressDescription></xsl:if>
<!-- ISO Index:  1.2.19  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Ownr/PstlAdr/StrtNm"/><xsl:if test="string($value)"><gl-bus:organizationAddressStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressStreet></xsl:if>
<!-- ISO Index:  1.2.22  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Ownr/PstlAdr/TwnNm"/><xsl:if test="string($value)"><gl-bus:organizationAddressStateOrProvince contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressStateOrProvince></xsl:if>
<!-- ISO Index:  1.2.21  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Ownr/PstlAdr/PstCd"/><xsl:if test="string($value)"><gl-bus:organizationAddressZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressZipOrPostalCode></xsl:if>
<!-- ISO Index:  1.2.23  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Ownr/PstlAdr/CtrySubDvsn"/><xsl:if test="string($value)"><gl-bus:organizationAddressCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:organizationAddressCountry></xsl:if>
</gl-bus:organizationAddress>
<gl-bus:contactInformation>
<!-- ISO Index:  1.2.49  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Ownr/CtctDtls/NmPrfx"/><xsl:if test="string($value)"><gl-bus:contactPrefix contextRef="now"><xsl:value-of select="$value"/></gl-bus:contactPrefix></xsl:if>
<!-- ISO Index:  1.2.50  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Ownr/CtctDtls/Nm"/><xsl:if test="string($value)"><gl-bus:contactLastName contextRef="now"><xsl:value-of select="$value"/></gl-bus:contactLastName></xsl:if>
<gl-bus:contactPhone>
<!-- ISO Index:  1.2.51  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Ownr/CtctDtls/PhneNb"/><xsl:if test="string($value)"><gl-bus:contactPhoneNumber contextRef="now"><xsl:value-of select="$value"/></gl-bus:contactPhoneNumber></xsl:if>
</gl-bus:contactPhone>
<gl-bus:contactPhone>
<!-- ISO Index:  1.2.52  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Ownr/CtctDtls/MobNb"/><xsl:if test="string($value)"><gl-bus:contactPhoneNumber contextRef="now"><xsl:value-of select="$value"/></gl-bus:contactPhoneNumber></xsl:if>
</gl-bus:contactPhone>
<gl-bus:contactFax>
<!-- ISO Index:  1.2.53  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Ownr/CtctDtls/FaxNb"/><xsl:if test="string($value)"><gl-bus:contactFaxNumber contextRef="now"><xsl:value-of select="$value"/></gl-bus:contactFaxNumber></xsl:if>
</gl-bus:contactFax>
<gl-bus:contactEMail>
<!-- ISO Index:  1.2.54  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Ownr/CtctDtls/EmailAdr"/><xsl:if test="string($value)"><gl-bus:contactEMailAddress contextRef="now"><xsl:value-of select="$value"/></gl-bus:contactEMailAddress></xsl:if>
</gl-bus:contactEMail>
</gl-bus:contactInformation>
</gl-cor:entityInformation>

<gl-cor:entryHeader>
         <gl-cor:sourceJournalID contextRef="now">gi</gl-cor:sourceJournalID>
         <gl-cor:entryDetail>
<!-- For NSG 3 reference implementation default accounts are used on Header type entries where all are considered as entries of the "Trade creditors, short term account"-->

<!--Account tuples-->
<!-- The finnish acount to be used is "191001" (Raportointikoodisto, Standard business reporting code set chart of accounts-->
<gl-cor:account>
<gl-cor:accountMainID contextRef="now">191001</gl-cor:accountMainID><gl-cor:mainAccountTypeDescription contextRef="now">FI</gl-cor:mainAccountTypeDescription><xsl:comment>See the Finnish chart of accounts here, also in swedish: https://koodistot.suomi.fi/extension;registryCode=sbr-fi-code-lists;schemeCode=MC-2019-1;extensionCode=MC65</xsl:comment>
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

<!--For the NSG reference implementation. Add other countries accounting references too for the Cash at bank, money transfers intermediate accounts reconciliations for own use.-->
<xsl:for-each select="$ac_map//gl-cor:account[gl-cor:mainAccountTypeDescription/text() = 'FI' and gl-cor:accountMainID/text()='191001']/parent::gl-cor:entryDetail/child::gl-cor:account">
<xsl:if test="gl-cor:mainAccountTypeDescription/text() != 'FI'">
 <gl-cor:account>
		 <gl-cor:accountMainID contextRef="now"><xsl:value-of select="./gl-cor:accountMainID/text()"/></gl-cor:accountMainID>
		 <gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="./gl-cor:mainAccountTypeDescription/text()"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>
</xsl:if>
</xsl:for-each>


<xsl:variable name="tC" select="//TxsSummry/TtlCdtNtries/Sum"/>
<xsl:variable name="tD" select="//TxsSummry/TtlDbtNtries/Sum"/>
<xsl:variable name="value" select="format-number( round(100*(number($tC)-number($tD))) div 100 ,
'##.00' )"/>
<!--Total values don't have a separate currency indicater, so choosing the account currency-->
<xsl:variable name="ccy" select="//Acct/Ccy"/>

<xsl:variable name="value2" select="2"/>
<xsl:if test="number($value) and number($value2) and string($ccy)">
<gl-cor:amount decimals='{$value2}' contextRef="now" unitRef="{$ccy}"><xsl:value-of select="$value"/></gl-cor:amount>
</xsl:if>

<!--BT-2--><xsl:variable name="value" select="substring(//Stmt/FrToDt/ToDtTm, 1, 10)"/><xsl:if test="string($value)"><gl-cor:postingDate contextRef="now"><xsl:value-of select="$value"/></gl-cor:postingDate></xsl:if>

<gl-bus:amountMemo contextRef="now">false</gl-bus:amountMemo>



<!--Identifier refrence tuples-->
<!--Account information-->
<gl-cor:identifierReference>
<!--AccountDetails-->
<xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Id/IBAN"/><xsl:if test="string($value)">
<gl-cor:identifierExternalReference>
  <!-- ISO Index:  1.2.1  --><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">IBAN</gl-cor:identifierAuthority>
</gl-cor:identifierExternalReference>
</xsl:if>
<xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Id/Othr/Id"/><xsl:if test="string($value)">
<gl-cor:identifierExternalReference>
<!-- ISO Index:  1.2.3  --><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode>
<!-- ISO Index:  1.2.5  Basic bank account numer (BBAN)--><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Id/Othr/SchmeNm/Cd"/><xsl:if test="string($value)"><gl-cor:identifierAuthority
 contextRef="now">BBAN</gl-cor:identifierAuthority></xsl:if>
<!-- ISO Index:  1.2.6  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Id/Othr/SchmeNm/Prtry"/><xsl:if test="string($value)"><gl-cor:identifierAuthority
 contextRef="now">other</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
</xsl:if>
</gl-cor:identifierReference>


<!--Servicer-Financial Institution-->
<!-- ISO Index:  1.2.58  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svcr/FinInstnId/BICFI"/><xsl:if test="string($value)">

<gl-cor:identifierReference>
<gl-cor:identifierExternalReference>
<gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode>
<gl-cor:identifierAuthority  contextRef="now">BIC</gl-cor:identifierAuthority>
</gl-cor:identifierExternalReference>
<!-- ISO Index:  1.2.64  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svcr/FinInstnId/Nm"/><xsl:if test="string($value)">
<gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>
<gl-cor:identifierType contextRef="now">FI-F</gl-cor:identifierType>
<!--Servicer-Financial Institution Address Details-->
<gl-bus:identifierAddress>
<!-- ISO Index:  1.2.67  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/FinInstnIdr/PstlAdr/Dept"/><xsl:if test="string($value)"><gl-bus:identifierAddressDescription contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressDescription></xsl:if>
<!-- ISO Index:  1.2.69  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/FinInstnIdr/PstlAdr/StrtNm"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!-- ISO Index:  1.2.70  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/FinInstnIdr/PstlAdr/BldgNb"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<!-- ISO Index:  1.2.72  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/FinInstnIdr/PstlAdr/TwnNm"/><xsl:if test="string($value)"><gl-bus:identifierStateOrProvince contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStateOrProvince></xsl:if>
<!-- ISO Index:  1.2.73  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/FinInstnIdr/PstlAdr/CtrySubDvsn"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!-- ISO Index:  1.2.71  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/FinInstnIdr/PstlAdr/PstCd"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
</gl-bus:identifierAddress>
</gl-cor:identifierReference>
</xsl:if>



<!--Servicer-Branch-->
<xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/BrnchId/Id"/><xsl:if test="string($value)">
<gl-cor:identifierReference>

<!-- ISO Index:  1.2.83  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/BrnchId/Id"/><xsl:if test="string($value)"><gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode></xsl:if>
<!-- ISO Index:  1.2.84  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/BrnchId/Nm"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>
<!--Identifier Type, BANK_BRANCH-->
<!--TODO kun tyypitys valmis, korjaa tähän-->
<!--
<gl-cor:identifierType contextRef="now">BANK_BRANCH</gl-cor:identifierType>
-->
<!--Identifier Organization Type, organization-->

<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>
<!--TODO kun tyypitys valmis, korjaa tähän-->
<!--
<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-S</gl-cor:identifierType></xsl:if>
-->

<!--Servicer-Branch Address Details-->
<gl-bus:identifierAddress>
<!-- ISO Index:  1.2.87  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/BrnchId/PstlAdrDept"/><xsl:if test="string($value)"><gl-bus:identifierAddressDescription contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressDescription></xsl:if>
<!-- ISO Index:  1.2.89  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/BrnchId/PstlAdrStrtNm"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!-- ISO Index:  1.2.90  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/BrnchId/PstlAdrBldgNb"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<!-- ISO Index:  1.2.92  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/BrnchId/PstlAdrTwnNm"/><xsl:if test="string($value)"><gl-bus:identifierStateOrProvince contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStateOrProvince></xsl:if>
<!-- ISO Index:  1.2.93  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/BrnchId/PstlAdrCtrySubDvsn"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!-- ISO Index:  1.2.91  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/BrnchId/PstlAdrPstCd"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
</gl-bus:identifierAddress>
</gl-cor:identifierReference>
<!--end of Servicer-Branch-->
</xsl:if>


<gl-cor:documentType contextRef="now">finance-charge</gl-cor:documentType>


<xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/LglSeqNb"/>
 <xsl:choose>
 <xsl:when test="not(string($value))">
<!--prio 1-->
<!-- ISO Index:  2.3  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/LglSeqNb"/>  
    <xsl:choose>
    <xsl:when test="not(string($value))">
    <!--prio 2-->
    <!-- ISO Index:  2.2  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/ElctrncSeqNb"/>
        <xsl:choose>
            <xsl:when test="not(string($value))">
            <!--prio 3-->
            <!-- ISO Index:  2.1  -->
			<xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Id"/>
            <xsl:if test="string($value)"><gl-cor:documentNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentNumber></xsl:if>
            </xsl:when>
            <xsl:otherwise>
			<!--
            <gl-cor:documentNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentNumber>
			-->
            </xsl:otherwise>
        </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
	<!--
	<gl-cor:documentNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentNumber></xsl:otherwise>
	-->
	</xsl:otherwise>
    </xsl:choose>
</xsl:when>
<xsl:otherwise>
<!--
<gl-cor:documentNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentNumber>
-->
</xsl:otherwise>
</xsl:choose>


<!-- ISO Index:  2.4  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/CreDtTm"/><xsl:if test="string($value)"><gl-cor:documentDate contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentDate></xsl:if>

<gl-cor:detailComment contextRef="now">Header</gl-cor:detailComment>

</gl-cor:entryDetail>

<xsl:for-each select="//BkToCstmrStmt[1]/Stmt/Ntry/NtryDtls">


<xsl:for-each select="./TxDtls">
<!--Entry Detail tuples-->
<gl-cor:entryDetail>
<!--Not applicable for NSG POC because of Header and Header VAT rows-->
<!-- ISO Index:  2.77  -->
<!--<xsl:variable name="value" select="./NtryRef"/><xsl:if test="string($value)"><gl-cor:lineNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:lineNumber></xsl:if>-->

<!--TODO kato tässä  unitref versus amountCurrenct-elementti mäppäyksistä-->
<!--prio 1-->
<!-- ISO Index:  2.1.10  -->
<xsl:variable name="value" select="./AmtDtls/TxAmt/Amt"/>
<xsl:variable name="ccy" select="./AmtDtls/TxAmt/Amt/@Ccy"/>
<xsl:variable name="value2" select="string-length(substring-after(./AmtDtls/TxAmt/Amt, '.'))"/>
<xsl:variable name="CdtDbt" select="$value/ancestor::Ntry/CdtDbtInd"/>

<xsl:if test="number($value) and string($value2) and string($ccy) and string($CdtDbt)">

<xsl:choose>
     <xsl:when test="$CdtDbt/text()='CRDT'">
<!--Account tuples-->
<!-- The finnish account to be used is "17001" (Raportointikoodisto, Standard business reporting code set chart of accounts-->
<gl-cor:account>
<gl-cor:accountMainID contextRef="now">17001</gl-cor:accountMainID><gl-cor:mainAccountTypeDescription contextRef="now">FI</gl-cor:mainAccountTypeDescription><xsl:comment>See the Finnish chart of accounts here, also in swedish: https://koodistot.suomi.fi/extension;registryCode=sbr-fi-code-lists;schemeCode=MC-2019-1;extensionCode=MC65</xsl:comment>
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

<!--For NSG 3 reference implementation. Add other countries accounting references too for the Trade debtors, short term.-->
<xsl:for-each select="$ac_map//gl-cor:account[gl-cor:mainAccountTypeDescription/text() = 'FI' and gl-cor:accountMainID/text()='17001']/parent::gl-cor:entryDetail/child::gl-cor:account">
<xsl:if test="gl-cor:mainAccountTypeDescription/text() != 'FI'">
 <gl-cor:account>
		 <gl-cor:accountMainID contextRef="now"><xsl:value-of select="./gl-cor:accountMainID/text()"/></gl-cor:accountMainID>
		 <gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="./gl-cor:mainAccountTypeDescription/text()"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>
</xsl:if>
</xsl:for-each>
        <gl-cor:amount decimals='{$value2}' contextRef="now" unitRef="{$ccy}"><xsl:value-of select="$value *(-1)"/></gl-cor:amount>
     </xsl:when>
     <xsl:otherwise>
	 
	 <!--Account tuples-->
<!-- The finnish account to be used is "290601" (Raportointikoodisto, Standard business reporting code set chart of accounts-->
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

<!--For NSG 3 reference implementation. Add other countries accounting references too for the Trade creditors, short term.-->
<xsl:for-each select="$ac_map//gl-cor:account[gl-cor:mainAccountTypeDescription/text() = 'FI' and gl-cor:accountMainID/text()='290601']/parent::gl-cor:entryDetail/child::gl-cor:account">
<xsl:if test="gl-cor:mainAccountTypeDescription/text() != 'FI'">
 <gl-cor:account>
		 <gl-cor:accountMainID contextRef="now"><xsl:value-of select="./gl-cor:accountMainID/text()"/></gl-cor:accountMainID>
		 <gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="./gl-cor:mainAccountTypeDescription/text()"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>
</xsl:if>
</xsl:for-each>
       <gl-cor:amount decimals='{$value2}' contextRef="now" unitRef="{$ccy}"><xsl:value-of select="$value"/></gl-cor:amount>
     </xsl:otherwise>
</xsl:choose>
</xsl:if>


<!--prio 2-->
<!-- ISO Index:  2.246  -->
<xsl:variable name="value" select="./RmtInf/Strd/RfrdDocAmt/DuePyblAmt"/>
<xsl:variable name="ccy" select="./RmtInf/Strd/RfrdDocAmt/DuePyblAmt/@Ccy"/>
<xsl:variable name="value2" select="string-length(substring-after(./RmtInf/Strd/RfrdDocAmt/DuePyblAmt, '.'))"/>
<xsl:variable name="CdtDbt" select="$value/ancestor::Ntry/CdtDbtInd"/>
<xsl:if test="string($value) and string($value2) and string($ccy) and string($CdtDbt)">
<xsl:choose>
     <xsl:when test="$CdtDbt/text()='CRDT'">
	 <!--Account tuples-->
<!-- The finnish account to be used is "17001" (Raportointikoodisto, Standard business reporting code set chart of accounts-->
<gl-cor:account>
<gl-cor:accountMainID contextRef="now">17001</gl-cor:accountMainID><gl-cor:mainAccountTypeDescription contextRef="now">FI</gl-cor:mainAccountTypeDescription><xsl:comment>See the Finnish chart of accounts here, also in swedish: https://koodistot.suomi.fi/extension;registryCode=sbr-fi-code-lists;schemeCode=MC-2019-1;extensionCode=MC65</xsl:comment>
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

<!--For NSG 3 reference implementation. Add other countries accounting references too for the Trade debtors, short term.-->
<xsl:for-each select="$ac_map//gl-cor:account[gl-cor:mainAccountTypeDescription/text() = 'FI' and gl-cor:accountMainID/text()='17001']/parent::gl-cor:entryDetail/child::gl-cor:account">
<xsl:if test="gl-cor:mainAccountTypeDescription/text() != 'FI'">
 <gl-cor:account>
		 <gl-cor:accountMainID contextRef="now"><xsl:value-of select="./gl-cor:accountMainID/text()"/></gl-cor:accountMainID>
		 <gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="./gl-cor:mainAccountTypeDescription/text()"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>
</xsl:if>
</xsl:for-each>
<!--Account tuples-->
<!-- The finnish account to be used is "17001" (Raportointikoodisto, Standard business reporting code set chart of accounts-->
<gl-cor:account>
<gl-cor:accountMainID contextRef="now">17001</gl-cor:accountMainID><gl-cor:mainAccountTypeDescription contextRef="now">FI</gl-cor:mainAccountTypeDescription><xsl:comment>See the Finnish chart of accounts here, also in swedish: https://koodistot.suomi.fi/extension;registryCode=sbr-fi-code-lists;schemeCode=MC-2019-1;extensionCode=MC65</xsl:comment>
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

<!--For NSG 3 reference implementation. Add other countries accounting references too for the Trade debtors, short term.-->
<xsl:for-each select="$ac_map//gl-cor:account[gl-cor:mainAccountTypeDescription/text() = 'FI' and gl-cor:accountMainID/text()='17001']/parent::gl-cor:entryDetail/child::gl-cor:account">
<xsl:if test="gl-cor:mainAccountTypeDescription/text() != 'FI'">
 <gl-cor:account>
		 <gl-cor:accountMainID contextRef="now"><xsl:value-of select="./gl-cor:accountMainID/text()"/></gl-cor:accountMainID>
		 <gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="./gl-cor:mainAccountTypeDescription/text()"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>
</xsl:if>
</xsl:for-each>
        <gl-cor:amount decimals='{$value2}' contextRef="now" unitRef="{$ccy}"><xsl:value-of select="$value *(-1)"/></gl-cor:amount>
     </xsl:when>
     <xsl:otherwise>
	 	 
	 <!--Account tuples-->
<!-- The finnish account to be used is "290601" (Raportointikoodisto, Standard business reporting code set chart of accounts-->
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

<!--For NSG 3 reference implementation. Add other countries accounting references too for the Trade creditors, short term.-->
<xsl:for-each select="$ac_map//gl-cor:account[gl-cor:mainAccountTypeDescription/text() = 'FI' and gl-cor:accountMainID/text()='290601']/parent::gl-cor:entryDetail/child::gl-cor:account">
<xsl:if test="gl-cor:mainAccountTypeDescription/text() != 'FI'">
 <gl-cor:account>
		 <gl-cor:accountMainID contextRef="now"><xsl:value-of select="./gl-cor:accountMainID/text()"/></gl-cor:accountMainID>
		 <gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="./gl-cor:mainAccountTypeDescription/text()"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>
</xsl:if>
</xsl:for-each>
       <gl-cor:amount decimals='{$value2}' contextRef="now" unitRef="{$ccy}"><xsl:value-of select="$value"/></gl-cor:amount>
     </xsl:otherwise>
</xsl:choose>
</xsl:if>


<!--prio 3-->
<!-- ISO Index:  2.246  -->
<xsl:variable name="value" select="./RmtInf/Strd/RfrdDocAmt/CdtNoteAmt"/>
<xsl:variable name="ccy" select="./RmtInf/Strd/RfrdDocAmt/CdtNoteAmt/@Ccy"/>
<xsl:variable name="value2" select="string-length(substring-after(./RmtInf/Strd/RfrdDocAmt/CdtNoteAmt, '.'))"/>
<xsl:variable name="CdtDbt" select="$value/ancestor::Ntry/CdtDbtInd"/>
<xsl:if test="string($value) and string($value2) and string($ccy) and string($CdtDbt)">
<xsl:choose>
     <xsl:when test="$CdtDbt/text()='CRDT'">
	 <!--Account tuples-->
<!-- The finnish account to be used is "17001" (Raportointikoodisto, Standard business reporting code set chart of accounts-->
<gl-cor:account>
<gl-cor:accountMainID contextRef="now">17001</gl-cor:accountMainID><gl-cor:mainAccountTypeDescription contextRef="now">FI</gl-cor:mainAccountTypeDescription><xsl:comment>See the Finnish chart of accounts here, also in swedish: https://koodistot.suomi.fi/extension;registryCode=sbr-fi-code-lists;schemeCode=MC-2019-1;extensionCode=MC65</xsl:comment>
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

<!--For NSG 3 reference implementation. Add other countries accounting references too for the Trade debtors, short term.-->
<xsl:for-each select="$ac_map//gl-cor:account[gl-cor:mainAccountTypeDescription/text() = 'FI' and gl-cor:accountMainID/text()='17001']/parent::gl-cor:entryDetail/child::gl-cor:account">
<xsl:if test="gl-cor:mainAccountTypeDescription/text() != 'FI'">
 <gl-cor:account>
		 <gl-cor:accountMainID contextRef="now"><xsl:value-of select="./gl-cor:accountMainID/text()"/></gl-cor:accountMainID>
		 <gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="./gl-cor:mainAccountTypeDescription/text()"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>
</xsl:if>
</xsl:for-each>
        <gl-cor:amount decimals='{$value2}' contextRef="now" unitRef="{$ccy}"><xsl:value-of select="$value *(-1)"/></gl-cor:amount>
     </xsl:when>
     <xsl:otherwise>
	 	 
	 <!--Account tuples-->
<!-- The finnish account to be used is "290601" (Raportointikoodisto, Standard business reporting code set chart of accounts-->
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

<!--For NSG 3 reference implementation. Add other countries accounting references too for the Trade creditors, short term.-->
<xsl:for-each select="$ac_map//gl-cor:account[gl-cor:mainAccountTypeDescription/text() = 'FI' and gl-cor:accountMainID/text()='290601']/parent::gl-cor:entryDetail/child::gl-cor:account">
<xsl:if test="gl-cor:mainAccountTypeDescription/text() != 'FI'">
 <gl-cor:account>
		 <gl-cor:accountMainID contextRef="now"><xsl:value-of select="./gl-cor:accountMainID/text()"/></gl-cor:accountMainID>
		 <gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="./gl-cor:mainAccountTypeDescription/text()"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>
</xsl:if>
</xsl:for-each>
       <gl-cor:amount decimals='{$value2}' contextRef="now" unitRef="{$ccy}"><xsl:value-of select="$value"/></gl-cor:amount>
     </xsl:otherwise>
</xsl:choose>
</xsl:if>




<!--prio 4-->
<!-- ISO Index:  2.255   -->
<xsl:variable name="value" select="./RmtInf/Strd/RfrdDocAmt/RmtdAmt"/>
<xsl:variable name="ccy" select="./RmtInf/Strd/RfrdDocAmt/RmtdAmt/@Ccy"/>
<xsl:variable name="value2" select="string-length(substring-after(./RmtInf/Strd/RfrdDocAmt/RmtdAmt, '.'))"/>
<xsl:variable name="CdtDbt" select="$value/ancestor::Ntry/CdtDbtInd"/>

<xsl:if test="string($value) and string($value2) and string($ccy) and string($CdtDbt)">
<xsl:choose>
     <xsl:when test="$CdtDbt/text()='CRDT'">
	 <!--Account tuples-->
<!-- The finnish account to be used is "17001" (Raportointikoodisto, Standard business reporting code set chart of accounts-->
<gl-cor:account>
<gl-cor:accountMainID contextRef="now">17001</gl-cor:accountMainID><gl-cor:mainAccountTypeDescription contextRef="now">FI</gl-cor:mainAccountTypeDescription><xsl:comment>See the Finnish chart of accounts here, also in swedish: https://koodistot.suomi.fi/extension;registryCode=sbr-fi-code-lists;schemeCode=MC-2019-1;extensionCode=MC65</xsl:comment>
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

<!--For NSG 3 reference implementation. Add other countries accounting references too for the Trade debtors, short term.-->
<xsl:for-each select="$ac_map//gl-cor:account[gl-cor:mainAccountTypeDescription/text() = 'FI' and gl-cor:accountMainID/text()='17001']/parent::gl-cor:entryDetail/child::gl-cor:account">
<xsl:if test="gl-cor:mainAccountTypeDescription/text() != 'FI'">
 <gl-cor:account>
		 <gl-cor:accountMainID contextRef="now"><xsl:value-of select="./gl-cor:accountMainID/text()"/></gl-cor:accountMainID>
		 <gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="./gl-cor:mainAccountTypeDescription/text()"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>
</xsl:if>
</xsl:for-each>
        <gl-cor:amount decimals='{$value2}' contextRef="now" unitRef="{$ccy}"><xsl:value-of select="$value *(-1)"/></gl-cor:amount>
     </xsl:when>
     <xsl:otherwise>
	 	 
	 <!--Account tuples-->
<!-- The finnish account to be used is "290601" (Raportointikoodisto, Standard business reporting code set chart of accounts-->
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

<!--For NSG 3 reference implementation. Add other countries accounting references too for the Trade creditors, short term.-->
<xsl:for-each select="$ac_map//gl-cor:account[gl-cor:mainAccountTypeDescription/text() = 'FI' and gl-cor:accountMainID/text()='290601']/parent::gl-cor:entryDetail/child::gl-cor:account">
<xsl:if test="gl-cor:mainAccountTypeDescription/text() != 'FI'">
 <gl-cor:account>
		 <gl-cor:accountMainID contextRef="now"><xsl:value-of select="./gl-cor:accountMainID/text()"/></gl-cor:accountMainID>
		 <gl-cor:mainAccountTypeDescription contextRef="now"><xsl:value-of select="./gl-cor:mainAccountTypeDescription/text()"/></gl-cor:mainAccountTypeDescription>
</gl-cor:account>
</xsl:if>
</xsl:for-each>
       <gl-cor:amount decimals='{$value2}' contextRef="now" unitRef="{$ccy}"><xsl:value-of select="$value"/></gl-cor:amount>
     </xsl:otherwise>
</xsl:choose>
</xsl:if>


<!-- ISO Index:  2.1.4  --><xsl:variable name="value" select="./AmtDtls/InstdAmt/CcyXchg/TrgtCcy"/><xsl:if test="string($value)"><gl-muc:amountCurrency contextRef="now"><xsl:value-of select="$value"/></gl-muc:amountCurrency></xsl:if>


<!-- ISO Index:  4.1.0  --><xsl:variable name="value" select="./ValDt/Dt"/><xsl:if test="string($value)"><gl-muc:amountOriginalExchangeRateDate contextRef="now"><xsl:value-of select="$value"/></gl-muc:amountOriginalExchangeRateDate></xsl:if>
<!-- ISO Index:  4.1.1  --><xsl:variable name="value" select="./ValDt/DtTm"/><xsl:if test="string($value)"><gl-muc:amountOriginalExchangeRateDate contextRef="now"><xsl:value-of select="substring($value,1,10)"/></gl-muc:amountOriginalExchangeRateDate></xsl:if>
<!-- ISO Index:  2.1.1  --><xsl:variable name="value" select="./AmtDtls/InstdAmt/Amt"/><xsl:variable name="ccy" select="./AmtDtls/InstdAmt/Amt/@Ccy"/><xsl:if test="string($value) and string($ccy)"><xsl:variable name="value2" select="string-length(substring-after(./AmtDtls/InstdAmt/Amt, '.'))"/><gl-muc:amountOriginalAmount contextRef="now" decimals="{$value2}" unitRef="{$ccy}"><xsl:value-of select="$value"/></gl-muc:amountOriginalAmount></xsl:if>
<!-- ISO Index:  2.1.3  --><xsl:variable name="value" select="./AmtDtls/InstdAmt/CcyXchg/SrcCcy"/><xsl:if test="string($value)"><gl-muc:amountOriginalCurrency contextRef="now"><xsl:value-of select="$value"/></gl-muc:amountOriginalCurrency></xsl:if>
<!-- ISO Index:  2.1.6  --><xsl:variable name="value" select="./AmtDtls/InstdAmt/CcyXchg/XchgRate"/><xsl:if test="string($value)">
<xsl:variable name="value2" select="string-length(substring-after(./AmtDtls/InstdAmt/CcyXchg/XchgRate, '.'))"/><gl-muc:amountOriginalExchangeRate contextRef="now" decimals="{$value2}" unitRef="NotUsed"><xsl:value-of select="$value"/></gl-muc:amountOriginalExchangeRate></xsl:if>


<!-- ISO Index:  2.1.5  --><xsl:variable name="value" select="./AmtDtls/InstdAmt/CcyXchg/UnitCcy"/><xsl:if test="string($value)"><gl-muc:amountOriginalTriangulationCurrency contextRef="now"><xsl:value-of select="$value"/></gl-muc:amountOriginalTriangulationCurrency></xsl:if>

<!--Bank statement D/C is the opposite to accounting D/C)-->
<!-- ISO Index:  2.79  --><xsl:variable name="value" select="./CdtDbtInd[text()='DBIT']"/><xsl:if test="string($value)"><gl-cor:debitCreditCode contextRef="now">C</gl-cor:debitCreditCode></xsl:if>
<!-- ISO Index:  2.79  --><xsl:variable name="value" select="./CdtDbtInd[text()='CRDT']"/><xsl:if test="string($value)"><gl-cor:debitCreditCode contextRef="now">D</gl-cor:debitCreditCode></xsl:if>
<!-- ISO Index:  2.80  --><xsl:variable name="value" select="./RvslInd[text()='true']"/><xsl:if test="string($value)"><gl-bus:amountMemo contextRef="now"><xsl:value-of select="$value"/></gl-bus:amountMemo></xsl:if>


<!-- ISO Index:  4.1.0  --><xsl:variable name="value" select="./ancestor::Ntry/BookgDt/Dt"/><xsl:if test="string($value)"><gl-cor:postingDate contextRef="now"><xsl:value-of select="$value"/></gl-cor:postingDate></xsl:if>
<!-- ISO Index:  4.1.1  --><xsl:variable name="value" select="./ancestor::Ntry/BookgDt/DtTm"/><xsl:if test="string($value)"><gl-cor:postingDate contextRef="now"><xsl:value-of select="substring($value,1,10)"/></gl-cor:postingDate></xsl:if>
<!-- ISO Index:  2.80  --><xsl:variable name="value" select="./Sts[text()='BOOK']"/><xsl:if test="string($value)"><gl-bus:amountMemo contextRef="now">false</gl-bus:amountMemo></xsl:if>


<!--Identifier refrence tuples-->
<!--Account information-->
<!--TODO identifierCode ja identifierType tähän-->



<gl-cor:identifierReference>
<!--AccountDetails-->
<xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Id/IBAN"/><xsl:if test="string($value)">
<gl-cor:identifierExternalReference>
  <!-- ISO Index:  1.2.1  --><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">IBAN</gl-cor:identifierAuthority>
</gl-cor:identifierExternalReference>
</xsl:if>
<xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Id/Othr/Id"/><xsl:if test="string($value)">
<gl-cor:identifierExternalReference>
<!-- ISO Index:  1.2.3  --><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode>
<!-- ISO Index:  1.2.5  Basic bank account numer (BBAN)--><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Id/Othr/SchmeNm/Cd"/><xsl:if test="string($value)"><gl-cor:identifierAuthority
 contextRef="now">BBAN</gl-cor:identifierAuthority></xsl:if>
<!-- ISO Index:  1.2.6  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Id/Othr/SchmeNm/Prtry"/><xsl:if test="string($value)"><gl-cor:identifierAuthority
 contextRef="now">other</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
</xsl:if>
<!--TODO kelaa tän tuplen alkuun ja hoida tyypitys etc esim bank-account-->
</gl-cor:identifierReference>


<!-- ISO Index:  1.2.58  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svcr/FinInstnId/BICFI"/><xsl:if test="string($value)">

<gl-cor:identifierReference>
<gl-cor:identifierExternalReference>
<gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode>
<gl-cor:identifierAuthority  contextRef="now">BIC</gl-cor:identifierAuthority>
</gl-cor:identifierExternalReference>
<!-- ISO Index:  1.2.64  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svcr/FinInstnId/Nm"/><xsl:if test="string($value)">
<gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>
<gl-cor:identifierType contextRef="now">FI-F</gl-cor:identifierType>
</gl-cor:identifierReference>
</xsl:if>

<xsl:variable name="value" select="./DxDtls/Refs/PmtInfId"/><xsl:if test="string($value)">
<gl-cor:identifierReference>
<!-- ISO Index:  2.146  --><gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode>
<!--TODO korjaa kun tyypitys on tehty-->
<!--<gl-cor:identifierType contextRef="now">BANK_PAYMENT_INFORMATION</gl-cor:identifierType>-->
</gl-cor:identifierReference>
</xsl:if>

<xsl:variable name="value" select="./DxDtls/Refs/InstrId"/><xsl:if test="string($value)">
<gl-cor:identifierReference>
<!-- ISO Index:  2.147  --><gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode>
<!--TODO korjaa kun tyypitys on tehty-->
<!--<gl-cor:identifierType contextRef="now">BANK_INSTRUCTION</gl-cor:identifierType>-->
</gl-cor:identifierReference>
</xsl:if>

<xsl:variable name="value" select="./DxDtls/Refs/EndToEndId"/><xsl:if test="string($value)">
<gl-cor:identifierReference>
<!-- ISO Index:  2.148  --><gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode>
<!--TODO korjaa kun tyypitys on tehty-->
<!--<gl-cor:identifierType contextRef="now">BANK_ENDTOEND</gl-cor:identifierType>-->
</gl-cor:identifierReference>
</xsl:if>

<xsl:variable name="value" select="./DxDtls/Refs/Prtry/Ref"/><xsl:if test="string($value)">
<gl-cor:identifierReference>
<!-- ISO Index:  2.155  --><gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode>
<!--TODO korjaa kun tyypitys on tehty-->
<!--<gl-cor:identifierType contextRef="now">BANK_OTHER</gl-cor:identifierType>-->
</gl-cor:identifierReference>
</xsl:if>



<!--Servicer-Financial Institution-->
<xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/FinInstnIdr/BICFI"/><xsl:if test="string($value)">
<gl-cor:identifierReference>
<gl-cor:identifierExternalReference>
<!-- ISO Index:  1.2.58  --><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">BIC</gl-cor:identifierAuthority>
</gl-cor:identifierExternalReference>
<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>
<!--TODO korjaa kun osapuoli tunnistettu-->
<!--<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-S</gl-cor:identifierType></xsl:if>-->


<!--Servicer-Financial Institution Address Details-->
<gl-bus:identifierAddress>
<!-- ISO Index:  1.2.67  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/FinInstnIdr/PstlAdr/Dept"/><xsl:if test="string($value)"><gl-bus:identifierAddressDescription contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressDescription></xsl:if>
<!-- ISO Index:  1.2.69  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/FinInstnIdr/PstlAdr/StrtNm"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!-- ISO Index:  1.2.70  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/FinInstnIdr/PstlAdr/BldgNb"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<!-- ISO Index:  1.2.72  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/FinInstnIdr/PstlAdr/TwnNm"/><xsl:if test="string($value)"><gl-bus:identifierStateOrProvince contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStateOrProvince></xsl:if>
<!-- ISO Index:  1.2.73  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/FinInstnIdr/PstlAdr/CtrySubDvsn"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!-- ISO Index:  1.2.71  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/FinInstnIdr/PstlAdr/PstCd"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
</gl-bus:identifierAddress>
</gl-cor:identifierReference>
<!--end of Servicer-Financial Institution-->
</xsl:if>



<xsl:variable name="value" select="./RltdAgts/DbtrAgt/FinInstnId/BICFI"/><xsl:if test="string($value)">
<!--DebtorAgent-Financial Institution-->
<gl-cor:identifierReference>
<!-- ISO Index:  6.1.6  --><xsl:variable name="value" select="./RltdAgts/DbtrAgt/FinInstnId/ClrSysMmbId/MmbId"/><xsl:if test="string($value)"><gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode></xsl:if>
<!-- ISO Index:  6.1.7  --><xsl:variable name="value" select="./RltdAgts/DbtrAgt/FinInstnId/ClrSysMmbId/Nm"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>
<gl-cor:identifierExternalReference>
<!-- ISO Index:  1.2.58  --><xsl:variable name="value" select="./RltdAgts/DbtrAgt/FinInstnId/BICFI"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">BIC</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>
<!--TODO kun osapuolia tarkennetaan-->
<!--
<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-B</gl-cor:identifierType></xsl:if>
-->
</gl-cor:identifierReference>
<!--end of DebtorAgent-Financial Institution-->

</xsl:if>

<xsl:variable name="value" select="./RltdAgts/CdtrAgt/FinInstnId/BICFI"/><xsl:if test="string($value)">
<!--CreditorAgent-Financial Institution-->
<gl-cor:identifierReference>
<!-- ISO Index:  6.1.6  --><xsl:variable name="value" select="./RltdAgts/CdtrAgt/FinInstnId/ClrSysMmbId/MmbId"/><xsl:if test="string($value)"><gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode></xsl:if>
<!-- ISO Index:  6.1.7  --><xsl:variable name="value" select="./RltdAgts/CdtrAgt/FinInstnId/ClrSysMmbId/Nm"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>
<gl-cor:identifierExternalReference>
<!-- ISO Index:  6.1.1  --><xsl:variable name="value" select="./RltdAgts/CdtrAgt/FinInstnId/BICFI"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">BIC</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>
<!--TODO korjaa kun osapuoli tunnistettu-->
<!--<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-S</gl-cor:identifierType></xsl:if>-->
</gl-cor:identifierReference>
<!--end of CreditorAgent-Financial Institution-->
</xsl:if>

<xsl:variable name="value" select="./RltdPties/Dbtr/Pty/Nm"/><xsl:if test="string($value)">
<!--Debtor-->
<gl-cor:identifierReference>
<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>

<xsl:variable name="value" select="./RltdPties/Dbtr/Pty/Nm"/><xsl:if test="string($value)">
<gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription><gl-cor:identifierType contextRef="now">FI-B</gl-cor:identifierType></xsl:if>

<xsl:variable name="value" select="./RltdPties/Dbtr/Pty/PstlAdr/Dept"/>
<!--Debtor Address Details-->
<gl-bus:identifierAddress>
<!-- ISO Index:  9.1.3  --><xsl:variable name="value" select="./RltdPties/Dbtr/Pty/PstlAdr/Dept"/><xsl:if test="string($value)"><gl-bus:identifierAddressDescription contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressDescription></xsl:if>
<!-- ISO Index:  9.1.5  --><xsl:variable name="value" select="./RltdPties/Dbtr/Pty/PstlAdr/StrtNm"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!-- ISO Index:  9.1.6  --><xsl:variable name="value" select="./RltdPties/Dbtr/Pty/PstlAdr/BldgNb"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<!-- ISO Index:  9.1.8  --><xsl:variable name="value" select="./RltdPties/Dbtr/Pty/PstlAdr/TwnNm"/><xsl:if test="string($value)"><gl-bus:identifierStateOrProvince contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStateOrProvince></xsl:if>
<!-- ISO Index:  9.1.9  --><xsl:variable name="value" select="./RltdPties/Dbtr/Pty/PstlAdr/CtrySubDvsn"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!-- ISO Index:  9.1.7  --><xsl:variable name="value" select="./RltdPties/Dbtr/Pty/PstlAdr/PstCd"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>

</gl-bus:identifierAddress>
</gl-cor:identifierReference>
<!--end of Debtor-->
</xsl:if>

<xsl:variable name="value" select="./RltdPties/UltmtDbtr/Pty/Nm"/><xsl:if test="string($value)">
<!--Ultimate Debtor-->
<gl-cor:identifierReference>


<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>

<xsl:variable name="value" select="./RltdPties/UltmtDbtr/Pty/Nm"/><xsl:if test="string($value)">
<gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription><gl-cor:identifierType contextRef="now">FI-B</gl-cor:identifierType></xsl:if>

<xsl:variable name="value" select="./RltdPties/UltmtDbtr/Pty/PstlAdr/Dept"/><xsl:if test="string($value)">

<!--Ultimate Debtor Address Details-->
<gl-bus:identifierAddress>
<!-- ISO Index:  9.1.3  --><xsl:variable name="value" select="./RltdPties/UltmtDbtr/Pty/PstlAdr/Dept"/><xsl:if test="string($value)"><gl-bus:identifierAddressDescription contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressDescription></xsl:if>
<!-- ISO Index:  9.1.5  --><xsl:variable name="value" select="./RltdPties/UltmtDbtr/Pty/PstlAdr/StrtNm"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!-- ISO Index:  9.1.6  --><xsl:variable name="value" select="./RltdPties/UltmtDbtr/Pty/PstlAdr/BldgNb"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<!-- ISO Index:  9.1.8  --><xsl:variable name="value" select="./RltdPties/UltmtDbtr/Pty/PstlAdr/TwnNm"/><xsl:if test="string($value)"><gl-bus:identifierStateOrProvince contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStateOrProvince></xsl:if>
<!-- ISO Index:  9.1.9  --><xsl:variable name="value" select="./RltdPties/UltmtDbtr/Pty/PstlAdr/CtrySubDvsn"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!-- ISO Index:  9.1.7  --><xsl:variable name="value" select="./RltdPties/UltmtDbtr/Pty/PstlAdr/PstCd"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
</gl-bus:identifierAddress>
</xsl:if>
</gl-cor:identifierReference>
</xsl:if>
<!--end of Ultimate Debtor-->



<!-- ISO Index:  9.1.3  --><xsl:variable name="value" select="./RltdPties/UlmtCdtr/Pty/PstlAdr/Dept"/><xsl:if test="string($value)">
<!--Ultimate Creditor-->
<gl-cor:identifierReference>
<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>

<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-P</gl-cor:identifierType></xsl:if>
<!--Ultimate Creditor Address Details-->
<gl-bus:identifierAddress>
<!-- ISO Index:  9.1.3  --><xsl:variable name="value" select="./RltdPties/UlmtCdtr/Pty/PstlAdr/Dept"/><xsl:if test="string($value)"><gl-bus:identifierAddressDescription contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressDescription></xsl:if>
<!-- ISO Index:  9.1.5  --><xsl:variable name="value" select="./RltdPties/UlmtCdtr/Pty/PstlAdr/StrtNm"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!-- ISO Index:  9.1.6  --><xsl:variable name="value" select="./RltdPties/UlmtCdtr/Pty/PstlAdr/BldgNb"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<!-- ISO Index:  9.1.8  --><xsl:variable name="value" select="./RltdPties/UlmtCdtr/Pty/PstlAdr/TwnNm"/><xsl:if test="string($value)"><gl-bus:identifierStateOrProvince contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStateOrProvince></xsl:if>
<!-- ISO Index:  9.1.9  --><xsl:variable name="value" select="./RltdPties/UlmtCdtr/Pty/PstlAdr/CtrySubDvsn"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!-- ISO Index:  9.1.7  --><xsl:variable name="value" select="./RltdPties/UlmtCdtr/Pty/PstlAdr/PstCd"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
</gl-bus:identifierAddress>
</gl-cor:identifierReference>
<!--end of Ultimate Creditor-->
</xsl:if>

<!-- ISO Index:  9.1.3  --><xsl:variable name="value" select="./RltdPties/Cdtr/Pty/Nm"/><xsl:if test="string($value)">
<!--Creditor-->
<gl-cor:identifierReference>
<!--Identifier Organization Type, organization-->
<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>
<xsl:variable name="value" select="./RltdPties/Cdtr/Pty/Nm"/><xsl:if test="string($value)">
<gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription><gl-cor:identifierType contextRef="now">FI-S</gl-cor:identifierType></xsl:if>

<!--Creditor Address Details-->
<gl-bus:identifierAddress>
<!-- ISO Index:  9.1.3  --><xsl:variable name="value" select="./RltdPties/Cdtr/Pty/PstlAdr/Dept"/><xsl:if test="string($value)"><gl-bus:identifierAddressDescription contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressDescription></xsl:if>
<!-- ISO Index:  9.1.5  --><xsl:variable name="value" select="./RltdPties/Cdtr/Pty/PstlAdr/StrtNm"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!-- ISO Index:  9.1.6  --><xsl:variable name="value" select="./RltdPties/Cdtr/Pty/PstlAdr/BldgNb"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<!-- ISO Index:  9.1.8  --><xsl:variable name="value" select="./RltdPties/Cdtr/Pty/PstlAdr/TwnNm"/><xsl:if test="string($value)"><gl-bus:identifierStateOrProvince contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStateOrProvince></xsl:if>
<!-- ISO Index:  9.1.9  --><xsl:variable name="value" select="./RltdPties/Cdtr/Pty/PstlAdr/CtrySubDvsn"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!-- ISO Index:  9.1.7  --><xsl:variable name="value" select="./RltdPties/Cdtr/Pty/PstlAdr/PstCd"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
</gl-bus:identifierAddress>
</gl-cor:identifierReference>
<!--end of Creditor-->
</xsl:if>


<!--DEBTOR ACCOUNT-->
<xsl:variable name="value" select="./RltdPties/DbtrAcct/IBAN"/><xsl:if test="string($value)">
<gl-cor:identifierReference>
<!--Identifier Type, BDEBTOR_ACCOUNT-->
<!--TODO kun tyypitys valmis, korjaa tähän-->
<!--
<gl-cor:identifierType contextRef="now">DEBTOR_ACCOUNT</gl-cor:identifierType>
-->
<gl-cor:identifierExternalReference>
<xsl:variable name="value" select="./RltdPties/DbtrAcct/IBAN"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">IBAN</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<!-- ISO Index:  1.1.3  --><xsl:variable name="value" select="./RltdPties/DbtrAcct/Othr/Id"/><xsl:if test="string($value)"><gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode></xsl:if>
<!-- ISO Index:  1.1.5  Basic bank account numer (BBAN)--><xsl:variable name="value" select="./RltdPties/DbtrAcct/Othr/SchmeNm/Cd"/><xsl:if test="string($value)"><gl-cor:identifierAuthority
 contextRef="now">BBAN</gl-cor:identifierAuthority></xsl:if>
<!-- ISO Index:  1.1.6  --><xsl:variable name="value" select="./RltdPties/DbtrAcct/Othr/SchmeNm/Prtry"/><xsl:if test="string($value)"><gl-cor:identifierAuthority
 contextRef="now">other</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
</gl-cor:identifierReference>
<!--CREDITOR ACCOUNT-->
<gl-cor:identifierReference>
<!--Identifier Type, CREDITOR_ACCOUNT-->
<!--TODO kun tyypitys valmis, korjaa tähän-->
<!--
<gl-cor:identifierType contextRef="now">CREDITOR_ACCOUNT</gl-cor:identifierType>
-->
<gl-cor:identifierExternalReference>
<xsl:variable name="value" select="./RltdPties/CdtrAcct/IBAN"/><xsl:if test="string($value)"><gl-cor:identifierAuthorityCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierAuthorityCode><gl-cor:identifierAuthority
 contextRef="now">IBAN</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
<gl-cor:identifierExternalReference>
<!-- ISO Index:  1.1.3  --><xsl:variable name="value" select="./RltdPties/CdtrAcct/Othr/Id"/><xsl:if test="string($value)"><gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode></xsl:if>
<!-- ISO Index:  1.1.5  Basic bank account numer (BBAN)--><xsl:variable name="value" select="./RltdPties/CdtrAcct/Othr/SchmeNm/Cd"/><xsl:if test="string($value)"><gl-cor:identifierAuthority
 contextRef="now">BBAN</gl-cor:identifierAuthority></xsl:if>
<!-- ISO Index:  1.1.6  --><xsl:variable name="value" select="./RltdPties/CdtrAcct/Othr/SchmeNm/Prtry"/><xsl:if test="string($value)"><gl-cor:identifierAuthority
 contextRef="now">other</gl-cor:identifierAuthority></xsl:if>
</gl-cor:identifierExternalReference>
</gl-cor:identifierReference>
</xsl:if>

<!--Servicer-Branch-->
<xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/BrnchId/Id"/><xsl:if test="string($value)">
<gl-cor:identifierReference>

<!-- ISO Index:  1.2.83  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/BrnchId/Id"/><xsl:if test="string($value)"><gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode></xsl:if>
<!-- ISO Index:  1.2.84  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/BrnchId/Nm"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>
<!--Identifier Type, BANK_BRANCH-->
<!--TODO kun tyypitys valmis, korjaa tähän-->
<!--
<gl-cor:identifierType contextRef="now">BANK_BRANCH</gl-cor:identifierType>
-->
<!--Identifier Organization Type, organization-->

<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>
<!--TODO kun tyypitys valmis, korjaa tähän-->
<!--
<xsl:if test="string($value)"><gl-cor:identifierType contextRef="now">FI-S</gl-cor:identifierType></xsl:if>
-->

<!--Servicer-Branch Address Details-->
<gl-bus:identifierAddress>
<!-- ISO Index:  1.2.87  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/BrnchId/PstlAdrDept"/><xsl:if test="string($value)"><gl-bus:identifierAddressDescription contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressDescription></xsl:if>
<!-- ISO Index:  1.2.89  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/BrnchId/PstlAdrStrtNm"/><xsl:if test="string($value)"><gl-bus:identifierStreet contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStreet></xsl:if>
<!-- ISO Index:  1.2.90  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/BrnchId/PstlAdrBldgNb"/><xsl:if test="string($value)"><gl-bus:identifierAddressStreet2 contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierAddressStreet2></xsl:if>
<!-- ISO Index:  1.2.92  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/BrnchId/PstlAdrTwnNm"/><xsl:if test="string($value)"><gl-bus:identifierStateOrProvince contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierStateOrProvince></xsl:if>
<!-- ISO Index:  1.2.93  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/BrnchId/PstlAdrCtrySubDvsn"/><xsl:if test="string($value)"><gl-bus:identifierCountry contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierCountry></xsl:if>
<!-- ISO Index:  1.2.91  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Acct/Svc/BrnchId/PstlAdrPstCd"/><xsl:if test="string($value)"><gl-bus:identifierZipOrPostalCode contextRef="now"><xsl:value-of select="$value"/></gl-bus:identifierZipOrPostalCode></xsl:if>
</gl-bus:identifierAddress>
</gl-cor:identifierReference>
<!--end of Servicer-Branch-->
</xsl:if>

<!--DebtorAgent-Branch-->
<xsl:variable name="value" select="./RltdAgts/DbtrAgt/BrnchId/Id"/><xsl:if test="string($value)">
<gl-cor:identifierReference>
<!-- ISO Index:  6.1.26  --><xsl:variable name="value" select="./RltdAgts/DbtrAgt/BrnchId/Id"/><xsl:if test="string($value)"><gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode></xsl:if>
<!-- ISO Index:  6.1.27  --><xsl:variable name="value" select="./RltdAgts/DbtrAgt/BrnchId/Nm"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>
<!--Identifier Type, DEBTOR_AGENT_BRANCH-->
<!--TODO kun tyypitys valmis, korjaa tähän-->
<!--
<gl-cor:identifierType contextRef="now">BANK_BRANCH</gl-cor:identifierType>
-->
<!--Identifier Organization Type, organization-->

<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>

</gl-cor:identifierReference>
<!--end of DebtorAgent-Branch-->
</xsl:if>

<!--CreditorAgent-Branch-->
<xsl:variable name="value" select="./RltdAgts/CdtrAgt/BrnchId/Id"/><xsl:if test="string($value)">
<gl-cor:identifierReference>
<!-- ISO Index:  6.1.26  --><xsl:variable name="value" select="./RltdAgts/CdtrAgt/BrnchId/Id"/><xsl:if test="string($value)"><gl-cor:identifierCode contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierCode></xsl:if>
<!-- ISO Index:  6.1.27  --><xsl:variable name="value" select="./RltdAgts/CdtrAgt/BrnchId/Nm"/><xsl:if test="string($value)"><gl-cor:identifierDescription contextRef="now"><xsl:value-of select="$value"/></gl-cor:identifierDescription></xsl:if>
<!--Identifier Type, Creditor_AGENT_BRANCH-->
<!--TODO kun tyypitys valmis, korjaa tähän-->
<!--
<gl-cor:identifierType contextRef="now">BANK_BRANCH</gl-cor:identifierType>
-->
<!--Identifier Organization Type, organization-->

<gl-cor:identifierOrganizationType contextRef="now">organization</gl-cor:identifierOrganizationType>

</gl-cor:identifierReference>
<!--end of CreditorAgent-Branch-->
</xsl:if>


<gl-cor:documentType contextRef="now">finance-charge</gl-cor:documentType>

<xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/LglSeqNb"/>
 <xsl:choose>
 <xsl:when test="not(string($value))">
<!--prio 1-->
<!-- ISO Index:  2.3  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/LglSeqNb"/>  
    <xsl:choose>
    <xsl:when test="not(string($value))">
    <!--prio 2-->
    <!-- ISO Index:  2.2  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/ElctrncSeqNb"/>
        <xsl:choose>
            <xsl:when test="not(string($value))">
            <!--prio 3-->
            <!-- ISO Index:  2.1  -->
			<xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/Id"/>
            <xsl:if test="string($value)">
			<!--
			<gl-cor:documentNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentNumber>
			-->
			</xsl:if>
            </xsl:when>
            <xsl:otherwise>
			<!--
            <gl-cor:documentNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentNumber>
			-->
            </xsl:otherwise>
        </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
	<!--
	<gl-cor:documentNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentNumber>
	-->
	</xsl:otherwise>
    </xsl:choose>
</xsl:when>
<xsl:otherwise><gl-cor:documentNumber contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentNumber></xsl:otherwise>
</xsl:choose>
<!-- ISO Index:  2.4  --><xsl:variable name="value" select="//BkToCstmrStmt[1]/Stmt/CreDtTm"/><xsl:if test="string($value)"><gl-cor:documentDate contextRef="now"><xsl:value-of select="$value"/></gl-cor:documentDate></xsl:if>

<gl-cor:detailComment contextRef="now">Row</gl-cor:detailComment>

<!-- ISO Index:  2.267  --><xsl:variable name="value" select="./RltdDts/AccptncDtTm"/><xsl:if test="string($value)"><gl-cor:dateAcknowledged contextRef="now"><xsl:value-of select="$value"/></gl-cor:dateAcknowledged></xsl:if>


<!-- ISO Index:  2.84  --><xsl:variable name="value" select="./AcctSvcrRef"/><xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">voucher</gl-taf:originatingDocumentType><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>


<!-- ISO Index:  2.145  --><xsl:variable name="value" select="./DxDtls/Refs/AcctSvcrRef"/>
<xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentType contextRef="now">voucher</gl-taf:originatingDocumentType><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber>
</gl-taf:originatingDocumentStructure>
</xsl:if>



<xsl:for-each select="./RmtInf/Strd/RfrdDocInf">
<gl-taf:originatingDocumentStructure>
<!-- ISO Index:  2.240  --><xsl:variable name="value" select="./Tp/CdOrPrty/Cd[text()='CINV']"/>
<xsl:if test="string($value)"><gl-taf:originatingDocumentType contextRef="now">invoice</gl-taf:originatingDocumentType></xsl:if>
<!-- ISO Index:  2.240  --><xsl:variable name="value" select="./Tp/CdOrPrty/Cd[text()='CREN']"/>
<xsl:if test="string($value)"><gl-taf:originatingDocumentType contextRef="now">credit-note</gl-taf:originatingDocumentType></xsl:if>
<!-- ISO Index:  2.243  --><xsl:variable name="value" select="./Nb"/><xsl:if test="string($value)"><gl-taf:originatingDocumentNumber contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentNumber></xsl:if>
<!-- ISO Index:  2.244  --><xsl:variable name="value" select="./RltdDt"/><xsl:if test="string($value)"><gl-taf:originatingDocumentDate contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentDate></xsl:if>
</gl-taf:originatingDocumentStructure>
</xsl:for-each>




<!--TODO tyypitys jkorjaa tähän, nyt laitettu other-->
<!-- ISO Index:  2.262  --><xsl:variable name="value" select="./RmtInf/Strd/CdtrRefInf/Ref"/>
<xsl:if test="string($value)">
<gl-taf:originatingDocumentStructure>
<gl-taf:originatingDocumentIdentifierType contextRef="now">O</gl-taf:originatingDocumentIdentifierType><gl-taf:originatingDocumentIdentifierCode contextRef="now"><xsl:value-of select="$value"/></gl-taf:originatingDocumentIdentifierCode>
</gl-taf:originatingDocumentStructure>
</xsl:if>


</gl-cor:entryDetail>

</xsl:for-each>


</xsl:for-each>
</gl-cor:entryHeader>


</gl-cor:accountingEntries>

 
</xbrli:xbrl>
</xsl:template>
 
 
 
 
 
</xsl:stylesheet>