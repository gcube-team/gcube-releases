package gr.uoa.di.madgik.workflow.client.library.stubs;

import static gr.uoa.di.madgik.workflow.client.library.utils.WorkflowEngineCLConstants.*;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.CONDORParams;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.GRIDParams;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.HADOOPParams;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.JDLParams;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.StatusReport;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.StatusRequest;
import gr.uoa.di.madgik.workflow.client.library.exceptions.WorkflowEngineException;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;


@WebService(name = porttypeLN, targetNamespace = porttypeNS)
public interface WorkflowEngineStub {

	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	String adaptJDL(JDLParams jdlParams) throws WorkflowEngineException;
	
	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	String adaptGRID(GRIDParams gridParams) throws WorkflowEngineException;
	
	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	String adaptHADOOP(HADOOPParams hadoopParams) throws WorkflowEngineException;
	
	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	String adaptCONDOR(CONDORParams condorParams) throws WorkflowEngineException;
	
	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	StatusReport executionStatus(StatusRequest statusRequest) throws WorkflowEngineException;
	
	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	String about(String about) throws WorkflowEngineException;
	
}
