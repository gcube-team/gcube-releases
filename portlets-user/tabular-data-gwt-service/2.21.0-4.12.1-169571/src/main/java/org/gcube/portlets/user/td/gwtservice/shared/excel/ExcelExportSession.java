/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.excel;

import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TableType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ExcelExportSession implements Serializable {

	private static final long serialVersionUID = 4176034045408445284L;

	private TabResource tabResource;
	private TableType exportType;

	private String id;
	private String agencyId;
	private String version;

	private ColumnData obsValueColumn;

	public TabResource getTabResource() {
		return tabResource;

	}

	public void setTabResource(TabResource tabResource) {
		this.tabResource = tabResource;
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

	@Override
	public String toString() {
		return "ExcelExportSession [tabResource=" + tabResource + ", exportType=" + exportType + ", id=" + id
				+ ", agencyId=" + agencyId + ", version=" + version + ", obsValueColumn=" + obsValueColumn + "]";
	}

	

}
