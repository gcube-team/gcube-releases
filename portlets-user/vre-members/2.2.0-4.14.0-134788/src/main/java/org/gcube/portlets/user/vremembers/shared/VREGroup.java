package org.gcube.portlets.user.vremembers.shared;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class VREGroup implements Serializable{
	private long id;
	private String name;
	private String parentName;
	private String description;
	private ArrayList<BelongingUser> users;
	public VREGroup() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public VREGroup(long id, String name, String parentName,
			String description, ArrayList<BelongingUser> users) {
		super();
		this.id = id;
		this.name = name;
		this.parentName = parentName;
		this.description = description;
		this.users = users;
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
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public ArrayList<BelongingUser> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<BelongingUser> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "VREGroup [id=" + id + ", name=" + name + ", parentName="
				+ parentName + ", description=" + description + "]";
	}
	
	
}
