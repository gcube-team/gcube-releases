package org.gcube.smartgears.lifecycle.application;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.gcube.smartgears.lifecycle.application.ApplicationLifecycle.*;

import java.util.List;

import org.gcube.smartgears.lifecycle.State;

/**
 * The state and state transitions of the lifecycle of an application managed as a gCube service.
 * 
 * @author Fabio Simeoni
 *
 */
public enum ApplicationState implements State<ApplicationState> {
	
	/**
	 * The state of applications that are in the process of initialisation.
	 */
	started(start){
		
		public List<ApplicationState> next() {
			return asList(active, failed);
		}
		
		
		@Override
		public String remoteForm() {
			return "STARTED";
		}
	},

	/**
	 * The state of applications that have completed initialisation and can accept client requests.
	 */
	active(activation) {

		public List<ApplicationState> next() {
			return asList(failed,stopped);
		}
		
		
		@Override
		public String remoteForm() {
			return "ready";
		}
	},
	
	/**
	 * The state of applications that have can no longer accept requests, even though they may in the future.
	 */
	stopped(stop){
		
		public List<ApplicationState> next() {
			return asList(failed,active);
		}
		
		
		@Override
		public String remoteForm() {
			return "down";
		}
	},

	/**
	 * The permanent state of applications that has encountered some fatal failure and can no longer accept requests.
	 */
	failed(failure){

		public List<ApplicationState> next() {
			return emptyList();
		}
		
		
		@Override
		public String remoteForm() {
			return "failed";
		}
	};

	private final String event;
	
	//used internally
	ApplicationState(String event) {
		this.event=event;
	}
	
	@Override
	public String event() {
		return event;
	}

}
