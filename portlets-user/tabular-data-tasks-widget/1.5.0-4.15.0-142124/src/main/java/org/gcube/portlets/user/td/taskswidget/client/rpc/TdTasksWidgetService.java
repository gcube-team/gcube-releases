package org.gcube.portlets.user.td.taskswidget.client.rpc;

import java.util.List;

import org.gcube.portlets.user.td.taskswidget.shared.job.TdJobModel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdOperationModel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdTaskModel;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("tabularDataTasksService")
public interface TdTasksWidgetService extends RemoteService {

	List<TdTaskModel> getTdTasks(int start, int limit, boolean forceupdate) throws Exception;

	List<TdOperationModel> getTdServiceClientCapabilities() throws Exception;

	/**
	 * @return
	 * @throws Exception
	 */
	int countTdTasksFromCache() throws Exception;

	/**
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	TdTaskModel getTdTaskForId(String taskId) throws Exception;

	/**
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	List<TdJobModel> getListJobForTaskId(String taskId) throws Exception;

	/**
	 * @param debug
	 * @param tabularResourceId
	 */
	void setDegubTabularResource(boolean debug, String tabularResourceId);
}
