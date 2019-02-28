package org.gcube.common.workspacetaskexecutor.shared;

import org.gcube.common.workspacetaskexecutor.shared.exception.ItemNotExecutableException;
import org.gcube.common.workspacetaskexecutor.shared.exception.TaskErrorException;
import org.gcube.common.workspacetaskexecutor.shared.exception.TaskNotExecutableException;


/**
 * The Interface ExecutableTask.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 2, 2018
 * @param <I> the generic type must extend {@link BaseTaskConfiguration}
 * @param <O> the generic type  must extend {@link BaseTaskExecutionStatus}
 */
public interface ExecutableTask<I extends BaseTaskConfiguration, C extends BaseTaskComputation, O extends BaseTaskExecutionStatus> {


	/**
	 * Execute run.
	 *
	 * @param taskConfiguration the task configuration
	 * @return the o
	 * @throws ItemNotExecutableException the item not executable exception
	 * @throws Exception the exception
	 */
	O executeRun(I taskConfiguration) throws ItemNotExecutableException, Exception;


	/**
	 * Abort run.
	 *
	 * @param taskConfiguration the task configuration
	 * @return the boolean
	 * @throws TaskErrorException the task error exception
	 * @throws TaskNotExecutableException the task not executable exception
	 * @throws Exception the exception
	 */
	Boolean abortRun(I taskConfiguration) throws TaskErrorException, TaskNotExecutableException, Exception;


	/**
	 * Monitor run status.
	 *
	 * @param taskConfiguration the task configuration
	 * @param taskComputation the task computation
	 * @return the o
	 * @throws TaskErrorException the task error exception
	 * @throws Exception the exception
	 */
	O monitorRunStatus(I taskConfiguration, C taskComputation) throws TaskErrorException, Exception;

}
