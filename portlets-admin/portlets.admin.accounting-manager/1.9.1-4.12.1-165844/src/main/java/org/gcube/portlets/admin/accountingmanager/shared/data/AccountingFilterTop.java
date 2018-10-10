package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class AccountingFilterTop extends AccountingFilterDefinition implements
		Serializable {

	private static final long serialVersionUID = -6805006183397381154L;
	private FilterKey filterKey;
	private Boolean showOthers;
	private Integer topNumber;
	private ArrayList<AccountingFilter> filters;

	public AccountingFilterTop() {
		super();
		this.chartType = ChartType.Top;
		showOthers = false;
		topNumber = 5;
		filterKey = null;
		filters = null;

	}
	
	public AccountingFilterTop(Boolean showOthers,Integer topNumber) {
		super();
		this.chartType = ChartType.Top;
		this.showOthers = showOthers;
		this.topNumber = topNumber;
		filterKey = null;
		filters = null;

	}
	

	public AccountingFilterTop(FilterKey filterKey,
			ArrayList<AccountingFilter> filters, Boolean showOthers,
			Integer topNumber) {
		super();
		chartType = ChartType.Top;
		this.filterKey = filterKey;
		this.filters = filters;
		this.showOthers = showOthers;
		this.topNumber = topNumber;

	}

	public FilterKey getFilterKey() {
		return filterKey;
	}

	public void setFilterKey(FilterKey filterKey) {
		this.filterKey = filterKey;
	}

	public Boolean getShowOthers() {
		return showOthers;
	}

	public void setShowOthers(Boolean showOthers) {
		this.showOthers = showOthers;
	}

	public Integer getTopNumber() {
		return topNumber;
	}

	public void setTopNumber(Integer topNumber) {
		this.topNumber = topNumber;
	}

	public ArrayList<AccountingFilter> getFilters() {
		return filters;
	}

	public void setFilters(ArrayList<AccountingFilter> filters) {
		this.filters = filters;
	}

	@Override
	public String toString() {
		return "AccountingFilterTop [filterKey=" + filterKey + ", showOthers="
				+ showOthers + ", topNumber=" + topNumber + ", filters="
				+ filters + "]";
	}

}
