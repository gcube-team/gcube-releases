package org.gcube.common.authorization.library.provider;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceIdentifier implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String serviceClass;
	private String serviceName;
	private String serviceId;
	
	protected ServiceIdentifier() {}

	public ServiceIdentifier(String serviceClass, String serviceName, String serviceId) {
		super();
		this.serviceClass = serviceClass;
		this.serviceName = serviceName;
		this.serviceId = serviceId;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getServiceId() {
		return serviceId;
	}
	
	public String getFullIdentifier(){
		return serviceClass+":"+serviceName+":"+serviceId;
	}
	
	
	public static ServiceIdentifier getServiceIdentifierFromId(String serviceId){
		 String[] split = serviceId.split(":");
		 if (split.length!=3)
			 throw new IllegalArgumentException("invalid serviceId provided: "+serviceId);
		 return new ServiceIdentifier(split[0], split[1] , split[2]);
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
		ServiceIdentifier other = (ServiceIdentifier) obj;
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
		
}
