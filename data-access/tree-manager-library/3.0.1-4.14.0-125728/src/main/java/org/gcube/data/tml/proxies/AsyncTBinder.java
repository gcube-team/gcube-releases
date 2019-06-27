package org.gcube.data.tml.proxies;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import org.gcube.common.clients.delegates.Callback;

/**
 * An interface over remote T-Binder endpoints.
 * 
 * <p>
 * T-Binder endpoints bind given data sources to T-Reader and T-Writer endpoints.
 * These give access to the bound sources under a tree-based model. 
 * 
 * <p>
 *  
 * @author Fabio Simeoni
 * 
 * @see TReaderClient
 * @see TWriterClient
 *
 */
public interface AsyncTBinder {

	/**
	 * Binds asynchronously to T-Reader and/or T-Writer services and notifies a {@link Callback} of the outcome of the call
	 * @param parameters the binding parameters
	 * @param consumer a callback for the asynchronous delivery of the outcome of the call
	 * 
	 * @throws RejectedExecutionException if the call cannot be submitted for asynchronous execution 
	 */
	Future<?> bindAsync(BindRequest parameters,Callback<List<Binding>> callback);
	
	
	/**
	 * Binds asynchronously to T-Reader and/or T-Writer services 
	 * and returns a {@link Future} of the call outcome which clients can poll or use to cancel submission.
	 * @param parameters the binding parameters
	 * @return the {@link Future} outcome
	 * 
	 * @throws RejectedExecutionException if the call cannot be submitted for asynchronous execution
	 **/
	Future<List<Binding>> bindAsync(BindRequest parameters);
}