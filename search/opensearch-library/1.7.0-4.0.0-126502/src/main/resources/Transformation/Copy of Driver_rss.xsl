<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
        <xsl:output indent="yes" method="xml" omit-xml-declaration="yes"/>
        <xsl:template match="/">
              <xsl:for-each select="//*[local-name()='item']/*[local-name()='title']">
              	<record>
                  <xsl:value-of select="."/>
				</record>
              </xsl:for-each>
         </xsl:template>
</xsl:stylesheet>