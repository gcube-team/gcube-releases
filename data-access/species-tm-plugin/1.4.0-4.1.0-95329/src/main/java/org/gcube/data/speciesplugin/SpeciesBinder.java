package org.gcube.data.speciesplugin;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.gcube.data.speciesplugin.requests.RequestBinder;
import org.gcube.data.speciesplugin.requests.SpeciesRequest;
import org.gcube.data.speciesplugin.store.SpeciesNeoStore;
import org.gcube.data.speciesplugin.store.SpeciesStore;
import org.gcube.data.tmf.api.Property;
import org.gcube.data.tmf.api.SourceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * @author "Valentina Marioli valentina.marioli@isti.cnr.it"
 */
public class SpeciesBinder implements SourceBinder {

	private static Logger log = LoggerFactory.getLogger(SpeciesBinder.class);	
	private static RequestBinder db = new RequestBinder();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<? extends SpeciesSource> bind(Element request) throws Exception {
		
		//parse request
		SpeciesRequest speciesRequest = db.bind(request);
		log.trace("speciesRequest: "+speciesRequest);
		
		String id = speciesRequest.getId();
		
		if (id == null) {
			id = UUID.randomUUID().toString();
			log.trace("No id specified, generated id "+id);
		}

		//obtains a store for that identifier 
		SpeciesStore store = new SpeciesNeoStore(id);
		
		String requestXML = db.toXML(speciesRequest);
		List<Property> properties = Collections.singletonList(new Property("Creation request", "request", requestXML));

		//create source
		SpeciesSource source = new SpeciesSource(id, store, properties);

		//configure as per request
		source.setName(speciesRequest.getName());
		source.setDescription(speciesRequest.getDescription());

		SpeciesLifecycle lifecycle = new SpeciesLifecycle(source, speciesRequest.getScientificNames(), speciesRequest.getDatasources(), speciesRequest.isStrictMatch(), speciesRequest.getRefreshPeriod(), speciesRequest.getTimeUnit());
		source.setLifecycle(lifecycle);

		source.setCreationTime(Calendar.getInstance());
		source.setLastUpdate(Calendar.getInstance());

		SpeciesReader reader = new SpeciesReader(source);
		source.setReader(reader);	


		log.info("bound source {}",source);

		return Collections.singletonList(source);
	}
}
