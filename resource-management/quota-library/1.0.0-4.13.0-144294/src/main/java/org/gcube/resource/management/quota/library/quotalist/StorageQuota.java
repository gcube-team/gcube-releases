package org.gcube.resource.management.quota.library.quotalist;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class StorageQuota extends Quota {
	private String context;
	private String identifier;	
	private CallerType callerType;	
	private TimeInterval timeInterval;
	private Double quotaValue;
	
		
	private Calendar lastUpdateTime;
	private Calendar creationTime;
	
	
	protected StorageQuota(){}
	
	public StorageQuota(String context,String identifier,CallerType callerType, TimeInterval timeInterval,Double quotaValue) {
		super();
		this.context = context;
		this.identifier = identifier;
		this.callerType=callerType;
		this.timeInterval = timeInterval;
		this.quotaValue = quotaValue;		
	}
	
	@Override
	public QuotaType getQuotaType() {
		return QuotaType.STORAGE;
	}

	@Override
	public String getQuotaAsString() {
		return this.context+","+this.identifier+","+this.callerType+","+this.timeInterval.toString()+","+quotaValue;
		
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

	@Override
	public String toString() {
		return "StorageQuota [context=" + context + ", identifier="
				+ identifier + ", callerType=" + callerType + ", timeInterval=" + timeInterval
				+ ", quotaValue=" + quotaValue + ", lastUpdateTime="
				+ lastUpdateTime + ", creationTime=" + creationTime + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		StorageQuota other = (StorageQuota) obj;
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
		if (timeInterval != other.timeInterval)
			return false;
		return true;
	}
	
	
}
