package org.gcube.datatransfer.scheduler.library.outcome;

import java.net.URI;

public class TransferObjectInfo {
	private String objectId;
	
	private URI URI;
	private Long size;
		
	//	private Transfer transfer;
	private String transferid;

	
	public TransferObjectInfo() {
		this.objectId=null;
		this.URI=null;
		this.size=null;
		this.transferid=null;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public URI getURI() {
		return URI;
	}
	public void setURI(URI uRI) {
		URI = uRI;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public String getTransferid() {
		return transferid;
	}
	public void setTransferid(String transferid) {
		this.transferid = transferid;
	}
	
	
}
