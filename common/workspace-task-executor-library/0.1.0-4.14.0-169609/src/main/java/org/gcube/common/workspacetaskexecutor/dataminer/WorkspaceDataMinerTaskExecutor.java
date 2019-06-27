package org.gcube.common.workspacetaskexecutor.dataminer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.workspacetaskexecutor.shared.ExecutableItem;
import org.gcube.common.workspacetaskexecutor.shared.ExecutableTask;
import org.gcube.common.workspacetaskexecutor.shared.FilterOperator;
import org.gcube.common.workspacetaskexecutor.shared.TaskOperator;
import org.gcube.common.workspacetaskexecutor.shared.TaskOutput;
import org.gcube.common.workspacetaskexecutor.shared.TaskParameter;
import org.gcube.common.workspacetaskexecutor.shared.TaskParameterType;
import org.gcube.common.workspacetaskexecutor.shared.WSItemObject;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskComputation;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskConfiguration;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskExecutionStatus;
import org.gcube.common.workspacetaskexecutor.shared.exception.ItemNotConfiguredException;
import org.gcube.common.workspacetaskexecutor.shared.exception.ItemNotExecutableException;
import org.gcube.common.workspacetaskexecutor.shared.exception.TaskConfigurationNotFoundException;
import org.gcube.common.workspacetaskexecutor.shared.exception.TaskErrorException;
import org.gcube.common.workspacetaskexecutor.shared.exception.TaskNotExecutableException;
import org.gcube.common.workspacetaskexecutor.shared.exception.WorkspaceFolderLocked;
import org.gcube.common.workspacetaskexecutor.util.JsonUtil;
import org.gcube.common.workspacetaskexecutor.util.WsUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;


/**
 * The Class WorkspaceDataMinerTaskExecutor.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 4, 2018
 */
public class WorkspaceDataMinerTaskExecutor implements ExecutableTask<TaskConfiguration, TaskComputation, TaskExecutionStatus>, ExecutableItem<TaskConfiguration>{

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(WorkspaceDataMinerTaskExecutor.class);

	private static WorkspaceDataMinerTaskExecutor INSTANCE = null;

	public static final String WS_DM_TASK_TASK_CONF = "WS-DM-TASK.TASK-CONF";

	private String usernameOwner;

	private DataMinerAccessPoint dataMinerAP;

	private JsonUtil jsonUtil = new JsonUtil();


	/**
	 * Gets the data miner access point.
	 *
	 * @return the data miner access point
	 */
	private DataMinerAccessPoint getDataMinerAccessPoint(){
		if(dataMinerAP==null)
			dataMinerAP = new DataMinerAccessPoint();

		return dataMinerAP;
	}

	/**
	 * Instantiates a new workspace data miner task executor.
	 */
	private WorkspaceDataMinerTaskExecutor() {
	}


	/**
	 * With owner.
	 *
	 * @param usernameOwner the username owner
	 */
	public void withOwner(String usernameOwner){
		this.usernameOwner = usernameOwner;
	}


