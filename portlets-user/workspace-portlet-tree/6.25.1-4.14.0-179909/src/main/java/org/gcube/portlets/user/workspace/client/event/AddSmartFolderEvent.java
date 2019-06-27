package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class AddSmartFolderEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Oct 9, 2015
 */
public class AddSmartFolderEvent extends GwtEvent<AddSmartFolderEventHandler>{
	public static Type<AddSmartFolderEventHandler> TYPE = new Type<AddSmartFolderEventHandler>();

	private String smartFolderName;
	private String searchText;
	private String workpaceFolderId;
	private String description;
	private String parentId;
	
	/**
	 * @return the parentId
	 */
	public String getParentId() {
		return parentId;
	}

	/**
	 * Instantiates a new adds the smart folder event.
	 *
	 * @param searchText the search text
	 * @param parentId the parent id
	 */
	public AddSmartFolderEvent(String searchText, String parentId) {
		this.searchText = searchText;
		this.parentId = parentId;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<AddSmartFolderEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(AddSmartFolderEventHandler handler) {
		handler.onSaveSmartFolder(this);
		
	}

	/**
	 * Gets the smart folder name.
	 *
	 * @return the smart folder name
	 */
	public String getSmartFolderName() {
		return smartFolderName;
	}

	/**
	 * Gets the search text.
	 *
	 * @return the search text
	 */
	public String getSearchText() {
		return searchText;
	}

	/**
	 * Gets the workpace folder id.
	 *
	 * @return the workpace folder id
	 */
	public String getWorkpaceFolderId() {
		return workpaceFolderId;
	}

	
	/**
	 * Sets the smart folder name.
	 *
	 * @param smartFolderName the new smart folder name
	 */
	public void setSmartFolderName(String smartFolderName) {
		this.smartFolderName = smartFolderName;
	}

	/**
	 * Sets the workpace folder id.
	 *
	 * @param workpaceFolderId the new workpace folder id
	 */
	public void setWorkpaceFolderId(String workpaceFolderId) {
		this.workpaceFolderId = workpaceFolderId;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
