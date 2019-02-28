package org.gcube.portlets.user.accountingdashboard.client.application.controller;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ControllerModule extends AbstractGinModule {
	@Override
	protected void configure() {

		bind(Controller.class).in(Singleton.class);
		bind(EventBus.class).annotatedWith(Names.named("ControllerEventBus")).to(SimpleEventBus.class)
				.in(Singleton.class);
	}

}
