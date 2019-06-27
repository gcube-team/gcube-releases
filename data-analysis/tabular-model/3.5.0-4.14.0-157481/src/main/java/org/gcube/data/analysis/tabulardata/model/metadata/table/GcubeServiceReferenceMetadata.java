package org.gcube.data.analysis.tabulardata.model.metadata.table;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.metadata.DataDependentMetadata;

@XmlRootElement(name = "GcubeServiceReferenceMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class GcubeServiceReferenceMetadata implements DataDependentMetadata{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8939340746069638786L;
	
	private String serviceClass;
	private String serviceName;
	private Date creationDate;
	private String externalId;
	
	@SuppressWarnings("unused")
	private GcubeServiceReferenceMetadata(){		
	}

	public GcubeServiceReferenceMetadata(String serviceClass,
			String serviceName, Date creationDate, String externalId) {
		super();
		this.serviceClass = serviceClass;
		this.serviceName = serviceName;
		this.creationDate = creationDate;
		this.externalId = externalId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result
				+ ((externalId == null) ? 0 : externalId.hashCode());
		result = prime * result
				+ ((serviceClass == null) ? 0 : serviceClass.hashCode());
		result = prime * result
				+ ((serviceName == null) ? 0 : serviceName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GcubeServiceReferenceMetadata other = (GcubeServiceReferenceMetadata) obj;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (externalId == null) {
			if (other.externalId != null)
				return false;
		} else if (!externalId.equals(other.externalId))
			return false;
		if (serviceClass == null) {
			if (other.serviceClass != null)
				return false;
		} else if (!serviceClass.equals(other.serviceClass))
			return false;
		if (serviceName == null) {
			if (other.serviceName != null)
				return false;
		} else if (!serviceName.equals(other.serviceName))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GcubeServiceReferenceMetadata [serviceClass=");
		builder.append(serviceClass);
		builder.append(", serviceName=");
		builder.append(serviceName);
		builder.append(", creationDate=");
		builder.append(creationDate);
		builder.append(", externalId=");
		builder.append(externalId);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @return the serviceClass
	 */
	public String getServiceClass() {
		return serviceClass;
	}

	/**
	 * @param serviceClass the serviceClass to set
	 */
	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the externalId
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * @param externalId the externalId to set
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	@Override
	public boolean isInheritable() {
		return false;
	}
	
	
}
