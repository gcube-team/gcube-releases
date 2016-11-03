package org.gcube.common.scope;

import static org.junit.Assert.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopedTasks;
import org.junit.Test;

public class BindingTest {

	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	
	@Test
	public void callablesAreBound() throws Exception {
		
		final String callScope = "somescope";
		
		ScopeProvider.instance.set(callScope);
		
		Callable<Void> unbound = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				assertEquals("task thread is not bound to call scope, but to "+ScopeProvider.instance.get(),callScope,ScopeProvider.instance.get());
				return null;
			}
		};
		
		Callable<Void> bound = ScopedTasks.bind(unbound);
		
		String newScope = "newscope";
		
		//scope in current thread changes
		ScopeProvider.instance.set(newScope);
		
		//task is submittted
		executor.submit(bound).get();
		
		//resetting task
		assertEquals("call thread does not retain its latest scope",newScope,ScopeProvider.instance.get());

		//reset call scope
		ScopeProvider.instance.reset();
		
		Callable<Void> cleanupTest = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				assertNull(ScopeProvider.instance.get());
				return null;
			}
		};
		
		executor.submit(cleanupTest).get();
		
	}
	

	
}
