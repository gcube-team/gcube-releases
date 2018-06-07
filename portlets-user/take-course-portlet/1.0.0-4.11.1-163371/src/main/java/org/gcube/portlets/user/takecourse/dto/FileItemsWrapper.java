package org.gcube.portlets.user.takecourse.dto;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class FileItemsWrapper implements Serializable {
	
	private String folderName;
	private String folderId;
	private ArrayList<TMFileItem> items;

	public FileItemsWrapper() {
		
	}
	public FileItemsWrapper(String folderName, String folderId,	ArrayList<TMFileItem> items) {
		super();
		this.folderName = folderName;
		this.folderId = folderId;
		this.items = items;

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
	public ArrayList<TMFileItem> getItems() {
		return items;
	}
	public void setItems(ArrayList<TMFileItem> items) {
		this.items = items;
	}
	@Override
	public String toString() {
		return "FileItemsWrapper [folderName=" + folderName + ", folderId=" + folderId + ", items=" + items + "]";
	}
	
	
}
