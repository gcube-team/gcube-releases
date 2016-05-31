package org.gcube.resourcemanagement.whnmanager.api;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import org.gcube.resourcemanagement.whnmanager.api.exception.GCUBEUnrecoverableException;
import org.gcube.resourcemanagement.whnmanager.api.types.AddScopeInputParams;
import org.gcube.resourcemanagement.whnmanager.api.types.ScopeRIParams;

//Service Endpoint Interface
@WebService(targetNamespace=WhnManager.TNS)
@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL)
public interface WhnManager {

	public static final String SERVICE_NAME = "gcube/vremanagement/ws/whnmanager";
	public static final String TNS = "http://gcube-system.org/";
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	public boolean addScope(AddScopeInputParams params) throws GCUBEUnrecoverableException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	public boolean removeScope(String name)  throws GCUBEUnrecoverableException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	public boolean addRIToScope(ScopeRIParams params) throws GCUBEUnrecoverableException;
//	
//	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
//	public boolean activateRI(RIData ri) throws GCUBEUnrecoverableException;
//
//	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
//	public boolean deactivateRI(RIData ri) throws GCUBEUnrecoverableException;
//	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	public boolean removeRIFromScope(ScopeRIParams params) throws GCUBEUnrecoverableException;
}
