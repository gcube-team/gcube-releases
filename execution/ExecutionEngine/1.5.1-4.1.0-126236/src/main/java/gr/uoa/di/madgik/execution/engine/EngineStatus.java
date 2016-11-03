package gr.uoa.di.madgik.execution.engine;

/**
 * This class acts as a placeholder for statistics concerning the status of the execution engine instance on  a node.
 * 
 * @author gpapanikos
 */
public class EngineStatus
{
	
	/** The Number of plans currently managed by the node. */
	public int NumberOfPlans=0;
	
	/** The Number of running plans. */
	public int NumberOfRunningPlans=0;
	
	/** The Number of completed plans. */
	public int NumberOfCompletedPlans=0;
	
	/** The Number of ready plans. */
	public int NumberOfReadyPlans=0;
	
	/** The Number of paused plans. */
	public int NumberOfPausedPlans=0;
	
	/** The Number of canceling plans. */
	public int NumberOfCancelingPlans=0;

	/** The percentage of utilization. */
	public float PercentageOfUtilization=0.0f;

}
