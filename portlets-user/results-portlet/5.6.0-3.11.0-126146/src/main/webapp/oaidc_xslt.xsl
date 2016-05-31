<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output encoding="UTF-8" method="html"
		omit-xml-declaration="yes" />
	<xsl:template match="/">
		<div>
			<table class="metadata-table">
				<tbody>
					<xsl:if test="//metadata/schema">
						<tr>
							<td>
								<b>Schema:</b>
							</td>
							<td>
								<xsl:value-of select="//metadata/schema" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//metadata/record//*[local-name()='title']">
						<tr>
							<td>
								<b>Title:</b>
							</td>
							<td>
								<xsl:value-of select="//metadata/record//*[local-name()='title']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:for-each select="//metadata/record//*[local-name()='creator']">
						<xsl:variable name="css-class">
							<xsl:choose>
								<xsl:when test="position() mod 2 = 0">
									metadata-style-even
								</xsl:when>
								<xsl:otherwise>
									metadata-style-odd
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<tr class="{$css-class}">
							<td>
								<b>Creator:</b>
							</td>
							<td>
								<xsl:value-of select="." />
							</td>
						</tr>
					</xsl:for-each>
					<xsl:for-each select="//metadata/record//*[local-name()='subject']">
						<xsl:variable name="css-class">
							<xsl:choose>
								<xsl:when test="position() mod 2 = 0">
									metadata-style-even
								</xsl:when>
								<xsl:otherwise>
									metadata-style-odd
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<tr class="{$css-class}">
							<td>
								<b>Subject:</b>
							</td>
							<td>
								<xsl:value-of select="." />
							</td>
						</tr>
					</xsl:for-each>
					<xsl:if test="//metadata/record//*[local-name()='description']">
						<tr>
							<td>
								<b>Description:</b>
							</td>
							<td>
								<xsl:value-of select="//metadata/record//*[local-name()='description']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="/metadata/record//*[local-name()='publisher']">
						<tr>
							<td>
								<b>Publisher:</b>
							</td>
							<td>
								<xsl:value-of select="//metadata/record//*[local-name()='publisher']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//metadata/record//*[local-name()='contributor']">
						<tr>
							<td>
								<b>Contributor:</b>
							</td>
							<td>
								<xsl:value-of select="//metadata/record//*[local-name()='contributor']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//metadata/record//*[local-name()='date']">
						<tr>
							<td>
								<b>Date:</b>
							</td>
							<td>
								<xsl:value-of select="//metadata/record//*[local-name()='date']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:for-each select="//metadata/record//*[local-name()='type']">
						<xsl:variable name="css-class">
							<xsl:choose>
								<xsl:when test="position() mod 2 = 0">
									metadata-style-even
								</xsl:when>
								<xsl:otherwise>
									metadata-style-odd
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<tr class="{$css-class}">
							<td>
								<b>Type:</b>
							</td>
							<td>
								<xsl:value-of select="." />
							</td>
						</tr>
					</xsl:for-each>
					<xsl:if test="//metadata/record//*[local-name()='format']">
						<tr>
							<td>
								<b>Format:</b>
							</td>
							<td>
								<xsl:value-of select="//metadata/record//*[local-name()='format']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//metadata/record//*[local-name()='identifier']">
						<tr>
							<td>
								<b>Identifier:</b>
							</td>
							<td>
								<xsl:value-of select="//metadata/record//*[local-name()='identifier']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//metadata/record//*[local-name()='source']">
						<tr>
							<td>
								<b>Source:</b>
							</td>
							<td>
								<xsl:value-of select="//metadata/record//*[local-name()='source']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//metadata/record//*[local-name()='language']">
						<tr>
							<td>
								<b>Language:</b>
							</td>
							<td>
								<xsl:value-of select="//metadata/record//*[local-name()='language']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//metadata/record//*[local-name()='relation']">
						<tr>
							<td>
								<b>Relation:</b>
							</td>
							<td>
								<xsl:value-of select="//metadata/record//*[local-name()='relation']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:for-each select="//metadata/record//*[local-name()='coverage']">
						<xsl:variable name="css-class">
							<xsl:choose>
								<xsl:when test="position() mod 2 = 0">
									metadata-style-even
								</xsl:when>
								<xsl:otherwise>
									metadata-style-odd
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<tr class="{$css-class}">
							<td>
								<b>Coverage:</b>
							</td>
							<td>
								<xsl:value-of select="." />
							</td>
						</tr>
					</xsl:for-each>
					<xsl:if test="//metadata/record//*[local-name()='rights']">
						<tr>
							<td>
								<b>Rights:</b>
							</td>
							<td>
								<xsl:value-of select="//metadata/record//*[local-name()='rights']" />
							</td>
						</tr>
					</xsl:if>
				</tbody>
			</table>
		</div>
	</xsl:template>
</xsl:stylesheet>
