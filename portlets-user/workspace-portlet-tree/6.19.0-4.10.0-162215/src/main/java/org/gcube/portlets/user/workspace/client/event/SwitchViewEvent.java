package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer.ViewSwitchType;
import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;

import com.google.gwt.event.shared.GwtEvent;

public class SwitchViewEvent extends GwtEvent<SwitchViewEventHandler> implements GuiEventInterface{
	public static Type<SwitchViewEventHandler> TYPE = new Type<SwitchViewEventHandler>();
	
	private ViewSwitchType type;
	
	public SwitchViewEvent(ViewSwitchType type) {
		this.type = type;
	}

	@Override
	public Type<SwitchViewEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SwitchViewEventHandler handler) {
		handler.onSwitchView(this);
		
	}

	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.SWITCH_VIEW_EVENT;
	}

	public ViewSwitchType getType() {
		return type;
	}
	
}
