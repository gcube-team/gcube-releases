package org.gcube.application.framework.search.library.model;

public class Criterion {
	
	protected String searchFieldId;
	
	protected String searchFieldValue;
	
	protected String searchFieldName;



	/**
	 * The generic constructor
	 */
	public Criterion() {
		super();
	}

	
	
	/**
	 * A specific constructor
	 * @param searchFieldId the name of the searchable field
	 * @param searchFieldValue the value of the searchable field
	 * e.g. title = 'databases'
	 */
	public Criterion(String searchFieldId, String searchFieldName, String searchFieldValue) {
		super();
		this.searchFieldId = searchFieldId;
		this.searchFieldValue = searchFieldValue;
		this.setSearchFieldName(searchFieldName);
	}

	/**
	 * @return the name of the searchable field
	 */
	public String getSearchFieldId() {
		return searchFieldId;
	}

	/**
	 * Sets the name of the searchable field
	 * @param searchFieldName the name of the searchable field
	 */
	public void setSearchFieldId(String searchFieldId) {
		this.searchFieldId = searchFieldId;
	}

	/**
	 * @return the value of the searchable field
	 */
	public String getSearchFieldValue() {
		return searchFieldValue;
	}

	/**
	 * Sets the value of the searchable field
	 * @param searchFieldValue the value of the searchable field
	 */
	public void setSearchFieldValue(String searchFieldValue) {
		this.searchFieldValue = searchFieldValue;
	}

	/** {@inheritDoc}*/
	public Criterion clone() {
		Criterion newCriterion = new Criterion();
		newCriterion.setSearchFieldId(this.searchFieldId);
		newCriterion.setSearchFieldValue(this.searchFieldValue);
		newCriterion.setSearchFieldName(this.searchFieldName);
		return newCriterion;
	}
	
	public String getSearchFieldName() {
		return searchFieldName;
	}



	public void setSearchFieldName(String searchFieldName) {
		this.searchFieldName = searchFieldName;
	}

}
