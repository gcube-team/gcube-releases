package org.gcube.data.analysis.tabulardata.task.executor;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.enterprise.event.Event;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.gcube.data.analysis.tabulardata.cleaner.GarbageCollector;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.WorkerResult;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.RollbackTaskInfo;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo.TaskType;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.exceptions.TabularResourceLockedException;
import org.gcube.data.analysis.tabulardata.metadata.StorableHistoryStep;
import org.gcube.data.analysis.tabulardata.metadata.resources.StorableResource;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.ColumnId;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.RelationLink;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.StorableTabularResource;
import org.gcube.data.analysis.tabulardata.metadata.task.StorableTask;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.DatasetViewTableMetadata;
import org.gcube.data.analysis.tabulardata.model.relationship.TableRelationship;
import org.gcube.data.analysis.tabulardata.model.resources.TableResource;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.StatementContainer;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ResourceDescriptorResult;
import org.gcube.data.analysis.tabulardata.task.RunnableTask;
import org.gcube.data.analysis.tabulardata.task.TabularResourceDescriptor;
import org.gcube.data.analysis.tabulardata.task.TaskContext;
import org.gcube.data.analysis.tabulardata.task.executor.ExecutionHolder.ResourceHolder;
import org.gcube.data.analysis.tabulardata.task.executor.operation.creators.OperationWorkerCreator;
import org.gcube.data.analysis.tabulardata.task.executor.operation.creators.RollbackWorkerCreator;
import org.gcube.data.analysis.tabulardata.task.executor.operation.creators.WorkerCreator;
import org.gcube.data.analysis.tabulardata.utils.EntityManagerHelper;
import org.gcube.data.analysis.tabulardata.utils.OperationUtil;
import org.gcube.data.analysis.tabulardata.utils.ResourceCreated;
import org.gcube.data.analysis.tabulardata.utils.TableContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TaskWrapper implements Runnable{

	private EntityManager entityManager;
	private CubeManager cubeManager;
	private TaskContext taskContext;
	private StorableTabularResource tabularResource;
	private StorableTask task;
	private boolean resumedExecution;
	private GarbageCollector garbageCollector;
	private ExecutionHolder executionHolder = new ExecutionHolder();
	private Event<ResourceCreated> tableResourceEvent;

	private OperationUtil operationUtil;
	
	private TaskHandler taskHandler;

	private RunnableTask onSuccessEvent;

	private boolean parallelizableExecution = false;

	private boolean aborted;

	private static Logger logger = LoggerFactory.getLogger(TaskWrapper.class);

	private String threadGroupName = null;

	private DatabaseConnectionProvider connectionProvider;

	public TaskWrapper(EntityManagerHelper entityManagerHelper, CubeManager cubeManager,
			TaskContext taskContext, StorableTabularResource tabularResource,
			StorableTask task, GarbageCollector garbageCollector, 
			Event<ResourceCreated> tableResourceEvent,
			DatabaseConnectionProvider connectionProvider, OperationUtil operationUtil, boolean resumedExecution) throws TabularResourceLockedException{
		super();
		this.entityManager= entityManagerHelper.getEntityManager();
		this.cubeManager = cubeManager;
		this.taskContext = taskContext;
		this.tabularResource = tabularResource;
		this.task = task;
		this.resumedExecution = resumedExecution;
		this.garbageCollector = garbageCollector;
		this.tableResourceEvent = tableResourceEvent;
		this.parallelizableExecution = this.taskContext.isParallelizableExecution();
		this.connectionProvider = connectionProvider;
		this.operationUtil = operationUtil;
		checkAndlockTabularResource();
	}

	private void checkAndlockTabularResource(){
		tabularResource = this.entityManager.find(StorableTabularResource.class, tabularResource.getId());
		try{		
			this.entityManager.getTransaction().begin();

			logger.trace("tabularResource is locked? "+tabularResource.isLocked());

			if (tabularResource.isLocked()) throw new TabularResourceLockedException("tabular resource "+tabularResource.getName()+" is locked by another task");
			else{
				tabularResource.lock();
				this.entityManager.merge(tabularResource);
			}

			this.entityManager.getTransaction().commit();
		}catch (RollbackException re) {
			logger.error("error on transaction code",re );
			this.entityManager.close();
			throw re;
		}catch(DatabaseException de){
			logger.error("database error code is "+de.getDatabaseErrorCode(),de);
			this.entityManager.close();
			throw de;
		}catch (RuntimeException e) {
			logger.error("error on transaction code",e );
			this.entityManager.getTransaction().rollback();
			this.entityManager.close();
			throw e;
		}


	}

	public void abort(){
		if (taskHandler!=null)
			this.taskHandler.abort();
		Statement stmt = StatementContainer.get(this.threadGroupName);
		logger.info("searching for thread group "+this.threadGroupName+" and statement is null? "+ (stmt==null));
		try{
			if (stmt!=null && !stmt.isClosed())
				stmt.cancel();
		}catch(Exception e ){
			logger.warn("the query cannot be aborted",e);
		}
		this.aborted = true;
	}

	public TaskContext getTaskContext() {
		return taskContext;
	}

	public StorableTask getTask() {
		return task;
	}

	public void registerOnSuccessEvent(RunnableTask event){
		this.onSuccessEvent = event;
	}

	@Override
	public void run() {
		boolean tabularResourceValidity = tabularResource.isValid();

		this.threadGroupName = Thread.currentThread().getThreadGroup().getName();

		logger.info("saved thread group is "+ this.threadGroupName);

		TabularResourceDescriptor descriptor = new TabularResourceDescriptor(tabularResource.getName(), tabularResource.getVersion(), tabularResource.getId());

		try{

			WorkerCreator workerCreator = task.getTaskType()== TaskType.ROLLBACK? new RollbackWorkerCreator(): new OperationWorkerCreator();

			initialize();

			if (aborted) throw new OperationAbortedException();

			taskHandler = new TaskHandler(cubeManager, taskContext, workerCreator, descriptor, operationUtil, connectionProvider);

			tabularResourceValidity = taskHandler.run(executionHolder);

			if (aborted) throw new OperationAbortedException();

			tabularResource.setTableId(taskContext.getCurrentTable().getValue());

			Table table = cubeManager.getTable(taskContext.getCurrentTable()); 

			tabularResource.setTableType(table.getTableType().getName());

			if (this.parallelizableExecution)
				updateReferencedTabularResource();

			onSuccess(taskHandler.isStopped());

		}catch(WorkerException we){
			logger.error("error executing operation", we);
			onError(we);
		}catch (OperationAbortedException e) {
			logger.warn("operation aborted");
			onAbort();
		}catch (Throwable tb) {
			logger.error("unexpected error", tb);
			onError(new WorkerException("unexpected error executing operation", tb));
		}

		try{
			persistTabularResourceOnTaskFinished(tabularResourceValidity);
		}finally{

			try{		
				this.entityManager.getTransaction().begin();
				tabularResource.unlock();
				this.entityManager.merge(tabularResource);
				this.entityManager.getTransaction().commit();
			}catch (RollbackException re) {
				logger.error("error on transaction code",re );
				throw re;
			}catch(DatabaseException de){
				logger.error("database error code is "+de.getDatabaseErrorCode(),de);
				throw de;
			}catch (RuntimeException e) {
				logger.error("error on transaction code",e );
				this.entityManager.getTransaction().rollback();
			}
			this.entityManager.close();
			StatementContainer.reset();
			TableContainer.reset();
		}

	}


	private void initialize() throws WorkerException{
		initializing();
		TaskInfo taskInfo = this.task.getStoredTask();
		taskInfo.setStartTime(Calendar.getInstance());
		this.task.setStoredTask(taskInfo);
		try{
			storeEntities();
			tableInitializer();
		}catch(Exception we){
			throw new WorkerException("erorr initializing task", we);
		}
		inProgress();
	}


	private void tableInitializer() throws Exception{
		TableId startingTableId = null;
		if (tabularResource.getTableId()!=null)
			startingTableId = new TableId(tabularResource.getTableId());
		taskContext.setStartingTable(startingTableId);
		//if (!taskContext.hasNext()) throw new Exception("no operation to execute set");
		taskContext.setCurrentTable(startingTableId);
	}

	/*private TableId cloneStartingTable(TableId startingTableId) {
		if (startingTableId==null) return null;
		Table startingTable = cubeManager.getTable(startingTableId);
		return cubeManager.createTable(startingTable.getTableType()).like(startingTable, true).create().getId();
	}*/


	private void storeEntities() throws Exception{

		try{		
			this.entityManager.getTransaction().begin();
			tabularResource = this.entityManager.merge(tabularResource);

			this.entityManager.persist(taskContext);
			if (!resumedExecution){
				this.entityManager.persist(task);
				tabularResource.addTask(task);
			}
			this.entityManager.merge(tabularResource);
			this.entityManager.getTransaction().commit();
		}catch (RollbackException re) {
			logger.error("error on transaction code",re );
			throw re;
		}catch(DatabaseException de){
			logger.error("database error code is "+de.getDatabaseErrorCode(),de);
			throw de;
		}catch (RuntimeException e) {
			logger.error("error on transaction code",e );
			this.entityManager.getTransaction().rollback();
		}
	}

	private void updateReferencedTabularResource(){

		final Table table = cubeManager.getTable(taskContext.getCurrentTable());

		try{		
			this.entityManager.getTransaction().begin();
			List<RelationLink> links = new ArrayList<RelationLink>();
			for (TableRelationship relationship : table.getCodelistRelationships()){
				Table externalReferenceTable = cubeManager.getTable(relationship.getTargetTableId());
				if (externalReferenceTable.contains(TableDescriptorMetadata.class)){
					StorableTabularResource externalTR = this.entityManager.find(StorableTabularResource.class, externalReferenceTable.getMetadata(TableDescriptorMetadata.class).getRefId());
					RelationLink relationLink = this.entityManager.find(RelationLink.class, new ColumnId(tabularResource.getId(), relationship.getTargetColumnId().getValue()));
					if (relationLink!=null){
						relationLink.setLinksToTabularResource(externalTR);
						//logger.debug("modifying existing link: "+relationLink);
						this.entityManager.merge(relationLink);
					} else {
						relationLink = new RelationLink(tabularResource, relationship.getTargetColumnId().getValue(), externalTR);
						logger.info("linked is null?? "+(relationLink.getLinkedTabularResource()==null));
						logger.info("links to is null?? "+(relationLink.getLinksToTabulaResource()==null));
						logger.info("column ID?? "+(relationLink.getColumnLocalId()==null));
						this.entityManager.persist(relationLink);
					}
					links.add(relationLink);	
				}else {
					logger.warn("no table descriptor metadata found for table "+externalReferenceTable.getId());
					continue;
				}
			}
			for (RelationLink oldRl: tabularResource.getLinksTo())
				if (!links.contains(oldRl))
					this.entityManager.remove(oldRl);

			tabularResource.setLinksTo(links);
			logger.trace("links to set: "+links);
			this.entityManager.merge(tabularResource);
			this.entityManager.getTransaction().commit();
		}catch (RollbackException re) {
			logger.error("error on transaction code",re );
			throw re;
		}catch(DatabaseException de){
			logger.error("database error code is "+de.getDatabaseErrorCode(),de);
			throw de;
		}catch (RuntimeException e) {
			logger.error("error on transaction code",e );
			this.entityManager.getTransaction().rollback();
		}
	}

	private void failureProcedure(){
		if (TableContainer.get(this.threadGroupName)!=null)
			garbageCollector.addTablesToRemove(TableContainer.get(this.threadGroupName));
		tabularResource.setTableId(taskContext.getStartingTable()!=null?taskContext.getStartingTable().getValue():null);
	}

	void onError(WorkerException we){
		failureProcedure();
		failed(we);
	}

	void onAbort(){
		failureProcedure();
		aborted();
	}

	void onSuccess(boolean stopped){
		historyModifications(executionHolder.getStepsToAddOnSuccess());
		garbageCollector.addTablesToRemove(executionHolder.getToRemoveOnFinish());
		//TODO addResources(executionHolder.getCreatedResources());
		assignReadOnlyRightOnDB(taskContext.getCurrentTable());
		if (!stopped){
			Table table = cubeManager.removeValidations(taskContext.getCurrentTable());
			if (table.contains(DatasetViewTableMetadata.class))
				cubeManager.removeValidations(((DatasetViewTableMetadata)table.getMetadata(DatasetViewTableMetadata.class)).getTargetDatasetViewTableId());
			success(table, createCollateralResource(executionHolder.getCreatedResources()));
		}
		else stopped(cubeManager.getTable(taskContext.getCurrentTable()));
	}

	private void assignReadOnlyRightOnDB(TableId currentTableId) {
		Table table = cubeManager.getTable(currentTableId);
		try{
			SQLHelper.executeSQLCommand("GRANT SELECT ON TABLE "+table.getName()+" TO readonly", connectionProvider);	
			if (table.contains(DatasetViewTableMetadata.class)){
				TableId viewId = ((DatasetViewTableMetadata)table.getMetadata(DatasetViewTableMetadata.class)).getTargetDatasetViewTableId();
				Table view = cubeManager.getTable(viewId);
				SQLHelper.executeSQLCommand("GRANT SELECT ON TABLE "+view.getName()+" TO readonly", connectionProvider);	
			}
		}catch(Exception e){
			logger.error("error assigning right to readonly for table "+currentTableId);
		}
	}

	private List<TableId> createCollateralResource(List<ResourceHolder> createdResources) {
		int index =0;

		List<TableId> collateralTablesCreated = new ArrayList<>();
		for (ResourceHolder res : createdResources)
			if (res.getResourceDescriptor().getResource().getResourceType().equals(TableResource.class) ){
				ResourceDescriptorResult resourceDescriptor = res.getResourceDescriptor();
				String resourceName = resourceDescriptor.getName()==null ? tabularResource.getName()+"(collateral-"+index+")":
					resourceDescriptor.getName();
				tableResourceEvent.fire(new ResourceCreated((TableResource)resourceDescriptor.getResource(), 
						resourceName, this.task.getStoredTask().getSubmitter()));
				collateralTablesCreated.add(((TableResource)resourceDescriptor.getResource()).getTableId());
			} else {
				//TODO : change resourceType
				final StorableResource sr = new StorableResource(res.getResourceDescriptor().getName(), res.getResourceDescriptor().getDescription(),
						res.getResourceDescriptor().getResourceType(), res.getCreatorId(), res.getResourceDescriptor().getResource());
				tabularResource.addResource(sr);
				sr.setTabularResource(tabularResource);
				try{		
					this.entityManager.getTransaction().begin();
					this.entityManager.persist(sr);
					this.entityManager.merge(tabularResource);
					this.entityManager.getTransaction().commit();
				}catch (RollbackException re) {
					logger.error("error on transaction code",re );
					throw re;
				}catch(DatabaseException de){
					logger.error("database error code is "+de.getDatabaseErrorCode(),de);
					throw de;
				}catch (RuntimeException e) {
					logger.error("error on transaction code",e );
					this.entityManager.getTransaction().rollback();
				}
			}
		if (collateralTablesCreated.size()>0)
			logger.info(this.taskContext.getCurrentInvocation().getWorkerFactory().getOperationDescriptor().getName()+" has generated "+collateralTablesCreated);
		return collateralTablesCreated;
	}

	private void historyModifications(final List<StorableHistoryStep> historyStepsToAdd){
		logger.debug("history steps to add are "+historyStepsToAdd.size());
		if (task.getStoredTask().getType()== TaskType.ROLLBACK){
			RollbackTaskInfo rollbackTask = (RollbackTaskInfo) task.getStoredTask();
			for(final Long stepId: rollbackTask.getHistoryStepsToRemove()){
				StorableHistoryStep step = this.entityManager.find(StorableHistoryStep.class, stepId);
				step.getTabularResources().remove(tabularResource);
				tabularResource.removeHistoryStep(step);
			}
		}else{
			try{		
				this.entityManager.getTransaction().begin();
				for (final StorableHistoryStep step: historyStepsToAdd){
					logger.debug("adding history "+step);
					step.addTabularResource(tabularResource);
					this.entityManager.persist(step);
				}
				tabularResource.addHistorySteps(historyStepsToAdd);
				this.entityManager.merge(tabularResource);
				this.entityManager.getTransaction().commit();
			}catch (RollbackException re) {
				logger.error("error on transaction code",re );
				throw re;
			}catch(DatabaseException de){
				logger.error("database error code is "+de.getDatabaseErrorCode(),de);
				throw de;
			}catch (RuntimeException e) {
				logger.error("error on transaction code",e );
				this.entityManager.getTransaction().rollback();
			}

		}

	}

	/*	TODO private void addResources(List<ResourceHolder> createdResources) throws WorkerException {
		for (ResourceHolder resource : createdResources){
			tabularResource.addResource(new StorableResource(resource.getResourceDescriptor().getDescription(), 
					resource.getResourceDescriptor().getResource().getResourceType()
					, resource.getCreatorId(), resource.getResourceDescriptor().getResource()));	

			//temporary code (until resource are not managed at high level)
			if (resource.getResourceDescriptor().getResource().getResourceType() == ResourceType.TABLE){
				TableResource tRes = (TableResource) resource.getResourceDescriptor().getResource();
				//TODO createTabularResourceFromResource(tRes, resource.getResourceDescriptor().getDescription());
			}
		}
	}*/

	private void persistTabularResourceOnTaskFinished(boolean tabularResourceValidity){

		tabularResource.setValid(tabularResourceValidity);
		try{		
			this.entityManager.getTransaction().begin();
			this.entityManager.merge(tabularResource);
			this.entityManager.merge(taskContext);
			this.entityManager.merge(task);
			this.entityManager.getTransaction().commit();
		}catch (RollbackException re) {
			logger.error("error on transaction code",re );
			throw re;
		}catch(DatabaseException de){
			logger.error("database error code is "+de.getDatabaseErrorCode(),de);
			throw de;
		}catch (RuntimeException e) {
			logger.error("error on transaction code",e );
			this.entityManager.getTransaction().rollback();
		}
	}

	private void failed(Throwable cause){
		logger.error("error executing task",cause);
		TaskInfo info = task.getStoredTask();
		info.setEndTime(Calendar.getInstance());
		info.setStatus(TaskStatus.FAILED);
		Exception exc = new Exception(cause.getClass().getSimpleName()+": "+cause.getMessage());
		exc.setStackTrace(cause.getStackTrace());
		info.setErrorCause(exc);
		task.setStoredTask(info);
	}

	private void stopped(Table table){
		logger.error("stopping execution");
		TaskInfo info = task.getStoredTask();
		info.setEndTime(Calendar.getInstance());
		info.setStatus(TaskStatus.STOPPED);
		info.setResult(new org.gcube.data.analysis.tabulardata.commons.webservice.types.WorkerResult(table));
		task.setStoredTask(info);
	}

	private void aborted(){
		logger.error("task aborted");
		TaskInfo info = task.getStoredTask();
		info.setEndTime(Calendar.getInstance());
		info.setStatus(TaskStatus.ABORTED);
		task.setStoredTask(info);
	}

	private void success(Table result, List<TableId> collateralTables ){
		TaskInfo info = task.getStoredTask();
		WorkerResult res = new WorkerResult(result, collateralTables);
		logger.info("collateral resource in the Result are "+res.getCollateralTables());
		info.setResult(res);
		info.setEndTime(Calendar.getInstance());
		try{
			if (this.onSuccessEvent!=null)
				this.onSuccessEvent.run(result);
		}catch(Exception e){
			logger.warn("on success event not executed");
		}
		info.setStatus(TaskStatus.SUCCEDED);
		task.setStoredTask(info);
	}

	private void inProgress(){
		TaskInfo info = task.getStoredTask();
		info.setStatus(TaskStatus.IN_PROGRESS);
		task.setStoredTask(info);
	}


	private void initializing(){
		TaskInfo info = task.getStoredTask();
		info.setStatus(TaskStatus.INITIALIZING);
		task.setStoredTask(info);
	}

}
