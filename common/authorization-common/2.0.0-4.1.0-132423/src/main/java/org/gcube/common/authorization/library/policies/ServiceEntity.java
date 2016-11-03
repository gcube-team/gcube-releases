package org.gcube.common.authorization.library.policies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceEntity {

	private ServiceAccess service = null;

	private List<ServiceAccess> excludes = null;

	@SuppressWarnings("unused")
	private ServiceEntity() {}

	protected ServiceEntity(ServiceAccess service) {
		if (service==null) throw new IllegalArgumentException("serviceAccess cannot be null");
		this.service = service;
	}

	protected ServiceEntity(List<ServiceAccess> excludes) {
		if (excludes==null || excludes.isEmpty()) throw new IllegalArgumentException("list of excludes cannot be empty");
		List<ServiceAccess> newExcludes = new ArrayList<ServiceAccess>();
		check:
			for (ServiceAccess sa: excludes){
				if (sa.getClass()==null) throw new IllegalArgumentException("excluding all services to a deny policy is illegal");
				for (ServiceAccess alreadySaved : newExcludes)
					if (sa.isSubsetOf(alreadySaved))
						break check;
				newExcludes.add(sa);
			}

		this.excludes = newExcludes;
	}

	public ServiceAccess getService() {
		return service;
	}

	public List<ServiceAccess> getExcludes() {
		return Collections.unmodifiableList(excludes);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((excludes == null) ? 0 : excludes.hashCode());
		result = prime * result + ((service == null) ? 0 : service.hashCode());
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
		ServiceEntity other = (ServiceEntity) obj;
		if (excludes == null) {
			if (other.excludes != null)
				return false;
		} else if (!excludes.equals(other.excludes))
			return false;
		if (service == null) {
			if (other.service != null)
				return false;
		} else if (!service.equals(other.service))
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (service!=null)
			return "ServiceEntity [service=" + service + "]";
		return "ServiceEntity [ allExcept : " + excludes+ "]";
	}
	
	public String getAsString() {
		if (service!=null)
			return service.getAsString();
		else return "allExcept"+ excludes;
	}
	

}
