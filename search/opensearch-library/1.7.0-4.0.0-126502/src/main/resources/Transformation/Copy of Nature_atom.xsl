<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" indent="yes"/>
<xsl:template match="/">
    <xsl:value-of select="*[local-name()='entry']/*[local-name()='title']"/>
 </xsl:template>
</xsl:transform>