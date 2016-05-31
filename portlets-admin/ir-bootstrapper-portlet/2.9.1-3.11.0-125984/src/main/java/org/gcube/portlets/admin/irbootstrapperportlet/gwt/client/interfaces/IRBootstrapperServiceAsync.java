/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobTypeUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ResourceTypeUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.SubmittedJobInfoUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.autoComplete.JobAutoCompleteData;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * An asynchronous interface for the IRBootstrapper service following GWT RPC design
 * 
 * @author Spyros Boutsis, NKUA
 */
public interface IRBootstrapperServiceAsync {

	/**
	 * This method performs initialization tasks (such as parsing the
	 * IRBootstrapepr configuration in the current scope) and must be
	 * invoked when the portlet is initialized.
	 * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
	 */
	public void initialize(AsyncCallback<Void> callback);
	
	/**
	 * This method retrieves all the content collections' IDs
	 * @param callback -
	 * 			the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
	 */
//	public void getContentCollections(AsyncCallback<ArrayList<String>> callback);
	
	/**
	 * A method to get all the jobs available for each resource
	 * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
	 */
	public void getJobs(AsyncCallback<List<ResourceTypeUIElement>> callback);
	
	/**
	 * A method to refresh the list of available jobs for each resource and retrieve it
	 * @return the list of jobs
     * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
	 */
	public void refreshAndGetJobs(AsyncCallback<List<ResourceTypeUIElement>> callback);
	
	/**
	 * A method to get all the submitted jobs available for each resource
	 * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
	 */
	public void getSubmittedJobs(AsyncCallback<List<ResourceTypeUIElement>> callback);
	
	/**
	 * A method to get a submitted job, given its UID.
	 * @param the UID of the job to get
	 * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
	 */
	public void getSubmittedJobByUID(String jobUID, AsyncCallback<JobUIElement> callback);
	
	void submitJobForExecution(String jobUID, Map<String, String> userInputs,
			AsyncCallback<String> callback);
	
	/**
	 * A method to cancel a submitted job.
	 * @param jobUID the UID of the job to cancel
	 * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
	 */
	public void cancelSubmittedJob(String jobUID, AsyncCallback<Void> callback);
	
	/**
	 * A method to remove a submitted job
	 * @param jobUID the UID of the job to remove
	 * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
	 */
	public void removeSubmittedJob(String jobUID, AsyncCallback<Void> callback);
	
	/**
	 * A method to get all the jobs available per each job type
	 * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
	 */
	public void getAllJobsPerJobType(AsyncCallback<List<JobTypeUIElement>> callback);
	
	/**
	 * A method to save a new or modified job
	 * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
	 */
	public void saveJob(JobUIElement job, AsyncCallback<String> callback);
	
	/**
	 * A method to delete an job
	 * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
	 */
	public void deleteJob(JobUIElement job, AsyncCallback<Void> callback);
	
	/**
	 * A method to retrieve auto-complete data for all the available jobs
	 * @param callback -
     *            the AsyncCallback used to handle the asynchronous completion
     *            of the remote method, in accordance with the GWT RPC framework
	 */
	public void getAutoCompleteData(AsyncCallback<JobAutoCompleteData> callback);

	public void submitJobsForBatchExecution(ArrayList<String> jobsUIDs, Map<String, String> userInputs, AsyncCallback<String> callback);

	public void getPersistedSubmittedJobs(AsyncCallback<HashMap<String, ArrayList<SubmittedJobInfoUIElement>>> callback);

	public void clearPersistedLogs(AsyncCallback<Boolean> callback);
}
