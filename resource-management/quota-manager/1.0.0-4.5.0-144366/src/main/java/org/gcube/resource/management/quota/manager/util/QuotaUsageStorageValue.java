package org.gcube.resource.management.quota.manager.util;


import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.analytics.UsageStorageValue;
import org.gcube.accounting.datamodel.AggregatedUsageRecord;

public class QuotaUsageStorageValue extends UsageStorageValue{

	public QuotaUsageStorageValue(){}
	
	public QuotaUsageStorageValue(String context,String identifier,Class<? extends AggregatedUsageRecord<?, ?>> clz,TemporalConstraint temporalConstraint){
		super();
		this.context=context;
		this.identifier=identifier;
		this.clz=clz;
		this.temporalConstraint=temporalConstraint;
	}
		
	
	protected Double dQuota;
	
	public Double getdQuota() {
		return dQuota;
	}

	public void setdQuota(Double dQuota) {
		this.dQuota = dQuota;
	}

	
	
}
