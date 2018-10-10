package org.gcube.resource.management.quota.library.quotalist;


import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "servicepackage")
@XmlAccessorType (XmlAccessType.FIELD)
public class ServicePackage {

	protected long id;
	protected String name;
	protected List<ServicePackageDetail> servicesPackagesDetail;
	
	public ServicePackage() {}

	public ServicePackage(String name,List<ServicePackageDetail> servicesPackagesDetail) {
		super();
		this.name = name;
		this.servicesPackagesDetail = servicesPackagesDetail;
	}

	
	public String getServicePackagesAsString() {
		return this.name+","+this.servicesPackagesDetail;
	}
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ServicePackageDetail> getServicesPackageDetail() {
		return servicesPackagesDetail;
	}

	public void setPackageDetail(List<ServicePackageDetail> servicesPackagesDetail) {
		this.servicesPackagesDetail = servicesPackagesDetail;
	}

	@Override
	public String toString() {
		return "ServicePackage [id=" + id + ", name=" + name + ", packageDetail="
				+ servicesPackagesDetail + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((servicesPackagesDetail == null) ? 0 : servicesPackagesDetail.hashCode());
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
		ServicePackage other = (ServicePackage) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (servicesPackagesDetail == null) {
			if (other.servicesPackagesDetail != null)
				return false;
		} else if (!servicesPackagesDetail.equals(other.servicesPackagesDetail))
			return false;
		return true;
	}
	
	
}
