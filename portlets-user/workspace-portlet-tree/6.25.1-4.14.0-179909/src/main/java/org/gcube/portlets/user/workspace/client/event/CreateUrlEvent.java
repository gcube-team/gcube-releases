package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class CreateUrlEvent extends GwtEvent<CreateUrlEventHandler> implements GuiEventInterface{
	public static Type<CreateUrlEventHandler> TYPE = new Type<CreateUrlEventHandler>();
	
	private FileModel parentFileModel;
	private String itemIdentifier;

	public CreateUrlEvent(String itemIdentifier, FileModel parent) {
		this.parentFileModel = parent;
		this.itemIdentifier = itemIdentifier;
	}

	@Override
	public Type<CreateUrlEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CreateUrlEventHandler handler) {
		handler.onClickCreateUrl(this);
		
	}

	public FileModel getParentFileModel() {
		return parentFileModel;
	}

	public void setParentFileModel(FileModel parentFileModel) {
		this.parentFileModel = parentFileModel;
	}

	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.ADDED_FILE_EVENT;
	}

	public String getItemIdentifier() {
		return itemIdentifier;
	}

}
