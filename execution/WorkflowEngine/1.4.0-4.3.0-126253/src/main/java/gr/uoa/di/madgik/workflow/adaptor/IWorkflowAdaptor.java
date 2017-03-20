package gr.uoa.di.madgik.workflow.adaptor;

import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.workflow.adaptor.utils.IAdaptorResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.IOutputResource;
import gr.uoa.di.madgik.workflow.exception.WorkflowEnvironmentException;
import gr.uoa.di.madgik.workflow.exception.WorkflowInternalErrorException;
import gr.uoa.di.madgik.workflow.exception.WorkflowSerializationException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import java.util.Set;

/**
 * The Interface IWorkflowAdaptor, and its implementers, are the main entry point to the Workflow Engine in order
 * to construct an internally managed workflow through the Execution Engine when the description of the workflow is
 * already know using one of the supported workflow definition languages.
 * 
 * @author gpapanikos
 */
public interface IWorkflowAdaptor
{
	
	/**
	 * Sets the adaptor resources. These include all the needed inputs that the workflow will need in order to 
	 * construct the internal execution plan and will be later needed during the execution of this plan. 
	 * 
	 * @param Resources the resources needed
	 * 
	 * @throws WorkflowValidationException Problem with the validity of one of the provided elements
	 */
	public void SetAdaptorResources(IAdaptorResources Resources) throws WorkflowValidationException;
	
	/**
	 * Based on the provided resources, a call to this method will do all the needed processing and once completed
	 * a full execution plan will be created
	 * 
	 * @throws WorkflowValidationException Problem with the validity of one of the provided elements
	 * @throws WorkflowSerializationException Problem with the serialization of one of the provided elements
	 * @throws WorkflowInternalErrorException An internal error has occurred
	 * @throws WorkflowEnvironmentException There was a problem with the environment the Workflow Engine is contained
	 */
	public void CreatePlan() throws WorkflowValidationException, WorkflowSerializationException,WorkflowInternalErrorException, WorkflowEnvironmentException;
	
	/**
	 * Retrieves the created plan after the successful invocation of the {@link IWorkflowAdaptor#CreatePlan()}
	 * 
	 * @return the execution plan
	 */
	public ExecutionPlan GetCreatedPlan();
	
	/**
	 * The retrieved set of output resources contains informations needed as to how to retrieve the outputs of the
	 * workflow after the execution of the created plan  
	 * 
	 * @return A set of the output resources retrieval information
	 */
	public Set<IOutputResource> GetOutput();
	
	/**
	 * Sets the execution id which will be associated with the created plan
	 * 
	 * @param ExecutionId The execution id
	 */
	public void SetExecutionId(String ExecutionId);
}
