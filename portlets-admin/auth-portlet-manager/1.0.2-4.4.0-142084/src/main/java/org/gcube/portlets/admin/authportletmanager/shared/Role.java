package org.gcube.portlets.admin.authportletmanager.shared;

import java.io.Serializable;

public class Role implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String identifier;
	
	public Role() {
	
	}
	public Role(String name, String identifier) {
		super();
		this.name = name;
		this.identifier = identifier;
		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	
}
