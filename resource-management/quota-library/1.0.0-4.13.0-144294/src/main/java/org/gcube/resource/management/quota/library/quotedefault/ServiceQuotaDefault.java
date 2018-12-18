package org.gcube.resource.management.quota.library.quotedefault;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.resource.management.quota.library.quotalist.AccessType;
import org.gcube.resource.management.quota.library.quotalist.CallerType;
import org.gcube.resource.management.quota.library.quotalist.QuotaType;
import org.gcube.resource.management.quota.library.quotalist.TimeInterval;



/**
 * 
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 *	ex:
 *	<ServiceQuotaDefault>
 *		<callerType>USER</callerType>
 *      <timeInterval>DAILY</timeInterval>
 *      <serviceId>a:b:c</serviceId>
 *      <accessType>ALL</accessType>
 *      <quotaValue>1.5</quotaValue>
 * </ServiceQuotaDefault>
 */

@XmlRootElement(name = "ServiceQuotaDefault")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceQuotaDefault extends QuotaDefault {

	private CallerType callerType;	
	private TimeInterval timeInterval;	
	private String serviceId;
	private AccessType  accessType;	
	private Double quotaValue;
	
	protected ServiceQuotaDefault(){}
	
	@Override
	public QuotaType getQuotaType() {
		return QuotaType.SERVICE;
	}

	@Override
	public String getQuotaAsString() {
		return this.callerType+","+this.serviceId+","+this.timeInterval.toString()+","+this.quotaValue+","+this.accessType;
		
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
		return "ServiceQuotaDefault [callerType=" + callerType
				+ ", timeInterval=" + timeInterval + ", quotaValue="
				+ quotaValue + ", serviceId=" + serviceId + ", accessType="
				+ accessType + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accessType == null) ? 0 : accessType.hashCode());
		result = prime * result
				+ ((callerType == null) ? 0 : callerType.hashCode());
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
		ServiceQuotaDefault other = (ServiceQuotaDefault) obj;
		if (accessType != other.accessType)
			return false;
		if (callerType != other.callerType)
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
