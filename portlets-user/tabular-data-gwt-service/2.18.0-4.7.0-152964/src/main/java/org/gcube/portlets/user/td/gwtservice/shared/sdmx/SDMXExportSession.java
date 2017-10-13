/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.sdmx;

import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.source.Source;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Agencies;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TableType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SDMXExportSession implements Serializable {

	private static final long serialVersionUID = 4176034045408445284L;

	private TabResource tabResource;
	private Agencies agency;
	private Source source;
	private TableType exportType;

	private String id;
	private String agencyId;
	private String version;
	private String registryBaseUrl;

	private ColumnData obsValueColumn;
	private boolean excel;

	public TabResource getTabResource() {
		return tabResource;

	}

	public void setTabResource(TabResource tabResource) {
		this.tabResource = tabResource;
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

	public TableType getExportType() {
		return exportType;
	}

	public void setExportType(TableType exportType) {
		this.exportType = exportType;
	}

	public ColumnData getObsValueColumn() {
		return obsValueColumn;
	}

	public void setObsValueColumn(ColumnData obsValueColumn) {
		this.obsValueColumn = obsValueColumn;
	}

	public boolean isExcel() {
		return excel;
	}

	public void setExcel(boolean excel) {
		this.excel = excel;
	}

	@Override
	public String toString() {
		return "SDMXExportSession [tabResource=" + tabResource + ", agency=" + agency + ", source=" + source
				+ ", exportType=" + exportType + ", id=" + id + ", agencyId=" + agencyId + ", version=" + version
				+ ", registryBaseUrl=" + registryBaseUrl + ", obsValueColumn=" + obsValueColumn + ", excel=" + excel
				+ "]";
	}

}
