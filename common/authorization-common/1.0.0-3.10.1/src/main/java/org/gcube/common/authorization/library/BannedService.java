package org.gcube.common.authorization.library;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BannedService {

	private String serviceClass;
	private String serviceName;
	private Calendar banTime;
		
	protected BannedService() {
		super();
	}
	
	public BannedService(String serviceClass, String serviceName, Calendar banTime) {
		super();
		this.serviceClass = serviceClass;
		this.serviceName = serviceName;
		this.banTime = banTime;
	}

	public BannedService(String serviceClass, String serviceName) {
		super();
		this.serviceClass = serviceClass;
		this.serviceName = serviceName;
		this.banTime = Calendar.getInstance();
	}
	
	public String getServiceClass() {
		return serviceClass;
	}

	public String getServiceName() {
		return serviceName;
	}

	public Calendar getCreationTime() {
		return banTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((serviceClass == null) ? 0 : serviceClass.hashCode());
		result = prime * result
				+ ((serviceName == null) ? 0 : serviceName.hashCode());
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
		BannedService other = (BannedService) obj;
		if (serviceClass == null) {
			if (other.serviceClass != null)
				return false;
		} else if (!serviceClass.equals(other.serviceClass))
			return false;
		if (serviceName == null) {
			if (other.serviceName != null)
				return false;
		} else if (!serviceName.equals(other.serviceName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BannedService [serviceClass=" + serviceClass + ", serviceName="
				+ serviceName + ", banTime=" + banTime.getTimeInMillis() + "]";
	}
	
}
