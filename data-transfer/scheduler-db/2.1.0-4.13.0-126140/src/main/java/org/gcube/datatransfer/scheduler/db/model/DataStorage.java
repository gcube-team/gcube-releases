package org.gcube.datatransfer.scheduler.db.model;


import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;



@PersistenceCapable(table="DATASTORAGE")
public class DataStorage implements java.io.Serializable  {

	private static final long serialVersionUID = -1174680910739216774L;

	@PrimaryKey	
	private String dataStorageId;
	
	private String type;      //storing in a "RemoteNode" or in "StorageManager"
	
	//if it's for remote node 
	private String dataStorageIdOfIS;
	private String dataStorageLink;
	private String dataStorageName;
	private Long freeSpace;
	private String endpoint;
	private String port;
	private String status;
	private String username;
	private String pass;
	private String description;
	
	//if it's for StorageManager
	public String accessType;
	public String owner;
	public String serviceClass;
	public String serviceName;


	public DataStorage() {
		super();
		this.type = null;
		this.dataStorageIdOfIS = null;
		this.dataStorageName = null;
		this.freeSpace = null;
		this.endpoint = null;
		this.status = null;
		this.username = null;
		this.pass = null;
		this.port=null;
		this.accessType = null;
		this.owner = null;
		this.serviceClass = null;
		this.serviceName = null;
	}

	
	public String getDataStorageId() {
		return dataStorageId;
	}


	public void setDataStorageId(String dataStorageId) {
		this.dataStorageId = dataStorageId;
	}


	public String getPort() {
		return port;
	}


	public void setPort(String port) {
		this.port = port;
	}


	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	


	public String getDataStorageIdOfIS() {
		return dataStorageIdOfIS;
	}


	public void setDataStorageIdOfIS(String dataStorageIdOfIS) {
		this.dataStorageIdOfIS = dataStorageIdOfIS;
	}


	public String getDataStorageName() {
		return dataStorageName;
	}


	public void setDataStorageName(String dataStorageName) {
		this.dataStorageName = dataStorageName;
	}


	public Long getFreeSpace() {
		return freeSpace;
	}


	public void setFreeSpace(Long freeSpace) {
		this.freeSpace = freeSpace;
	}






	public String getEndpoint() {
		return endpoint;
	}


	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}


	public String getPass() {
		return pass;
	}


	public void setPass(String pass) {
		this.pass = pass;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}




	public String getAccessType() {
		return accessType;
	}


	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}


	public String getOwner() {
		return owner;
	}


	public void setOwner(String owner) {
		this.owner = owner;
	}


	public String getServiceClass() {
		return serviceClass;
	}


	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}


	public String getServiceName() {
		return serviceName;
	}


	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}


	public String getDataStorageLink() {
		return dataStorageLink;
	}


	public void setDataStorageLink(String dataStorageLink) {
		this.dataStorageLink = dataStorageLink;
	}

}
