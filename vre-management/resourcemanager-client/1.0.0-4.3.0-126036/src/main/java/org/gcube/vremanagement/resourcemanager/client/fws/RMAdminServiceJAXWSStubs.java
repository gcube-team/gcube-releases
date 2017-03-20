package org.gcube.vremanagement.resourcemanager.client.fws;


import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import static org.gcube.vremanagement.resourcemanager.client.Constants.*;

import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;


	/**
	 * 
	 * @author Andrea Manzi(CERN)
	 *
	 */
@WebService(name=porttypeAdminLocalName,targetNamespace=NAMESPACE_ADMIN)
public interface RMAdminServiceJAXWSStubs {
			
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public Empty CleanSoftwareState();

}
