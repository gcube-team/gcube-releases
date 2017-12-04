package org.gcube.data.analysis.tabulardata.service;

import static org.gcube.data.analysis.tabulardata.utils.Util.getUserAuthorizedObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.persistence.EntityManager;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.data.analysis.tabulardata.commons.utils.Constants;
import org.gcube.data.analysis.tabulardata.commons.webservice.OperationManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.ExecutionFailedException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.HistoryNotFoundException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.OperationNotFoundException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.BatchExecuteRequest;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.BatchOption;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.ExecuteRequest;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.OnRowErrorAction;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResourceType;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.exceptions.NoSuchObjectException;
import org.gcube.data.analysis.tabulardata.exceptions.TabularResourceLockedException;
import org.gcube.data.analysis.tabulardata.metadata.StorableHistoryStep;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.StorableTabularResource;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.OperationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.invocation.InvocationCreator;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerStatus;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.MetadataWorker;
import org.gcube.data.analysis.tabulardata.task.RunnableTask;
import org.gcube.data.analysis.tabulardata.task.TaskContext;
import org.gcube.data.analysis.tabulardata.task.engine.TaskEngine;
import org.gcube.data.analysis.tabulardata.utils.EntityManagerHelper;
import org.gcube.data.analysis.tabulardata.utils.Factories;
import org.gcube.data.analysis.tabulardata.utils.InternalInvocation;
import org.gcube.data.analysis.tabulardata.utils.OperationUtil;
import org.gcube.data.analysis.tabulardata.weld.WeldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@WebService(portName = "OperationManagerPort",
serviceName = OperationManager.SERVICE_NAME,
targetNamespace = Constants.OPERATION_TNS,
endpointInterface = "org.gcube.data.analysis.tabulardata.commons.webservice.OperationManager")
@Singleton
@WeldService
public class OperationManagerImpl implements OperationManager {

	private static final long CLONE_OPERATION_ID = 102;
	
	private Logger logger = LoggerFactory.getLogger(OperationManagerImpl.class);

	@Inject
	private CubeManager cubeManager;

	@Inject
	private Factories factories;

	@Inject
	private EntityManagerHelper emHelper;
	
	@Inject
	private OperationUtil opUtil;

	@Inject
	private TaskEngine taskEngine;

	@PostConstruct
	void initService(){
		logger.info("executing POST CONSTRUCT");
		Handler.activateProtocol();
	}
	
	@Override
	public List<OperationDefinition> getCapabilities() {
		logger.info("calling getCapabilities in scope {}",ScopeProvider.instance.get());
		List<OperationDefinition> definitions = new ArrayList<OperationDefinition>();
		for (WorkerFactory<?> factory: factories.values()){
			OperationDescriptor descr = factory.getOperationDescriptor();
			definitions.add(new OperationDefinition(descr.getOperationId().getValue(), descr.getName(), descr.getDescription(), descr.getParameters()));
		}
		return definitions;
	}

	@Override
	public OperationDefinition getOperationDescriptor(long operationId) throws OperationNotFoundException {
		logger.info("calling getCapabilities in scope {}",ScopeProvider.instance.get());
		if (!factories.containsKey(new OperationId(operationId))) throw new OperationNotFoundException("operation with id "+operationId+"not found");		
		else {
			OperationDescriptor descr = factories.get(new OperationId(operationId)).getOperationDescriptor();
			return new OperationDefinition(descr.getOperationId().getValue(), descr.getName(), descr.getDescription(), descr.getParameters());
		}
	}


	@Override
	public TaskInfo execute(ExecuteRequest request)
			throws NoSuchTabularResourceException,
			OperationNotFoundException, InternalSecurityException {
		logger.info("calling execute invocations {} on TR wit id {}",request.getInvocation(), request.getTargetTabularResourceId() );
		EntityManager entityManager = emHelper.getEntityManager();
		StorableTabularResource tabularResource;
		try{
			tabularResource = getUserAuthorizedObject(request.getTargetTabularResourceId(), StorableTabularResource.class, entityManager);
		}catch(NoSuchObjectException e){
			entityManager.close();
			throw new NoSuchTabularResourceException(request.getTargetTabularResourceId());
		}

		try{
			return execute(Collections.singletonList(request.getInvocation()), BatchOption.NONE, tabularResource, false, entityManager);
		}catch(TabularResourceLockedException ise){
			logger.error("illegal state executing operation ", ise);
			throw new InternalSecurityException(ise.getCause());
		}finally{
			entityManager.close();
		}
	}

	@Override
	public TaskInfo removeValidations(Long tabularResourceId)
			throws NoSuchTabularResourceException, InternalSecurityException {
		logger.info("calling remove validations on TR wit id {}",tabularResourceId );
		EntityManager entityManager = emHelper.getEntityManager();
		StorableTabularResource tabularResource;
		try{
			tabularResource = getUserAuthorizedObject(tabularResourceId, StorableTabularResource.class, entityManager);
		}catch(NoSuchObjectException e){
			entityManager.close();
			throw new NoSuchTabularResourceException(tabularResourceId);
		}

		try{
			return execute(new ArrayList<OperationExecution>(0), BatchOption.NONE, tabularResource, true, entityManager );
		}catch(TabularResourceLockedException | OperationNotFoundException ise){
			logger.error("illegal state executing operation ", ise);
			throw new InternalSecurityException(ise.getCause());
		}finally{
			entityManager.close();
		}
	}
	
