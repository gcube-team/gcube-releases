package org.gcube.portlets.admin.accountingmanager.server.amservice.query;

import java.util.ArrayList;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.datamodel.AggregatedUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class AccountingQueryBasic extends AccountingQuery {
	private Class<? extends AggregatedUsageRecord<?, ?>> type;
	private TemporalConstraint temporalConstraint;
	private ArrayList<Filter> filters;
	private boolean noContext;

	public AccountingQueryBasic(
			Class<? extends AggregatedUsageRecord<?, ?>> type,
			TemporalConstraint temporalConstraint, ArrayList<Filter> filters,
			boolean noContext) {
		super();
		chartType = ChartType.Basic;
		this.type = type;
		this.temporalConstraint = temporalConstraint;
		this.filters = filters;
		this.noContext = noContext;
	}

	public Class<? extends AggregatedUsageRecord<?, ?>> getType() {
		return type;
	}

	public void setType(Class<? extends AggregatedUsageRecord<?, ?>> type) {
		this.type = type;
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

	public boolean isNoContext() {
		return noContext;
	}

	public void setNoContext(boolean noContext) {
		this.noContext = noContext;
	}

	@Override
	public String toString() {
		return "AccountingQueryBasic [type=" + type + ", temporalConstraint="
				+ temporalConstraint + ", filters=" + filters + ", noContext="
				+ noContext + "]";
	}

	

}