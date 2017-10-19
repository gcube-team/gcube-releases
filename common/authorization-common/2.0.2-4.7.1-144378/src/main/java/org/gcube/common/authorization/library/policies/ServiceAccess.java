package org.gcube.common.authorization.library.policies;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceAccess {
		
	private String serviceClass;
	private String name ;
	private String serviceId;
	private HashMap<String, String> serviceSpecificPolices;
		
	public ServiceAccess() {}

	public ServiceAccess(String serviceClass) {
		super();
		this.serviceClass = serviceClass;
	}
	
	public ServiceAccess(String name, String serviceClass) {
		this(serviceClass);
		this.name = name;
	}

	public ServiceAccess(String name, String serviceClass, String serviceId) {
		this(name, serviceClass);
		this.serviceId = serviceId;
	}
	
	public boolean isSubsetOf(ServiceAccess access){
		if (this.equals(access)) return true;
		if (access.getServiceClass()==null) return true;
		
		if (access.getServiceClass().equals(this.serviceClass)){
			if (access.getName()==null) return true;
			if (access.getName().equals(this.name) && access.getServiceId()==null) 
				return true;
		} 
		return false;
	}
	
	public String getAsString(){
		if (serviceClass == null)
			return "*";
		StringBuilder toReturn = new StringBuilder(serviceClass);
		if (name == null)
			return toReturn.append(":").append("*").toString();
		toReturn.append(":").append(name);
		if (serviceId==null && (serviceSpecificPolices==null || serviceSpecificPolices.size()==0))
			return toReturn.append(":").append("*").toString();
		if (serviceId!=null)
			toReturn.append(":").append(serviceId);
		if (serviceSpecificPolices!=null && serviceSpecificPolices.size()!=0){
			toReturn.append("{");
			for (Entry<String , String> entry: serviceSpecificPolices.entrySet())
				toReturn.append(entry.getKey()).append(":").append(entry.getValue());
			toReturn.append("}");
		}
		return toReturn.toString();
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public String getName() {
		return name;
	}

	public String getServiceId() {
		return serviceId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((serviceClass == null) ? 0 : serviceClass.hashCode());
		result = prime * result
				+ ((serviceId == null) ? 0 : serviceId.hashCode());
		result = prime
				* result
				+ ((serviceSpecificPolices == null) ? 0
						: serviceSpecificPolices.hashCode());
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
		ServiceAccess other = (ServiceAccess) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
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
		if (serviceSpecificPolices == null) {
			if (other.serviceSpecificPolices != null)
				return false;
		} else if (!serviceSpecificPolices.equals(other.serviceSpecificPolices))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getAsString();
	}

	
}
