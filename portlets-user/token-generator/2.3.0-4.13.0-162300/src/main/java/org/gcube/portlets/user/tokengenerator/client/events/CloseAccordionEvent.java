package org.gcube.portlets.user.tokengenerator.client.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

public class CloseAccordionEvent  extends GwtEvent<CloseAccordionEventHandler> {
	public static Type<CloseAccordionEventHandler> TYPE = new Type<CloseAccordionEventHandler>();
	Widget closedPanel;
	
	public CloseAccordionEvent(Widget closedPanel) {
		this.closedPanel = closedPanel;
	}
	
	public Widget getClosedPanel(){
		return this.closedPanel;
	}

	@Override
	public Type<CloseAccordionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CloseAccordionEventHandler handler) {
		handler.onCloseAccordion(this);
	}
}
