/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.job;

import gr.uoa.di.madgik.taskexecutionlogger.model.WorkflowLogEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.BootstrappingConfiguration;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.ConfigurationParserConstants;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.IRBootstrapperData;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.threads.TaskExecutionListener;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.EvaluationResult;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.ExecutionEntity;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.DataType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task.AssignTaskType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.EntityExecutionData;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.allen_sauer.gwt.log.client.Log;


/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class JobType extends ExecutionEntity implements Cloneable {

	/** Logger */
	private static Logger logger = Logger.getLogger(JobType.class);

	/** The name of this jobType */
	private String jobTypeName;
	
	/** A small description of this job */
	private String jobDescription;
	
	/** The name of the base job that this job extends */
	private String baseJobName;
	
	/** The input dataType class */
	private Class<? extends DataType> inputClass;
	
	/** The input dataType*/
	private DataType input;
	
	/** The job's task tree */
	private ExecutionEntity taskTree;
	
	/** The list of initialization tasks for this job */
	private List<AssignTaskType> initAssignments;
	
	/** The list of chain execution assignments */
	private List<AssignTaskType> chainExecutionAssignments;
	
	/** The list of DataTypes that are applicable as inputs to this job */
	private List<DataType> applicableInputResources;
	
	private WorkflowLogEntry wfEntry = null;
	
	/**
	 * Class constructor
	 */
	public JobType(Class<? extends DataType> inputClass) {
		this.inputClass = inputClass;
		this.applicableInputResources = null;
	}
	
	/**
	 * Initializes this JobType
	 * @param jobTypeName the name of the task type
	 * @param inputDataType the task's input data type
	 * @param tasks the XML definition of the job's task tree
	 */
	public void initialize(String jobTypeName, String jobDescription, DataType inputDataType, Element tasks, BootstrappingConfiguration conf) throws Exception {
		if (inputDataType.getClass() != inputClass)
			throw new Exception("The given input is invalid for this type of job.");
		this.jobTypeName = jobTypeName;
		this.jobDescription = jobDescription;
		this.UID = jobTypeName;
		this.input = inputDataType;
		this.baseJobName = null;
		try {
			this.taskTree = parseTaskTree(tasks, conf);
		} catch (Exception e) {
			logger.error("Failed to parse the task tree of jobType: " + jobTypeName, e);
			throw new Exception("Failed to parse the task tree of jobType: " + jobTypeName);
		}
	}
	
	/**
	 * Parses the XML fragment that describes the job's task tree
	 * @param taskTree the XML fragment to parse
	 * @param the active {@link BootstrappingConfiguration} object
	 * @return an ExecutionEntity object wrapping the task tree
	 * @throws Exception
	 */
	private ExecutionEntity parseTaskTree(Element taskTree, BootstrappingConfiguration conf) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Element el = (Element) ((NodeList) xpath.evaluate("*", taskTree, XPathConstants.NODESET)).item(0);
		ExecutionEntity ee = conf.parseJobExecutionEntity(el);
		ee.setParent(this);
		return ee;
	}
	
	/**
	 * Instantiates this JobType, creating a new job.
	 * @param newJobName the name of the new job
	 * @return the new job
	 */
	public JobType newInstance(String newJobName) {
		JobType jt = null;
		try {
			jt = (JobType) this.clone();
		} catch (Exception e) { }
		jt.entityName = newJobName;		
		return jt;
	}
	
	/**
	 * Instantiates this JobType, creating a new job.
	 * @param newJobName the name of the new job
	 * @param baseJob the name of the base job that this job extends
	 * @return the new job
	 */
	public JobType newInstance(String newJobName, String baseJob) {
		JobType jt = null;
		try {
			jt = (JobType) this.clone();
		} catch (Exception e) { }
		jt.entityName = newJobName;
		jt.baseJobName = baseJob;
		return jt;
	}
	
	public void setJobName(String jobName) {
		this.entityName = jobName;
	}

	public void setBaseJobName(String baseJobName) {
		this.baseJobName = baseJobName;
	}
	
	public void setJobTypeName(String jobTypeName) {
		this.jobTypeName = jobTypeName;
	}
	
	/**
	 * Sets the list of initialization assignments for this job type instance
	 * @param initAssignments the list of initialization assignments
	 */
	public void setInitAssignments(List<AssignTaskType> initAssignments) {
		if (initAssignments != null) {
			this.initAssignments = new LinkedList<AssignTaskType>();
			for (AssignTaskType att : initAssignments) {
				try {
					this.initAssignments.add((AssignTaskType) att.clone());
				} catch (Exception e) { }
			}
		}
	}
	
	public void setChainExecutionAssignments(List<AssignTaskType> assignments) {
		this.chainExecutionAssignments = assignments;
	}
	
	public List<AssignTaskType> getChainExecutionAssignments() {
		return this.chainExecutionAssignments;
	}
	
	/**
	 * Adds an initialization assignment in the list of this job instance's
	 * initialization list
	 * @param initAssignment the initialization assignment to add
	 */
	public void addInitAssignment(AssignTaskType initAssignment) {
		if (this.initAssignments == null)
			this.initAssignments = new LinkedList<AssignTaskType>();

		try {
			this.initAssignments.add((AssignTaskType) initAssignment.clone());
		} catch (Exception e) { }
	}
	
	public void addChainExecutionAssignment(AssignTaskType assignment) {
		if (this.chainExecutionAssignments == null)
			this.chainExecutionAssignments = new LinkedList<AssignTaskType>();
		this.chainExecutionAssignments.add(assignment);
	}
	
	/**
	 * Returns the name of the base job that this job extends (if any)
	 * @return
	 */
	public String getBaseJobName() {
		return this.baseJobName;
	}
	
	/**
	 * Returns the list of initialization assignments for this job
	 * @return
	 */
	public List<AssignTaskType> getInitAssignments() {
		return this.initAssignments;
	}
	
	/**
	 * Returns the DataType object that represents the input of this JobType
	 * @return
	 */
	public DataType getInput() {
		return this.input;
	}
	
	/**
	 * Returns the root of this job's task tree
	 * @return the tree root
	 */
	public ExecutionEntity getTaskTree() {
		return this.taskTree;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#initializeWithDataInScope(org.gcube.common.core.scope.GCUBEScope)
	 */
	public void initializeWithDataInScope(String scope) throws Exception {
		/* Find the applicable inputs for this job, in the given scope */
		this.applicableInputResources = this.input.findMatchesInScope(scope);
		
		/* TODO: remove
		 * 
		 */
//		if (this.applicableInputResources != null)
//			logger.debug(this.getName() + ": Found " + this.applicableInputResources.size() + " applicable inputs.-");
	}

	/**
	 * Returns the list of DataTypes corresponding to the applicable inputs for
	 * this job.
	 * @return the list of applicable DataTypes
	 */
	public List<DataType> getApplicableInputs() {
		return this.applicableInputResources;
	}
	
	/**
	 * Accepts a list of DataTypes and removes from the list of this JobType's
	 * applicable inputs the ones whose UID matches the UID of a DataType contained
	 * in the given list.
	 * @param applicableInputsToRemove the list of DataTypes containing the UIDs to remove
	 */
	public void removeApplicableInputs(List<DataType> applicableInputsToRemove) {
		for (DataType inputToRemove : applicableInputsToRemove) {
			String UIDToRemove = inputToRemove.getUID();
			for (DataType applInput : this.applicableInputResources) {
				if (applInput.getUID().equals(UIDToRemove)) {
					this.applicableInputResources.remove(applInput);
					break;
				}
			}
		}
	}
	
	/**
	 * Creates a job instance for each one of the applicable inputs for this job, and
	 * adds it to the {@link BootstrappingConfiguration} object
	 */
	public void createJobInstancesForAllApplicableInputs(String scope) throws Exception {
		BootstrappingConfiguration conf = IRBootstrapperData.getInstance().getBootstrappingConfiguration(scope);

		
		/* TODO: remove
		 * 
		 */
		/*logger.debug("Creating " + applicableInputResources.size() + " job instances for: " + this.getName());
		for (int i=0; i< applicableInputResources.size(); i++) {
			logger.debug("------------------------------------------------------");
			logger.debug("Input for instance " + i + ":");
			Resource res = applicableInputResources.get(i).getAssociatedResource();
			for (String attr : res.getAttributeNames()) {
				logger.debug(attr + " ----> " + res.getAttributeValue(attr));
			}
		}*/
		
		
		/* Create as many instances of the current job as the number of found
		 * applicable inputs and add them to the instances list kept by the active
		 * BootstrappingConfiguration object for this scope.
		 * For each instantiation, invoke 'initializeWithDataInScope' so that this
		 * instance's task tree is also initialized with the possible inputs
		 */
		try {
			for (DataType input : applicableInputResources) {
				JobType job = this.newInstance(this.getName());
				job.input = (DataType) input.clone();
				job.taskTree.initializeWithDataInScope(scope);
				job.scope = scope;
				job.UID = this.UID + "_" + input.getUID();
				job.setInitAssignments(this.initAssignments);
				job.setChainExecutionAssignments(this.chainExecutionAssignments);
				job.setIsFulfilled(job.taskTree.isFulfilled());
				conf.addJobInstance(this.jobTypeName, job);
			}
		} catch (Exception e) {
			logger.error("Error while creating job instances for the applicable inputs for the job: " + this.getName(), e);
			throw new Exception("Error while creating job instances for the applicable inputs for the job: " + this.getName());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#evaluate(java.lang.String)
	 */
	@Override
	public EvaluationResult evaluate(String expression) {
		try {
			/* Split the expression around the first dot */
			String[] parts = expression.split("\\.", 2);
			if (parts[0].equals(this.getName()) || parts[0].equals(this.jobTypeName)) {
				parts = parts[1].split("\\.", 2);
				if (parts[0].equals(ConfigurationParserConstants.INPUT_ELEMENT)) {
					if (parts.length == 1)
						return new EvaluationResult(input, input.getTypeDefinition());
					return input.evaluate(parts[1]);
				}
			}
			else {
				return taskTree.evaluate(expression);
			}
		} catch (Exception e) { }
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#getTypeName()
	 */
	public String getTypeName() {
		return this.jobTypeName;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#getUIDescription()
	 */
	@Override
	public String getUIDescription() {
		return this.jobDescription;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#execute(org.gcube.portlets.admin.irbootstrapperportlet.servlet.util.EntityExecutionData)
	 */
	public String execute(final EntityExecutionData eed) {
		return execute(eed, null, null, null);
	}
	
	public String execute(final EntityExecutionData eed, Map<String,String> userInputs, final String jobsChainUID, JobType prevJobInchain) {
		Map<String, String> userInputsByTarget = new HashMap<String, String>();
		if (jobsChainUID==null || prevJobInchain==null) {
			/* Find AssignTaskType objects corresponding to the given userInputs, and
			 * create a map where the inputs are coupled with their assignment targets
			 */
			if (userInputs != null && userInputs.keySet().size()>0) {
				for (String uid : userInputs.keySet()) {
					for (AssignTaskType att : this.getInitAssignments()) {
						if (att.getUID().equals(uid)) {
							userInputsByTarget.put(att.getAssignTo(), userInputs.get(uid));
							break;
						}
					}
				}
			}
		}
		
		JobType job = null;
		try { 
			job = (JobType) this.clone();
		} catch (Exception e) { }
		
		/* The new job instance must have a unique ID, different from the job UID */
		job.assignRandomUID();
		
		/* Construct the new job instance name, based on the current date and time */
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date startDate = new Date();
		String jobActualName = new String(job.entityName);
		job.entityName = job.entityName + "_" + formatter.format(startDate);
		if (jobsChainUID==null || prevJobInchain==null) {
			/* Assign the given user inputs to the respective cloned AssignTaskType objects */
			if (userInputsByTarget.keySet().size()>0) {
				for (String assignTo : userInputsByTarget.keySet()) {
					for (AssignTaskType att : job.getInitAssignments()) {
						if (att.getAssignTo().equals(assignTo)) {
							try {
								AssignTaskType att2 = (AssignTaskType) att.clone();
								att2.setParent(job);
								att2.setAssignFrom(userInputsByTarget.get(assignTo));
								att2.execute(eed);
								break;
							} catch (Exception e) { }
						}
					}
				}
			}
		}
		else if (jobsChainUID != null) {
			logger.debug("Initializing from previous job in chain... " + prevJobInchain.getName());
			job.initializeFromPreviousJobInChain(prevJobInchain);
		}
		
		/* Add the new job instance to the list of submitted jobs */
		BootstrappingConfiguration conf = IRBootstrapperData.getInstance().getBootstrappingConfiguration(scope);
		conf.addSubmittedJobInstance(job);
		job.wfEntry = new WorkflowLogEntry(job.UID, jobActualName, job.jobTypeName, eed.getSession().getUsername(), ExecutionEntity.ExecutionState.RUNNING.toString(), startDate, job.getUIDescription(), job.scope);
//		logger.debug("Created a new wf entry for the cloned job with the following information...");
//		logger.debug("ID -> " + job.getUID());
//		logger.debug("NAME -> " + job.entityName);
//		logger.debug("TYPE -> " + job.jobTypeName);
//		logger.debug("STATUS -> " + ExecutionEntity.ExecutionState.RUNNING.toString());
//		logger.debug("DESC -> " + job.getUIDescription());
		/* Execute the new job instance */
		eed.setWorkflowLogger(job.wfEntry);
		job.createExecutionLog(logger, eed.getSession());
		job.setWorkflowLogger(job.wfEntry);
		final JobType jobToExecute = job;
			
		
		IRBootstrapperData.getInstance().submitRunnableForExecution(new Runnable() {
				public void run() {
					jobToExecute.setExecutionState(ExecutionEntity.ExecutionState.RUNNING);
					jobToExecute.taskTree.execute(eed);
				}
			},
			
			jobToExecute.getUID(),
			
			new TaskExecutionListener() {
				public void onTaskExecutionCompleted(String taskUID) {
					logger.debug("Task completed and got notified in JobType....");
					taskCompleted(taskUID);	
				}
				private void taskCompleted(String taskUID) {
					JobType completedJob = IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope()).
						getSubmittedJobInstanceByUID(taskUID);
					logger.debug("Completed Job id -> " + completedJob.getUID());
					
					if (completedJob.taskTree.getExecutionState() == ExecutionState.COMPLETED_FAILURE) {
						completedJob.setExecutionState(ExecutionState.COMPLETED_FAILURE);
						if (jobsChainUID != null)
							IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope()).jobInChainFailed(jobsChainUID, completedJob);
					}
					else if (completedJob.taskTree.getExecutionState() == ExecutionState.COMPLETED_WARNINGS){
						completedJob.setExecutionState(ExecutionState.COMPLETED_WARNINGS);
						if (jobsChainUID != null)
							IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope()).jobInChainCompleted(jobsChainUID, completedJob);
					}
					else if (completedJob.taskTree.getExecutionState() == ExecutionState.CANCELLED) {
						completedJob.setExecutionState(ExecutionState.CANCELLED);
						if (jobsChainUID != null)
							IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope()).jobInChainCompleted(jobsChainUID, completedJob);
					}
					else {
						completedJob.setExecutionState(ExecutionState.COMPLETED_SUCCESS);
						if (jobsChainUID != null)
							IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope()).jobInChainCompleted(jobsChainUID, completedJob);
					}
					logger.debug("Job " + getName() + "(" + taskUID + ") finished, state is: " + completedJob.getExecutionState().toString());
					completedJob.getWorkflowLogger().setStatus(completedJob.getExecutionState().toString());
					completedJob.getWorkflowLogger().persistLog();
				}
			});
		return job.getUID();
	}

	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#cancel()
	 */
	public void cancel() {
		this.taskTree.cancel();
		ArrayList<String> taskUIDs = new ArrayList<String>(1);
		taskUIDs.add(this.getUID());
		IRBootstrapperData.getInstance().cancelTasks(taskUIDs);
		
		if (this.taskTree.getExecutionState() == ExecutionState.CANCELLED)
			this.setExecutionState(ExecutionState.CANCELLED);
		//this.getWorkflowLogger().setStatus(ExecutionState.CANCELLED.toString());
		//this.getWorkflowLogger().persistLog();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		try {
			JobType jt = (JobType) super.clone();
			jt.applicableInputResources = new LinkedList<DataType>();
			jt.input = (DataType) input.clone();
			jt.taskTree = (ExecutionEntity) this.taskTree.clone();
			jt.taskTree.setParent(jt);
			return jt;
		} catch (Exception e) {
			logger.error("Error while cloning JobType object", e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#toXML(java.lang.StringBuilder)
	 */
	@Override
	public void toXML(StringBuilder output) { }	
	
	public void initializeFromPreviousJobInChain(JobType previousJob) {
		if (this.chainExecutionAssignments == null) {
			return;
		}
		logger.debug("Initializing from previous job in chain. Going to perform the needed assignment");
		for (AssignTaskType att : this.chainExecutionAssignments) {
			// remove the '%' from the assignments. Needed only for chain assignments because they are applied in a later step
			try {
				String assignFrom = att.getAssignFrom().substring(1);
				String value = previousJob.evaluate(assignFrom).getEvaluatedNode().getTextContent();
				logger.debug("Assigning to -> " + assignFrom + " the value -> " + value);
				logger.debug("Assigning to node -> " + att.getAssignTo().substring(1));
				this.evaluate(att.getAssignTo().substring(1)).getEvaluatedNode().setTextContent(value);
			} catch (Exception e) {
				logger.error("An exception was thrown while performing the assignments in the chain tasks assignments ", e);
			}
		}
	}
}
