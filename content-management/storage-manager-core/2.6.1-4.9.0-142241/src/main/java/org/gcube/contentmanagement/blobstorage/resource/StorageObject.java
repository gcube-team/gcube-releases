package org.gcube.contentmanagement.blobstorage.resource;


/**
 * Class that define a entity object (a file or a directory).
 * This entity, contains file properties and methods for the client queries 
 * This type of resource is builded by Transportmanager for answer the client
 * ex: if the customer asks for the contents of a remote folder. It will be returned a List of StorageObject
 * 
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class StorageObject {
	
	private String type;
	private String name;
	private String owner;
	private String creationTime;
	private String id;
	
	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public StorageObject(String name, String type){
		setType(type);
		setName(name);
	}
	
	public StorageObject(String name, String type, String owner, String creationTime){
		setType(type);
		setName(name);
		setOwner(owner);
		setCreationTime(creationTime);
	}
	
	public boolean isDirectory() {
		return type.equalsIgnoreCase("dir");
	}

	public boolean isFile() {
		return type.equalsIgnoreCase("file");
	}

	
	private void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setId(String id) {
	this.id=id;
		
	}

	public String getId(){
		return this.id;
	}
}
