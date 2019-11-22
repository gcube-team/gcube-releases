package org.gcube.data.spd.client.proxies;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.data.spd.client.Constants;
import org.gcube.data.spd.client.plugins.AbstractPlugin;
import org.gcube.data.spd.model.util.SerializableList;
import org.gcube.data.streams.Stream;
import org.glassfish.jersey.client.ChunkedInput;

public class DefaultResultSet implements ResultSetClient {

	private final ProxyDelegate<WebTarget> delegate;

	public DefaultResultSet(ProxyDelegate<WebTarget> config){
		this.delegate = config;
	}

	@Override
	public ChunkedInput<String> getResultSet(final String locator){
		Call<WebTarget, ChunkedInput<String>> call = new Call<WebTarget, ChunkedInput<String>>() {
			@Override
			public ChunkedInput<String> call(WebTarget manager) throws Exception {
				return  manager.path(locator).request().get(new GenericType<ChunkedInput<String>>() {});
			}
		};
		try {
			return delegate.make(call);

		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void closeResultSet(final String locator){
		Call<WebTarget, Empty> call = new Call<WebTarget, Empty>() {
			@Override
			public Empty call(WebTarget manager) throws Exception {
				manager.path(locator).request().delete();
				return new Empty();
			}
		};
		try {
			delegate.make(call);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected boolean sendInput(final String locator, final List<String> input) {
		Call<WebTarget, Boolean> call = new Call<WebTarget, Boolean>() {
			@Override
			public Boolean call(WebTarget manager) throws Exception {
				return manager.path(locator).request().put(Entity.xml(new SerializableList<String>(input)), Boolean.class);
			}
		};
		try {
			return delegate.make(call);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected static void sendInput(final String gCoreEnpointId, final String locatorId, final Stream<String> stream) throws Exception{
		Thread thread = new Thread(){
			public void run(){
				List<String> collected = new ArrayList<String>(10);
				DefaultResultSet client = (DefaultResultSet)AbstractPlugin.resultset(gCoreEnpointId).build();
				while (stream.hasNext()){
					collected.add(stream.next());
					if (collected.size()>=Constants.INPUT_BUNCH){
						if (!client.sendInput(locatorId, collected))
							throw new RuntimeException();		
						collected.clear();
					}
				}
				if (collected.size()>0)
					if (!client.sendInput(locatorId, collected))
						throw new RuntimeException();		
				client.sendInput(locatorId, new ArrayList<String>(0));
			}
		};
		thread.start();
	}

}