	@Override
	@SOAPBinding(parameterStyle = ParameterStyle.WRAPPED)
	public TaskInfo batchExecute(BatchExecuteRequest request)
			throws NoSuchTabularResourceException, 
			OperationNotFoundException, InternalSecurityException{

		EntityManager entityManager = emHelper.getEntityManager();
		logger.info("calling batch execute invocations {} on TR wit id {}",request.getInvocations(), request.getTargetTabularResourceId() );
		StorableTabularResource tabularResource;
		try{
			tabularResource = getUserAuthorizedObject(request.getTargetTabularResourceId(), StorableTabularResource.class, entityManager);
		}catch(NoSuchObjectException e){
			entityManager.close();
			throw new NoSuchTabularResourceException(request.getTargetTabularResourceId());
		}
		try{
			return execute(request.getInvocations(), request.getOption(), tabularResource, false,entityManager);
		}catch(TabularResourceLockedException ise){
			logger.error("illegal state executing operation ", ise);
			throw new InternalSecurityException(ise.getCause());
		}finally{
			entityManager.close();
		}

	}

	public TaskInfo execute(List<OperationExecution> invocations, BatchOption option, StorableTabularResource tabularResource, boolean internal, EntityManager entityManager) throws OperationNotFoundException, InternalSecurityException{
		return execute(invocations, option, tabularResource, internal, entityManager, null);
	}
	
	public TaskInfo execute(List<OperationExecution> invocations, BatchOption option, StorableTabularResource tabularResource, boolean internal, EntityManager entityManager, RunnableTask onSuccess) throws OperationNotFoundException, InternalSecurityException{

		logger.debug("tabular resource "+tabularResource.getId()+" is locked? "+tabularResource.isLocked());

		if (!internal && tabularResource.getTabularResourceType()==TabularResourceType.FLOW &&
				!(invocations.size()==1 && invocations.get(0).getOperationId()==CLONE_OPERATION_ID))
			throw new InternalSecurityException("the operation cannot be executed on FLOWs");


		long start = System.currentTimeMillis();

		List<InternalInvocation> invocationCouples = new ArrayList<InternalInvocation>(invocations.size());
		for (OperationExecution invocation: invocations){

			InternalInvocation invocationCouple = opUtil.getInvocationById(invocation.getOperationId(), invocation.getParameters());
			if (invocation.getColumnId()!=null)
				invocationCouple.setColumnId(new ColumnLocalId(invocation.getColumnId()));
			invocationCouples.add(invocationCouple);
		}

		TaskContext context = new TaskContext(invocationCouples,OnRowErrorAction.ASK);

		opUtil.addPostValidations(context, tabularResource);
		opUtil.addPostOperations(context);


		if (tabularResource.isFinalized() && !context.isParallelizableExecution()) throw new RuntimeException("cannot execute data operation on finalized tabular resource");
		String caller = AuthorizationProvider.instance.get().getClient().getId();
		TaskInfo toReturn = taskEngine.createTask(caller, context, tabularResource, onSuccess);
		logger.trace("execute took "+((System.currentTimeMillis()-start)));
		return toReturn;
	}


	@Override
	public TaskInfo rollbackTo(long tabularRessourceId, long historyStepId)
			throws HistoryNotFoundException, NoSuchTabularResourceException, OperationNotFoundException, InternalSecurityException {
		StorableTabularResource sTr;
		EntityManager entityManager = emHelper.getEntityManager();
		try{
			sTr = getUserAuthorizedObject(tabularRessourceId,  StorableTabularResource.class, entityManager);
		} catch(NoSuchObjectException e){
			throw new NoSuchTabularResourceException(tabularRessourceId);
		}
		if (sTr.isFinalized()) throw new RuntimeException("cannot execute operation on finalized tabular resource");

		StorableHistoryStep sThs;
		try{
			sThs = entityManager.find(StorableHistoryStep.class, historyStepId );
		} catch(Exception e){
			throw new HistoryNotFoundException(e);
		}
		if (!sTr.getHistorySteps().contains(sThs)) 
			throw new HistoryNotFoundException("history with id "+historyStepId+"not found");

		int indexOfHistoryStep = sTr.getHistorySteps().indexOf(sThs);

		List<Long> historyStepIdToremoveOnRollback = getHistoryStepToRemoveOnRollback(sTr.getHistorySteps(), indexOfHistoryStep);
		TaskContext context = new TaskContext(getRollbackInvocations(sTr.getHistorySteps(), indexOfHistoryStep),OnRowErrorAction.ASK);
		if (!context.isParallelizableExecution()){
			opUtil.addPostValidations(context, sTr);
			opUtil.addPostOperations(context);
		}
		
		String submitter = AuthorizationProvider.instance.get().getClient().getId(); 
		TaskInfo toReturn = taskEngine.createRollbackTask(submitter, context, sTr, historyStepIdToremoveOnRollback);
		entityManager.close();
		return toReturn;
	}

