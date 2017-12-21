package org.gcube.contentmanager.storageserver.data;

public class OpLogRemoteObject {
	
	private String filename;
	private String type;
	private String name;
	private String owner;
	private String creationTime;
	private String lastAccess;
	private String lastOperation;
	private String lastUser;
	private int linkCount;
	private String delete;
	private String callerIp;
	private String id;
	private String dir;
	private long length;
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getType() {
		return type;
	}
	
	public long getLength() {
		return length;
	}
	
	public void setType(String type) {
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
	public String getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}
	public String getLastAccess() {
		return lastAccess;
	}
	public void setLastAccess(String lastAccess) {
		this.lastAccess = lastAccess;
	}
	public String getLastOperation() {
		return lastOperation;
	}
	public void setLastOperation(String lastOperation) {
		this.lastOperation = lastOperation;
	}
	public String getLastUser() {
		return lastUser;
	}
	public void setLastUser(String lastUser) {
		this.lastUser = lastUser;
	}
	public int getLinkCount() {
		return linkCount;
	}
	public void setLinkCount(int linkCount) {
		this.linkCount = linkCount;
	}
	public String getDelete() {
		return delete;
	}
	public void setDelete(String delete) {
		this.delete = delete;
	}
	public String getCallerIp() {
		return callerIp;
	}
	public void setCallerIp(String callerIp) {
		this.callerIp = callerIp;
	}
	public void setLength(long length) {
		this.length=length;
		
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	
	
	

}
