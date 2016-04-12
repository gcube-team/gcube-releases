package org.gcube.data.spd.stubs;

import static org.gcube.data.spd.client.Constants.*;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.data.spd.stubs.exceptions.InvalidIdentifierException;
import org.gcube.data.spd.stubs.exceptions.InvalidInputException;
import org.gcube.data.spd.stubs.exceptions.InvalidJobException;
import org.gcube.data.spd.stubs.types.Status;
import org.gcube.data.spd.stubs.types.SubmitJob;

@WebService(name=executor_portType,targetNamespace=executor_target_namespace)
public interface ExecutorStub {

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)	
	String submitJob(SubmitJob input ) throws InvalidInputException, InvalidJobException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)	
	String getResultLink(String id) throws InvalidIdentifierException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)	
	String getErrorLink(String id) throws InvalidIdentifierException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	Status getStatus(String id) throws InvalidIdentifierException; 
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	Empty removeJob(String id) throws InvalidIdentifierException; 
	
}
