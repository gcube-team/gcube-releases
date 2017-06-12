package org.gcube.common.core.utils.handlers;

import java.util.HashMap;
import java.util.Map;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.common.core.scope.GCUBEScopeManager;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.events.GCUBEProducer;
import org.gcube.common.core.utils.handlers.events.Event;
import org.gcube.common.core.utils.handlers.events.Monitor;
import org.gcube.common.core.utils.handlers.events.Topic;
import org.gcube.common.core.utils.handlers.events.Event.LifetimeEvent;
import org.gcube.common.core.utils.handlers.events.Topic.LifetimeTopic;
import org.gcube.common.core.utils.handlers.lifetime.Lifetime;
import org.gcube.common.core.utils.handlers.lifetime.State;
import org.gcube.common.core.utils.handlers.lifetime.State.Created;
import org.gcube.common.core.utils.handlers.lifetime.State.Failed;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * A partial implementation of {@link GCUBEIHandler}.
 * <p>
 * In particular, a {@link GCUBEHandler}:
 * <ul> 
 *  <li>has an automatically generated identity;</li>
 *  <li>has the unqualified name of its class as its default name;</li>
 *  <li>implements {@link Lifetime} for handlers that manage their own lifetime.
 *  <br><b>Note:</b> {@link GCUBEHandler} implements {@link Lifetime} but does not <em>declare</em> it. 
 *  Subclasses that commit to invoking those methods should do so. Those
 *  that do not make this commitment define handlers that remain permanently in the {@link State.Created} state.
 *  </li>
 * </ul>
 * 
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 * @param <HANDLED> the type of the handled object. */
public abstract class GCUBEHandler<HANDLED> implements GCUBEIHandler<HANDLED> {
	
	/** Identifier generator. **/
	private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
	
	/**Object logger.*/
	protected GCUBELog logger = new GCUBELog(this);
	
	/** The identifier of the handler. */
	private String id = uuidGen.nextUUID();
	
	/** The name of the handler. Defaults to the name of its class. */
	private String name = this.getClass().getSimpleName(); 
	
	/** The handled object. */
	protected HANDLED handled;
	
	/** The explicit state of the handler. */
	private Map<String,Object> blackboard=new HashMap<String,Object>();
	
	/** The status of the handler. */
	State state = Created.INSTANCE;
	
	/** Internal event producer. */
	protected GCUBEProducer<Topic,Object> producer = new GCUBEProducer<Topic,Object>();
	
	/** The {@link GCUBEScopeManager} used during the execution of the handler. */
	private GCUBEScopeManager scopeManager;

	/** The Security Manager for the execution. */
	private GCUBESecurityManager securityManager;
	
	/**Creates an instance.*/
	public GCUBEHandler() {}
	
	/**Creates an instance with a given handled object.
	 * @param handled the handled object.*/
	public GCUBEHandler(HANDLED handled) {this.setHandled(handled);}
	
	/**{@inheritDoc}*/
	public void setLogger(GCUBELog logger) {this.logger=logger;}
	
	/**{@inheritDoc}*/
	public GCUBELog getLogger() {return this.logger;}
	
	/**{@inheritDoc}*/
	public abstract void run() throws Exception;
	
	/**{@inheritDoc}*/
	public HANDLED getHandled() {return this.handled;}
	
	/**{@inheritDoc}*/
	public void setHandled(HANDLED handled) {this.handled=handled;}
	
	/**{@inheritDoc}*/
	public Map<String, Object> getBlackboard() {return blackboard;}
	
	/**{@inheritDoc}*/
	public void setBlackboard(Map<String, Object> state) {this.blackboard = state;}
	
	/**{@inheritDoc}*/
	public void clearBlackboard() {this.blackboard.clear();}

	/**{@inheritDoc}*/
	public String getId() {return id;}
	
	/**{@inheritDoc}*/
	public String getName() {return this.name;}
	
	/**{@inheritDoc}*/
	public void setName(String name) {this.name=name;}
	
	/**{@inheritDoc}*/
	public void undo() {}
	
	/**{@inheritDoc}*/
	public void subscribe(Monitor monitor,Topic...topics){producer.subscribe(monitor,topics.length==0?new Topic[]{LifetimeTopic.INSTANCE}:topics);}
	
	/**{@inheritDoc}*/
	public void unsubscribe(Monitor monitor,Topic...topics) {producer.unsubscribe(monitor,topics.length==0?new Topic[]{LifetimeTopic.INSTANCE}:topics);}
	
	/**Notifies all subscribed monitors of an {@link Event} about an associated {@link Topic}.
	 * @param (optional) the topic.
	 * @param e the event.*/
	@SuppressWarnings("unchecked") protected <TOPIC extends Topic> void notify(TOPIC topic,Event<TOPIC,?> e) {producer.notify(topic,e);}
	
	/**Notifies all subscribed monitors of a {@link LifetimeEvent}.
	* @param e the event.*/
	@SuppressWarnings("unchecked") private void notify(LifetimeEvent e) {e.setPayload(this);producer.notify(LifetimeTopic.INSTANCE,e);}
	
	/**See {@link Lifetime#getState()}.**/
	synchronized public State getState(){return this.state;}
	
	/**
	 * Sets the state of the handler.
	 * @param state the state.
	 * @throws IllegalArgumentException if the state is <code>null</code>
	 * @throws IllegalStateException if the handler cannot transition to the state from its current state.
	 */
	synchronized public void setState(State state) throws IllegalArgumentException,IllegalStateException {
		if (state==null) throw new IllegalArgumentException();
		if (this.state.getClass().isAssignableFrom(state.getClass())) return;//tolerate self-transitions
		boolean found=false;
		for (State s : state.getPrevious()) //transition legal? allows subclassing
			if (s.getClass().isAssignableFrom(this.state.getClass())) {found=true;break;}
		
		if (!found) throw new IllegalStateException("could not move from "+this.state+" to "+state); 
		
		try{
			try {this.state.onExit();} //exit current status
			catch(Exception e){
				if (!(state instanceof Failed)) throw e;//force transition to failed only
				logger.warn("could not exit from "+this.state+" on failing",e);
			}
			//logger.trace("moved from "+this.ltState+" to "+state);
			this.state=state; //change status
			LifetimeEvent ltEvent = state.getLifetimeEvent();
			if (ltEvent!=null) this.notify(ltEvent); //inform monitors
			this.state.onEnter();//enter new status
		}
		catch(Exception e) {//problem causes failure
			if (state instanceof Failed) {
				logger.warn("could not move to "+state+", forcing it.",e);
				this.state=Failed.INSTANCE;//force transition if to failed
			}
			else {
				logger.error("could not move to "+state,e);
				this.setState(Failed.INSTANCE); //try a proper transition otherwise
			}
		}
		
	}	
	
	/**{@inheritDoc}*/
	public void setSecurityManager(GCUBESecurityManager manager) {this.securityManager = manager;}
	
	/**{@inheritDoc}*/
	public void setScopeManager(GCUBEScopeManager manager) {this.scopeManager = manager;}
	
	/**{@inheritDoc} */
	public GCUBESecurityManager getSecurityManager() {return this.securityManager;}
	
	/**{@inheritDoc} */
	public GCUBEScopeManager getScopeManager() {return this.scopeManager;}	
	
}





