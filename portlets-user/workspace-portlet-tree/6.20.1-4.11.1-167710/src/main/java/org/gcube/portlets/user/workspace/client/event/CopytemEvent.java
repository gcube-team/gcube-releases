package org.gcube.portlets.user.workspace.client.event;

import java.util.List;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;

import com.google.gwt.event.shared.GwtEvent;

public class CopytemEvent extends GwtEvent<CopytemEventHandler> implements GuiEventInterface{
	public static Type<CopytemEventHandler> TYPE = new Type<CopytemEventHandler>();
	
//	private String itemId = null;

	private List<String> ids;
	
//	public CopytemEvent(String itemId) {
//		this.itemId = itemId;
//	}
	
	public CopytemEvent(List<String> ids) {
		this.ids = ids;
	}


	@Override
	public Type<CopytemEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CopytemEventHandler handler) {
		handler.onCopyItem(this);
		
	}

//	public String getItemId() {
//		return itemId;
//	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface#getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.COPY_EVENT;
	}

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

}
