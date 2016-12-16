<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:gmd="http://www.isotc211.org/2005/gmd" version="1.0" xmlns:gml="http://www.opengis.net/gml" xmlns:gts="http://www.isotc211.org/2005/gts" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:geonet="http://localhost/geonetwork" >
	<xsl:output indent="yes" method="xml" omit-xml-declaration="yes" />
	<xsl:template match="/">
		<oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
			<xsl:for-each select="gmd:MD_Metadata/gmd:language/gmd:LanguageCode/@codeListValue">
				<xsl:if test="normalize-space(.)">
					<xsl:element name="language" namespace="http://purl.org/dc/elements/1.1/">
						<xsl:value-of select="normalize-space(.)" />
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString/text()">
				<xsl:if test="normalize-space(.)">
					<xsl:element name="title" namespace="http://purl.org/dc/elements/1.1/">
						<xsl:value-of select="normalize-space(.)" />
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date/gco:DateTime/text()">
				<xsl:if test="normalize-space(.)">
					<xsl:element name="date" namespace="http://purl.org/dc/elements/1.1/">
						<xsl:value-of select="normalize-space(.)" />
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:abstract/gco:CharacterString/text()">
				<xsl:if test="normalize-space(.)">
					<xsl:element name="description" namespace="http://purl.org/dc/elements/1.1/">
						<xsl:value-of select="normalize-space(.)" />
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="gmd:MD_Metadata/gmd:contact/gmd:CI_ResponsibleParty/gmd:organisationName/gco:CharacterString/text()">
				<xsl:if test="normalize-space(.)">
					<xsl:element name="publisher" namespace="http://purl.org/dc/elements/1.1/">
						<xsl:value-of select="normalize-space(.)" />
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointOfContact/gmd:CI_ResponsibleParty/gmd:organisationName/gco:CharacterString/text()">
				<xsl:if test="normalize-space(.)">
					<xsl:element name="publisher" namespace="http://purl.org/dc/elements/1.1/">
						<xsl:value-of select="normalize-space(.)" />
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue">
				<xsl:if test="normalize-space(.)">
					<xsl:element name="rights" namespace="http://purl.org/dc/elements/1.1/">
						<xsl:value-of select="concat('to access: ', normalize-space(.))" />
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:useConstraints/gmd:MD_RestrictionCode/@codeListValue">
				<xsl:if test="normalize-space(.)">
					<xsl:element name="rights" namespace="http://purl.org/dc/elements/1.1/">
						<xsl:value-of select="concat('to use: ', normalize-space(.))" />
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:otherConstraints/gco:CharacterString/text()">
				<xsl:if test="normalize-space(.)">
					<xsl:element name="rights" namespace="http://purl.org/dc/elements/1.1/">
						<xsl:value-of select="normalize-space(.)" />
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="gmd:MD_Metadata/gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource/gmd:linkage/gmd:URL/text()">
				<xsl:if test="normalize-space(.)">
					<xsl:element name="source" namespace="http://purl.org/dc/elements/1.1/">
						<xsl:value-of select="normalize-space(.)" />
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod">
				<xsl:if test="normalize-space(.)">
					<xsl:element name="coverage" namespace="http://purl.org/dc/elements/1.1/">
						<xsl:value-of select="normalize-space(concat(gml:beginPosition/text(),' ',gml:endPosition/text()))" />
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox">
				<xsl:if test="normalize-space(.)">
					<xsl:element name="coverage" namespace="http://purl.org/dc/elements/1.1/">
						<xsl:value-of select="normalize-space(concat('west:',gmd:westBoundLongitude/gco:Decimal/text(),' east:',gmd:eastBoundLongitude/gco:Decimal/text(),' south:',gmd:southBoundLatitude/gco:Decimal/text(),' north:',gmd:northBoundLatitude/gco:Decimal/text()))" />
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString/text()">
				<xsl:if test="normalize-space(.)">
					<xsl:element name="identifier" namespace="http://purl.org/dc/elements/1.1/">
						<xsl:value-of select="normalize-space(.)" />
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:topicCategory/gmd:MD_TopicCategoryCode/text()">
				<xsl:if test="normalize-space(.)">
					<xsl:element name="subject" namespace="http://purl.org/dc/elements/1.1/">
						<xsl:value-of select="normalize-space(.)" />
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
<!-- 
could not find any mapping for for the following DC fields:
contributor
creator
format
relation
type
 -->
		</oai_dc:dc>
	</xsl:template>
</xsl:stylesheet>