package org.gcube.common.core.contexts.ghn;

import org.gcube.common.core.contexts.ghn.Events.CredentialRequestEvent;
import org.gcube.common.core.contexts.ghn.Events.GHNEvent;
import org.gcube.common.core.contexts.ghn.Events.SecurityTopic;
import org.gcube.common.core.utils.events.GCUBEConsumer;
import org.gcube.common.core.utils.events.GCUBEEvent;

/** Base implementation of a {@link GCUBEConsumer} of {@link GHNEvent GHNEvents}.
 *  @author Fabio Simeoni (University of Strathclyde)
 * */
public abstract class CredentialRequestConsumer implements GCUBEConsumer<SecurityTopic,Object> {

		/**Receives RI lifetime events and dispatches them to topic-specific callbacks.*/
		public <T1 extends SecurityTopic, P1 extends Object> void onEvent(GCUBEEvent<T1, P1>... events) {
			if (events==null) return;
			for (GCUBEEvent<T1,P1> event : events)
				if (event.getTopic()==SecurityTopic.CREDENTIAL_REQUEST) this.onCredentialRequest((CredentialRequestEvent)event);
		}
		/**Activation event callback.
		 * @param event the event.*/
		protected abstract void onCredentialRequest(CredentialRequestEvent event);

}
