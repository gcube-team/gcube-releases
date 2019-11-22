package org.gcube.portlets.user.tdwx.shared;

import java.io.Serializable;

/**
 * Filter Information
 * 
 * @author "Giancarlo Panichi"
 * 
 */
public class FilterInformation implements Serializable {

	private static final long serialVersionUID = 856712991956862922L;

	private String filterField;
	private String filterType;
	private String filterComparison;
	private String filterValue;

	public FilterInformation() {
	}

	public FilterInformation(String filterField, String filterType,
			String filterComparison, String filterValue) {
		this.filterField = filterField;
		this.filterType = filterType;
		this.filterComparison = filterComparison;
		this.filterValue = filterValue;
	}

	public String getFilterField() {
		return filterField;
	}

	public void setFilterField(String filterField) {
		this.filterField = filterField;
	}

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getFilterComparison() {
		return filterComparison;
	}

	public void setFilterComparison(String filterComparison) {
		this.filterComparison = filterComparison;
	}

	public String getFilterValue() {
		return filterValue;
	}

	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}

	@Override
	public String toString() {
		return "FilterInformation [filterField=" + filterField
				+ ", filterType=" + filterType + ", filterComparison="
				+ filterComparison + ", filterValue=" + filterValue + "]";
	}

}