	/**
	 * Gets the single instance of WorkspaceDataMinerTaskExecutor.
	 *
	 * @return single instance of WorkspaceDataMinerTaskExecutor
	 */
	public static WorkspaceDataMinerTaskExecutor getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new WorkspaceDataMinerTaskExecutor();
		}
		return INSTANCE;
	}


	/**
	 * Check owner.
	 *
	 * @throws Exception the exception
	 */
	private void checkOwner() throws Exception {

		if(usernameOwner==null || usernameOwner.isEmpty())
			throw new Exception("You must set a valid 'usernameOwner'. Using the method #withOwner");

	}


	/**
	 * Validate task configuration.
	 *
	 * @param taskConfiguration the task configuration
	 * @throws Exception the exception
	 */
	private static void ValidateTaskConfiguration(TaskConfiguration taskConfiguration) throws Exception {
		Validate.notNull(taskConfiguration, "The "+TaskConfiguration.class.getSimpleName()+" is null");
		Validate.notNull(taskConfiguration.getWorkspaceItemId(), "The WorkspaceItem Id in the configuration is null");
		Validate.notNull(taskConfiguration.getTaskId(), "The Task Id in the configuration is null");
		Validate.notNull(taskConfiguration.getMaskedToken(), "The Token is missing (add it as masked string)");
	}


	/**
	 * Gets the configuration from saved.
	 *
	 * @param workspaceItemId the workspace item id
	 * @param configurationKey the configuration key
	 * @return the configuration from saved
	 * @throws TaskConfigurationNotFoundException the task configuration not found exception
	 * @throws Exception the exception
	 */
	private TaskConfiguration getConfigurationFromSaved(String workspaceItemId, String configurationKey) throws TaskConfigurationNotFoundException, Exception{

		List<TaskConfiguration> listConfigs = getListOfTaskConfigurations(workspaceItemId);

		if(listConfigs!=null){
			//validating the configuration server-side
			for (TaskConfiguration taskConf : listConfigs) {
				//if the configurationKey are equals
				if(taskConf.getConfigurationKey().compareTo(configurationKey)==0){
					return taskConf;
				}
			}
		}

		throw new TaskConfigurationNotFoundException("The configuration with "+TaskConfiguration.FIELD_CONFIGURATION_KEY+" "+configurationKey+" does not exist");
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.workspacetaskexecutor.CheckableTask#checkItemExecutable(java.lang.String)
	 */
	@Override
	public List<TaskConfiguration> getListOfTaskConfigurations(String workspaceItemId) throws Exception {
		logger.debug("Get list of Task Configurations for "+workspaceItemId+" starts...");
		List<TaskConfiguration> conf = null;
		checkOwner();
		WorkspaceItem item = WsUtil.getItem(usernameOwner, workspaceItemId);
		String arrayConf = WsUtil.getPropertyValue(item, WS_DM_TASK_TASK_CONF);
		logger.info("Read "+WS_DM_TASK_TASK_CONF+" value: "+arrayConf);
		if(arrayConf==null || arrayConf.isEmpty()){
			logger.warn("The item id "+workspaceItemId+" has not "+TaskConfiguration.class.getSimpleName() +" saved");
			return null;
		}

		try{
			TypeReference<List<TaskConfiguration>> mapType = new TypeReference<List<TaskConfiguration>>() {};
			conf = jsonUtil.readList(arrayConf, mapType);
		}catch(Exception e){
			logger.warn("The item id "+workspaceItemId+" has a wrong "+TaskConfiguration.class.getSimpleName()+" saved");
			logger.error("Error on serializing configuration: "+arrayConf, e);
			//eraseAllTaskConfigurations(workspaceItemId);
		}

		logger.debug("Found configuration/s: "+conf);

		if(conf!=null)
			logger.info("Returning "+conf.size()+" configuration/s");

		return conf;
	}


	/**
	 * Check item configurations.
	 *
	 * @param workspaceItemId the workspace item id
	 * @return the list
	 * @throws ItemNotConfiguredException the item not configured exception
	 * @throws WorkspaceFolderLocked the workspace folder locked
	 * @throws Exception the exception
	 */
	public List<TaskConfiguration> checkItemConfigurations(String workspaceItemId) throws ItemNotConfiguredException, WorkspaceFolderLocked, Exception{
		List<TaskConfiguration> confs = getListOfTaskConfigurations(workspaceItemId);

		if(confs==null)
			throw new ItemNotConfiguredException("The item "+workspaceItemId+" has not "+TaskConfiguration.class.getSimpleName()+" saved");

		DataMinerAccessPoint dmAP = getDataMinerAccessPoint();

		for (TaskConfiguration taskConfiguration : confs) {
			TaskExecutionStatus taskStatus = dmAP.getRunningTask(taskConfiguration);
			if(taskStatus!=null)
				throw new WorkspaceFolderLocked(workspaceItemId, "The item: "+workspaceItemId+ "is locked by running Task: "+taskStatus);
		}

		return confs;
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.workspacetaskexecutor.CheckableTask#isItemExecutable(java.lang.String)
	 */
	@Override
	public Boolean isItemExecutable(String workspaceItemId) throws Exception {
		List<TaskConfiguration> confs = getListOfTaskConfigurations(workspaceItemId);

		if(confs==null || confs.isEmpty()){
			logger.debug("The item: "+workspaceItemId+" has not a (valid) configuration "+WS_DM_TASK_TASK_CONF);
			return false;
		}
		logger.debug("The item: "+workspaceItemId+" has a valid "+WS_DM_TASK_TASK_CONF+" with "+confs.size()+" configuration/s");
		return true;

	}


	/**
	 * Load item.
	 *
	 * @param itemId the item id
	 * @return the WS item object
	 */
	public WSItemObject loadItem(String itemId){
		WorkspaceItem item = null;
		Validate.notNull(itemId, "Input parameter itemId is null");
		try{
			item = WsUtil.getItem(usernameOwner, itemId);
		}catch(Exception e){
			logger.error("Error during get item with id: "+itemId, e);
			throw new Error("Error during get item with id: "+itemId+". Eihter it does not exit or you have not permissions to read it");
		}

		Validate.notNull(item, "The item with id: "+itemId+" is null");
		WSItemObject wsItem = new WSItemObject();
		try {
			wsItem.setItemId(item.getId());
			wsItem.setItemName(item.getName());
			wsItem.setOwner(item.getOwner().getPortalLogin());
			wsItem.setFolder(item.isFolder());
			if(!item.isFolder()){
				wsItem.setPublicLink(item.getPublicLink(false));
			}

			return wsItem;
		}
		catch (InternalErrorException e) {
			//silent
			logger.warn("Error during filling item properties for item id: "+itemId, e);
			try {
				wsItem.setItemId(item.getId());
				wsItem.setItemName(item.getName());
				return wsItem;
			}
			catch (InternalErrorException e1) {
				logger.error("Error during filling id and name for item id: "+itemId, e);
				return null;
			}

		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.common.workspacetaskexecutor.ConfigurableTask#removeTaskConfig(java.lang.Object)
	 */
	@Override
	public Boolean removeTaskConfiguration(TaskConfiguration taskConfiguration) throws ItemNotExecutableException, Exception {

		logger.info("Removing task configuration with key: "+taskConfiguration.getConfigurationKey() + " starts...");

		ValidateTaskConfiguration(taskConfiguration);
		checkOwner();
		boolean found = false;

		WorkspaceItem item = WsUtil.getItem(usernameOwner, taskConfiguration.getWorkspaceItemId());
		List<TaskConfiguration> configurations = getListOfTaskConfigurations(taskConfiguration.getWorkspaceItemId());

		if(configurations==null)
			throw new ItemNotExecutableException("The item "+taskConfiguration.getWorkspaceItemId()+" has not configurations saved");


		List<TaskConfiguration> newConfigurations = new ArrayList<TaskConfiguration>(configurations.size());

		for (TaskConfiguration tc : configurations) {
			if(tc.getConfigurationKey().compareTo(taskConfiguration.getConfigurationKey())!=0){
				newConfigurations.add(tc);
			}else{
				//Configuration found
				found = true;
			}
		}

		if(found){
			JSONArray newConfgs = jsonUtil.toJSONArray(newConfigurations);
			WsUtil.setPropertyValue(item, WS_DM_TASK_TASK_CONF, newConfgs.toString());
			logger.info("Removed correclty the task configuration "+taskConfiguration+ " from saved configurations");
			return true;
		}

		logger.info("Task configuration "+taskConfiguration+ " not found, removed configuration is false");
		return false;

	}


	/* (non-Javadoc)
	 * @see org.gcube.common.workspacetaskexecutor.shared.ExecutableItem#getTaskConfiguration(java.lang.String)
	 */
	@Override
	public TaskConfiguration getTaskConfiguration(String itemId, String configurationKey)
		throws TaskConfigurationNotFoundException, Exception {

		return getConfigurationFromSaved(itemId, configurationKey);
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.workspacetaskexecutor.ConfigurableTask#addTaskConfig(java.lang.Object)
	 */
	@Override
	public void setTaskConfiguration(TaskConfiguration taskConfiguration) throws Exception {
		logger.debug("Set task configuration "+taskConfiguration+" starts...");
		ValidateTaskConfiguration(taskConfiguration);
		checkOwner();

		boolean found = false;
		WorkspaceItem item = WsUtil.getItem(usernameOwner, taskConfiguration.getWorkspaceItemId());
		List<TaskConfiguration> configurations = getListOfTaskConfigurations(taskConfiguration.getWorkspaceItemId());

		if(configurations==null)
			configurations = new ArrayList<TaskConfiguration>(1); //It is the first configuration server-side

		List<TaskConfiguration> newConfigurations = new ArrayList<TaskConfiguration>(configurations.size());

		for (TaskConfiguration tc : configurations) {
			if(tc.getConfigurationKey().compareTo(taskConfiguration.getConfigurationKey())!=0){
				newConfigurations.add(tc);
			}else{
				//Configuration found
				found = true;
				logger.info("The configuration with "+TaskConfiguration.FIELD_CONFIGURATION_KEY +" found, updating it");
				newConfigurations.add(taskConfiguration);
			}
		}

		if(!found){
			logger.info("The configuration with "+TaskConfiguration.FIELD_CONFIGURATION_KEY +" not found, adding it as new");
			newConfigurations.add(taskConfiguration);
		}
		try{
			JSONArray jsonConfigs = jsonUtil.toJSONArray(newConfigurations);
			WsUtil.setPropertyValue(item, WS_DM_TASK_TASK_CONF, jsonConfigs.toString());
			logger.debug("Updated json configuration/s is/are: "+jsonConfigs.toString());
			logger.info(taskConfiguration +" added/updated");
		}catch(JSONException e){
			logger.error("Error on saving Task Configuration: "+taskConfiguration, e);
			throw new Exception("Error on saving Task Configuration: "+taskConfiguration+", Please retry");
		}
	}


	/**
	 * Gets the parameter types.
	 *
	 * @return the parameter types
	 */
	public List<TaskParameterType> getParameterTypes(){
		return getDataMinerAccessPoint().getParameterTypes();

	}

	/* (non-Javadoc)
	 * @see org.gcube.common.workspacetaskexecutor.shared.ExecutableItem#eraseAllExecutableConfigurations()
	 */
	@Override
	public Boolean eraseAllTaskConfigurations(String itemId) throws ItemNotExecutableException, Exception {
		logger.info("Erase all configurations starts...");
		Validate.notNull(itemId, "The itemId is null");
		checkOwner();
		//Check if the item is executable
		isItemExecutable(itemId);
		WorkspaceItem item = WsUtil.getItem(usernameOwner, itemId);
		WsUtil.setPropertyValue(item, WS_DM_TASK_TASK_CONF, null);
		return true;
	}


	/* (non-Javadoc)
	 * @see org.gcube.common.workspacetaskexecutor.ExecutableTask#doRun(org.gcube.common.workspacetaskexecutor.BaseTaskConfiguration)
	 */
	@Override
	public TaskExecutionStatus executeRun(TaskConfiguration taskConfiguration)
		throws ItemNotExecutableException, TaskNotExecutableException, Exception {

		ValidateTaskConfiguration(taskConfiguration);
		checkOwner();

		TaskConfiguration taskConf = getConfigurationFromSaved(taskConfiguration.getWorkspaceItemId(), taskConfiguration.getConfigurationKey());
		DataMinerAccessPoint dap = getDataMinerAccessPoint();
		return dap.doRunTask(taskConf);
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.workspacetaskexecutor.ExecutableTask#stopRun(org.gcube.common.workspacetaskexecutor.BaseTaskConfiguration)
	 */
	@Override
	public Boolean abortRun(TaskConfiguration taskConfiguration)
		throws TaskErrorException, TaskNotExecutableException, Exception {

		ValidateTaskConfiguration(taskConfiguration);

		DataMinerAccessPoint dap = getDataMinerAccessPoint();
		dap.abortRunningTask(taskConfiguration);
		return true;
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.workspacetaskexecutor.ExecutableTask#monitorRunStatus(org.gcube.common.workspacetaskexecutor.BaseTaskConfiguration)
	 */
	@Override
	public TaskExecutionStatus monitorRunStatus(
		TaskConfiguration taskConfiguration, TaskComputation taskComputation)
		throws TaskErrorException, Exception {

		ValidateTaskConfiguration(taskConfiguration);
		DataMinerAccessPoint dap = getDataMinerAccessPoint();
		return dap.monitorStatus(taskConfiguration, taskComputation);
	}


	/**
	 * Gets the list operators.
	 *
	 * @param filterForParameterTypes the filter for parameter types. It returns only {@link TaskOperator} matching the input filters
	 * @param operator the operator applied to the filters. It must be of type {@link FilterOperator}
	 * @return the list operators
	 * @throws Exception the exception
	 */
	public List<TaskOperator> getListOperators(String[] filterForParameterTypes, FilterOperator operator) throws Exception{

		DataMinerAccessPoint dap = getDataMinerAccessPoint();

		if(filterForParameterTypes==null || filterForParameterTypes.length==0){
			logger.info("Returning "+dap.getListOperators().size()+ " operator/s. No filter applied");
			return dap.getListOperators();
		}

		List<String> listFilterParameterTypes = new ArrayList<String>();
		listFilterParameterTypes.addAll(Arrays.asList(filterForParameterTypes));
		listFilterParameterTypes.removeAll(Arrays.asList("", null));
		logger.info("Getting list operators applying filters: "+listFilterParameterTypes+", operator: "+operator);

		List<TaskOperator> filteredListOperators = new ArrayList<TaskOperator>(dap.getListOperators().size());

		if(operator==null){
			operator = FilterOperator.LOGICAL_OR;
			logger.info("The input parameter "+FilterOperator.class.getSimpleName() +" is null, using default "+operator);
		}

		//APPLYING FILTERS ON PARAMETER NAME/TYPE
		for (TaskOperator taskOperator : dap.getListOperators()) {
			logger.trace("***Algor: "+taskOperator.getName());
			List<TaskParameter> io = taskOperator.getInputOperators();
			int filterPassed = 0;

			List<String> matchedFilter = new ArrayList<String>();
			for (TaskParameter taskParameter : io) {
				logger.trace("has type: "+taskParameter.getType());
				for (String filterParameterName : listFilterParameterTypes) {
					if(taskParameter.getType()!=null){
						if(taskParameter.getType().getType().compareToIgnoreCase(filterParameterName)==0){
							//IGNORING FILTER ALREADY MATCHED
							if(!matchedFilter.contains(filterParameterName)){
								matchedFilter.add(filterParameterName);
								filterPassed++;
							}
						}
					}
				}
			}

			if(operator.equals(FilterOperator.LOGICAL_OR) && filterPassed>=1){
				filteredListOperators.add(taskOperator);
				logger.info("Added operator: "+taskOperator.getName() + ". It is matching "+FilterOperator.LOGICAL_OR +" with filters: "+listFilterParameterTypes);
				logger.trace("Operators are: "+taskOperator.getInputOperators());
			//IN AND ALL FILTERS MUST BE MATCHED
			}else if(operator.equals(FilterOperator.LOGICAL_AND) && filterPassed==listFilterParameterTypes.size()){
				filteredListOperators.add(taskOperator);
				logger.info("Added operator: "+taskOperator.getName()+ ". It is matching "+FilterOperator.LOGICAL_AND +" with filters: "+listFilterParameterTypes);
				logger.trace("Operators are: "+taskOperator.getInputOperators());
			}
			else if(operator.equals(FilterOperator.LOGICAL_NOT) && filterPassed==0){
				filteredListOperators.add(taskOperator);
				logger.info("Added operator: "+taskOperator.getName() + ". It is matching "+FilterOperator.LOGICAL_NOT +" with filters: "+listFilterParameterTypes);
				logger.trace("Operators are: "+taskOperator.getInputOperators());
			}else{
				logger.info("Removed operator: "+taskOperator.getName() + ". It is not matching the operator: "+operator+" with filters: "+listFilterParameterTypes);
				logger.trace("Operators are: "+taskOperator.getInputOperators());
				filteredListOperators.remove(taskOperator);
			}

		}

		return filteredListOperators;
	}


	/**
	 * Gets the task output.
	 *
	 * @param taskConfiguration the task configuration
	 * @param taskComputation the task computation
	 * @return the task output
	 * @throws TaskErrorException the task error exception
	 * @throws Exception the exception
	 */
	public TaskOutput getTaskOutput(TaskConfiguration taskConfiguration, TaskComputation taskComputation) throws TaskErrorException, Exception {

		ValidateTaskConfiguration(taskConfiguration);
		DataMinerAccessPoint dap = getDataMinerAccessPoint();
		return dap.getOutput(taskConfiguration, taskComputation);
	}

}
