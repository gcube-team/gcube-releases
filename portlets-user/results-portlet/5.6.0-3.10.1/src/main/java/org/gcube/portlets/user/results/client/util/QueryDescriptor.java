package org.gcube.portlets.user.results.client.util;

import java.util.List;

import org.gcube.portlets.user.results.shared.SearchableFieldBean;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author massi
 *
 */
public class QueryDescriptor implements IsSerializable{

	private String simpleTerm;
	private List<SearchableFieldBean> advancedFields; 
	private List<String> selectedCollections;
	private String description;
	private String browseBy;
	private String language;
	private QuerySearchType type;
	
	
	public QueryDescriptor() {}

	public QueryDescriptor(String qdesc) {
		simpleTerm = qdesc;
		this.description = "";
		this.browseBy = "";
		this.language = "";
		this.selectedCollections = null;
		this.advancedFields = null;
		this.type = QuerySearchType.SIMPLE;
	}


	public QueryDescriptor(String term, List<SearchableFieldBean> advancedFields, List<String> selectedCollections, String description, String language, QuerySearchType type, String browseBy) {
		super();
		this.description = description;
		this.advancedFields = advancedFields;
		this.browseBy = browseBy;
		this.language = language;
		this.selectedCollections = selectedCollections;
		this.simpleTerm = term;
		this.type = type;
	}

	public String getBrowseBy() {
		return browseBy;
	}

	public void setBrowseBy(String browseBy) {
		this.browseBy = browseBy;
	}


	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<String> getSelectedCollections() {
		return selectedCollections;
	}
	
	public void setSelectedCollections(List<String> selectedCollections) {
		this.selectedCollections = selectedCollections;
	}
	
	public String getSimpleTerm() {
		return simpleTerm;
	}
	
	public void setSimpleTerm(String term) {
		this.simpleTerm = term;
	}

	public List<SearchableFieldBean> getAdvancedFields() {
		return advancedFields;
	}

	public void setAdvancedFields(List<SearchableFieldBean> advancedFields) {
		this.advancedFields = advancedFields;
	}

	public QuerySearchType getType() {
		return type;
	}

	public void setType(QuerySearchType type) {
		this.type = type;
	}	
	
}
