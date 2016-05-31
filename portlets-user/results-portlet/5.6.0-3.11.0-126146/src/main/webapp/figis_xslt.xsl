<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output encoding="UTF-8" method="html"
		omit-xml-declaration="yes" />
	<xsl:template match="/">
		<div>
			<table class="metadata-table">
				<tbody>
					<xsl:if test="//*[local-name()='title']">
						<tr>
							<td>
								<b>Title:</b>
							</td>
							<td>
								<xsl:value-of select="//*[local-name()='title']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//*[local-name()='three_alpha_code']">
						<tr>
							<td>
								<b>Three alpha code:</b>
							</td>
							<td>
								<xsl:value-of select="//*[local-name()='three_alpha_code']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//*[local-name()='name_en']">
						<tr>
							<td>
								<b>Name EN:</b>
							</td>
							<td>
								<xsl:value-of select="//*[local-name()='name_en']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//*[local-name()='name_fr']">
						<tr>
							<td>
								<b>Name FR:</b>
							</td>
							<td>
								<xsl:value-of select="//*[local-name()='name_fr']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//*[local-name()='name_es']">
						<tr>
							<td>
								<b>Name ES:</b>
							</td>
							<td>
								<xsl:value-of select="//*[local-name()='name_es']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//*[local-name()='images']">
						<tr>
							<td>
								<b>Images:</b>
							</td>
							<td>
							<a href="{//*[local-name()='images']}" target="_blank"><xsl:value-of select="//*[local-name()='images']"/></a>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//*[local-name()='scientific_name']">
						<tr>
							<td>
								<b>Scientific Name:</b>
							</td>
							<td>
								<xsl:value-of select="//*[local-name()='scientific_name']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//*[local-name()='family']">
						<tr>
							<td>
								<b>Family:</b>
							</td>
							<td>
								<xsl:value-of select="//*[local-name()='family']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//*[local-name()='personal_author']">
						<tr>
							<td>
								<b>Personal Author:</b>
							</td>
							<td>
								<xsl:value-of select="//*[local-name()='personal_author']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//*[local-name()='year']">
						<tr>
							<td>
								<b>Year:</b>
							</td>
							<td>
								<xsl:value-of select="//*[local-name()='year']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//*[local-name()='diagnostic_features']">
						<tr>
							<td>
								<b>Diagnostic Features:</b>
							</td>
							<td>
								<xsl:value-of select="//*[local-name()='diagnostic_features']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//*[local-name()='area_text']">
						<tr>
							<td>
								<b>Area:</b>
							</td>
							<td>
								<xsl:value-of select="//*[local-name()='area_text']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//*[local-name()='habitat_bio']">
						<tr>
							<td>
								<b>Habitat bio:</b>
							</td>
							<td>
								<xsl:value-of select="//*[local-name()='habitat_bio']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//*[local-name()='interest_fisheries']">
						<tr>
							<td>
								<b>Interest Fisheries:</b>
							</td>
							<td>
								<xsl:value-of select="//*[local-name()='interest_fisheries']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//*[local-name()='local_names']">
						<tr>
							<td>
								<b>Local Name:</b>
							</td>
							<td>
								<xsl:value-of select="//*[local-name()='local_names']" />
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//*[local-name()='factsheet_url']">
						<tr>
							<td>
								<b>Factsheet URL</b>
							</td>
							<td>
								<a href="{//*[local-name()='factsheet_url']}" target="_blank"><xsl:value-of select="//*[local-name()='factsheet_url']"/></a>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="//*[local-name()='factsheet_id']">
						<tr>
							<td>
								<b>Factsheet ID:</b>
							</td>
							<td>
								<xsl:value-of select="//*[local-name()='factsheet_id']" />
							</td>
						</tr>
					</xsl:if>
				</tbody>
			</table>
		</div>
	</xsl:template>
</xsl:stylesheet>
