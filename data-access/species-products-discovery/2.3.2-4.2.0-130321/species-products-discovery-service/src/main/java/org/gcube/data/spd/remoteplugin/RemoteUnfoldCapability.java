package org.gcube.data.spd.remoteplugin;

import static org.gcube.data.streams.dsl.Streams.convert;

import java.net.URI;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.data.spd.exception.ServiceException;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.plugin.fwk.capabilities.UnfoldCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.stubs.NamesMappingRequest;
import org.gcube.data.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteUnfoldCapability implements UnfoldCapability{

	volatile Logger logger = LoggerFactory.getLogger(RemoteNamesMappingCapability.class);
	
	private Set<Conditions> props = new HashSet<Conditions>();
	private String parentName;
	private List<String> uris;
	
	public RemoteUnfoldCapability(Conditions[] properties, String parentName, List<String> uris){
		if (properties!=null)
			for (Conditions prop: properties)
				props.add(prop);
		this.parentName = parentName;
		this.uris = uris;
	}


	@Override
	public void unfold(ObjectWriter<String> writer, String scientificName)
			throws ExternalRepositoryException {
		String locator = null;
		try {
			locator = RemotePlugin.getRemoteDispatcher(uris).namesMapping(new NamesMappingRequest(scientificName, this.parentName));
			Stream<String> names = convert(URI.create(locator)).ofStrings().withDefaults();
			while (names.hasNext())
				writer.write(names.next());
		} catch (ServiceException e) {
			throw new ExternalRepositoryException("error contacting external services",e);
		} catch (RemoteException re){
			throw new ExternalRepositoryException("error contacting remote service",re);
		}
	}
	
}
