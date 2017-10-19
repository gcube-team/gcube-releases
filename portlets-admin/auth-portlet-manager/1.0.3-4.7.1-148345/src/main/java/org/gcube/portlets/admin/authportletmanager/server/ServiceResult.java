package org.gcube.portlets.admin.authportletmanager.server;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Service")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ServiceResult {

	@XmlElement(name="ServiceClass")
	private String serviceClass;
	@XmlElement(name="ServiceName")
	private String serviceName;
	
	
	public String getServiceClass() {
		return serviceClass;
	}
	public String getServiceName() {
		return serviceName;
	}
		
}
