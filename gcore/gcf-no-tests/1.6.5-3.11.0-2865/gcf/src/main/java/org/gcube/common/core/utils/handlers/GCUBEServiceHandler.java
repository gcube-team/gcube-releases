package org.gcube.common.core.utils.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.faults.GCUBEException;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.faults.GCUBERetryEquivalentException;
import org.gcube.common.core.faults.GCUBERetrySameException;
import org.gcube.common.core.faults.GCUBEUnrecoverableException;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.handlers.lifetime.Lifetime;
import org.gcube.common.core.utils.handlers.lifetime.State.Done;
import org.gcube.common.core.utils.handlers.lifetime.State.Failed;
import org.gcube.common.core.utils.handlers.lifetime.State.Running;
import org.gcube.common.scope.api.ScopeProvider;

/**
 * Abstract extension of {@link GCUBEHandler} which implements a best-attempt strategy 
 * to interact with an instance of a port-type of a gCube service (the <em>target port-type</em>)
 * on behalf of the handled object (the <em>client</em>).
 * The strategy involves:<p>
 * 
 * (1) Interacting with an instance of the target port-type, if its endpoint reference was previously cached within the client;<br>
 * (2) Discovering suitable instances of the target port-type, if the cached instance fails or no such cache exists yet;<br>
 * (3) Interacting with each instance of the target port-type, until the interaction is successful;<br>
 * (4) Caching the endpoint reference of the instance of the target port-type with which interaction is successful;<p>
 * 
 * To completely define strategy,concrete  subclasses <em>must</em> implement the following abstract methods in accordance with
 * the specific semantics of the interaction:<p>
 * 
 * (a) {@link #findInstances()} to discover suitable instances of the target port-type;<br>
 * (b) {@link #interact(EndpointReferenceType)}</code> to interact with an instance of the target port-type;<p>
 * 
 * They <em>may</em> and normally <em>will</em> also wish to implement the following method:<p>
 * 
 * (c) {@link #getTargetPortTypeName()} to return the name of the target port-type for customised logging and
 * to grant EPR reuse across all the handler types that target the same port-type and operate on behalf of the
 * same client. If the client <em>is scope-sensitive</em> (i.e. returns a non-<code>null</code> scope), then sharing
 * is further scope-qualified.
 * 
 * </p>
 * 
 * Within the client, the cache of endpoint references of the target port-type is indexed by name of the port-type, 
 * so that the they can be shared by all {@link GCUBEServiceHandler GCUBEServiceHandlers} which 
 * - in different ways and for different purposes - interact with the same port-type on behalf of the same client. 
 * The cache is entirely managed by the {@link GCUBEServiceHandler}, but the client must implement the
 * {@link GCUBEServiceClient} interface (or else extend its base implementation {@link GCUBEServiceClientImpl}).<p> 
 * 
 * Before being cached, finally, endpoint references are also stored in the state of the {@link GCUBEServiceHandler} 
 * with the name <code>targetEPR</code>, so as to be shared asynchronously with other {@link GCUBEHandler GCUBEHandlers} in
 * the context of a {@link GCUBESequentialHandler}.</p>  
 *
 * @author Fabio Simeoni (University of Strathclyde)
 *
 * @param <CLIENT> the type of the handled object. It must implement the {@link GCUBEServiceClient} interface.
 */
public abstract class GCUBEServiceHandler<CLIENT extends GCUBEServiceClient> extends GCUBEHandler<CLIENT> implements Lifetime<CLIENT> {
	
	/** Name of state property for last successful EP. */
	private static final String TARGETEPR_NAME = "targetEPR";
	//by default, will try once more with instances which prove busy.
	private int attempts=2; 
	
	private final String DEFAULT_PT_NAME = this.getName();
	
	/**
	 * Creates an instance.
	 */
	public GCUBEServiceHandler() {}
	
	/**
	 * Creates an instance to act on or on behalf of a service client.
	 * @param client the client.
	 */
	public GCUBEServiceHandler(CLIENT client) {super(client);}
	
