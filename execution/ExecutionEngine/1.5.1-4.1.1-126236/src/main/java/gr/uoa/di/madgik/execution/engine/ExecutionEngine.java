package gr.uoa.di.madgik.execution.engine;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.hint.EnvHint;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
import gr.uoa.di.madgik.execution.exception.ExecutionEngineFullException;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.is.InformationSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * This class acts as a singleton instance of an {@link ExecutionEngine} through which plans are submitted, executed
 * and managed.
 * 
 * @author gpapanikos
 */
public class ExecutionEngine 
{
	
	/** The Engine. */
	private static ExecutionEngine Engine=null;
	
	/** The object to synchronize access with */
	private static final Object lockMe=new Object();
	
	/** The execution engine configuration. */
	private ExecutionEngineConfig Config;
	
	/** The Executors that are managing the execution of the submitted plans. */
	private List<PlanExecutor> Executors=new ArrayList<PlanExecutor>();
	
	/** Execution engine topic producer ID */
	public static final String PRODUCERID = "ExecutionEngine";
	/** Execution engine load notification topic */
	public static final String LOADTOPICNAME = "NodeLoadMonitoring";
	
	/** Localhost name used for notifications */
	private static String localhost = null;

	public static String getLocalhost() {
		return localhost;
	}

	/**
	 * Initializes the single instance of the engine
	 * 
	 * @param Config the config
	 */
	public static void Init(ExecutionEngineConfig Config)
	{
		synchronized(ExecutionEngine.lockMe)
		{
			if(ExecutionEngine.Engine==null)
			{
				ExecutionEngine.Engine=new ExecutionEngine(Config);
			}
		}
	}
	
	/**
	 * Submit an execution plan to retrieve an {@link ExecutionHandle}
	 * 
	 * @param plan the plan
	 * 
	 * @return the execution handle
	 * 
	 * @throws ExecutionEngineFullException the execution engine full
	 * @throws ExecutionValidationException A validation error occurred
	 * @throws ExecutionInternalErrorException An internal error occurred
	 */
	public static ExecutionHandle Submit(ExecutionPlan plan) throws ExecutionEngineFullException, ExecutionValidationException, ExecutionInternalErrorException
	{
		synchronized(ExecutionEngine.lockMe)
		{
			if(ExecutionEngine.Engine==null) throw new ExecutionInternalErrorException("Execution engine has not been initialized");
			return ExecutionEngine.Engine.SubmitPlan(plan);
		}
	}
	
	/**
	 * Start the execution of the plan that is described by the provided {@link ExecutionHandle}
	 * 
	 * @param Handle the handle
	 * 
	 * @throws ExecutionInternalErrorException An internal error occurred
	 */
	public static void Execute(ExecutionHandle Handle) throws ExecutionInternalErrorException
	{
		synchronized(ExecutionEngine.lockMe)
		{
			if(ExecutionEngine.Engine==null) throw new ExecutionInternalErrorException("Execution engine has not been initialized");
			ExecutionEngine.Engine.ExecutePlan(Handle);
		}
	}

	/**
	 * Retrieves the status if the engine in this node
	 * 
	 * @return the engine status
	 * 
	 * @throws ExecutionInternalErrorException An internal error occurred
	 */
	public static EngineStatus GetEngineStatus() throws ExecutionInternalErrorException
	{
		synchronized(ExecutionEngine.lockMe)
		{
			if(ExecutionEngine.Engine==null) throw new ExecutionInternalErrorException("Execution engine has not been initialized");
			return ExecutionEngine.Engine.EngineStatus();
		}
	}
	
	/**
	 * Instantiates a new execution engine.
	 * 
	 * @param Config the config
	 */
	private ExecutionEngine(ExecutionEngineConfig Config)
	{
		this.Config=Config;
		ExecutionEngine.localhost = Config.getHostname();
	}
	
	/**
	 * Submit plan.
	 * 
	 * @param plan the plan
	 * 
	 * @return the execution handle
	 * 
	 * @throws ExecutionEngineFullException the execution engine full exception
	 * @throws ExecutionValidationException the execution validation exception
	 */
	private ExecutionHandle SubmitPlan(ExecutionPlan plan) throws ExecutionEngineFullException, ExecutionValidationException
	{
		plan.Validate();
		if(!this.CanAcceptNewPlan()) throw new ExecutionEngineFullException("Reached maximum number of executing plans");
		ExecutionHandle Handle=new ExecutionHandle(plan, getLocalhost());
		return Handle;
	}
	
	/**
	 * Execute plan.
	 * 
	 * @param Handle the handle
	 */
	private void ExecutePlan(ExecutionHandle Handle)
	{
		PlanExecutor ex=new PlanExecutor(Handle);
		this.Executors.add(ex);
		ex.start();
	}
	
	/**
	 * Can accept new plan.
	 * 
	 * @return true, if successful
	 */
	private boolean CanAcceptNewPlan()
	{
		int numberofplans=0;
		numberofplans=this.Executors.size();
		if(this.Config.GetMaximumNumberOfPlans()==ExecutionEngineConfig.InfinitePlans) return true;
		if(numberofplans<this.Config.GetMaximumNumberOfPlans()) return true;
		return false;
	}
	
	/**
	 * Engine status.
	 * 
	 * @return the engine status
	 */
	private EngineStatus EngineStatus()
	{
		EngineStatus stats=new EngineStatus();
		synchronized (ExecutionEngine.lockMe)
		{
			stats.NumberOfPlans=this.Executors.size();
			stats.NumberOfRunningPlans=0;
			stats.NumberOfCompletedPlans=0;
			stats.NumberOfReadyPlans=0;
			stats.NumberOfPausedPlans=0;
			stats.NumberOfCancelingPlans=0;
			stats.PercentageOfUtilization=0.0f;
			for(PlanExecutor exec : this.Executors)
			{
				switch(exec.GetHandle().GetHandleState())
				{
					case Completed:
					{
						stats.NumberOfCompletedPlans+=1;
						break;
					}
					case Ready:
					{
						stats.NumberOfReadyPlans+=1;
						stats.PercentageOfUtilization+=exec.GetHandle().GetPlan().Config.Utiliaztion;
						break;
					}
					case Paused:
					{
						stats.NumberOfPausedPlans+=1;
						break;
					}
					case Running:
					{
						stats.NumberOfRunningPlans+=1;
						stats.PercentageOfUtilization+=exec.GetHandle().GetPlan().Config.Utiliaztion;
						break;
					}
					case Cancel:
					{
						stats.NumberOfCancelingPlans+=1;
						break;
					}
				}
			}
		}
		return stats;
	}
	
	/**
	 * Removes the executor.
	 * 
	 * @param executor the executor
	 */
	protected static void RemoveExecutor(PlanExecutor executor)
	{
		synchronized (ExecutionEngine.lockMe)
		{
			executor.Dispose();
			ExecutionEngine.Engine.Executors.remove(executor);
		}
	}
	
	/**
	 * Checks if execution engine is initialized
	 * 
	 * @return
	 * 		true if execution engine is initialized, otherwise false
	 * @throws ExecutionInternalErrorException
	 */
	protected static boolean isInitialized() throws ExecutionInternalErrorException {
		synchronized(ExecutionEngine.lockMe) {
			if(ExecutionEngine.Engine==null)
				return false;
			return true;
		}
	}
}
