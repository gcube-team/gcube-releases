package gr.uoa.di.madgik.execution.engine;

/**
 * Configuration class defining the operation of the {@link ExecutionEngine}
 * 
 * @author gpapanikos
 */
public class ExecutionEngineConfig
{
	
	/** A constant value indicating infinite plans acceptable by the engine. */
	public static final int InfinitePlans=0;
	
	/** The Maximum number of plans. */
	private int MaximumNumberOfPlans=ExecutionEngineConfig.InfinitePlans;
	
	private String hostname = "localhost:4000";
	
	/**
	 * Instantiates a new execution engine config.
	 * 
	 * @param MaximumNumberOfPlans the maximum number of plans
	 */
	public ExecutionEngineConfig( int MaximumNumberOfPlans)
	{
		if(MaximumNumberOfPlans<=0) this.MaximumNumberOfPlans=ExecutionEngineConfig.InfinitePlans;
		else this.MaximumNumberOfPlans=MaximumNumberOfPlans;
	}
	
	/**
	 * Instantiates a new execution engine config.
	 * 
	 * @param MaximumNumberOfPlans the maximum number of plans
	 * @param hostname running hostname of the engine
	 * @param port running port of the engine
	 */
	public ExecutionEngineConfig( int MaximumNumberOfPlans, String hostname, int port) {
		this(MaximumNumberOfPlans);
		
		this.hostname = hostname + ":" +  String.valueOf(port);
	}
	
	/**
	 * Gets the maximum number of plans.
	 * 
	 * @return the maximum number of plans.
	 */
	public int GetMaximumNumberOfPlans()
	{
		return this.MaximumNumberOfPlans;
	}

	public String getHostname() {
		return hostname;
	}
}
