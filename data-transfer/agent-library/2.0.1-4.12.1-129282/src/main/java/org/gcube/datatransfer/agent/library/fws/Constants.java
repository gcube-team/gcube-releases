package org.gcube.datatransfer.agent.library.fws;

import javax.xml.namespace.QName;

import org.gcube.common.clients.stubs.jaxws.GCoreService;

import static org.gcube.common.clients.stubs.jaxws.GCoreServiceBuilder.*;

public class Constants {
	
	  public static final String serviceNS = "http://gcube-system.org/namespaces/datatransfer/agent/datatransferagent/service";
	  public static final String serviceLocalName = "DataTransferAgentService";
	  public static final QName serviceName = new QName(serviceNS,serviceLocalName);
	 
	  public static final String porttypeNS = "http://gcube-system.org/namespaces/datatransfer/agent/datatransferagent";
	  static final String porttypeLocalName = "DataTransferAgentPortType";
	  
	  public static final String service_class="DataTransfer";
	  public static final String service_name="agent-service";
	  
	  
	  
	  public static final GCoreService<AgentServiceJAXWSStubs> agent = service().withName(serviceName)
              .coordinates(service_class,service_name)
             .andInterface(AgentServiceJAXWSStubs.class); 

}

