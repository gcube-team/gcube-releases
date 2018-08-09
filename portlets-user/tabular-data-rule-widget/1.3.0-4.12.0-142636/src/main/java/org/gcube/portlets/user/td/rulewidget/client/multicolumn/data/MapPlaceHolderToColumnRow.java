package org.gcube.portlets.user.td.rulewidget.client.multicolumn.data;

import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.rule.RuleColumnPlaceHolderDescriptor;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class MapPlaceHolderToColumnRow implements Serializable {

	private int id;
	private RuleColumnPlaceHolderDescriptor ruleColumnPlaceHolderDescriptor;
	private ColumnData column;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3124977633549118003L;

	public MapPlaceHolderToColumnRow() {
		super();

	}

	public MapPlaceHolderToColumnRow(int id,
			RuleColumnPlaceHolderDescriptor ruleColumnPlaceHolderDescriptor,
			ColumnData column) {
		super();
		this.id = id;
		this.ruleColumnPlaceHolderDescriptor = ruleColumnPlaceHolderDescriptor;
		this.column = column;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RuleColumnPlaceHolderDescriptor getRuleColumnPlaceHolderDescriptor() {
		return ruleColumnPlaceHolderDescriptor;
	}

	public void setRuleColumnPlaceHolderDescriptor(
			RuleColumnPlaceHolderDescriptor ruleColumnPlaceHolderDescriptor) {
		this.ruleColumnPlaceHolderDescriptor = ruleColumnPlaceHolderDescriptor;
	}

	public ColumnData getColumn() {
		return column;
	}

	public void setColumn(ColumnData column) {
		this.column = column;
	}

	public String getPlaceHolderLabel(){
		if(ruleColumnPlaceHolderDescriptor!=null){
			return ruleColumnPlaceHolderDescriptor.getLabel();
		} else {
			return null;
		}
	}
	
	public String getColumnLabel(){
		if(column!=null){
			return column.getLabel();
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return "MapPlaceHolderToColumnRow [id=" + id
				+ ", ruleColumnPlaceHolderDescriptor="
				+ ruleColumnPlaceHolderDescriptor + ", column=" + column + "]";
	}
	
	
	

}