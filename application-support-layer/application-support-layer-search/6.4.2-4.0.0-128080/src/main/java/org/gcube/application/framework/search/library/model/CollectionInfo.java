package org.gcube.application.framework.search.library.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

public class CollectionInfo implements Serializable {
	
	String id ;
	String name;
	String description;
	String collectionType;
	//Vector<String> languages;
	String creationDate;
	String recno;
	boolean isCollectionGroup;
	ArrayList<Field> searchIndices;
	ArrayList<Field> presentationFields;
	ArrayList<Field> browsableFields;
	boolean fts;
	boolean geospatial;
	boolean external;	// not needed for search
	ArrayList<String> languages;
	String ftsId;
	Field geospatialField;
	
	
	
	public Field getGeospatialField() {
		return geospatialField;
	}

	public void setGeospatialField(Field geospatialField) {
		this.geospatialField = geospatialField;
	}

	public void setCollectionType(String collectionType){
		this.collectionType = collectionType;
	}
	
	public String getCollectionType(){
		return collectionType;
	}
	
	public String getFtsId() {
		return ftsId;
	}

	public void setFtsId(String ftsId) {
		this.ftsId = ftsId;
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
	
	public void setLanguages(ArrayList<String> langs) {
		languages = langs;
	}
	
	public CollectionInfo() {
		fts = false;
		searchIndices = new ArrayList<Field>();
		languages = new ArrayList<String>();
		presentationFields = new ArrayList<Field>();
		browsableFields = new ArrayList<Field>();
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


//	public Vector<String> getLanguages() {
//		return languages;
//	}
//
//
//	public void setLanguages(Vector<String> languages) {
//		this.languages = languages;
//	}


	public String getCreationDate() {
		return creationDate;
	}


	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}


	public String getRecno() {
		return recno;
	}


	public void setRecno(String recno) {
		this.recno = recno;
	}


	public boolean isCollectionGroup() {
		return isCollectionGroup;
	}


	public void setCollectionGroup(boolean isCollectionGroup) {
		this.isCollectionGroup = isCollectionGroup;
	}


	public ArrayList<Field> getIndices() {
		return searchIndices;
	}
	
	public ArrayList<Field> getBrowsableFields() {
		return browsableFields;
	}
	
	public ArrayList<Field> getPresentationFields() {
		return presentationFields;
	}


	public void setIndices(ArrayList<Field> indices) {
		this.searchIndices = indices;
	}
	
	public void setBrowsableFields(ArrayList<Field> brFields) {
		 this.browsableFields = brFields;
	}

	public void setPresentationFields(ArrayList<Field> presentationFields) {
		this.presentationFields = presentationFields;
	}
	
	public boolean isFts() {
		return fts;
	}


	public void setFts(boolean fts) {
		this.fts = fts;
	}


	public boolean isGeospatial() {
		return geospatial;
	}


	public void setGeospatial(boolean geospatial) {
		this.geospatial = geospatial;
	}


	public boolean isExternal() {
		return external;
	}


	public void setExternal(boolean external) {
		this.external = external;
	}
	
//	public void setLanguage() {
//		language = true;
//	}
//	
//	public boolean getLanguage() {
//		return language;
//	}
	

	@Override
	public boolean equals(Object colInfo) {
		if (!(colInfo instanceof CollectionInfo))
			return false;
		else {
			if (((CollectionInfo)colInfo).getId().equals(this.id))
				return true;
			else
				return false;
		}
	}

}
