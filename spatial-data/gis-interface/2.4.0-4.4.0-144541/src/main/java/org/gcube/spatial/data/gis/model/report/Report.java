package org.gcube.spatial.data.gis.model.report;

import java.util.ArrayList;

public class Report {

	public static enum OperationState{
		COMPLETE,ERROR,WARN
	}

	private Report.OperationState dataOperationResult = Report.OperationState.ERROR;
	private Report.OperationState metaOperationResult = Report.OperationState.ERROR;
	private ArrayList<String> dataOperationMessages = new ArrayList<String>();
	private ArrayList<String> metaOperationMessages = new ArrayList<String>();
	/**
	 * @return the dataOperationResult
	 */
	public Report.OperationState getDataOperationResult() {
		return dataOperationResult;
	}
	/**
	 * @param dataOperationResult the dataOperationResult to set
	 */
	public void setDataOperationResult(Report.OperationState dataOperationResult) {
		this.dataOperationResult = dataOperationResult;
	}
	/**
	 * @return the metaOperationResult
	 */
	public Report.OperationState getMetaOperationResult() {
		return metaOperationResult;
	}
	/**
	 * @param metaOperationResult the metaOperationResult to set
	 */
	public void setMetaOperationResult(Report.OperationState metaOperationResult) {
		this.metaOperationResult = metaOperationResult;
	}
	/**
	 * @return the dataOperationMessages
	 */
	public ArrayList<String> getDataOperationMessages() {
		return dataOperationMessages;
	}
	/**
	 * @param dataOperationMessages the dataOperationMessages to set
	 */
	public void setDataOperationMessages(ArrayList<String> dataOperationMessages) {
		this.dataOperationMessages = dataOperationMessages;
	}
	/**
	 * @return the metaOperationMessages
	 */
	public ArrayList<String> getMetaOperationMessages() {
		return metaOperationMessages;
	}
	/**
	 * @param metaOperationMessages the metaOperationMessages to set
	 */
	public void setMetaOperationMessages(ArrayList<String> metaOperationMessages) {
		this.metaOperationMessages = metaOperationMessages;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Report [dataOperationResult=");
		builder.append(dataOperationResult);
		builder.append(", metaOperationResult=");
		builder.append(metaOperationResult);
		builder.append(", dataOperationMessages=");
		builder.append(dataOperationMessages);
		builder.append(", metaOperationMessages=");
		builder.append(metaOperationMessages);
		builder.append("]");
		return builder.toString();
	}

	

}
