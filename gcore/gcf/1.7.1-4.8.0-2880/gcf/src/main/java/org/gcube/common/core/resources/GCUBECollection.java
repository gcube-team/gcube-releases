package org.gcube.common.core.resources;

import java.util.ArrayList;
import java.util.Calendar;



public abstract class GCUBECollection  extends GCUBEResource {

	
	/**
	 * The type of the resource.
	 */
	public static final String TYPE="Collection";
	

	
	public GCUBECollection() {
		this.type = TYPE;
	}
	
	/** 
	 * The Collection  description.
	 */
	private String description;

	/** 
	 * The Collection Name
	 */
	private String name;
	
	/**
	 * virtual attribute
	 */
	
	private boolean isVirtual = false;
	
	
	private boolean isUserCollection= false;
	

	private Calendar creationTime;
	
	
	private String creator;
	
	private int numberOfMembers;
	
	private Calendar lastUpdateTime;
	
	private Calendar previousUpdateTime;
	
	private String LastModifier;
	
	
	private ArrayList<String> isMemberOf = new ArrayList<String>();
	
	private ArrayList<String > schemaURIs = new ArrayList<String>();



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

	public ArrayList<String> getIsMemberOf() {
		return isMemberOf;
	}

	public void setIsMemberOf(ArrayList<String> isMemberOf) {
		this.isMemberOf = isMemberOf;
	}

	public boolean isUserCollection() {
		return isUserCollection;
	}

	public void setUserCollection(boolean isUserCollection) {
		this.isUserCollection = isUserCollection;
	}

	public boolean isVirtual() {
		return isVirtual;
	}

	public void setVirtual(boolean isVirtual) {
		this.isVirtual = isVirtual;
	}

	public String getLastModifier() {
		return LastModifier;
	}

	public void setLastModifier(String lastModifier) {
		LastModifier = lastModifier;
	}

	public Calendar getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Calendar lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumberOfMembers() {
		return numberOfMembers;
	}

	public void setNumberOfMembers(int numberOfMembers) {
		this.numberOfMembers = numberOfMembers;
	}

	public Calendar getPreviousUpdateTime() {
		return previousUpdateTime;
	}

	public void setPreviousUpdateTime(Calendar previousUpdateTime) {
		this.previousUpdateTime = previousUpdateTime;
	}

	public ArrayList<String> getSchemaURIs() {
		return schemaURIs;
	}

	public void setSchemaURIs(ArrayList<String> schemaURIs) {
		this.schemaURIs = schemaURIs;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (!super.equals(obj)) return false;
		
		final GCUBECollection other = (GCUBECollection) obj;
		
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
		
		if (isUserCollection != other.isUserCollection) return false;
		
		if (isMemberOf == null) {
			if (other.isMemberOf != null)
				return false;
		} else if (! isMemberOf.equals(other.isMemberOf))
			return false;
		
		if (LastModifier == null) {
			if (other.LastModifier != null)
				return false;
		} else if (! LastModifier.equals(other.LastModifier))
			return false;
		
		if (lastUpdateTime == null) {
			if (other.lastUpdateTime != null)
				return false;
		} else if (! lastUpdateTime.equals(other.lastUpdateTime))
			return false;
		
		if (numberOfMembers != other.numberOfMembers) return false;
		
		if (previousUpdateTime == null) {
			if (other.previousUpdateTime != null)
				return false;
		} else if (! previousUpdateTime.equals(other.previousUpdateTime))
			return false;
		
		if (schemaURIs == null) {
			if (other.schemaURIs != null)
				return false;
		} else if (! schemaURIs.equals(other.schemaURIs))
			return false;
		
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (! name.equals(other.name))
			return false;
		
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (! description.equals(other.description))
			return false;
		
		if (isVirtual != other.isVirtual) return false;
		
		
		return true;
	}

	
}


