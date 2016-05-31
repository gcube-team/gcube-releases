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
//	private FileType fileType;
//	public enum FileType {TREE, ATTACH};
	
	public FileDownloadEvent(String itemIdentifier, String name, DownloadType downloadType) {
		this.itemIdentifier = itemIdentifier;
		this.downloadType = downloadType;
		this.itemName = name;
//		this.fileType = fileType;
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

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface#getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.FILE_DOWNLAD_EVENT;
	}
	
}
