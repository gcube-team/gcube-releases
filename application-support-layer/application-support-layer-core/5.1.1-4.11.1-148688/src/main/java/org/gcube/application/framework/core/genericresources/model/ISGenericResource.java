package org.gcube.application.framework.core.genericresources.model;

import java.io.Serializable;


/**
 * @author Valia Tsaqgkalidou (NKUA)
 *
 */
public class ISGenericResource implements Serializable {

	protected String id;
	protected String name;
	protected String description;
	protected String body;
	protected String secondaryType;
	
	
	/**
	 * @return the secondary type of the generic resource
	 */
	public String getSecondaryType() {
		return secondaryType;
	}


	/**
	 * @param secondaryType the secondary type of the generic resource to be set
	 */
	public void setSecondaryType(String secondaryType) {
		this.secondaryType = secondaryType;
	}


	/**
	 * Generic Constructor
	 */
	public ISGenericResource() {
		super();
		this.id = "";
		this.name = "";
		this.description = "";
		this.body = "";
		this.secondaryType = "";
	}
	
	
	/**
	 * @param id the generic resource ID
	 * @param name the generic resource name
	 * @param description the generic resource description
	 * @param body the generic resource body
	 * @param sType the generic resource secondary type
	 */
	public ISGenericResource(String id, String name, String description,
			String body, String sType) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.body = body;
		this.secondaryType = sType;
	}


	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the body
	 */
	public String getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}
	
	
}
