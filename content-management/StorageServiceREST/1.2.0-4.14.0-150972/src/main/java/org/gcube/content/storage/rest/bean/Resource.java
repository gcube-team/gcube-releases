package org.gcube.content.storage.rest.bean;

public class Resource{
	
	int id;
	String resourceRemotePath;
	String resourceLocalPath;
	String owner;
	
	public Resource() {
		super();
	}
	public Resource(int i, String resourceLocalPath, String resourceRemotePath, String owner) {
		super();
		this.id = i;
		this.resourceLocalPath=resourceLocalPath;
		this.resourceRemotePath = resourceRemotePath;
		this.owner=owner;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getResourcePath() {
		return resourceRemotePath;
	}
	public String getResourceLocalPath() {
		return resourceLocalPath;
	}
	public void setResourceLocalPath(String resourceLocalPath) {
		this.resourceLocalPath = resourceLocalPath;
	}
	public void setResourceName(String resourcePath) {
		this.resourceRemotePath = resourcePath;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}	
	
}