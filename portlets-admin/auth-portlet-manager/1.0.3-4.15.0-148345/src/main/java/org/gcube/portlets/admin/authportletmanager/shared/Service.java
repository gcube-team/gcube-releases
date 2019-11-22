package org.gcube.portlets.admin.authportletmanager.shared;

import java.io.Serializable;


/**
 * 
 * @author pieve
 *
 */
public class Service implements Serializable, Comparable<Service> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3580147805704412936L;
	/**
	 * 
	 */
	
	String serviceClass;
	String serviceName;
	String serviceId;
	public Service() {
		super();
	}
	public Service(String serviceClass, String serviceName, String serviceId) {
		super();
		this.serviceClass = serviceClass;
		this.serviceName = serviceName;
		this.serviceId = serviceId;
	}
	
	public String getServiceClass() {
		return serviceClass;
	}
	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((serviceClass == null) ? 0 : serviceClass.hashCode());
		result = prime * result
				+ ((serviceId == null) ? 0 : serviceId.hashCode());
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
		Service other = (Service) obj;
		if (serviceClass == null) {
			if (other.serviceClass != null)
				return false;
		} else if (!serviceClass.equals(other.serviceClass))
			return false;
		if (serviceId == null) {
			if (other.serviceId != null)
				return false;
		} else if (!serviceId.equals(other.serviceId))
			return false;
		if (serviceName == null) {
			if (other.serviceName != null)
				return false;
		} else if (!serviceName.equals(other.serviceName))
			return false;
		return true;
	}
	@Override
	public int compareTo(Service o) {
		return this.serviceClass.compareTo(o.serviceClass);
	}
	

	
	
		
}
