package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class OpenReportsEvent extends GwtEvent<OpenReportsEventHandler> {
	public static Type<OpenReportsEventHandler> TYPE = new Type<OpenReportsEventHandler>();
	
	private FileModel sourceFileModel = null; //Report template
	
	public OpenReportsEvent(FileModel fileSourceModel) {
		this.sourceFileModel = fileSourceModel;
	}

	@Override
	public Type<OpenReportsEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	protected void dispatch(OpenReportsEventHandler handler) {
		handler.onClickOpenReports(this);
		
	}

	public FileModel getSourceFileModel() {
		return sourceFileModel;
	}
}
