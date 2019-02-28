package org.gcube.portlets.admin.accountingmanager.server.amservice.query;

import java.util.ArrayList;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.datamodel.AggregatedUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterKey;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class AccountingQueryTop extends AccountingQuery {
	private Class<? extends AggregatedUsageRecord<?, ?>> type;
	private String scope;
	private FilterKey filterKey;
	private Integer topNumber;
	private Boolean showOthers;
	private TemporalConstraint temporalConstraint;
	private ArrayList<Filter> filters;

	public AccountingQueryTop(Class<? extends AggregatedUsageRecord<?, ?>> type, String scope, FilterKey filterKey,
			Boolean showOthers, Integer topNumber, TemporalConstraint temporalConstraint, ArrayList<Filter> filters) {
		super();
		chartType = ChartType.Top;
		this.type = type;
		this.scope = scope;
		this.temporalConstraint = temporalConstraint;
		this.filters = filters;
		this.filterKey = filterKey;
		this.showOthers = showOthers;
		this.topNumber = topNumber;
	}

	public Class<? extends AggregatedUsageRecord<?, ?>> getType() {
		return type;
	}

	public void setType(Class<? extends AggregatedUsageRecord<?, ?>> type) {
		this.type = type;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public TemporalConstraint getTemporalConstraint() {
		return temporalConstraint;
	}

	public void setTemporalConstraint(TemporalConstraint temporalConstraint) {
		this.temporalConstraint = temporalConstraint;
	}

	public ArrayList<Filter> getFilters() {
		return filters;
	}

	public void setFilters(ArrayList<Filter> filters) {
		this.filters = filters;
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

	@Override
	public String toString() {
		return "AccountingQueryTop [type=" + type + ", scope=" + scope + ", filterKey=" + filterKey + ", topNumber="
				+ topNumber + ", showOthers=" + showOthers + ", temporalConstraint=" + temporalConstraint + ", filters="
				+ filters + "]";
	}

}