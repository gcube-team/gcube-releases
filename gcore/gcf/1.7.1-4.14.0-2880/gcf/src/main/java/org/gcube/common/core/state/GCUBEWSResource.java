package org.gcube.common.core.state;

import java.util.Arrays;
import java.util.Calendar;

import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.contexts.GHNContext.Mode;
import org.gcube.common.core.informationsystem.notifier.ISNotifier;
import org.gcube.common.core.informationsystem.publisher.ISPublisher;
import org.gcube.common.core.scope.GCUBEScope;
import org.globus.wsrf.InvalidResourceKeyException;
import org.globus.wsrf.NoSuchResourceException;
import org.globus.wsrf.PersistenceCallback;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.ResourceLifetime;
import org.globus.wsrf.ResourceProperties;
import org.globus.wsrf.ResourceProperty;
import org.globus.wsrf.TopicListAccessor;
import org.globus.wsrf.impl.ResourcePropertyTopic;
import org.globus.wsrf.impl.SimpleResourceProperty;
import org.globus.wsrf.utils.AddressingUtils;

/**
 * An abstract specialisation of {@link GCUBEStatefulResource} for stateful entities that 
 * are accessible to remote clients through the interface of a service port-type.
 * <p>
 * 
 * In particular, a WS-Resource:<p>
 * 
 * -) has a public identifier comprised of its local identifier and the endpoint of the associated port-type.<br>
 * 
 * -) exposes (part of) its state to remote client as multi-valued Resource Properties (RPs). These public properties are 
 * collected in a {@link GCUBEWSResourcePropertySet} and documented in the interface definition of the associated
 * port-type. <br>
 * 
 * -) notifies clients of events about one or more topics, particularly the change of RP values.<br>
 * 
 * -) may have a finite lifetime though its lifetime may be dynamically extended.<br>
 * 
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 */
public abstract class GCUBEWSResource extends GCUBEStatefulResource<GCUBEWSResourceKey> implements PersistenceCallback, ResourceLifetime, ResourceProperties, TopicListAccessor  {
	
	/** The Property Set (RP) of the resource. */
	private GCUBEWSResourcePropertySet propSet;
	
	/** The topic list of the resource. */
	private GCUBETopicList topicList;

	/** The port-type associated with the resource.*/
	private GCUBEStatefulPortTypeContext portTypeContext;
			
	/**
	 * Initialises the internal data structures of the resource. 
	 * <p>
	 * If overridden, it should be invoked to add RPs and topics. 
	 * @throws ResourceException if the structures could not be initialised.
	 */
	protected void initialiseContainers() throws Exception {	

		try {
			propSet = new GCUBEWSResourcePropertySet(this);
			topicList = new GCUBETopicList(this);
		} catch (Exception e) {throw new ResourceException(e);}
		
		//creates and loads resource properties
		for (String name : this.getPropertyNames()) {
			ResourceProperty prop = this.getProperty(name);
			this.getResourcePropertySet().add(prop);
		}
		
		//creates and loads resource properties topic
		for (String name : this.getTopicNames()) {
			ResourceProperty prop = this.getProperty(name);
			ResourcePropertyTopic topic = new ResourcePropertyTopic(prop);
			topic.setSendOldValue(false);
			this.getTopicList().addTopic(topic);
			this.getResourcePropertySet().add(topic);
		}
 	}	
	
	/**{@inheritDoc}*/
	protected void initialise(GCUBEWSResourceKey id, Object ... params) throws Exception {

		this.setID(id!=null?id:this.getPorttypeContext().makeKey(uuidGen.nextUUID()));	
		logger.trace("initialising "+this.getClass().getSimpleName()+"("+this.getID()+")");
		
		this.setTerminationTime(null);//default required in case property is accessed before it is set (e.g. for serialisation)
				
		//dispatch to service-specific initialisation
		this.initialise(params);
		
		//sets the termination time last in case initialisation is a lengthy business.
		Integer lifetime = getPorttypeContext().getResourceLifeTime();
		if (lifetime!=null) {
			Calendar terminationTime= this.getCurrentTime();
			terminationTime.add(Calendar.SECOND,lifetime);
			this.setTerminationTime(terminationTime);
		}
			
	}
	
