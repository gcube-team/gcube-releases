package org.gcube.portlets.user.tdwx.shared;

import java.io.Serializable;

/**
 * Sort Information of column
 * 
 * @author "Giancarlo Panichi"
 * 
 */
public class SortInformation implements Serializable {

	private static final long serialVersionUID = 3030241316222415997L;

	private String sortField;
	private String sortDir;

	public SortInformation() {
	}

	public SortInformation(String sortField, String sortDir) {
		this.sortField = sortField;
		this.sortDir = sortDir;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getSortDir() {
		return sortDir;
	}

	public void setSortDir(String sortDir) {
		this.sortDir = sortDir;
	}

	@Override
	public String toString() {
		return "SortInformation [sortField=" + sortField + ", sortDir="
				+ sortDir + "]";
	}

}
