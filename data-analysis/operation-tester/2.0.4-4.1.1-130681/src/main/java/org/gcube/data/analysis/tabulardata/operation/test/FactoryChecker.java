package org.gcube.data.analysis.tabulardata.operation.test;

import java.util.HashSet;
import java.util.Set;

import org.gcube.data.analysis.tabulardata.operation.OperationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Assert;

public class FactoryChecker {
	
	WorkerFactory<?> workerFactory;

	public FactoryChecker(WorkerFactory<?> workerFactory) {
		this.workerFactory = workerFactory;
	}
	
	public void check(){
		checkOperationDescriptor();
	}
	
	private void checkOperationDescriptor(){
		OperationDescriptor operationDescriptor = workerFactory.getOperationDescriptor();
		Assert.assertNotNull(operationDescriptor);
		Assert.assertNotNull(operationDescriptor.getName());
		Assert.assertNotNull(operationDescriptor.getDescription());
		Assert.assertNotNull(operationDescriptor.getOperationId());
		Assert.assertNotNull(operationDescriptor.getScope());
		Assert.assertNotNull(operationDescriptor.getType());
		Assert.assertNotNull(operationDescriptor.getParameters());
		Set<String> parameterIds=new HashSet<>();
		
		for(Parameter param:operationDescriptor.getParameters()){
			Assert.assertNotNull(param);
			String paramId=param.getIdentifier();
			Assert.assertNotNull(paramId);
			Assert.assertFalse(parameterIds.contains(paramId));
			parameterIds.add(paramId);
		}
	}
	
}
