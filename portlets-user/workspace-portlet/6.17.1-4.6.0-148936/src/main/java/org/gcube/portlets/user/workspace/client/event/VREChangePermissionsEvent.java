package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

public class VREChangePermissionsEvent extends GwtEvent<VREChangePermissionsEventHandler> {
	public static Type<VREChangePermissionsEventHandler> TYPE = new Type<VREChangePermissionsEventHandler>();
	
	private FileModel fileModel = null; //Folder source click
	
	/**
	 * @param fileModel
	 */
	public VREChangePermissionsEvent(FileModel fileModel) {
		this.fileModel = fileModel;
	}

	@Override
	public Type<VREChangePermissionsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(VREChangePermissionsEventHandler handler) {
		handler.onChangePermissionsOpen(this);
		
	}
	
	public FileModel getFileModel() {
		return fileModel;
	}

	public void setFileModel(FileModel fileModel) {
		this.fileModel = fileModel;
	}
}
