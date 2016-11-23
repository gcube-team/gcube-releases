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
import org.gcube.data.spd.plugin.fwk.capabilities.ExpansionCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.stubs.ExpandWithSynonymsRequest;
import org.gcube.data.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteExpandCapability implements ExpansionCapability {

	volatile Logger logger = LoggerFactory.getLogger(RemoteExpandCapability.class);
	
	private Set<Conditions> props = new HashSet<Conditions>();
	private String parentName;
	private List<String> uris;
	
	public RemoteExpandCapability(Conditions[] properties, String parentName, List<String> uris){
		if (properties!=null)
			for (Conditions prop: properties)
				props.add(prop);
		this.parentName = parentName;
		this.uris = uris;
	}
	
	@Override
	public void getSynonyms(ObjectWriter<String> writer, String scientificName) throws ExternalRepositoryException {
		try{
			String locator = RemotePlugin.getRemoteDispatcher(uris).expandWithSynonyms(new ExpandWithSynonymsRequest(this.parentName, scientificName));
			Stream<String> synonyms = convert(URI.create(locator)).ofStrings().withDefaults();
			while (synonyms.hasNext())
				writer.write(synonyms.next());
		}catch (ServiceException e) {
			throw new ExternalRepositoryException("error contacting external services",e);
		} catch (RemoteException re){
			throw new ExternalRepositoryException("error contacting remote service",re);
		}
	}

}
