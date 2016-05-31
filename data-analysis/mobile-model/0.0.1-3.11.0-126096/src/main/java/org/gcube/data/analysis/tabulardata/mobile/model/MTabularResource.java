package org.gcube.data.analysis.tabulardata.mobile.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.mobile.service.MTabularResourceType;

public class MTabularResource {
	
	private long id;
				
	private List<String> sharedWithUser = new ArrayList<String>();
	private List<String> sharedWithGroup = new ArrayList<String>();
	
	private Map<String, String> properties = new HashMap<String, String>();
	
	private Calendar creationDate;
	
	private String name;
	
	private String owner;
	
	private MTabularResourceType type;
	
	@SuppressWarnings("unused")
	private MTabularResource(){}
	
	public MTabularResource(long id, Calendar creationDate, String name,
			String owner, MTabularResourceType type) {
		super();
		this.id = id;
		this.creationDate = creationDate;
		this.name = name;
		this.owner = owner;
		this.type = type;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
		
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the properties
	 */
	public Map<String, String> getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public List<String> getSharedWithUsers() {
		return sharedWithUser;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.tabular.TabularResource#getSharedWithGroup()
	 */
	public List<String> getSharedWithGroup() {
		return sharedWithGroup;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}
	
		
	/**
	 * @return the sharedWithUser
	 */
	public List<String> getSharedWithUser() {
		return sharedWithUser;
	}

	/**
	 * @param sharedWithUser the sharedWithUser to set
	 */
	public void setSharedWithUser(List<String> sharedWithUser) {
		this.sharedWithUser = sharedWithUser;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param sharedWithGroup the sharedWithGroup to set
	 */
	public void setSharedWithGroup(List<String> sharedWithGroup) {
		this.sharedWithGroup = sharedWithGroup;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(MTabularResourceType type) {
		this.type = type;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public MTabularResourceType getType() {
		return type;
	}
	
	
	
}
