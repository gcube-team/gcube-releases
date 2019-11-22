/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.rpc;

import java.util.List;

import org.gcube.portlets.user.td.taskswidget.shared.job.TdJobModel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdOperationModel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdTaskModel;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 12, 2013
 * 
 */
public interface TdTasksWidgetServiceAsync {

	void getTdServiceClientCapabilities(AsyncCallback<List<TdOperationModel>> callback);

	void getTdTasks(int start, int limit, boolean forceupdate,
			AsyncCallback<List<TdTaskModel>> callback);

	void countTdTasksFromCache(AsyncCallback<Integer> callback);

	void getTdTaskForId(String taskId, AsyncCallback<TdTaskModel> callback);

	void getListJobForTaskId(String taskId,
			AsyncCallback<List<TdJobModel>> callback);
	
	void setDegubTabularResource(boolean debug, String tabularResourceId,AsyncCallback<Void> callback);

}
