package org.gcube.datatransfer.scheduler.db.model;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table="TRANSFER")
public class Transfer implements java.io.Serializable {


	private static final long serialVersionUID = -3008824308899830764L;

	@PrimaryKey	
	private String transferId;

	//type of schedule
	protected String typeOfScheduleId;

	protected String submitter;
	protected String status;
	protected String scope;
	protected String agentId;	
	protected String agentHostname;

	//if the type is LocalFileBased then there is no need for having DataSource and DataStorages
	// in other case we check at least for data source
	protected String transferType;
	protected String typeOfStorage;

	protected String destinationFolder;
	protected boolean overwrite;
	protected boolean unzipFile;

	//tree case---------
	protected String sourceId;
	protected String storageId;
	protected String pattern; //serialized
	protected String[]  treeOutcomes; //ids...
	//------------------

	protected String[] outcomes; //ids...
	protected int num_updates;

	//if Completed
	private String[] objectTrasferredIDs;
	private String[] objectFailedIDs;

	//if failed 
	public String[]  transferError;
	public String transferIdOfAgent;

	public long startTime;
	public long totalTime;

	public long total_size;
	public long bytes_have_been_transferred;

	//"dd.MM.yy-HH.mm.ss"
	String submittedDate; 

	//flag for knowing when the objs are stored at DB or not 
	public boolean readyObjects;

	public Transfer (){
		this.submitter=null;
		this.sourceId=null;
		this.storageId=null;
		this.agentId=null;
		this.typeOfScheduleId=null;
		this.status=null;
		this.objectTrasferredIDs=null;
		this.objectFailedIDs= null;
		this.transferError=null;
		this.transferIdOfAgent=null;
		this.scope=null;
		this.transferType=null;
		this.destinationFolder=null;
		this.overwrite=false;
		this.unzipFile=false;
		this.submittedDate=null;
		this.readyObjects=false;
		this.outcomes=null;
		this.agentHostname=null;
		this.startTime=-1;
		this.totalTime=-1;
		this.bytes_have_been_transferred=0;
		this.total_size=0;
		this.pattern=null;
		this.treeOutcomes=null;
		this.num_updates=0;
	}



	public int getNum_updates() {
		return num_updates;
	}



	public void setNum_updates(int num_updates) {
		this.num_updates = num_updates;
	}



	public String[] getOutcomes() {
		return outcomes;
	}

	//file-based
	public void setOutcomes(String[] outcomes) {
		this.num_updates++;
		this.outcomes=outcomes;
	}


	public String getTransferIdOfAgent() {
		return transferIdOfAgent;
	}
	public void setTransferIdOfAgent(String transferIdOfAgent) {
		this.transferIdOfAgent = transferIdOfAgent;
	}

	public String getTypeOfScheduleId() {
		return typeOfScheduleId;
	}
	public void setTypeOfScheduleId(String typeOfScheduleId) {
		this.typeOfScheduleId = typeOfScheduleId;
	}


	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getStorageId() {
		return storageId;
	}

	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}

	public String getTransferId() {
		return transferId;
	}
	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}
	public String getSubmitter() {
		return submitter;
	}
	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}


	public String[] getObjectTrasferredIDs() {
		return objectTrasferredIDs;
	}
	public void setObjectTrasferredIDs(String[] objectTrasferredIDs) {
		this.objectTrasferredIDs = objectTrasferredIDs;
	}


	public String[] getObjectFailedIDs() {
		return objectFailedIDs;
	}
	public void setObjectFailedIDs(String[] objectFailedIDs) {
		this.objectFailedIDs = objectFailedIDs;
	}

	public String[] getTransferError() {
		return transferError;
	}
	public void setTransferError(String[] transferError) {
		this.transferError = transferError;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}


	public String getTransferType() {
		return transferType;
	}


	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}


	public String getDestinationFolder() {
		return destinationFolder;
	}
	public void setDestinationFolder(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}

	public boolean isOverwrite() {
		return overwrite;
	}
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public boolean isUnzipFile() {
		return unzipFile;
	}
	public void setUnzipFile(boolean unzipFile) {
		this.unzipFile = unzipFile;
	}


	public String getSubmittedDate() {
		return submittedDate;
	}


	public void setSubmittedDate(String submittedDate) {
		this.submittedDate = submittedDate;
	}


	public boolean isReadyObjects() {
		return readyObjects;
	}
	public void setReadyObjects(boolean readyObjects) {
		this.readyObjects = readyObjects;
	}


	public String getTypeOfStorage() {
		return typeOfStorage;
	}


	public void setTypeOfStorage(String typeOfStorage) {
		this.typeOfStorage = typeOfStorage;
	}


	public long getStartTime() {
		return startTime;
	}


	public long getTotalTime() {
		return totalTime;
	}


	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}


	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}


	public long getTotal_size() {
		return total_size;
	}


	public long getBytes_have_been_transferred() {
		return bytes_have_been_transferred;
	}
	public void setTotal_size(long total_size) {
		this.total_size = total_size;
	}
	public void setBytes_have_been_transferred(long bytes_have_been_transferred) {
		this.bytes_have_been_transferred = bytes_have_been_transferred;
	}
	public String getAgentHostname() {
		return agentHostname;
	}
	public void setAgentHostname(String agentHostname) {
		this.agentHostname = agentHostname;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String[] getTreeOutcomes() {
		return treeOutcomes;
	}

	//tree-based
	public void setTreeOutcomes(String[] treeOutcomes) {
		this.num_updates++;
		this.treeOutcomes=treeOutcomes;
	}
	
	//in periodically case
	public void resetProgress(){
		//this.total_size=0;
		this.bytes_have_been_transferred=0;
	}

}
