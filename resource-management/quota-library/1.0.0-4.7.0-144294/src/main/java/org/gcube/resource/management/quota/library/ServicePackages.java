package org.gcube.resource.management.quota.library;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.resource.management.quota.library.quotalist.ServicePackage;


@XmlRootElement(name = "servicepackages")
@XmlAccessorType (XmlAccessType.FIELD)
public class ServicePackages {

	@XmlElement(name = "servicepackage")
	private List<ServicePackage> servicePackages;

	@SuppressWarnings("unused")
	private ServicePackages(){}

	public ServicePackages(List<ServicePackage> servicePackages) {
		super();
		this.servicePackages = servicePackages;
	}
	
	public List<ServicePackage> getPackages() {
		return servicePackages;
	}

	
}
