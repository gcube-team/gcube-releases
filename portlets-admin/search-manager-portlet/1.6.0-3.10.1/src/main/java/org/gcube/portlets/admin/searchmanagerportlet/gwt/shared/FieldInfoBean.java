package org.gcube.portlets.admin.searchmanagerportlet.gwt.shared;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FieldInfoBean implements IsSerializable, Comparable<FieldInfoBean>  {
	
	private String ID;
	private String label;
	private String description; // it can be an empty string
	
	ArrayList<SearchableFieldInfoBean> searchableFields = new ArrayList<SearchableFieldInfoBean>();
	ArrayList<PresentableFieldInfoBean> presentableFields = new ArrayList<PresentableFieldInfoBean>();
	

	Set<CollectionInfoBean> availableSearchableCollectionsIDs = new HashSet<CollectionInfoBean>();
	
	Set<CollectionInfoBean> availablePresentableCollectionsIDs = new HashSet<CollectionInfoBean>();
	
	
	public Set<CollectionInfoBean> getAvailableSearchableCollectionsIDs() {
		return availableSearchableCollectionsIDs;
	}


	public void setAvailableSearchableCollectionsIDs(Set<CollectionInfoBean> availableCollectionsIDs) {
		this.availableSearchableCollectionsIDs = availableCollectionsIDs;
	}
	
	
	public Set<CollectionInfoBean> getAvailablePresentableCollectionsIDs() {
		return availablePresentableCollectionsIDs;
	}


	public void setAvailablePresentableCollectionsIDs(Set<CollectionInfoBean> availableCollectionsIDs) {
		this.availablePresentableCollectionsIDs = availableCollectionsIDs;
	}


	public FieldInfoBean() {
		super();
	}


	public FieldInfoBean(String iD, String label, String description, ArrayList<SearchableFieldInfoBean> searchableFields,
			ArrayList<PresentableFieldInfoBean> presentableFields) {
		super();
		this.ID = iD;
		this.label = label;
		this.description = description;
		this.searchableFields = searchableFields;
		this.presentableFields = presentableFields;
	}


	public String getID() {
		return ID;
	}


	public String getLabel() {
		return label;
	}


	public String getDescription() {
		return description;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public void setLabel(String label) {
		this.label = label;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public void addSearchableField(SearchableFieldInfoBean sBean) {
		this.searchableFields.add(sBean);
	}


	public void addPresentableField(PresentableFieldInfoBean pBean) {
		this.presentableFields.add(pBean);
	}


	public ArrayList<SearchableFieldInfoBean> getSearchableFields() {
		return searchableFields;
	}


	public ArrayList<PresentableFieldInfoBean> getPresentableFields() {
		return presentableFields;
	}


	public int compareTo(FieldInfoBean arg0) {
		return this.label.compareTo(arg0.label);
	}	
	
}
