package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class ShowUrlEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 29, 2016
 */
public class ShowUrlEvent extends GwtEvent<ShowUrlEventHandler> {
	public static Type<ShowUrlEventHandler> TYPE = new Type<ShowUrlEventHandler>();

	private FileModel sourceFileModel = null; //Url page

	/**
	 * Instantiates a new show url event.
	 *
	 * @param fileSourceModel the file source model
	 */
	public ShowUrlEvent(FileModel fileSourceModel) {
		this.sourceFileModel = fileSourceModel;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ShowUrlEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ShowUrlEventHandler handler) {
		handler.onClickUrl(this);

	}

	/**
	 * Gets the source file model.
	 *
	 * @return the source file model
	 */
	public FileModel getSourceFileModel() {
		return sourceFileModel;
	}
}
