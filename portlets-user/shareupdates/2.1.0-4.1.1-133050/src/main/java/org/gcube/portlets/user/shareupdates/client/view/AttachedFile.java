package org.gcube.portlets.user.shareupdates.client.view;

/**
 * Attached file class.
 * @author Costantino Perciante at ISTI-CNR
 */
public class AttachedFile {
	
	// the name of the file
	private String fileName;
	
	// where it has been uploaded on the server (tmp directory of tomcat)
	private String fileAbsolutePathOnServer;
	
	// a description of the file (its content for a pdf, size for images)
	private String description;
	
	// when showing a file, this is the url that can be used for download
	private String downloadUrl; 
	
	// thumbnail url related to the type of file (pdf, png, jpg)
	private String thumbnailUrl;
	
	// format type (pdf, jpg ecc..)
	private String format;
	
	// object used to show on the client the attachment
	private AttachmentPreviewer atPrev;
	
	// has been it correctly uploaded on the server?
	private boolean correctlyUploaded;

	public AttachedFile(String fileName, String fileAbsolutePathOnServer,
			String description, String downloadUrl, String thumbnailUrl,
			String format, AttachmentPreviewer atPrev, boolean correctlyUploaded) {
		super();
		this.fileName = fileName;
		this.fileAbsolutePathOnServer = fileAbsolutePathOnServer;
		this.description = description;
		this.downloadUrl = downloadUrl;
		this.thumbnailUrl = thumbnailUrl;
		this.format = format;
		this.atPrev = atPrev;
		this.correctlyUploaded = correctlyUploaded;
	}

	/**
	 * Constructor used when the check uploaded file fails
	 * @param fileNameLabel
	 * @param absolutePathOnServer
	 * @param atPrev attachment previewer
	 * @param thumbnail url thumbnail
	 */
	public AttachedFile(String fileName, String fileAbsolutePathOnServer,
			AttachmentPreviewer atPrev, String thumbnailUrl) {
		this.fileName = fileName;
		this.fileAbsolutePathOnServer = fileAbsolutePathOnServer;
		this.atPrev = atPrev;
		this.thumbnailUrl = thumbnailUrl;
		this.correctlyUploaded = false;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileAbsolutePathOnServer() {
		return fileAbsolutePathOnServer;
	}

	public void setFileAbsolutePathOnServer(String fileAbsolutePathOnServer) {
		this.fileAbsolutePathOnServer = fileAbsolutePathOnServer;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public AttachmentPreviewer getAtPrev() {
		return atPrev;
	}

	public void setAtPrev(AttachmentPreviewer atPrev) {
		this.atPrev = atPrev;
	}

	public boolean isCorrectlyUploaded() {
		return correctlyUploaded;
	}

	public void setCorrectlyUploaded(boolean correctlyUploaded) {
		this.correctlyUploaded = correctlyUploaded;
	}
	@Override
	public String toString() {
		return "AttachedFile [fileNameLabel=" + fileName
				+ ", fileAbsolutePathOnServer=" + fileAbsolutePathOnServer
				+ ", description=" + description + ", downloadUrl="
				+ downloadUrl + ", thumbnailUrl=" + thumbnailUrl + ", mime="
				+ format + ", atPrev=" + atPrev + ", correctlyUploaded="
				+ correctlyUploaded + "]";
	}
}
