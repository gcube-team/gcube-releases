package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * May 23, 2013
 * 
 */
public class EditUserPermissionEvent extends
		GwtEvent<EditUserPermissionEventHandler> {
	public static Type<EditUserPermissionEventHandler> TYPE = new Type<EditUserPermissionEventHandler>();
	private FileModel sourceFile;


	public EditUserPermissionEvent(FileModel sourceFileModel) {
		this.sourceFile= sourceFileModel;
	}

	@Override
	public Type<EditUserPermissionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EditUserPermissionEventHandler handler) {
		handler.onEditUserPermission(this);
	}

	public FileModel getSourceFolder() {
		return sourceFile;
	}

	public void setSourceFolder(FileModel sourceFile) {
		this.sourceFile = sourceFile;
	}
}
