package org.gcube.vremanagement.softwaregateway.client.fws;

import static org.gcube.vremanagement.softwaregateway.client.Constants.*;
import static org.gcube.vremanagement.softwaregateway.client.fws.Types.*;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;

/**
 * 
 * @author Roberto Cirillo (ISTI -CNR)
 *
 */
@WebService(name=porttypeRegistrationLocalName,targetNamespace=NAMESPACE_REGISTRATION)
public interface SGRegistrationServiceJAXWSStubs {
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public String register(String registerRequest);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public Empty  unregister(LocationCoordinates request);

	
}


