package org.gcube.accounting.analytics;

import java.util.List;

import org.gcube.accounting.datamodel.AggregatedUsageRecord;

public class UsageServiceValue extends UsageValue {


	protected Class<? extends AggregatedUsageRecord<?, ?>> clz;
	protected TemporalConstraint temporalConstraint;		
	protected List<FiltersValue> filtersValue;
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
	public TemporalConstraint getTemporalConstraint() {
		if (temporalConstraint!=null)
			return temporalConstraint; 
		else
			return null;

	}

	public String getOrderingProperty() {
		return orderingProperty;
	}

	public void setOrderingProperty(String orderingProperty) {
		this.orderingProperty = orderingProperty;
	}
	public void setTemporalConstraint(TemporalConstraint temporalConstraint) {
		this.temporalConstraint = temporalConstraint;
	}

	public Class<? extends AggregatedUsageRecord<?, ?>> getClz() {
		return clz;
	}

	public void setClz(String context,Class<? extends AggregatedUsageRecord<?, ?>> clz) {
		this.clz = clz;
	}

	public UsageServiceValue(){}

	public UsageServiceValue(String context,String identifier,Class<? extends AggregatedUsageRecord<?, ?>> clz,TemporalConstraint temporalConstraint,List<FiltersValue> filtersValue){
		super();
		this.context=context;
		this.filtersValue=filtersValue;
		this.clz=clz;
		this.temporalConstraint=temporalConstraint;
		this.identifier=identifier;

	}

	public List<FiltersValue> getFiltersValue() {
		return filtersValue;
	}

	public void setFiltersValue(List<FiltersValue> filtersValue) {
		this.filtersValue = filtersValue;
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
		return "UsageServiceValue [clz=" + clz + ", temporalConstraint="
				+ temporalConstraint + ", filtersValue=" + filtersValue
				+ ", identifier=" + identifier + ", d=" + d
				+ ", orderingProperty=" + orderingProperty + ", context="
				+ context + "]";
	}



}
