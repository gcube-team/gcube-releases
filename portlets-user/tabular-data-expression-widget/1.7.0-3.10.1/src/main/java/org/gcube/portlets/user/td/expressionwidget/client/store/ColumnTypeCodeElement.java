package org.gcube.portlets.user.td.expressionwidget.client.store;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ColumnTypeCodeElement {
	
	protected int id; // For insert in table only
	protected ColumnTypeCode code;
	
	
	public ColumnTypeCodeElement(int id,ColumnTypeCode code){
		this.id=id;
		this.code=code;
	}
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public ColumnTypeCode getCode() {
		return code;
	}
	public void setCode(ColumnTypeCode code) {
		this.code = code;
	}
	
	public String getLabel() {
		return code.getLabel();
	}


	@Override
	public String toString() {
		return "ColumnTypeCodeElement [id=" + id + ", code=" + code + "]";
	}

	
	
}
