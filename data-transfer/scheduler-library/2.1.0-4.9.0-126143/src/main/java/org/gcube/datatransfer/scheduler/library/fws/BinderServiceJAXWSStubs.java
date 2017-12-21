package org.gcube.datatransfer.scheduler.library.fws;

import static javax.jws.soap.SOAPBinding.ParameterStyle.BARE;
import static org.gcube.datatransfer.scheduler.library.fws.Constants.*;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.wsaddressing.W3CEndpointReference;


@WebService(name=porttypeFactoryLocalName,targetNamespace=porttypeFactoryNS)
public interface BinderServiceJAXWSStubs {
		
	@SOAPBinding(parameterStyle=BARE)
	public W3CEndpointReference create(String name);
}

