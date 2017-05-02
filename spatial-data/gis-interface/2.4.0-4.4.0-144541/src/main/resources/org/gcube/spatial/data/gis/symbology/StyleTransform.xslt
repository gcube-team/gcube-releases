<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:sld="http://www.opengis.net/sld">
 <xsl:strip-space elements="*"/>
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
        </xsl:copy>
    </xsl:template>
    <xsl:template match="//sld:Rule/sld:Name">
        <sld:Title><xsl:apply-templates select="@*|node()" /></sld:Title>
    </xsl:template>   
    <xsl:template match=
    "*[not(@*|*|comment()|processing-instruction()) 
     and normalize-space()=''
      ]"/>     
</xsl:stylesheet>