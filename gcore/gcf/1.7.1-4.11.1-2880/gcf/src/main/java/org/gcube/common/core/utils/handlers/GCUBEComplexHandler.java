package org.gcube.common.core.utils.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.gcube.common.core.utils.logging.GCUBELog;



/**
 * An abstract extension of {@link GCUBEHandler} that manages one or more
 * <em>component handlers</em>.<br> 
 * <p>
 * A {@link GCUBEComplexHandler} <em>may</em>
 * constrain its components to handle objects of the same type (by instantiating the type parameter
 * to a type other than {@link Object}). 
 * In turn, the component handlers <em>may</em> be built so as to share
 * the <em>same</em> handled object. 
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 * @param <HANDLED> the type of the object handled by the component handlers.
 */
public abstract class GCUBEComplexHandler<HANDLED> extends GCUBEHandler<HANDLED> {
	
	/** The component handlers */
	private List<GCUBEIHandler<? extends HANDLED>> handlers = new ArrayList<GCUBEIHandler<? extends HANDLED>>();
	
	
	/**
	 * Creates an instance with a list of component handlers.
	 * @param components the component handlers.
	 */
	//note: if we had made this method parametric (e,g, <T extends HANDLED), we would forced all
	//component handlers to have the same type, which is too strong for our requirements.
	public GCUBEComplexHandler(GCUBEIHandler<? extends HANDLED> ... components) {this.addHandlers(components);}
	
	/** 
	 * Adds one or more handlers to the list of component handlers. 
	 * @param components the handlers.
	 */
	//note: if we had made this method parametric (e,g, <T extends HANDLED), we would forced all
	//component handlers to have the same type, which is too strong for our requirements.
	//note: synchronization required to avoid interference during handler execution.
	synchronized public void addHandlers(GCUBEIHandler<? extends HANDLED> ... components) throws IllegalArgumentException {	
		if (components==null) throw new IllegalArgumentException("null is not a valid argument");
		this.handlers.addAll(Arrays.asList(components));
	}
	
	/**{@inheritDoc}*/
	public void setLogger(GCUBELog logger) {
		super.setLogger(logger);
		for (GCUBEIHandler<? extends HANDLED> handler : this.getHandlers()) handler.setLogger(this.getLogger()); //propagate logger	
	}
	
	/**
	 * Removes (the first occurrence of) a handler from the list of component handlers.
	 * @param component the handler.
	 * @return <code>true</code> if the handler was in the list, <code>false</code> otherwise.
	 */
	//note: synchronization required to avoid interference during handler execution.
	synchronized public boolean removeHandler(GCUBEIHandler<? extends HANDLED> component){	
		return this.handlers.remove(component);
	}
	
	/**
	 * Returns the component handlers.
	 * @return the handlers. 
	 * Note that modifications to the list will have no effect on the state of the handler.
	 */
	//note: synchronization required to avoid interference during list copy.
	synchronized public List<GCUBEIHandler<? extends HANDLED>> getHandlers() {return new ArrayList<GCUBEIHandler<? extends HANDLED>>(this.handlers);}
	
	/**{@inheritDoc} */
	synchronized public void undo() {
		//propagate error backwards
		ListIterator<GCUBEIHandler<? extends HANDLED>> i = this.handlers.listIterator(handlers.size());
		while (i.hasPrevious()) i.previous().undo();
	}
	
}