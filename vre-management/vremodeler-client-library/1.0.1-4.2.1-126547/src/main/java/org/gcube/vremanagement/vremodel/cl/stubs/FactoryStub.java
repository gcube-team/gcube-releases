package org.gcube.vremanagement.vremodel.cl.stubs;

import static org.gcube.vremanagement.vremodel.cl.Constants.*;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.vremanagement.vremodel.cl.stubs.types.ExistingNames;
import org.gcube.vremanagement.vremodel.cl.stubs.types.ReportList;


@WebService(name=factory_portType,targetNamespace=factory_target_namespace)
public interface FactoryStub {

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public W3CEndpointReference createResource(Empty empty);
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public ReportList getAllVREs(Empty empty);
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public ExistingNames getExistingNamesVREs(Empty empty);
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public void removeVRE(String id);
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public void initDB(Empty empty);
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public W3CEndpointReference getEPRbyId(String id);
	
	
}
