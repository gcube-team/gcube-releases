package org.gcube.smartgears.lifecycle;

import org.gcube.common.events.Hub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLifecycle<S extends State<S>> implements Lifecycle<S> {

	private static final Logger log = LoggerFactory.getLogger(Lifecycle.class);
	
	private final Hub hub;
	private final String name;
	private S currentState;
	private S previousState;
	
	public DefaultLifecycle(Hub hub, String name, S startState){
		this.hub = hub;
		this.currentState = startState;
		this.name = name;
	}
	
	@Override
	public S state() {
		return currentState;
	}

	@Override
	public void moveTo(S next) {
		
		if (next == currentState) 
			return;
		
		if (currentState.next().contains(next)){

			this.previousState = this.currentState;
			this.currentState = next;
			
			log.trace("{} transitioned from {} to {}",new Object[]{name,previousState,currentState});
			
			hub.fire(this, currentState.event());
			
		}
		else throw new IllegalStateException("cannot transition "+name+" from " +currentState+" to "+next);
			
	}
	
	@Override
	public boolean tryMoveTo(S next) {
		try {
			moveTo(next);
			return true;
		}
		catch(IllegalStateException ignore) {
			return false;
		}
		
	}

	public S previous() {
		return this.previousState;
	}

}
