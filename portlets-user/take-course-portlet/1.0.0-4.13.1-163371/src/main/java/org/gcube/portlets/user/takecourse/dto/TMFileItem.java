package org.gcube.portlets.user.takecourse.dto;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class TMFileItem implements Serializable {
	private String workspaceItemId;
	private String filename;
	private String ownerUserName;
	private String ownerPhotoURL;
	private Date lastUpdated;
	private ImageType type;
	private String fileDownLoadURL;
	private boolean read;
	
	public TMFileItem() {
	}

	public TMFileItem(String workspaceItemId, String filename, String ownerUserName, String ownerPhotoURL,
			Date lastUpdated, ImageType type, String fileDownLoadURL, boolean read) {
		super();
		this.workspaceItemId = workspaceItemId;
		this.filename = filename;
		this.ownerUserName = ownerUserName;
		this.ownerPhotoURL = ownerPhotoURL;
		this.lastUpdated = lastUpdated;
		this.type = type;
		this.fileDownLoadURL = fileDownLoadURL;
		this.read = read;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public String getWorkspaceItemId() {
		return workspaceItemId;
	}

	public void setWorkspaceItemId(String workspaceItemId) {
		this.workspaceItemId = workspaceItemId;
	}

	public String getFilename() {
		return filename;
	}

	public String getFileDownLoadURL() {
		return fileDownLoadURL;
	}

	public void setFileDownLoadURL(String fileDownLoadURL) {
		this.fileDownLoadURL = fileDownLoadURL;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getOwnerUserName() {
		return ownerUserName;
	}

	public void setOwnerUserName(String ownerUserName) {
		this.ownerUserName = ownerUserName;
	}

	public String getOwnerPhotoURL() {
		return ownerPhotoURL;
	}

	public void setOwnerPhotoURL(String ownerPhotoURL) {
		this.ownerPhotoURL = ownerPhotoURL;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public ImageType getType() {
		return type;
	}

	public void setType(ImageType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TMFileItem [workspaceItemId=");
		builder.append(workspaceItemId);
		builder.append(", filename=");
		builder.append(filename);
		builder.append(", ownerUserName=");
		builder.append(ownerUserName);
		builder.append(", ownerPhotoURL=");
		builder.append(ownerPhotoURL);
		builder.append(", lastUpdated=");
		builder.append(lastUpdated);
		builder.append(", type=");
		builder.append(type);
		builder.append(", fileDownLoadURL=");
		builder.append(fileDownLoadURL);
		builder.append("]");
		return builder.toString();
	}
	
	
}
