package org.gcube.portlets.user.td.tablewidget.client.type;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TableType;


/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TableTypeElement implements Serializable  {
	
	private static final long serialVersionUID = -5913441587564742269L;
	
	private Integer id;
	private TableType tableType;

	public TableTypeElement(Integer id, TableType tableType){
		this.id=id;
		this.tableType=tableType;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public TableType getTableType() {
		return tableType;
	}

	public void setTableType(TableType tableType) {
		this.tableType = tableType;
	}
	
	public String getLabel(){
		return tableType.toString();
	}

	@Override
	public String toString() {
		return "TableTypeElement [id=" + id + ", tableType=" + tableType + "]";
	}

}
