package org.gcube.application.framework.search.library.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SearchableFieldInfo implements Serializable, Cloneable{
	
	String id;
	String collectionId;
	Set<String> indexCapabilities = new HashSet<String>();	// equal, or, not, etc.
	String indexQueryLanguage;
	boolean isSortable;
	String fieldName;
	
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getCollectionId() {
		return collectionId;
	}
	
	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}
	
	public Set<String> getIndexCapabilities() {
		return indexCapabilities;
	}
	
	public void setIndexCapabilities(Set<String> indexCapabilities) {
		this.indexCapabilities = indexCapabilities;
	}
	
	public String getIndexQueryLanguage() {
		return indexQueryLanguage;
	}
	
	public void setIndexQueryLanguage(String indexQueryLanguage) {
		this.indexQueryLanguage = indexQueryLanguage;
	}
	
	public boolean isSortable() {
		return isSortable;
	}
	
	public void setSortable(boolean isSortable) {
		this.isSortable = isSortable;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	
	public SearchableFieldInfo clone() {
		SearchableFieldInfo clonedSF = new SearchableFieldInfo();
		clonedSF.setId(new String(this.id));
		clonedSF.setCollectionId(new String(this.collectionId));
		for (String ic:indexCapabilities) {
			clonedSF.getIndexCapabilities().add(new String(ic));
		}
		clonedSF.setSortable(this.isSortable);
		clonedSF.setFieldName(new String(this.fieldName));
		return clonedSF;
	}

	
}
