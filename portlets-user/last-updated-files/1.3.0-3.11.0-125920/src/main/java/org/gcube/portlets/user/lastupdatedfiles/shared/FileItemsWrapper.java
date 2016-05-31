package org.gcube.portlets.user.lastupdatedfiles.shared;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class FileItemsWrapper implements Serializable {
	String folderName;
	String folderId;
	ArrayList<LufFileItem> items;
	boolean isInfrastructure;
	public FileItemsWrapper() {
		
	}
	public FileItemsWrapper(String folderName, String folderId,	ArrayList<LufFileItem> items, boolean isInfrastructure) {
		super();
		this.folderName = folderName;
		this.folderId = folderId;
		this.items = items;
		this.isInfrastructure = isInfrastructure;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public String getFolderId() {
		return folderId;
	}
	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}
	public ArrayList<LufFileItem> getItems() {
		return items;
	}
	public void setItems(ArrayList<LufFileItem> items) {
		this.items = items;
	}
	public boolean isInfrastructure() {
		return isInfrastructure;
	}
	public void setInfrastructure(boolean isInfrastructure) {
		this.isInfrastructure = isInfrastructure;
	}
	
	
}
