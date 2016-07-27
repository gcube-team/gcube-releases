package org.gcube.resourcemanagement.support.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.HandlerManager;

public class Resource_support implements EntryPoint {
	private HandlerManager eventBus;
	
	public Resource_support() {
		super();
	}

	public Resource_support(HandlerManager eventBus){
		singleton = this;
		this.eventBus = eventBus;
	}
	
	private static Resource_support singleton;
	
	public static Resource_support get() {
		return singleton;		
	}
	
	public HandlerManager getEventBus() {
		return this.eventBus;
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
	}
}
