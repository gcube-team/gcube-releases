package org.gcube.portlets.admin.dataminermanagerdeployer.client.event;


import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ContentPushEvent extends GwtEvent<ContentPushEvent.ContentPushHandler> {

    public interface ContentPushHandler extends EventHandler {
        void onContentPush(ContentPushEvent event);
    }

    public static final Type<ContentPushHandler> TYPE = new Type<>();


    public ContentPushEvent() {}

    public static void fire(HasHandlers source) {
        source.fireEvent(new ContentPushEvent());
    }

    @Override
    public Type<ContentPushHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ContentPushHandler handler) {
        handler.onContentPush(this);
    }
}