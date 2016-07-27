package org.gcube.portlets.user.workspace.client.model;

import java.io.Serializable;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class BulkCreatorModel extends BaseModelData implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
//	public enum DownloadState implements Serializable {
//		ONGOING, FAILED, COMPLETED
//	}
	
	public static int ONGOING = 0;
	public static int FAILED = 2;
	public static int COMPLETED = 1;

	public BulkCreatorModel(){
	}

	public BulkCreatorModel(String identifier, String name, float percentage) {
		setIdentifier(identifier);
		setName(name);
		setPercentage(percentage);
	}
	
	/**
	 * state values:  ONGOING = 0; FAILED = -1; COMPLETED = 1;
	 * 
	 * @param identifier
	 * @param name
	 * @param percentage
	 * @param state
	 */
	public BulkCreatorModel(String identifier, String name, float percentage, int state, int requestsNumber, int failuresNumber) {
		this(identifier,name,percentage);
		setState(state);
		setRequestsNumber(requestsNumber);
		setFailuersNumber(failuresNumber);
		
	}

	private void setState(int state) {
		set(ConstantsExplorer.DOWNLOADSTATE, state);
	}
	
	private void setRequestsNumber(int reqs) {
		set(ConstantsExplorer.NUMREQUESTS, reqs);
	}
	
	private void setFailuersNumber(int fails) {
		set(ConstantsExplorer.NUMFAILS, fails);
	}

	public void setIdentifier(String identifier) {
		set(ConstantsExplorer.IDENTIFIER, identifier);	
	}
	
	public String getIdentifier(){
		return get(ConstantsExplorer.IDENTIFIER);
	}
	
	private void setName(String name) {
		set(ConstantsExplorer.NAME, name);
	}
	
	public String getName() {
		return get(ConstantsExplorer.NAME);
	}

	public void setPercentage(float percentage){
		set(ConstantsExplorer.STATUS, percentage);
	}
	
	public float getPercentage(){
		return (Float)get(ConstantsExplorer.STATUS);
	}
	
	public int getState(){
		return (Integer)get(ConstantsExplorer.DOWNLOADSTATE);
	}
	
	public int getNumRequests(){
		return (Integer)get(ConstantsExplorer.NUMREQUESTS);
	}
	
	public int getNumFails(){
		return (Integer)get(ConstantsExplorer.NUMFAILS);
	}
	
}
