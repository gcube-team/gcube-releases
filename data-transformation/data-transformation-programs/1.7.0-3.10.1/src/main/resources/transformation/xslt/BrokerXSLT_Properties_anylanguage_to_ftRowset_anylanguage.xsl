<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output xmlns:xsl="http://www.w3.org/1999/XSL/Transform" indent="yes" method="xml" omit-xml-declaration="yes" />
	<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="/">
		<ROWSET xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
			<ROW xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
				<xsl:for-each xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="//Properties/property/*[local-name()='key']">
					<xsl:if xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="normalize-space(.)">
						<FIELD xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="{local-name(..)}.{local-name()}">
							<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="//Properties/property/*[local-name()='value']">
					<xsl:if xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="normalize-space(.)">
						<FIELD xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="{local-name(..)}.{local-name()}">
							<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="normalize-space(.)" />
						</FIELD>
					</xsl:if>
				</xsl:for-each>
			</ROW>
		</ROWSET>
	</xsl:template>
</xsl:stylesheet>
