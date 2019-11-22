package org.gcube.common.core.state;

import org.globus.wsrf.ResourceException;

/**
 * A specialisation of {@link GCUBEResourceHome} for {@link GCUBELocalResource GCUBELocalResource}s.
 *
 * @author Fabio Simeoni (University of Strathclyde) 
 *
 */
public abstract class GCUBELocalHome extends GCUBEResourceHome<String,String,GCUBELocalResource>  {
    
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////LIFETIME MANAGEMENT
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** {@inheritDoc}*/
	protected void onInitialisation() throws Exception {
		//sanity check on resource class
		if (!GCUBELocalResource.class.isAssignableFrom(resourceClass))	throw new Exception(this.getClass().getSimpleName()+":is not a LocalResource class");
		super.onInitialisation();
	}
    
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////CALLBACKS
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Callbacks are used for link management and context propagation.
	
	 /**{@inheritDoc}*/
    protected void onReuse(GCUBELocalResource resource) throws ResourceException {resource.addLink();}
	
   	/**{@inheritDoc}*/
	protected void preInitialise(GCUBELocalResource resource) throws ResourceException {
		resource.setServiceContext(this.getServiceContext());//propagates context to resource
		super.preInitialise(resource);
	}
	
	/**{@inheritDoc}*/
    protected boolean onRemove(GCUBELocalResource resource) throws ResourceException {
    	resource.removeLink();
    	boolean remove = resource.getLinks()==0?true:false;//remove local resource if no longer shared
    	if (!remove)
    		resource.store(); //store new link count then!
    	return remove;
    }

}