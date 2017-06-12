package org.gcube.application.datamanagementfacilityportlet.client.rpc.data;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientAnalysisStatus;

import com.google.gwt.user.client.rpc.IsSerializable;

public class LiveQueryDescriptor implements IsSerializable{

	private String currentQuery;
	private ClientAnalysisStatus status=ClientAnalysisStatus.Pending;
	private String error;
	private List<String> fields=new ArrayList<String>();
	private long count;
	
	public LiveQueryDescriptor() {
		// TODO Auto-generated constructor stub
	}

	public LiveQueryDescriptor(String currentQuery,
			ClientAnalysisStatus status, String error, List<String> fields,
			long count) {
		super();
		this.currentQuery = currentQuery;
		this.status = status;
		this.error = error;
		this.fields = fields;
		this.count = count;
	}

	/**
	 * @return the currentQuery
	 */
	public String getCurrentQuery() {
		return currentQuery;
	}

	/**
	 * @param currentQuery the currentQuery to set
	 */
	public void setCurrentQuery(String currentQuery) {
		this.currentQuery = currentQuery;
	}

	/**
	 * @return the status
	 */
	public ClientAnalysisStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ClientAnalysisStatus status) {
		this.status = status;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	/**
	 * @return the fields
	 */
	public List<String> getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	/**
	 * @return the count
	 */
	public long getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(long count) {
		this.count = count;
	}
	
}
