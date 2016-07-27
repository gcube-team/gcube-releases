<?xml version="1.0" encoding="ISO-8859-1"?>
<!-- 
  Portions of this file Copyright 1999-2005 University of Chicago
  Portions of this file Copyright 1999-2005 The University of Southern California.
  
  This file or a portion of this file is licensed under the
  terms of the Globus Toolkit Public License, found at
  http://www.globus.org/toolkit/download/license.html.
  If you redistribute this file, with or without
  modifications, you must include this notice in the file.
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:glue="http://mds.globus.org/glue/ce/1.1"
                xmlns:gx="http://mds.globus.org/2004/07/29/ganglia-to-glue"
                xmlns:sched="http://mds.globus.org/batchproviders/2004/09"
>

<!-- TODO: a bunch of schema fields not implemented yet -->

<xsl:template match="/">

<glue:ClusterInformation>
<xsl:text>
</xsl:text>

<xsl:comment>

Produced by MDS4 Ganglia to GLUE Schema XSLT stylesheet
$Id: ganglia_to_glue.xslt,v 1.11.2.3 2005/06/21 22:44:12 mdarcy Exp $

</xsl:comment>

<xsl:for-each select="GANGLIA_XML">

<xsl:for-each select="CLUSTER|GRID/CLUSTER">
<xsl:text>
</xsl:text>

<!--
<glue:Cluster>
<xsl:attribute name="glue:Name"><xsl:value-of select="@NAME"/></xsl:attribute>
<xsl:attribute name="glue:UniqueID"><xsl:value-of select="@NAME"/></xsl:attribute>
-->

<glue:Cluster glue:Name="{@NAME}" glue:UniqueID="{@NAME}">
<xsl:text>
</xsl:text>

<glue:SubCluster glue:Name="main" glue:UniqueID="main">

<xsl:for-each select="HOST">
<xsl:text>
</xsl:text>

<glue:Host>
<xsl:attribute name="glue:Name"><xsl:value-of select="@NAME"/></xsl:attribute>
<xsl:attribute name="glue:UniqueID"><xsl:value-of select="@NAME"/></xsl:attribute>

<!-- Benchmark not available -->

<!-- TODO units for VirtualSize and VirtualAvailable - can't use use
    the fixUnitsToMB template as a sum is involved -->
<glue:MainMemory>
<xsl:attribute name="glue:VirtualSize">
  <xsl:call-template name="emitProperNumeric">
    <xsl:with-param name="numeric" select="floor(0.001*(METRIC[@NAME='mem_total']/@VAL + METRIC[@NAME='swap_total']/@VAL))"/>
  </xsl:call-template>
</xsl:attribute>

<xsl:attribute name="glue:VirtualAvailable">
  <xsl:call-template name="emitProperNumeric">
    <xsl:with-param name="numeric" select="floor(0.001*(METRIC[@NAME='mem_free']/@VAL + METRIC[@NAME='swap_free']/@VAL))"/>
  </xsl:call-template>
</xsl:attribute>

<xsl:attribute name="glue:RAMSize">
     <xsl:for-each select="METRIC[@NAME='mem_total']">
        <xsl:call-template name="fixUnitsToMB"  />
     </xsl:for-each>
</xsl:attribute>

<xsl:attribute name="glue:RAMAvailable">
     <xsl:for-each select="METRIC[@NAME='mem_free']">
        <xsl:call-template name="fixUnitsToMB"  />
     </xsl:for-each>
</xsl:attribute>

</glue:MainMemory>


<glue:Processor>
<!-- Vendor, Model, Version, ClockSpeed, InstructionSet, OtherProcessorDescription, CacheL1, CacheL1I, CacheL1D, CacheL2 -->

<xsl:attribute name="glue:ClockSpeed">
  <xsl:call-template name="emitProperNumeric">
    <xsl:with-param name="numeric" select="METRIC[@NAME='cpu_speed']/@VAL"/>
  </xsl:call-template>
</xsl:attribute>

<xsl:attribute name="glue:InstructionSet">
  <xsl:call-template name="emitProperString">
    <xsl:with-param name="string" select="METRIC[@NAME='machine_type']/@VAL"/>
  </xsl:call-template>
</xsl:attribute>

</glue:Processor>

<glue:OperatingSystem>

<xsl:attribute name="glue:Name">
  <xsl:call-template name="emitProperString">
    <xsl:with-param name="string" select="METRIC[@NAME='os_name']/@VAL"/>
  </xsl:call-template>
</xsl:attribute>

<xsl:attribute name="glue:Release">
  <xsl:call-template name="emitProperString">
    <xsl:with-param name="string" select="METRIC[@NAME='os_release']/@VAL"/>
  </xsl:call-template>
</xsl:attribute>

</glue:OperatingSystem>

<!-- <glue:StorageDevice>  not available -->

<glue:Architecture>

<!-- TODO what does PlatformType look like? -->
<xsl:attribute name="glue:SMPSize">
  <xsl:call-template name="emitProperNumeric">
    <xsl:with-param name="numeric" select="METRIC[@NAME='cpu_num']/@VAL"/>
  </xsl:call-template>
</xsl:attribute>

</glue:Architecture>

<!-- <glue:ApplicationSoftware> not available -->

