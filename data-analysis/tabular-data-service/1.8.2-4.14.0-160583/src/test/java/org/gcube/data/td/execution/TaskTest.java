package org.gcube.data.td.execution;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.OnRowErrorAction;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerStatus;
import org.gcube.data.analysis.tabulardata.task.TaskContext;
import org.gcube.data.analysis.tabulardata.task.engine.TaskEngine;
import org.gcube.data.analysis.tabulardata.utils.InternalInvocation;
import org.gcube.data.td.unit.TestWorker;
import org.gcube.data.td.unit.TestWorkerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class TaskTest {

	@Inject
	private TaskEngine taskEngine;
			
		
	@Inject @RequestScoped
	TestWorkerFactory factory;
		
	@Inject @RequestScoped
	TestWorkerFactory factory2;
	
	
	@Test	
	public void abortWorker(){
		TestWorker tw = new TestWorker(null);	
				
		Thread t = new Thread(tw);
		t.start();
		
		tw.abort();
		
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Assert.assertEquals(WorkerStatus.ABORTED, tw.getStatus());
	}
	
	@Test
	public void mytest(){
	 Assert.assertEquals(factory, factory2);
	}
	
	@Test
	public void abortExecution(){
		
		Map<String, Object> parameters = Collections.emptyMap();
		
		InternalInvocation ii = new InternalInvocation(parameters, factory);
		
		List<InternalInvocation> couples = Collections.singletonList(ii);
		
		TaskContext context = new TaskContext(couples, OnRowErrorAction.DISCARD);
			
		
			
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
		}
		
		/*
		System.out.println(info.getStatus());
		
		System.out.println(info.getResult().getResultTable());	*/	
	}
	
	
}
