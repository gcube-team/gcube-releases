package org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientFieldType;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ClientField implements IsSerializable{

	
	ClientFieldType type;
	String name;
	String value;
	
	public ClientFieldType getType() {
		return type;
	}
	
	public ClientField() {
		type=ClientFieldType.STRING;
		name="DefaultFieldName";
		value="DefaultValue";
	}
	
	public ClientField(String name,String value) {
		this();
		this.name=name;
		this.value=value;
	}
	
	public ClientField(String name,String value,ClientFieldType type) {
		this(name,value);
		this.setType(type);
	}
	public void setType(ClientFieldType type) {
		this.type = type;		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
