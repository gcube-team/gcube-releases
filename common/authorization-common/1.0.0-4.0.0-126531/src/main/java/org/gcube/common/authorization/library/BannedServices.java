package org.gcube.common.authorization.library;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BannedServices {

	private List<BannedService> services;

	protected BannedServices(){}
	
	public BannedServices(List<BannedService> services) {
		super();
		this.services = services;
	}



	public List<BannedService> get() {
		return services;
	}
	
	
	
}
