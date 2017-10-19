package org.gcube.portlets.admin.accountingmanager.server.amservice.query;

import java.util.ArrayList;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.datamodel.AggregatedUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;
import org.gcube.portlets.admin.accountingmanager.shared.data.Spaces;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class AccountingQuerySpaces extends AccountingQuery {
	private Class<? extends AggregatedUsageRecord<?, ?>> type;
	private Spaces spaces;
	private TemporalConstraint temporalConstraint;
	private ArrayList<Filter> filters;

	public AccountingQuerySpaces(
			Class<? extends AggregatedUsageRecord<?, ?>> type, Spaces spaces,
			TemporalConstraint temporalConstraint, ArrayList<Filter> filters) {
		super();
		chartType = ChartType.Spaces;
		this.type = type;
		this.temporalConstraint = temporalConstraint;
		this.filters = filters;
		this.spaces = spaces;
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

	public Spaces getSpaces() {
		return spaces;
	}

	public void setSpaces(Spaces spaces) {
		this.spaces = spaces;
	}

	@Override
	public String toString() {
		return "AccountingQuerySpaces [type=" + type + ", spaces=" + spaces
				+ ", temporalConstraint=" + temporalConstraint + ", filters="
				+ filters + "]";
	}

}