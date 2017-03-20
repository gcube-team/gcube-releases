package org.gcube.datatransfer.scheduler.library.fws;

import static javax.jws.soap.SOAPBinding.ParameterStyle.BARE;
import static org.gcube.datatransfer.scheduler.library.fws.Constants.*;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


@WebService(name=porttypeManagementLocalName,targetNamespace=porttypeManagementNS)
public interface ManagementServiceJAXWSStubs {
	
	@SOAPBinding(parameterStyle=BARE)
	public String about(String name);
	
	@SOAPBinding(parameterStyle=BARE)
	public String getAddr(String tmp);
	
	@SOAPBinding(parameterStyle=BARE)
	public String getAllTransfersInfo(String name);
	
	@SOAPBinding(parameterStyle=BARE)
	public String getObjectsFromIS(String type);
	
	@SOAPBinding(parameterStyle=BARE)
	public String existAgentInIS(String agent);
	
	@SOAPBinding(parameterStyle=BARE)
	public String existAgentInDB(String agent);
	
	@SOAPBinding(parameterStyle=BARE)
	public String getAgentStatistics(String nothing);
	
}

