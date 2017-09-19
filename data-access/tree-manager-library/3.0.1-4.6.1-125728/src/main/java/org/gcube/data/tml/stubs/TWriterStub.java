package org.gcube.data.tml.stubs;

import static javax.jws.soap.SOAPBinding.ParameterStyle.*;
import static org.gcube.data.tml.Constants.*;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.gcube.data.tml.exceptions.InvalidTreeException;
import org.gcube.data.tml.exceptions.UnknownTreeException;
import org.gcube.data.tml.stubs.Types.NodeHolder;
import org.gcube.data.tml.stubs.Types.UnsupportedOperationFault;
import org.gcube.data.tml.stubs.Types.UnsupportedRequestFault;

/**
 * A stub for the T-Writer service.
 * 
 * @author Fabio Simeoni
 *
 */
@WebService(name=writerPortType,targetNamespace=namespace)
@SOAPBinding(parameterStyle=BARE)
public interface TWriterStub {

	public NodeHolder add(NodeHolder holder) throws UnsupportedOperationFault, 
													   UnsupportedRequestFault,
													   InvalidTreeException;

	@WebMethod(operationName="addRS")
	public String addStream(String locator) throws UnsupportedOperationFault, 
												   UnsupportedRequestFault;
	
	
	public NodeHolder update(NodeHolder holder) throws UnsupportedOperationFault, 
	   													UnsupportedRequestFault,
	   													UnknownTreeException,
	   													InvalidTreeException;
	
	@WebMethod(operationName="updateRS")
	public String updateStream(String locator) throws UnsupportedOperationFault, 
												   	  UnsupportedRequestFault;
}
