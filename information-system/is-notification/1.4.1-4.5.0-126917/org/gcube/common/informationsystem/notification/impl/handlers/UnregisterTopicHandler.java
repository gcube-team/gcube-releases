package org.gcube.common.informationsystem.notification.impl.handlers;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.types.VOID;
import org.gcube.informationsystem.notifier.stubs.NotifierPortType;
import org.gcube.informationsystem.notifier.stubs.RegisterTopicMessage;

public class UnregisterTopicHandler extends NotifierCall<RegisterTopicMessage, VOID>{

	public UnregisterTopicHandler(RegisterTopicMessage param, GCUBEScope scope, GCUBESecurityManager securityManager){
		super(scope, securityManager);
		this.setParameter(param);
	}
	
	@Override
	protected VOID makeCall(NotifierPortType notifierPort) throws Exception {
		notifierPort.unregisterTopic(this.getParameter());
		return new VOID();
	}

}
