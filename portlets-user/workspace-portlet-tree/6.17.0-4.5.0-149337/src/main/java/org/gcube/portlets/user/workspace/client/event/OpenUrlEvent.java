package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class OpenUrlEvent extends GwtEvent<OpenUrlEventHandler> {
	public static Type<OpenUrlEventHandler> TYPE = new Type<OpenUrlEventHandler>();
	
	private FileModel sourceFileModel = null; //Url page
	
	public OpenUrlEvent(FileModel fileSourceModel) {
		this.sourceFileModel = fileSourceModel;
	}

	@Override
	public Type<OpenUrlEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	protected void dispatch(OpenUrlEventHandler handler) {
		handler.onClickUrl(this);
		
	}

	public FileModel getSourceFileModel() {
		return sourceFileModel;
	}
}
