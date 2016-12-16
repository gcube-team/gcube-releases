package org.gcube.accounting.analytics;

import org.gcube.accounting.datamodel.AggregatedUsageRecord;
/**
 * Object for calculate quota 
 * 
 * 
 * @author pieve
 *
 */
public abstract class UsageValue {
	protected Class<? extends AggregatedUsageRecord<?, ?>> clz;
	protected TemporalConstraint temporalConstraint;		
	//protected List<FiltersValue> filtersValue;
	protected String identifier;
	protected Double d;
	protected String orderingProperty;
	protected String context;
	
	public UsageValue(){}
	public abstract Class<? extends AggregatedUsageRecord<?, ?>> getClz();
	public abstract String getIdentifier();
	public abstract Double getD();
	public abstract String getOrderingProperty();
	public abstract TemporalConstraint getTemporalConstraint();
	public abstract String getContext();
	
	public abstract void setOrderingProperty(String orderingProperty);
	public abstract void setD(Double d);
	public abstract void setContext(String context);

	
}
