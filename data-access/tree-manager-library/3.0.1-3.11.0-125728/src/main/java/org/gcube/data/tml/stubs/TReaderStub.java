package org.gcube.data.tml.stubs;

import static javax.jws.soap.SOAPBinding.ParameterStyle.BARE;
import static org.gcube.data.tml.Constants.namespace;
import static org.gcube.data.tml.Constants.readerPortType;

import javax.jws.*;
import javax.jws.soap.*;

import org.gcube.data.tml.exceptions.*;
import org.gcube.data.tml.proxies.*;
import org.gcube.data.tml.stubs.Types.LookupRequest;
import org.gcube.data.tml.stubs.Types.LookupStreamRequest;
import org.gcube.data.tml.stubs.Types.NodeHolder;
import org.gcube.data.tml.stubs.Types.QueryRequest;
import org.gcube.data.tml.stubs.Types.UnsupportedOperationFault;
import org.gcube.data.tml.stubs.Types.UnsupportedRequestFault;

/**
 * A stub for the T-Reader service.
 * 
 * @author Fabio Simeoni
 *
 */
@WebService(name=readerPortType,targetNamespace=namespace)
@SOAPBinding(parameterStyle=BARE)
public interface TReaderStub {


	@WebMethod(operationName="getByID")
	public NodeHolder lookup(LookupRequest request) throws UnsupportedOperationFault, 
													   UnsupportedRequestFault,
													   UnknownTreeException;
	
	@WebMethod(operationName="getNode")
	public NodeHolder lookupNode(Path request) throws UnsupportedOperationFault, 
													               UnsupportedRequestFault,
													               UnknownPathException;
	
	@WebMethod(operationName="getByIDs")
	public String lookupStream(LookupStreamRequest request) throws UnsupportedOperationFault, 
																  UnsupportedRequestFault;
	
	@WebMethod(operationName="getNodes")
	public String lookupNodeStream(String locator) throws UnsupportedOperationFault, 
														  UnsupportedRequestFault;
	
	@WebMethod(operationName="get")
	public String query(QueryRequest request) throws UnsupportedOperationFault,
											   	    UnsupportedRequestFault;
	
}
