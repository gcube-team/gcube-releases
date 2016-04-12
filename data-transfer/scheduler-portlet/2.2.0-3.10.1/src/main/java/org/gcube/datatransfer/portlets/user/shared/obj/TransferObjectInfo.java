package org.gcube.datatransfer.portlets.user.shared.obj;

import com.kfuntak.gwt.json.serialization.client.JsonSerializable;


public class TransferObjectInfo implements JsonSerializable{
	private String objectId;
	
	private String URI;
	private Long size;
		
	//	private Transfer transfer;
	private String transferid;

	
	public TransferObjectInfo() {
		this.objectId="";
		this.URI="";
		this.size=new Long(0);
		this.transferid="";
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getURI() {
		return URI;
	}
	public void setURI(String uRI) {
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
