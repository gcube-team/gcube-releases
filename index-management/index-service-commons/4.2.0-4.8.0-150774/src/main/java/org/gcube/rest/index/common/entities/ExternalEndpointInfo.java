package org.gcube.rest.index.common.entities;

public class ExternalEndpointInfo {
	
	public enum ExternalXmlType{
		RSS, ATOM
	}
	
	private String descriptionBaseUrl;
	private String searchBaseUrl;
	private String searchTerm;
	private ExternalXmlType searchType;
	
	public String getDescriptionBaseUrl() {
		return descriptionBaseUrl;
	}
	public void setDescriptionBaseUrl(String descriptionBaseUrl) {
		this.descriptionBaseUrl = descriptionBaseUrl;
	}
	public String getSearchBaseUrl() {
		return searchBaseUrl;
	}
	public void setSearchBaseUrl(String searchBaseUrl) {
		this.searchBaseUrl = searchBaseUrl;
	}
	public String getSearchTerm() {
		return searchTerm;
	}
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	public ExternalXmlType getSearchType() {
		return searchType;
	}
	public void setSearchType(ExternalXmlType searchType) {
		this.searchType = searchType;
	}
	@Override
	public String toString() {
		return "ExternalEndpointInfo [descriptionBaseUrl=" + descriptionBaseUrl + ", searchBaseUrl=" + searchBaseUrl
				+ ", searchTerm=" + searchTerm + ", searchType=" + searchType + "]";
	}

	
}
