package org.gcube.common.authorization.library;

import java.util.concurrent.Callable;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.utils.Caller;


public class AuthorizedTasks {

	/**
	 * Binds a {@link Callable} task to the current scope and user.
	 * @param task the task
	 * @return an equivalent {@link Callable} task bound to the current scope and user
	 */
	static public <V> Callable<V> bind(final Callable<V> task) {
		
		
		final Caller userCall = AuthorizationProvider.instance.get();
		
		final String token = SecurityTokenProvider.instance.get();
		
		return new Callable<V>() {
			@Override
			public V call() throws Exception {
				
				AuthorizationProvider.instance.set(userCall);
				SecurityTokenProvider.instance.set(token);
				try {
					return task.call();
				}
				finally {
					AuthorizationProvider.instance.reset();
					SecurityTokenProvider.instance.reset();
				}
				
			}
		};
	}
	
	/**
	 * Binds a {@link Runnable} task to the current scope and user.
	 * @param task the task
	 * @return an equivalent {@link Runnable} task bound to the current scope and user
	 */
	static public <V> Runnable bind(final Runnable task) {
		
		
		final Caller userCall = AuthorizationProvider.instance.get();
		
		final String token = SecurityTokenProvider.instance.get();
		
		return new Runnable() {
			@Override
			public void run() {
				AuthorizationProvider.instance.set(userCall);
				SecurityTokenProvider.instance.set(token);
				try {
					task.run();
				}
				finally {
					AuthorizationProvider.instance.reset();
					SecurityTokenProvider.instance.reset();
				}
				
			}
		};
	}
	
}
