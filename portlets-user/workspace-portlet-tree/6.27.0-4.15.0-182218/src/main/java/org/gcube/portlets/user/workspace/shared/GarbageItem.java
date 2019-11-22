/**
 * 
 */
package org.gcube.portlets.user.workspace.shared;

import java.io.Serializable;

/**
 * The Class GarbageItem.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it Oct 23, 2015
 * 
 * USED TO SEND NOTIFICATION AFTER DELETE
 */
public class GarbageItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1532030116595698658L;
	
	private boolean sourceItemIsShared;
	private String oldItemName;
	private String oldItemId;
	private String sourceFolderSharedId;

	private String error;

	public GarbageItem(){
		
	}
	/**
	 * Instantiates a new garbage item.
	 *
	 * @param sourceItemIsShared the source item is shared
	 * @param oldItemName the old item name
	 * @param oldItemId the old item id
	 * @param sourceFolderSharedId the source folder shared id
	 */
	public GarbageItem(boolean sourceItemIsShared, String oldItemName,
			String oldItemId, final String sourceFolderSharedId) {
		this.sourceItemIsShared = sourceItemIsShared;
		this.oldItemName = oldItemName;
		this.oldItemId = oldItemId;
		this.sourceFolderSharedId = sourceFolderSharedId;
	}

	/**
	 * Checks if is source item is shared.
	 *
	 * @return the sourceItemIsShared
	 */
	public boolean isSourceItemIsShared() {
		return sourceItemIsShared;
	}

	/**
	 * Gets the old item name.
	 *
	 * @return the oldItemName
	 */
	public String getOldItemName() {
		return oldItemName;
	}

	/**
	 * Gets the old item id.
	 *
	 * @return the oldItemId
	 */
	public String getOldItemId() {
		return oldItemId;
	}

	/**
	 * Gets the source folder shared id.
	 *
	 * @return the sourceFolderSharedId
	 */
	public String getSourceFolderSharedId() {
		return sourceFolderSharedId;
	}

	/**
	 * Sets the source item is shared.
	 *
	 * @param sourceItemIsShared the sourceItemIsShared to set
	 */
	public void setSourceItemIsShared(boolean sourceItemIsShared) {
		this.sourceItemIsShared = sourceItemIsShared;
	}

	/**
	 * Sets the old item name.
	 *
	 * @param oldItemName the oldItemName to set
	 */
	public void setOldItemName(String oldItemName) {
		this.oldItemName = oldItemName;
	}

	/**
	 * Sets the old item id.
	 *
	 * @param oldItemId the oldItemId to set
	 */
	public void setOldItemId(String oldItemId) {
		this.oldItemId = oldItemId;
	}

	/**
	 * Sets the source folder shared id.
	 *
	 * @param sourceFolderSharedId the sourceFolderSharedId to set
	 */
	public void setSourceFolderSharedId(String sourceFolderSharedId) {
		this.sourceFolderSharedId = sourceFolderSharedId;
	}
	
	public void setError(String error){
		this.error = error;
	}
	
	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GarbageItem [sourceItemIsShared=");
		builder.append(sourceItemIsShared);
		builder.append(", oldItemName=");
		builder.append(oldItemName);
		builder.append(", oldItemId=");
		builder.append(oldItemId);
		builder.append(", sourceFolderSharedId=");
		builder.append(sourceFolderSharedId);
		builder.append(", error=");
		builder.append(error);
		builder.append("]");
		return builder.toString();
	}
}
