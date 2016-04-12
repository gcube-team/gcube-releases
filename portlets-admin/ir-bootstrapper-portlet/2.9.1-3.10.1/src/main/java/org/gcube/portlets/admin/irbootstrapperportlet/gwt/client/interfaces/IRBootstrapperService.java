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

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * A RemoteService interface for the IRBootstrapper service following GWT RPC design
 * 
 * @author Spyros Boutsis, NKUA
 */
public interface IRBootstrapperService  extends RemoteService {

	/**
	 * This method performs initialization tasks (such as parsing the
	 * IRBootstrapepr configuration in the current scope) and must be
	 * invoked when the portlet is initialized.
	 * @throws Exception Server side exception as dictated by GWTs RPC framework
	 */
	public void initialize() throws Exception;
	
	/**
	 * This method retrieves all the content collections' IDs
	 * @return The list of the collections' IDs
	 */
//	public ArrayList<String> getContentCollections();
	
	/**
	 * A method to get all the jobs available for each resource
	 * @return the list of jobs
     * @throws Exception Server side exception as dictated by GWTs RPC framework
	 */
	public List<ResourceTypeUIElement> getJobs() throws Exception;
	
	/**
	 * A method to refresh the list of available jobs for each resource and retrieve it
	 * @return the list of jobs
     * @throws Exception Server side exception as dictated by GWTs RPC framework
	 */
	public List<ResourceTypeUIElement> refreshAndGetJobs() throws Exception;
	
	/**
	 * A method to get all the submitted jobs available for each resource
	 * @return the list of submitted jobs
     * @throws Exception Server side exception as dictated by GWTs RPC framework
	 */
	public List<ResourceTypeUIElement> getSubmittedJobs() throws Exception;
	
	/**
	 * A method to get a submitted job, given its UID.
	 * @param the UID of the job to get
	 * @return the job UI element
     * @throws Exception Server side exception as dictated by GWTs RPC framework
	 */
	public JobUIElement getSubmittedJobByUID(String jobUID) throws Exception;
	
	/**
	 * A method to retrieve all the persisted submitted jobs
	 * @return The list of persisted submitted jobs
	 * @throws Exception Server side exception as dictated by GWTs RPC framework
	 */
	public HashMap<String, ArrayList<SubmittedJobInfoUIElement>> getPersistedSubmittedJobs() throws Exception;
	
	/**
	 * A method to submit a job for execution.
	 * @param jobUID the UID of the job to submit for execution
	 * @throws Exception
	 */
	public String submitJobForExecution(String jobUID, Map<String, String> userInputs) throws Exception;
	
	public String submitJobsForBatchExecution(ArrayList<String> jobsUIDs, Map<String, String> userInputs) throws Exception;
	
	/**
	 * A method to cancel a submitted job.
	 * @param jobUID the UID of the job to cancel
	 * @throws Exception
	 */
	public void cancelSubmittedJob(String jobUID) throws Exception;
	
	/**
	 * A method to remove a submitted job.
	 * @param jobUID the UID of the job to remove
	 * @throws Exception
	 */
	public void removeSubmittedJob(String jobUID) throws Exception;
	
	/**
	 * A method to get all the jobs available per each job type
	 * @return the list of jobs per job type
     * @throws Exception Server side exception as dictated by GWTs RPC framework
	 */
	public List<JobTypeUIElement> getAllJobsPerJobType() throws Exception;
	
	/**
	 * A method to save a new or modified job
	 * @param job the job to save
	 * @return the saved jobUID
	 * @throws Exception Server side exception as dictated by GWTs RPC framework
	 */
	public String saveJob(JobUIElement job) throws Exception;
	
	/**
	 * A method to delete an existing
	 * @param job the job to delete
	 * @throws Exception Server side exception as dictated by GWTs RPC framework
	 */
	public void deleteJob(JobUIElement job) throws Exception;
	
	/**
	 * A method to retrieve auto-complete data for all the available jobs
	 * @return the auto-complete data structure
	 * @throws Exception
	 */
	public JobAutoCompleteData getAutoCompleteData() throws Exception;
	
	public Boolean clearPersistedLogs();
}
