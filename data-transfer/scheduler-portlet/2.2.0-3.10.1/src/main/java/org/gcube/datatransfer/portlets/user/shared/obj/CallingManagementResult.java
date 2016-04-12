package org.gcube.datatransfer.portlets.user.shared.obj;

import java.util.ArrayList;
import java.util.List;


import com.google.gwt.core.client.GWT;
import com.kfuntak.gwt.json.serialization.client.JsonSerializable;
import com.kfuntak.gwt.json.serialization.client.Serializer;

public class CallingManagementResult implements JsonSerializable{


	private List<String> errors;
	private String getAllTransfersInfoResult;
	//for calling the getTransfersInfo
	private List<TransferInfo> allTheTransfersInDB;
	private List<TransferObjectInfo> allTheTransferObjectsInDB;

	
	
	public CallingManagementResult(){
		this.errors=new ArrayList<String>();
		this.errors.add("");
		this.allTheTransferObjectsInDB=new ArrayList<TransferObjectInfo>();
		this.allTheTransfersInDB=new ArrayList<TransferInfo>();
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
	
	
	public static Serializer createSerializer(){
		   return GWT.create(Serializer.class);
	}
}
