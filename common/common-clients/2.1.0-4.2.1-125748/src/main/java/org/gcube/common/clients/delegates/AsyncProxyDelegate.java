package org.gcube.common.clients.delegates;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.scope.api.ScopeProvider;

/**
 * A {@link ProxyDelegate} that delivers the outcome of {@link Call}s asynchronously, either through polling or
 * notifications.
 * <p>
 * The delegates use {@link ExecutorService}s to make calls in separate threads. If required, clients may provide their own
 * {@link ExecutorService}s at the point of call submission.
 * 
 * @author Fabio Simeoni
 * 
 * @param <S> the type of service stubs
 */
public class AsyncProxyDelegate<S> implements ProxyDelegate<S> {

	// we try to cope with demand within holding on to threads that may never be used
	private final static ExecutorService service = Executors.newCachedThreadPool();

	// quits the default service when JVM does
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				service.shutdown();
			}
		});
	}

	// the inner synchronous delegate
	private final ProxyDelegate<S> inner;

	/**
	 * Creates an instance with a (synchronous) {@link ProxyDelegate}
	 * 
	 * @param delegate the delegate
	 */
	public AsyncProxyDelegate(ProxyDelegate<S> delegate) {
		this.inner = delegate;
	}

	@Override
	public <V> V make(Call<S, V> call) throws Exception {
		return inner.make(call);
	}

	@Override
	public ProxyConfig<?, S> config() {
		return inner.config();
	}

	/**
	 * Makes a {@link Call} to a service endpoint asynchronously, returning a {@link Future} that clients can use to
	 * poll for and obtain the call outcome, or to cancel the call (assuming that the call is designed for cancellation
	 * or has not been made yet).
	 * 
	 * @param call the {@link Call} to be made asynchronously
	 * @return the {@link Future} of the {@link Call} outcome
	 * 
	 * @param <V> the type of the value returned from the {@link Call}
	 * 
	 * @throws RejectedExecutionException if the call cannot not be submitted for asynchronous execution
	 */
	public <V> Future<V> makeAsync(Call<S, V> call) throws RejectedExecutionException {

		return makeAsync(call, service);

	}

	/**
	 * Makes a {@link Call} to a service endpoint asynchronously, returning a {@link Future} that clients can use to
	 * poll for and obtain the call outcome, or to cancel the call (assuming that the call is designed for cancellation
	 * or has not been made yet).
	 * 
	 * @param call the {@link Call} to be executed asynchronously
	 * @param service a {@link ExecutorService} to which the {@link Call} should be submitted for execution
	 * 
	 * @return the {@link Future} of the {@link Call} outcome
	 * 
	 * @param <V> the type of the value returned from the {@link Call}
	 * 
	 * @throws RejectedExecutionException if the call cannot not be submitted for asynchronous execution
	 * 
	 */
	public <V> Future<V> makeAsync(final Call<S, V> call, ExecutorService service) throws RejectedExecutionException {

		final String callScope = ScopeProvider.instance.get();
		
		// create task from call
		Callable<V> callTask = new Callable<V>() {

			@Override
			public V call() throws Exception {
				
				ScopeProvider.instance.set(callScope);
				
				return inner.make(call);
			}
		};

		// submit task
		return service.submit(callTask);
	}

	/**
	 * Makes a {@link Call} to a service endpoint asynchronously, notifying a {@link Callback} of its outcome. Returns a
	 * {@link Future} that clients can use to cancel the execution of the call (assuming that the call is designed for
	 * cancellation or has not been made yet).
	 * 
	 * @param call the {@link Call}
	 * @param callback the {@link Callback}
	 * 
	 * @return the {@link Future} of call submission
	 * 
	 * @throws RejectedExecutionException if the call cannot not be submitted for asynchronous execution
	 */
	public <V> Future<?> makeAsync(final Call<S, V> call, final Callback<V> callback) throws RejectedExecutionException {

		return makeAsync(call, callback, service);
	}

	/**
	 * Makes a {@link Call} to a service endpoint asynchronously, notifying a {@link Callback} of its outcome. Returns a
	 * {@link Future} that clients can use to cancel the execution of the call (assuming that the call is designed for
	 * cancellation or has not been made yet).
	 * 
	 * @param call the {@link Call}
	 * @param callback the {@link Callback}
	 * @param service the {@link ExecutorService} that executes the call
	 * 
	 * @return the {@link Future} of call submission
	 * 
	 * @throws RejectedExecutionException if the call cannot not be submitted for asynchronous execution
	 */
	public <V> Future<?> makeAsync(final Call<S, V> call, final Callback<V> callback, ExecutorService service)
			throws RejectedExecutionException {

		// submit call
		final Future<V> callFuture = makeAsync(call, service);

		// create a task that blocks waiting on outome
		Runnable waitingTask = new Runnable() {

			@Override
			public void run() {
				try {

					long timeout = callback.timeout();
					
					V outcome = null;
					
					// honour callback timeout
					if (timeout==0)
						//not only may clients want to wait indefinitely, they may have set the timeout on proxy
						outcome = callFuture.get();
					else 
						outcome = callFuture.get(timeout, TimeUnit.MILLISECONDS);
				
					// notify callback
					callback.done(outcome);

				} catch (InterruptedException e) {
					
					// we assume client has cancelled task, hence do not notify it of its own actions
					// but we reset the flag for other consumers, such as the executor service
					// so clients do not need to worry about it.
					Thread.currentThread().interrupt();
					
				} catch (ExecutionException e) {
					
					// notify callback of underlying failure
					// by now, it will have already beeen converted
					callback.onFailure(e.getCause());

				} catch (TimeoutException e) {

					// attempt to cancel the call, in case it's designed for it
					callFuture.cancel(true);
					
					// notify callback the required timeout has expired
					callback.onFailure(e);


				}
			}
		};

		// submits waiting task
		service.submit(waitingTask);

		// return call task future, rather than waiting task
		return callFuture;
	}
}
