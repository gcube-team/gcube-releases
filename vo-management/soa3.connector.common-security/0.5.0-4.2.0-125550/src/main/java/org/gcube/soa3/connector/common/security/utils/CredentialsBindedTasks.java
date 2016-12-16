package org.gcube.soa3.connector.common.security.utils;

import java.util.concurrent.Callable;

import org.gcube.soa3.connector.common.security.CredentialManager;
import org.gcube.soa3.connector.common.security.Credentials;


/**
 * Utility to bind the execution of standard tasks to the current credentials.
 * 
 * @author Ciro Formisano
 *
 */
public class CredentialsBindedTasks {

	/**
	 * Binds a {@link Callable} task to the current credentials.
	 * @param task the task
	 * @return an equivalent {@link Callable} task bound to the current credentials
	 */
	static public <V> Callable<V> bind(final Callable<V> task) {
		
		final Credentials callCredentials = CredentialManager.instance.get();
		
		return new Callable<V>() {
			@Override
			public V call() throws Exception {
				
				//bind underlying thread to callscope
				CredentialManager.instance.set(callCredentials);
				
				try {
					return task.call();
					
				}
				finally {
					CredentialManager.instance.reset();
				}
				
			}
		};
	}
	
	/**
	 * Binds a {@link Runnable} task to the current credentials.
	 * @param task the task
	 * @return an equivalent {@link Runnable} task bound to the current credentials
	 */
	static public <V> Runnable bind(final Runnable task) {
		
		final Credentials callCredentials = CredentialManager.instance.get();
		
		return new Runnable() {
			@Override
			public void run() {
				
				//bind underlying thread to callcredentials
				CredentialManager.instance.set(callCredentials);
				
				try {
					task.run();
				}
				finally {
					CredentialManager.instance.reset();
				}
				
			}
		};
	}
	
}
