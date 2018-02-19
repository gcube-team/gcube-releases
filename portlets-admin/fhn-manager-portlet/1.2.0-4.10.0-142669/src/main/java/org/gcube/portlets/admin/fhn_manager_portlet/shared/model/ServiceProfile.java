package org.gcube.portlets.admin.fhn_manager_portlet.shared.model;

import java.util.Date;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;

import com.google.gwt.user.client.rpc.IsSerializable;


public class ServiceProfile implements Storable,IsSerializable{


	
	
	
	public static final ObjectType TYPE=ObjectType.SERVICE_PROFILE;
	
	public static final String prefix="service.";
	
	public static final String ID_FIELD=prefix+"ID";
	public static final String VERSION_FIELD=prefix+"VERSION";
	public static final String DESCRIPTION_FIELD=prefix+"DESCRIPTION";
	public static final String CREATION_FIELD=prefix+"CREATION";
	
	/**
	 * Describes a Service configuration that can be instanced on a cloud provider
	 */
	
	private String id=null;
	private String version;
	private String description;
	private Date creationDate;
	
	@Override
	public String getName() {
		return description!=null?getDescription():getId();
	}
	
	
	
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ServiceProfile other = (ServiceProfile) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public ServiceProfile(String id, String version, String description,
			Date creationDate) {
		super();
		this.id = id;
		this.version = version;
		this.description = description;
		this.creationDate = creationDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServiceProfile [id=");
		builder.append(id);
		builder.append(", version=");
		builder.append(version);
		builder.append(", description=");
		builder.append(description);
		builder.append(", creationDate=");
		builder.append(creationDate);
		builder.append("]");
		return builder.toString();
	}

	public ServiceProfile() {
		// TODO Auto-generated constructor stub
	}
	
	public String getId() {
		return id;
	}
	

	@Override
	public String getKey() {
		return getId();		
	}

	@Override
	public Object getObjectField(String fieldName) {
		if(fieldName.equals(ID_FIELD)) return getId();
		if(fieldName.equals(VERSION_FIELD)) return getVersion();
		if(fieldName.equals(DESCRIPTION_FIELD)) return getDescription();
		if(fieldName.equals(CREATION_FIELD)) return getCreationDate();
		return null;
	}

	
	@Override
	public ObjectType getType() {
		return TYPE;
	}
}
