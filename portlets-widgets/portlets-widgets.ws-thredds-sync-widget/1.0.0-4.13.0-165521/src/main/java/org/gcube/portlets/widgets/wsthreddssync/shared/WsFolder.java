package org.gcube.portlets.widgets.wsthreddssync.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WsFolder implements Serializable, IsSerializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6165912778479858611L;
	private String folderId;
	private String foderName;

	
	public WsFolder() {
	}

	

	public WsFolder(String folderId, String foderName) {
		this.folderId = folderId;
		this.foderName = foderName;
	}



	public String getFolderId() {
		return folderId;
	}


	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}


	public String getFoderName() {
		return foderName;
	}


	public void setFoderName(String foderName) {
		this.foderName = foderName;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WsFolder [folderId=");
		builder.append(folderId);
		builder.append(", foderName=");
		builder.append(foderName);
		builder.append("]");
		return builder.toString();
	}



	
	
}
