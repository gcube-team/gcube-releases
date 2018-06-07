package org.gcube.data.analysis.statisticalmanager.proxies;

import java.rmi.RemoteException;

import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.gcube.data.analysis.statisticalmanager.stubs.faults.StatisticalManagerFault;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputationRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputations;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMListGroupedAlgorithms;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMOutput;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMParameters;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMTypeParameter;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMComputation;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMOperationInfo;

public interface StatisticalManagerFactory {

	SMListGroupedAlgorithms getAlgorithms(SMTypeParameter... typeParameters);

	SMListGroupedAlgorithms getAlgorithmsUser(SMTypeParameter... typeParameters);

	SMParameters getAlgorithmParameters(String algorithm);

	SMOutput getAlgorithmOutputs(String request);

	SMOperationInfo getComputationInfo(String computationId, String user);

	SMComputation getComputation(String computationId);

	void removeComputation(String computationId);

	SMComputations getComputations(String user,
			SMTypeParameter... typeParameters);

	String executeComputation(SMComputationRequest requestComputation);

	String resubmitComputation(String computationId);

}
