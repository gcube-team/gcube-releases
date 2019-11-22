<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output omit-xml-declaration="yes" indent="yes"/>
  <xsl:strip-space elements="*"/>

  <xsl:template match="/">
    <table>
      <xsl:apply-templates/>
    </table>
  </xsl:template>

  <xsl:template match="*">
    <tr>
      <td>
        <p><xsl:value-of select="name()"/></p>
      </td>
      <td>
        <p><xsl:value-of select="."/></p>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="*[*]">
    <tr>
      <td>
        <p><xsl:value-of select="name()"/></p>
      </td>
      <td>
        <table>
          <xsl:apply-templates/>
        </table>
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>