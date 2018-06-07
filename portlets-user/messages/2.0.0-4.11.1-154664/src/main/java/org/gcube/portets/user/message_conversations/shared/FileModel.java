package org.gcube.portets.user.message_conversations.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FileModel implements IsSerializable{
	private String identifier, name;
	private String parentId;
	boolean isDirectory;
	private String downloadLink;
	
	public FileModel() {
		super();
	}

	
	
	public FileModel(String identifier, String name, String parentId, boolean isDirectory, String downloadLink) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.parentId = parentId;
		this.isDirectory = isDirectory;
		this.downloadLink = downloadLink;
	}



	public FileModel(String identifier, String name, String parentId, boolean isDirectory) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.parentId = parentId;
		this.isDirectory = isDirectory;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentId() {
		return parentId;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	public String getDownloadLink() {
		return downloadLink;
	}


	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@Override
	public String toString() {
		return "FileModel [identifier=" + identifier + ", name=" + name + ", parentId=" + parentId + ", isDirectory="
				+ isDirectory + ", downloadLink=" + downloadLink + "]";
	}

	
}
