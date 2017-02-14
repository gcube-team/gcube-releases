package gr.uoa.di.madgik.workflow.adaptor.utils.jdl;

import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.workflow.adaptor.WorkflowJDLAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.utils.IOutputResource;

/**
 * This class acts as a container for information needed to retrieve the output files of a workflow created by the
 * {@link WorkflowJDLAdaptor} and can be used to retrieve the workflow output after the successful execution 
 * of the created plan. Each instance of this class is created for each output sandbox that is declared in the 
 * supplied JDL description
 * 
 * @author gpapanikos
 */
public class OutputSandboxJDLResource implements IOutputResource
{
	
	/** 
	 * The Variable id of the variable in the {@link ExecutionPlan#Variables} holding the Storage System
	 * id of the actual output file
	 */
	public String VariableID=null;
	
	/** The name this resource has in the respective sandbox that declares it */
	public String SandboxName=null;
	
	/** If the file is defined in the context of a DAG node, the node's name is stated here */
	public String NodeName=null;
	
	public int SandboxIndex=-1;
}
