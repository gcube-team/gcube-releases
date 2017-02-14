package org.gcube.common.authorization.library.provider;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceInfo extends ClientInfo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ServiceIdentifier serviceIdentifier;
	
	protected ServiceInfo() {}

	public ServiceInfo(ServiceIdentifier serviceIdentifier) {
		super();
		this.serviceIdentifier = serviceIdentifier;
	}

	@Override
	public String getId() {
		return serviceIdentifier.getFullIdentifier();
	}

	public ServiceIdentifier getServiceIdentifier() {
		return serviceIdentifier;
	}

	@Override
	public List<String> getRoles() {
		return Collections.emptyList();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((serviceIdentifier == null) ? 0 : serviceIdentifier
						.hashCode());
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
		ServiceInfo other = (ServiceInfo) obj;
		if (serviceIdentifier == null) {
			if (other.serviceIdentifier != null)
				return false;
		} else if (!serviceIdentifier.equals(other.serviceIdentifier))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ServiceInfo [serviceIdentifier=" + serviceIdentifier + "]";
	}
	
}
