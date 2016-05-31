package org.gcube.portlets.admin.searchmanagerportlet.gwt.shared;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CollectionFieldsBean implements IsSerializable{

	private String ID;
	private String name;
	
	ArrayList<String> presentableFields = new ArrayList<String>();
	ArrayList<String> searchableFields = new ArrayList<String>();
	
	public CollectionFieldsBean() {}
	
	public CollectionFieldsBean(String id, String name) {
		this.ID = id;
		this.name =  name;
	}

	public ArrayList<String> getPresentableFields() {
		return presentableFields;
	}

	public void setPresentableFields(ArrayList<String> presentableFields) {
		this.presentableFields = presentableFields;
	}

	public ArrayList<String> getSearchableFields() {
		return searchableFields;
	}

	public void setSearchableFields(ArrayList<String> searchableFields) {
		this.searchableFields = searchableFields;
	}

	public String getID() {
		return ID;
	}

	public String getName() {
		return name;
	}
}
