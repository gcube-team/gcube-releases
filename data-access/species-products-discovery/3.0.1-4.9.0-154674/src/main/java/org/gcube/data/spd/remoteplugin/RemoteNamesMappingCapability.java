package org.gcube.data.spd.remoteplugin;

import static org.gcube.data.streams.dsl.Streams.convert;

import java.net.URI;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.gcube.data.spd.exception.ServiceException;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.plugin.fwk.capabilities.MappingCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteNamesMappingCapability implements MappingCapability {

	volatile Logger logger = LoggerFactory.getLogger(RemoteNamesMappingCapability.class);

	private Set<Conditions> props = new HashSet<Conditions>();
	private String parentName;
	private Collection<String> uris;

	public RemoteNamesMappingCapability(Conditions[] properties, String parentName, Collection<String> uris){
		if (properties!=null)
			for (Conditions prop: properties)
				props.add(prop);
		this.parentName = parentName;
		this.uris = uris;
	}


	@Override
	public void getRelatedScientificNames(ObjectWriter<String> writer,
			String commonName) throws ExternalRepositoryException{
		//TODO : call remote rest service
		String locator = "";// RemotePlugin.getRemoteDispatcher(uris).namesMapping(commonName, this.parentName);
		Stream<String> names = convert(URI.create(locator)).ofStrings().withDefaults();
		while (names.hasNext())
			writer.write(names.next());

	}


}