	/**
	 * Initialises the resource. 
	 * <p>
	 * Implement by casting to specific parameter types (if any are expected) and performing initialisation
	 * in accordance with resource semantics.  
	 * @param params (optional) the initialisation parameters.
	 * @throws Exception if the resource could not be initialised.
	 */
	protected abstract void initialise(Object...params) throws Exception;
	
	/**
	 * Returns the RP set of the resource.
	 * @return the property set.
	 */
	public GCUBEWSResourcePropertySet getResourcePropertySet() {return this.propSet;}
	
	/**
	* Returns the topic list of the resource.
	* @return the topic list.
	**/
	public GCUBETopicList getTopicList() {return this.topicList;}
	
	
	/**
	 * Returns the endpoint of the resource.
	 * @return the endpoint.
	 * @throws Exception if the endpoint could not be derived.
	 */
	public EndpointReferenceType getEPR() throws Exception {
		return AddressingUtils.createEndpointReference(GHNContext.getContext().getBaseURL()+this.getPorttypeContext().getJNDIName(),this.getID());		
	}
	
	/**
	 * Returns the current time.
	 * @return the time.
	 */
	public Calendar getCurrentTime() {return this.propSet.getCurrentTime();}	
	
	/**
	 * Returns the termination time of the resource.
	 * @return the termination time.
	 */
	public Calendar getTerminationTime() {return this.propSet.getTerminationTime();}
	
	/**
	 * Sets the termination time of resource.
	 * @param calendar the termination time.
	 */
	public void setTerminationTime(Calendar calendar) 
	{
		if (calendar!=null)
			logger.info(this.getClass().getSimpleName()+"("+this.getID()+") set to expire at "+calendar.getTime().toString()+" seconds");
		this.propSet.setTerminationTime(calendar);
	}
	
	/** 
	 * Publishes the resource in one or more scopes.
	 * @param scopes the scopes. 
	 */
	public void publish(final GCUBEScope ... scopes) throws ResourceException 
	{
		
		if ((scopes.length==0) || 
			 ((GHNContext.getContext().getMode() == Mode.STANDALONE)
			 && (GHNContext.getContext().getMode() != Mode.ROOT))) return;
		
		ISNotifier notifier=null;
		if (GCUBEWSResource.this.getTopicList().asList().size() > 0) 
			for (GCUBEScope scope : scopes) {				
			  if (scope==null) continue;//sanity check
				try {
					(notifier==null?notifier=GHNContext.getImplementation(ISNotifier.class):notifier).registerISNotification(getEPR(), getTopicList().asList(), getServiceContext(), scope);}
				catch(Exception e) {logger.warn("could not publish topics for "+GCUBEWSResource.this.getClass().getSimpleName()+"("+getID()+") in "+scope,e);}		
			}
		
		GCUBEPublicationProfile profile = this.getPorttypeContext().getPublicationProfile();
		
		if (profile==null) return;
				
		logger.info("publishing "+this.getClass().getSimpleName()+"("+this.getID()+") in "+Arrays.asList(scopes));
		
		ISPublisher publisher=null;
		for (GCUBEScope scope : scopes) {				
			if (scope== null) continue;//sanity check
			try {
				(publisher==null?getPublisher():publisher).registerWSResource(GCUBEWSResource.this,scope);}
			catch(Exception e) {logger.warn("could not publish "+GCUBEWSResource.this.getClass().getSimpleName()+"("+getID()+") in "+scope,e);}	
		}
			
	}
	
