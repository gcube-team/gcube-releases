package org.gcube.resource.management.quota.library.quotalist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "servicepackagedetail")
@XmlAccessorType (XmlAccessType.FIELD)
public class ServicePackageDetail {

	protected long id;
	protected long idServicesPackage;
	protected String content;

	public ServicePackageDetail() {}

	public ServicePackageDetail(long idServicesPackage,String content) {
		super();
		this.idServicesPackage = idServicesPackage;
		this.content = content;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdServicePackage() {
		return idServicesPackage;
	}

	public void setIdServicesPackage(long idServicesPackage) {
		this.idServicesPackage = idServicesPackage;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (idServicesPackage ^ (idServicesPackage >>> 32));
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
		ServicePackageDetail other = (ServicePackageDetail) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (id != other.id)
			return false;
		if (idServicesPackage != other.idServicesPackage)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ServicePackageDetail [id=" + id + ", idPackage=" + idServicesPackage
				+ ", content=" + content + "]";
	}	
	
	
}
