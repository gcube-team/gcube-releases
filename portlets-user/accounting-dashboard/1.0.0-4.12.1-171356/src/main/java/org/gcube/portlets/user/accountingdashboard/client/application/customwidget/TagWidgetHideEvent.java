package org.gcube.portlets.user.accountingdashboard.client.application.customwidget;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class TagWidgetHideEvent extends GwtEvent<TagWidgetHideEvent.TagWidgetHideEventHandler> {

	public interface TagWidgetHideEventHandler extends EventHandler {
		void onHide(TagWidgetHideEvent event);
	}

	public interface HasTagWidgetHideEventHandlers extends HasHandlers {
		HandlerRegistration addTagWidgetHideEventHandler(TagWidgetHideEventHandler handler);
	}

	private TagWidget tagWidget;

	public static final Type<TagWidgetHideEventHandler> TYPE = new Type<>();

	public TagWidgetHideEvent(TagWidget tagWidget) {
		this.tagWidget = tagWidget;
	}

	public static void fire(HasHandlers source, TagWidgetHideEvent event) {
		source.fireEvent(event);
	}

	@Override
	public Type<TagWidgetHideEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TagWidgetHideEventHandler handler) {
		handler.onHide(this);
	}

	public TagWidget getTagWidget() {
		return tagWidget;
	}

	@Override
	public String toString() {
		return "TagWidgetHideEvent [tagWidget=" + tagWidget + "]";
	}

}