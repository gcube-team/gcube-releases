package org.gcube.application.framework.search.library.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Field implements Cloneable, Serializable {
	
	String id = new String();
	String label = new String();
	String description = new String();
	String dataType = new String();
	ArrayList<SearchableFieldInfo> searchableFields;
	ArrayList<PresentableFieldInfo> presentableFields;
	ArrayList<String> indexCapabilities; 	// the common capabilities of searchable fields
	String value = new String();
	boolean isSearchable;
	ArrayList<String> languages;
	String name = new String();				// used for presentation reasons by the portlet (for example in the case of FTS)
	boolean isSortable;
	
	public boolean isSortable() {
		return isSortable;
	}

	public void setSortable(boolean isSortable) {
		this.isSortable = isSortable;
	}

	public void addLanguage(String language) {
		if (languages == null) {
			languages = new ArrayList<String>();
		}
		languages.add(language);
	}
	
	public ArrayList<String> getLanguages() {
		return languages;
	}
	
	
	public Field() {
		searchableFields = new ArrayList<SearchableFieldInfo>();
		presentableFields = new ArrayList<PresentableFieldInfo>();
		indexCapabilities = new ArrayList<String>();
		languages = new ArrayList<String>();
	}
	
	public void setSearchable(boolean searchable){
		isSearchable = searchable;
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


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getDataType() {
		return dataType;
	}


	public void setDataType(String dataType) {
		this.dataType = dataType;
	}


	public ArrayList<SearchableFieldInfo> getSearchableFields() {
		return searchableFields;
	}


	public void setSearchableFields(ArrayList<SearchableFieldInfo> searchableFields) {
		this.searchableFields = searchableFields;
	}
	
	public void addSearchable(SearchableFieldInfo sfi) {
		searchableFields.add(sfi);
	}


	public ArrayList<PresentableFieldInfo> getPresentableFields() {
		return presentableFields;
	}
	
	public void addPresentable(PresentableFieldInfo pfi) {
		presentableFields.add(pfi);
	}


	public void setPresentableFields(
			ArrayList<PresentableFieldInfo> presentableFields) {
		this.presentableFields = presentableFields;
	}


	public ArrayList<String> getIndexCapabilities() {
		return indexCapabilities;
	}


	public void setIndexCapabilities(ArrayList<String> indexCapabilities) {
		this.indexCapabilities = indexCapabilities;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}
	
	public Field clone() {
		Field clonedF = new Field();
		clonedF.setId(new String(this.id));
		clonedF.setLabel(new String(this.label));
		clonedF.setDescription(new String(this.dataType));
		clonedF.setDataType(new String(this.dataType));
		clonedF.setName(new String(this.name));
		for (int i = 0; i < this.searchableFields.size(); i++) {
			clonedF.addSearchable(searchableFields.get(i).clone());
		}
		for (int i = 0; i < this.presentableFields.size(); i++) {
			clonedF.addPresentable(presentableFields.get(i).clone());
		}
		for (int i = 0; i < this.indexCapabilities.size(); i++) {
			clonedF.getIndexCapabilities().add(new String(this.indexCapabilities.get(i)));
		}
		clonedF.setValue(this.value);
		clonedF.setSearchable(this.isSearchable);
		
		return clonedF;
	}
	
	@Override
	public boolean equals(Object compare) {
		if (((Field)compare).getId().equals(this.id))
			return true;
		else
			return false;
	}
	
	public void setName(String nm) {
		name = nm;
	}
	
	public String getName() {
		return name;
	}
	
}
