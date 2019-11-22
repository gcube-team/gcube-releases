package org.gcube.common.core.contexts.ghn;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.ghn.Events.CredentialDelegationEvent;
import org.gcube.common.core.contexts.ghn.Events.CredentialPayload;
import org.gcube.common.core.contexts.ghn.Events.GHNEvent;
import org.gcube.common.core.contexts.ghn.Events.SecurityTopic;
import org.gcube.common.core.utils.events.GCUBEConsumer;
import org.gcube.common.core.utils.events.GCUBEEvent;

/** Base implementation of a {@link GCUBEConsumer} of {@link GHNEvent GHNEvents}.
 *  @author Fabio Simeoni (University of Strathclyde)
 * */
public abstract class CredentialConsumer implements  GCUBEConsumer<SecurityTopic,Object> {

	/**Receives RI lifetime events and dispatches them to topic-specific callbacks.*/
	//NOTE: 'final' is crucial for protection here. Ensures that no impostor can make use of other services'credentials
	//through overriding
	final public <T1 extends SecurityTopic, P1 extends Object> void onEvent(GCUBEEvent<T1, P1>... events) {
		if (events==null) return;
		for (GCUBEEvent<T1,P1> event : events) {
			if (event.getTopic()==SecurityTopic.CREDENTIAL_DELEGATION &&
					((CredentialPayload)event.getPayload()).getServiceContext().equals(this.getServiceContext()))//ignores all credentials but its own 
				this.onCredentialDelegation((CredentialDelegationEvent)event);

		}
	}
	/**Activation event callback.
	 * @param event the event.*/
	protected abstract void onCredentialDelegation(CredentialDelegationEvent event);
	/** Returns the context of the service on behalf of which the consumer is requesting credential delegation. */
	public abstract GCUBEServiceContext getServiceContext();


}
