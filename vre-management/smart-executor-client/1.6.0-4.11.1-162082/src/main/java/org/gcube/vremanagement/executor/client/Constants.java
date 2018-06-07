package org.gcube.vremanagement.executor.client;

import javax.xml.namespace.QName;

import org.gcube.common.calls.jaxws.GcubeService;
import org.gcube.vremanagement.executor.api.SmartExecutor;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class Constants {

	/* Used for REST*/
	public static final String SERVICE_CLASS = "VREManagement";
	public static final String SERVICE_NAME = "SmartExecutor";
	public static final String SERVICE_ENTRY_NAME = "org.gcube.vremanagement.executor.ResourceInitializer";
	
	
	/* Used for SOAP */
	
	/* Must be the same of generated WEB-INF/gcube-app.xml */
	/**
	 * Use SERVICE_CLASS instead
	 */
	@Deprecated
	public static final String GCUBE_SERVICE_CLASS = SERVICE_CLASS;
	/**
	 * Use SERVICE_NAME instead
	 */
	@Deprecated
	public static final String GCUBE_SERVICE_NAME = SERVICE_NAME;
	
	@Deprecated
	public static final QName SMART_EXECUTOR_QNAME = new QName(SmartExecutor.TARGET_NAMESPACE, SmartExecutor.WEB_SERVICE_SERVICE_NAME);
	@Deprecated
	public static final GcubeService<SmartExecutor> smartExecutor = GcubeService.service().withName(SMART_EXECUTOR_QNAME).andInterface(SmartExecutor.class);
	
	
	
	
}
