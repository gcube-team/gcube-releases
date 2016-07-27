<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output indent="yes" method="xml" omit-xml-declaration="yes" />
	<xsl:template match="/">
		<ROWSET>
			<ROW>
				<xsl:for-each select="//ROWSET/ROW/FIELD">
					<xsl:choose>
						<xsl:when test="self::node()[@name ='CollectionID']">
						</xsl:when>
					</xsl:choose>
					<xsl:choose>
						<xsl:when test="self::node()[@name ='ContentOID']">
						</xsl:when>
					</xsl:choose>
					<xsl:choose>
						<xsl:when test="self::node()[@name ='acl']">
						</xsl:when>
					</xsl:choose>
					<xsl:choose>
						<xsl:when test="self::node()[@name !='CollectionID' and @name!='ContentOID' and text() and not(@name = preceding::FIELD/@name and text() = preceding::FIELD/text())]">
							<xsl:copy-of select="self::node()" />
						</xsl:when>
					</xsl:choose>
				</xsl:for-each>
			</ROW>
		</ROWSET>
	</xsl:template>
</xsl:stylesheet>