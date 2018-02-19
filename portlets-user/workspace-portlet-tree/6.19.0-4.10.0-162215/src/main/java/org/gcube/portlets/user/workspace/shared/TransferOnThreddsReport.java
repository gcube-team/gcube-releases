/**
 *
 */
package org.gcube.portlets.user.workspace.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * The Class TransferOnThreddsReport.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 27, 2017
 */
public class TransferOnThreddsReport implements Serializable, IsSerializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -8593731354178551332L;
	private String transferId;
	private String folderId;
	private Boolean onError = false;
	private String reportMessage = null;
	private Boolean reportCreatedOnWorkspace = false;

	/**
	 * Instantiates a new transfer on thredds report.
	 */
	public TransferOnThreddsReport() {

	}

	/**
	 * Instantiates a new transfer on thredds report.
	 *
	 * @param transferId the transfer id
	 * @param folderId the folder id
	 * @param onError the on error
	 * @param reportMessage the report message
	 */
	public TransferOnThreddsReport(String transferId, String folderId, Boolean onError, String reportMessage) {

		this.transferId = transferId;
		this.folderId = folderId;
		this.onError = onError;
		this.reportMessage = reportMessage;
	}

	/**
	 * Sets the report created on workspace.
	 *
	 * @param reportCreatedOnWorkspace the reportCreated to set
	 */
	public void setReportCreatedOnWorkspace(Boolean reportCreatedOnWorkspace) {

		this.reportCreatedOnWorkspace = reportCreatedOnWorkspace;
	}


	/**
	 * Checks if is transferring report available.
	 *
	 * @return the boolean
	 */
	public Boolean isTransferringReportAvailable() {

		return reportCreatedOnWorkspace;
	}


	/**
	 * Gets the transfer id.
	 *
	 * @return the transferId
	 */
	public String getTransferId() {

		return transferId;
	}


	/**
	 * Gets the folder id.
	 *
	 * @return the folderId
	 */
	public String getFolderId() {

		return folderId;
	}


	/**
	 * Gets the on error.
	 *
	 * @return the onError
	 */
	public Boolean isOnError() {

		return onError;
	}


	/**
	 * Sets the transfer id.
	 *
	 * @param transferId the transferId to set
	 */
	public void setTransferId(String transferId) {

		this.transferId = transferId;
	}


	/**
	 * Sets the folder id.
	 *
	 * @param folderId the folderId to set
	 */
	public void setFolderId(String folderId) {

		this.folderId = folderId;
	}


	/**
	 * Sets the on error.
	 *
	 * @param onError the onError to set
	 */
	public void setOnError(Boolean onError) {

		this.onError = onError;
	}





	/**
	 * Gets the report message.
	 *
	 * @return the reportMessage
	 */
	public String getReportMessage() {

		return reportMessage;
	}


	/**
	 * Sets the report message.
	 *
	 * @param reportMessage the reportMessage to set
	 */
	public void setReportMessage(String reportMessage) {

		this.reportMessage = reportMessage;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("TransferOnThreddsReport [transferId=");
		builder.append(transferId);
		builder.append(", folderId=");
		builder.append(folderId);
		builder.append(", onError=");
		builder.append(onError);
		builder.append(", reportMessage=");
		builder.append(reportMessage);
		builder.append(", reportCreated=");
		builder.append(reportCreatedOnWorkspace);
		builder.append("]");
		return builder.toString();
	}






}
