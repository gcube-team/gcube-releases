package org.gcube.portlets.user.workspace.client.gridevent;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class LoadBreadcrumbEvent extends GwtEvent<LoadBreadcrumbEventHandler> {
	public static Type<LoadBreadcrumbEventHandler> TYPE = new Type<LoadBreadcrumbEventHandler>();

	private FileModel fileModel;


	public FileModel getFileModel() {
		return fileModel;
	}

	public LoadBreadcrumbEvent(FileModel item, boolean addAsLastParent) {
		this.fileModel = item;
	}

	@Override
	public Type<LoadBreadcrumbEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadBreadcrumbEventHandler handler) {
		handler.loadBreadcrumb(this);

	}

}