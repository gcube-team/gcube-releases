<?xml version="1.0" encoding="UTF-8"?>
<!--  Very Simple XSL-T which transform the layout into a more readeble form -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/">
	<xsl:apply-templates select="/"/>
	</xsl:template>
	<xsl:template match="/">
		<root>
		<xsl:for-each select="//portlet-class">
			<portlet>
				<xsl:value-of select="."/>
			</portlet>
		</xsl:for-each>
		</root>
	</xsl:template>
</xsl:stylesheet>
