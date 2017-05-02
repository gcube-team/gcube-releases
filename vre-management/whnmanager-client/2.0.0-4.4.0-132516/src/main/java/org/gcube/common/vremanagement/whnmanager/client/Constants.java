package org.gcube.common.vremanagement.whnmanager.client;

import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;
import org.gcube.common.calls.jaxws.GcubeService;
import org.gcube.resourcemanagement.whnmanager.api.WhnManager;
import static org.gcube.common.calls.jaxws.GcubeService.service;

/**
 * 
 * @author roberto cirillo (ISTI-CNR)
 *
 */
public class Constants {

	public static final String SERVICE_CLASS = "VREManagement";
	public static final String SERVICE_NAME = "WhnManager";
	public static final String NAMESPACE = "http://gcube-system.org/namespaces/common/vremanagement/whnmanager";
	public static final String PORT_TYPE_NAME = "whn-manager/gcube/vremanagement/ws/whnmanager";

	
	public static final String serviceNS = "http://gcube-system.org/namespaces/common/vremanagement/whnmanager/service";
	public static final String serviceLocalName = "WHNManagerService";
	public static final QName serviceName = new QName(serviceNS,serviceLocalName);
	
	public static final String porttypeNS = "http://gcube-system.org/namespaces/common/vremanagement/whnmanager";
	public static final String porttypeLocalName = "GHNManagerPortType";
	  
	public static final String CONTEX_SERVICE_NAME="whn-manager";
		
	public static final int DEFAULT_TIMEOUT= (int) TimeUnit.SECONDS.toMillis(10);
		
	public static final QName WHNMANAGER_QNAME = new QName(org.gcube.resourcemanagement.whnmanager.api.Costants.TNS, WhnManager.SERVICE_NAME);

	public static final GcubeService<WhnManager> whnManager =service().withName(WHNMANAGER_QNAME).andInterface(WhnManager.class);

}