	@Override
	@SOAPBinding(parameterStyle = ParameterStyle.WRAPPED)
	public void executeSynchMetadataOperation(ExecuteRequest request)
			throws NoSuchTabularResourceException, OperationNotFoundException,
			InternalSecurityException, ExecutionFailedException {

		logger.info("executing synch operation {} on tabular resource {}", request.getInvocation(), request.getTargetTabularResourceId());
		
		StorableTabularResource sTr;
		EntityManager entityManager = emHelper.getEntityManager();
		try{
			sTr = getUserAuthorizedObject(request.getTargetTabularResourceId(),  StorableTabularResource.class, entityManager);
		} catch(NoSuchObjectException e){
			logger.error("no such tabular resource ",e);
			throw new NoSuchTabularResourceException(request.getTargetTabularResourceId());
		}

		OperationId opId = new OperationId(request.getInvocation().getOperationId());

		if (!factories.containsKey(opId)){
			logger.error("operation with id {} not found ",opId);
			throw new OperationNotFoundException("operation with id "+opId+" not found");
		}
		WorkerFactory<?> factory = factories.get(opId);
		if (!(factory.getWorkerType() == MetadataWorker.class)){
			logger.error("invalid worker type ");
			throw new OperationNotFoundException("the operation with id "+opId+" is not a Metadata operation");
		}

		TableId table = new TableId(sTr.getTableId());
		
		InvocationCreator creator = InvocationCreator.getCreator(factory.getOperationDescriptor()).setTargetTable(table);
		if (request.getInvocation().getColumnId()!=null)
			creator.setTargetColumn(new ColumnLocalId(request.getInvocation().getColumnId()));
		creator.setParameters(request.getInvocation().getParameters());		

		Worker<?> worker;
		try{
			worker = factory.createWorker(creator.create());
			worker.run();
		} catch (InvalidInvocationException e) {
			logger.error("error executing synch operation ",e);
			throw new ExecutionFailedException("invalid invocation for metadata operation "+opId+" ("+e.getMessage()+")");
		} catch (Exception e) {
			logger.error("error executing synch operation ",e);
			throw new ExecutionFailedException("unexpected exception for metadata operation "+opId);
		}
		if (worker.getStatus()==WorkerStatus.FAILED){
			logger.error("error executing synch operation ",worker.getException());
			throw new ExecutionFailedException("error executing metadata worker ("+worker.getException().getMessage()+")");
		}
	}

	private List<InternalInvocation> getRollbackInvocations(List<StorableHistoryStep> historySteps, int indexOfHistoryStep) throws HistoryNotFoundException, OperationNotFoundException{
		List<InternalInvocation> invocationCouples = new ArrayList<InternalInvocation>();
		for (int i = historySteps.size()-1; i>indexOfHistoryStep; i-- ){
			StorableHistoryStep step = historySteps.get(i);
			if (step.isContainsDiff()){
				WorkerFactory<?> factory = factories.get(new OperationId(step.getOperationInvocation().getOperationId()));
				if (factory==null){
					logger.error("operation with id {} not found", step.getOperationInvocation().getOperationId());
					throw new OperationNotFoundException("operation with id "+step.getOperationInvocation().getOperationId()+" not found");
				}
				try{
					InternalInvocation invocationCouple = new InternalInvocation(step.getOperationInvocation().getParameters(), 
							factory,  cubeManager.getTable(new TableId(step.getTableId())));
					if (step.getOperationInvocation().getColumnId()!=null)
						invocationCouple.setColumnId(new ColumnLocalId(step.getOperationInvocation().getColumnId()));
					invocationCouples.add(invocationCouple); 
				}catch(Exception e){
					logger.warn("error retrieving history step for rollbackable operation",e);
					throw new HistoryNotFoundException(e);
				}
			} else{
				try{
					invocationCouples = new ArrayList<InternalInvocation>();
					invocationCouples.add(new InternalInvocation.NOPInvocation(cubeManager.getTable(new TableId(step.getTableId()))));
				}catch(Exception e){
					logger.warn("error retrieving history step for not rollbackable operation",e);
					throw new HistoryNotFoundException(e);
				}
			}

		}
		return invocationCouples;

	}
	
	private List<Long> getHistoryStepToRemoveOnRollback(List<StorableHistoryStep> historySteps, int indexOfHistoryStep){
		List<Long> historyStepToRemove = new ArrayList<Long>();		
		for (int i = historySteps.size()-1; i>indexOfHistoryStep; i-- ){
			StorableHistoryStep step = historySteps.get(i);
			historyStepToRemove.add(step.getId());
		}
		return historyStepToRemove;
	}
	
}





