package org.gcube.portlets.widgets.wstaskexecutor.client.rpc;

import java.util.List;

import org.gcube.common.workspacetaskexecutor.shared.TaskOperator;
import org.gcube.common.workspacetaskexecutor.shared.TaskOutput;
import org.gcube.common.workspacetaskexecutor.shared.TaskParameterType;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskComputation;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskConfiguration;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskExecutionStatus;
import org.gcube.common.workspacetaskexecutor.shared.exception.ItemNotConfiguredException;
import org.gcube.common.workspacetaskexecutor.shared.exception.ItemNotExecutableException;
import org.gcube.common.workspacetaskexecutor.shared.exception.TaskErrorException;
import org.gcube.common.workspacetaskexecutor.shared.exception.TaskNotExecutableException;
import org.gcube.portlets.widgets.wstaskexecutor.shared.GcubeScope;
import org.gcube.portlets.widgets.wstaskexecutor.shared.SelectableOperator;
import org.gcube.portlets.widgets.wstaskexecutor.shared.WSItem;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;



/**
 * The Interface WsTaskExecutorWidgetService.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * May 16, 2018
 */
@RemoteServiceRelativePath("workspacetaskexecutor")
public interface WsTaskExecutorWidgetService extends RemoteService {

	/**
	 * Gets the list of scopes for logged user.
	 *
	 * @return the list of scopes for logged user
	 * @throws Exception the exception
	 */
	List<GcubeScope> getListOfScopesForLoggedUser() throws Exception;

	/**
	 * Monitor task execution status.
	 *
	 * @param configuration the configuration
	 * @param taskComputation the task computation
	 * @return the task execution status
	 * @throws TaskErrorException the task error exception
	 * @throws Exception the exception
	 */
	TaskExecutionStatus monitorTaskExecutionStatus(
		TaskConfiguration configuration, TaskComputation taskComputation) throws TaskErrorException, Exception;


	/**
	 * Check item task configurations.
	 *
	 * @param itemId the item id
	 * @return the list
	 * @throws ItemNotConfiguredException the item not configured exception
	 * @throws Exception the exception
	 */
	List<TaskConfiguration> checkItemTaskConfigurations(String itemId) throws ItemNotConfiguredException, Exception;


	/**
	 * Gets the availables parameter types.
	 *
	 * @return the availables parameter types
	 * @throws Exception the exception
	 */
	List<TaskParameterType> getAvailableParameterTypes() throws Exception;

	/**
	 * Creates the task configuration.
	 *
	 * @param itemId the item id
	 * @param taskConfiguration the task configuration
	 * @param isUpdate the is update
	 * @return the boolean
	 * @throws Exception the exception
	 */
	Boolean createTaskConfiguration(
		String itemId, TaskConfiguration taskConfiguration, boolean isUpdate)
		throws Exception;

	/**
	 * Gets the item task configurations.
	 *
	 * @param itemId the item id
	 * @return the item task configurations
	 * @throws Exception the exception
	 */
	List<TaskConfiguration> getItemTaskConfigurations(String itemId)
		throws Exception;

	/**
	 * Removes the task configuration.
	 *
	 * @param taskConfiguration the task configuration
	 * @return the boolean
	 * @throws ItemNotExecutableException the item not executable exception
	 * @throws Exception the exception
	 */
	Boolean removeTaskConfiguration(TaskConfiguration taskConfiguration)
		throws ItemNotExecutableException, Exception;


	/**
	 * Execute the task.
	 *
	 * @param taskConfiguration the task configuration
	 * @return the task execution status
	 * @throws ItemNotExecutableException the item not executable exception
	 * @throws TaskNotExecutableException the task not executable exception
	 * @throws Exception the exception
	 */
	TaskExecutionStatus executeTheTask(TaskConfiguration taskConfiguration)
		throws ItemNotExecutableException, TaskNotExecutableException,
		Exception;



	/**
	 * Gets the list operators per scope.
	 *
	 * @param scope the scope
	 * @param selectableOperators the selectable operators
	 * @return the list operators per scope
	 * @throws Exception the exception
	 */
	List<TaskOperator> getListOperatorsPerScope(String scope, SelectableOperator selectableOperators) throws Exception;


	/**
	 * Gets the output.
	 *
	 * @param taskConfiguration the task configuration
	 * @param taskComputation the task computation
	 * @return the output
	 * @throws TaskErrorException the task error exception
	 * @throws Exception the exception
	 */
	TaskOutput getOutput(
		TaskConfiguration taskConfiguration, TaskComputation taskComputation)
		throws TaskErrorException, Exception;


	/**
	 * Load item.
	 *
	 * @param itemId the item id
	 * @return the WS item
	 * @throws Exception the exception
	 */
	WSItem loadItem(String itemId) throws Exception;
}
