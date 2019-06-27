/*
 *
 */
package org.gcube.common.workspacetaskexecutor.dataminer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.workspacetaskexecutor.shared.BaseTaskConfiguration;
import org.gcube.common.workspacetaskexecutor.shared.TaskOperator;
import org.gcube.common.workspacetaskexecutor.shared.TaskOutput;
import org.gcube.common.workspacetaskexecutor.shared.TaskParameter;
import org.gcube.common.workspacetaskexecutor.shared.TaskParameterType;
import org.gcube.common.workspacetaskexecutor.shared.TaskStatus;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskComputation;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskConfiguration;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskExecutionStatus;
import org.gcube.common.workspacetaskexecutor.shared.exception.TaskErrorException;
import org.gcube.common.workspacetaskexecutor.shared.exception.TaskNotExecutableException;
import org.gcube.common.workspacetaskexecutor.util.Converter;
import org.gcube.common.workspacetaskexecutor.util.EncrypterUtil;
import org.gcube.data.analysis.dataminermanagercl.server.DataMinerService;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.shared.data.OutputData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.MapResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.Resource;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ParameterType;
import org.gcube.data.analysis.dataminermanagercl.shared.process.ComputationStatus;
import org.gcube.data.analysis.dataminermanagercl.shared.process.ComputationStatus.Status;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.gcube.data.analysis.dataminermanagercl.shared.process.OperatorsClassification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class DataMinerAccessPoint.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 2, 2018
 */
public class DataMinerAccessPoint {

	private DataMinerService dataMinerService;
	private static Logger logger = LoggerFactory.getLogger(DataMinerAccessPoint.class);

	/** The map call back. */
	// Fully synchronized HashMap
	private Map<String, TaskExecutionStatus> mapExecutionTask = Collections.synchronizedMap(new HashMap<String, TaskExecutionStatus>());


	/**
	 * Instantiates a new data miner access point.
	 */
	public DataMinerAccessPoint() {
		dataMinerService = new DataMinerService();
	}

	/**
	 * Removes the task from memory.
	 *
	 * @param algConfiguration the alg configuration
	 * @return the task execution status
	 */
	private TaskExecutionStatus removeTaskFromMemory(BaseTaskConfiguration algConfiguration){

		return mapExecutionTask.remove(algConfiguration.getConfigurationKey());
	}


	/**
	 * Save task in memory.
	 *
	 * @param algConfiguration the alg configuration
	 * @param taskStatus the task status
	 */
	private void saveTaskInMemory(BaseTaskConfiguration algConfiguration, TaskExecutionStatus taskStatus){

		mapExecutionTask.put(algConfiguration.getConfigurationKey(), taskStatus);
	}


	/**
	 * Gets the running task.
	 *
	 * @param taskConfiguration the task configuration
	 * @return the running task
	 */
	public TaskExecutionStatus getRunningTask(BaseTaskConfiguration taskConfiguration){
		return mapExecutionTask.get(taskConfiguration.getConfigurationKey());

	}


	/**
	 * Abort running task.
	 *
	 * @param taskConfiguration the alg configuration
	 * @throws TaskErrorException the task error exception
	 * @throws TaskNotExecutableException the task not executable exception
	 */
	public void abortRunningTask(TaskConfiguration taskConfiguration) throws TaskErrorException, TaskNotExecutableException{

		TaskExecutionStatus task = getRunningTask(taskConfiguration);

		if(task==null)
			throw new TaskErrorException("The task with configuration: "+taskConfiguration+" is not running");

		SClient sClient;
		try {
			sClient = dataMinerService.getClient();
			sClient.cancelComputation(DMConverter.toComputationId(task.getTaskComputation()));
		}
		catch (Exception e) {
			String error = "Error on get Client or the Operator for id: "+taskConfiguration.getTaskId();
			logger.error(error, e);
			throw new TaskNotExecutableException(error);
		}
	}


