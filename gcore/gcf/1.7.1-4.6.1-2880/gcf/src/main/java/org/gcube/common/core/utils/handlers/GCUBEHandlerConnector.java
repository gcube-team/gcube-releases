package org.gcube.common.core.utils.handlers;

import java.util.Map;


/**
 * Abstract implementation of component handlers used to connect the output of those which immediately precede them 
 * with the input of those which immediately follow them.
 * @param <PREVIOUS> the type of component handler which immediately precedes the connector.
 * @param <NEXT> the type of component handler which immediately follows the connector.
 * @author Fabio Simeoni (University of Strathclyde)
 **/
public abstract class GCUBEHandlerConnector<T,PREVIOUS extends GCUBEIHandler<? extends T>,NEXT extends GCUBEIHandler<? extends T>> extends GCUBEHandler<T> {
	/**The component handler which precedes the connector.*/
	protected PREVIOUS previous;
	/**The component handler which follows the connector.*/
	protected NEXT next;	
	
	/** Return the component handler which precedes the connector.
	 * @return the component handler.*/
	public PREVIOUS getPrevious() {return this.previous;}
	/** Return the component handler which follows the connector.
	 * @return the component handler.*/
	public NEXT getNext() {return this.next;}
	
	/** {@inheritDoc} */ @Override
	public final void run() throws Exception {
		logger.debug("connecting "+this.previous.getClass().getSimpleName()+" with "+this.next.getClass().getSimpleName()+" through "+this.getClass());
		//non destructive state merge
		for (Map.Entry<String,Object> entry : this.previous.getBlackboard().entrySet())
			if (!this.next.getBlackboard().containsKey(entry.getKey())) 
			this.next.getBlackboard().put(entry.getKey(),entry.getValue());
		
		this.connect();
	}
	
	/** Invoked by {{@link #run()}, defines the connections between the {{@link #previous} and the {{@link #next} component handlers. 
	 * 	Implement to define specific connections. */
	public abstract void connect() throws Exception;
	
}
	
