package org.gcube.common.core.state;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.namespace.QName;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.resources.GCUBEResource.RemoveScopeEvent;
import org.gcube.common.core.resources.GCUBEResource.ResourceConsumer;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeManager.IllegalScopeException;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.handlers.GCUBEScheduledHandler;
import org.gcube.common.scope.api.ScopeProvider;
import org.globus.wsrf.NoSuchResourceException;
import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceHome;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.impl.lifetime.SetTerminationTimeProvider;

/**
 * An abstract specialisation of {@link GCUBEResourceHome} to {@link GCUBEWSResource GCUBEWSResources}.
 * <p>
 * It enforces scoping rules for resources and it manages their lifetime and publication in accordance with
 * configuration (cf. {@link #onInitialisation()}). In particular: <p>
 * 
 * -) resources creation must be scoped and the scope must be compatible with (that
 * of) the Running Instance. As a result, the resource acquires the scope and is published therein. <br>
 * 
 * -) resources removal must be scoped and the scope must be compatible with (that of) the resource. 
 * As a result, the resource loses the scope and is unpublished from it. If the resource is left unscoped,
 * it is physically removed (including its persistent serialisation, if any).<br> 
 * 
 * -) resource retrieval can be unscoped for legacy reasons. if scoped, however,
 *  the scope must be compatible with (that of) the resource. <br>  
 * 
 * -) the removal of the Running Instance from a scope propagates to all the resources created in that scope.<br>
 *  
 * -) persistent resources are republished in all their scopes at container startup. <br>
 *  
 * -) resources lifetime is checked periodically for resources that are in memory, and at the point of
 * usage for those that exist only in storage. if found expired, the resource is removed from all its scopes and thus
 * physically removed (including its persistent serialisation, if any).
 * 
 *
 * @author Fabio Simeoni (University of Strathclyde)
 * @author Manuele Simi (CNR)
 * 
 *
 */
public abstract class GCUBEWSHome extends GCUBEResourceHome<ResourceKey,GCUBEWSResourceKey,GCUBEWSResource> implements ResourceHome {


	/** The qualified name of the resource key. */
	protected QName keyTypeName;
    /** The handler that  controls the lifetime of WS-Resources.*/
    protected SweeperScheduler sweeperScheduler;
    /** Time between activations of the {@link org.gcube.common.core.state.GCUBEWSHome.SweeperScheduler.Sweeper}, in seconds. */
    private long sweeperDelay = 60;
    
    /**
     * Sets the qualified name of the key of the WS-Resource managed by the home.
     * @param keyName the name.
     */
    public synchronized void setKeyName(String keyName) {
    	if (this.keyTypeName==null) this.keyTypeName = QName.valueOf(keyName);
    	else throw new RuntimeException("key name already configured");//cannot change this configuration
    }
 
    /**
     * Returns the qualified name of the key of the WS-Resources managed by the home.
     * @return the name (<code>{<em>[port-type namespaces<]/em>}ResourceKey</code> by default).
     */
    public synchronized QName getKeyTypeName() {
    	this.keyTypeName=new QName(this.getPortTypeContext().getNamespace(),"ResourceKey");
    	return this.keyTypeName;
    }
    
    /**
     * Satisfies the {@link ResourceHome} interface by returning the {@link Class} of the value wrapped by the resource identifier.
     * 
     *  @return the class.
     */
	public final Class<String> getKeyTypeClass() {return String.class;}
	
	/**
	 * Sets the time between activations of the sweeper.
	 * @param delay the time, in seconds.
	 */
	public synchronized void setSweeperDelay(long delay) {this.sweeperDelay = delay;}

	/**
	 * Returns the time between activations of the sweeper.
	 * @return the time, in seconds.
	 */
	public synchronized long getSweeperDelay() {return this.sweeperDelay;}
	
    /**
	 * Returns the associated {@link GCUBEStatefulPortTypeContext}.
	 * @return the port-type context.                         
	 */
	public abstract GCUBEStatefulPortTypeContext getPortTypeContext();
	
