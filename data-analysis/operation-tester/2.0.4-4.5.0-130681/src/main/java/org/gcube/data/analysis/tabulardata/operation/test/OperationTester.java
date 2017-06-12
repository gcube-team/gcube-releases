package org.gcube.data.analysis.tabulardata.operation.test;

import java.util.Map;

import javax.inject.Inject;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.OperationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.OperationScope;
import org.gcube.data.analysis.tabulardata.operation.invocation.InvocationCreator;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerStatus;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ResourceDescriptorResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.remover.ResourceRemoverProvider;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public abstract class OperationTester<T extends WorkerFactory<?>> {

	static{
		Handler.activateProtocol();
	}
	
	FactoryChecker factoryChecker;

	WorkerChecker<?> workerChecker;
	
	WorkerChecker<RollbackWorker> rollbackWorkerChecker;

	@Inject
	private CubeManager cubeManager;

	@Before
	public void setupFactoryChecker() {
		factoryChecker = new FactoryChecker(getFactory());
	}

	protected abstract WorkerFactory<?> getFactory();

	@BeforeClass
	public static void beforeClass() {
		ScopeProvider.instance.set("/gcube/devsec");
	}

	private OperationInvocation createOperationInvocation() {
		OperationDescriptor descriptor = getFactory().getOperationDescriptor();
		InvocationCreator creator = InvocationCreator.getCreator(descriptor);
		creator.setParameters(getParameterInstances());
		OperationScope scope = descriptor.getScope();
		if (scope == OperationScope.TABLE || scope == OperationScope.COLUMN)
			creator.setTargetTable(getTargetTableId());
		if (scope == OperationScope.COLUMN)
			creator.setTargetColumn(getTargetColumnId());
		return creator.create();
	}

	@Test
	public final void testFactory() {
		factoryChecker.check();
	}

	protected abstract Map<String, Object> getParameterInstances();

	@Test
	public final void testInvocationDescription()throws InvalidInvocationException{
		OperationInvocation invocation = createOperationInvocation();
		System.out.println(getFactory().describeInvocation(invocation));
	}
	
	
	@Test
	public final void testWorker() throws InvalidInvocationException {
		OperationInvocation invocation = createOperationInvocation();
		workerChecker = new WorkerChecker(cubeManager, getFactory().createWorker(invocation));
		
		//check precondition
		if (!getFactory().getPrecoditionValidations().isEmpty())
			for (WorkerFactory<ValidationWorker> vw: getFactory().getPrecoditionValidations()){
				WorkerChecker<ValidationWorker> preconditionChecker = new WorkerChecker<ValidationWorker>(cubeManager, vw.createWorker(invocation));
				preconditionChecker.check();
				Assert.assertTrue(preconditionChecker.worker.getResult().isValid());
					
			}
		
		
		workerChecker.check();
		
		if (workerChecker.worker.getStatus()==WorkerStatus.FAILED)
			workerChecker.worker.getException().printStackTrace();
		
		Assert.assertEquals(WorkerStatus.SUCCEDED, workerChecker.worker.getStatus());
		
		
		if (getFactory().getWorkerType().equals(DataWorker.class) && getFactory().isRollbackable() ){
			DataWorker dataWorker = (DataWorker) workerChecker.worker;
			rollbackWorkerChecker = new WorkerChecker<RollbackWorker>(cubeManager, 
					getFactory().createRollbackWoker(dataWorker.getResult().getDiffTable(),
							dataWorker.getResult().getResultTable(), invocation));
			rollbackWorkerChecker.check();
					
			Assert.assertTrue(rollbackWorkerChecker.worker.getResult().getResultTable().sameStructureAs(cubeManager.getTable(invocation.getTargetTableId())));
			
		}else if (getFactory() instanceof ResourceRemoverProvider){
			try{
				for (ResourceDescriptorResult descr: ((ResourcesResult)workerChecker.worker.getResult()).getResources())
					((ResourceRemoverProvider)getFactory()).getResourceRemover().onRemove(descr.getResource());
				System.out.println("resource cleaned");
			}catch(Exception e){
				System.err.println("Error deleting resource "+e.getMessage());
			}
		}
		
	}
	

	protected boolean checkFallback(){
		return true;
	}

	protected abstract ColumnLocalId getTargetColumnId();

	protected abstract TableId getTargetTableId();

}
