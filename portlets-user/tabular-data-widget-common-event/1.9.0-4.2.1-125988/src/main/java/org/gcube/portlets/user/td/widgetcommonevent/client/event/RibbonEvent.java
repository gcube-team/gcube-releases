package org.gcube.portlets.user.td.widgetcommonevent.client.event;

import org.gcube.portlets.user.td.widgetcommonevent.client.type.RibbonType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class RibbonEvent extends GwtEvent<RibbonEvent.RibbonEventHandler> {

	public static Type<RibbonEventHandler> TYPE = new Type<RibbonEventHandler>();
	private RibbonType ribbonType;

	public interface RibbonEventHandler extends EventHandler {
		void onRibbon(RibbonEvent event);
	}

	public interface HasRibbonEventHandler extends HasHandlers{
		public HandlerRegistration addRibbonEventHandler(RibbonEventHandler handler);
	}
	
	public RibbonEvent(RibbonType ribbonType) {
		this.ribbonType = ribbonType;
	}

	public RibbonType getRibbonType() {
		return ribbonType;
	}

	@Override
	protected void dispatch(RibbonEventHandler handler) {
		handler.onRibbon(this);
	}

	@Override
	public Type<RibbonEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<RibbonEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, RibbonType ribbonType) {
		source.fireEvent(new RibbonEvent(ribbonType));
	}

	@Override
	public String toString() {
		return "RibbonEvent [ribbonType=" + ribbonType + "]";
	}
	
	
	
}
