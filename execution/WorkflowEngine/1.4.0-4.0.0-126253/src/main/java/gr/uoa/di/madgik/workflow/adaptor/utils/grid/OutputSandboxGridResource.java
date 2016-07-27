package gr.uoa.di.madgik.workflow.adaptor.utils.grid;

import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.workflow.adaptor.WorkflowGridAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.utils.IOutputResource;

/**
 * This class acts as a container for information needed to retrieve the output files of a workflow created by the
 * {@link WorkflowGridAdaptor} and can be used to retrieve the workflow output after the successful execution 
 * of the created plan. Each instance of this class is created for each resource provided to the adaptor through an
 * {@link AttachedGridResource} that is marked as {@link AttachedGridResource.ResourceType#OutData}
 * 
 * @author gpapanikos
 */
public class OutputSandboxGridResource implements IOutputResource
{
	
	/** 
	 * The Key identifying the output as provided to the respective {@link AttachedGridResource#Key}
	 */
	public String Key=null;
	
	/** 
	 * The Variable id of the variable in the {@link ExecutionPlan#Variables} holding the Storage System
	 * id of the actual output file
	 */
	public String VariableID=null;
}
