package org.gcube.datatransfer.scheduler.library.outcome;

import java.io.Serializable;
import java.util.List;


import com.thoughtworks.xstream.XStream;

public class CallingManagementResult implements Serializable{
	private static final long serialVersionUID = 1L;
	protected static XStream xstream = new XStream();

	private List<String> errors;
	
	//for calling the getTransfersInfo
	private List<TransferInfo> allTheTransfersInDB;
	private List<TransferObjectInfo> allTheTransferObjectsInDB;
	private String getAllTransfersInfoResult;

	
	public CallingManagementResult(){
		this.errors=null;
		this.allTheTransferObjectsInDB=null;
		this.allTheTransfersInDB=null;
		this.setGetAllTransfersInfoResult(null);
	}
	
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public String getGetAllTransfersInfoResult() {
		return getAllTransfersInfoResult;
	}
	public void setGetAllTransfersInfoResult(String getAllTransfersInfoResult) {
		this.getAllTransfersInfoResult = getAllTransfersInfoResult;
	}
	
	public List<TransferInfo> getAllTheTransfersInDB() {
		return allTheTransfersInDB;
	}
	public void setAllTheTransfersInDB(List<TransferInfo> allTheTransfersInDB) {
		this.allTheTransfersInDB = allTheTransfersInDB;
	}
	public List<TransferObjectInfo> getAllTheTransferObjectsInDB() {
		return allTheTransferObjectsInDB;
	}
	public void setAllTheTransferObjectsInDB(
			List<TransferObjectInfo> allTheTransferObjectsInDB) {
		this.allTheTransferObjectsInDB = allTheTransferObjectsInDB;
	}


	public String toXML(){
		return xstream.toXML(this);
	}
}
