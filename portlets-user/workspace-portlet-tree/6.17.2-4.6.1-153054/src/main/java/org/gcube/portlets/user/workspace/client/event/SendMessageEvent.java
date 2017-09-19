package org.gcube.portlets.user.workspace.client.event;

import java.util.List;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class SendMessageEvent extends GwtEvent<SendMessageEventHandler> implements GuiEventInterface{
	public static Type<SendMessageEventHandler> TYPE = new Type<SendMessageEventHandler>();
	
	private List<FileModel> listFileModelSelected;
	
//	public AddFolderEvent(FolderModel newFolder, FolderModel parentFileModel) {
//		this.newFolder = newFolder;
//		this.parentFileModel = parentFileModel;
//	}
	
	public SendMessageEvent(List<FileModel> selected) {
		this.listFileModelSelected = selected;
	}

	@Override
	public Type<SendMessageEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	protected void dispatch(SendMessageEventHandler handler) {
		handler.onSendMessage(this);
		
	}

	public List<FileModel> getListFileModelSelected() {
		return listFileModelSelected;
	}

	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.CREATE_NEW_MESSAGE;
	}

}
