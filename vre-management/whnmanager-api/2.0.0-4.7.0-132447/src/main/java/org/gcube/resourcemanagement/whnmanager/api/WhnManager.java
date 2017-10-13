package org.gcube.resourcemanagement.whnmanager.api;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.gcube.resourcemanagement.whnmanager.api.exception.GCUBEUnrecoverableException;

//Service Endpoint Interface
@WebService(targetNamespace=WhnManager.TNS)
@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL)
public interface WhnManager {

	public static final String SERVICE_NAME = "gcube/vremanagement/ws/whnmanager";
	public static final String TNS = "http://gcube-system.org/";
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public boolean addToContext(String context) throws GCUBEUnrecoverableException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public boolean removeFromContext(String context)  throws GCUBEUnrecoverableException;

}
