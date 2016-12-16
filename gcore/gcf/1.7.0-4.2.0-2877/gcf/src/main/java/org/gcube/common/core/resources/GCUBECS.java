package org.gcube.common.core.resources;

import java.util.Calendar;

/**
 * 
 * 
 * @author Andrea Manzi (ISTI-CNR)
 *
 */
public abstract class GCUBECS   extends GCUBEResource {
	
	/**
	 * The type of the resource.
	 */
	public static final String TYPE="CS";
	

	
	public GCUBECS() {
		this.type = TYPE;
	}
	
	
	private boolean base64;
	
	private String description;
	
	private String WSDL;
	
	private String BPEL;
	
	private String Osiris;
	
	private String creator;
	
	private Calendar creationTime;
	
	private String processName;



	public boolean isBase64() {
		return base64;
	}

	public void setBase64(boolean base64) {
		this.base64 = base64;
	}

	public String getBPEL() {
		return BPEL;
	}

	public void setBPEL(String bpel) {
		BPEL = bpel;
	}

	public Calendar getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Calendar creationTime) {
		this.creationTime = creationTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOsiris() {
		return Osiris;
	}

	public void setOsiris(String osiris) {
		Osiris = osiris;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getWSDL() {
		return WSDL;
	}

	public void setWSDL(String wsdl) {
		WSDL = wsdl;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (!super.equals(obj)) return false;
		
		final GCUBECS other = (GCUBECS) obj;
		
		if (base64 != other.base64) return false;
		
		if (BPEL == null) {
			if (other.BPEL != null)
				return false;
		} else if (! BPEL.equals(other.BPEL))
			return false;
		
		if (creationTime == null) {
			if (other.creationTime != null)
				return false;
		} else if (! creationTime.equals(other.creationTime))
			return false;
		
		if (creator == null) {
			if (other.creator != null)
				return false;
		} else if (! creator.equals(other.creator))
			return false;
		
		if (Osiris == null) {
			if (other.Osiris != null)
				return false;
		} else if (! Osiris.equals(other.Osiris))
			return false;
		
		if (processName == null) {
			if (other.processName != null)
				return false;
		} else if (! processName.equals(other.processName))
			return false;
		
		if (WSDL == null) {
			if (other.WSDL != null)
				return false;
		} else if (! WSDL.equals(other.WSDL))
			return false;
		
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (! description.equals(other.description))
			return false;
		
		
		return true;
	}


}
