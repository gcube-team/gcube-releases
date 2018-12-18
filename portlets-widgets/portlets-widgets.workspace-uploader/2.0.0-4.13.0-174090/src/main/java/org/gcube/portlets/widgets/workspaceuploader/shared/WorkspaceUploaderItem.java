/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.shared;

import java.io.Serializable;

/**
 * The Class WorkspaceUploaderItem.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Aug 3, 2015
 */
public class WorkspaceUploaderItem implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -7657531873615480050L;


	/**
	 * The Enum UPLOAD_STATUS.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Aug 6, 2015
	 */
	public static enum UPLOAD_STATUS{

		WAIT("WAIT", "wait"),
		IN_PROGRESS("IN_PROGRESS", "in progress"),
		FAILED("FAILED", "failed"),
		ABORTED("ABORTED", "aborted"),
		COMPLETED("COMPLETED", "completed");

		protected String id;
		protected String label;

		/**
		 * Instantiates a new upload status.
		 *
		 * @param id the id
		 * @param label the label
		 */
		private UPLOAD_STATUS(String id, String label) {
			this.id = id;
			this.label = label;
		}
	}

	private String identifier;
	private WorkspaceUploadFile file;
	private UPLOAD_STATUS uploadStatus;
	private String statusDescription;
	private String clientUploadKey;
	private UploadProgress uploadProgress;
	private Boolean isOverwrite;
//	private Long threadId;
	private Boolean erasable;

	/**
	 * Instantiates a new workspace uploader item.
	 */
	public WorkspaceUploaderItem() {
	}

	/**
	 * Instantiates a new workspace uploader item.
	 *
	 * @param identifier the identifier
	 */
	public WorkspaceUploaderItem(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Instantiates a new workspace uploader item.
	 *
	 * @param identifier the identifier
	 * @param file the file
	 * @param status the status
	 * @param statusDescription the status description
	 * @param uploadProgress the upload progress
	 */
	public WorkspaceUploaderItem(String identifier, WorkspaceUploadFile file, UPLOAD_STATUS status, String statusDescription, UploadProgress uploadProgress) {
		super();
		this.identifier = identifier;
		this.file = file;
		this.uploadStatus = status;
		this.statusDescription = statusDescription;
		this.uploadProgress = uploadProgress;
	}


	/**
	 * Gets the identifier.
	 *
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}


	/**
	 * Gets the status description.
	 *
	 * @return the statusDescription
	 */
	public String getStatusDescription() {
		return statusDescription;
	}


	/**
	 * Sets the status description.
	 *
	 * @param statusDescription the statusDescription to set
	 */
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}


	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public WorkspaceUploadFile getFile() {
		return file;
	}


	/**
	 * Gets the upload status.
	 *
	 * @return the uploadStatus
	 */
	public UPLOAD_STATUS getUploadStatus() {
		return uploadStatus;
	}

	/**
	 * Sets the file.
	 *
	 * @param file the file to set
	 */
	public void setFile(WorkspaceUploadFile file) {
		this.file = file;
	}


	/**
	 * Sets the upload status.
	 *
	 * @param uploadStatus the uploadStatus to set
	 */
	public void setUploadStatus(UPLOAD_STATUS uploadStatus) {
		this.uploadStatus = uploadStatus;
	}


	/**
	 * Gets the client upload key.
	 *
	 * @return the clientUploadKey
	 */
	public String getClientUploadKey() {
		return clientUploadKey;
	}


	/**
	 * Sets the client upload key.
	 *
	 * @param clientUploadKey the clientUploadKey to set
	 */
	public void setClientUploadKey(String clientUploadKey) {
		this.clientUploadKey = clientUploadKey;
	}

	/**
	 * Gets the upload progress.
	 *
	 * @return the uploadProgress
	 */
	public UploadProgress getUploadProgress() {
		return uploadProgress;
	}

	/**
	 * Sets the upload progress.
	 *
	 * @param uploadProgress the uploadProgress to set
	 */
	public void setUploadProgress(UploadProgress uploadProgress) {
		this.uploadProgress = uploadProgress;
	}

	/**
	 * @return the isOverwrite
	 */
	public Boolean getIsOverwrite() {
		return isOverwrite;
	}

	/**
	 * @param isOverwrite the isOverwrite to set
	 */
	public void setIsOverwrite(Boolean isOverwrite) {
		this.isOverwrite = isOverwrite;
	}

	/**
	 * Sets the erasable.
	 *
	 * @param b the new erasable
	 */
	public void setErasable(Boolean b) {
		this.erasable = b;
	}


	/**
	 * Checks if is erasable.
	 *
	 * @return the erasable
	 */
	public Boolean isErasable() {

		if(erasable==null)
			return false;

		return erasable;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("WorkspaceUploaderItem [identifier=");
		builder.append(identifier);
		builder.append(", file=");
		builder.append(file);
		builder.append(", uploadStatus=");
		builder.append(uploadStatus);
		builder.append(", statusDescription=");
		builder.append(statusDescription);
		builder.append(", clientUploadKey=");
		builder.append(clientUploadKey);
//		builder.append(", uploadProgress=");
//		builder.append(uploadProgress);
		builder.append(", isOverwrite=");
		builder.append(isOverwrite);
		builder.append(", erasable=");
		builder.append(erasable);
		builder.append("]");
		return builder.toString();
	}



}
