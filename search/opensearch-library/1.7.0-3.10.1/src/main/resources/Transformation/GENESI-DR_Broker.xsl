<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output indent="yes" method="text"/>
  <xsl:template match="*[local-name()='Series' and namespace-uri()='http://xmlns.com/2008/dclite4g#']">
    <xsl:value-of select="*[local-name()='description' and namespace-uri()='http://purl.org/dc/elements/1.1/']/@*[local-name()='resource' and namespace-uri()='http://www.w3.org/1999/02/22-rdf-syntax-ns#']"/>
  </xsl:template>
</xsl:transform>

