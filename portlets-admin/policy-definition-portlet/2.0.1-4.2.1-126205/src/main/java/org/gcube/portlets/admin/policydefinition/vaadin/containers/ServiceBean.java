package org.gcube.portlets.admin.policydefinition.vaadin.containers;

import java.io.Serializable;

public class ServiceBean implements Serializable{
	
	private static final long serialVersionUID = -5621877214506917906L;
	private String id;
	private String serviceName;
	private String serviceClass;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceClass() {
		return serviceClass;
	}
	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}
	
	public ServiceBean() {
		super();
	}
	public ServiceBean(String id, String serviceName, String serviceClass) {
		super();
		this.id = id;
		this.serviceName = serviceName;
		this.serviceClass = serviceClass;
	}
	@Override
	public String toString() {
		return "ServiceBean [id=" + id + ", serviceName=" + serviceName
				+ ", serviceClass=" + serviceClass + "]";
	}
}
