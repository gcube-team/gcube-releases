package org.gcube.common.core.state;

import org.gcube.common.core.contexts.GCUBEServiceContext;

/**
 * An abstract specialisation of {@link GCUBEStatefulResource} for stateful entities that 
 * are locally accessible. 
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 * 
 */
public abstract class GCUBELocalResource extends GCUBEStatefulResource<String> {
	
	/** Number of client that share the resource.*/
	private int links = 1;
	
	/** The context of the service in which the resource is used.*/
	protected GCUBEServiceContext context;
	

	/**{@inheritDoc} */
	protected void initialise(String id, Object ... params) throws Exception {
		super.setID((id!=null)?id:uuidGen.nextUUID());
		logger.trace("initialising "+this.getClass().getSimpleName()+"("+this.getID()+")");		
	}
	
	/**
	 * Returns the number of clients that share the resource.
	 * @return the number of clients that share the resource.
	 */
	public synchronized int getLinks() {return this.links;}

	/** Increments the number of clients that share the resource.*/
	public synchronized void addLink() {
		this.links++;
		logger.debug("increased sharing for "+this.getClass().getSimpleName()+"("+this.getID()+") to "+this.links);
	}
	
	/** Decrements the number of clients that share the resource.*/
	public synchronized void removeLink() {
		this.links--;
		logger.debug("decreased sharing for "+this.getClass().getSimpleName()+"("+this.getID()+") to "+this.links);
	}

	/**
	 * Sets the number of clients that share the resource.
	 * @param links the number of clients that share the resource.
	 */
	public synchronized void setLinks(int links) {if (links>0) this.links=links;}

	/** {@inheritDoc}*/
	public GCUBEServiceContext getServiceContext() {return this.context;}
	
	/**
	 * Sets the service context associated with the local resource.
	 * @param context the context.
	 */
	public void setServiceContext(GCUBEServiceContext context) {
		if (this.context==null) {//no need of synchronisation here as invoked from home in isolated thread
			this.context=context;
			this.logger.setContext(context);
		} else throw new RuntimeException("context already configured");
	}
	

}
