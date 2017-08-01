package org.gcube.portlets.user.workspace.client.event;


import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;

import com.google.gwt.event.shared.GwtEvent;

public class FileDownloadEvent extends GwtEvent<FileDownloadEventHandler> implements GuiEventInterface {
	public static Type<FileDownloadEventHandler> TYPE = new Type<FileDownloadEventHandler>();
	public enum DownloadType {SHOW, DOWNLOAD};
	private String itemIdentifier = null;
	private DownloadType downloadType;
	private String itemName;
	private boolean isFolder;
	private String versionId;


	/**
	 * Instantiates a new file download event.
	 *
	 * @param itemIdentifier the item identifier
	 * @param name the name
	 * @param downloadType the download type
	 * @param isFolder the is folder
	 * @param versionId the version id related to older version
	 */
	public FileDownloadEvent(String itemIdentifier, String name, DownloadType downloadType, boolean isFolder, String versionId) {
		this.itemIdentifier = itemIdentifier;
		this.downloadType = downloadType;
		this.itemName = name;
		this.isFolder = isFolder;
		this.versionId = versionId;
	}

	@Override
	public Type<FileDownloadEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(FileDownloadEventHandler handler) {
		handler.onFileDownloadEvent(this);
	}


	public String getDownloadTypeToString() {
		return downloadType.toString();
	}

	public DownloadType getDownloadType() {
		return downloadType;
	}

	public String getItemIdentifier() {
		return itemIdentifier;
	}

	public String getItemName() {
		return itemName;
	}

	public boolean isFolder() {
		return isFolder;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface#getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.FILE_DOWNLAD_EVENT;
	}


	/**
	 * @return the versionId
	 */
	public String getVersionId() {

		return versionId;
	}

}
