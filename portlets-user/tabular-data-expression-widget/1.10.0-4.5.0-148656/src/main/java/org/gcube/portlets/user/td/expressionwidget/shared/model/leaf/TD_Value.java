package org.gcube.portlets.user.td.expressionwidget.shared.model.leaf;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class TD_Value extends C_Leaf {

	private static final long serialVersionUID = 2802022467528178596L;
	protected String id = "TD_Value";
	protected String value;
	protected ColumnDataType valueType;

	public TD_Value() {

	}

	public TD_Value(ColumnDataType valueType, String value) {
		this.valueType = valueType;
		this.value = value;
		if (valueType != null) {
			switch(valueType){
			case Boolean:
			case Date:
			case Geometry:
			case Integer:
			case Numeric:
				this.readableExpression =  value;
				break;
			case Text:
				this.readableExpression = "\""+ value + "\"";
				break;
			default:
				this.readableExpression = "";
				break;
			
			}
			
			
		} else {
			this.readableExpression = "InvalidType";
		}

	}

	@Override
	public String getId() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ColumnDataType getValueType() {
		return valueType;
	}

	public void setValueType(ColumnDataType valueType) {
		this.valueType = valueType;
	}

	@Override
	public String toString() {
		return "TD_Value [value=" + value + ", valueType=" + valueType + "]";
	}

}
