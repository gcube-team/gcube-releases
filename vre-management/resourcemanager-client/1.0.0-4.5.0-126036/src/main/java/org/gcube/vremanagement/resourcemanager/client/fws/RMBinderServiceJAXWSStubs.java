package org.gcube.vremanagement.resourcemanager.client.fws;

import static org.gcube.vremanagement.resourcemanager.client.Constants.*;
import static org.gcube.vremanagement.resourcemanager.client.fws.Types.*;


import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;



/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
@WebService(name=porttypeBinderLocalName,targetNamespace=NAMESPACE_BINDER)
public interface RMBinderServiceJAXWSStubs {
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public String AddResources(AddResourcesParameters params);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public String RemoveResources(RemoveResourcesParameters params);

}
