package org.gcube.portlets.widgets.wstaskexecutor.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.workspacetaskexecutor.dataminer.WorkspaceDataMinerTaskExecutor;
import org.gcube.common.workspacetaskexecutor.shared.FilterOperator;
import org.gcube.common.workspacetaskexecutor.shared.TaskOperator;
import org.gcube.common.workspacetaskexecutor.shared.TaskOutput;
import org.gcube.common.workspacetaskexecutor.shared.TaskParameterType;
import org.gcube.common.workspacetaskexecutor.shared.WSItemObject;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskComputation;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskConfiguration;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskExecutionStatus;
import org.gcube.common.workspacetaskexecutor.shared.exception.ItemNotConfiguredException;
import org.gcube.common.workspacetaskexecutor.shared.exception.ItemNotExecutableException;
import org.gcube.common.workspacetaskexecutor.shared.exception.TaskErrorException;
import org.gcube.common.workspacetaskexecutor.shared.exception.TaskNotExecutableException;
import org.gcube.common.workspacetaskexecutor.shared.exception.WorkspaceFolderLocked;
import org.gcube.common.workspacetaskexecutor.util.EncrypterUtil;
import org.gcube.portlets.widgets.wstaskexecutor.client.rpc.WsTaskExecutorWidgetService;
import org.gcube.portlets.widgets.wstaskexecutor.server.util.PortalContextUtil;
import org.gcube.portlets.widgets.wstaskexecutor.server.util.RuntimeResourceReader;
import org.gcube.portlets.widgets.wstaskexecutor.shared.GcubeScope;
import org.gcube.portlets.widgets.wstaskexecutor.shared.GcubeScopeType;
import org.gcube.portlets.widgets.wstaskexecutor.shared.SelectableOperator;
import org.gcube.portlets.widgets.wstaskexecutor.shared.WSItem;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The server side implementation of the RPC service.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 4, 2018
 */
