package org.gcube.resource.management.quota.library.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.resource.management.quota.library.quotalist.TimeInterval;


@XmlRootElement(name = "quotaStatus")
@XmlAccessorType(XmlAccessType.FIELD)

public class QuotaStorageStatus {
	
	protected QuotaStorageStatus(){}
	
	
	public QuotaStorageStatus(String identifier,TimeInterval timeInterval,Double quotaValue,Double quotaUsage) {
		super();
		this.identifier = identifier;
		this.timeInterval = timeInterval;
		this.quotaValue = quotaValue;	
		this.quotaUsage=quotaUsage;
	}
	
	private String identifier;	
	private TimeInterval timeInterval;
	private Double quotaValue;
	private Double quotaUsage;
	public String getIdentifier() {
		return identifier;
	}
	
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
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
	public Double getQuotaUsage() {
		return quotaUsage;
	}
	public void setQuotaUsage(Double quotaUsage) {
		this.quotaUsage = quotaUsage;
	}
	
	@Override
	public String toString() {
		return "QuotaStorageStatus [identifier=" + identifier
				+ ", timeInterval=" + timeInterval + ", quotaValue="
				+ quotaValue + ", quotaUsage=" + quotaUsage + "]";
	}
	
	
	
}
