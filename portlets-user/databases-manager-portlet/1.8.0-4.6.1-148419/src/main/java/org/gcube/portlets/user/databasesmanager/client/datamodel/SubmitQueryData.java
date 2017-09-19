package org.gcube.portlets.user.databasesmanager.client.datamodel;

public class SubmitQueryData {

	private String query;
	private boolean readOnlyQuery;
	private boolean smartCorrection;
	// the dialect information
	private String language;

	public SubmitQueryData() {
	}

	public void setQuery(String value) {
		this.query = value;
	}

	public void setReadOnlyQuery(boolean value) {
		this.readOnlyQuery = value;
	}

	public void setSmartCorrection(boolean value) {
		this.smartCorrection = value;
	}

	public void setLanguage(String value) {
		this.language = value;
	}

	public String getQuery() {
		return this.query;
	}

	public boolean getReadOnlyQuery() {
		return this.readOnlyQuery;
	}

	public boolean getSmartCorrection() {
		return this.smartCorrection;
	}

	public String getLanguage() {
		return this.language;
	}
}
