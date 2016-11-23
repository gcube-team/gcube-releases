package org.gcube.portlets.admin.accountingmanager.client.event;

import org.gcube.portlets.admin.accountingmanager.shared.tabs.EnableTabs;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class EnableTabsEvent extends
		GwtEvent<EnableTabsEvent.EnableTabsEventHandler> {

	public static Type<EnableTabsEventHandler> TYPE = new Type<EnableTabsEventHandler>();
	private EnableTabs enableTabs;

	public interface EnableTabsEventHandler extends EventHandler {
		void onEnableTabs(EnableTabsEvent event);
	}

	public interface HasEnableTabsEventHandler extends HasHandlers {
		public HandlerRegistration addEnableTabsEventHandler(
				EnableTabsEventHandler handler);
	}

	public EnableTabsEvent(EnableTabs enableTabs) {
		this.enableTabs = enableTabs;
	}

	@Override
	protected void dispatch(EnableTabsEventHandler handler) {
		handler.onEnableTabs(this);
	}

	@Override
	public Type<EnableTabsEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<EnableTabsEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, EnableTabsEvent enableTabsEvent) {
		source.fireEvent(enableTabsEvent);
	}

	public EnableTabs getEnableTabs() {
		return enableTabs;
	}

	@Override
	public String toString() {
		return "EnableTabsEvent [enableTabs=" + enableTabs + "]";
	}

}