	/**{@inheritDoc}*/
	public void run() throws Exception {

		setState(Running.INSTANCE);
		try {
			//if needed, initialises cache on behalf of client;
			if (this.getHandled().getPortTypeMap()==null) 
				this.getHandled().setPortTypeMap(new HashMap<String,EndpointReferenceType>());
	
			
			EndpointReferenceType epr = this.getCachedEPR();
						
			if (epr!=null) {//if any, use cached instance first
				try {
					logger.info("using cached instance of "+this.getTargetPortTypeName()+"@ "+epr.getAddress());
					this._interact(epr);
					setState(Done.INSTANCE);
					return;//done
				}
				//if instance says so, no point continuing
				catch (GCUBEUnrecoverableException e) {throw e;}
				//in all other cases, follow best-effort strategy
				catch(Exception ignore) {
					logger.warn("failed @ instance "+epr.getAddress(),ignore);
					//NB: we treat all exceptions as we would GCUBERetryEquivalentFault: basically ignore them. 
					//This applies also to GCUBERetrySameFault: better try with others than wait on this single one. At worse, 
					//we will encounter it again and wait then.				
				}	
			}
			

			List<EndpointReferenceType> eprs = this.getInstances();//get all instances
			EndpointReferenceType successfulEpr = this.tryInstances(eprs);//try to interact with at least one
			this.cacheEPR(successfulEpr);//if we go this far, cache successful instance
			setState(Done.INSTANCE);
						
		}	
		//something went wrong
		catch (Exception e) {
			//if there was something in cache it did not work, clear it
			this.clearCachedEPR();
			setState(Failed.INSTANCE);//notify monitors
			//NB: best-effort strategy gives strong warranty
			//this service is going to fail everywhere in VRE.
			//It's a tradeoff: may prove wrong, but will more 
			//likely save potentially large resource consumption
			throw e;
		}
	}


	/**
	 * Returns a non-empty list of EPRs to suitable instances of the target port-type.
	 * 
	 * 
	 * @return the EPRs.
	 * @throws Exception if the EPRs cannot be returned.
	 * @throws NoQueryResultException if suitable instances cannot be found.
	 */
	protected List<EndpointReferenceType> getInstances() throws NoQueryResultException, Exception {
		logger.info("looking for instances of "+this.getTargetPortTypeName());
		List<EndpointReferenceType> eprs = this.findInstances();
		if (this.getCachedEPR()!=null){//no point in trying cached epr again (if we are here it's because it has failed.
			EndpointReferenceType duplicate=null;
			for (EndpointReferenceType epr : eprs) if (epr.getAddress().equals(this.getCachedEPR().getAddress())) duplicate=epr;
			eprs.remove(duplicate);
		}
		if (eprs == null || eprs.size()==0) {throw new NoQueryResultException();}
		return eprs;
	}
	
	
	/**
	 * Used internally to wrap {@link #interact(EndpointReferenceType)} and handle its successes and failures.
	 * <br> 
	 * If {@link #interact(EndpointReferenceType)} succeeds, it store the input EPR 
	 * in the handler's state. If {@link #interact(EndpointReferenceType)} fails with a {@link GCUBEFault}, 
	 * it propagates the {@link GCUBEException} which models it most specifically.
	 *   
	 * @param epr the EPR of an instance of the target port-type.	 
	 * @throws Exception if the interaction fails to complete successfully.
	 * @see #interact(EndpointReferenceType).
	 */
	protected void _interact(EndpointReferenceType epr) throws Exception{
		try{
			this.interact(epr);
			//shares EPR in state for asynchronous sharing with other handlers.
			this.getBlackboard().put(TARGETEPR_NAME, epr);
		}
		catch (GCUBEFault f) {
			f.setFaultMessage("error from "+this.getTargetPortTypeName()+":"+f.toString());
			throw f.toException();
		}
	}
	
	/**
	 * Interacts with a list of instances of the target port-type, until one is successful.
	 * 
	 * @param eprs the instance EPRs.
	 * @return the EPR of the successful instance.
	 * @throws Exception if no interactions completes successfully.
	 */
	protected EndpointReferenceType tryInstances(List<EndpointReferenceType> eprs) throws Exception {
		
				
		List<EndpointReferenceType> busyEprs; //groups 'busy' instances
		int attempts=0;//measures 'patience' with busy instances
		Exception lastException=null;
		
		do {
			busyEprs = new ArrayList<EndpointReferenceType>();//initialises busy list
			attempts++;
			for (EndpointReferenceType epr : eprs) {
				if (epr==null) continue; //sanity check
				try {
					logger.info("trying instance of "+this.getTargetPortTypeName()+" @ "+epr.getAddress());
					this._interact(epr);	
					return epr;//if we made this far, it's done
				}
				//if instance says so, no point continuing
				catch (GCUBEUnrecoverableException e) {
					throw e;
				}
				//if instance is busy, put in list and try later
				catch(GCUBERetrySameException e) {
					lastException=e;
					logger.info("trying again later @ "+epr.getAddress());
					busyEprs.add(epr);
				}
				//if explicitly indicated, move on silently
				catch (GCUBERetryEquivalentException ignoreSilent) {
					lastException=ignoreSilent;
					logger.warn("instance "+epr.getAddress()+" says "+((ignoreSilent.getMessage()==null)?"":":"+ignoreSilent.getMessage()));
				}
				//by default, move on too but loudly this time 
				catch (Exception ignoreLoud) {
					lastException=ignoreLoud;
					logger.error("failed @ instance "+epr.getAddress(),ignoreLoud);
				}
			}
			eprs = busyEprs;//try busy instances now
		//but only if there are any && we have not lost patience
		} while (!eprs.isEmpty() && attempts<this.getAttempts());

		//done our best...quitting with last exception encountered
		throw lastException;	
		
	}

