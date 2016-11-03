package org.gcube.portlets.user.td.widgetcommonevent.shared;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TabResourceType;

/**
 * Tabular Resoure Identification
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TRId implements Serializable {

	private static final long serialVersionUID = 192846115142001630L;

	/**
	 * Tabular resource id
	 */
	private String id;

	/**
	 * TabResourceType: Standard, Flow....
	 */
	private TabResourceType tabResourceType;

	/**
	 * Tabular resource type(look TableType): Generic, Codelist, Dataset...
	 */
	private String tableTypeName;

	/**
	 * Last view id or table id of tabular resource
	 */
	private String tableId;

	/**
	 * Type of last Table or View
	 */
	private String tableType;

	/**
	 * If tableId identifies a View then referenceTargertTableId is the
	 * reference to associated table
	 */
	private String referenceTargetTableId;

	/**
	 * True if tableId identifies a View
	 */
	private boolean viewTable;

	
	public static TRId newInstance(TRId trId) {
		return new TRId(trId.getId(), trId.getTabResourceType(),
				trId.getTableTypeName(), trId.getTableId(),
				trId.getTableType(), trId.getReferenceTargetTableId(),
				trId.isViewTable());
	}

	public TRId() {

	}

	public TRId(String id) {
		this.id = id;
		tabResourceType = null;
		tableTypeName = null;
		tableId = null;
		tableType = null;
		referenceTargetTableId = null;
		viewTable = false;

	}

	public TRId(String id, TabResourceType tabResourceType, String tableId) {
		this.id = id;
		this.tabResourceType = tabResourceType;
		tableTypeName = null;
		this.tableId = tableId;
		tableType = null;
		referenceTargetTableId = null;
		viewTable = false;

	}

	public TRId(String id, TabResourceType tabResourceType,
			String tableTypeName, String tableId, String tableType) {
		this.id = id;
		this.tabResourceType = tabResourceType;
		this.tableTypeName = tableTypeName;
		this.tableId = tableId;
		this.tableType = tableType;
		referenceTargetTableId = null;
		viewTable = false;
	}

	public TRId(String id, TabResourceType tabResourceType,
			String tableTypeName, String tableId, String tableType,
			String referenceTargetTableId, boolean viewTable) {
		this.id = id;
		this.tabResourceType = tabResourceType;
		this.tableTypeName = tableTypeName;
		this.tableId = tableId;
		this.tableType = tableType;
		this.referenceTargetTableId = referenceTargetTableId;
		this.viewTable = viewTable;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TabResourceType getTabResourceType() {
		return tabResourceType;
	}

	public void setTabResourceType(TabResourceType tabResourceType) {
		this.tabResourceType = tabResourceType;
	}

	public String getTableTypeName() {
		return tableTypeName;
	}

	public void setTableTypeName(String tableTypeName) {
		this.tableTypeName = tableTypeName;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public String getReferenceTargetTableId() {
		return referenceTargetTableId;
	}

	public void setReferenceTargetTableId(String referenceTargetTableId) {
		this.referenceTargetTableId = referenceTargetTableId;
	}

	public boolean isViewTable() {
		return viewTable;
	}

	public void setViewTable(boolean viewTable) {
		this.viewTable = viewTable;
	}

	public boolean equals(TRId equals) {
		boolean eq = false;
		if (id != null && tableId != null && equals != null
				&& equals.getId() != null && equals.getTableId() != null
				&& id.compareTo(equals.getId()) == 0
				&& tableId.compareTo(equals.getTableId()) == 0) {
			eq = true;
		}

		return eq;
	}

	@Override
	public String toString() {
		return "TRId [id=" + id + ", tabResourceType=" + tabResourceType
				+ ", tableTypeName=" + tableTypeName + ", tableId=" + tableId
				+ ", tableType=" + tableType + ", referenceTargetTableId="
				+ referenceTargetTableId + ", viewTable=" + viewTable + "]";
	}

}
