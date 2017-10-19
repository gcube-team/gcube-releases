package org.gcube.data.analysis.tabulardata.mobile.service;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

@WebService(targetNamespace="http://gcube-system.org/mobile")
@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL)
public interface MobileInterface {

	public static final String SERVICE_NAME = "mobileinterface";

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	List<String> getAllTabularResource(@WebParam(name="user")String user,@WebParam(name="scope") String scope);

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	String getData(@WebParam(name="user")String user,@WebParam(name="scope") String scope, @WebParam(name="trid")long trid,@WebParam(name="orderColumnId") String orderColumnId, @WebParam(name="orderType") MOrderDirection orderType,
			@WebParam(name="start") int start, @WebParam(name="limit") int limit);
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	int getCount(@WebParam(name="user")String user,@WebParam(name="scope") String scope, @WebParam(name="trid")long trid);
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	List<String> getTableDescription(@WebParam(name="user")String user,@WebParam(name="scope") String scope, @WebParam(name="trid")long trid);

}
