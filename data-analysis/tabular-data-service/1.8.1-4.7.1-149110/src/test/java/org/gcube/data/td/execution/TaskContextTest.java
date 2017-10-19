package org.gcube.data.td.execution;

import java.util.Collections;
import java.util.HashMap;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.OnRowErrorAction;
import org.gcube.data.analysis.tabulardata.task.TaskContext;
import org.gcube.data.analysis.tabulardata.utils.InternalInvocation;
import org.gcube.data.td.unit.TestWorkerFactory;
import org.junit.Test;

public class TaskContextTest {

	@Test
	public void insertInvocation(){

		InternalInvocation ii = new InternalInvocation(new HashMap<String, Object>(), new TestWorkerFactory());
		System.out.println("prepared internalInvocation is "+ii.getInvocationId());

		InternalInvocation ni = new InternalInvocation(new HashMap<String, Object>(), new TestWorkerFactory());
		System.out.println("new internalInvocation is "+ni.getInvocationId());

		TaskContext tc = new TaskContext(Collections.singletonList(ii), OnRowErrorAction.ASK );

		boolean first = true;

		while (tc.hasNext()){
			tc.moveNext();
			System.out.println("executing "+tc.getCurrentInvocation().getInvocationId());

			if (first){
				tc.insertRecoveryInvocation(ni);
				tc.movePrevious();
				first= false;
			}

		}

	}


}