	/**{@inheritDoc} */
	public GCUBEServiceContext getServiceContext() {return this.getPortTypeContext().getServiceContext();}


    
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////LIFETIME MANAGEMENT
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Invoked during initialisation of the Running Instance to initialise the home.<p>
	 * 
     * Most of the initialisation occurs from JNDI configuration, which
     * must adhere to the following template (text in italics marks points of instantiations):</p>
	 * 
	 * <code>
	 * &lt;resource name="[<em>name</em>]" type="[<em> the FQN of a subclass of {@link GCUBEWSHome}</em>]"&gt;<br>
	 *    &nbsp;&nbsp;&lt;resourceParams&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name&gt;factory&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>org.globus.wsrf.jndi.BeanFactory&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name>resourceClass&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>[<em>FQN of a {@link GCUBEWSResource} extension</em>]&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *	 	&nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name>persistenceDelegateClass&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>[<em>FQN of a concrete subclass of {@link org.gcube.common.core.persistence.GCUBEPersistenceDelegate}</em>]&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name>keyName&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>[<em>the serialisation of a {@link javax.xml.namespace.QName}</em>]&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *	    &nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name>sweeperDelay&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>[<em>milliseconds</em>]&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *	 &nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name>cacheTimeout&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>[<em>milliseconds, e.g. 120000</em>]&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *	&nbsp;&nbsp;&lt;/resourceParams&gt;<br>
	 *&lt;/resource&gt;
	 *</code>
	 *
	 *<p> where <code>keyName</code>, <code>sweeperDelay</code> and <code>cacheTimeout</code> are optional, 
	 *and the <code>name</code> of the JNDI resource and its context of occurrence are to be constrained by clients.
	 *
	 **/
	@Override protected void onInitialisation() throws Exception {
		if (!GCUBEWSResource.class.isAssignableFrom(resourceClass)) //typecheck on resource class
			throw new Exception(this.getClass().getSimpleName()+" does not extend "+GCUBEWSResource.class.getSimpleName());
        this.sweeperScheduler =new SweeperScheduler();
        super.onInitialisation();
        this.sweeperScheduler.run();
        logger.info("activated sweeper to run every "+getSweeperDelay()+" seconds");        
   }
	
	/**{@inheritDoc}*/
	@Override protected void onReady() throws Exception {
		super.onReady();
		ResourceConsumer consumer =  new ResourceConsumer() {
			@Override protected void onRemoveScope(RemoveScopeEvent event) {
				logger.info("re-assessing scope consistency of all resources after removal of "+event.getPayload()[0]);
	    		for (GCUBEWSResourceKey key : getIdentifiers()) {
		    		try {
						GCUBEWSResource resource = find(key);
						
						ScopeProvider.instance.set(event.getPayload()[0].toString());//now we can change the scope to allow locking
						
						GCUBEReadWriteLock.GCUBEWriteLock lock=resource.getLock().writeLock();
						try{lock.lockInterruptibly();} catch(InterruptedException e) {continue;}//removed concurrently
						try {
							for (String resScopeExpression : resource.getResourcePropertySet().getScope()) {//for each ws-resource scope
								GCUBEScope resScope = GCUBEScope.getScope(resScopeExpression);
								//we use test in setScope() to check if scope is no longer compatible with RI
								try {getServiceContext().setScope(resScope);}
								catch(IllegalScopeException usedAsTest) {
									try {
										logger.trace("removing resource from scope "+resScope+" after RI");
										
										ScopeProvider.instance.set(resScope.toString());
										
										remove(resource);
									}
									catch(Exception e) {logger.warn("could not remove "+resource.getClass().getSimpleName()+"("+resource.getID()+") from scope "+resScope);}
								}
							}
						}
						finally{lock.unlock();}
					}
					catch (Exception e) {logger.debug("problem",e);continue;}////publication will expire naturally
		    	}}
		};
		getServiceContext().getInstance().subscribeResourceEvents(consumer,GCUBEResource.ResourceTopic.REMOVESCOPE);
		logger.trace("registered for RI scope removal events");//subscribe with RI for scope removal events	
   }
	
	/**{@inheritDoc}*/
	@Override protected void onFailure() throws Exception {if (this.sweeperScheduler!=null) this.sweeperScheduler.stop();}
	
	///////////////////---------------------LOW-LEVEL OPERATIONS---------------------//////////////////////////                       
    //                                                                                                       //
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
    /**{@inheritDoc}*/
    protected GCUBEWSResource get(ResourceKey id) throws ResourceException, NoSuchResourceException {
    	GCUBEWSResource resource = super.get(id);
    	//non-legacy call requires scope check but this prevents reuse across multiple scopes from create in superclass.
    	//cannot throw a more specific exception and catch it there, as the superclass is scope agnostic. must renounce for now..
		if (!isLegacyCall() && !resource.inScope(currentScope())) throw new NoSuchResourceException();
    	return resource;
    }
	
 	///////////////////---------------------HIGH-LEVEL OPERATIONS---------------------/////////////////////////                       
    //                                                                                                       //
    // These operations are extended for scope management purposes.Ideally, we would add the following       //
    // checks in low-level get():                                                                            //
    //                                                                                                       //
    // -) precondition:calls must be scoped                                                                  //
    // -) precondition:scope must be RI-compatible (as internal calls bypass GCUBEHandler checks)            //
    // -) postcondition:scope must be resource-compatible (resource must be in call scope to be used)        //
    //                                                                                                       //
    // Unfortunately, unscoped legacy scopes must be allowed for state polling in find() only. So we         // 
    // move preconditions to high-level operation, repeating them.                                           //
    //                                                                                                       //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    
   	/** {@inheritDoc}*/
 	public GCUBEWSResource create(Object ... params) throws ResourceException {
 		if (this.isLegacyCall() || !this.isValidScope()) throw new ResourceException(new IllegalScopeException());	
 		return super.create(params);
 	}
 	
