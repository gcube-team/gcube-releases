<?xml version="1.0" encoding="UTF-8"?>
<!--  Author: Massimiliano Assante, assante@isti.cnr.it
    This stylesheet generates a dynamic stylesheet to be applied to any metadata.
    
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:nxsl="http://www.w3.org/1999/XSL/TransformXX"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

    <xsl:output omit-xml-declaration="yes" method="xml" encoding="UTF-8"/>
    <xsl:namespace-alias stylesheet-prefix="nxsl" result-prefix="nxsl"/>
    <xsl:template match="Layout">

        <nxsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:bks="http://gcube.org/AnnotationFrontEnd/AFEAnnotationV2">
            <xsl:variable name="ns" select="//Fields/Namespace/namespace::*"/>
            <xsl:copy-of select="$ns"/>
            <xsl:attribute name="version">1.0</xsl:attribute>
            <nxsl:output version="1.0" omit-xml-declaration="yes" method="xml" encoding="UTF8"/>
            <nxsl:template match="/">
                <table>
                    <xsl:apply-templates select="Fields"/>
                </table>
            </nxsl:template>
        </nxsl:stylesheet>
    </xsl:template>

    <!-- 
            handles the single fields
    -->
    <xsl:template match="Fields/SingleField">
        <nxsl:for-each select="{@xpath}">
            <xsl:variable name="currElem">
                <xsl:value-of select="@name"/>
            </xsl:variable>
            <tr>
                <td align="right" class="window-title-inactive" width="120">
                    <b title="{@tooltip}">
                        <xsl:value-of select="$currElem"/>
                    </b>
                </td>
                <td>
                    <xsl:choose>
                        <xsl:when test="@maxLen ='' ">
                            <nxsl:value-of select="."/>
                        </xsl:when>
                        <xsl:otherwise>
                            <nxsl:value-of select="substring(.,1,{@maxLen})"/>
                            <nxsl:if test="string-length({@xpath}) &gt; {@maxLen}">
                                <i>... (more)</i>
                            </nxsl:if>
                        </xsl:otherwise>
                    </xsl:choose>    
                </td>
            </tr>
        </nxsl:for-each>
    </xsl:template>

    <!-- 
        handles a field which can appear more than once 
    -->
    <xsl:template match="Fields/MultiField">
        
        <nxsl:if test="{@xpath}">
            <xsl:variable name="currElem">
                <xsl:value-of select="@name"/>
            </xsl:variable>
            <tr>
                <td align="right" class="window-title-inactive" width="120">
                    <b>
                        <xsl:value-of select="$currElem"/>
                    </b>
                </td>
                <td>
                    <nxsl:for-each select="{@xpath}">
                        <nxsl:variable name="elementsPerRow" ><nxsl:value-of select="number({//MultiField[@name=$currElem]/ElementsPerRow})"/></nxsl:variable>
                        <nxsl:variable name="visibleRowNo" ><nxsl:value-of select="number({//MultiField[@name=$currElem]/VisibleRowNo})"/></nxsl:variable>
                        <nxsl:choose>
                            <nxsl:when test="($elementsPerRow * $visibleRowNo) > position()">
                                <xsl:choose>
                                    <xsl:when test="@maxLen ='' ">
                                        <nxsl:value-of select="."/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <nxsl:value-of select="substring(.,1,{@maxLen})"/>
                                        <nxsl:if test="string-length({@xpath}) &gt; {@maxLen}">
                                            <i>... (more)</i>
                                        </nxsl:if>
                                    </xsl:otherwise>
                                </xsl:choose>    
                                <nxsl:if test="position() != last()">, </nxsl:if>
                                <nxsl:if test="(position() mod ($elementsPerRow ) ) = 0 ">
                                    <br/>
                                </nxsl:if>
                            </nxsl:when>
                            <nxsl:when
                                test="($elementsPerRow * $visibleRowNo)  = position()"> ...
                            </nxsl:when>
                        </nxsl:choose>
                    </nxsl:for-each>
                </td>
            </tr>
        </nxsl:if>
    </xsl:template>

</xsl:stylesheet>
