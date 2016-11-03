package org.gcube.vremanagement.vremodeler.impl.util;

import java.io.Serializable;

public class ServicePair implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String serviceClass;
	private String serviceName;
	
	public ServicePair(String serviceName, String serviceClass){
		this.serviceClass= serviceClass;
		this.serviceName= serviceName;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public String getServiceName() {
		return serviceName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
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

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServicePair other = (ServicePair) obj;
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
	
	
	
}