    /** {@inheritDoc}*/
    public GCUBEWSResource create(GCUBEWSResourceKey id, Object ... params) throws ResourceException {
    	if (this.isLegacyCall() || !this.isValidScope()) throw new ResourceException(new IllegalScopeException());
 		return super.create(id,params);
    }
    
    /**{@inheritDoc}*/
	protected GCUBEWSResource reuse(GCUBEWSResourceKey id, Object ... params) throws ResourceException {
		GCUBEScope callScope = currentScope();
		
		ScopeProvider.instance.reset();//simulate an unscoped call to pass get() checks;
		
		GCUBEWSResource resource;
		try{resource = super.reuse(id,params);}
		finally{this.getServiceContext().setScope(callScope);}//reset the scope so that it can be added in onScope();
		return resource;
	}
	
    /**
     * Returns the resource bound to the current call. 
     * @return the resource.
     * @throws ResourceException if the resource could not be identified or returned.
     */
    public GCUBEWSResource find() throws ResourceException {
    	try {return (GCUBEWSResource) ResourceContext.getResourceContext().getResource();}
    	catch(Exception e) {throw new ResourceException(e);}
    }
    
    
	/** {@inheritDoc}*/
	public GCUBEWSResource find(ResourceKey id) throws ResourceException,NoSuchResourceException {
		GCUBEWSResource resource = super.find(this.convertLegacyID(id));
		//postcondition:non-legacy call requires scope check
		if (!isLegacyCall() && !this.isValidScope()) throw new NoSuchResourceException();
		return resource;
	}
	
    
	/** {@inheritDoc} */
	public void remove(ResourceKey id) throws ResourceException {
		if (this.isLegacyCall() || !this.isValidScope()) throw new ResourceException(new IllegalScopeException());
    	super.remove(this.convertLegacyID(id));
    	//further scope checks necessarily in onRemove() as we do not get the resource back from super().
	}
	
	//helper
	GCUBEScope currentScope() {
		String currentScope = ScopeProvider.instance.get();
		return currentScope==null?null:GCUBEScope.getScope(currentScope);
	}
	/**
	 * Used internally to detect correctly scoped calls.
	 * @throws ResourceExceptionif the call is not correctly scoped.
	 */
	private boolean isValidScope() throws ResourceException {
		return this.getServiceContext().getInstance().inScope(currentScope());
	}
	
	/**
	 * Used internally to detect legacy calls.
	 * @return <code>true</code> if the call is legacy, <code>false</code> otherwise.
	 */
	private boolean isLegacyCall() {
		return currentScope()==null;
	}
	
	/**
	 * Used internally to convert a legacy identifier.
	 * @param id the legacy identifier.
	 * @return the converted identifier.
	 */
	private GCUBEWSResourceKey convertLegacyID(ResourceKey id) {
		return id==null?null:GCUBEWSResourceKey.class.isAssignableFrom(id.getClass())?(GCUBEWSResourceKey)id:new GCUBEWSResourceKey(id);
	}
	
 	///////////////////---------------------------CALLBACKS---------------------------/////////////////////////                       
    //                                                                                                       //
	//  Callbacks refined for scope management, lifetime management, state publication and                   //
	//  context propagation.                                                                                 //        
	//                                                                                                       //
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**{@inheritDoc} */
    protected void preInitialise(GCUBEWSResource resource) throws ResourceException {   
        super.preInitialise(resource);
    	resource.setPortTypeContext(this.getPortTypeContext());//propagates port-type context to resource
    	try {resource.initialiseContainers();}
        catch(Exception e) {throw new ResourceException(e);}
    }
    
    /** {@inheritDoc}*/
    protected void postInitialise(GCUBEWSResource resource) throws ResourceException {       
    	super.postInitialise(resource);
    	resource.getResourcePropertySet().addScope(currentScope());
    	resource.publish(currentScope());
    }
    
    /**{@inheritDoc} */
    protected void onReuse(GCUBEWSResource resource) throws ResourceException {
    	super.onReuse(resource);
    	GCUBEScope callScope = currentScope();//publish only in a new scope
    	if (resource.getResourcePropertySet().addScope(callScope)) 
    			resource.publish(callScope);
    }    

