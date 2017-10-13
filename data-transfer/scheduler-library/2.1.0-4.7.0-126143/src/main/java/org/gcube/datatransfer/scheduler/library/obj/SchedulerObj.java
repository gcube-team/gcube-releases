package org.gcube.datatransfer.scheduler.library.obj;


import java.io.Serializable;

import org.gcube.data.trees.patterns.Pattern;
import org.gcube.datatransfer.common.agent.Types.AnyHolder;
import org.gcube.datatransfer.common.agent.Types.StorageManagerDetails;
import org.gcube.datatransfer.common.agent.Types.storageType;


import com.thoughtworks.xstream.XStream;

public class SchedulerObj implements Serializable{
	private static final long serialVersionUID = 1L;
	//------------------------

	public TypeOfSchedule typeOfSchedule;
	public String typeOfTransfer;
	public String scope;

	public boolean unzipFile;
	public boolean overwrite;
	public boolean syncOp;

	public String agentHostname;
	public Pattern pattern; //tree case
	
	//source
	public String[] inputUrls;	
	public String dataSourceId;
	public String treeSourceID; //tree case

	//destination
	public String destinationFolder;
	public storageType typeOfStorage ;
	public StorageManagerDetails smDetails;  // when its about storageManager
	public String[] outputUrls; //when its about datastorage (RemoteNode)
	public String dataStorageId; 
	public String treeStorageID; //tree case

	//"dd.MM.yy-hh.mm"
	String submittedDate; 
	
	protected static XStream xstream = new XStream();

	public SchedulerObj(){
		
		this.typeOfSchedule=null;
		this.typeOfTransfer=null;
		this.scope=null;
		this.unzipFile=false;
		this.overwrite=false;
		this.syncOp=false;
		this.inputUrls=null;
		this.dataSourceId=null;
		this.destinationFolder=null;
		this.typeOfStorage=null;
		this.smDetails=null;
		this.submittedDate=null;
		this.outputUrls=null;
		this.dataStorageId=null;
		this.treeSourceID=null;
		this.treeStorageID=null;
		this.pattern=null;
	}

	public String toXML(){
		return xstream.toXML(this);
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
	public boolean isUnzipFile() {
		return unzipFile;
	}
	public void setUnzipFile(boolean unzipFile) {
		this.unzipFile = unzipFile;
	}
	public boolean isOverwrite() {
		return overwrite;
	}
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}
	public boolean isSyncOp() {
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

	

	public String[] getInputUrls() {
		return inputUrls;
	}

	public void setInputUrls(String[] inputUrls) {
		this.inputUrls = inputUrls;
	}

	public String getDataSourceId() {
		return dataSourceId;
	}
	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}


	public String getDestinationFolder() {
		return destinationFolder;
	}

	public void setDestinationFolder(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}

	
	
	public storageType getTypeOfStorage() {
		return typeOfStorage;
	}

	public void setTypeOfStorage(storageType typeOfStorage) {
		this.typeOfStorage = typeOfStorage;
	}

	public StorageManagerDetails getSmDetails() {
		return smDetails;
	}

	public void setSmDetails(StorageManagerDetails smDetails) {
		this.smDetails = smDetails;
	}

	public String getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(String submittedDate) {
		this.submittedDate = submittedDate;
	}

	public String[] getOutputUrls() {
		return outputUrls;
	}

	public void setOutputUrls(String[] outputUrls) {
		this.outputUrls = outputUrls;
	}

	public String getDataStorageId() {
		return dataStorageId;
	}

	public void setDataStorageId(String dataStorageId) {
		this.dataStorageId = dataStorageId;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public String getTreeSourceID() {
		return treeSourceID;
	}

	public String getTreeStorageID() {
		return treeStorageID;
	}

	public void setTreeSourceID(String treeSourceID) {
		this.treeSourceID = treeSourceID;
	}

	public void setTreeStorageID(String treeStorageID) {
		this.treeStorageID = treeStorageID;
	}
	
}