	/** 
	 * Unpublish the resource from one o more scopes.
	 * @param scopes the scopes.
	 * @author Ciro Formisano
	 */
	public void unpublish(final GCUBEScope ... scopes) throws ResourceException {

		if ((scopes.length==0) || ((GHNContext.getContext().getMode() == Mode.STANDALONE)
				 && (GHNContext.getContext().getMode() != Mode.ROOT))) return;
			
		ISNotifier notifier=null;
		if (GCUBEWSResource.this.getTopicList().asList().size() > 0) 
			for (GCUBEScope scope : scopes) {				
			  if (scope==null) continue;//sanity check
				try {
					(notifier==null?notifier=GHNContext.getImplementation(ISNotifier.class):notifier).unregisterISNotification(getEPR(), getTopicList().asList(), getServiceContext(), scope);}
				catch(Exception e) {logger.warn("could not unpublish topics for "+this.getClass().getSimpleName()+"("+getID()+") in "+scope,e);}		
			}

		
		GCUBEPublicationProfile profile = this.getPorttypeContext().getPublicationProfile(); 
		
		if (profile==null) return;
		
		logger.info("unpublishing "+this.getClass().getSimpleName()+"("+this.getID()+") from "+ Arrays.asList(scopes));
		
		ISPublisher publisher=null;
		for (GCUBEScope scope : scopes) {				
			if (scope== null) continue;//sanity check
			try {
				(publisher==null?getPublisher():publisher).removeWSResource(GCUBEWSResource.this,scope);}
			catch(Exception e) {logger.warn(GCUBEWSResource.this.getClass().getSimpleName()+"("+getID()+") could not be unpublished from "+scope);}	
		}
		
	}
	
	/**
	 * Returns the context of the port-type associated with the resource.
	 * @return the context
	 */
	public GCUBEStatefulPortTypeContext getPorttypeContext() {return this.portTypeContext;}
	
	/**
	 * Invoked by the {@link GCUBEResourceHome} of the resource to set the associated port-type.
	 * @param context the context.
	 */
	public void setPortTypeContext(GCUBEStatefulPortTypeContext context) {
		if (this.portTypeContext==null) {
			this.portTypeContext=context;
			this.logger.setContext(context.getServiceContext());
		} else throw new RuntimeException("resource port-type already configured");
	}

	/**{@inheritDoc}*/
	public GCUBEServiceContext getServiceContext() {return this.getPorttypeContext().getServiceContext();}
	
	/**
	 * Returns the names of the RPs of the resource (none by default).
	 * @return the names.
	 */
	protected String[] getPropertyNames() {return new String[0];}


	/**
	 * Returns the names of the topics of the resource (none by default).
	 * @return the topic names.
	 */
	protected String[] getTopicNames() {return new String[0];}
	
	/**
	 * Returns a new RP with a given local name.
	 * By default it creates {@link SimpleResourceProperty}. Override to use different {@link ResourceProperty} implementation.
	 * @param name the local name.
	 * @return the property.
	 * @throws Exception if the property could not be created.
	 */
	protected ResourceProperty getProperty(String name) throws Exception {
		return GCUBEWSResourcePropertyProxy.createSimpleResourceProperty(new QName(this.getPorttypeContext().getNamespace(),name), 
				this.getResourcePropertySet()); 
		
	}
	
	/** 
	 * Legacy operation. 
	 * @deprecated*/ 
	public void load(ResourceKey key) throws ResourceException, NoSuchResourceException, InvalidResourceKeyException {}

	/**
	 * Indicates whether the resource is in one or more given scopes.
	 * @param scopes the scopes. 
	 * @return <code>true</code> if the resource is in <em>all</em> the input scopes, <code>false</code> otherwise.
	 */
	public synchronized boolean inScope(GCUBEScope ... scopes) {
		
		if (scopes.length==0) throw new IllegalArgumentException();
		
		outer:for (GCUBEScope scope : scopes) {
			if (scope == null) return false;		
			for (String resScopeString : this.getResourcePropertySet().getScope()){//RPset is synchronised					
				GCUBEScope resScope = GCUBEScope.getScope(resScopeString);
				if (resScope.isInfrastructure()?scope.equals(resScope):scope.isEnclosedIn(resScope)) continue outer;//found a match
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the {@link ISPublisher} used for resource publication.
	 * @return the publisher, which is an implementation of {@link ISAsyncPublisher} by default.
	 * @throws Exception if the publisher could not be returned.
	 */
	protected ISPublisher getPublisher() throws Exception {
		return  GHNContext.getImplementation(ISPublisher.class);
	}
	
}
