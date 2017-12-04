package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class AccountingFilter implements Serializable {

	private static final long serialVersionUID = 7200526591393559078L;
	private int id;
	private FilterKey filterKey;
	private String filterValue;

	public AccountingFilter() {
		super();
	}

	public AccountingFilter(int id, FilterKey filterKey, String filterValue) {
		super();
		this.id=id;
		this.filterKey = filterKey;
		this.filterValue = filterValue;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public FilterKey getFilterKey() {
		return filterKey;
	}

	public void setFilterKey(FilterKey filterKey) {
		this.filterKey = filterKey;
	}

	public String getFilterValue() {
		return filterValue;
	}

	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}

	@Override
	public String toString() {
		return "AccountingFilter [id=" + id + ", filterKey=" + filterKey
				+ ", filterValue=" + filterValue + "]";
	}

}
