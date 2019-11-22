
package org.gcube.common.workspacetaskexecutor.shared;

import java.io.Serializable;


/**
 * The Class WSItemObject.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 11, 2018
 */
public class WSItemObject implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3466278154835064930L;
	private String itemId;
	private String itemName;

	private String owner;
	private String publicLink;
	private boolean isFolder;


	/**
	 * Instantiates a new WS item object.
	 */
	public WSItemObject() {

	}


	/**
	 * Instantiates a new WS item object.
	 *
	 * @param itemId the item id
	 * @param itemName the item name
	 * @param owner the owner
	 * @param publicLink the public link
	 * @param isFolder the is folder
	 */
	public WSItemObject(
		String itemId, String itemName, String owner, String publicLink,
		boolean isFolder) {

		this.itemId = itemId;
		this.itemName = itemName;
		this.owner = owner;
		this.publicLink = publicLink;
		this.isFolder = isFolder;
	}



	/**
	 * Gets the item id.
	 *
	 * @return the itemId
	 */
	public String getItemId() {

		return itemId;
	}



	/**
	 * Gets the item name.
	 *
	 * @return the itemName
	 */
	public String getItemName() {

		return itemName;
	}



	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	public String getOwner() {

		return owner;
	}



	/**
	 * Gets the public link.
	 *
	 * @return the publicLink
	 */
	public String getPublicLink() {

		return publicLink;
	}



	/**
	 * Checks if is folder.
	 *
	 * @return the isFolder
	 */
	public boolean isFolder() {

		return isFolder;
	}



	/**
	 * Sets the item id.
	 *
	 * @param itemId the itemId to set
	 */
	public void setItemId(String itemId) {

		this.itemId = itemId;
	}



	/**
	 * Sets the item name.
	 *
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {

		this.itemName = itemName;
	}



	/**
	 * Sets the owner.
	 *
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {

		this.owner = owner;
	}



	/**
	 * Sets the public link.
	 *
	 * @param publicLink the publicLink to set
	 */
	public void setPublicLink(String publicLink) {

		this.publicLink = publicLink;
	}



	/**
	 * Sets the folder.
	 *
	 * @param isFolder the isFolder to set
	 */
	public void setFolder(boolean isFolder) {

		this.isFolder = isFolder;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("WSItemObject [itemId=");
		builder.append(itemId);
		builder.append(", itemName=");
		builder.append(itemName);
		builder.append(", owner=");
		builder.append(owner);
		builder.append(", publicLink=");
		builder.append(publicLink);
		builder.append(", isFolder=");
		builder.append(isFolder);
		builder.append("]");
		return builder.toString();
	}


}
