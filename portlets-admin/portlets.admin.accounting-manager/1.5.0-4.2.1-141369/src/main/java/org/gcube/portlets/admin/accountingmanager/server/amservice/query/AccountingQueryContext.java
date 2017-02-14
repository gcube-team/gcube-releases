package org.gcube.portlets.admin.accountingmanager.server.amservice.query;

import java.util.ArrayList;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.datamodel.AggregatedUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AccountingQueryContext extends AccountingQuery {
	private Class<? extends AggregatedUsageRecord<?, ?>> type;
	private Context context;
	private TemporalConstraint temporalConstraint;
	private ArrayList<Filter> filters;

	public AccountingQueryContext(
			Class<? extends AggregatedUsageRecord<?, ?>> type, Context context,
			TemporalConstraint temporalConstraint, ArrayList<Filter> filters) {
		super();
		chartType = ChartType.Context;
		this.type = type;
		this.temporalConstraint = temporalConstraint;
		this.filters = filters;
		this.context = context;
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

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public String toString() {
		return "AccountingQueryContext [type=" + type + ", context=" + context
				+ ", temporalConstraint=" + temporalConstraint + ", filters="
				+ filters + "]";
	}

}