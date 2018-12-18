package org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.data;

import java.util.Date;

import org.gcube.portlets.admin.fhn_manager_portlet.client.GUICommon;

public class FieldDefinition{
	
	public enum Type{
		String,Byte,Double,Date,Integer
	}
	
	private String field;
	private String Label;
	private FieldDefinition.Type type;
	
	public FieldDefinition(String field, String label, FieldDefinition.Type type) {
		super();
		this.field = field;
		Label = label;
		this.type = type;
	}
	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}
	/**
	 * @param field the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return Label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		Label = label;
	}
	/**
	 * @return the type
	 */
	public FieldDefinition.Type getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(FieldDefinition.Type type) {
		this.type = type;
	}
	
	public String format(Object fieldValue){
		return format(fieldValue,getType());
	}
	
	
	private static final String format(Object value,FieldDefinition.Type type){
		if(value==null) return "N/A";
		try{
		switch(type){
		case Date : return GUICommon.dateFormat.format((Date) value);
		case Double : return GUICommon.decimalFormat.format((double) value);
		case Byte : return GUICommon.readableBytes(((Long)value).longValue());
		default : return value.toString();
		}
		}catch(Throwable t){
			// log error
			return "N/A";
		}
	}
	
}