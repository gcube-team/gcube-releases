package org.gcube.opensearch.opensearchoperator.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.is.InformationSystem;

/**
 * Class implementing the ResourceRepository interface that is used to retrieve OpenSearch resources
 * utilizing an InformationSystem. This repository uses an ISOpenSearchResourceCache to quickly return 
 * cached OpenSearch resources and further populates this cache in the event of a cache miss
 * 
 * @author gerasimos.farantatos
 *
 */
public class ISResourceRepository implements ResourceRepository {
	
	private ISOpenSearchResourceCache cache = null;
	private EnvHintCollection envHints = null;
	private Logger logger = LoggerFactory.getLogger(ISResourceRepository.class.getName());
	
	/**
	 * Creates a new ISResourceRepository
	 * 
	 * @param cache The cache to be used and further populated by the repository
	 * @param envHints The environment hints that will be passed while querying the InformationSystem
	 */
	public ISResourceRepository(ISOpenSearchResourceCache cache, EnvHintCollection envHints) {
		this.cache = cache;
		this.envHints = envHints;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchoperator.resource.ResourceRepository#get(String)
	 */
	public OpenSearchResource get(String descriptionDocumentURL) throws Exception{
		OpenSearchResource res;
		String xmlRes = null;
		synchronized(cache) {
			if((res = cache.resources.get(descriptionDocumentURL)) != null) {
		//		logger.info("Found resource in cache"); TODO remove
				return res;
			}
		}
		
		/*
		 * Retrieve the generic openSearch resource for this DD
		 */
		xmlRes = InformationSystem.GetOpenSearchGenericByDescriptionDocumentURI(descriptionDocumentURL, envHints);
		
		synchronized(cache) {
		//	logger.info("Did not find resource in cache"); TODO remove
			res = new ISOpenSearchResource(xmlRes, cache.descriptionDocuments, cache.resourcesXML, cache.XSLTs, this.envHints);
			cache.resources.put(descriptionDocumentURL, res);
		}
		return res;
	}

}
