package org.gcube.accounting.analytics;

import org.gcube.accounting.datamodel.AggregatedUsageRecord;

public class UsageStorageValue extends UsageValue {
	protected Class<? extends AggregatedUsageRecord<?, ?>> clz;
	protected TemporalConstraint temporalConstraint;			
	protected String identifier;
	protected Double d;
	protected String orderingProperty;
	
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}	

	public Double getD() {
		return d;
	}

	public void setD(Double d) {
		this.d = d;
	}
	public String getOrderingProperty() {
		return orderingProperty;
	}

	public void setOrderingProperty(String orderingProperty) {
		this.orderingProperty = orderingProperty;
	}
	public TemporalConstraint getTemporalConstraint() {
		if (temporalConstraint!=null)
			return temporalConstraint; 
		else
			return null;
	}

	public void setTemporalConstraint(TemporalConstraint temporalConstraint) {
		this.temporalConstraint = temporalConstraint;
	}

	public Class<? extends AggregatedUsageRecord<?, ?>> getClz() {
		return clz;
	}

	public void setClz(Class<? extends AggregatedUsageRecord<?, ?>> clz) {
		this.clz = clz;
	}
	
	public UsageStorageValue(){}
	
	
	public UsageStorageValue(String context,String identifier,Class<? extends AggregatedUsageRecord<?, ?>> clz){
		super();
		this.context=context;
		this.clz=clz;
		this.identifier=identifier;
		
	}
	
	public UsageStorageValue(String context,String identifier,Class<? extends AggregatedUsageRecord<?, ?>> clz,TemporalConstraint temporalConstraint){
		super();
		this.context=context;
		this.identifier=identifier;
		this.clz=clz;
		this.temporalConstraint=temporalConstraint;
		
	}
	@Override
	public String getContext() {
		return this.context;
	}
	@Override
	public void setContext(String context) {
		this.context=context;
		
	}
	@Override
	public String toString() {
		return "UsageStorageValue [clz=" + clz + ", temporalConstraint="
				+ temporalConstraint + ", identifier=" + identifier + ", d="
				+ d + ", orderingProperty=" + orderingProperty
				+ ", context=" + context
				+ "]";
	}

	
}
