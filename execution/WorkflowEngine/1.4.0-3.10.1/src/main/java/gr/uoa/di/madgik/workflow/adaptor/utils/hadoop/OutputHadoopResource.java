package gr.uoa.di.madgik.workflow.adaptor.utils.hadoop;

import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.workflow.adaptor.utils.IOutputResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.grid.AttachedGridResource;

public class OutputHadoopResource implements IOutputResource
{
	public enum OutputType
	{
		OutputArchive,
		StdErr,
		StdOut
	}
	
	/** 
	 * The Key identifying the output as provided to the respective {@link AttachedGridResource#Key}
	 */
	public String Key=null;
	
	/** 
	 * The Variable id of the variable in the {@link ExecutionPlan#Variables} holding the Storage System
	 * id of the actual output file
	 */
	public String VariableID=null;
	public OutputType TypeOfOutput=OutputType.OutputArchive;

}
