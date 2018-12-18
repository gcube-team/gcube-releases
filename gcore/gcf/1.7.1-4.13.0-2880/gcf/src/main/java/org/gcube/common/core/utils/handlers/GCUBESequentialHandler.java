package org.gcube.common.core.utils.handlers;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.utils.handlers.lifetime.Lifetime;
import org.gcube.common.core.utils.handlers.lifetime.State.Done;
import org.gcube.common.core.utils.handlers.lifetime.State.Failed;
import org.gcube.common.core.utils.handlers.lifetime.State.Running;



/**
 * An extension of {@link GCUBEComplexHandler} for handlers which chain the execution 
 * of their component handlers. A {@link GCUBESequentialHandler} propagates 
 * its state across the chain as a blackboard based on which component handlers <em>may</em>
 * may communicate in a decoupled fashion. For further decoupling, a {@link GCUBESequentialHandler}
 * recognises components which have the semantics of {@link GCUBEHandlerConnector GCUBEHandlerConnectors} and connects
 * them to components which immediately precede them and immediately follow them in the chain.
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 *
 * @param <HANDLED> the type of the object handled by the component handlers.
 * @see GCUBEHandlerConnector
 */
public class GCUBESequentialHandler<HANDLED> extends GCUBEComplexHandler<HANDLED> implements Lifetime<HANDLED> {

	/**
	 * Creates an instance with a list of component handlers.
	 * @param components the component handlers.
	 */
	public GCUBESequentialHandler(GCUBEIHandler<? extends HANDLED> ... components) {super(components);}
	
	/**{@inheritDoc}*/
	synchronized public void run() throws Exception {//synchronizes with respect to additions and removals of components

		setState(Running.INSTANCE);
		final List<GCUBEIHandler<? extends HANDLED>> handlers = this.connectComponents();	
		for (final GCUBEIHandler<? extends HANDLED> handler : handlers) {
			try {handler.run();}
			catch(Exception e) {//some problem, propagates fault backwards
				new Thread() {
					public void run() {
						try{for (int j=handlers.indexOf(handler)-1; j>=0;j--) handlers.get(j).undo();}
						catch(Exception e) {logger.warn("Could not recover from failure of "+handler.getClass());}
					}}.start();
				setState(Failed.INSTANCE);	
				throw e;
			}
		}
		setState(Done.INSTANCE);
	}

	/**
	 * Invoked internally before a run to insert and configure {@link GCUBEHandlerConnector GCUBEHandlerConnectors} before running.
	 * In particular, existing connectors are configured and new {@link GCUBEHandlerConnector GCUBEHandlerConnectors} are
	 * inserted in between non-connector components.
	 * @return the list of component handlers augmented with configured connectors.
	 */
	@SuppressWarnings("unchecked")
	protected List<GCUBEIHandler<? extends HANDLED>> connectComponents() {
		
		List<GCUBEIHandler<? extends HANDLED>> components = this.getHandlers();
		components.add(0,this);components.add(this);//..append the sequential handler at both ends
		
		List<GCUBEIHandler<? extends HANDLED>> handlers = new ArrayList<GCUBEIHandler<? extends HANDLED>>();//prepares a list of handlers to be executed
		
		for (int i=0; i<components.size();i++) {
			
			//sort out 'coordinates'
			GCUBEIHandler prevComponent = i-1>=0?components.get(i-1):null;
			GCUBEIHandler currentComponent = components.get(i);
			GCUBEIHandler nextComponent = i+1<components.size()?components.get(i+1):null;
			
			handlers.add(currentComponent); //copy to output list
			
			if (currentComponent instanceof GCUBEHandlerConnector) {//configure manual connector
				GCUBEHandlerConnector connector = (GCUBEHandlerConnector) currentComponent;
				connector.previous=prevComponent;
				connector.next = nextComponent;
			}
			else if (nextComponent!=null && !(nextComponent instanceof GCUBEHandlerConnector)) {//not a connector but followed by one
				GCUBEHandlerConnector connector = new GCUBEHandlerConnector(){public void connect(){}};
				connector.previous=currentComponent;
				connector.next = nextComponent;
				handlers.add(connector);//add a default connector to output list
			}
		}
		handlers.remove(handlers.size()-1);handlers.remove(0); //now remove sequential handler from both ends of output list
		return handlers;
	}
	
	
}

