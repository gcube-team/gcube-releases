<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output indent="yes" method="xml" omit-xml-declaration="yes" />
	<xsl:template match="/">
		<ROWSET xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
			<ROW xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
				<xsl:for-each select="//DwC/*[local-name()='nameAccordingTo']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/*[local-name()='taxonRemarks']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/*[local-name()='scientificName']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/*[local-name()='class']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/*[local-name()='scientificNameAuthorship']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/*[local-name()='taxonomicStatus']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/*[local-name()='genus']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/*[local-name()='modified']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/*[local-name()='acceptedNameUsageID']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/*[local-name()='phylum']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/*[local-name()='accessRights']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/*[local-name()='kingdom']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/*[local-name()='order']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/*[local-name()='parentNameUsageID']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/*[local-name()='family']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/*[local-name()='bibliographicCitation']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/*[local-name()='scientificNameID']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/*[local-name()='taxonRank']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/*[local-name()='specificEpithet']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/vernacularNames/*[local-name()='language']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name(..)}.{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/vernacularNames/*[local-name()='locality']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name(..)}.{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="//DwC/vernacularNames/*[local-name()='vernacularName']">
					<xsl:if test="normalize-space(.)">
						<FIELD name="{local-name(..)}.{local-name()}">
							<xsl:value-of select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
			</ROW>
		</ROWSET>
	</xsl:template>
</xsl:stylesheet>