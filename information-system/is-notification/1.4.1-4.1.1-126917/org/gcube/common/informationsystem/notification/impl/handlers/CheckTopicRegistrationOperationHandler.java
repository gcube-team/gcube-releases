package org.gcube.common.informationsystem.notification.impl.handlers;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.informationsystem.notifier.stubs.IsOngoingRequest;
import org.gcube.informationsystem.notifier.stubs.NotifierPortType;

public class CheckTopicRegistrationOperationHandler extends
		NotifierCall<IsOngoingRequest, boolean[]> {

	
	public CheckTopicRegistrationOperationHandler(IsOngoingRequest param, GCUBEScope scope, GCUBESecurityManager securityManager){
		super(scope, securityManager);
		this.setParameter(param);
	}
	
	@Override
	protected boolean[] makeCall(NotifierPortType notifierPort) throws Exception {
		return notifierPort.isOngoing(this.getParameter()).getIsOngoingMask();
	}

}
