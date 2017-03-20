<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output xmlns:xsl="http://www.w3.org/1999/XSL/Transform" indent="yes" method="xml" omit-xml-declaration="yes" />
	<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="/">
		<ROWSET xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
			<ROW xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
				<xsl:for-each xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="//taxonomyItem/*[local-name()='citation']">
					<xsl:if xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="normalize-space(.)">
						<FIELD xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="nameAccordingTo">
							<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="//taxonomyItem/*[local-name()='author']">
					<xsl:if xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="normalize-space(.)">
						<FIELD xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="scientificNameAuthorship">
							<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="//taxonomyItem/*[local-name()='rank']">
					<xsl:if xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="normalize-space(.)">
						<FIELD xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="taxonRank">
							<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="//taxonomyItem/*[local-name()='parentID']">
					<xsl:if xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="normalize-space(.)">
						<FIELD xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="parentNameUsageID">
							<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="//taxonomyItem/*[local-name()='scientificName']">
					<xsl:if xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="normalize-space(.)">
						<FIELD xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="{local-name()}">
							<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="//taxonomyItem/status/*[local-name()='statusRemarks']">
					<xsl:if xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="normalize-space(.)">
						<FIELD xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="taxonRemarks">
							<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="//taxonomyItem/status/*[local-name()='status']">
					<xsl:if xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="normalize-space(.)">
						<FIELD xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="taxonomicStatus">
							<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="//taxonomyItem/status/*[local-name()='acceptedID']">
					<xsl:if xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="normalize-space(.)">
						<FIELD xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="acceptedNameUsageID">
							<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="//taxonomyItem/*[local-name()='credits']">
					<xsl:if xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="normalize-space(.)">
						<FIELD xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliographicCitation">
							<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="//taxonomyItem/*[local-name()='LSID']">
					<xsl:if xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="normalize-space(.)">
						<FIELD xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="scientificNameID">
							<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="//taxonomyItem/*[local-name()='modified']">
					<xsl:if xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="normalize-space(.)">
						<FIELD xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="{local-name()}">
							<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="//taxonomyItem/commonNames/commonName/*[local-name()='language']">
					<xsl:if xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="normalize-space(.)">
						<FIELD xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="vernacularNames.{local-name()}">
							<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="//taxonomyItem/commonNames/commonName/*[local-name()='locality']">
					<xsl:if xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="normalize-space(.)">
						<FIELD xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="vernacularNames.{local-name()}">
							<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="//taxonomyItem/commonNames/commonName/*[local-name()='name']">
					<xsl:if xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="normalize-space(.)">
						<FIELD xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="vernacularNames.vernacularName">
							<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
			</ROW>
		</ROWSET>
	</xsl:template>
</xsl:stylesheet>
