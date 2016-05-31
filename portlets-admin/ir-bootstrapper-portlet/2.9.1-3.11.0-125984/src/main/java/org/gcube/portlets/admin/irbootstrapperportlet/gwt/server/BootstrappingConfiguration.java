/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server;

import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.Resource;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.ResourceCache;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.ResourceManager;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.ExecutionEntity;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.DataType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.job.JobType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task.AssignTaskType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task.CustomTaskType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task.ParallelExecution;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task.SequentialExecution;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.EntityExecutionData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class BootstrappingConfiguration {

	/** Logger */
	private static Logger logger = Logger.getLogger(BootstrappingConfiguration.class);

	/** The map that keeps the registered data types */
	private Map<String, DataType<? extends Resource>> dataTypes;

	/** The map that keeps the registered task types */
	private Map<String, ExecutionEntity> taskTypes;

	/** The map that keeps the registered job types */
	private Map<String, JobType> jobTypes;

	/** The list that keeps the available jobs */
	private List<JobType> jobs;

	/** The map that keeps the list of defined jobs per job type */
	private Map<String, List<JobType>> jobsPerJobType;

	/** The map that keeps a list of extension job names per base job name */
	private Map<String, List<String>> jobExtensionNamesPerJobName;	

	/** A map that contains all the possible instances for each job in the active scope, based
    on the different applicable inputs for each job in this scope. The map contains a list of job 
    instances per job type name. */
	private Map<String, List<JobType>> possibleJobInstancesPerJobType;

	/** A map that contains all the possible instances for each job in the active scope, based
    on the different applicable inputs for each job in this scope. The map holds mappings between
    UIDs of job instances and the actual JobType objects. */
	private Map<String, JobType> possibleJobInstancesByUID;

	/** A map that contains all the submitted jobs instances in the active scope, by job instance UID. */
	private Map<String, JobType> submittedJobInstances;

	/** A list that contains all the submitted chains of jobs s*/
	private Map<String, Object[]> submittedBatchJobInstanceChains;

	/** The {@link ResourceCache} object to use for fetching resources */
	private ResourceCache resourceCache;

	/** The scope where this {@link BootstrappingConfiguration} object operates in */
	String scope;

	/** The generic resource that contains the bootstrapper configuration */
	private GenericResource configGR;

	/**
	 * Class constructor
	 */
	public BootstrappingConfiguration() {
		dataTypes = new HashMap<String, DataType<? extends Resource>>();
		taskTypes = new HashMap<String, ExecutionEntity>();
		jobTypes = new HashMap<String, JobType>();
		jobs = new LinkedList<JobType>();
		jobsPerJobType = new HashMap<String, List<JobType>>();
		jobExtensionNamesPerJobName = new HashMap<String, List<String>>();
		possibleJobInstancesPerJobType = new HashMap<String, List<JobType>>();
		possibleJobInstancesByUID = new HashMap<String, JobType>();
		submittedJobInstances = new HashMap<String, JobType>();
		submittedBatchJobInstanceChains = new HashMap<String, Object[]>();
		configGR = null;
	}

	/**
	 * Reads the configuration for the given scope and initializes the internal structures
	 * @param scope
	 */
	public void initialize(String scope) throws Exception {
		this.scope = scope;

		parseConfiguration();
	}

	/**
	 * Returns the {@link ResourceCache} object used by this {@link BootstrappingConfiguration} object
	 * @return the {@link ResourceCache} object
	 */
	public ResourceCache getResourceCache() {
		return this.resourceCache;
	}

	/**
	 * Parses the configuration that describes the available datatypes, tasks and jobs
	 */
	private void parseConfiguration() throws Exception {

		resourceCache = new ResourceCache(scope);

		/* Clear the previously parsed data */
		dataTypes.clear();
		taskTypes.clear();
		jobTypes.clear();
		jobs.clear();
		possibleJobInstancesPerJobType.clear();
		possibleJobInstancesByUID.clear();

		/* Add the built-in task types */
		ParallelExecution pe = new ParallelExecution();
		pe.setScope(scope);
		SequentialExecution se = new SequentialExecution();
		se.setScope(scope);
		AssignTaskType att = new AssignTaskType();
		att.setScope(scope);
		taskTypes.put(ConfigurationParserConstants.PARALLEL_ELEMENT, pe);
		taskTypes.put(ConfigurationParserConstants.SEQUENTIAL_ELEMENT, se);
		taskTypes.put(ConfigurationParserConstants.ASSIGN_ELEMENT, att);

		/* Retrieve the configuration */
		Document confDoc = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbFactory.newDocumentBuilder();
			//confDoc = builder.parse(new InputSource(new FileReader("/tmp/conf.xml")));
			configGR = ResourceManager.retrieveGenericResource("$IRBootstrapperConfiguration", scope);
			confDoc = builder.parse(new InputSource(new StringReader(configGR.profile().bodyAsString())));
		} catch (Exception e) {
			logger.error("Error while reading the bootstrapping configuration in scope: " + scope.toString(), e);
			throw new Exception("Error while reading the bootstrapping configuration in scope: " + scope.toString());
		}

		try {
			/* Parse the input/output data types */
			parseDataTypes(confDoc);

			logger.debug("Data types parsing OK");
			
			/* Parse the task types */
			parseTaskTypes(confDoc);

			logger.debug("Task types parsing OK");
			
			/*Parse the job types */
			parseJobTypes(confDoc);

			logger.debug("Job types parsing OK");
			
			/* Now that all types have been parsed, populate the resource cache
			 * with the resource types referenced by them */
			resourceCache.populateCacheFromIS();
			
			logger.debug("Cache on IS OK");

			/* Parse the jobs */
			parseJobs(confDoc);
			
			logger.debug("Jobs parsing OK");

			/* Initialize the internal data structures of job inheritance (extensions) */
			processInheritanceInfo();
			
			logger.debug("Inheritance parsing OK");

			/* Finally create the job instances based on the applicable input resources */
			createJobInstances();
			
			logger.debug("Job Instances created fine");
		} catch (Exception e) {
			logger.error("Failed to parse the Bootstrapper configuration. An exception is thrown", e);
			throw e;
		}
	}

	/**
	 * Parses the input/output data types declared in the configuration
	 * @param confDoc the configuration document
	 * @throws Exception
	 */
	private void parseDataTypes(Document confDoc) throws Exception{
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nlDataTypes = null;
		try {
			nlDataTypes = (NodeList) xpath.evaluate(
					"//" + ConfigurationParserConstants.TYPES_ELEMENT + "/" + ConfigurationParserConstants.DATATYPE_ELEMENT, 
					confDoc, XPathConstants.NODESET);
		} catch (Exception e) {
			logger.error("Failed to locate the declared data types in the configuration", e);
			throw new Exception("Failed to locate the declared data types in the configuration");
		}

		for (int i=0; i<nlDataTypes.getLength(); i++) {
			Element elDataType = (Element) nlDataTypes.item(i);
			String dataTypeClassName = elDataType.getAttribute(ConfigurationParserConstants.CLASS_ATTRIBUTE);
			String dataTypeName = elDataType.getAttribute(ConfigurationParserConstants.NAME_ATTRIBUTE);

			/* Load the data type's class using reflection */
			Class<? extends DataType> dataTypeClass = null;
			try {
				dataTypeClass = Class.forName(dataTypeClassName).asSubclass(DataType.class);
			} catch (Exception e) {
				logger.error("Unable to find dataType class: " + dataTypeClassName);
				throw new Exception("Unable to find dataType class: " + dataTypeClassName);
			}

			/* Construct a new object representing this dataType */
			DataType<? extends Resource> dataTypeObj = null;
			try {
				/* Retrieve the class of the actual generic parameter used by this DataType class */
				Class dataTypeGenericParamClass = (Class) ((ParameterizedType) dataTypeClass.getGenericSuperclass()).getActualTypeArguments()[0];
				//TODO it was GCubeScope
				Constructor<? extends DataType> ctor = dataTypeClass.getConstructor(String.class, dataTypeGenericParamClass);
				dataTypeObj = ctor.newInstance(scope, null);
			} catch (Exception e) {
				logger.error("Unable to create dataType instance", e);
				throw new Exception("Unable to create dataType instance");
			}

			/* Locate the "initialize" method of the dataType object */
			Method initMethod = null;
			try {
				initMethod = dataTypeClass.getMethod("initialize", String.class);
			} catch (Exception e) {
				logger.error("Unable to locate the 'initialize' method in: " + dataTypeClassName);
				throw new Exception("Unable to locate the 'initialize' method in: " + dataTypeClassName);
			}

			/* Initialize the new dataType object */
			try {
				initMethod.invoke(dataTypeObj, dataTypeName);
			} catch (InvocationTargetException e) {
				logger.error("Error while initializing " + dataTypeName + " instance", e.getCause());
				throw (Exception) e.getCause();
			} catch (Exception e) {
				logger.error("Failed to invoke the 'initialize' method of " + dataTypeName, e);
				throw new Exception("Failed to invoke the 'initialize' method of " + dataTypeName);
			}

			/* Store the data type in the data types map */
			dataTypes.put(dataTypeName, dataTypeObj);
		}
	}

	/**
	 * Parses the task types declared in the configuration
	 * @param confDoc the configuration document
	 * @throws Exception
	 */
	private void parseTaskTypes(Document confDoc) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nlTaskTypes = null;
		try {
			nlTaskTypes = (NodeList) xpath.evaluate(
					"//" + ConfigurationParserConstants.TYPES_ELEMENT + "/" + ConfigurationParserConstants.TASKTYPE_ELEMENT, 
					confDoc, XPathConstants.NODESET);
		} catch (Exception e) {
			logger.error("Failed to locate the declared task types in the configuration", e);
			throw new Exception("Failed to locate the declared task types in the configuration");
		}

		for (int i=0; i<nlTaskTypes.getLength(); i++) {
			Element elTaskType = (Element) nlTaskTypes.item(i);
			String taskTypeClassName = elTaskType.getAttribute(ConfigurationParserConstants.CLASS_ATTRIBUTE);
			String taskTypeName = elTaskType.getAttribute(ConfigurationParserConstants.NAME_ATTRIBUTE);
			String inputDataTypeName = (String) xpath.evaluate(
					ConfigurationParserConstants.INPUT_ELEMENT + "/@" + ConfigurationParserConstants.TYPE_ATTRIBUTE,
					elTaskType, XPathConstants.STRING);
			String outputDataTypeName = (String) xpath.evaluate(
					ConfigurationParserConstants.OUTPUT_ELEMENT + "/@" + ConfigurationParserConstants.TYPE_ATTRIBUTE,
					elTaskType, XPathConstants.STRING);
			Element elRun = (Element) xpath.evaluate(ConfigurationParserConstants.RUN_ELEMENT, elTaskType, XPathConstants.NODE);

			/* Load the task type's class using reflection */
			Class<? extends CustomTaskType> taskTypeClass = null;
			try {
				taskTypeClass = Class.forName(taskTypeClassName).asSubclass(CustomTaskType.class);
			} catch (Exception e) {
				logger.error("Unable to find taskType class: " + taskTypeClassName);
				throw new Exception("Unable to find taskType class: " + taskTypeClassName);
			}

			/* Construct a new object representing this taskType */
			CustomTaskType taskTypeObj = null;
			try {
				taskTypeObj = taskTypeClass.newInstance();
			} catch (Exception e) {
				logger.error("Unable to create " + taskTypeName + " instance", e);
				throw new Exception("Unable to create " + taskTypeName + " instance");
			}

			/* Locate the "initialize" method of the taskType object */
			Method initMethod = null;
			try {
				initMethod = taskTypeClass.getMethod("initialize",
						String.class, DataType.class, DataType.class, Element.class);
			} catch (Exception e) {
				logger.error("Unable to locate the 'initialize' method in: " + taskTypeClassName);
				throw new Exception("Unable to locate the 'initialize' method in: " + taskTypeClassName);
			}

			/* Initialize the new taskType object */
			try {
				initMethod.invoke(taskTypeObj, taskTypeName, 
						dataTypes.get(inputDataTypeName).clone(),
						dataTypes.get(outputDataTypeName).clone(),
						elRun);
			} catch (InvocationTargetException e) {
				logger.error("Error while initializing " + taskTypeName + " instance", e.getCause());
				throw (Exception) e.getCause();
			} catch (Exception e) {
				logger.error("Failed to invoke the 'initialize' method of " + taskTypeName, e);
				throw new Exception("Failed to invoke the 'initialize' method of " + taskTypeName);
			}
			taskTypeObj.setScope(scope);

			/* Add the target resource types of this tasks's input and output to the list of resource
			 * types to be retrieved by the ResourceRetriever object */
			resourceCache.addResourceToFetchedResourceTypesList(taskTypeObj.getInput().getAssociatedResource());
			resourceCache.addResourceToFetchedResourceTypesList(taskTypeObj.getOutput().getAssociatedResource());

			/* Store the data type in the data types map */
			taskTypes.put(taskTypeName, taskTypeObj);
		}
	}

	/**
	 * Parses the job types declared in the configuration
	 * @param confDoc the configuration document
	 * @throws Exception
	 */
	private void parseJobTypes(Document confDoc) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nlJobTypes = null;
		try {
			nlJobTypes = (NodeList) xpath.evaluate(
					"//" + ConfigurationParserConstants.TYPES_ELEMENT + "/" + ConfigurationParserConstants.JOBTYPE_ELEMENT, 
					confDoc, XPathConstants.NODESET);
		} catch (Exception e) {
			logger.error("Failed to locate the declared job types in the configuration", e);
			throw new Exception("Failed to locate the declared job types in the configuration");
		}

		for (int i=0; i<nlJobTypes.getLength(); i++) {
			Element elJobType = (Element) nlJobTypes.item(i);
			String jobTypeName = elJobType.getAttribute(ConfigurationParserConstants.NAME_ATTRIBUTE);
			String jobDescription = elJobType.getAttribute(ConfigurationParserConstants.DESCRIPTION_ATTRIBUTE);
			String inputDataTypeName = (String) xpath.evaluate(
					ConfigurationParserConstants.INPUT_ELEMENT + "/@" + ConfigurationParserConstants.TYPE_ATTRIBUTE,
					elJobType, XPathConstants.STRING);
			Element elJobDefinition = (Element) xpath.evaluate(ConfigurationParserConstants.JOBDEFINITION_ELEMENT,
					elJobType, XPathConstants.NODE);
			NodeList chainExecutionAssignments = (NodeList) xpath.evaluate(
					ConfigurationParserConstants.CHAINEXECUTION_ELEMENT + "/" + ConfigurationParserConstants.CHAINCONNECTASSIGNMENTS_ELEMENT + "/" + ConfigurationParserConstants.ASSIGN_ELEMENT,
					elJobType, XPathConstants.NODESET);

			/* Construct a new object representing this jobType and initialize it */
			JobType jobTypeObj = new JobType(dataTypes.get(inputDataTypeName).getClass());
			try {
				jobTypeObj.initialize(jobTypeName,
						jobDescription,
						(DataType) dataTypes.get(inputDataTypeName).clone(),
						elJobDefinition,
						this);

				/* Now parse the chain execution assignments described in the job */
				for (int j=0; j<chainExecutionAssignments.getLength(); j++) {
					Node assignment = chainExecutionAssignments.item(j);
					AssignTaskType assignTask = ((AssignTaskType) taskTypes.get(ConfigurationParserConstants.ASSIGN_ELEMENT)).newInstance("");
					assignTask.initialize((Element) assignment);
					assignTask.setParent(jobTypeObj);
					jobTypeObj.addChainExecutionAssignment(assignTask);
				}
			} catch (Exception e) {
				logger.error("Error while initializing " + jobTypeName + " instance", e);
				throw e;
			}
			jobTypeObj.setScope(scope);

			/* Add the target resource type of this jobtype's input to the list of resource types
			 * to be retrieved by the ResourceRetriever object */
			resourceCache.addResourceToFetchedResourceTypesList(jobTypeObj.getInput().getAssociatedResource());

			/* Store the data type in the data types map */
			jobTypes.put(jobTypeName, jobTypeObj);
			jobsPerJobType.put(jobTypeName, new LinkedList<JobType>());
		}
	}

	/**
	 * Parses the jobs defined in the configuration
	 * @param confDoc the configuration document
	 * @throws Exception
	 */
	private void parseJobs(Document confDoc) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nlJobs = null;
		try {
			nlJobs = (NodeList) xpath.evaluate(
					"//" + ConfigurationParserConstants.JOBS_ELEMENT + "/" + ConfigurationParserConstants.JOB_ELEMENT, 
					confDoc, XPathConstants.NODESET);
		} catch (Exception e) {
			logger.error("Failed to locate the defined jobs in the configuration", e);
			throw new Exception("Failed to locate the defined jobs in the configuration");
		}

		for (int i=0; i<nlJobs.getLength(); i++) {			
			Element elJob = (Element) nlJobs.item(i);
			String jobTypeName = elJob.getAttribute(ConfigurationParserConstants.JOBTYPE_ATTRIBUTE);
			String jobName = elJob.getAttribute(ConfigurationParserConstants.NAME_ATTRIBUTE);
			String jobExtends = elJob.getAttribute(ConfigurationParserConstants.EXTENDS_ATTRIBUTE);
			NodeList initAssignments = (NodeList) xpath.evaluate(
					ConfigurationParserConstants.JOBINIT_ELEMENT + "/" + ConfigurationParserConstants.ASSIGN_ELEMENT,
					elJob, XPathConstants.NODESET);

			/* Create the new job instance */
			JobType jobInstance = jobTypes.get(jobTypeName).newInstance(jobName, jobExtends.length() > 0 ? jobExtends : null);

			/* If this job extends another job, perform the initialization assignments of the base job now */
			if (jobExtends!=null && jobExtends.trim().length()>0) {
				NodeList nlBaseJobInitAssignments = (NodeList) xpath.evaluate(
						"//" + ConfigurationParserConstants.JOBS_ELEMENT + "/" + 
						ConfigurationParserConstants.JOB_ELEMENT + "[@name='" + jobExtends + "']/" +
						ConfigurationParserConstants.JOBINIT_ELEMENT + "/" + 
						ConfigurationParserConstants.ASSIGN_ELEMENT, 
						confDoc, XPathConstants.NODESET);

				for (int j=0; j<nlBaseJobInitAssignments.getLength(); j++) {
					Node assignment = nlBaseJobInitAssignments.item(j);
					AssignTaskType assignTask = ((AssignTaskType) taskTypes.get(ConfigurationParserConstants.ASSIGN_ELEMENT)).newInstance("");
					assignTask.initialize((Element) assignment);
					assignTask.setParent(jobInstance);
					assignTask.doAssignment();
					//jobInstance.addInitAssignment(assignTask);
				}

				/* Create an association between the base job name and this job name */
				List<String> extendedJobNames = jobExtensionNamesPerJobName.get(jobExtends);
				if (extendedJobNames == null) {
					extendedJobNames = new LinkedList<String>();
					jobExtensionNamesPerJobName.put(jobExtends, extendedJobNames);
				}
				extendedJobNames.add(jobName);				
			}

			/* Now perform the initialization assignments described in the job */
			for (int j=0; j<initAssignments.getLength(); j++) {
				Node assignment = initAssignments.item(j);
				AssignTaskType assignTask = ((AssignTaskType) taskTypes.get(ConfigurationParserConstants.ASSIGN_ELEMENT)).newInstance("");
				assignTask.initialize((Element) assignment);
				assignTask.setParent(jobInstance);
				assignTask.doAssignment();
				jobInstance.addInitAssignment(assignTask);
			}

			/* Initialize the job instance and add it to the internal data structures */
			jobInstance.initializeWithDataInScope(scope);
			jobs.add(jobInstance);
			jobsPerJobType.get(jobTypeName).add(jobInstance);
		}

	}

	/**
	 * Parses a XML fragment describing an execution entity inside a job and returns
	 * an appropriate object describing it.
	 * @param xml the XML fragment to parse
	 * @return an ExecutionEntity object 
	 * @throws Exception
	 */
	public ExecutionEntity parseJobExecutionEntity(Element xml) throws Exception {
		ExecutionEntity ret = null;
		String elementName = xml.getNodeName();
		if (elementName.equals(ConfigurationParserConstants.PARALLEL_ELEMENT)) {
			ret = taskTypes.get(elementName).newInstance(elementName);
			((ParallelExecution) ret).initialize(xml, this);
		}
		else if (elementName.equals(ConfigurationParserConstants.SEQUENTIAL_ELEMENT)) {
			ret = taskTypes.get(elementName).newInstance(elementName);
			((SequentialExecution) ret).initialize(xml, this);
		}
		else if (elementName.equals(ConfigurationParserConstants.ASSIGN_ELEMENT)) {
			ret = taskTypes.get(elementName).newInstance(elementName);
			((AssignTaskType) ret).initialize(xml);
		}
		else if (elementName.equals(ConfigurationParserConstants.TASK_ELEMENT)) {
			String taskTypeName = xml.getAttribute(ConfigurationParserConstants.TASKTYPE_ATTRIBUTE);
			String taskName = xml.getAttribute(ConfigurationParserConstants.NAME_ATTRIBUTE);
			ExecutionEntity taskType = taskTypes.get(taskTypeName);
			if (taskType == null) {
				logger.error("CustomTaskType '" + taskTypeName + "' was not found.");
				throw new Exception("CustomTaskType '" + taskTypeName + "' was not found.");
			}
			ret = taskType.newInstance(taskName);
		}
		return ret;
	}

	/**
	 * Saves the currently loaded bootstrapper configuration to a generic
	 * resource in XML form.
	 */
	void saveConfiguration() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("<BootstrapInfo>");
		sb.append("<types>");

		/* Write data type info */
		for (String dtName : this.dataTypes.keySet()) {
			sb.append("<type name=\"");
			sb.append(dtName);
			sb.append("\" class=\"");
			sb.append(this.dataTypes.get(dtName).getClass().getName());
			sb.append("\"/>");
		}

		/* Write task type info */
		for (String taskName : this.taskTypes.keySet()) {
			if (this.taskTypes.get(taskName) instanceof CustomTaskType) {
				CustomTaskType taskObj = (CustomTaskType) this.taskTypes.get(taskName);
				sb.append("<tasktype name=\"");
				sb.append(taskName);
				sb.append("\" class=\"");
				sb.append(taskObj.getClass().getName());
				sb.append("\"><input type=\"");
				sb.append(taskObj.getInput().getTypeName());
				sb.append("\"/><output type=\"");
				sb.append(taskObj.getOutput().getTypeName());
				sb.append("\"/><run>true</run>");
				sb.append("</tasktype>");
			}
		}

		/* Write job type info */
		for (String jobTypeName : this.jobTypes.keySet()) {
			JobType jobObj = this.jobTypes.get(jobTypeName);
			sb.append("<jobtype name=\"");
			sb.append(jobTypeName);
			sb.append("\" description=\"");
			sb.append(jobObj.getUIDescription());
			sb.append("\"><input type=\"");
			sb.append(jobObj.getInput().getTypeName());
			sb.append("\"/><jobDefinition>");
			jobObj.getTaskTree().toXML(sb);
			sb.append("</jobDefinition></jobtype>");
		}

		sb.append("</types>");

		/* Write job instances info */
		sb.append("<jobs>");
		for (JobType jobInstance : jobs) {
			sb.append("<job jobtype=\"");
			sb.append(jobInstance.getTypeName());
			sb.append("\" name=\"");
			sb.append(jobInstance.getName());
			sb.append("\"");
			String jobExtends = jobInstance.getBaseJobName();
			if (jobExtends != null) {
				sb.append(" extends=\"");
				sb.append(jobExtends);
				sb.append("\"");
			}
			sb.append("><initialization>");
			for (AssignTaskType att : jobInstance.getInitAssignments()) {
				att.toXML(sb);
			}
			sb.append("</initialization></job>");
		}
		sb.append("</jobs>");
		sb.append("</BootstrapInfo>");

		try {
			//TODO check if this is correct
			configGR.profile().newBody(sb.toString());
			ResourceManager.updateGenericResource(configGR, scope);
		} catch (Exception e) {
			logger.error("Failed to save bootstrapping configuration.");
			throw new Exception("Failed to save bootstrapping configuration.", e);
		}
	}

	/**
	 * Creates and returns a new instance of the object representing
	 * the task with the given name.
	 * @param taskTypeName
	 */
	public ExecutionEntity newTaskObject(String taskTypeName) {
		return taskTypes.get(taskTypeName).newInstance("");
	}

	/**
	 * Finds a jobType object by its name and returns it
	 * @param jobName the name of the job to search for
	 * @return
	 */
	private JobType findJobObjectFromJobName(String jobName) {
		for (JobType job : jobs) {
			if (job.getName().equals(jobName))
				return job;
		}
		return null;
	}

	/**
	 * Creates the required links of the base and extension jobs in the internal
	 * data structures.
	 */
	private void processInheritanceInfo() {
		for (String baseJobName : jobExtensionNamesPerJobName.keySet()) {			
			JobType baseJob = findJobObjectFromJobName(baseJobName);
			List<String> extendedJobNames = jobExtensionNamesPerJobName.get(baseJobName);
			for (String jobName : extendedJobNames) {
				JobType extendedJob = findJobObjectFromJobName(jobName);
				baseJob.removeApplicableInputs(extendedJob.getApplicableInputs());	
			}
		}
	}

	/**
	 * Creates a number of instances for each available job, based on the applicable
	 * inputs found for each job.
	 */
	private void createJobInstances() throws Exception {
		for (JobType job : this.jobs)
			job.createJobInstancesForAllApplicableInputs(this.scope);
	}

	/**
	 * Returns the list of available data types
	 * @return
	 */
	public Map<String, DataType<? extends Resource>> getDataTypes() {
		return this.dataTypes;
	}

	/**
	 * Returns the list of available task types
	 * @return
	 */
	public Map<String, ExecutionEntity> getTaskTypes() {
		return this.taskTypes;
	}

	/**
	 * Returns the list of available jobs
	 * @return
	 */
	public List<JobType> getJobs() {
		return this.jobs;
	}

	/**
	 * Returns a map containing all jobs defined for each job type
	 * @return
	 */
	public Map<String, List<JobType>> getAllJobsPerJobType() {
		return this.jobsPerJobType;
	}

	/**
	 * Adds a job instance to the list of instances for the given job type
	 * @param jobTypeName the job type name
	 * @param jobInstance the job instance to add
	 */
	public void addJobInstance(String jobTypeName, JobType jobInstance) {
		List<JobType> instanceList = possibleJobInstancesPerJobType.get(jobTypeName);
		if (instanceList == null)
			instanceList = new LinkedList<JobType>();
		instanceList.add(jobInstance);
		possibleJobInstancesPerJobType.put(jobTypeName, instanceList);
		possibleJobInstancesByUID.put(jobInstance.getUID(), jobInstance);
	}

	/**
	 * Clears the map of job instances for all the job types
	 */
	public void clearAllJobInstances() {
		possibleJobInstancesPerJobType.clear();
		possibleJobInstancesByUID.clear();
	}

	/**
	 * Clears the list of job instances for the given job type
	 * @param jobTypeName the job type name
	 */
	public void clearJobTypeInstances(String jobTypeName) {
		possibleJobInstancesPerJobType.remove(jobTypeName);
	}

	/**
	 * Returns all the instances of all job types in the active scope
	 * @return the map of job instances, containing (jobTypeName, List<Job>) pairs
	 */
	public Map<String, List<JobType>> getAllJobInstances() {
		return this.possibleJobInstancesPerJobType;
	}

	/**
	 * Returns all the instances of a given job type in the active scope
	 * @return the list of job instances
	 */
	public List<JobType> getJobInstancesForJobType(String jobTypeName) {
		return this.possibleJobInstancesPerJobType.get(jobTypeName);
	}

	/**
	 * Adds a submitted (started) job instance to the list of submitted jobs
	 * @param jobInstance the submitted job
	 */
	public void addSubmittedJobInstance(JobType jobInstance) {
		submittedJobInstances.put(jobInstance.getUID(), jobInstance);
	}

	/**
	 * Removes a submitted (stopped) job instance from the list of submitted jobs
	 * @param jobUID the UID of the job instance to remove
	 */
	public void removeSubmittedJobInstance(String jobUID) {
		this.submittedJobInstances.remove(jobUID);
	}

	/**
	 * Returns the list of submitted job instances in the active scope
	 * @return the list of submitted jobs
	 */
	public List<JobType> getSubmittedJobInstances() {
		return new LinkedList<JobType>(this.submittedJobInstances.values());
	}

	/**
	 * Returns a submitted job instance in the active scope, given its UID
	 * @return the list of job instances
	 */
	public JobType getSubmittedJobInstanceByUID(String jobUID) {
		return this.submittedJobInstances.get(jobUID);
	}

	/**
	 * Submits a new instance of the job with the given UID for execution
	 * @param jobUID the UID of the job to submit
	 * @param eed the {@link EntityExecutionData} object to be used during the job's execution
	 */
	public String submitJobForExecution(String jobUID, EntityExecutionData eed, Map<String,String> userInputs) throws Exception {
		if (possibleJobInstancesByUID == null)
			throw new Exception();

		JobType job = possibleJobInstancesByUID.get(jobUID);
		if (job == null)
			throw new Exception();

		return job.execute(eed, userInputs, null, null);
	}

	/**
	 * Submits a list of jobs
	 * Submits a new instance of the job with the given UID for execution
	 * @param jobUID the UID of the job to submit
	 * @param eed the {@link EntityExecutionData} object to be used during the job's execution
	 */
	public String submitJobsForExecution(ArrayList<String> jobsUIDs, EntityExecutionData eed, Map<String,String> userInputs) throws Exception {
		if (possibleJobInstancesByUID == null)
			throw new Exception();

		String jobChainUID = UUID.randomUUID().toString();
		List<JobType> jobsChain = new LinkedList<JobType>();
		for (String jid : jobsUIDs) {
			JobType j = possibleJobInstancesByUID.get(jid);
			if (j == null)
				throw new Exception();
			jobsChain.add(j);
		}
		submittedBatchJobInstanceChains.put(jobChainUID, new Object[] { jobsChain, eed });

		JobType job = jobsChain.get(0);
		return job.execute(eed, userInputs, jobChainUID, null);

	}

	public void jobInChainCompleted(String jobChainUID, JobType completedJob) {
		Object[] data = submittedBatchJobInstanceChains.get(jobChainUID);
		List<JobType> chain = (List<JobType>) data[0];
		EntityExecutionData eed = (EntityExecutionData) data[1];

		/*
		 * Remove the job that is just completed from the chain.
		 * If there is another job to be executed do it otherwise remove the whole chain
		 */
		chain.remove(0);
		if (chain.size() == 0) {
			logger.debug("chain has no items inside... removing it");
			submittedBatchJobInstanceChains.remove(jobChainUID);
		}
		else {
			JobType nextJob = chain.get(0);
			logger.debug("Going to execute --> " + nextJob.getName());
			nextJob.execute(eed, null, jobChainUID, completedJob);
		}
	}

	public void jobInChainFailed(String jobChainUID, JobType failedJob) {
		submittedBatchJobInstanceChains.remove(jobChainUID);
	}

	/**
	 * Returns the JobType object corresponding to a given job type name
	 * @param jobTypeName
	 * @return
	 */
	public JobType getJobTypeByName(String jobTypeName) {
		return jobTypes.get(jobTypeName);
	}

	/**
	 * Returns a map containing all the available JobType objects paired
	 * with their names.
	 * @return
	 */
	public Map<String, JobType> getallJobTypes() {
		return this.jobTypes;
	}
}
