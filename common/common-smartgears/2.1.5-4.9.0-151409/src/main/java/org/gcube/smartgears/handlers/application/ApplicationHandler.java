package org.gcube.smartgears.handlers.application;

import org.gcube.smartgears.handlers.Handler;

/**
 * A {@link Handler} of {@link ApplicationEvent}s.
 * 
 * @author Fabio Simeoni
 * 
 * @param <T> the self type of the handler.
 * 
 * @see ApplicationEvent
 */
public interface ApplicationHandler<T extends ApplicationHandler<T>> extends Handler<ApplicationEvent<T>> {

}
