package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class SaveSmartFolderEvent extends GwtEvent<SaveSmartFolderEventHandler>{
	public static Type<SaveSmartFolderEventHandler> TYPE = new Type<SaveSmartFolderEventHandler>();

	private String smartFolderName;
	private String searchText;
	private String workpaceFolderId;
	
	public SaveSmartFolderEvent(String smartFolderName, String searchText, String workpaceFolderId) {

		this.smartFolderName = smartFolderName;
		this.searchText = searchText;
		this.workpaceFolderId = workpaceFolderId;
	}

	@Override
	public Type<SaveSmartFolderEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SaveSmartFolderEventHandler handler) {
		handler.onSaveSmartFolder(this);
		
	}

	public String getSmartFolderName() {
		return smartFolderName;
	}

	public String getSearchText() {
		return searchText;
	}

	public String getWorkpaceFolderId() {
		return workpaceFolderId;
	}
}
