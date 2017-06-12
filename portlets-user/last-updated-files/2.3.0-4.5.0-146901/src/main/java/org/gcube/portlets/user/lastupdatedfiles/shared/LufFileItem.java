package org.gcube.portlets.user.lastupdatedfiles.shared;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class LufFileItem implements Serializable {
	private String workspaceItemId;
	private String filename;
	private String ownerUserName;
	private String ownerPhotoURL;
	private Date lastUpdated;
	private ImageType type;
	private String fileDownLoadURL;
	
	public LufFileItem() {
	}

	public LufFileItem(String workspaceItemId, String filename,
			String ownerUserName, String ownerPhotoURL, Date lastUpdated,
			ImageType type, String fileDownLoadURL) {
		super();
		this.workspaceItemId = workspaceItemId;
		this.filename = filename;
		this.ownerUserName = ownerUserName;
		this.ownerPhotoURL = ownerPhotoURL;
		this.lastUpdated = lastUpdated;
		this.type = type;
		this.fileDownLoadURL = fileDownLoadURL;
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
	
	
}
