package org.gcube.common.vremanagement.ghnmanager.client;

import javax.xml.namespace.QName;

import org.gcube.common.clients.stubs.jaxws.GCoreService;
import org.gcube.common.vremanagement.ghnmanager.client.fws.GHNManagerServiceJAXWSStubs;

import static org.gcube.common.clients.stubs.jaxws.GCoreServiceBuilder.*;

/**
 * 
 * @author andrea
 *
 */
public class Constants {

	public static final String SERVICE_CLASS = "VREManagement";
	public static final String SERVICE_NAME = "GHNManager";
	public static final String NAMESPACE = "http://gcube-system.org/namespaces/common/vremanagement/ghnmanager";
	public static final String PORT_TYPE_NAME = "gcube/common/vremanagement/GHNManager";

	
	  public static final String serviceNS = "http://gcube-system.org/namespaces/common/vremanagement/ghnmanager/service";
	  public static final String serviceLocalName = "GHNManagerService";
	  public static final QName serviceName = new QName(serviceNS,serviceLocalName);
	 
	  public static final String porttypeNS = "http://gcube-system.org/namespaces/common/vremanagement/ghnmanager";
	  public static final String porttypeLocalName = "GHNManagerPortType";
	  
	  
	  public static final GCoreService<GHNManagerServiceJAXWSStubs> ghnmanager = service().withName(serviceName)
            .coordinates(SERVICE_CLASS,SERVICE_NAME)
           .andInterface(GHNManagerServiceJAXWSStubs.class); 
}
