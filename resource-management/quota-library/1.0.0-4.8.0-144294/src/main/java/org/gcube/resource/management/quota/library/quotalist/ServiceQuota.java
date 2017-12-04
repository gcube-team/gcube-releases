package org.gcube.resource.management.quota.library.quotalist;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceQuota extends Quota {

	private String context;
	private String identifier;	
	private CallerType callerType;	
	private TimeInterval timeInterval;
	private Double quotaValue;
	/*
	private Long  servicePackageId;
	*/
	private String serviceId;
	private AccessType  accessType;
		
	private Calendar lastUpdateTime;
	private Calendar creationTime;
	
	
	protected ServiceQuota(){}
	
	public ServiceQuota(String context,String identifier,CallerType callerType,TimeInterval timeInterval,Double quotaValue,AccessType accessType) {
		super();
		this.context = context;
		this.identifier = identifier;
		this.callerType=callerType;
		this.timeInterval = timeInterval;
		this.quotaValue = quotaValue;	
		this.accessType=accessType;
	}
	
	public ServiceQuota(String context,String identifier,CallerType callerType, String serviceId,TimeInterval timeInterval,Double quotaValue,AccessType accessType) {
		super();
		this.context = context;
		this.identifier = identifier;
		this.callerType=callerType;			
		this.serviceId=serviceId;
		this.timeInterval = timeInterval;
		this.quotaValue = quotaValue;	
		this.accessType= accessType;
	}
	
	@Override
	public QuotaType getQuotaType() {
		return QuotaType.SERVICE;
	}

	@Override
	public String getQuotaAsString() {
		return this.context+","+this.identifier+","+this.callerType+","+this.serviceId+","+this.timeInterval.toString()+","+this.quotaValue+","+this.accessType;
		
	}

	public String getContext() {
		return context;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}	

	@Override
	public CallerType getCallerType() {
		return callerType;
	}
	
	public void setCallerType(CallerType callerType) {
		this.callerType = callerType;
	}
	
	@Override
	public TimeInterval getTimeInterval() {		
		return timeInterval;
	}
	public void setTimeInterval(TimeInterval timeInterval) {
		this.timeInterval = timeInterval;
	}

	public Double getQuotaValue() {
		return quotaValue;
	}
	public void setQuotaValue(Double quotaValue) {
		this.quotaValue = quotaValue;
	}
	
	public Calendar getLastUpdateTime() {
		return lastUpdateTime;
	}

	public Calendar getCreationTime() {
		return creationTime;
	}

	public void setLastUpdateTime(Calendar lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public void setCreationTime(Calendar creationTime) {
		this.creationTime = creationTime;
	}

	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public AccessType getAccessType() {
		return accessType;
	}

	public void setAccessType(AccessType accessType) {
		this.accessType = accessType;
	}

	

	@Override
	public String toString() {
		return "ServiceQuota [context=" + context + ", identifier="
				+ identifier + ", callerType=" + callerType + ", timeInterval="
				+ timeInterval + ", quotaValue=" + quotaValue + ", serviceId="
				+ serviceId + ", accessType=" + accessType
				+ ", lastUpdateTime=" + lastUpdateTime + ", creationTime="
				+ creationTime + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accessType == null) ? 0 : accessType.hashCode());
		result = prime * result
				+ ((callerType == null) ? 0 : callerType.hashCode());
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result
				+ ((creationTime == null) ? 0 : creationTime.hashCode());
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result
				+ ((lastUpdateTime == null) ? 0 : lastUpdateTime.hashCode());
		result = prime * result
				+ ((quotaValue == null) ? 0 : quotaValue.hashCode());
		result = prime * result
				+ ((serviceId == null) ? 0 : serviceId.hashCode());
		result = prime * result
				+ ((timeInterval == null) ? 0 : timeInterval.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceQuota other = (ServiceQuota) obj;
		if (accessType != other.accessType)
			return false;
		if (callerType != other.callerType)
			return false;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (creationTime == null) {
			if (other.creationTime != null)
				return false;
		} else if (!creationTime.equals(other.creationTime))
			return false;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		if (lastUpdateTime == null) {
			if (other.lastUpdateTime != null)
				return false;
		} else if (!lastUpdateTime.equals(other.lastUpdateTime))
			return false;
		if (quotaValue == null) {
			if (other.quotaValue != null)
				return false;
		} else if (!quotaValue.equals(other.quotaValue))
			return false;
		if (serviceId == null) {
			if (other.serviceId != null)
				return false;
		} else if (!serviceId.equals(other.serviceId))
			return false;
		if (timeInterval != other.timeInterval)
			return false;
		return true;
	}

	
	
}
