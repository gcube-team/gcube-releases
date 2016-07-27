package org.gcube.smartgears.lifecycle.container;

import static java.util.Arrays.*;
import static org.gcube.smartgears.lifecycle.container.ContainerLifecycle.*;

import java.util.Collections;
import java.util.List;

import org.gcube.smartgears.lifecycle.State;

/**
 * The state and state transitions of the lifecycle of an application managed as a gCube service.
 * 
 * @author Fabio Simeoni
 *
 */
public enum ContainerState implements State<ContainerState> {
	
	/**
	 * The state of a container that is in the process of initialisation.
	 */
	started(start){
		
		public List<ContainerState> next() {
			return asList(active,failed);
		}
		
		@Override
		public String remoteForm() {
			return "started";
		}
	},

	/**
	 * The state of a container in which not all applications are active.
	 */
	partActive(part_activation) {

		public List<ContainerState> next() {
			return asList(active,stopped,down);
		}
		
		@Override
		public String remoteForm() {
			return "ready";
		}
	},
	
	/**
	 * The state of a container in which all applications are active.
	 */
	active(activation) {

		public List<ContainerState> next() {
			return asList(partActive,stopped,down);
		}
		
		@Override
		public String remoteForm() {
			return "certified";
		}
	},
	
	/**
	 * The state of a container in which applications can no longer accept requests, even though they may in the future.
	 */
	stopped(stop){
		
		public List<ContainerState> next() {
			return asList(partActive,active);
		}
		
		@Override
		public String remoteForm() {
			return "down";
		}
	},
	
	/**
	 * The state of a container which has been explicitly shutdown.
	 */
	down(shutdown){
		
		public List<ContainerState> next() {
			return asList(partActive,active);
		}
		
		@Override
		public String remoteForm() {
			return "down";
		}
	},
	
	/**
	 * The state of a container that has not completed initialisation.
	 */
	failed(failure) {

		public List<ContainerState> next() {
			return Collections.emptyList();
		}
		
		@Override
		public String remoteForm() {
			return "failed";
		}
	};

	private final String event;
	
	//used internally
	ContainerState(String event) {
		this.event=event;
	}
	
	@Override
	public String event() {
		return event;
	}
}
