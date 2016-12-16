package org.gcube.common.core.contexts.ghn;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.utils.events.GCUBEEvent;
import org.gcube.common.core.utils.events.GCUBETopic;
import org.ietf.jgss.GSSCredential;

/**
 * gHN-related events and topics. 
 * @author Fabio Simeoni (University of Strathclyde)
 **/
public class Events {

	//////////////////////////////////////////////////////////////////// LIFETIME
	
	/** A {@link GCUBETopic} for the registration of RIs.*/
	public static enum GHNTopic implements GCUBETopic {RIREGISTRATION,UPDATE,SHUTDOWN,READY}
	
	/** Single {@link GCUBEEvent} type for {@link GHNTopic GHNTopics}.*/
	public static abstract class GHNEvent<PAYLOAD> extends GCUBEEvent<GHNTopic,PAYLOAD> {}
	/** {@link GCUBEEvent} for RI registration.*/
	public static class GHNRIRegistrationEvent extends GHNEvent<GCUBEServiceContext> {
		public GHNRIRegistrationEvent(GCUBEServiceContext context){this.payload=context;}
	}
	/** Generic {@link org.gcube.common.core.utils.events.GCUBEEvent GCUBEEvent} for GHN lifetime events.*/
	public static class GHNLifeTimeEvent extends GHNEvent<Object> {}
	
	
	//////////////////////////////////////////////////////////////////////////////////// SECURITY
	
	
	
	/** A {@link org.gcube.common.core.utils.events.GCUBETopic GCUBETopic} for credentials-related events.*/
	public static enum SecurityTopic implements GCUBETopic {CREDENTIAL_REQUEST,CREDENTIAL_DELEGATION}

	public static abstract class SecurityEvent<PAYLOAD> extends GCUBEEvent<SecurityTopic,PAYLOAD> {}
	/** {@link org.gcube.common.core.utils.events.GCUBEEvent GCUBEEvent} for credential requests.*/
	public static class CredentialRequestEvent extends SecurityEvent<GCUBEServiceContext> {
		public CredentialRequestEvent(GCUBEServiceContext context) {this.setPayload(context);}
	}
	/** {@link org.gcube.common.core.utils.events.GCUBEEvent GCUBEEvent} for credential delegation.*/
	public static class CredentialDelegationEvent extends SecurityEvent<CredentialPayload> {
		public CredentialDelegationEvent(CredentialPayload payload) {this.setPayload(payload);}
	}

	/** Payload of {@link CredentialDelegationEvent CredentialDelegationEvents} */
	public static class CredentialPayload {
		private GCUBEServiceContext context;private GSSCredential credentials;
		public CredentialPayload(GCUBEServiceContext context,GSSCredential credentials) {this.context=context;this.credentials=credentials;}
		public GCUBEServiceContext getServiceContext() {return this.context;}
		public GSSCredential getCredentials() {return this.credentials;}
	}
	

}
