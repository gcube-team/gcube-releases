package org.gcube.common.scope.impl;

import java.util.concurrent.Callable;

import org.gcube.common.scope.api.ScopeProvider;

/**
 * Utility to bind the execution of standard tasks to the current scope.
 * 
 * @author Fabio Simeoni
 *
 */
public class ScopedTasks {

	/**
	 * Binds a {@link Callable} task to the current scope.
	 * @param task the task
	 * @return an equivalent {@link Callable} task bound to the current scope
	 */
	static public <V> Callable<V> bind(final Callable<V> task) {
		
		final String callScope = ScopeProvider.instance.get();
		
		return new Callable<V>() {
			@Override
			public V call() throws Exception {
				
				//bind underlying thread to callscope
				ScopeProvider.instance.set(callScope);
				
				try {
					return task.call();
					
				}
				finally {
					ScopeProvider.instance.reset();
				}
				
			}
		};
	}
	
	/**
	 * Binds a {@link Runnable} task to the current scope.
	 * @param task the task
	 * @return an equivalent {@link Runnable} task bound to the current scope
	 */
	static public <V> Runnable bind(final Runnable task) {
		
		final String callScope = ScopeProvider.instance.get();
		
		return new Runnable() {
			@Override
			public void run() {
				
				//bind underlying thread to callscope
				ScopeProvider.instance.set(callScope);
				
				try {
					task.run();
				}
				finally {
					ScopeProvider.instance.reset();
				}
				
			}
		};
	}
	
}
