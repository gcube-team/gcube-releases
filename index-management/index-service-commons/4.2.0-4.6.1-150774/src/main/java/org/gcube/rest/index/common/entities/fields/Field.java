package org.gcube.rest.index.common.entities.fields;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.rest.index.common.entities.fields.config.FieldType;

@XmlRootElement
public class Field {
	
	private String name;
	private FieldType fieldType;

	
	@SuppressWarnings("unused")
	private Field(){} //no empty field should be provided by constructor
	
	
	public Field(String name){
		this(name, FieldType.STRING);
	}

	public Field(String name, FieldType fieldType){
		this.name = name;
		this.fieldType = fieldType;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}

	
	public static Field copyOf(Field field){
		Field f = new Field();
		f.setName(field.getName());
		f.setFieldType(field.getFieldType());
		return f;
	}
	
	@Override
	public String toString(){
		return " [fieldName: "+name +" , fieldType: "+fieldType +"]";
	}
	
	
}
