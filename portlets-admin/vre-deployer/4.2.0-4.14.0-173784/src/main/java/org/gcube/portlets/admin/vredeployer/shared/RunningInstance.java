package org.gcube.portlets.admin.vredeployer.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RunningInstance implements Serializable {
	private String name;
	private String serviceClass;
	
	public RunningInstance() {
		super();
	}

	public RunningInstance(String name, String serviceClass) {
		super();
		this.name = name;
		this.serviceClass = serviceClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}
	
	
}
