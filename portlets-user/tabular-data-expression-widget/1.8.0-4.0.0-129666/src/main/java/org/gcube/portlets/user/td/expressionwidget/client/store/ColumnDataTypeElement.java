package org.gcube.portlets.user.td.expressionwidget.client.store;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ColumnDataTypeElement {
	
	protected int id; // For insert in table only
	protected ColumnDataType type;
	
	
	public ColumnDataTypeElement(int id,ColumnDataType type){
		this.id=id;
		this.type=type;
	}
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public ColumnDataType getType() {
		return type;
	}
	public void setCode(ColumnDataType type) {
		this.type = type;
	}
	
	public String getLabel() {
		return type.toString();
	}


	@Override
	public String toString() {
		return "ColumnDataTypeElement [id=" + id + ", type=" + type + "]";
	}

	
	
	
}
