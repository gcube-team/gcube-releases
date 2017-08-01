package org.gcube.portlets.user.td.widgetcommonevent.shared.grid.model;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * 
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class RowRaw implements Serializable {

	private static final long serialVersionUID = -1603145847134978854L;

	protected String rowId;
	protected Map<String, String> map;

	public RowRaw() { 

	}

	/**
	 * 
	 * @param rowId
	 *            row identify
	 * @param map
	 *            A map from columnLocalId to value as String
	 */
	public RowRaw(String rowId, Map<String, String> map) {
		this.rowId = rowId;
		this.map = map;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	@Override
	public String toString() {
		return "RowRaw [rowId=" + rowId + ", map=" + map + "]";
	}

}
