package org.gcube.data.spd.client.proxies;

import static org.gcube.common.clients.exceptions.FaultDSL.again;
import static org.gcube.data.streams.dsl.Streams.convert;
import static org.gcube.data.streams.dsl.Streams.pipe;
import static org.gcube.common.clients.stubs.jaxws.JAXWSUtils.*;
import gr.uoa.di.madgik.grs.record.GenericRecord;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.spd.client.ResultGenerator;
import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.model.exceptions.InvalidQueryException;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.stubs.ManagerStub;
import org.gcube.data.spd.stubs.exceptions.UnsupportedCapabilityException;
import org.gcube.data.spd.stubs.exceptions.UnsupportedPluginException;
import org.gcube.data.streams.Stream;
import com.thoughtworks.xstream.XStream;

public class DefaultManager implements Manager {

	private final ProxyDelegate<ManagerStub> delegate;
	
	private final ResultGenerator<ResultElement> resultElementGenerator = new ResultGenerator<ResultElement>();
	
	public DefaultManager(ProxyDelegate<ManagerStub> config){
		this.delegate = config;
	}
	
		
	@Override
	public Stream<ResultElement> search(final String query) throws InvalidQueryException, UnsupportedPluginException, UnsupportedCapabilityException{
		
		Call<ManagerStub, URI> call = new Call<ManagerStub, URI>() {
			@Override
			public URI call(ManagerStub manager) throws Exception {
				String uri = manager.search(query);
				return new URI(uri);
			}
		};
		try {
			return pipe(convert(delegate.make(call)).of(GenericRecord.class)
					.withTimeout(delegate.config().property("streamTimeoutInMinutes", Integer.class), TimeUnit.MINUTES)).through(resultElementGenerator);
		}catch(Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public List<PluginDescription> getPluginsDescription() {
		
		Call<ManagerStub, List<PluginDescription>> call = new Call<ManagerStub, List<PluginDescription>>() {
			@Override
			public List<PluginDescription> call(ManagerStub manager) throws Exception {
				List<String> xmlDescriptions = manager.getSupportedPlugins(empty).getDescriptions();
				List<PluginDescription> descriptors = new ArrayList<PluginDescription>();
				if (xmlDescriptions==null) return descriptors;
				XStream xstream = new XStream();
				for (String xmlDescription: xmlDescriptions)
					descriptors.add((PluginDescription)xstream.fromXML(xmlDescription));
				return descriptors;
				
			}
		};
		
		try {
			return delegate.make(call);
		}catch(Exception e) {
			throw again(e).asServiceException();
		}
	}
	
		
}
