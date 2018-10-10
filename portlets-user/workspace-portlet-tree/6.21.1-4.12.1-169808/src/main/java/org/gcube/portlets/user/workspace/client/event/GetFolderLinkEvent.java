
package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class GetFolderLinkEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Sep 13, 2016
 */
public class GetFolderLinkEvent extends GwtEvent<GetFolderLinkEventHandler> {

	public static Type<GetFolderLinkEventHandler> TYPE =
		new Type<GetFolderLinkEventHandler>();
	private FileModel targetFile = null;
	private boolean setPublic;

	/**
	 * Instantiates a new gets the folder link event.
	 *
	 * @param target
	 *            the target
	 */
	public GetFolderLinkEvent(FileModel target, boolean setPublic) {

		this.targetFile = target;
		this.setPublic = setPublic;
	}


	/**
	 * @return the setPublic
	 */
	public boolean isSetPublic() {

		return setPublic;
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<GetFolderLinkEventHandler> getAssociatedType() {

		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared
	 * .EventHandler)
	 */
	@Override
	protected void dispatch(GetFolderLinkEventHandler handler) {

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
