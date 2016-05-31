package gr.uoa.di.madgik.execution.engine;

import java.util.UUID;

import gr.uoa.di.madgik.execution.engine.monitoring.ExecutionMonitor;
import gr.uoa.di.madgik.execution.engine.queue.PlanQueue;
import gr.uoa.di.madgik.execution.engine.queue.PlanQueueElement;
import gr.uoa.di.madgik.execution.exception.ExecutionEngineFullException;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;

/**
 * This class is a decorator of {@link ExecutionEngine} class with the deviation
 * that the execution tasks are queued until desired requirements are met. When
 * a new plan is called to be executed, it is then added up to a queue. The
 * queued plans are investigated either the threshold of the concurrent
 * executions per node are met, otherwise the plan is overtaken until a maximum
 * of times.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class QueueableExecutionEngine {
	
	private String RESOURCEID;	//XXX it will be removed

	/** Execution monitoring mechanism based on notifications */
	private static ExecutionMonitor executionMonitor;

	/** The queue that holds the execution handlers */
	private static PlanQueue planQueue;

	private static boolean terminate = false;

	private static Thread consumer;

	private final static Object lock = new String("Initialization lock");
	private static boolean initialized = false;
	
	private static synchronized boolean getTerminate() {
		return terminate;
	}

	private static synchronized void setTerminate(boolean term) {
		terminate = term;
	}

	/**
	 * Initializes the single instance of the engine;
	 */
	public static void Init(ExecutionEngineConfig Config, String resourceID) {
		synchronized (lock) {
			if (initialized == false)
				initialized = true;
			else
				return;
		}
		ExecutionEngine.Init(Config);
		executionMonitor = new ExecutionMonitor(resourceID);
		planQueue = new PlanQueue(executionMonitor);

		executionMonitor.init();

		consumer = new Thread() {
			public void run() {
				ExecutionHandle handler = null;

				try {
					while (!getTerminate()) {
						PlanQueueElement ele = null;
						try {
							ele = planQueue.takeFirst();
							if (ele == null) {
								if (!planQueue.isEmpty())
									synchronized (executionMonitor.getLoadUpdatesAvailable()) {
										executionMonitor.getLoadUpdatesAvailable().wait(60 * 1000); // XXX wait 1 min before retrying or an update happens.
									}
								continue;
							}
						} catch (InterruptedException e) {return;}

						handler = ele.getHandle();
						ExecutionEngine.Execute(handler);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		};
		consumer.start();

	}

	/**
	 * Submit an execution plan to retrieve an {@link ExecutionHandle}
	 * 
	 * @param plan
	 *            the plan
	 * @return the execution handle
	 * 
	 * @throws ExecutionEngineFullException
	 *             the execution engine full
	 * @throws ExecutionValidationException
	 *             A validation error occurred
	 * @throws ExecutionInternalErrorException
	 *             An internal error occurred
	 */
	public static ExecutionHandle Submit(ExecutionPlan plan) throws ExecutionEngineFullException, ExecutionValidationException, ExecutionInternalErrorException {
		synchronized (lock) {
			if (initialized == false)
				return null;
		}

		return ExecutionEngine.Submit(plan);
	}

	/**
	 * Submit an execution plan to retrieve an {@link ExecutionHandle}
	 * 
	 * @param plan
	 *            the plan
	 * @param util
	 *            the utilization factor. Leave null to keep plan's value.
	 * @param passedBy
	 *            the times a plan may be passed by. Leave null to keep plan's
	 *            value.
	 * @return the execution handle
	 * 
	 * @throws ExecutionEngineFullException
	 *             the execution engine full
	 * @throws ExecutionValidationException
	 *             A validation error occurred
	 * @throws ExecutionInternalErrorException
	 *             An internal error occurred
	 */
	public static ExecutionHandle Submit(ExecutionPlan plan, Float util, Integer passedBy) throws ExecutionEngineFullException, ExecutionValidationException,
			ExecutionInternalErrorException {
		synchronized (lock) {
			if (initialized == false)
				return null;
		}

		plan.Config.Utiliaztion = util == null ? plan.Config.Utiliaztion : util;
		plan.Config.PassedBy = passedBy == null ? plan.Config.PassedBy : passedBy;
		return ExecutionEngine.Submit(plan);
	}

	/**
	 * Start the execution of the plan that is described by the provided
	 * {@link ExecutionHandle}. The plan should be queued if desired
	 * requirements are not met.
	 * 
	 * @param Handle
	 *            the handle
	 * 
	 * @throws ExecutionInternalErrorException
	 *             An internal error occurred
	 */
	public static void Execute(ExecutionHandle Handle) throws ExecutionInternalErrorException {
		synchronized (lock) {
			if (initialized == false)
				return;
		}

		PlanQueueElement planElement = new PlanQueueElement(Handle.GetPlan().Config.Utiliaztion, Handle.GetPlan().Config.PassedBy, Handle);
		try {
			if(!ExecutionEngine.isInitialized())
				throw new ExecutionInternalErrorException("Execution engine has not been initialized");
			planQueue.putLast(planElement);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new ExecutionInternalErrorException("Interrupted exception occured", e);
		}
	}

	/**
	 * Retrieves the status of the engine in this node
	 * 
	 * @return the engine status
	 * 
	 * @throws ExecutionInternalErrorException
	 *             An internal error occurred
	 */
	public static EngineStatus GetEngineStatus() throws ExecutionInternalErrorException {
		synchronized (lock) {
			if (initialized == false)
				return null;
		}

		return ExecutionEngine.GetEngineStatus();
	}

	/**
	 * Removes the executor.
	 * 
	 * @param executor
	 *            the executor
	 */
	protected static void RemoveExecutor(PlanExecutor executor) {
		synchronized (lock) {
			if (initialized == false)
				return;
		}

		ExecutionEngine.RemoveExecutor(executor);
	}

	/**
	 * Terminates monitoring
	 */
	public static void Destroy() {
		synchronized (lock) {
			if (initialized == false)
				return;
		}
		setTerminate(true);
		executionMonitor.terminate();
		consumer.interrupt();
	}
}
