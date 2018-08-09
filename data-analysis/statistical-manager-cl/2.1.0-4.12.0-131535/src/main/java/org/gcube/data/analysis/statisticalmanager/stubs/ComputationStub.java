package org.gcube.data.analysis.statisticalmanager.stubs;


import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.computation__portType;
import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.computation_target_namespace;

import java.rmi.RemoteException;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.gcube.data.analysis.statisticalmanager.stubs.faults.StatisticalManagerFault;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMAbstractResource;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMOperationInfo;

@WebService(name=computation__portType,targetNamespace=computation_target_namespace)
public interface ComputationStub {

@SOAPBinding(parameterStyle=ParameterStyle.BARE) 
	public SMOperationInfo getComputationInfo(String requestComputationInfo)throws RemoteException,StatisticalManagerFault;
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public SMAbstractResource getOutput(String requestComputationOutput)throws RemoteException,StatisticalManagerFault;
    
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public void remove(String requestRemoveComputation)throws RemoteException,StatisticalManagerFault;
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public void stop(String requestRemoveComputation)throws RemoteException,StatisticalManagerFault;
	
}
