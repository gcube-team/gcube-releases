package org.gcube.common.informationsystem.notification.impl.handlers;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.types.VOID;
import org.gcube.informationsystem.notifier.stubs.NotifierPortType;
import org.gcube.informationsystem.notifier.stubs.SubscribeMessage;

public class RemoveSubscriptionHandler extends
		NotifierCall<SubscribeMessage, VOID> {

	public RemoveSubscriptionHandler(SubscribeMessage param, GCUBEScope scope, GCUBESecurityManager securityManager){
		super(scope, securityManager);
		this.setParameter(param);
		this.setScope(scope);
	}
	
	@Override
	protected VOID makeCall(NotifierPortType notifierPort) throws Exception {
		notifierPort.removeSubscription(this.getParameter());
		return new VOID();
	}

}
