package org.gcube.common.core.resources;

import java.util.Calendar;

/**
 * @author Andrea Manzi (ISTI-CNR)
 */
public abstract class GCUBECSInstance   extends GCUBEResource {
	/**
	 * The type of the resource.
	 */
	public static final String TYPE="CSInstance";
	

	
	public GCUBECSInstance() {
		this.type = TYPE;
	}
	
	private String description;
	
	private String csId;
	
	private String owner;
	
	private Calendar registrationTime;
	
	private Calendar StartupTime;
	
	private Calendar EndTime;
	
	private String status;
	
	private String MessageStatus;



	public String getCsId() {
		return csId;
	}

	public void setCsId(String csId) {
		this.csId = csId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Calendar getEndTime() {
		return EndTime;
	}

	public void setEndTime(Calendar endTime) {
		EndTime = endTime;
	}

	public String getMessageStatus() {
		return MessageStatus;
	}

	public void setMessageStatus(String messageStatus) {
		MessageStatus = messageStatus;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Calendar getRegistrationTime() {
		return registrationTime;
	}

	public void setRegistrationTime(Calendar registrationTime) {
		this.registrationTime = registrationTime;
	}

	public Calendar getStartupTime() {
		return StartupTime;
	}

	public void setStartupTime(Calendar startupTime) {
		StartupTime = startupTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		if (!super.equals(obj)) return false;
		
		final GCUBECSInstance other = (GCUBECSInstance) obj;
		
		if (csId == null) {
			if (other.csId != null)
				return false;
		} else if (! csId.equals(other.csId))
			return false;
		
		if (EndTime == null) {
			if (other.EndTime != null)
				return false;
		} else if (! EndTime.equals(other.EndTime))
			return false;
		
		if (MessageStatus == null) {
			if (other.MessageStatus != null)
				return false;
		} else if (! MessageStatus.equals(other.MessageStatus))
			return false;
		
		if (registrationTime == null) {
			if (other.registrationTime != null)
				return false;
		} else if (! registrationTime.equals(other.registrationTime))
			return false;
		
		if (StartupTime == null) {
			if (other.StartupTime != null)
				return false;
		} else if (! StartupTime.equals(other.StartupTime))
			return false;
		
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (! owner.equals(other.owner))
			return false;
		
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (! description.equals(other.description))
			return false;
		
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (! status.equals(other.status))
			return false;
		
		
		return true;
	}

}
