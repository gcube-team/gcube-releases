package org.gcube.common.authorization.library;

import java.util.concurrent.Callable;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.scope.api.ScopeProvider;


public class AuthorizedTasks {

	/**
	 * Binds a {@link Callable} task to the current scope and user.
	 * @param task the task
	 * @return an equivalent {@link Callable} task bound to the current scope and user
	 */
	static public <V> Callable<V> bind(final Callable<V> task) {
		
		final String callScope = ScopeProvider.instance.get();
		
		final UserInfo userCall = AuthorizationProvider.instance.get();
		
		return new Callable<V>() {
			@Override
			public V call() throws Exception {
				
				//bind underlying thread to callscope
				ScopeProvider.instance.set(callScope);
				//bind underlying thread to call user
				AuthorizationProvider.instance.set(userCall);
				try {
					return task.call();
				}
				finally {
					ScopeProvider.instance.reset();
					AuthorizationProvider.instance.reset();
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
		
		final String callScope = ScopeProvider.instance.get();
		
		final UserInfo userCall = AuthorizationProvider.instance.get();
		
		return new Runnable() {
			@Override
			public void run() {
				
				//bind underlying thread to callscope
				ScopeProvider.instance.set(callScope);
				//bind underlying thread to call user
				AuthorizationProvider.instance.set(userCall);
				
				try {
					task.run();
				}
				finally {
					ScopeProvider.instance.reset();
					AuthorizationProvider.instance.reset();
				}
				
			}
		};
	}
	
}
