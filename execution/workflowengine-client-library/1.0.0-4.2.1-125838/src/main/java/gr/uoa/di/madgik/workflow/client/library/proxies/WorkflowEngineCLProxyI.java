package gr.uoa.di.madgik.workflow.client.library.proxies;

import gr.uoa.di.madgik.workflow.client.library.beans.Types.CONDORParams;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.GRIDParams;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.HADOOPParams;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.JDLParams;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.StatusReport;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.StatusRequest;
import gr.uoa.di.madgik.workflow.client.library.exceptions.WorkflowEngineException;


public interface WorkflowEngineCLProxyI {

	String about(String about) throws WorkflowEngineException;
	
	String adaptJDL(JDLParams jdlParams) throws WorkflowEngineException;
	
	String adaptGRID(GRIDParams gridParams) throws WorkflowEngineException;
	
	String adaptHADOOP(HADOOPParams hadoopParams) throws WorkflowEngineException;
	
	String adaptCONDOR(CONDORParams condorParams) throws WorkflowEngineException;
	
	StatusReport executionStatus(StatusRequest statusRequest) throws WorkflowEngineException;
	
}
