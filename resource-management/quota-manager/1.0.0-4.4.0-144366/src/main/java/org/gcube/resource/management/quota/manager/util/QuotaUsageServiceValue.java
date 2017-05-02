package org.gcube.resource.management.quota.manager.util;

import java.util.List;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.analytics.UsageServiceValue;
import org.gcube.accounting.datamodel.AggregatedUsageRecord;
import org.gcube.resource.management.quota.library.quotalist.AccessType;
import org.gcube.resource.management.quota.library.quotalist.CallerType;

public class QuotaUsageServiceValue extends UsageServiceValue{

	protected Double dQuota;
	protected CallerType callerType;
	protected AccessType accessType; 
	
	public QuotaUsageServiceValue(){}
	
	public QuotaUsageServiceValue(String context,String identifier,Class<? extends AggregatedUsageRecord<?, ?>> clz,TemporalConstraint temporalConstraint,List<Filter> filters){
		super();
		this.context=context;
		this.filters=filters;
		this.clz=clz;
		this.temporalConstraint=temporalConstraint;
		this.identifier=identifier;

	}
	
	
	public Double getdQuota() {
		return dQuota;
	}
	public void setdQuota(Double dQuota) {
		this.dQuota = dQuota;
	}

	public CallerType getCallerType() {
		return callerType;
	}
	public void setCallerType(CallerType callerType) {
		this.callerType = callerType;
	}

	public AccessType getAccessType() {
		return accessType;
	}
	public void setAccessType(AccessType accessType) {
		this.accessType = accessType;
	}
	
}
