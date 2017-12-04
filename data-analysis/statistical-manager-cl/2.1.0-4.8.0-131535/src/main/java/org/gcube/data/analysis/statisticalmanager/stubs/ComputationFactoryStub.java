package org.gcube.data.analysis.statisticalmanager.stubs;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.*;

import java.rmi.RemoteException;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.gcube.data.analysis.statisticalmanager.stubs.faults.StatisticalManagerFault;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMAlgorithmsRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputationRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputations;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputationsRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMListGroupedAlgorithms;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMOutput;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMParameters;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMComputation;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMError;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMFile;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMObject;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMTable;

@WebService(name = computation_factory_portType, targetNamespace = computation_factory_target_namespace)
public interface ComputationFactoryStub {
	
	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	public String executeComputation(SMComputationRequest requestComputation)
			throws RemoteException,StatisticalManagerFault;

	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	public String resubmitComputation(String requestComputation)
			throws RemoteException,StatisticalManagerFault;

	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	public SMParameters getAlgorithmParameters(String request)
			throws RemoteException,StatisticalManagerFault;
	
	
	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	public SMOutput getAlgorithmOutputs(String request)
			throws RemoteException,StatisticalManagerFault;

	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	public SMListGroupedAlgorithms getAlgorithms(SMAlgorithmsRequest request)
			throws RemoteException,StatisticalManagerFault;

	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	public SMListGroupedAlgorithms getAlgorithmsUser(SMAlgorithmsRequest request)
			throws RemoteException,StatisticalManagerFault;

	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	public SMComputations getComputations(SMComputationsRequest request)
			throws RemoteException,StatisticalManagerFault;

	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	public SMComputation getComputation(String request)
			throws RemoteException,StatisticalManagerFault;

	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	public void removeComputation(java.lang.String requestRemoveComputation)
			throws RemoteException,StatisticalManagerFault;

}
