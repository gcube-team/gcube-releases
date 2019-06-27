package org.gcube.vremanagement.vremodel.cl.stubs;

import java.util.Calendar;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.vremanagement.vremodel.cl.stubs.types.FunctionalityList;
import org.gcube.vremanagement.vremodel.cl.stubs.types.FunctionalityNodes;
import org.gcube.vremanagement.vremodel.cl.stubs.types.GHNArray;
import org.gcube.vremanagement.vremodel.cl.stubs.types.SetFunctionalityRequest;
import org.gcube.vremanagement.vremodel.cl.stubs.types.VREDescription;

import static org.gcube.vremanagement.vremodel.cl.Constants.*;

@WebService(name=manager_portType,targetNamespace=manager_target_namespace)
public interface ManagerStub {

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	void setDescription(VREDescription description);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	VREDescription getDescription(Empty empty);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	boolean isUseCloud(Empty empty);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	String getQuality(Empty empty);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	void setQuality(String quality);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	FunctionalityList getFunctionality(Empty empty);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	void setFunctionality(SetFunctionalityRequest functionality);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	FunctionalityNodes getFunctionalityNodes(Empty empty);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	void setGHNs(GHNArray ghns);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	void setVREtoPendingState(Empty empty);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	void deployVRE(Empty empty);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	void undeployVRE(Empty empty);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	void renewVRE(Calendar untilDate);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	String checkStatus(Empty empty);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	void setUseCloud(boolean useCloud);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	void setCloudVMs(int vms);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	int getCloudVMs(Empty empty);
	
}