	/**
	 * Returns the number of times the handler tries to interact with
	 * instances of the target port-type which prove busy.
	 * 
	 * @return the number of attempts.
	 */
	public int getAttempts() {
		return this.attempts;
	}

	/**
	 * Sets how many times the handler should try to interact with
	 * instances of the target port-type which prove busy.
	 *  
	 * @param attempts the number of attempts.
	 */
	public void setAttempts(int attempts) {this.attempts = attempts;}
	
	/**
	 *  Clears the EPR of the instance of the target port-type which was last cached.
	 */
	protected void clearCachedEPR() {this.getHandled().getPortTypeMap().put(this.getCacheKey(),null);}
	

	/**
	 * Caches the EPR of an instance of the target port-type.
	 * @param epr the EPR.
	 */
	protected void cacheEPR(EndpointReferenceType epr) {
		logger.trace("remembering last useful instance of "+this.getTargetPortTypeName());
		this.getHandled().getPortTypeMap().put(this.getCacheKey(),epr);
	}
	

	/**
	 * Returns the EPR of the instance of the target port-type which was last cached.
	 * @return the EPR.
	 */
	protected EndpointReferenceType getCachedEPR() {
		return this.getHandled().getPortTypeMap().get(this.getCacheKey());		
	}

	/**
	 * Returns a key to store 'successful' endpoints of the target port-type in the cache provided by the service client.
	 * <p>
	 * By default, the key is derived from the name of the port-type (cf. {@link #getTargetPortTypeName()} so
	 * as to reuse across endpoints across all handler types that target the same port-type and share the same client.
	 * If the client is scope-sensitive, however, the key is further scope-qualified.
	 * 
	 * @see #getTargetPortTypeName()
	 * @return the key.
	 */
	protected String getCacheKey() {
		String name = this.getTargetPortTypeName();
		GCUBEScope scope = null;
		if (getScopeManager()!=null) scope = getScopeManager().getScope();
		else if (this.getHandled().getScope()!=null) scope= this.getHandled().getScope();
		return scope==null?name:name+scope.toString();
	}
	
	/**
	 * Returns the local name of the target port-type as the basis for a key to store 'successful' 
	 * EPRs of the port-type in the cache provided by the service client (cf. {@link #getCacheKey()}). 
	 * <p>
	 * By default, it returns the name of handler (cf. {@link #getName()}), which will <em>not</em> grant EPR reuse
	 * across any other handler type that may target the same port-type. Override and return a specific port-type name 
	 * to grant EPR reuse across all handler types that target the same port-type and share the same client.
	 * 
	 * @see {@link #getName()} 
	 * @see {@link #getCacheKey()}.
	 * @return the name.
	 **/
	protected String getTargetPortTypeName() {return DEFAULT_PT_NAME;}
	

	/**
	 * Discovers EPRs to suitable instances of the target port-type.
	 * 
	 * @return the EPRs.
	 * @throws Exception if suitable instances cannot be found.
	 */
	protected abstract List<EndpointReferenceType> findInstances() throws Exception;

	
	/**
	 * Interacts with an instance of the target port-type.
	 * 
	 * @param epr the EPR of the instance.	 
	 * @throws Exception if the interaction does not complete successfully.
	 */
	protected abstract void interact(EndpointReferenceType epr) throws Exception;
	
	
	/**Characterises empty query results.*/
	public static class NoQueryResultException extends Exception {
		/**Default serialisation ID.*/
		private static final long serialVersionUID = 1L;} 
}
