package org.gcube.portlets.user.td.gwtservice.shared.rule;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class RuleColumnPlaceHolderDescriptor implements Serializable {

	private static final long serialVersionUID = -7746819321348425711L;
	private String id;
	private String label;
	private ColumnDataType columnDataType;

	public RuleColumnPlaceHolderDescriptor() {
		super();
	}

	public RuleColumnPlaceHolderDescriptor(String id, String label,
			ColumnDataType columnDataType) {
		super();
		this.id = id;
		this.label = label;
		this.columnDataType = columnDataType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ColumnDataType getColumnDataType() {
		return columnDataType;
	}

	public void setColumnDataType(ColumnDataType columnDataType) {
		this.columnDataType = columnDataType;
	}

	@Override
	public String toString() {
		return "RuleColumnPlaceHolderDescriptor [id=" + id + ", label=" + label
				+ ", columnDataType=" + columnDataType + "]";
	}

}
