package org.gcube.data.oai.tmplugin;

import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.gcube.data.oai.tmplugin.repository.Repository;
import org.gcube.data.oai.tmplugin.repository.Set;
import org.gcube.data.oai.tmplugin.requests.Request;
import org.gcube.data.oai.tmplugin.requests.RequestBinder;
import org.gcube.data.oai.tmplugin.requests.WrapRepositoryRequest;
import org.gcube.data.oai.tmplugin.requests.WrapSetsRequest;
import org.gcube.data.tmf.api.SourceBinder;
import org.gcube.data.tmf.api.exceptions.InvalidRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class OAIBinder implements SourceBinder {

	private static Logger log = LoggerFactory.getLogger(OAIBinder.class);

	private RepositoryProvider provider;

	public OAIBinder() {
		this(new ProductionProvider());
	}

	//backdoor for testing
	OAIBinder(RepositoryProvider p) {
		provider=p;
	}

	@Override
	public List<? extends OAISource> bind(Element DOMRequest)
			throws InvalidRequestException, Exception {

		//parse parameters
		Request request = null;
		try {	
			request= new RequestBinder().bind(DOMRequest,Request.class);
		}
		catch(Exception e) {
			throw new InvalidRequestException("malformed request",e);
		}

		//dispatch
		if (request instanceof WrapRepositoryRequest)
			return wrapRepository((WrapRepositoryRequest)request); 

		else if (request instanceof WrapSetsRequest)
			return wrapSets((WrapSetsRequest)request);

		else throw new InvalidRequestException("unknown request "+DOMRequest.getTagName());
	}

	//a source per all identified sets, no identifiers means all sets
	List<OAISource> wrapSets(WrapSetsRequest request) throws Exception{

		log.info("request.getSets() "  +request.getSets());
		Repository repository= provider.newRepository(request);

		List<OAISource> sources= new ArrayList<OAISource>();

		List<Set> sets = repository.getSetsWith(request.getSets());
		log.info("sets "  +sets.toString());

		for (Set set : sets) {

			//is possible to add hash of something (eg. name+desc) to make it unique ?
			//			String sourceId = set.id();			
			//			OAISource source = new OAISource(sourceId);

			OAISource source = new OAISource(UUID.randomUUID().toString());

			source.setName(repository.name() + ": " + set.name());

			String prefix = "Set: "+ set.name() + " - ";
			//defaults to null
			source.setDescription(request.getDescription()==null?prefix + repository.description():prefix + request.getDescription());

			OAIReader reader = new OAIReader(source,repository,Arrays.asList(set)); 

			source.setReader(reader);

			source.setCreationTime(Calendar.getInstance());

			source.setLifecycle(new Lifecycle(source));

			log.trace("created source "+source.id());

			sources.add(source);

		}

		return sources;
	}

	//one source over all the identified sets, no identifiers means all sets
	List<OAISource> wrapRepository(WrapRepositoryRequest request) throws Exception{

		Repository repository = provider.newRepository(request);

		//no ids means all sets
		List<Set> sets = repository.getSetsWith(request.getSets());

		//		OAISource source = new OAISource(request.getId());

		OAISource source = new OAISource(UUID.randomUUID().toString());

		//defaults to repository name
		source.setName(request.getName()==null?repository.name():request.getName());

		source.setCreationTime(Calendar.getInstance());

		//defaults to repository description
		source.setDescription(request.getDescription()==null?repository.description():request.getDescription());		

		source.setReader(new OAIReader(source,repository,sets));

		source.setLifecycle(new Lifecycle(source));

		log.trace("created source {}"+source.id());

		return singletonList(source);

	}


}