    /** {@inheritDoc}*/
    protected void onLoad(GCUBEWSResource resource, boolean firstLoad) throws ResourceException {      
    	
    	super.onLoad(resource,firstLoad);
        if (sweeperScheduler.isExpired(resource)) {//check termination time of resource
    		String msg = "resource "+this.resourceClass.getSimpleName()+"("+resource.getID()+") is expired";
    		logger.trace(msg);
    		sweeperScheduler.remove(resource);
    		throw new NoSuchResourceException(msg);
    	}
    	
    	if (firstLoad) { //if it's a first time load, republish resource in all its scopes
        	List<GCUBEScope> scopes =  new ArrayList<GCUBEScope>();
        	for (String s : resource.getResourcePropertySet().getScope()) scopes.add(GCUBEScope.getScope(s));
        	resource.publish(scopes.toArray(new GCUBEScope[0])); 
        }
    }
    
    /**{@inheritDoc}*/
    protected boolean onRemove(GCUBEWSResource resource) throws ResourceException {
    	
    	GCUBEScope callScope = currentScope();
		
		//first unpublish resource with a scope check
    	if (resource.getResourcePropertySet().removeScope(callScope)) {
    		logger.info("removing a "+this.resourceClass.getSimpleName()+"("+resource.getID()+") from scope "+callScope);
    		resource.unpublish(callScope);
    	}
    	else throw new ResourceException(new IllegalScopeException()); //see scope management comments above
		
    	//then physically remove only if resource is not published elsewhere
		if (resource.getResourcePropertySet().getScope().size()==0) {           
			SetTerminationTimeProvider.sendTerminationNotification(resource);//@TODO check with termination provider
			//logger.info("removing a "+this.resourceClass.getSimpleName()+"("+resource.getID()+")");
			return true;
		}
				
		return false;
    }  
    

 	///////////////////----------------------LIFETIME MANAGEMENT----------------------/////////////////////////                       
    //                                                                                                       //
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    /**
     * Monitors the lifetime of resource at regular intervals.
     * @author Fabio Simeoni (University of Strathclyde)     *
     */
	private class SweeperScheduler extends GCUBEScheduledHandler<Object> {
		
		/** Creates an instance */
		protected SweeperScheduler() {
			super(getSweeperDelay(),GCUBEScheduledHandler.Mode.LAZY);
			this.setName(GCUBEWSHome.this.getServiceContext().getName()+"-Resource Sweeper-"+GCUBEWSHome.this.getClass().getSimpleName());
			Sweeper sweeper = new Sweeper();
			sweeper.setName(this.getName());
			this.setScheduled(sweeper);
		}
		
		/** {@inheritDoc} */
		@Override protected boolean repeat(Exception exception, int exceptionCount) {
			if (exception!=null) logger.warn("could not sweep "+resourceClass.getSimpleName()+" resources",exception);
			return true;
		}
		/**
		 * Indicates whether, at any given time, a given resource's lifetime is expired.
		 * @param resource the resource.
		 * @param time the time.
		 * @return <code>true</code> if the resource is expired, <code>false</code> otherwise.
		 */
		private boolean isExpired(GCUBEWSResource resource, Calendar ... time) {
			Calendar currentTime = (time==null || time.length==0)?Calendar.getInstance():time[0];
	        Calendar terminationTime = resource.getTerminationTime();
	        return (terminationTime != null && terminationTime.before(currentTime));
		}
		
		/**
		 * Removes a resource from all its scopes, regardless of the scope associated with the current thread.
		 * @param resource the resource.
		 */
		protected void remove(GCUBEWSResource resource) {
			//remove it from all scopes, ignoring any scope this call may be made in.
			for (String scope : resource.getResourcePropertySet().getScope()){//removes resources one scope at the time so as to pass scope normal scope checks
		       	getServiceContext().setScope(GCUBEScope.getScope(scope));
		    	try {GCUBEWSHome.this.remove(resource);}
	           	catch(Exception e) {logger.warn("could not remove "+resourceClass.getSimpleName()+"("+resource.getID()+") from scope "+scope+" after expiry",e);}
	        }
		}
		
		
		/**
		 * Removes expired resources.
		 * @author Fabio Simeoni (University of Strathclyde)
		 */
		protected class Sweeper extends GCUBEHandler<Object> {
			
			/** {@inheritDoc} */
			@Override public void run() throws Exception {
				List<GCUBEWSResource> expired = new ArrayList<GCUBEWSResource>();
				Calendar currentTime = Calendar.getInstance();
				for (GCUBEWSResource resource: getResources()) if (isExpired(resource, currentTime)) expired.add(resource);           
				if (expired.size()>0) logger.trace("sweeping "+expired.size()+" expired resource(s)");
				for (GCUBEWSResource resource : expired) remove(resource);
				
			}
			
		}
	}
}