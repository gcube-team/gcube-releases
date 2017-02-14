<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output indent="yes" method="xml" omit-xml-declaration="yes" />
	<xsl:strip-space elements="*" />
	<xsl:template match="/*">
		<xsl:copy>
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
	<xsl:template match="*">
		<xsl:param name="pName" />
		<xsl:apply-templates>
			<xsl:with-param name="pName" select="concat($pName,name(),'.')" />
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="*[not(*)]">
		<xsl:param name="pName" />
		<xsl:element name="{$pName}{name()}">
			<xsl:value-of select="." />
		</xsl:element>
		<xsl:apply-templates select="@*" mode="attr">
			<xsl:with-param name="pName" select="concat($pName,name(),'.')" />
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="@*" mode="attr">
		<xsl:param name="pName" />
		<xsl:element name="{$pName}{name()}">
			<xsl:value-of select="." />
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>