@SuppressWarnings("serial")
public class WsTaskExecutorWidgetServiceImpl extends RemoteServiceServlet implements
    WsTaskExecutorWidgetService {

	private static Logger logger = LoggerFactory.getLogger(WsTaskExecutorWidgetServiceImpl.class);
	public static final String DM_RESOURCE_NAME = "DataMiner";
	public static final String DM_RESOURCE_CATEGORY = "DataAnalysis";

	/**
	 * Checks if is within portal.
	 *
	 * @return true if you're running into the portal, false if in development
	 */
	public static boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		}
		catch (Exception ex) {
			logger.info("Development Mode ON");
			return false;
		}
	}

	/**
	 * Sets the masked token.
	 *
	 * @param taskConfiguration the task configuration
	 * @return the task configuration
	 * @throws Exception the exception
	 */
	private TaskConfiguration setMaskedToken(TaskConfiguration taskConfiguration) throws Exception{

		String scope = taskConfiguration.getScope();
		if(scope==null)
			throw new Exception("Missing scope in the input configuration. Set it and try again");

		GCubeUser user = PortalContextUtil.getUserLogged(this.getThreadLocalRequest());
		String token = PortalContextUtil.getTokenFor(scope, user.getUsername());
		taskConfiguration.setMaskedToken(EncrypterUtil.encryptString(token));

		return taskConfiguration;

	}

	/**
	 * Gets the task executor.
	 *
	 * @return the task executor
	 */
	private WorkspaceDataMinerTaskExecutor getTaskExecutor(){

		GCubeUser user = PortalContextUtil.getUserLogged(this.getThreadLocalRequest());
		String scope = PortalContextUtil.getCurrentScope(this.getThreadLocalRequest());
		ScopeProvider.instance.set(scope);
		logger.info("Using the user '"+user.getUsername()+ "' and scope '"+scope+"' read from PortalContext for instancing the "+WorkspaceDataMinerTaskExecutor.class.getSimpleName());
		WorkspaceDataMinerTaskExecutor exec =  WorkspaceDataMinerTaskExecutor.getInstance();
		//exec.withOwner(user.getUsername());
		return exec;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wstaskexecutor.client.GreetingService#getListOfScopesForLoggedUser()
	 */
	@Override
	public List<GcubeScope> getListOfScopesForLoggedUser()
		throws Exception {

		logger.debug("getListOfScopesForLoggedUser called");
		List<GcubeScope> listOfScopes = new ArrayList<GcubeScope>();

		if (!isWithinPortal()){
			listOfScopes.add(new GcubeScope("devVRE", "/gcube/devsec/devVRE", GcubeScopeType.VRE));
			listOfScopes.add(new GcubeScope("NextNext", "/gcube/devNext/NextNext", GcubeScopeType.VRE));
			listOfScopes.add(new GcubeScope("devNext", "/gcube/devNext/devNext", GcubeScopeType.VRE));
			Collections.sort(listOfScopes);
			return listOfScopes;
		}

		try {
			GCubeUser user = PortalContext.getConfiguration().getCurrentUser(this.getThreadLocalRequest());
			long userId = user.getUserId();
			// Instanciate the manager
			GroupManager groupManager = new LiferayGroupManager();
			
			//FILTERING THE VREs FOR GATEWAY
			long theGroupId = PortalContext.getConfiguration().getCurrentGroupId(this.getThreadLocalRequest());
			logger.info("Searching registered VREs for userId {} and grouId {}", userId, theGroupId);
			Set<GCubeGroup> listOfGroups = groupManager.listGroupsByUserAndSiteGroupId(userId, theGroupId);
			logger.info("Found {} GcubeGroups (alias VREs) in which the User {} is registred", listOfGroups.size(), user.getUsername());
			
			for (GCubeGroup gCubeGroup : listOfGroups) {
				GcubeScopeType scopeType=null;
				if(groupManager.isVRE(gCubeGroup.getGroupId())){
					scopeType =  GcubeScopeType.VRE;
				}
//				else if(groupManager.isVO(gCubeGroup.getGroupId())){
//					scopeType =  GcubeScopeType.VO;
//				}
//				}else if(groupManager.isRootVO(gCubeGroup.getGroupId())){
//					scopeType =  GcubeScopeType.ROOT;
//				}

				if(scopeType!=null){
					GcubeScope gcubeVRE = new GcubeScope(gCubeGroup.getGroupName(), groupManager.getInfrastructureScope(gCubeGroup.getGroupId()), scopeType);
					listOfScopes.add(gcubeVRE);
				}

			}

			//ADDING THE ROOT SCOPE
//			String infraName = PortalContext.getConfiguration().getInfrastructureName();
//			GcubeScope gcubeRoot = new GcubeScope(infraName, "/"+infraName, GcubeScopeType.ROOT);
//			listOfScopes.add(gcubeRoot);

		}
		catch (UserRetrievalFault | UserManagementSystemException
						| GroupRetrievalFault e) {
			logger.error("Error occurred server-side getting VRE folders: ", e);
			throw new Exception("Sorry, an error occurred server-side getting VRE folders, try again later");
		}
		
		String scope = PortalContextUtil.getCurrentScope(this.getThreadLocalRequest());
		List<GcubeScope> dmScopes = new ArrayList<GcubeScope>(listOfScopes.size());
		//GETTING THE ENDPOINTS WHERE THE DM IS DEPLOYED
		for (GcubeScope gcubeScope : listOfScopes) {
			boolean exists = RuntimeResourceReader.serviceEndpointExists(scope, DM_RESOURCE_NAME, DM_RESOURCE_CATEGORY);
			logger.debug("Is the DM deployed in the scope {}? {}",scope, exists);
			if(exists) 
				dmScopes.add(gcubeScope);
		}

		Collections.sort(dmScopes);
		logger.info("Returning list of DM VREs where the User is registered: "+dmScopes);
		return dmScopes;
	}
	


	/**
	 * Check item task configurations.
	 *
	 * @param itemId the item id
	 * @return the list
	 * @throws Exception the exception
	 */
	@Override
	public List<TaskConfiguration> checkItemTaskConfigurations(String itemId) throws Exception {
		logger.debug("Checking Task Configurations for item: "+itemId);
		WorkspaceDataMinerTaskExecutor exec =  getTaskExecutor();
		List<TaskConfiguration> confs = null;
		try {
			confs = exec.checkItemConfigurations(itemId);
		}
		catch (ItemNotConfiguredException e){
			String msg = "No TaskConfiguration found for itemId: "+itemId+", retuning null";
			logger.info(msg);
			throw e;
		}catch (WorkspaceFolderLocked e) {
			logger.info(e.getMessage());
			throw e;

		}catch (Exception e) {
			logger.error("Error on checking TaskConfigurations for itemId: "+itemId,e);
			throw new Exception("Error occurred during checking Task Configurations for id: "+itemId+ ". Refresh and try again later");
		}

		logger.debug("Returning configurations: "+confs);

		return confs;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wstaskexecutor.client.rpc.WsTaskExecutorWidgetService#getAvailablesParameterTypes()
	 */
	@Override
	public List<TaskParameterType> getAvailableParameterTypes()
		throws Exception {

		WorkspaceDataMinerTaskExecutor exec =  getTaskExecutor();
		return exec.getParameterTypes();
	}


	/**
	 * Creates the task configuration.
	 *
	 * @param itemId the item id
	 * @param taskConfiguration the task configuration
	 * @param isUpdate the is update
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Override
	public Boolean createTaskConfiguration(String itemId, TaskConfiguration taskConfiguration, boolean isUpdate) throws Exception{
		WorkspaceDataMinerTaskExecutor exec =  getTaskExecutor();
		try {
			GCubeUser user = PortalContextUtil.getUserLogged(this.getThreadLocalRequest());
			taskConfiguration.setOwner(user.getUsername());
			taskConfiguration = setMaskedToken(taskConfiguration);
			exec.setTaskConfiguration(taskConfiguration);
			return true;
		}
		catch (Exception e) {
			logger.error("Error on creating the TaskConfiguration for itemId: "+itemId,e);
			throw new Exception("Sorry, an rrror occurred during creating the configuration for itemId: "+itemId+ ". Refresh and try again later");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.workspacetaskexecutor.ConfigurableTask#removeTaskConfig(java.lang.Object)
	 */
	@Override
	public Boolean removeTaskConfiguration(TaskConfiguration taskConfiguration) throws ItemNotExecutableException, Exception {
		logger.debug("Remove task configuration "+taskConfiguration+ " starts...");

		GCubeUser user = PortalContextUtil.getUserLogged(this.getThreadLocalRequest());

		if(user.getUsername().compareTo(taskConfiguration.getOwner())!=0)
			throw new Exception("You has not authorized to delete this configuration. You must be the owner of");

		WorkspaceDataMinerTaskExecutor exec =  getTaskExecutor();
		return exec.removeTaskConfiguration(taskConfiguration);
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wstaskexecutor.client.rpc.WsTaskExecutorWidgetService#getItemTaskConfigurations(java.lang.String)
	 */
	@Override
	public List<TaskConfiguration> getItemTaskConfigurations(String itemId) throws Exception{
		logger.debug("Getting Task Configurations for item: "+itemId);
		WorkspaceDataMinerTaskExecutor exec =  getTaskExecutor();
		List<TaskConfiguration> confs = null;
		try {
			confs = exec.getListOfTaskConfigurations(itemId);
		}
		catch (ItemNotConfiguredException e){
			String msg = "No TaskConfiguration found for itemId: "+itemId+", retuning null";
			logger.info(msg);
			throw e;
		}catch (WorkspaceFolderLocked e) {
			logger.info(e.getMessage());
			throw e;

		}catch (Exception e) {
			logger.error("Error on getting TaskConfigurations for itemId: "+itemId,e);
			throw new Exception("Error occurred during loading Task Configurations for id: "+itemId+ ". Refresh and try again later");
		}

		logger.debug("Returning configurations: "+confs);

		return confs;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wstaskexecutor.client.rpc.WsTaskExecutorWidgetService#executeTheTask(org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskConfiguration)
	 */
	@Override
	public TaskExecutionStatus executeTheTask(TaskConfiguration taskConfiguration) throws ItemNotExecutableException, TaskNotExecutableException, Exception {

		WorkspaceDataMinerTaskExecutor exec =  getTaskExecutor();
		return exec.executeRun(taskConfiguration);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wstaskexecutor.client.rpc.WsTaskExecutorWidgetService#monitorTaskExecutionStatus(org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskConfiguration, org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskComputation)
	 */
	@Override
	public TaskExecutionStatus monitorTaskExecutionStatus(
		TaskConfiguration configuration, TaskComputation taskComputation)
		throws TaskErrorException, Exception{
		WorkspaceDataMinerTaskExecutor exec =  getTaskExecutor();
		return exec.monitorRunStatus(configuration, taskComputation);
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wstaskexecutor.client.rpc.WsTaskExecutorWidgetService#monitorTaskExecutionStatus(org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskConfiguration, org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskComputation)
	 */
	@Override
	public TaskOutput getOutput(
		TaskConfiguration taskConfiguration, TaskComputation taskComputation)
		throws TaskErrorException, Exception{
		WorkspaceDataMinerTaskExecutor exec =  getTaskExecutor();
		return exec.getTaskOutput(taskConfiguration, taskComputation);
	}




	/**
	 * Gets the list operators per scope.
	 *
	 * @param scope the scope
	 * @param selectableOperators the selectable operators
	 * @return the list operators per scope
	 * @throws Exception the exception
	 */
	@Override
	public List<TaskOperator> getListOperatorsPerScope(String scope, SelectableOperator selectableOperators) throws Exception{

		if(scope==null || scope.isEmpty())
			throw new Exception("Invalid scope null");

		WorkspaceDataMinerTaskExecutor exec =  getTaskExecutor();
		String originalScope = ScopeProvider.instance.get();
		String originalToken = SecurityTokenProvider.instance.get();

		String[] filterForParameterTypes = null;
		FilterOperator filterOperator = null;
		if(selectableOperators!=null){
			filterForParameterTypes = selectableOperators.getFilterForParameterTypes();
			filterOperator = selectableOperators.getFilterOperator();
		}

		try{
			GCubeUser user = PortalContextUtil.getUserLogged(this.getThreadLocalRequest());
			String token = PortalContextUtil.getTokenFor(scope, user.getUsername());
			ScopeProvider.instance.set(scope);
			SecurityTokenProvider.instance.set(token);
			List<TaskOperator> operators = exec.getListOperators(filterForParameterTypes, filterOperator);

			//JUST A DEBUG LOG
			/*if(logger.isDebugEnabled()){
				//APPLYING FILTERS ON PARAMETER NAME/TYPE
				for (TaskOperator taskOperator : operators) {
					logger.trace("***Algor: "+taskOperator.getName());
					List<TaskParameter> io = taskOperator.getInputOperators();
					for (TaskParameter taskParameter : io) {
						logger.trace("key: "+taskParameter.getKey() + ", value: "+taskParameter.getValue() +", defaultValue: "+taskParameter.getDefaultValue());

					}

				}
			}*/

			logger.info("Returning "+operators.size()+ " operator/s for the scope: "+scope);
			return operators;

		}catch(Exception e){
			logger.error("Error on getting list of Operators for scope: "+scope,e);
			throw new Exception("It is not possible to get list of Operators in the scope: "+scope +". Please, check if a DataMiner is available for this scope");
		}finally{
			if(originalScope!=null)
				ScopeProvider.instance.set(originalScope);

			if(originalToken!=null)
				SecurityTokenProvider.instance.set(originalToken);
		}
	}


	/**
	 * Load item.
	 *
	 * @param itemId the item id
	 * @return the WS item
	 * @throws Exception the exception
	 */
	@Override
	public WSItem loadItem(String itemId) throws Exception{
		WorkspaceDataMinerTaskExecutor exec =  getTaskExecutor();

		try{
			WSItemObject wsIO = exec.loadItem(itemId);
			return new WSItem(wsIO.getItemId(), wsIO.getItemName(), wsIO.getOwner(), wsIO.getPublicLink(), wsIO.isFolder());

		}catch(Exception e){
			throw new Exception(e);
		}
	}


}
