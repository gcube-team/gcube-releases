package org.gcube.common.core.porttypes;

import java.rmi.RemoteException;

import org.globus.wsrf.impl.notification.GetCurrentMessageProvider;
import org.globus.wsrf.impl.notification.SubscribeProvider;
import org.oasis.wsn.GetCurrentMessage;
import org.oasis.wsn.GetCurrentMessageResponse;
import org.oasis.wsn.InvalidTopicExpressionFaultType;
import org.oasis.wsn.NoCurrentMessageOnTopicFaultType;
import org.oasis.wsn.ResourceUnknownFaultType;
import org.oasis.wsn.Subscribe;
import org.oasis.wsn.SubscribeCreationFailedFaultType;
import org.oasis.wsn.SubscribeResponse;
import org.oasis.wsn.TopicNotSupportedFaultType;
import org.oasis.wsn.TopicPathDialectUnknownFaultType;

/**
 * Extends {@link GCUBEProvider} for port-types that accept topic subscriptions and produce corresponding notifications. 
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public class GCUBENotificationProvider extends GCUBEProvider {

	/**
	 * Wrapped GLOBUS operation provider.
	 */
	private SubscribeProvider subscribeprovider = new SubscribeProvider();
	
	/**
	 * Wrapped GLOBUS operation provider.
	 */
	private GetCurrentMessageProvider currentMessageProvider = new GetCurrentMessageProvider();
	
	/**
	 * Dispatches the invocation to the corresponding GLOBUS provider.
	 * @param arg0 the provider's input,as per WSRF specifications.
	 * @return the provider's output,as per WSRF specifications.
	 * @throws RemoteException
	 * @throws ResourceUnknownFaultType
	 * @throws SubscribeCreationFailedFaultType
	 * @throws TopicNotSupportedFaultType
	 * @throws InvalidTopicExpressionFaultType
	 * @throws TopicNotSupportedFaultType
	 */
	public SubscribeResponse subscribe(Subscribe arg0) throws RemoteException,
			ResourceUnknownFaultType, SubscribeCreationFailedFaultType,
			TopicPathDialectUnknownFaultType, InvalidTopicExpressionFaultType,
			TopicNotSupportedFaultType {
		return subscribeprovider.subscribe(arg0);
	}

	/**
	 * Dispatches the invocation to the corresponding GLOBUS provider.
	 * @param arg0 the provider's input,as per WSRF specifications.
	 * @return the provider's output,as per WSRF specifications.
	 * @throws RemoteException
	 * @throws ResourceUnknownFaultType
	 * @throws InvalidTopicExpressionFaultType
	 * @throws TopicNotSupportedFaultType
	 * @throws NoCurrentMessageOnTopicFaultType
	 */
	public GetCurrentMessageResponse getCurrentMessage(GetCurrentMessage arg0)
			throws RemoteException, ResourceUnknownFaultType,
			InvalidTopicExpressionFaultType, TopicNotSupportedFaultType,
			NoCurrentMessageOnTopicFaultType {
		return currentMessageProvider.getCurrentMessage(arg0);
	}
	
	
}
