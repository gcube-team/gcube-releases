package org.gcube.data.analysis.tabulardata.task.executor;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskStep;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.ValidationDescriptor;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.CountMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.DatasetViewTableMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.task.TabularResourceDescriptor;
import org.gcube.data.analysis.tabulardata.task.TaskContext;
import org.gcube.data.analysis.tabulardata.task.executor.operation.OperationContext;
import org.gcube.data.analysis.tabulardata.task.executor.operation.OperationHandler;
import org.gcube.data.analysis.tabulardata.task.executor.operation.creators.OperationWorkerCreator;
import org.gcube.data.analysis.tabulardata.task.executor.operation.creators.WorkerCreator;
import org.gcube.data.analysis.tabulardata.task.executor.operation.listener.ExecutionListener;
import org.gcube.data.analysis.tabulardata.task.executor.operation.listener.PostOperationListener;
import org.gcube.data.analysis.tabulardata.utils.InternalInvocation;
import org.gcube.data.analysis.tabulardata.utils.OperationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskHandler implements ExecutionListener{

	private static Logger logger = LoggerFactory.getLogger(TaskHandler.class);

	private CubeManager cubeManager;

	private TaskContext context;

	private WorkerCreator workerCreator;

	private boolean stopped = false;

	private boolean aborted = false ;

	private TabularResourceDescriptor descriptor;

	private OperationHandler opHandler;

	private OperationUtil opUtil;

	private DatabaseConnectionProvider connProvider;

	public TaskHandler(CubeManager cubeManager,
			TaskContext context, WorkerCreator workerCreator,
			TabularResourceDescriptor descriptor, OperationUtil opUtil, DatabaseConnectionProvider connProvider) {
		super();
		this.cubeManager = cubeManager;
		this.context = context;
		this.workerCreator = workerCreator;
		this.descriptor = descriptor;
		this.opUtil = opUtil;
		this.connProvider = connProvider;
	}

	public boolean isStopped() {
		return stopped;
	}

	public boolean run(ExecutionHolder executionHolder) throws WorkerException, OperationAbortedException{
		if (context.getCurrentTable()!=null)
			this.cubeManager.removeValidations(context.getCurrentTable());

		while (!aborted && !stopped && context.hasNext()){
			context.moveNext();

			OperationContext operationContext = createExecutionContext(context.getCurrentInvocation(), context.getCurrentTask());

			opHandler = new OperationHandler(operationContext, workerCreator, this);

			TableId tableId = opHandler.run(executionHolder, context.getCurrentInvocation());

			context.setCurrentTable(tableId);
		}

		if (aborted) throw new OperationAbortedException();

		cubeManager.modifyTableMeta(context.getCurrentTable())
		.setTableMetadata(new TableDescriptorMetadata(descriptor.getName(), descriptor.getVersion(), descriptor.getRefId())).create();

		boolean valid = true;

		if (!context.isParallelizableExecution()){
			valid = executePostValidations();
			executePostOperations();			
		}

		setCountMetadata(context.getCurrentTable());

		return valid;
	}

	private void setCountMetadata(TableId currentTableId) {
		//caching the table count (if not already set by operations), not storing for view (already done by View operation)
		Table currentTable = cubeManager.getTable(currentTableId);
		if (!currentTable.contains(CountMetadata.class) && !currentTable.contains(DatasetViewTableMetadata.class)){
			int count;
			try {
				count = SQLHelper.getCount(this.connProvider, currentTable.getName(), null);
			} catch (Exception e) {
				logger.error("error getting count for table with id {}",context.getCurrentTable().getValue() );
				throw new RuntimeException(e);
			}
			cubeManager.modifyTableMeta(currentTableId)
			.setTableMetadata(new CountMetadata(count)).create();
		}
	}

	/**
	 * returns tabular resource validity
	 * 
	 * @return boolean 
	 */
	private void executePostOperations() throws WorkerException, OperationAbortedException{
		if (!context.getPostOperations().isEmpty()){
			ExecutionHolder executionHolder = new ExecutionHolder();
			PostOperationListener listener = new PostOperationListener(this);

			WorkerCreator operationWorkerCreator = new OperationWorkerCreator();

			for (int index = 0; index<context.getPostOperations().size(); index++){
				InternalInvocation invocation = context.getPostOperations().get(index);

				OperationContext operationContext = createExecutionContext(invocation, 
						context.getPostOperationTasks().get(index));
				OperationHandler operationHandler = new OperationHandler(operationContext, operationWorkerCreator, listener );

				TableId tableId = operationHandler.run(executionHolder, invocation);

				context.setCurrentTable(tableId);

			}

		}
	}

	/**
	 * returns tabular resource validity
	 * 
	 * @return boolean 
	 */
	private boolean executePostValidations() throws WorkerException, OperationAbortedException{

		logger.trace("executing "+context.getPostValidations().size()+" post validations");

		if (!context.getPostValidations().isEmpty()){

			ExecutionHolder executionHolder = new ExecutionHolder();
			PostOperationListener listener = new PostOperationListener(this);
			WorkerCreator operationWorkerCreator = new OperationWorkerCreator();

			for (int index = 0; index<context.getPostValidations().size(); index++){
				InternalInvocation invocation = context.getPostValidations().get(index);
				logger.trace("executing postValidation "+invocation.getWorkerFactory().getOperationDescriptor().getName());
				OperationContext operationContext = createExecutionContext(invocation , 
						context.getPostValidationTasks().get(index));
				OperationHandler operationHandler = new OperationHandler(operationContext, operationWorkerCreator, listener );

				operationHandler.run(executionHolder, invocation);

			}
			return listener.isValid();
		} else
			return true;
	}

	private OperationContext createExecutionContext(InternalInvocation invocation, TaskStep step) {
		WorkerFactory<?> factory = invocation.getWorkerFactory();
		OperationContext operationContext = new OperationContext(context.getCurrentTable(), context.getStartingTable(), factory, step, cubeManager);
		return operationContext;
	}


	@Override
	//check if return can be changed with void
	public boolean onStop(OperationContext opContext) {
		List<ColumnLocalId> validationColumns;
		switch (context.getBehaviour()) {
		case DISCARD:
			logger.debug("DISCARDING invalid rows");
			//total execution i snot stopped
			validationColumns = getValidationColumnsForOperation(opContext);
			if(validationColumns.isEmpty()) {
				stopped = true;
			} else{
				stopped= false;
				context.cleanValidationsOnCurrentStep();
				context.insertRecoveryInvocation(opUtil.getRemoveInvalidEntryInvocation(opContext.getCurrentTable(), validationColumns));
				context.movePrevious();
			}
			//precondition execution has to freeze to restart with row discarding
			return false;
		case SAVE:
			logger.debug("SAVING invalid rows");
			validationColumns = getValidationColumnsForOperation(opContext);
			if(validationColumns.isEmpty()) {
				stopped = true;
			} else{
				stopped= false;
				context.cleanValidationsOnCurrentStep();
				context.insertRecoveryInvocation(opUtil.getRemoveInvalidEntryInvocation(opContext.getCurrentTable(), validationColumns));
				context.movePrevious();
			}
			return false;
		default:
			logger.debug("STOPPING after invalid rows");
			stopped = true;
			return false;
		}
	}

	private List<ColumnLocalId> getValidationColumnsForOperation(
			OperationContext opContext) {
		List<ColumnLocalId> columns = new ArrayList<>();
		for (ValidationDescriptor descriptor : opContext.getTaskStep().getValidations())
			if (descriptor.getValidationColumn()!=null)
				columns.add(new ColumnLocalId(descriptor.getValidationColumn()));
		return columns;
	}

	public void abort(){
		if (opHandler!=null)
			opHandler.getWorkerExecutor().abort();
		this.aborted= true;
	}

}
