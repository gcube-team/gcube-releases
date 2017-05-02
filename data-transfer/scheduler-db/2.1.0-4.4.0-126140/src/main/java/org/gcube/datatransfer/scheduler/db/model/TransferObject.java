package org.gcube.datatransfer.scheduler.db.model;


import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import java.net.URI;


@PersistenceCapable(table="TRANSFER_OBJECT")
public class TransferObject implements java.io.Serializable {


	private static final long serialVersionUID = 5795558129951975039L;

	@PrimaryKey	
	@Persistent(customValueStrategy="uuid")
	private String objectId;
	
	private String uriLink;
	private Long size;
	
	//in case of storing in a remote node
	private String destUri;
		
	public TransferObject(){
		this.uriLink=null;
		this.destUri=null;
	}
	//	private Transfer transfer;
	private String transferid;

	public String getTransferid() {
		return transferid;
	}
	public void setTransferid(String transferid) {
		this.transferid = transferid;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}


	public String getSrcURI() {
		return uriLink;
	}
	public void setSrcURI(String uriLink) {
		this.uriLink = uriLink;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public String getDestUri() {
		return destUri;
	}
	public void setDestUri(String destUri) {
		this.destUri = destUri;
	}
	
	
	

	


}
