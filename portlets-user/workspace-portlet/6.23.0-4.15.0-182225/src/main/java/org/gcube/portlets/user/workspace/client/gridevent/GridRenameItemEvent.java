package org.gcube.portlets.user.workspace.client.gridevent;

import org.gcube.portlets.user.workspace.client.model.FileGridModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class GridRenameItemEvent extends GwtEvent<GridRenameItemEventHandler> {
	public static Type<GridRenameItemEventHandler> TYPE = new Type<GridRenameItemEventHandler>();

	private FileGridModel fileGridModel = null;
	private String newName;
	private String extension;

	
	public GridRenameItemEvent(FileGridModel fileGridModel, String newName, String extension) {
		this.fileGridModel = fileGridModel;
		this.newName = newName;
		this.extension = extension;
	}

	@Override
	public Type<GridRenameItemEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GridRenameItemEventHandler handler) {
		handler.onGridItemRename(this);
	}

	public FileGridModel getFileGridModel() {
		return fileGridModel;
	}

	public String getNewName() {
		return newName;
	}

	public String getExtension() {
		return extension;
	}
}