<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="*[local-name()='entry']">
    <record>
    <xsl:value-of select="*[local-name()='title']"/>
    </record>
 </xsl:template>
</xsl:transform>