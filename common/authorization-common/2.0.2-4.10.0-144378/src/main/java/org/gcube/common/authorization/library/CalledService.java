package org.gcube.common.authorization.library;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CalledService {

	private String serviceClass;
	private String serviceName;
			
	protected CalledService() {
		super();
	}

	public CalledService(String serviceClass, String serviceName) {
		super();
		this.serviceClass = serviceClass;
		this.serviceName = serviceName;
	}
	
	public String getServiceClass() {
		return serviceClass;
	}

	public String getServiceName() {
		return serviceName;
	}
	
}
