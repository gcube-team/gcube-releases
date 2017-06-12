/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.sdmx;

import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.source.Source;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateColumnData;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateData;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Agencies;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SDMXTemplateExportSession implements Serializable {

	private static final long serialVersionUID = 4176034045408445284L;

	private TemplateData templateData;
	private Agencies agency;
	private Source source;

	private String id;
	private String agencyId;
	private String version;
	private String registryBaseUrl;

	private TemplateColumnData obsValueColumn;

	public SDMXTemplateExportSession() {
		super();
	}

	public SDMXTemplateExportSession(TemplateData templateData,
			Agencies agency, Source source, String id, String agencyId,
			String version, String registryBaseUrl,
			TemplateColumnData obsValueColumn) {
		super();
		this.templateData = templateData;
		this.agency = agency;
		this.source = source;
		this.id = id;
		this.agencyId = agencyId;
		this.version = version;
		this.registryBaseUrl = registryBaseUrl;
		this.obsValueColumn = obsValueColumn;
	}

	public TemplateData getTemplateData() {
		return templateData;
	}

	public void setTemplateData(TemplateData templateData) {
		this.templateData = templateData;
	}

	public Agencies getAgency() {
		return agency;
	}

	public void setAgency(Agencies agency) {
		this.agency = agency;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAgencyId() {
		return agencyId;
	}

	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRegistryBaseUrl() {
		return registryBaseUrl;
	}

	public void setRegistryBaseUrl(String registryBaseUrl) {
		this.registryBaseUrl = registryBaseUrl;
	}

	public TemplateColumnData getObsValueColumn() {
		return obsValueColumn;
	}

	public void setObsValueColumn(TemplateColumnData obsValueColumn) {
		this.obsValueColumn = obsValueColumn;
	}

	@Override
	public String toString() {
		return "SDMXTemplateExportSession [templateData=" + templateData
				+ ", agency=" + agency + ", source=" + source + ", id=" + id
				+ ", agencyId=" + agencyId + ", version=" + version
				+ ", registryBaseUrl=" + registryBaseUrl + ", obsValueColumn="
				+ obsValueColumn + "]";
	}

}
