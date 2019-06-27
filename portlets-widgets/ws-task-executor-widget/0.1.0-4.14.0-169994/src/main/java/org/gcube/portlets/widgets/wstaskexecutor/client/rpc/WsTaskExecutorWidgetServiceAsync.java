/**
 *
 */
package org.gcube.portlets.widgets.wstaskexecutor.client.rpc;

import java.util.List;

import org.gcube.common.workspacetaskexecutor.shared.TaskOperator;
import org.gcube.common.workspacetaskexecutor.shared.TaskOutput;
import org.gcube.common.workspacetaskexecutor.shared.TaskParameterType;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskComputation;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskConfiguration;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskExecutionStatus;
import org.gcube.portlets.widgets.wstaskexecutor.shared.GcubeScope;
import org.gcube.portlets.widgets.wstaskexecutor.shared.SelectableOperator;
import org.gcube.portlets.widgets.wstaskexecutor.shared.WSItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;



/**
 * The Interface WsTaskExecutorWidgetServiceAsync.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 4, 2018
 */
public interface WsTaskExecutorWidgetServiceAsync {


    /**
     * The Class Util.
     *
     * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
     * May 4, 2018
     */
    public static final class Util
    {
        private static WsTaskExecutorWidgetServiceAsync instance;

        /**
         * Gets the single instance of Util.
         *
         * @return single instance of Util
         */
        public static final WsTaskExecutorWidgetServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (WsTaskExecutorWidgetServiceAsync) GWT.create( WsTaskExecutorWidgetService.class );
            }
            return instance;
        }

        /**
         * Instantiates a new util.
         */
        private Util()
        {
            // Utility class should not be instantiated
        }
    }

	/**
	 * Gets the list of scopes for logged user.
	 *
	 * @param asyncCallback the async callback
	 * @return the list of scopes for logged user
	 */
	void getListOfScopesForLoggedUser(AsyncCallback<List<GcubeScope>> asyncCallback);

	/**
	 * Check item task configurations.
	 *
	 * @param folderId the folder id
	 * @param asyncCallback the async callback
	 */
	void checkItemTaskConfigurations(String folderId, AsyncCallback<List<TaskConfiguration>> asyncCallback);


	/**
	 * Monitor task execution status.
	 *
	 * @param configuration the configuration
	 * @param taskComputation the task computation
	 * @param asyncCallback the async callback
	 */
	void monitorTaskExecutionStatus(
		TaskConfiguration configuration, TaskComputation taskComputation,
		AsyncCallback<TaskExecutionStatus> asyncCallback);



	/**
	 * Gets the available parameter types.
	 *
	 * @param asyncCallback the async callback
	 * @return the available parameter types
	 */
	void getAvailableParameterTypes(AsyncCallback<List<TaskParameterType>> asyncCallback);


	/**
	 * Creates the task configuration.
	 *
	 * @param itemId the item id
	 * @param taskConfiguration the task configuration
	 * @param isUpdate the is update
	 * @param asyncCallback the async callback
	 */
	void createTaskConfiguration(
		String itemId, TaskConfiguration taskConfiguration, boolean isUpdate, AsyncCallback<Boolean> asyncCallback);

	/**
	 * Gets the item task configurations.
	 *
	 * @param itemId the item id
	 * @param asyncCallback the async callback
	 * @return the item task configurations
	 */
	void getItemTaskConfigurations(String itemId, AsyncCallback<List<TaskConfiguration>> asyncCallback);


	/**
	 * Removes the task configuration.
	 *
	 * @param taskConfiguration the task configuration
	 * @param asyncCallback the async callback
	 */
	void removeTaskConfiguration(TaskConfiguration taskConfiguration, AsyncCallback<Boolean> asyncCallback);


	/**
	 * Execute the task.
	 *
	 * @param taskConfiguration the task configuration
	 * @param asyncCallback the async callback
	 */
	void executeTheTask(TaskConfiguration taskConfiguration, AsyncCallback<TaskExecutionStatus> asyncCallback);



	/**
	 * Gets the list operators per scope.
	 *
	 * @param scope the scope
	 * @param selectableOperators the selectable operators
	 * @param asyncCallback the async callback
	 * @return the list operators per scope
	 */
	void getListOperatorsPerScope(String scope, SelectableOperator selectableOperators, AsyncCallback<List<TaskOperator>> asyncCallback);


	/**
	 * Gets the output.
	 *
	 * @param taskConfiguration the task configuration
	 * @param taskComputation the task computation
	 * @param asyncCallback the async callback
	 * @return the output
	 */
	void getOutput(
		TaskConfiguration taskConfiguration, TaskComputation taskComputation, AsyncCallback<TaskOutput> asyncCallback);


	/**
	 * Load item.
	 *
	 * @param itemId the item id
	 * @param asyncCallback the async callback
	 */
	void loadItem(String itemId,  AsyncCallback<WSItem> asyncCallback);


}
