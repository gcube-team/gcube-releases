package org.gcube.vremanagement.resourcemanager.client.fws;

import static org.gcube.vremanagement.resourcemanager.client.Constants.*;
import static org.gcube.vremanagement.resourcemanager.client.fws.Types.*;


import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;



/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
@WebService(name=porttypeControllerLocalName,targetNamespace=NAMESPACE_CONTROLLER)
public interface RMControllerServiceJAXWSStubs {
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public Empty ChangeScopeOptions(OptionsParameters options);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public String DisposeScope(String scope);
	

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public Empty CreateScope(CreateScopeParameters params);

}