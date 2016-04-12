package org.gcube.common.informationsystem.notification.impl.handlers;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.informationsystem.notifier.stubs.NotifierPortType;
import org.gcube.informationsystem.notifier.stubs.SubscribeMessage;
import org.gcube.informationsystem.notifier.stubs.SubscribeToTopicResponse;

public class SubscribeToTopicHandler extends NotifierCall<SubscribeMessage, SubscribeToTopicResponse> {

	public SubscribeToTopicHandler(SubscribeMessage param, GCUBEScope scope, GCUBESecurityManager securityManager){
		super(scope, securityManager);
		this.setParameter(param);
	}
	
	@Override
	protected SubscribeToTopicResponse makeCall(NotifierPortType notifierPort) throws Exception {
		return notifierPort.subscribeToTopic(getParameter());		
	}

}
