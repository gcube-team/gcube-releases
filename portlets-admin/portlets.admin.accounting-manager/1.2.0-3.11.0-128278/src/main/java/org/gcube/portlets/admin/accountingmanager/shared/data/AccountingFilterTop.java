package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AccountingFilterTop extends AccountingFilterDefinition implements Serializable {

	private static final long serialVersionUID = -6805006183397381154L;
	private FilterKey filterKey;
	private Integer topNumber;
	private	ArrayList<AccountingFilter> filters;

	public AccountingFilterTop() {
		super();	
		this.chartType=ChartType.Top;
		topNumber=5;
		filterKey=null;
		filters=null;
		
	}

	public AccountingFilterTop(FilterKey filterKey, ArrayList<AccountingFilter> filters, Integer topNumber) {
		super();
		chartType = ChartType.Top;
		this.filterKey = filterKey;
		this.filters = filters;
		this.topNumber=topNumber;
	}

	public FilterKey getFilterKey() {
		return filterKey;
	}

	public void setFilterKey(FilterKey filterKey) {
		this.filterKey = filterKey;
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
		return "AccountingFilterTop [filterKey=" + filterKey + ", topNumber="
				+ topNumber + ", filters=" + filters + "]";
	}

	
	

}
