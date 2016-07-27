<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="utf-8" method="text" />
	<xsl:param name="delimiter" select="','" />
	<xsl:template match="/*[node()]">
		<xsl:text>{</xsl:text>
		<xsl:apply-templates mode="detect" select="." />
		<xsl:text>}</xsl:text>
	</xsl:template>
	<xsl:template match="*" mode="detect">
		<xsl:choose>
			<xsl:when test="preceding-sibling::*[name() = name(current())]">
				<xsl:if test="count(./child::*) &gt; 0">
					<xsl:text>, </xsl:text>
				</xsl:if>
				<xsl:apply-templates mode="detect" select="child::*" />
			</xsl:when>
			<xsl:when test="name(preceding-sibling::*[1]) = name(current()) and name(following-sibling::*[1]) != name(current())">
				<xsl:apply-templates mode="obj-content" select="." />
				<xsl:text>]</xsl:text>
				<xsl:if test="count(following-sibling::*[name() != name(current())]) &gt; 0">
					<xsl:text>, </xsl:text>
				</xsl:if>
			</xsl:when>
			<xsl:when test="name(preceding-sibling::*[1]) = name(current())">
				<xsl:apply-templates mode="obj-content" select="." />
				<xsl:if test="name(following-sibling::*) = name(current())">
					<xsl:text>, </xsl:text>
				</xsl:if>
			</xsl:when>
			<xsl:when test="count(./child::*) &gt; 0 or count(@*) &gt; 0">
				<xsl:text>"</xsl:text>
				<xsl:value-of select="name()" />
				<xsl:text>" : </xsl:text>
				<xsl:apply-templates mode="obj-content" select="." />
				<xsl:if test="count(following-sibling::*) &gt; 0">
					<xsl:text>, </xsl:text>
				</xsl:if>
			</xsl:when>
			<xsl:when test="count(./child::*) = 0">
				<xsl:if test="count(preceding-sibling::*) &gt; 0">
					<xsl:text>, </xsl:text>
				</xsl:if>
				<xsl:text>"</xsl:text>
				<xsl:value-of select="name()" />
				<xsl:text>" : "</xsl:text>
				<xsl:apply-templates mode="obj-concat" select=". | following-sibling::*[name() = name(current())]" />
				<xsl:text>"</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="*" mode="obj-content">
		<xsl:text>{</xsl:text>
		<xsl:apply-templates mode="attr" select="@*" />
		<xsl:if test="count(@*) &gt; 0 and (count(child::*) &gt; 0 or text())">
			<xsl:text>, </xsl:text>
		</xsl:if>
		<xsl:apply-templates mode="detect" select="./*" />
		<xsl:if test="count(child::*) = 0 and text() and not(@*)">
			<xsl:text>"</xsl:text>
			<xsl:value-of select="name()" />
			<xsl:text>" : "</xsl:text>
			<xsl:value-of select="text()" />
			<xsl:text>"</xsl:text>
		</xsl:if>
		<xsl:if test="count(child::*) = 0 and text() and @*">
			<xsl:text>"text" : "</xsl:text>
			<xsl:value-of select="text()" />
			<xsl:text>"</xsl:text>
		</xsl:if>
		<xsl:text>}</xsl:text>
		<xsl:if test="position() &lt; last()">
			<xsl:text>, </xsl:text>
		</xsl:if>
	</xsl:template>
	<xsl:template match="*" mode="obj-concat">
		<xsl:apply-templates mode="attr" select="@*" />
		<xsl:if test="count(@*) &gt; 0 and (count(child::*) &gt; 0 or text())">
			<xsl:text>, </xsl:text>
		</xsl:if>
		<xsl:apply-templates mode="detect" select="./*" />
		<xsl:if test="count(child::*) = 0 and text() and not(@*)">
		  <xsl:call-template name="string-replace-all">
		    <xsl:with-param name="text" select="text()" />
		    <xsl:with-param name="replace" select="'&quot;'" />
		    <xsl:with-param name="by" select="'\&quot;'" />
		  </xsl:call-template>
		</xsl:if>
		<xsl:if test="count(child::*) = 0 and text() and @*">
			<xsl:text>"text" : "</xsl:text>
			<xsl:value-of select="text()" />
			<xsl:text>"</xsl:text>
		</xsl:if>
		<xsl:if test="position() &lt; last()">
			<xsl:value-of select="$delimiter" />
		</xsl:if>
	</xsl:template>
	<xsl:template match="@*" mode="attr">
		<xsl:text>"</xsl:text>
		<xsl:value-of select="name()" />
		<xsl:text>" : "</xsl:text>
		<xsl:value-of select="." />
		<xsl:text>"</xsl:text>
		<xsl:if test="position() &lt; last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
	<xsl:template match="node/@TEXT | text()" name="removeBreaks">
		<xsl:param name="pText" select="normalize-space(.)" />
		<xsl:choose>
			<xsl:when test="not(contains($pText, ' '))">
				<xsl:copy-of select="$pText" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat(substring-before($pText, ' '), ' ')" />
				<xsl:call-template name="removeBreaks">
					<xsl:with-param name="pText" select="substring-after($pText, ' ')" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="string-replace-all">
		<xsl:param name="text" />
		<xsl:param name="replace" />
		<xsl:param name="by" />
		<xsl:choose>
			<xsl:when test="contains($text, $replace)">
				<xsl:value-of select="substring-before($text,$replace)" />
				<xsl:value-of select="$by" />
				<xsl:call-template name="string-replace-all">
					<xsl:with-param name="text" select="substring-after($text,$replace)" />
					<xsl:with-param name="replace" select="$replace" />
					<xsl:with-param name="by" select="$by" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>