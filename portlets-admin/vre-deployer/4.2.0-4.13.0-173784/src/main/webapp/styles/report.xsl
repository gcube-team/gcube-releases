<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!-- Author: Marko Mikulicic (ISTI-CNR) marko.mikulicic@isti.cnr.it-->
	
  <xsl:template match="org.gcube.vremanagement.vremodeler.utils.reports.DeployReport">
    <html>
      <style type="text/css">
      
    html {
    	font-family: Verdana, Geneva, Arial, Helvetica, sans-serif;
		font-size: 10px;    
    }
    
	.mytd, .myth { 
	  border: 1px solid #bebebe; 
	}
	
	/*
	li { list-style: none; }
	*/
	
	.myul {
          padding-left: 4px;
	}

	
	
	/*
	* { font-size: 10pt; }
	*/
	


	.tableTitle {
	  font-weight: bold; 
	 }

	/* sample formatting */
	.separator-Resources {border-top: 1px solid #bebebe; margin-top: 12px; margin-bottom: 12px }
	.hasChildren { margin-bottom: 4px; }
	
	/*
	li.element-Service > span { text-decoration: underline; }
	*/
	.element-Service {
		text-decoration: underline;
	}
	
	
	
	/* sample coloring */
	.keyword-RUNNING { color: green; }
	.keyword-ACTIVATED { color: #fe6000; }
	.keyword-SUCCESS { color: green; }
	.keyword-FAILED { color: red; }
	.keyword-SKIPPED { color: red; }

	/*
	li.element-MissingDependencies table { background-color: #fedede; }
	*/
	.element-MissingDependencies{
		background-color: #fedede;
	}
	
	
      </style>

      <!-- starts rendering -->
      <ul class="myul">
	<xsl:apply-templates select="." mode="list"/>
      </ul>
    </html>
  </xsl:template>
  
   <!-- some cells contain keywords which can be exposed at the css level -->
  <xsl:template match="Status|DependenciesResolutionStatus" mode="highlight">
    <span class="keyword-{.} element-{local-name()}"><xsl:value-of select="."/></span>
  </xsl:template>

  <!-- some other cells contain plain text (containg spaces) so we cannot encode the content in a css class -->
  <xsl:template match="*" mode="highlight">
    <span class="element-{local-name()}"><xsl:value-of select="."/></span>
  </xsl:template>


  <!-- Some tables aren't contained in a tag that allows the user to infer what is the table title. -->
  <xsl:template match="Resources" mode="tableHeader">
    <div class="separator-{local-name()}"/>
    <span class="tableTitle"><xsl:value-of select="local-name()"/>:</span>
  </xsl:template>

  <!-- but, normally the title of a table doesn't have to be displayed.
       If you want all titles to be rendered, just comment out this template
       and put '*' in the previous one.
    -->
  <xsl:template match="*" mode="tableHeader">
    <div class="separator-{local-name()}"/>
  </xsl:template>

  <!-- each cell of the tabular form is rendered here.
       no nested structure allowed.
    -->
  <xsl:template match="*" mode="tabular">
    <td class="mytd"><xsl:apply-templates select="." mode="highlight"/></td>
  </xsl:template>

  <!-- some elements are presented in tabular form.
       They are intercepted here and all of their children is rendered in the 'tabular' mode.
    -->
  <xsl:template match="ResolvedDependencies|MissingDependencies|Packages|Resources" mode="list">
    <li class="element-{local-name()}">
      <!-- some elements have to be rendered with some title. See the
	mode "tableHeader" template rule above. -->
      <xsl:apply-templates select="." mode="tableHeader"/>

      <table>
	<tr>
	  <!-- create the table headers. Table columns are inferred from the first child. -->
	  <xsl:for-each select="*[1]/*">
	    <th class="myth"><xsl:value-of select="local-name()"/></th>
	  </xsl:for-each>
	</tr>
	<xsl:for-each select="*">
	  <tr>
	    <xsl:apply-templates select="*" mode="tabular"/>
	  </tr>
	</xsl:for-each>
      </table>
    </li>
  </xsl:template>

  <!-- each cell of the list form is rendered here.
       it also handles nested structures.
    -->
  <xsl:template match="*" mode="list">
    <xsl:apply-templates select="." mode="baseList"/>
  </xsl:template>

  <!-- this is the base implementation of the list layout cells.
       If you need to override the presentation for a given entry, override the previous 
       template and you can still call the baseList mode for normal behavior.
    -->
  <xsl:template match="*" mode="baseList">
    <li class="element-{local-name()}"><span class="tableTitle"><xsl:value-of select="local-name()"/></span>: 

      <!-- detect if our children is a structured nested cell or a plain text (we are leaf) -->
      <xsl:choose>
	<xsl:when test="count(*) = 0">
	  <xsl:apply-templates select="." mode="highlight"/>
	</xsl:when>
	<xsl:otherwise>
	  <div class="hasChildren"/>
	  <ul class="myul">
	    <xsl:apply-templates select="*" mode="list"/>
	  </ul>
	</xsl:otherwise>
	</xsl:choose>
    </li>
  </xsl:template>
  
</xsl:stylesheet>
