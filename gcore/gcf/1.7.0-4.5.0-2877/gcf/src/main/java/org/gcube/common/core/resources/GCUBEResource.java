package org.gcube.common.core.resources;

import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.axis.components.uuid.UUIDGenFactory;

import org.gcube.common.core.contexts.ghn.Events.GHNTopic;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.events.GCUBEConsumer;
import org.gcube.common.core.utils.events.GCUBEEvent;
import org.gcube.common.core.utils.events.GCUBEProducer;
import org.gcube.common.core.utils.events.GCUBETopic;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * Specifies the behaviour common to all gCUBE resources.
 * 
 * @author Andrea Manzi (CNR), Fabio Simeoni (University of Strathclyde)
 *
 */
public abstract class GCUBEResource {

	/**
	 * The object logger;
	 */
	protected GCUBELog logger = new GCUBELog(this);
	
	/**
	 * The resource identifier.
	 */
	private String id=UUIDGenFactory.getUUIDGen().nextUUID();
	
	/**
	 * The resource type.
	 */
	protected String type;
	
	/**
	 * The scopes associated with the resource.
	 */
	private Map<String,GCUBEScope> scopes = new LinkedHashMap<String,GCUBEScope>();
	
	/**
	 * The version of the Profile to which this resource is compliant to.
	 */
	protected String resourceVersion;
	
	/**
	 * Sets the resource's logger.
	 * @param logger the logger.
	 */
	public void setLogger(GCUBELog logger) {this.logger=logger;}
	/**
	 * Returns the type of the resource.
	 * @return the type.
	 */
	public String getType() {return this.type;}

	/**
	 * Returns the resource identifier.
	 * @return the identifier.
	 */
	public String getID() {
		if ((this.id == null) || (this.id.compareTo("") == 0)) 
			//assign a new ID
			this.id = UUIDGenFactory.getUUIDGen().nextUUID();
		
		return this.id;
	}
	
	/**
	 * Sets the resource identifier. 
	 * As the identifier is a one-time constant, the operation returns successfully from the first invocation only.
	 * @param id the identifier
	 * @return <code>true</code> if the identifier was set, <code>false</code> otherwise.
	 */
	public synchronized boolean setID(String id) {
		
		if (id!=null) {
			this.id=id;
			return true;
		}
		logger.warn("Attempt to change identifier of gCUBE resource "+this.getID());
		return false;
	}
	
	/**
	 * Returns a read-only map of the scopes associated with the resource index by scope expression.
	 * @return the map.
	 */
	public synchronized Map<String,GCUBEScope> getScopes() {return Collections.unmodifiableMap(this.scopes);}
	
	/**
	 * Adds one or more scopes from the resource.
	 * @param scopes the scopes.
	 */
	@SuppressWarnings("unchecked")
	public synchronized Set<GCUBEScope> addScope(GCUBEScope ... scopes) {
		if (scopes==null || scopes.length==0) throw new IllegalArgumentException();
		Set<GCUBEScope> validScopes = this.validateAddScopes(scopes);
		Set<GCUBEScope> validScopesCopy = new HashSet<GCUBEScope>();
		validScopesCopy.addAll(validScopes);//copy to iterate over while removing
		for (GCUBEScope scope : validScopesCopy)		
			if (this.scopes.containsKey(scope.toString())) validScopes.remove(scope);
			else this.scopes.put(scope.toString(), scope);
		
		if (validScopes.size()>0) {
			logger.info("Added scope(s) "+validScopes);
			this.eventProducer.notify(ResourceTopic.ADDSCOPE,new AddScopeEvent(validScopes.toArray(new GCUBEScope[0])));		
		}
			

		return validScopes;
	}
	
	/**
	 * Invoked by {@link GCUBEResource#addScope(GCUBEScope...)}, to filter scopes as per
	 * resource-specific scoping rules.By default, it returns its input.
	 * @param scopes the scopes.
	 * @return the subset of valid scopes.
	 */
	public synchronized Set<GCUBEScope> validateAddScopes(GCUBEScope ... scopes) {
		Set<GCUBEScope> set = new LinkedHashSet<GCUBEScope>();
		Collections.addAll(set, scopes);
		return set;
	}
	
	/**
	 * Removes one or more scopes from the resource.
	 * Only scopes currently associated with the resource are actually removed.
	 * @param scopes the scopes.
	 * @return the list of scopes removed from the resource, empty if no scope was actually removed.
	 */
	@SuppressWarnings("unchecked")
	public synchronized Set<GCUBEScope> removeScope(GCUBEScope ... scopes) {
		
		if (scopes==null || scopes.length==0) throw new IllegalArgumentException();
		Set<GCUBEScope> validScopes = this.validateRemoveScopes(scopes);
		for (GCUBEScope scope : validScopes) {
				if (this.scopes.size()==0) {
					logger.info("Cannot remove from scope "+scope+" because resource must at least remain in one scope.");
					continue; //could 'break' but then would not log for each scope which is not removed.
				}
				if (this.scopes.remove(scope.toString())==null) {
					validScopes.remove(scope);
					logger.info("Cannot remove from scope "+scope+" because it is not in it");
				}
				else logger.info("Removed from scope "+scope);
		}
		
		if (validScopes.size()>0) //notifies only if some scope was actually removed!
			this.eventProducer.notify(ResourceTopic.REMOVESCOPE,new RemoveScopeEvent(validScopes.toArray(new GCUBEScope[0])));
		
		return validScopes;
	}

