package org.gcube.application.datamanagementfacilityportlet.client.rpc.data;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Response implements IsSerializable {

	private Boolean status=false;
	private HashMap<String,String> additionalObjects=new HashMap<String, String>();
	
	public Response() {}
	public Response(Boolean status) {
		super();
		this.status = status;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public void setAdditionalObjects(HashMap<String,String> additionalObjects) {
		this.additionalObjects = additionalObjects;
	}
	public HashMap<String,String> getAdditionalObjects() {
		return additionalObjects;
	}
	 
	
	
	
}
