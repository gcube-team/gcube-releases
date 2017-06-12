<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" >
<xsl:template match="VRE" mode="modifyAttributes">
  <xsl:attribute name="name">%%VRENAME%%</xsl:attribute>
</xsl:template>
<xsl:template match="*" mode="modifyAttributes"/>
<xsl:template match="collection[not(%%COLLECATIONCOMPARISON%%)]"/>
<xsl:template match="*">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates select="." mode="modifyAttributes"/>
    <xsl:apply-templates />
  </xsl:copy>
</xsl:template>
</xsl:stylesheet>