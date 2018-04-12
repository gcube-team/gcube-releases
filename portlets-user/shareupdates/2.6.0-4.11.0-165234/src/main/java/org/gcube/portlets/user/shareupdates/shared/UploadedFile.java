package org.gcube.portlets.user.shareupdates.shared;

import java.io.Serializable;

/**
 * Information of an already uploaded file.
 * @author Costantino Perciante at ISTI-CNR
 */
public class UploadedFile implements Serializable{

	/**
	 * Generated UUID
	 */
	private static final long serialVersionUID = 1690771870370846188L;

	// name of the file
	private String fileName;

	// path on the server of this file (tomcat tmp)
	private String fileAbsolutePathOnServer;

	// a description of the file (its content for a pdf, size for images)
	private String description;

	// when showing a file, this is the url that can be used for download
	private String downloadUrl; 

	// thumbnail url related to the type of file (pdf, png, jpg)
	private String thumbnailUrl;

	// mime type (pdf, jpg ecc..)
	private String format;

	public UploadedFile() { 
		super();
	}

	public UploadedFile(String fileName, String fileAbsolutePathOnServer,
			String description, String downloadUrl, String thumbnailUrl,
			String mime) {
		super();
		this.fileName = fileName;
		this.fileAbsolutePathOnServer = fileAbsolutePathOnServer;
		this.description = description;
		this.downloadUrl = downloadUrl;
		this.thumbnailUrl = thumbnailUrl;
		this.format = mime;
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

	public void setMime(String format) {
		this.format = format;
	}

	@Override
	public String toString() {
		return "UploadedFile [fileNameLabel=" + fileName
				+ ", fileAbsolutePathOnServer=" + fileAbsolutePathOnServer
				+ ", description=" + description + ", downloadUrl="
				+ downloadUrl + ", thumbnailUrl=" + thumbnailUrl + ", format="
				+ format + "]";
	}

}
