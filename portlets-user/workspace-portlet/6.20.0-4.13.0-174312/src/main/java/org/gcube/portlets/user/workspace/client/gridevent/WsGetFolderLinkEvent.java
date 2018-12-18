package org.gcube.portlets.user.workspace.client.gridevent;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;



/**
 * The Class WsGetFolderLinkEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 13, 2016
 */
public class WsGetFolderLinkEvent extends GwtEvent<WsGetFolderLinkEventHandler> {
  public static Type<WsGetFolderLinkEventHandler> TYPE = new Type<WsGetFolderLinkEventHandler>();

  private FileModel targetFile = null;

	/**
	 * Instantiates a new gets the folder link event.
	 *
	 * @param target the target
	 */
	public WsGetFolderLinkEvent(FileModel target) {
		this.targetFile = target;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<WsGetFolderLinkEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(WsGetFolderLinkEventHandler handler) {
		handler.onGetFolderLink(this);

	}

	/**
	 * Gets the source file.
	 *
	 * @return the source file
	 */
	public FileModel getSourceFile() {
		return targetFile;
	}
}