<glue:FileSystem glue:Name="entire-system" glue:Root="/">
<!-- Ganglia only provides node-wide space information, not per 
     filesystem; here we generate an entry for / with this information.
     It might be that instead we want to generate no filesystem element
     at all, although this format is of course not expressive enough
     to deal with quotas and the like even when we do have per-filesystem
     information -->
 <xsl:attribute name="glue:Size">
     <xsl:for-each select="METRIC[@NAME='disk_total']">
        <xsl:call-template name="fixUnitsToMB"  />
     </xsl:for-each>
 </xsl:attribute>

 <xsl:attribute name="glue:AvailableSpace">
     <xsl:for-each select="METRIC[@NAME='disk_free']">
        <xsl:call-template name="fixUnitsToMB"  />
     </xsl:for-each>
 </xsl:attribute>

</glue:FileSystem>

<!-- Ganglia only publishes one IP address per host,
     thus we assume the OutboundIP and the InboundIP are the same -->

<glue:NetworkAdapter glue:Name="main" glue:OutboundIP="true" glue:InboundIP="true">

<xsl:attribute name="glue:Name">
  <xsl:call-template name="emitProperString">
    <xsl:with-param name="string" select="@NAME"/>
  </xsl:call-template>
</xsl:attribute>

<xsl:attribute name="glue:IPAddress">
  <xsl:call-template name="emitProperString">
    <xsl:with-param name="string" select="@IP"/>
  </xsl:call-template>
</xsl:attribute>

<xsl:attribute name="glue:MTU">
  <xsl:call-template name="emitProperNumeric">
    <xsl:with-param name="numeric" select="METRIC[@NAME='mtu']/@VAL"/>
  </xsl:call-template>
</xsl:attribute>

</glue:NetworkAdapter>

<!-- <glue:SMPLoad> not available -->

<glue:ProcessorLoad>

<xsl:attribute name="glue:Last1Min">
  <xsl:call-template name="emitProperNumeric">
    <xsl:with-param name="numeric" select="floor(100 * METRIC[@NAME='load_one']/@VAL)"/>
  </xsl:call-template>
</xsl:attribute>

<xsl:attribute name="glue:Last5Min">
  <xsl:call-template name="emitProperNumeric">
    <xsl:with-param name="numeric" select="floor(100 * METRIC[@NAME='load_five']/@VAL)"/>
  </xsl:call-template>
</xsl:attribute>

<xsl:attribute name="glue:Last15Min">
  <xsl:call-template name="emitProperNumeric">
    <xsl:with-param name="numeric" select="floor(100 * METRIC[@NAME='load_fifteen']/@VAL)"/>
  </xsl:call-template>
</xsl:attribute>

</glue:ProcessorLoad>

<!-- The below was an attempt to do something with the metrics that we
     do not know about... it publishes the name of each available metric.
     However, this did not seem to be the right behaviour. 

    Two other reasonable options are:
      i) publish nothing here (as is the case)
     ii) publish the raw ganglia metrics

<gx:metrics>
  <xsl:for-each select="METRIC">
    <gx:metric>
     <xsl:attribute name="name"><xsl:value-of select="@NAME" /></xsl:attribute>
    </gx:metric>
  </xsl:for-each>
</gx:metrics>
-->

</glue:Host>

 </xsl:for-each>

<xsl:text>
</xsl:text>
</glue:SubCluster>

<xsl:text>
</xsl:text>
</glue:Cluster>
 
</xsl:for-each>
</xsl:for-each>

<xsl:text>
</xsl:text>
</glue:ClusterInformation>

</xsl:template>


<!-- The below template converts a METRIC into a long number of megabytes, 
     taking into account the UNITS of the METRIC.
     When this template runs, assume that the context node is a METRIC
     element with UNITS and VAL attributes.

     The expect UNITS values are B, KB, MB, GB and TB

 -->
<xsl:template name="fixUnitsToMB">
   <xsl:choose>

      <!-- nb: 0.00097656 = (1/1024) -->
      <xsl:when test="@UNITS='B'">
         <xsl:value-of select="floor(@VAL * 0.00097656 * 0.00097656)"/>
      </xsl:when>
      <xsl:when test="@UNITS='KB'">
         <xsl:value-of select="floor(@VAL * 0.00097656)"/>
      </xsl:when>
      <xsl:when test="@UNITS='MB'">
         <xsl:value-of select="floor(@VAL)"/>
      </xsl:when>
      <xsl:when test="@UNITS='GB'">
         <xsl:value-of select="floor(1024 * @VAL)"/>
      </xsl:when>
      <xsl:when test="@UNITS='TB'">
         <xsl:value-of select="floor(1024 * 1024 * @VAL)"/>
      </xsl:when>

      <!-- if we don't understand the units, output 0 and also put in 
           a comment for human readers
           TODO investigate outputting a warn to the XSLT processor -->
      <xsl:otherwise>0</xsl:otherwise>

   </xsl:choose> 
</xsl:template>

<!-- This template will take a numeric METRIC VAL and emit a default
     value of 0 if the value is blank, negative, or unspecified to
     avoid deserialization errors -->
<xsl:template name="emitProperNumeric">
  <xsl:param name="numeric"/>
   <xsl:choose>
    <xsl:when test='$numeric > 0'>
       <xsl:value-of select="$numeric"/>
    </xsl:when>
    <xsl:otherwise>
       <xsl:value-of select="0"/>
    </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<!-- This template will take a string METRIC VAL and emit a default
     value of "unknown" if the value is blank, negative, or
     unspecified to avoid deserialization errors -->
<xsl:template name="emitProperString">
  <xsl:param name="string"/>
   <xsl:choose>
    <xsl:when test='string-length($string) > 0'>
       <xsl:value-of select="$string"/>
    </xsl:when>
    <xsl:otherwise>
       <xsl:value-of select="unknown"/>
    </xsl:otherwise>
   </xsl:choose>
</xsl:template>

</xsl:stylesheet>
