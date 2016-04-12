/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server;

import gr.uoa.di.madgik.taskexecutionlogger.model.TaskLogEntry;
import gr.uoa.di.madgik.taskexecutionlogger.model.WorkflowLogEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.http.HttpSession;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;



//import org.apache.axis.components.uuid.UUIDGen;
//import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.AssignUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ExecutionEntityUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ExecutionTypeUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobTypeUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ParallelExecutionUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ResourceTypeUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ResourceUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.SequentialExecutionUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.SubmittedJobInfoUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.TaskInfoUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.TaskUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.UILogEntry;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ExecutionEntityUIElement.UIExecutionState;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.UILogEntry.UILogEntryLevel;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.autoComplete.JobAutoCompleteData;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.autoComplete.JobAutoCompleteEntryDesc;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.autoComplete.ObjectDesc;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.autoComplete.ObjectNameAndPtr;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.IRBootstrapperService;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.logging.LogEntry;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.logging.TaskExecutionLogger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.logging.LogEntry.LogEntryLevel;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.Resource;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.ExecutionEntity;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.ExecutionEntity.ExecutionState;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.DataType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.job.JobType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task.AssignTaskType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task.CustomTaskType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task.ExecutionType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task.ParallelExecution;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task.SequentialExecution;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.EntityExecutionData;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.shared.UnavailableScopeException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class IRBootstrapperServiceImpl extends RemoteServiceServlet implements IRBootstrapperService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1721183802995619036L;

	/** Logger */
	private static Logger logger = Logger.getLogger(IRBootstrapperServiceImpl.class);

	/**
	 * Task Execution Logger for persisted workflow logs
	 */
	public static gr.uoa.di.madgik.taskexecutionlogger.TaskExecutionLogger tLogger = gr.uoa.di.madgik.taskexecutionlogger.TaskExecutionLogger.getLogger();
	
	/** A map that contains all the submitted jobs instances as workflow entries, by job instance UID. */
	public static Map<String, WorkflowLogEntry> submittedJobEntries = new HashMap<String, WorkflowLogEntry>();


	/**
	 * Class constructor
	 */
	public IRBootstrapperServiceImpl() {
		try {
			Util.initialize();
		} catch (Exception e) {
			logger.error("Error while initializing.", e);
		}
	}

	/**
	 * Returns the current GCUBE scope
	 * @return the scope
	 * @throws UnavailableScopeException 
	 */
	private String getScope() throws UnavailableScopeException {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		String username = httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
		if (username != null) {
			ASLSession session = SessionManager.getInstance().getASLSession(httpSession.getId(), username);
			String scope = session.getScope();
			if (scope != null)
				return scope;
			else {
				throw new UnavailableScopeException("Scope is not available. Current session has expired");
			}
		}
		else {
			throw new UnavailableScopeException("Scope is not available. Current session has expired");
		}
			
	}
	
	/**
	 * Returns the active {@link ASLSession} object
	 * @return
	 */
	private ASLSession getSession() {
		/* Get the ASL session object */
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		String username = httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
		return SessionManager.getInstance().getASLSession(httpSession.getId(), username);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.IRBootstrapperService#initialize()
	 */
	synchronized public void initialize() throws Exception {
		logger.info("calling initialize function");
		try{
			logger.info("doing actual initialization");
			String scope;
			try {
				scope = getScope();
			} catch (UnavailableScopeException e1) {
				logger.error("Scope is unavailable. Initialization cannot be done");
				throw(e1);
			}
			BootstrappingConfiguration conf = IRBootstrapperData.getInstance().getBootstrappingConfiguration(scope);
			if (conf == null) {
				conf = new BootstrappingConfiguration();
				IRBootstrapperData.getInstance().setBootstrappingConfiguration(scope, conf);
			}
			conf.initialize(scope);
		}
		catch(Exception e){
			logger.error("Initialization failed. An exception was thrown", e);
			e.printStackTrace();
			throw e;
		}			
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.IRBootstrapperService#getJobs()
	 */
	public List<ResourceTypeUIElement> getJobs() throws Exception {
		List<ResourceTypeUIElement> resourceTypeList = new LinkedList<ResourceTypeUIElement>();
		BootstrappingConfiguration conf = IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope());
		List<JobType> jobs = new LinkedList<JobType>();

		Map<String, List<JobType>> jobMappings = conf.getAllJobInstances();

		/* Loop through the list of available job types */
		for (String jobType : jobMappings.keySet()) {

			/* For each available job type, get the list of job instances and add them to the job list */
			List<JobType> jobInstances = jobMappings.get(jobType);
			if (jobInstances != null) {
				jobs.addAll(jobInstances);

				/* Loop through the list of job instances */
				for (JobType job : jobInstances) {

					/* Get the job's input resource type and check if this type has already been
					 * added to the result. If not, add it. If yes, retrieve the existing
					 * ResourceTypeUIElement from the result.
					 */
					String resourceType = job.getInput().getAssociatedResource().getResourceTypeName();
					ResourceTypeUIElement rt = new ResourceTypeUIElement();
					rt.setUID(resourceType);
					rt.setName(resourceType);
					if (!resourceTypeList.contains(rt)) {
						resourceTypeList.add(rt);
					} else {
						for (ResourceTypeUIElement e : resourceTypeList) {
							if (e.equals(rt)) {
								rt = e;
								break;
							}
						}
					}

					/* Now check if the job's input resource has been already added to the
					 * ResourceTypeUIElement as a ResourceUIElement. If not, add it. If yes,
					 * retrieve the existing ResourceUIElement from the ResourceTypeUIElement. */
					DataType jobInput = job.getInput();
					ResourceUIElement resource = rt.findResource(jobInput.getUID());
					if (resource == null) {
						resource = new ResourceUIElement();
						resource.setUID(jobInput.getUID());
						resource.setName(jobInput.getUIName());
						resource.setDescription(jobInput.getUIDescription());
					//	if (jobInput instanceof MetadataCollectionDataType) {
					//		logger.debug("Setting the content collection ID to the ResourceUIElement....");
					//		logger.debug(jobInput.getAttributeValue(MetadataCollectionDataType.ATTR_RELATEDCOLID));
					//		resource.setContentCollectionID(jobInput.getAttributeValue(MetadataCollectionDataType.ATTR_RELATEDCOLID));
					//	}
						//
						rt.addResource(resource);					
					}

					/* Add the job to the ResourceUIElement as a JobUIElement child. */
					resource.addJob((JobUIElement) UIElementFromExecutionEntity(job));
				}
			}
		}

		return resourceTypeList;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.IRBootstrapperService#refreshAndGetJobs()
	 */
	public List<ResourceTypeUIElement> refreshAndGetJobs() throws Exception {
		String scope = getScope();
		BootstrappingConfiguration conf = IRBootstrapperData.getInstance().getBootstrappingConfiguration(scope);
		if (conf == null)
			throw new Exception();
		conf.initialize(scope);	/* Refresh */
		return getJobs();		/* Retrieve */
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.IRBootstrapperService#getSubmittedJobs()
	 */
	public List<ResourceTypeUIElement> getSubmittedJobs() throws Exception {
		List<ResourceTypeUIElement> resourceTypeList = new LinkedList<ResourceTypeUIElement>();
		BootstrappingConfiguration conf = IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope());
		List<JobType> jobs = new LinkedList<JobType>();

		/* For each available job, get the list of submitted job instances and add them to the job list */
		List<JobType> submittedJobInstances = conf.getSubmittedJobInstances();
		if (submittedJobInstances != null) {
			jobs.addAll(submittedJobInstances);

			/* Loop through the list of job instances */
			for (JobType job : submittedJobInstances) {

				/* Get the job's input resource type and check if this type has already been
				 * added to the result. If not, add it. If yes, retrieve the existing
				 * ResourceTypeUIElement from the result.
				 */
				String resourceType = job.getInput().getAssociatedResource().getResourceTypeName();
				ResourceTypeUIElement rt = new ResourceTypeUIElement();
				rt.setUID(resourceType);
				rt.setName(resourceType);
				if (!resourceTypeList.contains(rt))
					resourceTypeList.add(rt);
				else {
					for (ResourceTypeUIElement e : resourceTypeList) {
						if (e.equals(rt)) {
							rt = e;
							break;
						}
					}
				}

				/* Now check if the job's input resource has been already added to the
				 * ResourceTypeUIElement as a ResourceUIElement. If not, add it. If yes,
				 * retrieve the existing ResourceUIElement from the ResourceTypeUIElement. */
				DataType jobInput = job.getInput();
				ResourceUIElement resource = rt.findResource(jobInput.getUID());
				if (resource == null) {
					resource = new ResourceUIElement();
					resource.setUID(jobInput.getUID());
					resource.setName(jobInput.getUIName());
					resource.setDescription(jobInput.getUIDescription());
					rt.addResource(resource);					
				}

				/* Add the job to the ResourceUIElement as a JobUIElement child. */
				resource.addJob((JobUIElement) UIElementFromExecutionEntity(job));
			}
		}

		return resourceTypeList;
	}
	
//	/* (non-Javadoc)
//	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.IRBootstrapperService#getSubmittedJobs()
//	 */
//	public List<SubmittedJobInfoUIElement> getPersistedSubmittedJobs() throws Exception {
//		List<SubmittedJobInfoUIElement> jobs = new ArrayList<SubmittedJobInfoUIElement>();
//
//		/* For each available job, get the list of submitted job instances and add them to the job list */
//		List<WorkflowLogEntry> entries = tLogger.getLogEntriesAsObject();
//		if (entries != null) {
//			for (WorkflowLogEntry entry : entries) {
//				List<TaskLogEntry> taskMessages = entry.getEntries();
//				List<TaskInfoUIElement> taskMessagesBeanList = new ArrayList<TaskInfoUIElement>();
//				if (taskMessages != null) {
//					for (TaskLogEntry message : taskMessages) {
//						taskMessagesBeanList.add(new TaskInfoUIElement(message.getLevel().toString(), message.getMessage(), message.getDate()));
//					}
//				}
//				SubmittedJobInfoUIElement jobInfo = new SubmittedJobInfoUIElement(entry.getUid(), entry.getName(), entry.getType(), entry.getSubmitter(),
//						entry.getStatus(), entry.getStartDate(), entry.getEndDate(), entry.getDescription(), entry.getScope(), taskMessagesBeanList);
//				jobs.add(jobInfo);
//			}
//		}
//
//		return jobs;
//	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.IRBootstrapperService#getSubmittedJobs()
	 */
	public HashMap<String, ArrayList<SubmittedJobInfoUIElement>> getPersistedSubmittedJobs() throws Exception {
		String scope = getScope();
		HashMap<String, ArrayList<SubmittedJobInfoUIElement>> jobs = new HashMap<String, ArrayList<SubmittedJobInfoUIElement>>();

		/* For each available job, get the list of submitted job instances and add them to the job list */
		List<WorkflowLogEntry> entries = tLogger.getLogEntriesAsObject();
		if (entries != null) {
			for (WorkflowLogEntry entry : entries) {
				if (!entry.getScope().equals(scope))
					continue;
				ArrayList<SubmittedJobInfoUIElement> jobsForType = jobs.get(entry.getType());
				if (jobsForType == null) {
					jobsForType = new ArrayList<SubmittedJobInfoUIElement>();
				}
				List<TaskLogEntry> taskMessages = entry.getEntries();
				List<TaskInfoUIElement> taskMessagesBeanList = new ArrayList<TaskInfoUIElement>();
				if (taskMessages != null) {
					for (TaskLogEntry message : taskMessages) {
						taskMessagesBeanList.add(new TaskInfoUIElement(message.getLevel().toString(), message.getMessage(), message.getDate()));
					}
				}
				SubmittedJobInfoUIElement jobInfo = new SubmittedJobInfoUIElement(entry.getUid(), entry.getName(), entry.getType(), entry.getSubmitter(),
						entry.getStatus(), entry.getStartDate(), entry.getEndDate(), entry.getDescription(), entry.getScope(), taskMessagesBeanList);
				jobsForType.add(jobInfo);
				jobs.put(entry.getType(), jobsForType);
			}
		}

		return jobs;
	}
	
	public Boolean clearPersistedLogs() {
		return tLogger.clearLogEntries();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.IRBootstrapperService#getSubmittedJobByUID(java.lang.String)
	 */
	public JobUIElement getSubmittedJobByUID(String jobUID) throws Exception {
		BootstrappingConfiguration conf = IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope());
		JobType job = conf.getSubmittedJobInstanceByUID(jobUID);
		if (job == null)
			return null;

		return (JobUIElement) UIElementFromExecutionEntity(job);
	}

	/**
	 * Creates a tree of {@link ExecutionEntityUIElement} objects representing the task tree
	 * of the given {@link JobType} object.
	 * @param entity the job whose task tree to process
	 * @return the root of the generated tree of UI elements
	 */
	private ExecutionEntityUIElement jobTaskTreeToUIElementTree(JobType job) {
		LinkedList<Object[]> entityAndUIElementPairs = new LinkedList<Object[]>();

		/* Pair the root of the given job's task tree with a corresponding UI element
		 * and add the pair to the list */
		ExecutionEntityUIElement rootUIElement = UIElementFromExecutionEntity(job.getTaskTree());
		entityAndUIElementPairs.add(new Object[] { job.getTaskTree(), rootUIElement });

		/* While the list is not empty, process its items */ 
		while (!entityAndUIElementPairs.isEmpty()) {

			/* Retrieve a pair from the list */
			Object[] pair = entityAndUIElementPairs.poll();
			ExecutionEntity pairEntity = (ExecutionEntity) pair[0];
			ExecutionEntityUIElement pairElement = (ExecutionEntityUIElement) pair[1];

			/* If the execution entity contained in the pair is of type 'ExecutionType', then
			 * it contains some child entities which we need to process. In this case, for
			 * each child entity create a corresponding UI element and add it to the list,
			 * paired with the entity it was generated from.
			 * If the execution entity is of any other type, do nothing (there are no children
			 * to process)
			 */
			if (pairEntity instanceof ExecutionType) {
				ExecutionTypeUIElement executionTypeUIElement = (ExecutionTypeUIElement) pairElement;
				for (ExecutionEntity ee : ((ExecutionType) pairEntity).getEntitiesToBeExecuted()) {
					ExecutionEntityUIElement childUIElement = UIElementFromExecutionEntity(ee);
					executionTypeUIElement.addTask(childUIElement);
					childUIElement.setParentUID(pairElement.getUID());
					entityAndUIElementPairs.add(new Object[] { ee, childUIElement});
				}
			}

		}

		return rootUIElement;
	}

	/**
	 * Returns the corresponding portlet-side ExecutionState value for a given servlet-side ExecutionState value
	 * @param servletExecutionState the servlet-side ExecutionState value
	 * @return the corresponding portlet-side ExecutionState value
	 */
	private UIExecutionState servletToPortletExecutionState(ExecutionState servletExecutionState) {
		if (servletExecutionState == ExecutionState.NOT_STARTED)
			return UIExecutionState.NOT_STARTED;
		else if (servletExecutionState == ExecutionState.RUNNING)
			return UIExecutionState.RUNNING;
		else if (servletExecutionState == ExecutionState.COMPLETED_SUCCESS)
			return UIExecutionState.COMPLETED_SUCCESS;
		else if (servletExecutionState == ExecutionState.COMPLETED_FAILURE)
			return UIExecutionState.COMPLETED_FAILURE;
		else if (servletExecutionState == ExecutionState.COMPLETED_WARNINGS)
			return UIExecutionState.COMPLETED_WARNINGS;
		else if (servletExecutionState == ExecutionState.CANCELLED)
			return UIExecutionState.CANCELLED;
		return null;
	}

	/**
	 * Creates a {@link ExecutionEntityUIElement} object from a given {@link ExecutionEntity} object
	 * @param entity the source {@link ExecutionEntity} object
	 * @return the new {@link ExecutionEntityUIElement} object
	 */
	private ExecutionEntityUIElement UIElementFromExecutionEntity(ExecutionEntity entity) {
		ExecutionEntityUIElement element = null;

		if (entity instanceof SequentialExecution)
			element = new SequentialExecutionUIElement();
		else if (entity instanceof ParallelExecution)
			element = new ParallelExecutionUIElement();
		else if (entity instanceof AssignTaskType)
			element = new AssignUIElement();
		else if (entity instanceof CustomTaskType)
			element = new TaskUIElement();
		else
			element = new JobUIElement();

		element.setUID(entity.getUID());
		element.setName(entity.getName());
		element.setDescription(entity.getUIDescription());
		element.setIsFulfilled(entity.isFulfilled());
		element.setExecutionState(servletToPortletExecutionState(entity.getExecutionState()));
		TaskExecutionLogger log = entity.getExecutionLogger();
		if (log != null) {
			LinkedList<LogEntry> entityLogEntries = log.getLogEntries();
			LinkedList<UILogEntry> elementLogEntries = element.getExecutionLog();
			for (LogEntry le : entityLogEntries) {
				UILogEntryLevel uiLevel;
				LogEntryLevel level = le.getLevel();
				if (level == LogEntryLevel.TYPE_ERROR)
					uiLevel = UILogEntryLevel.TYPE_ERROR;
				else if (level == LogEntryLevel.TYPE_WARNING)
					uiLevel = UILogEntryLevel.TYPE_WARNING;
				else
					uiLevel = UILogEntryLevel.TYPE_INFORMATION;
				elementLogEntries.add(new UILogEntry(le.getMessage(), uiLevel));
			}
		}

		if (element instanceof AssignUIElement) {
			AssignUIElement aElement = (AssignUIElement) element;
			AssignTaskType aEntity = (AssignTaskType) entity;
			aElement.setAssignFrom(aEntity.getAssignFrom());
			aElement.setAssignTo(aEntity.getAssignTo());
			aElement.setRequiresUserInput(new Boolean(aEntity.requiresUserInput()));
			aElement.setUserInputLabel(aEntity.getUserInputLabel());
			aElement.setName(entity.getParent().getTypeName());
		}
		else if (element instanceof JobUIElement) {
			boolean jobRequiresUserInput = false;

			JobType jobEntity = (JobType) entity;
			JobUIElement jobElement = (JobUIElement) element;
			jobElement.setJobTypeName(jobEntity.getTypeName());
			jobElement.setJobExtends(jobEntity.getBaseJobName());
			jobElement.setTaskTree(jobTaskTreeToUIElementTree(jobEntity));
			List<AssignTaskType> initAssignments = jobEntity.getInitAssignments();
			if (initAssignments != null) {
				List<AssignUIElement> initAssignmentsUI = new LinkedList<AssignUIElement>();
				for (AssignTaskType att : initAssignments) {
					if (att.requiresUserInput() && jobRequiresUserInput == false)
						jobRequiresUserInput = att.requiresUserInput();
					initAssignmentsUI.add((AssignUIElement) UIElementFromExecutionEntity(att));
				}
				jobElement.setInitAssignments(initAssignmentsUI);
			}
			jobElement.setRequiresUserInput(new Boolean(jobRequiresUserInput));
		}

		return element;
	}

	/**
	 * Creates a list of {@link AssignTaskType} objects given a list of {@link AssignUIElement} objects
	 * @param UIAssignList
	 * @return
	 */
	private List<AssignTaskType> assignListFromUIAssignList(BootstrappingConfiguration conf, List<AssignUIElement> UIAssignList) {
		List<AssignTaskType> ret = new LinkedList<AssignTaskType>();
		for (AssignUIElement UIassign : UIAssignList) {
			AssignTaskType assign = (AssignTaskType) conf.newTaskObject(ConfigurationParserConstants.ASSIGN_ELEMENT);
			assign.setAssignFrom(UIassign.getAssignFrom());
			assign.setAssignTo(UIassign.getAssignTo());
			//TODO
			if (UIassign.getUserInputLabel() != null && UIassign.getUserInputLabel().length()>0)
				assign.setUserInputLabel(UIassign.getUserInputLabel());
			ret.add(assign);
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.IRBootstrapperService#submitJobForExecution(java.lang.String)
	 */
	public String submitJobForExecution(String jobUID, Map<String, String> userInputs) throws Exception {

		/* Construct the EntityExecutionData object that will be used during the
		 * submitted job's execution.
		 */
		EntityExecutionData eed = new EntityExecutionData();
		eed.setSession(getSession());

		/* Now submit the job, along with the new EntityExecutionData object */
		BootstrappingConfiguration conf = IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope());
		return conf.submitJobForExecution(jobUID, eed, userInputs);
	}

	public String submitJobsForBatchExecution(ArrayList<String> jobsUIDs, Map<String, String> userInputs) throws Exception {

		/* Construct the EntityExecutionData object that will be used during the
		 * submitted job's execution.
		 */
		EntityExecutionData eed = new EntityExecutionData();
		eed.setSession(getSession());

		/* Now submit the job, along with the new EntityExecutionData object */
		BootstrappingConfiguration conf = IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope());
		return conf.submitJobsForExecution(jobsUIDs, eed, userInputs);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.IRBootstrapperService#cancelSubmittedJob(java.lang.String)
	 */
	public void cancelSubmittedJob(String jobUID) throws Exception {
		BootstrappingConfiguration conf = IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope());
		conf.getSubmittedJobInstanceByUID(jobUID).cancel();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.IRBootstrapperService#removeSubmittedJob(java.lang.String)
	 */
	public void removeSubmittedJob(String jobUID) throws Exception {
		BootstrappingConfiguration conf = IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope());
		conf.removeSubmittedJobInstance(jobUID);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.IRBootstrapperService#getAllJobsPerJobType()
	 */
	public List<JobTypeUIElement> getAllJobsPerJobType() throws Exception {
		BootstrappingConfiguration conf = IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope());
		List<JobTypeUIElement> jobs = new LinkedList<JobTypeUIElement>();
		try {
			Map<String, List<JobType>> jobsPerJobType = conf.getAllJobsPerJobType();

			
			for (String jobTypeName : jobsPerJobType.keySet()) {
				JobTypeUIElement jobTypeEl = new JobTypeUIElement();
				jobTypeEl.setJobTypeName(jobTypeName);
				jobTypeEl.setJobTypeObject((JobUIElement) UIElementFromExecutionEntity(conf.getJobTypeByName(jobTypeName)));
				for (JobType j : jobsPerJobType.get(jobTypeName)) {
					jobTypeEl.addJob((JobUIElement) UIElementFromExecutionEntity(j));
				}
				jobs.add(jobTypeEl);
			}
		//	return jobs;
		} catch (Exception e) {
			logger.error("Exception: ", e);
			e.printStackTrace();
		}
		return jobs;
	}

	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.IRBootstrapperService#saveJob(org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement)
	 */
	public String saveJob(JobUIElement job) throws Exception {
		try {
			JobType jobObject = null;
			String jobUID = job.getUID();

			BootstrappingConfiguration conf = IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope());
			if (jobUID == null) {
				String jobTypeName = job.getJobTypeName();
				String jobName = job.getName();
				String jobExtends = job.getJobExtends();
				jobObject = conf.getallJobTypes().get(jobTypeName).newInstance(jobName, jobExtends!=null ? jobExtends : null);
				conf.getJobs().add(jobObject);

				jobUID = jobObject.getUID();
			}
			else {
				/* Find the job that is being edited */
				for (JobType j : conf.getJobs()) {
					if (j.getUID().equals(jobUID)) {
						jobObject = j;
						break;
					}
				}
				if (jobObject == null)
					throw new Exception("Job not found!");
			}

			/* Set the job attributes */
			jobObject.setJobName(job.getName());
			jobObject.setJobTypeName(job.getJobTypeName());
			jobObject.setBaseJobName(job.getJobExtends());

			/* Set the list of assignments on the job object */
			List<AssignTaskType> assignments = assignListFromUIAssignList(conf, job.getInitAssignments());
			for (AssignTaskType a : assignments)
				a.setParent(jobObject);
			jobObject.setInitAssignments(assignments);

			/* Save the modified bootstrapper configuration */
			conf.saveConfiguration();

			/* Return UID of the saved job */
			return jobUID;
		} catch (Exception e) {
			logger.error("Failed to save the job.", e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.IRBootstrapperService#deleteJob(org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement)
	 */
	public void deleteJob(JobUIElement job) throws Exception {
		BootstrappingConfiguration conf = IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope());
		String jobTypeName = job.getJobTypeName();
		String jobName = job.getName();

		for (JobType jt : conf.getAllJobsPerJobType().get(jobTypeName)) {
			if (jt.getName().equals(jobName)) {
				conf.getAllJobsPerJobType().get(jobTypeName).remove(jt);
				break;
			}
		}

		for (JobType jt : conf.getJobs()) {
			if (jt.getName().equals(jobName)) {
				conf.getJobs().remove(jt);
				break;
			}
		}

		conf.saveConfiguration();
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.IRBootstrapperService#getAutoCompleteData()
	 */
	public JobAutoCompleteData getAutoCompleteData() throws Exception {
		BootstrappingConfiguration conf = IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope());
		XPath xpath = XPathFactory.newInstance().newXPath();
		Map<String, ObjectDesc> objectDescStore = new HashMap<String, ObjectDesc>();
		List<JobAutoCompleteEntryDesc> autoCompleteData = new LinkedList<JobAutoCompleteEntryDesc>();

		/* Create descriptors for the currently loaded data types */
		Map<String, DataType<? extends Resource>> dataTypes = conf.getDataTypes();
		for (Entry<String, DataType<? extends Resource>> entry : dataTypes.entrySet()) {
			DataType dataType = entry.getValue();
			ObjectDesc objDesc = new ObjectDesc("dataType!" + entry.getKey());
			objectDescStore.put(objDesc.getID(), objDesc);
			Element n = dataType.getXMLTypeDefinitionDocument().getDocumentElement();				
			try {
				ObjectDesc typeDefinitionOD = XMLElementToAutoCompleteObjectDesc(n, xpath, objectDescStore);

				/* In the case of data types, there is no "root" element enclosing all the datatype attributes.
				 * For this reason, the type definition OD will not be used itself, but its children will be made
				 * direct children of the datatype OD.
				 */
				for (ObjectNameAndPtr onap : typeDefinitionOD.getChildren())
					objDesc.addChild(onap);
				objectDescStore.remove(typeDefinitionOD.getID());
			} catch (Exception e) {
				logger.error("Failed to create auto-complete object descriptor from DataType XML element: " + n.getNodeName(), e);
				throw new Exception();
			}
		}

		/* Create descriptors for the currently loaded task types */
		Map<String, ExecutionEntity> taskTypes = conf.getTaskTypes();
		for (Entry<String, ExecutionEntity> entry : taskTypes.entrySet()) {
			if (entry.getValue() instanceof CustomTaskType) {
				CustomTaskType taskType = (CustomTaskType) entry.getValue();
				ObjectDesc objDesc = new ObjectDesc("taskType!" + entry.getKey());
				objectDescStore.put(objDesc.getID(), objDesc);

				try {
					ObjectDesc typeDefinitionOD = XMLElementToAutoCompleteObjectDesc(
							taskType.getXMLTaskDefinitionDocument().getDocumentElement(),
							xpath,
							objectDescStore);
					objDesc.addChild(
							taskType.getXMLTaskDefinitionDocument().getDocumentElement().getNodeName(),
							typeDefinitionOD.getID());
					objDesc.addChild("input", "dataType!" + taskType.getInput().getTypeName());
					objDesc.addChild("output", "dataType!" + taskType.getOutput().getTypeName());
				} catch (Exception e) {
					logger.error("Failed to create auto-complete object descriptor from XML element of CustomTaskType: " + taskType.getName(), e);
					throw new Exception();
				}
			}
		}

		/* Create descriptors for the currently loaded job types */
		Map<String, JobType> jobTypes = conf.getallJobTypes();
		for (Entry<String, JobType> entry : jobTypes.entrySet()) {
			ObjectDesc objDesc = new ObjectDesc("jobType!" + entry.getKey());
			objectDescStore.put(objDesc.getID(), objDesc);

			JobType jobType = entry.getValue();
			objDesc.addChild("input", "dataType!" + jobType.getInput().getTypeName());

			LinkedBlockingQueue<ExecutionEntity> jobTraversalQueue = new LinkedBlockingQueue<ExecutionEntity>();
			jobTraversalQueue.add(jobType.getTaskTree());
			while (jobTraversalQueue.size() > 0) {
				ExecutionEntity ee = jobTraversalQueue.poll();
				if (ee instanceof ExecutionType) {
					ExecutionType et = (ExecutionType) ee;
					jobTraversalQueue.addAll(et.getEntitiesToBeExecuted());
				}
				else if (ee instanceof CustomTaskType) {
					CustomTaskType ctt = (CustomTaskType) ee;
					objDesc.addChild(ctt.getName(), "taskType!" + ctt.getTypeName());
				}
			}
		}

		/* Finally, create descriptors for the currently loaded job types */
		for (Entry<String, JobType> entry : jobTypes.entrySet()) {
			String jobTypeName = entry.getKey();
			JobAutoCompleteEntryDesc jacd = new JobAutoCompleteEntryDesc(jobTypeName, "jobType!" + jobTypeName);
			autoCompleteData.add(jacd);
		}

		return new JobAutoCompleteData(autoCompleteData, objectDescStore);
	}

	private ObjectDesc XMLElementToAutoCompleteObjectDesc(Node xmlElement, XPath xpath, Map<String, ObjectDesc> objectDescStore) throws Exception {
		NodeList nl = (NodeList) xpath.evaluate("*", xmlElement, XPathConstants.NODESET);
		if (nl.getLength() == 0)
			return null;
		else {
			ObjectDesc objDesc = new ObjectDesc(UUID.randomUUID().toString());
			objectDescStore.put(objDesc.getID(), objDesc);
			for (int i=0; i<nl.getLength(); i++) {
				ObjectDesc childObjDesc = XMLElementToAutoCompleteObjectDesc(nl.item(i), xpath, objectDescStore);
				if (childObjDesc == null)
					objDesc.addChild(nl.item(i).getNodeName(), null);
				else
					objDesc.addChild(nl.item(i).getNodeName(), childObjDesc.getID());
			}
			return objDesc;
		}
	}
}
