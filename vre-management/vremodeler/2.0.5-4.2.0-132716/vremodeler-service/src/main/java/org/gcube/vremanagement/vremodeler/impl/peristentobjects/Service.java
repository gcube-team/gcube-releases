package org.gcube.vremanagement.vremodeler.impl.peristentobjects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Service {

	Service(){}
		
	@DatabaseField(id = true,width = 60)
	private String id;
	
	@DatabaseField(canBeNull=false)
	private String serviceClass;
	
	@DatabaseField(canBeNull=false)
	private String serviceName;
	
	@DatabaseField(canBeNull=false)
	private String version;
	
	@DatabaseField(canBeNull=false)
	private String packageName;

	@DatabaseField(canBeNull=false)
	private String packageVersion;
	
	public Service(String id , String serviceClass, String serviceName,
			String version, String packageName, String packageVersion) {
		super();
		this.id =id;
		this.serviceClass = serviceClass;
		this.serviceName = serviceName;
		this.version = version;
		this.packageName = packageName;
		this.packageVersion = packageVersion;
	}
	
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageVersion() {
		return packageVersion;
	}

	public void setPackageVersion(String packageVersion) {
		this.packageVersion = packageVersion;
	}
	
	
	
}
