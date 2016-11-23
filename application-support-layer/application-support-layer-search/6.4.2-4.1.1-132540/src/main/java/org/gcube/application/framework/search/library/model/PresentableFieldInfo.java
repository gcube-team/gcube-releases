package org.gcube.application.framework.search.library.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class PresentableFieldInfo implements Serializable, Cloneable{
	
	String id;
	String collectionId;
	boolean projection = true;	// for now always set to true
	boolean isSortable;
	String fieldName;
	Set<String> presentationInfo;
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
	public boolean isProjection() {
		return projection;
	}
	public void setProjection(boolean projection) {
		this.projection = projection;
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
	public Set<String> getPresentationInfo() {
		return presentationInfo;
	}
	public void setPresentationInfo(Set<String> presentationInfo) {
		this.presentationInfo = presentationInfo;
	}
	
	public PresentableFieldInfo clone() {
		PresentableFieldInfo clonedPF = new PresentableFieldInfo();
		clonedPF.setId(new String(this.id));
		clonedPF.setCollectionId(new String(this.collectionId));
		clonedPF.setProjection(this.projection);
		clonedPF.setSortable(this.isSortable());
		clonedPF.setFieldName(new String(this.fieldName));
		if (this.presentationInfo != null)
			clonedPF.setPresentationInfo(new HashSet<String>(this.presentationInfo));
		
		return clonedPF;
	}

}
