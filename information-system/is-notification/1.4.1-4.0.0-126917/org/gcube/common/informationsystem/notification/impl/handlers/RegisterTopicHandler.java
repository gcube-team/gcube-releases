package org.gcube.common.informationsystem.notification.impl.handlers;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.notifier.stubs.NotifierPortType;
import org.gcube.informationsystem.notifier.stubs.RegisterTopicMessage;

public class RegisterTopicHandler extends
		NotifierCall<RegisterTopicMessage, String> {

	GCUBELog logger=new GCUBELog(RegisterTopicHandler.class);
	
	public RegisterTopicHandler(RegisterTopicMessage param, GCUBEScope scope, GCUBESecurityManager securityManager){
		super(scope, securityManager);
		this.setParameter(param);
	}

	@Override
	protected String makeCall(NotifierPortType notifierPort) throws Exception {
		return notifierPort.registerTopic(this.getParameter());
	}	
}
