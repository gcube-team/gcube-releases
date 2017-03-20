package org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.fields.FilterCategory;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.ClientFieldType;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.ClientFilterOperator;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.ClientObjectType;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.ClientResourceType;

import com.google.gwt.user.client.rpc.IsSerializable;

public class EnumSerializationForcer implements IsSerializable {

	private FilterCategory category;
	private SpeciesFields fields;
	private ClientFieldType fieldType;
	private ClientFilterOperator operator;
	private ClientObjectType objType;
	private ClientResourceType resType;
	
	public EnumSerializationForcer() {
		// TODO Auto-generated constructor stub
	}

	public FilterCategory getCategory() {
		return category;
	}

	public void setCategory(FilterCategory category) {
		this.category = category;
	}

	public SpeciesFields getFields() {
		return fields;
	}

	public void setFields(SpeciesFields fields) {
		this.fields = fields;
	}

	public ClientFieldType getFieldType() {
		return fieldType;
	}

	public void setFieldType(ClientFieldType fieldType) {
		this.fieldType = fieldType;
	}

	public ClientFilterOperator getOperator() {
		return operator;
	}

	public void setOperator(ClientFilterOperator operator) {
		this.operator = operator;
	}

	public ClientObjectType getObjType() {
		return objType;
	}

	public void setObjType(ClientObjectType objType) {
		this.objType = objType;
	}

	public ClientResourceType getResType() {
		return resType;
	}

	public void setResType(ClientResourceType resType) {
		this.resType = resType;
	}
	
	
}
