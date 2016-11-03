package gr.uoa.di.madgik.workflow.adaptor.utils.condor;

import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.workflow.adaptor.utils.IOutputResource;

public class OutputCondorResource implements IOutputResource
{
	public enum OutputType
	{
		OutputArchive
//		StdErr,
//		StdOut,
//		StdLog
	}
	
	/** 
	 * The Key identifying the output as provided to the respective {@link AttachedCondorResource#Key}
	 */
	public String Key=null;
	
	/** 
	 * The Variable id of the variable in the {@link ExecutionPlan#Variables} holding the Storage System
	 * id of the actual output file
	 */
	public String VariableID=null;
	public OutputType TypeOfOutput=OutputType.OutputArchive;

}
