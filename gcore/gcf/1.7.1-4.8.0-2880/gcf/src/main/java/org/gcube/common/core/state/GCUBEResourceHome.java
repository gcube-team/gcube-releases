package org.gcube.common.core.state;

import static org.gcube.common.core.contexts.GCUBEServiceContext.Status.FAILED;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.map.ReferenceMap;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEServiceContext.RILifetimeEvent;
import org.gcube.common.core.contexts.service.Consumer;
import org.gcube.common.core.persistence.GCUBENoPersistenceDelegate;
import org.gcube.common.core.persistence.GCUBEPersistenceDelegate;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.globus.wsrf.InvalidResourceKeyException;
import org.globus.wsrf.NoSuchResourceException;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.jndi.Initializable;
import org.globus.wsrf.utils.cache.LRUCache;



/**
 * Partial implementation of managers of {@link GCUBEStatefulResource GCUBEStatefulResources}.
 * <p>
 * It creates, retrieves, and removes resources parametrically with respect to their type. 
 *
 * Resource management can occur in either one of four different modes:<p>
 * 
 * <code>TRANSIENT</code>: resources are maintained exclusively in memory (default mode);<br>
 * 
 * <code>HARDPERSISTENT</code>: resources can be serialised to storage by configuration of a 
 * {@link GCUBEPersistenceDelegate}. At the reboot of the container
 * all resource serialisations are transparently restored to memory.<br>
 * 
 * <code>CACHEDPERSISTENT</code>: resources can be serialised to storage by configuration of a 
 * {@link GCUBEPersistenceDelegate} and remain in memory for a time
 * that is proportional to the frequency of usage <em>and</em> and the amount of available memory.
 * Resources that are not found in memory but exist on storage are transparently restored to memory.<br>
 * 
 * <code>SOFTPERSISTENT</code>: resources can be serialised to storage 
 * by configuration of a {@link GCUBEPersistenceDelegate} and remain in memory for a time
 * that is proportional to the amount of available memory.
 * Resources that are not found in memory but exist on storage are transparently restored to memory.
 * 
 * <p>
 * 
 * The four modes above reflect different trade-offs between the efficiency of resource access (which decreases
 * from the first to the fourth) and the efficiency of in-memory storage (which increases from the first to
 * the fourth). In all cases, the mode of operation is derived from configuration (cf. {@link #onInitialisation()}).

 * 
 * @param <RESOURCE> the type of {@link GCUBEStatefulResource}.
 * @param <RESOURCEID> the type of the {@link GCUBEStatefulResource} identifier.
 * @param <LEGACYID> the legacy identifier type. Used to retrofit the design to Globus technology while insulating clients from it.
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public abstract class GCUBEResourceHome<LEGACYID,RESOURCEID extends LEGACYID,RESOURCE extends GCUBEStatefulResource<RESOURCEID>> implements Initializable {

	//Note on the parametrisation: we use the LEGACYID parameter to align GCUBEWSHome subclass with Globus technology without exposing clients to it.
	
	/**Instance logger.*/
    protected final GCUBELog logger=new GCUBELog(this);
	
    /** Marks the initialisation of the home. */
  	private boolean initialized;

    /** The class of the managed resources. */
 	protected volatile Class<RESOURCE> resourceClass;
 	
    /** The class of the persistence delegate for the managed resources. */
 	protected volatile GCUBEPersistenceDelegate<RESOURCEID,RESOURCE> persistenceDelegate;
   	
 	/** Enumerates management modes. */
   	protected enum Mode {TRANSIENT,SOFTPERSISTENT,CACHEDPERSISTENT,HARDPERSISTENT};
   	
   	/** The management mode. */
   	protected volatile Mode mode = Mode.TRANSIENT;
    
    /** Undefined memory timeout for resources. */
   	private final int UNDEFINED_TIMEOUT=-1;
   	
    /** Infinite memory timeout for resources. */
   	private final int INFINITE_TIMEOUT=0;

   	/** The timeout of the resource cache, if any. */
   	private volatile long cacheTimeout=UNDEFINED_TIMEOUT;

    /** The managed resources, indexed by key. */
    private volatile Map<RESOURCEID,RESOURCE> resources;

    /** Memory cache for persistent resources. */
   	protected volatile LRUCache cache;

    /** A manager of locks on resources identifiers. */
    protected final LockManager lockManager=new LockManager();
    
    /** List of identifiers of resources created in the home's lifetime. */
	protected final List<RESOURCEID> resourceLog=Collections.synchronizedList(new ArrayList<RESOURCEID>());


    /**
     * Invoked by the container at startup, it subscribes a {@link GCUBEResourceHome.HomeConsumer} to lifetime events
     * of the Running Instance.
     *
     * @throws Exception if initialisation fails.
     */
    public final synchronized void initialize() throws Exception {
    	
    	//NOTE: this is dangerous code: invoking container holds lock to JNDI configuration but does not 
    	// cooperate with gCore threads which need to access it. if we try here to acquire locks they already, 
    	//it's deadlock. So, no calls to GHN context and minimal interaction with service context. 
    	
    	//single invocation allowed (from container) and won't proceed if RI has failed already.
    	if (this.initialized || this.getServiceContext().getStatus()==FAILED) return;
    	
		//subscribes asynchronous initialiser RI lifetime events.
        this.getServiceContext().subscribeLifetTime(new HomeConsumer());
        
    	this.initialized = true;//marks initialisation to prevent later invocations.
    }
    
    /**
     * Sets the name of the class of the resources managed by the home.
     * @param clazz the class name.
     * @throws ClassNotFoundException if the class name could not be resolved.
     */
    public synchronized void setResourceClass(String clazz) throws ClassNotFoundException {
    	//subclasses will perform dynamic typechecks, we dont know type parameter 
    	if (this.resourceClass==null) {
    		//assume home implementation and resource ship together hence uses home's classloader
    		Class<?> resourceClass = getClass().getClassLoader().loadClass(clazz);
    		this.resourceClass = (Class) resourceClass; 
    	}
       	else throw new RuntimeException("resource class already configured");//cannot change this configuration
    }
    
    /**
     * Returns the class of the resources managed by the home.
     * @return the class.
     * */
    public synchronized String getResourceClass() {
    	return resourceClass.getName();
    }
    
    /**
     * Sets the name of the class of the persistence delegate for the resources managed by the home.
     * @param clazz the class name.
     * @throws ClassNotFoundException if the class name could not be resolved.
     */
    public synchronized void setPersistenceDelegateClass(String clazz) throws Exception {
    	if (persistenceDelegate==null) { //actual runtime checks performed in #initialise(), in subclasses;
    	 	Class<?> delegateClass = getClass().getClassLoader().loadClass(clazz);
    		persistenceDelegate = (GCUBEPersistenceDelegate)  delegateClass.newInstance();
    	}
    	else throw new RuntimeException("persistence delegate already configured");
    }

    /**
     * Sets the timeout in milliseconds of the cache used by the home.
     * @param timeout the timeout.
     */
    public synchronized void setCacheTimeout(long timeout) {
    	if (this.cacheTimeout==UNDEFINED_TIMEOUT) this.cacheTimeout = timeout;
    	else throw new RuntimeException("cache timeout already configured");
    }
    
	/**
	 * Returns the associated {@link GCUBEServiceContext}.
	 * @return the service context.
	 */
	public abstract GCUBEServiceContext getServiceContext();
	
    
 	///////////////////----------------------LIFETIME MANAGEMENT----------------------/////////////////////////                       
    ///                                                                                                      //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * A {@link Consumer} that dispatches RI lifetime events to callbacks that my be overridden in
	 * accordance with service semantics.<p>
	 * @author Fabio Simeoni (University of Strathclyde)
     */
	private class HomeConsumer extends Consumer {
		/**Creates a new instance.*/
		HomeConsumer() {this.consumerLogger=logger;}//contextualise generic consumer logger
		/**{@inheritDoc}*/
		@Override protected void onRIInitialised(RILifetimeEvent event) throws Exception {
			super.onRIInitialised(event);
			logger.setContext(getServiceContext());//customises logger to log on behalf of service
			logger.trace("INITIALISING "+GCUBEResourceHome.this.getClass().getSimpleName().toUpperCase());
			onInitialisation();
		}

		/**{@inheritDoc} */ 
		@Override protected void onRIReady(RILifetimeEvent event) throws Exception {super.onRIReady(event);onReady();}
		/**{@inheritDoc} */
		@Override protected void onRIStateChange(RILifetimeEvent event) throws Exception {super.onRIStateChange(event);onStateChange();}
		/**{@inheritDoc} */
		@Override protected void onRIUpdated(RILifetimeEvent event) throws Exception {super.onRIUpdated(event);onUpdate();}		
		/**{@inheritDoc}*/
		@Override protected void onRIFailed(RILifetimeEvent event) throws Exception {super.onRIFailed(event);onFailure();}
	}
	
    
    /** 
     * Invoked during initialisation of the Running Instance to initialise the home. 
     * 
     * Most of the initialisation occurs from JNDI configuration.
     * Subclasses must define a configuration template which extends the following
     * (text in italics marks points of instantiations):</p>
	 * 
	 * <code>
	 * &lt;resource name="[<em>name</em>]" type="[<em>FQN of a subclass of {@link GCUBEResourceHome}</em>]"&gt;<br>
	 *    &nbsp;&nbsp;&lt;resourceParams&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name&gt;factory&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>org.globus.wsrf.jndi.BeanFactory&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name>resourceClass&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>[<em>FQN of a concrete subclass of {@link GCUBEStatefulResource}</em>]&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *	&nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name>persistenceDelegateClass&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>[<em>FQN of a concrete subclass of {@link GCUBEPersistenceDelegate}</em>]&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *	 &nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name>cacheTimeout&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>[<em>seconds</em>]&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *	&nbsp;&nbsp;&lt;/resourceParams&gt;<br>
	 *&lt;/resource&gt;
	 *</code>
	 *         
	 *<p>
	 *
	 *where: <p>
	 *-) the <code>name</code> of the JNDI resource and its <code>resourceClass</code> parameter may be further
	 *constrained by subclasses.<br>
	 *-) <code>persistenceDelegateClass</code> and <code>cacheTimeout</code> are optional and related.
	 *If neither is present, the mode of operation is <code>TRANSIENT</code>.
	 *If the first is present but the second is absent, then the mode of operation is <code>SOFTPERSISTENT</code>.
	 *If both are present, then the mode of operation is
	 *<code>HARDPERSISTENT</code> if the second has a value of <code>0</code> and <code>CACHEDPERSISTENT</code>
	 *in all other cases. 
	 *Finally, the second cannot occur without the first. 
	 *
	 * @throws Exception if the callback did not complete successfully (causes service failure).*/ 
	protected void onInitialisation() throws Exception 
	{
		
		//must have a resource class to work with
        if (resourceClass==null) throw new Exception("resource class is not configured");
        
        //derive mode of operation from configuration
        if (this.persistenceDelegate==null) {
        	if (cacheTimeout!=UNDEFINED_TIMEOUT) throw new Exception("cache is configured but persistence delegate is not");
        	persistenceDelegate = new GCUBENoPersistenceDelegate(); //persistent resources?	
        } else if (cacheTimeout == UNDEFINED_TIMEOUT) 	mode = Mode.SOFTPERSISTENT;//by default, they are not always kept in memory
        	else if (cacheTimeout==INFINITE_TIMEOUT) 	mode = Mode.HARDPERSISTENT;//unless efficient access is a stringent requirement
        		 else 									mode = Mode.CACHEDPERSISTENT; //compromise: retain the most recently used ones 
        
        //initialise persistence delegate
	 	persistenceDelegate.initialise(this);
	 	
        logger.info("managing resources in "+mode+" mode");
        
        //the mode impacts first of all on choice of data structures
        if (mode==Mode.TRANSIENT || mode == Mode.HARDPERSISTENT) resources = new HashMap<RESOURCEID,RESOURCE>();
        else {
        	resources = new ReferenceMap(ReferenceMap.HARD,ReferenceMap.SOFT,true);
            if (mode == Mode.CACHEDPERSISTENT) {
            	cache = new LRUCache();
                cache.setTimeout(cacheTimeout);
                cache.initialize();//simulates globus initialisation
                
            }
        }        
        //whatever the data structure, it has to be internally thread-safe
        resources = Collections.synchronizedMap(resources);
        //but must make sure to use it thread-safely: iterations or if-then-put's only by locking on the map itself 


        //deserialise what persisted before the last boot, if any
        if (mode!=Mode.TRANSIENT)     
        	for (RESOURCEID id : this.persistenceDelegate.getResourceIdentifiers()) //first-time load from disk after from memory
        		try {this.find(id);} catch(Exception e){logger.warn("could not first-load resource "+id,e);}
	}
	

	
	/** Invoked when the Running Instance is ready to operate. If needed, override in accordance with service semantics.
	 * @throws Exception if the callback did not complete successfully and the service ought to fail as a result.*/ 
	protected void onReady() throws Exception {}
	/** Invoked when the Runnning Instance is updated. If needed, override in accordance with service semantics.
	 * @throws Exception if the callback did not complete successfully and the service ought to fail as a result.*/ 
	protected void onUpdate() throws Exception {}; 
	/** Invoked upon a change to the RI's stateful resources, if any. If needed, override in accordance with service semantics.
	 * @throws Exception if the callback did not complete successfully and the service ought to fail as a result.*/ 
	protected void onStateChange() throws Exception {};
	/** Invoked when the Running Instance fails. If needed, override in accordance with service semantics.
	 * @throws Exception if the callback did not complete successfully and the service ought to fail as a result.*/ 
	protected void onFailure() throws Exception {} 
	
	
	///////////////////---------------------LOW-LEVEL OPERATIONS---------------------//////////////////////////                       
    ///                                                                                                      //
    // These operations maintain structures and, where appropriate interact with the persistence delegate    //
	// to abstract away storage. They assume that acquiring threads have been synchronised by high-level     //
	// operations but synchronise with owning threads where appropriate.                                     //
	//                                                                                                       //
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
    /**
     * Used internally to obtain an uninitialised resource.
     * @return the resource.
     * @throws ResourceException if the resource could not be created.
     */
    protected RESOURCE newInstance() throws ResourceException {//wraps generic exception in one place
    	try {return this.resourceClass.newInstance();} 
		catch (Exception e) {throw new ResourceException(e);}
    }
    
	/**
	 * Adds a resource to the home.
	 * @param resource the resource.
	 */
	protected void add(RESOURCE resource) {
		synchronized (this.resources) {//make these two steps atomic
			this.resources.put(resource.getID(),resource);
            if (!this.resourceLog.contains(resource.getID())) this.resourceLog.add(resource.getID());
            if (mode==Mode.CACHEDPERSISTENT) this.cache.update(resource);	
		}
    }

	/**
	 * Returns a resource from its identifier, deserialising it from storage if it is in a persistent
	 * state. 
	 * <p>
	 * It is invoked by {@link #find(Object) find(id)} and {@link #remove(Object) remove(id)} 
	 * after they have acquired a reentrant lock on the resource identifier.
	 * 
	 * @param id the identifier.
	 * @return the resource.
	 * @throws ResourceException if the resource cannot be found.
	 * @see #find(Object)
	 * @see #remove(Object)
	 */
    protected RESOURCE get(LEGACYID id) throws ResourceException, NoSuchResourceException {//legacy signature 
    	
    	RESOURCE resource = this.resources.get(id);
    	
    	if (resource == null) {//not in memory
            if (mode==Mode.TRANSIENT) throw new NoSuchResourceException();
            //maybe stored persistently?
            resource = newInstance();//create an empty shell
            this.preInitialise(resource);//give subclasses a say before loading
            resource.setID((RESOURCEID)id);//we know this will work as legacy identifiers have been previously converted
            boolean firstLoad = !this.resourceLog.contains(id);
            try {persistenceDelegate.load(resource,firstLoad);}
            catch (NoSuchResourceException e) {throw e;}
            catch(Exception e) {throw new ResourceException(e);}
            this.onLoad(resource,firstLoad);//give subclasses a say after loading
            this.add(resource);
        } else if (mode==Mode.CACHEDPERSISTENT) this.cache.update(resource);
        
        return resource;
    }
    
	/**
	 * Removes a resource from the home.
	 * @param resource the resource.
	 */
	protected void remove(RESOURCE resource) throws ResourceException {
		
		GCUBEReadWriteLock.GCUBEWriteLock lock = resource.getLock().writeLock();
		//synchronise with owning threads
		try {lock.lockPreemptively();} catch(InterruptedException e) {throw new NoSuchResourceException();}
		try{
			if (this.onRemove(resource)) {//if subclass says to go ahead...
				resource.onRemove();
				this.resources.remove(resource.getID());//cleanup memory
				this.resourceLog.remove(resource.getID());
				if (mode==Mode.CACHEDPERSISTENT) this.cache.remove(resource);//cleanup cache
				this.persistenceDelegate.remove(resource);//cleanup storage
			}
			else lock.cancelPreemptive();//resource still alive, cancel preempted flag;
		}
    	catch (Exception e) {lock.cancelPreemptive();throw new ResourceException("could not remove resource "+resource.getID(),e);}
    	finally{lock.unlock();}
    }
	
    /**
    * Returns the resources that are held in memory at the point of invocation.
    * @return the resources.
    */
    protected Collection<? extends RESOURCE> getResources() {
    	synchronized (this.resources) {//freeze map for copying purposes (copy hides iteration)
    		return new HashSet<RESOURCE>(this.resources.values());//set is now separated from map
		}
    	
    }
    
 	/**
 	 * Returns the identifiers of all resources ever created by the home. 
 	 * @return the identifiers.
 	 */
     public Collection<? extends RESOURCEID> getIdentifiers() {
     	if (mode==Mode.TRANSIENT || mode==Mode.HARDPERSISTENT) {
     	   	synchronized (this.resources) {//freeze map for copying purposes (copy hides iteration)
         		return new HashSet<RESOURCEID>(this.resources.keySet());//set is now separated from map
     		}
     	}
     	else return persistenceDelegate.getResourceIdentifiers();//delegate otherwise
     }
     

 	///////////////////---------------------HIGH-LEVEL OPERATIONS---------------------/////////////////////////                       
    ///                                                                                                      //
    // These operations interface acquiring threads and synchronise low-level operations where appropriate.  //
 	//                                                                                                       //
 	///////////////////////////////////////////////////////////////////////////////////////////////////////////

     
     /**
 	 * Creates a resource from a set of initialisation parameters. 
 	 * 
 	 * @see #create(Object, Object...)
 	 * @param params (optional) the initialisation parameters.
 	 * @return the resource.
 	 * @throws ResourceException if the resource could neither be reused or created.
 	 */
 	public RESOURCE create(Object ... params) throws ResourceException {
 		return createInternal(null,params);
 	}
 	
    /**
	 * Returns a resource with a given identifier from a set of initialisation parameters,
	 * creating it if it does not exist. 
	 * 
	 * @see #onReuse(GCUBEStatefulResource)
	 * @see #reuse(Object, Object...)
	 * @param id the identifier, or <code>null</code>.
	 * @param params (optional) the initialisation parameters.
	 * @return the resource.
	 * @throws ResourceException if the resource could neither be reused or created.
	 */
	public RESOURCE create(RESOURCEID id, Object ... params) throws ResourceException {    
		if (id==null) throw new ResourceException("identifier is missing");
		RESOURCE resource;	
		try {
			resource = this.reuse(id,params);//is there one already with this identifier?	 	    
			this.onReuse(resource);//give subclasses a say on what to do upon reuse
		}
		catch(NoSuchResourceException e){resource = createInternal(id,params);}
		return resource;
	}
	
	/**
	 * Returns a resource with a given identifier.
	 * @param id the identifier.
	 * @param params (optional) the initialisation parameters.
	 * @return the resource.
	 * @throws ResourceException if the resource could not be reused.
	 */
	protected RESOURCE reuse(RESOURCEID id, Object ... params) throws ResourceException {
		RESOURCE resource = null;	
		ReentrantLock lock = this.lockManager.getLock(id);
		try {lock.lock();resource = this.get(id);} finally {lock.unlock();}
        return resource;
	}
	
	/**
	 * Used internally to create a resource from an identifier and a set of initialisation parameters.
	 * @param id the identifier, or <code>null</code>.
	 * @param params (optional) the initialisation parameters.
	 * @return the resource.
	 * @throws ResourceException if the resource could not be created.
	 */
	protected RESOURCE createInternal(RESOURCEID id, Object ... params) throws ResourceException {
		logger.info("creating a "+this.resourceClass.getSimpleName());
    	RESOURCE resource = this.newInstance();
		this.preInitialise(resource);//give subclasses a say before initialisation
		try {resource.initialise(id,params);}
		catch(Exception e) {throw new ResourceException(e);}
		this.postInitialise(resource);//give subclasses a say after initialisation
		this.add(resource);
		return resource;
	}
    

  	/**
	 * Returns a resource from its identifier.
	 * 
	 * @param id the identifier.
	 * @return the resource.
	 * @throws ResourceException if no resource with the given identifier can be found.
	 */
    public RESOURCE find(LEGACYID id) throws ResourceException, NoSuchResourceException {//legacy signature
 
    	if (id == null) throw new ResourceException("resource identifier is missing");
    	ReentrantLock lock = this.lockManager.getLock(id);
        try {lock.lock();return this.get(id);} finally {lock.unlock();}
    }
    
	
    /**
     * Removes a resource with a given identifier.
     *
     * @param id the identifier.
     * @throws NoSuchResourceException if no resource exists with the given identifier.
     * @throws InvalidResourceKeyException if the identifier is invalid.
     * @throws ResourceException if the resource exists but could not be removed.
     */
    public void remove(LEGACYID id) throws ResourceException {//legacy signature
        
    	if (id == null) throw new InvalidResourceKeyException("resource identifier is missing");
    	ReentrantLock lock = this.lockManager.getLock(id);
    	try{lock.lock();this.remove(this.get(id));} finally {lock.unlock();}
    }

 	///////////////////---------------------------CALLBACKS---------------------------/////////////////////////                       
    ///                                                                                                      //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    
	 /**
     * Invoked during the creation of a resource and before its initialisation.
     * <p>
     * If overriding this method, please note that the resource may <em>not</em> have been assigned an identifier
     * yet.
     * 
     * @param resource the resource.
     * @throws ResourceException if the resource could not be pre-initialised.
     */
	protected void preInitialise(RESOURCE resource) throws ResourceException{
		//logger.trace("pre-initialising "+this.resourceClass.getSimpleName());
		try {resource.setPersistenceDelegate(this.persistenceDelegate);}
		catch(Exception e) {throw new ResourceException(e);}
	};
	
    /**
     * Invoked during the creation of a resource and after its initialisation.
     * 
     * @param resource the resource.
     * @throws ResourceException if the resource could not be post-initialised.
     */
    protected void postInitialise(RESOURCE resource) throws ResourceException {
    	logger.trace("post-initialising "+this.resourceClass.getSimpleName()+"("+resource.getID()+")");
    }
	
    /**
     * Invoked when a resource is reused across two or more calls to {@link #create(Object, Object...)} .
     * 
     * @param resource the resource.
     * @throws ResourceException if the resource could not be reused.
     */
    protected void onReuse(RESOURCE resource) throws ResourceException {
	  	logger.info("reusing a "+this.resourceClass.getSimpleName()+"("+resource.getID()+")");
    }  
    
    /**
     * Invoked after successfully loading a resource from storage.
     * 
     * @param resource the resource.
     * @param firstLoad <code>true</code> if this is the first time the resource is loaded since the last container reboot.
     * @throws ResourceException if the resource could not loaded.
     */
    protected void onLoad(RESOURCE resource, boolean firstLoad) throws ResourceException {}  
    
    /**
     * Invoked as a precondition to the removal of a resource.
     * 
     * @param resource the resource.
     * @throws ResourceException if the precondition could not be verified.
     */
    protected boolean onRemove(RESOURCE resource) throws ResourceException{
      	logger.info("removing a "+this.resourceClass.getSimpleName()+"("+resource.getID()+")");
    	return true;
    }

 	///////////////////------------------------LOCK MANAGEMENT------------------------/////////////////////////                       
    ///                                                                                                      //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    /**
     * A distributor of locks used internally to synchronise acquiring threads.
     * @author Fabio Simeoni (University of Strathclyde)
     *
     */
    class LockManager {
    	
    	/** Lock pool. */
    	Map<LEGACYID,ReentrantLock> locks = new ReferenceMap(ReferenceMap.HARD,ReferenceMap.SOFT,true);
    	
    	/**
    	 * Returns a shared lock for a given key.
    	 * @param key the key.
    	 * @return the lock.
    	 */
    	public synchronized ReentrantLock getLock(LEGACYID key) {
    		ReentrantLock lock = locks.get(key);
    		if (lock==null) {
    			lock = new ReentrantLock();
    			locks.put(key,lock);
    		}
    		return lock;
    	}
    	
    }
    
}