	/**
	 * Invoked by {@link GCUBEResource#removeScope(GCUBEScope...)}, to filter scopes as per
	 * resource-specific scoping rules.By default, it returns its input.
	 * @param scopes the scopes.
	 * @return the subset of valid scopes.
	 */
	public synchronized Set<GCUBEScope> validateRemoveScopes(GCUBEScope ... scopes) {
		Set<GCUBEScope> set = new LinkedHashSet<GCUBEScope>();
		Collections.addAll(set, scopes);
		return set;
	}
	
	/**
	 * Indicates whether the resource is in one or more given scopes.
	 * @param scopes the scopes. 
	 * @return <code> true</code> if it is in all the input scopes, <code>false</code> otherwise.
	 */
	public synchronized boolean inScope(GCUBEScope ... scopes) {
		if (scopes==null || scopes.length==0) throw new IllegalArgumentException();
		//default for not scoped resource: they are everywhere
		//if (this.getScopes().values().size() == 0)
		//	return true;
		outer: for (GCUBEScope scope : scopes) {
			if (scope == null) //if one of the input scopes is null, the resource is not in
				return false;		
			for (GCUBEScope resScope : this.getScopes().values()){								
				if (resScope.isInfrastructure()?scope.equals(resScope):scope.isEnclosedIn(resScope))
					continue outer;//resource is in this scope, move to test next scope
			}
			return false;
			
		}
		return true;
	}
	
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		final GCUBEResource other = (GCUBEResource) obj;
		
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (! id.equals(other.id))
			return false;
		
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (! type.equals(other.type))
			return false;
		
		if (scopes == null) {
			if (other.scopes != null)
				return false;
		} else if (! scopes.equals(other.scopes))
			return false;

		return true;
	}

	/**
	 * Deserialise the state of the resource from an existing serialisation. 
	 * The operations is destructive with respect to the current state of the resource.
	 * @param reader the {@link java.io.Reader Reader} which exposes the serialisation.
	 * @throws UnsupportedOperationException if this operation is not supported by implementation.
	 * @throws Exception if the operation is supported but could not complete successfully.
	 */
	public abstract void load(Reader reader) throws Exception;
	
	/**
	 * Serialise the current state of the resource. 
	 * @param writer the {@link java.io.Writer Writer} which exposes the serialisation.
	 * @throws UnsupportedOperationException
	 * @throws UnsupportedOperationException if this operation is not supported by implementation.
	 * @throws Exception if the operation is supported but could not complete successfully.
	 */
	public abstract void store(Writer writer) throws Exception;
	

	/** A {@link GCUBETopic} for lifetime events .*/
	public static enum ResourceTopic implements GCUBETopic {ADDSCOPE,REMOVESCOPE};
	
	/** Single {@link org.gcube.common.core.utils.events.GCUBEEvent GCUBEEvent} type for {@link GHNTopic GHNTopics}.*/
	public abstract class ResourceEvent<PAYLOAD> extends GCUBEEvent<ResourceTopic,PAYLOAD> {}
	/** Generic {@link GCUBEEvent} for the addition of a scope to the resource. */
	public class AddScopeEvent extends ResourceEvent<GCUBEScope[]>{AddScopeEvent(GCUBEScope ... scopes) {this.payload=scopes;}}
	/** Generic {@link GCUBEEvent} for the removal of a scope from the resource. */
	public class RemoveScopeEvent extends ResourceEvent<GCUBEScope[]>{RemoveScopeEvent(GCUBEScope ... scopes) {this.payload=scopes;}}

	/**Internal {@link GCUBEProducer} for {@link ResourceEvent ResourceEvents}.*/
	protected GCUBEProducer<ResourceTopic,Object> eventProducer = new GCUBEProducer<ResourceTopic,Object>();  
	
	/**Subscribes a consumer to one or more {@link ResourceEvent ResourceEvents}.
    * @param consumer the consumer.
    * @param topics the topics of interest.
    * @throws Exception if the subscription could not be completed.*/
   public void subscribeResourceEvents(ResourceConsumer consumer, ResourceTopic ...topics) throws Exception {
	   if (topics==null || topics.length==0) topics = ResourceTopic.values();		   
	   this.eventProducer.subscribe(consumer,topics);
   }
   
	/** Base implementation of a {@link GCUBEConsumer} of {@link ResourceEvent ResourceEvents}.*/
	public static class ResourceConsumer implements GCUBEConsumer<ResourceTopic,Object> {
		
		/**Receives RI lifetime events and dispatches them to topic-specific callbacks.*/
		public <T1 extends ResourceTopic, P1 extends Object> void onEvent(GCUBEEvent<T1, P1>... events) {
			if (events==null) return;
			for (GCUBEEvent<T1,P1> event : events) {
				ResourceTopic topic = event.getTopic();
				switch (topic) {
					case ADDSCOPE : this.onAddScope((AddScopeEvent)event);break;
					case REMOVESCOPE : this.onRemoveScope((RemoveScopeEvent)event);break;
				}
			}			
		}
		/**Add scope event callback.
		 * @param event the event.*/
		protected void onAddScope(AddScopeEvent event) {}
		/**Remove scope event callback.
		 * @param event the event.*/
		protected void onRemoveScope(RemoveScopeEvent event) {}
	}
	
    /** NoValidScopeFoundException exception  */
	public static class InvalidScopeException  extends Exception{private static final long serialVersionUID = 1L;}

	/**
	 * @return the resource version
	 */
	public String getResourceVersion() {
		return (this.resourceVersion != null) ? this.resourceVersion : "0.4.x";
	}
	/**
	 * @param version the resource version to set
	 */
	public void setResourceVersion(String version) {
		this.resourceVersion = version;
	}
	
	/**
	 * @return the last version available of the resource
	 */
	public String getLastResourceVersion() {
		return "0.4.x";
	}
	
}
