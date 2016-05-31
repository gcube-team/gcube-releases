package org.gcube.common.core.informationsystem.publisher;

import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.events.GCUBEConsumer;
import org.gcube.common.core.utils.events.GCUBEEvent;
import org.gcube.common.core.utils.events.GCUBETopic;

/**
 * Models the generic ISLocalPublisher behavior.<br/> 
 * 
 * An implementation of the ISLocalPublisher is in charge of publishing local events for changing in the lifetime of {@link GCUBEResource} hosted in the current gHN. 
 * Local publishing is usually enabled when an instance of the IS is co-hosted on the node.
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public interface ISLocalPublisher {
	
	/**  Subscribes a consumer to all {@link LocalProfileEvent}
	 * 
	 * @param consumer the consumer
	 * @throws ISPublisherException if the registration fails
	 * */
	public void subscribeLocalProfileEvents(LocalProfileConsumer consumer)	throws ISPublisherException;
	
	/** Checks if local publishing is enabled for a type of resource in a given scope
	 *
	 * @param resourceType a {@link GCUBEResource} type
	 * @param scope the scope to check
	 * @return true if the local publishing is enabled, false otherwise 
	 */
	public boolean isEnabled(String resourceType, GCUBEScope scope);
	
	/** A {@link LocalProfileTopic} for profile events */
	enum LocalProfileTopic implements GCUBETopic {REGISTERED,UPDATED,REMOVED};
	
	/** Generic {@link GCUBEEvent} for notification. */
	class LocalProfileEvent extends GCUBEEvent<LocalProfileTopic,GCUBEResourceMessage> { 
		public LocalProfileEvent(GCUBEResource resource, GCUBEScope scope) {this.payload = new GCUBEResourceMessage(resource, scope);}
		public LocalProfileEvent(String resourceID, String type, GCUBEScope scope) {this.payload = new GCUBEResourceMessage(resourceID, type, scope);}
	}
	
	
	/** Base implementation of a {@link GCUBEConsumer} of {@link LocalProfileEvent}.*/
	public static class LocalProfileConsumer implements GCUBEConsumer<LocalProfileTopic,Object> {
		
		public <T extends LocalProfileTopic, P extends Object> void onEvent(
				GCUBEEvent<T, P>... events) {			
			if (events==null) return;
			for (GCUBEEvent<T,P> event : events) {
				LocalProfileTopic topic = event.getTopic();
				GCUBEResourceMessage message = ((LocalProfileEvent) event).getPayload(); 				
				switch (topic) {
					case REGISTERED: this.onProfileRegistered(message.getResource(), message.getScope()); break;
					case UPDATED: this.onProfileUpdated(message.getResource(), message.getScope()); break;
					case REMOVED: this.onProfileRemoved(message.getID(), message.getType(), message.getScope());break;
				}
			}			
		}
		/**removed profile event callback.
		 * @param resourceID the identifier of the removed {@link GCUBEResource}
		 * @param type the type of the {@link GCUBEResource}
		 * @param scope the publishing scope
		 */
		protected void onProfileRemoved(String resourceID, String type, GCUBEScope scope) {}

		/**updated profile event callback.
		 * @param resource the updated {@link GCUBEResource}
		 * @param scope the publishing scope
		 */
		protected void onProfileUpdated(GCUBEResource resource, GCUBEScope scope) {}
		
		/**registered profile event callback.
		 * @param resource the registered {@link GCUBEResource}
		 * @param scope the publishing scope
		 */
		protected void onProfileRegistered(GCUBEResource resource, GCUBEScope scope) {}
		
	}
		
	
	class GCUBEResourceMessage {
		private GCUBEResource resource = null;
		private String ID;
		private String type;
		private GCUBEScope scope;
		
		GCUBEResourceMessage(GCUBEResource resource, GCUBEScope scope) {
			this.resource = resource;
			this.ID = this.resource.getID();
			this.type = this.resource.getType();
			this.scope = scope;
		}

		GCUBEResourceMessage(String resourceID, String type, GCUBEScope scope) {
			this.ID = resourceID;
			this.type = type;
			this.scope = scope;
		}

		GCUBEResource getResource() {return this.resource;}

		String getID() {return this.ID;}
		
		String getType() {return this.type;}
		
		GCUBEScope getScope() {return this.scope;}
		
	}

}