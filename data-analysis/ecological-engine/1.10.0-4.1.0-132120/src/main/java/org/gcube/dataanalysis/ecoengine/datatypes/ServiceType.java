package org.gcube.dataanalysis.ecoengine.datatypes;

import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;

public class ServiceType extends StatisticalType{

	
	public ServiceType(ServiceParameters serviceParameter,String name, String description, String defaultValue, boolean optional) {
		super(name, description, defaultValue, optional);
		this.serviceParameter = serviceParameter;
	}

	public ServiceType(ServiceParameters serviceParameter,String name, String description, String defaultValue) {
		super(name, description, defaultValue);
		this.serviceParameter = serviceParameter;
	}

	public ServiceType(ServiceParameters serviceParameter,String name, String description) {
		super(name, description);
		this.serviceParameter = serviceParameter;
	}

	protected ServiceParameters serviceParameter;

	public ServiceParameters getServiceParameter() {
		return serviceParameter;
	}

	public void setServiceParameter(ServiceParameters serviceParameter) {
		this.serviceParameter = serviceParameter;
	}
	
	
}
