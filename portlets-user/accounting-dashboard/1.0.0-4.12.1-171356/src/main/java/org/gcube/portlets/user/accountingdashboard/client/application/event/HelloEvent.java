package org.gcube.portlets.user.accountingdashboard.client.application.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class HelloEvent  extends GwtEvent<HelloEvent.HelloEventHandler> {

    public interface HelloEventHandler extends EventHandler {
        void onHello(HelloEvent event);
    }

    public static final Type<HelloEventHandler> TYPE = new Type<>();


    public HelloEvent() {}

    
    public static void fire(HasHandlers source, HelloEvent event) {
		source.fireEvent(event);
	}

    @Override
    public Type<HelloEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(HelloEventHandler handler) {
        handler.onHello(this);
    }
}