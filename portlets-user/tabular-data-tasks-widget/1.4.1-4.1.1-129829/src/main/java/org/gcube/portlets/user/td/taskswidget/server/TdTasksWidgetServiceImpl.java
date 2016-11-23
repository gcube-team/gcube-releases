package org.gcube.portlets.user.td.taskswidget.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTaskException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.operation.TaskId;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.taskswidget.client.rpc.TdTasksWidgetService;
import org.gcube.portlets.user.td.taskswidget.server.exception.TdConverterException;
import org.gcube.portlets.user.td.taskswidget.server.service.TaskTabularDataService;
import org.gcube.portlets.user.td.taskswidget.server.session.SessionUtil;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdJobModel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdOperationModel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdTaskModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TdTasksWidgetServiceImpl extends RemoteServiceServlet implements TdTasksWidgetService {

	public static Logger logger = LoggerFactory.getLogger(TdTasksWidgetServiceImpl.class);
//	public static int fakeCacheSize = 50;  //TODO FAKE CODE
	
	private static boolean debugApplication = false;

	private String debugTabularResource;

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	
	protected TaskTabularDataService getTabularDataServiceClient() {
		
		try {
			ASLSession aslSession = getASLSession();
			TaskTabularDataService serviceClient = SessionUtil.getTaskTdServiceClient(aslSession);
			
			if(serviceClient==null){
				serviceClient = new TaskTabularDataService(aslSession.getScope(), aslSession.getUsername());
				SessionUtil.setTaskServiceClient(aslSession,serviceClient);
			}

			return serviceClient;
			
		}catch (Exception e) {
			logger.error("Error on get Tabular Data Service Client", e);
		}
		
		return null;
	}
	
	protected Map<Long, OperationDefinition> getMapOperationDescription() {
		
		try {
			ASLSession aslSession = getASLSession();
			Map<Long, OperationDefinition> map = SessionUtil.getMapOperationDescription(aslSession);
			
			if(map==null){
				TaskTabularDataService service = getTabularDataServiceClient();
				map = service.getOperationDescriptionMap();
				SessionUtil.setMapOperationDescription(aslSession, map);
			}

			return map;
			
		}catch (Exception e) {
			logger.error("Error on get OperationDescription Map", e);
			logger.error("Retrieving empty operation map", e);
			return new HashMap<Long, OperationDefinition>(1);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	protected ASLSession getASLSession() {
		return SessionUtil.getAslSession(this.getThreadLocalRequest().getSession());
	}

	
	/**
	 * @return
	 * @throws Exception 
	 */
	protected TabularResourceId getCurrentTabularResource() throws Exception {

		logger.trace("debugApplication: "+debugApplication);
		
		if(!debugApplication){
			logger.trace("Get current tabular resource is NOT IN DEBUG MODE!");
			try {
				
				logger.info("Get current tabular resource..");
//				System.out.println("Get current tabular resource..");
				
				HttpSession httpSession = this.getThreadLocalRequest().getSession();
				
				TabResource currentTabulaResource = org.gcube.portlets.user.td.gwtservice.server.SessionUtil.getTabResource(httpSession);
				
	//			TabResource currentTabulaResource = gwtTdService.getTabResourceInformation(httpSession);
				int id = Integer.parseInt(currentTabulaResource.getTrId().getId());
				
				logger.trace("Found current tabular resource TR id: "+id+", returning");
//				System.out.println("Found current tabular resource TR id: "+id+", returning");
				return new TabularResourceId(id);
				
			
			} catch (Exception e) {
				logger.error("Error on get current tabula resource",e);
				throw new Exception("Error on recovering current tabular resource");
			}
		}else {
			logger.warn("Get current tabular resource starting in TEST MODE, debugging fake TR ID: "+debugTabularResource);
			return new TabularResourceId(Integer.parseInt(debugTabularResource));
		}

		//TODO FAKE CODE
//		return new TabularResourceId(42);
	}

	@Override
	public int countTdTasksFromCache() throws Exception{
		
//		/***********************FAKE CODE*******************************/
//		
//		return 5;
//		
//		/***********************END FAKE CODE*************************/
		
		logger.trace("Get tabular data - cache tasks size");
		LinkedHashMap<String, TdTaskModel> cacheTasks;
		
		try{
			ASLSession aslSession = getASLSession();
			cacheTasks = SessionUtil.getTasksCache(aslSession);
			
			if(cacheTasks==null){
				logger.warn("Cache is empty, returning size 0");
				return 0;
			}
			
		}catch (Exception e) {
			logger.error("Error on count cached tasks",e);
			return 0;
		}
		
		return cacheTasks!=null?cacheTasks.size():0;
		
	}

	@Override
	public List<TdTaskModel> getTdTasks(int start, int limit, boolean forceupdate) throws Exception {
		logger.trace("Get tabular data tasks, start: "+start+" limit: "+limit+" forceupdate: "+forceupdate);
		
//		printServlets();

		List<TdTaskModel> listTaskModel = null;
		List<TdTaskModel> sub = new ArrayList<TdTaskModel>();
		try{
		
			TaskTabularDataService service = getTabularDataServiceClient();
			
			if(service==null)
				throw new Exception("An error occurred on cantacting the Tabular Data service, try again later");
	
			ASLSession aslSession = getASLSession();
			
			if(forceupdate){
				logger.error("Force update is  true, invaliditing tasks cache");
				SessionUtil.setTasksCache(aslSession, null);
			}
			
			LinkedHashMap<String, TdTaskModel> linkedHashTaskModel = SessionUtil.getTasksCache(aslSession);
			
			if(linkedHashTaskModel==null){ //CACHE IS EMPTY
				logger.warn("Cache is empty, retriving data from service");
				linkedHashTaskModel = new LinkedHashMap<String, TdTaskModel>();
				
//				for (TabularResource tr : service.getTabularResources())
//					System.out.println(tr);
				
				TabularResourceId trId = getCurrentTabularResource();
				logger.trace("Found TabularResourceId: "+trId);
//				System.out.println("Found TabularResourceId: "+trId);
			
				List<Task> listTask = service.getTasks(trId);
				logger.trace("List Task have size: "+listTask.size());
				for (Task task : listTask) {
					try {
						
						logger.trace("Found Task id: "+task.getId() +" converting..");
						TdTaskModel taskModel = TdConverterBeanGWT.taskToTdTaskModel(task, trId, getTabularDataServiceClient(), getMapOperationDescription());
						
						logger.trace("Converted Task: "+taskModel);
						
						if(task.getId()!=null)
							linkedHashTaskModel.put(task.getId().getValue(), taskModel);
						
					} catch (TdConverterException e) {
						logger.error("Error on converting Task with id: "+task.getId(), e);
					}
				}
				
				logger.trace("Saving cache with : "+linkedHashTaskModel.size() +" task/s model");
				SessionUtil.setTasksCache(aslSession, linkedHashTaskModel); //SAVING CACHE
			}
			
			int end = Math.min(start+limit, linkedHashTaskModel.size());
			start = Math.min(start, end);
	
			listTaskModel = new ArrayList<TdTaskModel>(linkedHashTaskModel.values());
			
			logger.trace("chunk selected data bounds [start: "+start+" end: " + end+"]");
			
			listTaskModel = listTaskModel.subList(start, end);
			
			sub = new ArrayList<TdTaskModel>(listTaskModel);
			
		}catch (TDGWTSessionExpiredException e){
			logger.error("TDGWTSessionExpiredException, session expired", e);
			throw e;
		} catch (TDGWTServiceException e) {
			logger.error("TDGWTServiceException", e);
			throw e;
		} catch (SecurityException e) {
			logger.error("SecurityException", e);
			throw e;
		}catch (Exception e) {
			logger.error("Error occurred on retrieving Tasks", e);
			throw new Exception("Sorry an error occurred on retrieving Tasks, try again later");
		}
		return sub;
	}
	
	@Override
	public void setDegubTabularResource(boolean debug, String tabularResourceId){
		this.debugApplication = debug;
		this.debugTabularResource = tabularResourceId;	
	}


	@Override
	public List<TdJobModel> getListJobForTaskId(String taskId) throws Exception{

		//TODO
		return null;
	}
	

	@Override
	public TdTaskModel getTdTaskForId(String taskId) throws Exception{
		logger.trace("Get tabular data task for id: "+taskId);
		TaskTabularDataService service = getTabularDataServiceClient();
		
		TaskId operationId = new TaskId(taskId);
		
		TdTaskModel taskModel = new TdTaskModel();

		try {
			
			TabularResourceId trId = getCurrentTabularResource();
			Task task = service.getTask(operationId);
			taskModel = TdConverterBeanGWT.taskToTdTaskModel(task, trId, getTabularDataServiceClient(), getMapOperationDescription());
		
		} catch (NoSuchTaskException e) {
			logger.error("Error on recovering Task with id: "+operationId, e);
			throw new Exception("An error occurred on retrieving the Tabular Data Task");
		}
//		} catch (NoSuchTabularResourceException e) {
//			logger.error("Error on recovering Task with id: "+operationId, e);
//			throw new Exception("An error occurred on retrieving the Tabular Data Task");
//		}
		
		return taskModel;
	}



	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.td.taskswidget.client.rpc.TdTasksWidgetService#getTdServiceClientCapabilities()
	 */
	@Override
	public List<TdOperationModel> getTdServiceClientCapabilities() throws Exception {
		TaskTabularDataService service = getTabularDataServiceClient();
		
		if(service==null)
			throw new Exception("An error occurred on cantacting the Tabular Data service, try again later");

		List<TdOperationModel> listOperationModel = new ArrayList<TdOperationModel>();

		for (OperationDefinition  operationDefinition: service.getCapabilities()) {

			try {
				TdOperationModel operationModel = TdConverterBeanGWT.operationDefinitionToOperationModel(operationDefinition);
				listOperationModel.add(operationModel);
			} catch (TdConverterException e) {
				logger.error("Error on converting Operation Descriptor with id: "+operationDefinition.getOperationId(), e);
			}
		}
		
		return listOperationModel;
	}

}
