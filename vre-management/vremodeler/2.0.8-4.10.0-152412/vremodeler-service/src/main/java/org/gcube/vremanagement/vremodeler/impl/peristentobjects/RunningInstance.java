package org.gcube.vremanagement.vremodeler.impl.peristentobjects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class RunningInstance {
	
		
	RunningInstance(){}
		
	public RunningInstance(String id, String serviceClass, String serviceName) {
		super();
		this.id = id;
		this.serviceClass = serviceClass;
		this.serviceName = serviceName;
	}

	@DatabaseField(id = true,width = 60, columnName="id")
	private String id;
	
	@DatabaseField(canBeNull=false)
	private String serviceClass;
	
	@DatabaseField(canBeNull=false)
	private String serviceName;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName="ghn_id")
	private Ghn ghn;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Ghn getGhn() {
		return ghn;
	}

	public void setGhn(Ghn ghn) {
		this.ghn = ghn;
	}
	
	
	
}