	/**
	 * Do run task.
	 *
	 * @param taskConfiguration the alg configuration
	 * @return the task execution status
	 * @throws TaskNotExecutableException the task not executable exception
	 */
	public TaskExecutionStatus doRunTask(TaskConfiguration taskConfiguration) throws TaskNotExecutableException{

		Operator operator;
		SClient sClient;
		try {
			String token = EncrypterUtil.decryptString(taskConfiguration.getMaskedToken());
			sClient = dataMinerService.getClient(token);
			operator = sClient.getOperatorById(taskConfiguration.getTaskId());
		}
		catch (Exception e) {
			String error = "Error on get Client or the Operator for id: "+taskConfiguration.getTaskId();
			logger.error(error, e);
			throw new TaskNotExecutableException(error);
		}

		if (operator == null) {
			logger.error("Operator not found");
			throw new TaskNotExecutableException("Data Miner operator not found");
		}

		try {
			addParametersToOperator(operator, taskConfiguration.getListParameters());
			logger.debug("Start Computation");
			ComputationId computationId = sClient.startComputation(operator);
			logger.debug("Started ComputationId: " + computationId);
			return loadTaskExecutionStatus(taskConfiguration, DMConverter.toDMComputationId(computationId, System.currentTimeMillis(), null));

		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Monitor status.
	 *
	 * @param taskConfiguration the task configuration
	 * @param taskComputation the task computation
	 * @return the task execution status
	 * @throws TaskErrorException the task error exception
	 * @throws TaskNotExecutableException the task not executable exception
	 */
	public TaskExecutionStatus monitorStatus(TaskConfiguration taskConfiguration, final TaskComputation taskComputation) throws TaskErrorException, TaskNotExecutableException{
		TaskExecutionStatus theTaskStatus = getRunningTask(taskConfiguration);

		if(theTaskStatus==null)
			throw new TaskErrorException("No Task is running with the configuration: "+taskConfiguration.getConfigurationKey());

		TaskExecutionStatus tes = loadTaskExecutionStatus(taskConfiguration, taskComputation);

		//MOVED INTO METHOD getOutput
//		switch (tes.getStatus()) {
//		case COMPLETED:
//		case CANCELLED:
//		case FAILED:
//			logger.info("Removing "+tes+ "from memory");
//			removeTaskFromMemory(tes.getTaskConfiguration());
//			break;
//		default:
//			break;
//		}

		return tes;
	}


	/**
	 * Load task execution status.
	 *
	 * @param taskConfiguration the task configuration
	 * @param taskComputation the task computation
	 * @return the task execution status
	 * @throws TaskErrorException the task error exception
	 * @throws TaskNotExecutableException the task not executable exception
	 */
	private TaskExecutionStatus loadTaskExecutionStatus(TaskConfiguration taskConfiguration, final TaskComputation taskComputation) throws TaskErrorException, TaskNotExecutableException{

		SClient sClient;
		ComputationId computationId = DMConverter.toComputationId(taskComputation);
		try {
			String token = EncrypterUtil.decryptString(taskConfiguration.getMaskedToken());
			sClient = dataMinerService.getClient(token);
		}
		catch (Exception e) {
			logger.error("Error on get DM client", e);
			throw new TaskErrorException("Error on getting DataMiner client, Try later");
		}

		TaskExecutionStatus newTaskExecutionStatus = new TaskExecutionStatus(taskConfiguration, taskComputation);

		logger.debug("Requesting operation progress");
		ComputationStatus computationStatus = null;
		try {
			computationStatus = sClient.getComputationStatus(computationId);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();

		}

		logger.debug("ComputationStatus: " + computationStatus);
		if (computationStatus == null) {
			logger.error("ComputationStatus is null");
			return newTaskExecutionStatus;
		}

		Status status = computationStatus.getStatus();
		if (status == null) {
			logger.error("Status is null");
			return newTaskExecutionStatus;
		}

		newTaskExecutionStatus.setMessage(computationStatus.getMessage());
		newTaskExecutionStatus.setPercentCompleted((float) computationStatus.getPercentage());
		//String historyMsg=NEW_LINE+NEW_STATUS_CHARS+Converter.getCurrentFormattedDate(null)+NEW_LINE;
		String historyMsg="";
		switch (status) {
		case ACCEPTED:
			logger.debug("Operation "+TaskStatus.ACCEPTED);
			newTaskExecutionStatus.setStatus(TaskStatus.ACCEPTED);
			historyMsg+= "Status "+TaskStatus.ACCEPTED;
			historyMsg+=computationStatus.getMessage()!=null?": "+computationStatus.getMessage():"";
			break;
		case CANCELLED:
			logger.debug("Operation "+TaskStatus.CANCELLED);
			newTaskExecutionStatus.setStatus(TaskStatus.CANCELLED);
			historyMsg+= "Status "+TaskStatus.CANCELLED;
			historyMsg+=computationStatus.getMessage()!=null?": "+computationStatus.getMessage():"";
			taskComputation.setEndTime(System.currentTimeMillis());
			break;
		case COMPLETE:
			logger.debug("Operation "+TaskStatus.COMPLETED);
			newTaskExecutionStatus.setStatus(TaskStatus.COMPLETED);
			historyMsg+= "Status "+TaskStatus.COMPLETED;
			historyMsg+=computationStatus.getMessage()!=null?": "+computationStatus.getMessage():"";
			taskComputation.setEndTime(System.currentTimeMillis());
			break;
		case FAILED:
			logger.debug("Operation "+TaskStatus.FAILED);
			newTaskExecutionStatus.setStatus(TaskStatus.FAILED);
			historyMsg+= "Status "+TaskStatus.FAILED;
			historyMsg+=computationStatus.getMessage()!=null?": "+computationStatus.getMessage():"";
			taskComputation.setEndTime(System.currentTimeMillis());
			break;
		case RUNNING:
			logger.debug("Operation "+TaskStatus.ONGOING);
			newTaskExecutionStatus.setStatus(TaskStatus.ONGOING);
			historyMsg+= "Status "+TaskStatus.ONGOING;
			historyMsg+=computationStatus.getMessage()!=null?": "+computationStatus.getMessage():"";
			break;
		default:
			newTaskExecutionStatus.setStatus(TaskStatus.INITIALIZING);
			historyMsg+= "Status "+TaskStatus.INITIALIZING;
			historyMsg+=computationStatus.getMessage()!=null?": "+computationStatus.getMessage():"";
			break;

		}

		newTaskExecutionStatus.setMessage(historyMsg);
		saveTaskInMemory(taskConfiguration, newTaskExecutionStatus);
		return newTaskExecutionStatus;

	}


	/**
	 * Gets the output.
	 *
	 * @param taskConfiguration the task configuration
	 * @param taskComputation the task computation
	 * @return the output
	 * @throws TaskErrorException the task error exception
	 * @throws TaskNotExecutableException the task not executable exception
	 */
	public TaskOutput getOutput(TaskConfiguration taskConfiguration, TaskComputation taskComputation) throws TaskErrorException, TaskNotExecutableException {

		TaskExecutionStatus tes = null;

		try{

			tes = monitorStatus(taskConfiguration, taskComputation);

			SClient sClient;
			ComputationId computationId = DMConverter.toComputationId(taskComputation);
			try {
				String token = EncrypterUtil.decryptString(taskConfiguration.getMaskedToken());
				sClient = dataMinerService.getClient(token);
			}
			catch (Exception e) {
				logger.error("Error on get DM client", e);
				throw new TaskErrorException("Error on getting DataMiner client, Try later");
			}

			List<String> outputMessages = retrieveOutput(computationId, sClient);

			//REMOVING THE TASK FROM MEMORY
			switch (tes.getStatus()) {
			case COMPLETED:
			case CANCELLED:
			case FAILED:
				logger.info("Removing "+tes+ "from memory");
				removeTaskFromMemory(tes.getTaskConfiguration());
				break;
			default:
				break;
			}

			return new TaskOutput(tes, outputMessages);

		}catch(Exception e){
			if(tes!=null)
				removeTaskFromMemory(tes.getTaskConfiguration());

			logger.error("Get output error: ",e);
			throw e;
		}
	}


	/**
	 * Retrieve output.
	 *
	 * @param computationId the computation id
	 * @param sClient the s client
	 * @return the list
	 * @throws TaskErrorException the task error exception
	 */
	private List<String> retrieveOutput(ComputationId computationId, SClient sClient) throws TaskErrorException {
		try {
			List<String> outputMessages = new ArrayList<String>();
			OutputData output = sClient.getOutputDataByComputationId(computationId);
			logger.debug("Output: " + output);
			Resource resource = output.getResource();

			if (resource.isMap()) {
				MapResource mapResource = (MapResource) resource;
				for (String key : mapResource.getMap().keySet()) {
					Resource res = mapResource.getMap().get(key);
					outputMessages.add(DMConverter.getOutputMessage(key, res));
				}
			}else{
				outputMessages.add(DMConverter.getOutputMessage("", resource));
			}

			return outputMessages;
		} catch (Exception e) {
			logger.error("Error on retrieve the output for computationId: "+computationId, e);
			throw new TaskErrorException("Error on retrieve the output for computationId: "+computationId);
		}
	}




	/**
	 * Adds the parameters to operator.
	 *
	 * @param operator the operator
	 * @param parameters the parameters
	 * @return the operator
	 */
	private Operator addParametersToOperator(Operator operator, List<TaskParameter> parameters) {
		logger.debug("Adding parameters to operator");

		List<Parameter> listParameters = new ArrayList<Parameter>();
		for (TaskParameter taskParameter : parameters) {

			if(taskParameter.getType()==null)
				continue;

			Parameter dmParameter = DMConverter.toParameter(taskParameter);
			if(dmParameter!=null)
				listParameters.add(dmParameter);


		}
		logger.debug("Parameters list is: " + listParameters);
		operator.setOperatorParameters(listParameters);
		return operator;

	}

	/**
	 * Gets the parameter types.
	 *
	 * @return the parameter types
	 */
	public List<TaskParameterType> getParameterTypes(){
		List<ParameterType> typeNames = Converter.getEnumList(ParameterType.class);
		List<TaskParameterType> types = new ArrayList<TaskParameterType>();
		for (ParameterType parameterType : typeNames) {
			types.add(new TaskParameterType(parameterType.name()));
		}
		return types;

	}


	/**
	 * Gets the list operators.
	 *
	 * @return the list operators
	 * @throws Exception the exception
	 */
	public List<TaskOperator> getListOperators() throws Exception {
		SClient sClient = dataMinerService.getClient();
		List<TaskOperator> listOperator = new ArrayList<TaskOperator>();
		List<OperatorsClassification> operatorsClassifications = sClient.getOperatorsClassifications();

		//logger.debug("OperatorsClassifications: " + operatorsClassifications);

		if(operatorsClassifications!=null && operatorsClassifications.size()>0){
			//GET THE FIRST CATEGORY
			OperatorsClassification firstCategory=operatorsClassifications.get(0);

			if(firstCategory.getOperators()!=null&& !firstCategory.getOperators().isEmpty()){
				for (Operator operator : firstCategory.getOperators()) {
					List<Parameter> inputParameters = sClient.getInputParameters(operator);
					List<Parameter> outputParameters = sClient.getOutputParameters(operator);
					TaskOperator to = DMConverter.toTaskOperator(operator, inputParameters, outputParameters);
					if(to!=null)
						listOperator.add(to);
				}

//				firstCategory.getOperators(); // RETURNS THE LIST OF ALGORITHMS
//						Operator operator=firstCategory.getOperators().get(0);
//						logger.debug("First Operator: "+operator);
//						List<Parameter> inputParameters=sClient.getInputParameters(operator);
//						logger.debug("Input Parameters: "+inputParameters);
//
//						List<Parameter> outputParameters=sClient.getOutputParameters(operator);
//						logger.debug("Output Parameters: "+outputParameters);

			} else {
				logger.debug("Operators void");
			}
		}

		return listOperator;
	}



}
