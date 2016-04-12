package org.gcube.datatransfer.portlets.user.shared.obj;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.kfuntak.gwt.json.serialization.client.JsonSerializable;
import com.kfuntak.gwt.json.serialization.client.Serializer;


public class SchedulerObj implements JsonSerializable{
	//------------------------
	public String submitter;
	public TypeOfSchedule typeOfSchedule;
	public String typeOfTransfer;
	public String scope;

	public boolean unzipFile;
	public boolean overwrite;
	public boolean syncOp;

	public String agentHostname;

	//source
	public List<String> inputUrls;
	public String dataSourceId;
	public String sourceType; // Workspace, URI, DataSource

	//destination
	public String destinationFolder;
	public String dataStorageId;
	public String storageType ;
	
	//for StorageManagerDetails 
	public String accessType;
	public String owner;
	public String serviceClass;
	public String serviceName;
	
	//"dd.MM.yy-HH.mm"
	String submittedDate; 
	
	//authentication workspace
	String pass;

	public SchedulerObj(){
		this.submitter = "";
		this.typeOfTransfer = "";
		this.scope = "";
		this.agentHostname = "";
		this.inputUrls = new ArrayList<String>();
		this.dataSourceId = "";
		this.destinationFolder = "";
		this.storageType = "";
		this.accessType = "";
		this.owner = "";
		this.serviceClass = "";
		this.serviceName = "";
		this.typeOfSchedule = new TypeOfSchedule();
		this.unzipFile = false;
		this.overwrite = false;
		this.syncOp = false;	
		this.submittedDate="";
		this.pass="";
		this.dataStorageId="";
		this.sourceType="";
	}


	public String getPass() {
		return pass;
	}


	public void setPass(String pass) {
		this.pass = pass;
	}


	public TypeOfSchedule getTypeOfSchedule() {
		return typeOfSchedule;
	}
	public void setTypeOfSchedule(TypeOfSchedule typeOfSchedule) {
		this.typeOfSchedule = typeOfSchedule;
	}
	public String getTypeOfTransfer() {
		return typeOfTransfer;
	}
	public void setTypeOfTransfer(String typeOfTransfer) {
		this.typeOfTransfer = typeOfTransfer;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public boolean getUnzipFile() {
		return unzipFile;
	}
	public void setUnzipFile(boolean unzipFile) {
		this.unzipFile = unzipFile;
	}
	public boolean getOverwrite() {
		return overwrite;
	}
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}
	public boolean getSyncOp() {
		return syncOp;
	}
	public void setSyncOp(boolean syncOp) {
		this.syncOp = syncOp;
	}
	public String getAgentHostname() {
		return agentHostname;
	}
	public void setAgentHostname(String agentHostname) {
		this.agentHostname = agentHostname;
	}


	public List<String> getInputUrls() {
		return inputUrls;
	}

	public void setInputUrls(List<String> inputUrls) {
		this.inputUrls = inputUrls;
	}

	public String getDestinationFolder() {
		return destinationFolder;
	}

	public void setDestinationFolder(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}


	public String getStorageType() {
		return storageType;
	}


	public void setStorageType(String storageType) {
		this.storageType = storageType;
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


	public String getDataSourceId() {
		return dataSourceId;
	}


	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}


	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}
	public static Serializer createSerializer(){
		   return GWT.create(Serializer.class);
	 }

	public String getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(String submittedDate) {
		this.submittedDate = submittedDate;
	}


	public String getDataStorageId() {
		return dataStorageId;
	}


	public void setDataStorageId(String dataStorageId) {
		this.dataStorageId = dataStorageId;
	}


	public String getSourceType() {
		return sourceType;
	}


	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}


}
