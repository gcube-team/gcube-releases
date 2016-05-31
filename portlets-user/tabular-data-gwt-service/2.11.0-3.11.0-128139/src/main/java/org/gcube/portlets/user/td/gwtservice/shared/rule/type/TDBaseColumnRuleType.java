package org.gcube.portlets.user.td.gwtservice.shared.rule.type;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *	
 */
public class TDBaseColumnRuleType extends TDRuleColumnType {
	
	private static final long serialVersionUID = -2967788094664606371L;
	private ColumnDataType columnDataType;
	
	public TDBaseColumnRuleType(){
		super();
	}

	public TDBaseColumnRuleType(ColumnDataType columnDataType) {
		super();
		this.columnDataType = columnDataType;
	}

	public ColumnDataType getColumnDataType() {
		return columnDataType;
	}

	public void setColumnDataType(ColumnDataType columnDataType) {
		this.columnDataType = columnDataType;
	}

	@Override
	public String toString() {
		return "TDBaseColumnRuleType [columnDataType=" + columnDataType + "]";
	}
	
	
	
	
}
