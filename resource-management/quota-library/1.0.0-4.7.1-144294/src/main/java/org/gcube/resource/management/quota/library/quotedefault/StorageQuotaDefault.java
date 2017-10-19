package org.gcube.resource.management.quota.library.quotedefault;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.resource.management.quota.library.quotalist.CallerType;
import org.gcube.resource.management.quota.library.quotalist.QuotaType;
import org.gcube.resource.management.quota.library.quotalist.TimeInterval;

/**
 * 
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 * ex:
 * <StorageQuotaDefault>
 *		<callerType>USER</callerType>
 *      <timeInterval>DAILY</timeInterval>
 *      <quotaValue>1.5</quotaValue>
 * </StorageQuotaDefault>
 */


@XmlRootElement(name = "StorageQuotaDefault")
public class StorageQuotaDefault extends QuotaDefault {
		
	private CallerType callerType;	
	private TimeInterval timeInterval;
	private Double quotaValue;
	
	protected StorageQuotaDefault(){}
	
	
	public StorageQuotaDefault(CallerType callerType,TimeInterval timeInterval,Double quotaValue){
		this.callerType=callerType;
		this.timeInterval=timeInterval;
		this.quotaValue=quotaValue;
	}
	
	@Override

	public QuotaType getQuotaType() {
		return QuotaType.STORAGE;
	}

	@Override
	public String getQuotaAsString() {
		return this.callerType+","+this.timeInterval.toString()+","+quotaValue;
		
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
	
	@Override
	public String toString() {
		return "StorageQuotaDefault [callerType=" + callerType
				+ ", timeInterval=" + timeInterval + ", quotaValue="
				+ quotaValue + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((callerType == null) ? 0 : callerType.hashCode());
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
		StorageQuotaDefault other = (StorageQuotaDefault) obj;
		if (callerType != other.callerType)
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
