package org.gcube.vremanagement.executor.client;

import javax.xml.namespace.QName;

import org.gcube.common.calls.jaxws.GcubeService;
import org.gcube.vremanagement.executor.api.SmartExecutor;

public class Constants {

	/* Must be the same of generated WEB-INF/gcube-app.xml */
	public static final String GCUBE_SERVICE_CLASS = "VREManagement";
	public static final String GCUBE_SERVICE_NAME = "SmartExecutor";
	
	public static final QName SMART_EXECUTOR_QNAME = new QName(SmartExecutor.TARGET_NAMESPACE, SmartExecutor.WEB_SERVICE_SERVICE_NAME);
	public static final GcubeService<SmartExecutor> smartExecutor = GcubeService.service().withName(SMART_EXECUTOR_QNAME).andInterface(SmartExecutor.class);
	
	
}
