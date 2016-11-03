package org.gcube.application.datamanagementfacilityportlet.servlet;

public class JSONResponse<E> {

	public int totalcount;
	public E[] data;
	
	public JSONResponse(E[] data) {
		this.data=data;
		this.totalcount=this.data.length;
	}
	
}
