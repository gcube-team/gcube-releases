/**
 * 
 */
package org.gcube.portlets.widgets.workspaceuploader.shared;

import java.io.Serializable;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Aug 3, 2015
 */
public class WorkspaceUploadFile implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1513046515926364747L;
	
	private String parentId;
	private String fileName;
	private String itemId;
	
	/**
	 * 
	 */
	public WorkspaceUploadFile() {
	}

	
	/**
	 * @param parentId
	 * @param fileName
	 */
	public WorkspaceUploadFile(String parentId, String itemId, String fileName) {
		super();
		this.parentId = parentId;
		this.itemId = itemId;
		this.fileName = fileName;
	}


	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}


	/**
	 * @return the parentId
	 */
	public String getParentId() {
		return parentId;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	
	
	/**
	 * @return the itemId
	 */
	public String getItemId() {
		return itemId;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 31;
		hash = hash * 13 + (parentId == null ? 0 : parentId.hashCode());
		hash = hash * 17 + (fileName == null ? 0 : fileName.hashCode());
		hash = hash * 19 + (itemId == null ? 0 : itemId.hashCode());
		return hash;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WorkspaceUploadFile [parentId=");
		builder.append(parentId);
		builder.append(", fileName=");
		builder.append(fileName);
		builder.append(", itemId=");
		builder.append(itemId);
		builder.append("]");
		return builder.toString();
	}
}
