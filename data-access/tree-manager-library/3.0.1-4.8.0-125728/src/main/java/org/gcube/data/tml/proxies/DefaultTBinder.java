package org.gcube.data.tml.proxies;

import static org.gcube.common.clients.exceptions.FaultDSL.*;
import static org.gcube.data.tml.Utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.AsyncProxyDelegate;
import org.gcube.common.clients.delegates.Callback;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.tml.stubs.TBinderStub;
import org.gcube.data.tml.stubs.Types.BindingsHolder;

/**
 * Default implementation of {@link TBinder} and {@link AsyncTBinder}.
 * 
 * @author Fabio Simeoni
 *
 */
public class DefaultTBinder implements TBinder,AsyncTBinder {

	private final AsyncProxyDelegate<TBinderStub> delegate;
	
	/**
	 * Creates an instance with {@link ProxyDelegate}
	 * @param delegate the delegate
	 */
	public DefaultTBinder(ProxyDelegate<TBinderStub> delegate) {
		this.delegate=new AsyncProxyDelegate<TBinderStub>(delegate);
	}
	
	@Override
	public List<Binding> bind(final BindRequest parameters) {
		
		notNull("binding parameters",parameters);
		
		try {
			return delegate.make(bindCall(parameters));
		}
		catch(Exception e) {
			throw again(e).asServiceException();
		}
	}
	
	@Override
	public Future<List<Binding>> bindAsync(BindRequest parameters) {
		
		notNull("binding parameters",parameters);
		
		return delegate.makeAsync(bindCall(parameters));
		
	}
	
	@Override
	public Future<?> bindAsync(BindRequest parameters, Callback<List<Binding>> callback) throws RejectedExecutionException {
		
		notNull("binding parameters",parameters);
		notNull("callback",callback);
		
		return delegate.makeAsync(bindCall(parameters),callback);
		
	}
	
	//generates calls shared by sync and async operations
	private Call<TBinderStub, List<Binding>> bindCall(final BindRequest parameters) {

		return new Call<TBinderStub, List<Binding>>() {
			
			@Override
			public List<Binding> call(TBinderStub endpoint) throws Exception {
				BindingsHolder bindings = endpoint.bind(parameters);
				return (bindings == null || bindings.bindings==null) ?
											new ArrayList<Binding>():
											bindings.bindings;
			}
			
		};
	}
	

}
