package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class RenameItemEvent extends GwtEvent<RenameItemEventHandler> implements GuiEventInterface {

	public static Type<RenameItemEventHandler> TYPE = new Type<RenameItemEventHandler>();
	private FileModel fileTarget = null;
	private String newName;
	private String extension;

	public RenameItemEvent(FileModel target) {
		this.fileTarget = target;
	}
	
	@Override
	public Type<RenameItemEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RenameItemEventHandler handler) {
		handler.onRenameItem(this);
	}

	public EventsTypeEnum getKey() {
		return EventsTypeEnum.RENAME_ITEM_EVENT;
	}

	public FileModel getFileTarget() {
		return fileTarget;
	}

	public void setNewName(String newName) {
		this.newName = newName;
		
	}

	public void setExtension(String extension) {
		this.extension = extension;
		
	}

	public String getNewName() {
		return newName;
	}

	public String getExtension() {
		return extension;
	}
}