package org.gcube.portlets.user.td.gwtservice.shared.tr;

import java.io.Serializable;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class DimensionRow implements Serializable {

	private static final long serialVersionUID = -4220185160420435932L;

	
	protected String rowId;
	protected String value;
	
	public DimensionRow(){
		
	}
	
	public DimensionRow(String rowId, String value){
		this.rowId=rowId;
		this.value=value;
	}
	
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	@Override
	public String toString() {
		return "DimensionRow [rowId=" + rowId + ", value=" + value + "]";
	}


}
