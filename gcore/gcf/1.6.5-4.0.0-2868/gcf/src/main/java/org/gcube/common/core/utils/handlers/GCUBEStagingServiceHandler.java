package org.gcube.common.core.utils.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.faults.GCUBEException;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.faults.GCUBERetryEquivalentException;
import org.gcube.common.core.faults.GCUBERetrySameException;
import org.gcube.common.core.faults.GCUBEUnrecoverableException;

/**
 * Abstract extension of {@link GCUBEStatefulServiceHandler} which extends its best-attempt strategy 
 * to the case in which instances of the target port-type can be created through factory port-types. 
 * The strategy extends {@link GCUBEStatefulServiceHandler}'s by:
 * <p>
 * 
 * (1) Discovering factories whenever no instance can be found of the target port-type;<br>
 * (2) Interacting with each factory to create an instance of the target port-type, until one instance has been created;<br>
 * (3) Interacting with the created instance of the target port-type.<p>  
 * 
 * To completely define strategy, subclasses must implement the abstract methods required by {@link GCUBEStatefulServiceHandler}
 * as well as the following abstract methods:<p>
 * 
 * (a) {@link #findFactories()} to discover factories;<br>
 * (b) {@link #createInstance(EndpointReferenceType)} to interact with a factory to create an instance of the target port-type;<p>
 *
 * @author Fabio Simeoni (University of Strathclyde)
 *
 * @param <CLIENT> the type of the handled object. It must implement the {@link GCUBEServiceClient} interface.
 */
public abstract class GCUBEStagingServiceHandler<CLIENT extends GCUBEServiceClient> extends GCUBEStatefulServiceHandler<CLIENT>{
	
	
	/**
	 * Creates an instance.
	 */
	public GCUBEStagingServiceHandler() {}
	
	/**
	 * Creates an instance to act on or on behalf of a service client.
	 * @param client the client.
	 */
	public GCUBEStagingServiceHandler(CLIENT client) {super(client);}
	
	
	/**
	 * Returns a non-empty list of EPRs to instances of the target port-type. The list contains either 
	 * all the instances returned by {@link #getInstances()} or, if none is returned, the single
	 * instance spawned as the result of the first successful interaction with a factory returned by 
	 * {@link #getFactories()}.
	 * 
	 * @return the list of EPRs.
	 * @throws Exception if no EPRs can be returned.
	 */
	protected List<EndpointReferenceType> getInstances() throws Exception {
		
		try{
			return super.getInstances();
		}
		catch (NoQueryResultException ignore) {//if it fails...
			List<EndpointReferenceType> eprs = this.getFactories();//get all factories
			EndpointReferenceType newEPR = this.tryFactories(eprs);//try to spawn an instance with at least one
			return Arrays.asList(newEPR); //return a singleton list with the EPR of the spawned instance			
		}
	}
	
	/**
	 * Interacts with a list of factories to spawn an instance of the target port-type.
	 * 
	 * @param eprs the EPRs of the factories.
	 * @return the EPR of the spawned instance.
	 * @throws Exception if no interaction is successful. 
	 */
	protected EndpointReferenceType tryFactories(List<EndpointReferenceType> eprs) throws Exception {
		
		List<EndpointReferenceType> busyEprs; //groups 'busy' instances here
		int attempts=0;//measures patience with busy instances
		Exception lastException=null;
		
		do {
			busyEprs = new ArrayList<EndpointReferenceType>();
			attempts++;
			for (EndpointReferenceType epr : eprs){
				try {
					logger.info("Trying factory of "+this.getTargetPortTypeName()+" @ "+epr);
					return this._createInstance(epr); 
				}
				//if instance says so, no point continuing
				catch (GCUBEUnrecoverableException e) {//unrecoverable faults stop the show
					throw e;
				}
				//if instance is busy, put in list and try later
				catch(GCUBERetrySameException e) {//put busy instance in waiting list
					lastException=e;
					logger.info("Trying again later @ "+epr.getAddress());
					busyEprs.add(epr);
				}
				//if explicitly indicated, move on silently
				catch (GCUBERetryEquivalentException ignoreSilent) {
					lastException=ignoreSilent;
					logger.error("Failed @ factory "+epr.getAddress()+((ignoreSilent.getMessage()==null)?"":":"+ignoreSilent.getMessage()));
				}
				//by default, move on too but loudly this time 
				catch (Exception ignoreLoud) {
					lastException=ignoreLoud;
					logger.error("Failed @ factory "+epr.getAddress(),ignoreLoud);
				}
			} 
			
			eprs = busyEprs;//repeat but with busy instances
			
		} while (!eprs.isEmpty() && attempts<this.getAttempts());
	
		//done our best...quitting
		throw lastException;
	}
	
	
	/**
	 * Returns a non-empty list of EPRs to factories.
	 * 
	 * 
	 * @return the list of EPRs.
	 * @throws NoQueryResultException if no factory can be found.
	 * @throws Exception if no EPR can be returned.
	 * 
	 */
	protected List<EndpointReferenceType> getFactories() throws Exception,NoQueryResultException {

		logger.info("Looking for factories of "+this.getTargetPortTypeName());
		List<EndpointReferenceType> factoryEprs = this.findFactories();
		if (factoryEprs == null || factoryEprs.size()==0) throw new NoQueryResultException();	
		return factoryEprs;
	}
	
	/**
	 * Used internally to wrap {@link #createInstance(EndpointReferenceType)} and handle its failures.<br> 
	 * If {@link #createInstance(EndpointReferenceType)} fails with a {@link GCUBEFault}, it propagates the
	 * {@link GCUBEException} which models the fault most specifically.<br>
	 *   
	 * @param epr the EPR of a factory.	 
	 * @throws Exception if no instance could be created.
	 * @see #createInstance(EndpointReferenceType)
	 */
	protected EndpointReferenceType _createInstance(EndpointReferenceType epr) throws Exception{
		logger.info("Creating instance of "+this.getTargetPortTypeName()+" @ "+epr.getAddress());
		try{
			return this.createInstance(epr);
		}
		catch (GCUBEFault f) {
			f.setFaultMessage("Error from factory of "+this.getTargetPortTypeName()+":"+f.getFaultMessage());
			throw f.toException();
		}
	}
	
	/**
	 * Returns a list of EPRs to factories.
	 * 
	 * @return the list of EPRs.
	 * @throws Exception if no EPR can be returned.
	 */
	protected abstract List<EndpointReferenceType> findFactories() throws Exception;

	/**
	 * Interacts with a factory to create an instance of the target port-type.
	 * 	 
	 * @param epr the EPR of the factory.
	 * @return the EPR of the instance.
	 * @throws Exception if the interaction does not complete successfully.
	 */
	protected abstract EndpointReferenceType createInstance(EndpointReferenceType epr) throws Exception;
}

