package org.gcube.opensearch.opensearchdatasource.service.helpers;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.rr.element.search.index.OpenSearchDataSource;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.gcube.opensearch.opensearchdatasource.service.OpenSearchOperator;
import org.gcube.opensearch.opensearchlibrary.DescriptionDocument;
import org.gcube.opensearch.opensearchlibrary.queryelements.BasicQueryElementFactory;
import org.gcube.opensearch.opensearchlibrary.urlelements.BasicURLElementFactory;
import org.gcube.opensearch.opensearchoperator.resource.ISOpenSearchResource;
import org.gcube.opensearch.opensearchoperator.resource.ISOpenSearchResourceCache;
import org.gcube.rest.opensearch.common.discover.OpenSearchDataSourceDiscoverer;
import org.gcube.rest.opensearch.common.discover.OpenSearchDiscovererAPI;
import org.gcube.rest.opensearch.common.discover.exceptions.OpenSearchDiscovererException;
import org.gcube.rest.opensearch.common.resources.OpenSearchDataSourceResource;
import org.gcube.rest.resourcemanager.harvester.ResourceHarvester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * In charge of refreshing the generic resource caches contained in the state WS resources of the
 * {@link OpenSearchDataSource}. After purging a cache, the top-level generic resource is retrieved
 * again from the IS and re-cached, ensuring that the service operates on up-to-date generic resources.
 * All cached description documents contained in the caches are also purged from the caches.
 * Depending on configuration, the refresh operation can be performed periodically on a configurable time interval in milliseconds.
 * If the time interval is equal to 0, no periodic refresh cycles are performed.
 * The cache can also be refreshed on demand, via the {@link OpenSearchDataSource#refreshCache(org.gcube.opensearch.opensearchdatasource.stubs.RefreshCache)} operation
 * of its portType
 * 
 * @author gerasimos.farantatos, NKUA
 *
 */
public class CacheRefresher implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(CacheRefresher.class);
	
	
	private long lastRefresh;
	private long refreshIntervalMillis;
	private long cycleCount;
	private boolean forceRefresh;
	private Object synchMe = null;
	
	
	private final OpenSearchOperator openSearchOperator;
	private final OpenSearchDiscovererAPI<OpenSearchDataSourceResource> discoverer;
	private final String scope;
	private final String hostname;
	
	
	
	/**
	 * Creates a new cache refresher
	 * 
	 * @param refreshIntervalMillis The time interval between cache refresh cycles, in milliseconds
	 */
	public CacheRefresher(long refreshIntervalMillis, OpenSearchOperator openSearchOperator, OpenSearchDiscovererAPI<OpenSearchDataSourceResource> discoverer, String hostname, String scope) {
		if(refreshIntervalMillis < 0)
			throw new IllegalArgumentException("Negative time interval value");
		this.refreshIntervalMillis = refreshIntervalMillis;
		this.cycleCount = 1;
		this.synchMe = new Object();
		this.forceRefresh = false;
		this.openSearchOperator = openSearchOperator;
		this.discoverer = discoverer;
		this.hostname = hostname;
		this.scope = scope;
	}
	
	/**
	 * Performs the periodic and forced cache refresh operations
	 */
	public void run() {
		this.lastRefresh = Calendar.getInstance().getTimeInMillis();
		long millisSinceLastRefresh = 0;
		long nextRefresh = refreshIntervalMillis;
		
		while(true) {
			
			int refreshCount = 0;
			int failed = 0;
			if(refreshIntervalMillis != 0)
				logger.debug("Next cache refresh in " + (nextRefresh - millisSinceLastRefresh) + " milliseconds");
			else
				millisSinceLastRefresh = Integer.MIN_VALUE; //Just a value so that the condition will be satisfied at all times
			boolean explicitRefreshRequested = false;
			while(millisSinceLastRefresh < nextRefresh) {
				synchronized(synchMe) {
					try {
						if(refreshIntervalMillis != 0)
							synchMe.wait(nextRefresh - millisSinceLastRefresh); 
						else
							synchMe.wait();
					}catch(InterruptedException e) { /*swallowing is harmless in this case*/ }
					
					if(forceRefresh == true) {
						forceRefresh = false;
						explicitRefreshRequested = true;
						millisSinceLastRefresh = Calendar.getInstance().getTimeInMillis() - lastRefresh;
						nextRefresh = refreshIntervalMillis - millisSinceLastRefresh;
						break;
					}
				}

				if(refreshIntervalMillis != 0)
					millisSinceLastRefresh = Calendar.getInstance().getTimeInMillis() - lastRefresh;
			}

			
			
			if(explicitRefreshRequested == false) {
				nextRefresh = refreshIntervalMillis;
				lastRefresh = Calendar.getInstance().getTimeInMillis();
				logger.debug("Starting generic resource cache refresh cycle #" + cycleCount);
			}
			else
				logger.debug("Forced to start generic resource cache refresh");
			
			millisSinceLastRefresh = 0;
			
//			OpenSearchDataSourceResourceHome wsHome = (OpenSearchDataSourceResourceHome)StatefulContext.getPortTypeContext().getWSHome();
//			Collection<? extends GCUBEWSResourceKey> resourceKeys = wsHome.getIdentifiers();
			
			/*
			 * 
			 * 
			 * for(GCUBEWSResourceKey resourceKey : resourceKeys) {
	    		OpenSearchDataSourceResource resource;
	    		try {
	    			resource= wsHome.find(resourceKey);
	    			if(!(resource instanceof OpenSearchDataSourceResource))
	    				continue;
	    		}catch(Exception e) {
	    			logger.warn("Failed to retrieve WS resource " + resourceKey + ". Skipping.");
	    			continue;
	    		}
			 * */
			//discover local resources???
			
			
			Set<OpenSearchDataSourceResource> resources = Sets.newHashSet();
			try {
				resources = discoverer.discoverOpenSearchResourcesLocal(scope, hostname);
			} catch (OpenSearchDiscovererException e1) {
				logger.error("error in local resource discovery", e1);
			} 
			
			
	    	for(OpenSearchDataSourceResource resource : resources) {
		     	
		     	ISOpenSearchResourceCache cache = resource.cache;
		     	List<String> ddURIs = resource.getDescriptionDocumentURI();
		     	List<String> genericResourceIds = resource.getOpenSearchResource();
		     	String scope =resource.getScope();
		     	EnvHintCollection envHints = ((OpenSearchDataSourceResource)resource).getISEnvHints();
		     	
		     	int providerCount = resource.getCollectionID().size();
		     	
				String genericResourceXML[] = new String[providerCount];
				boolean genericResourceFail = false;
				for(int i = 0; i < providerCount; i++) {
					try {
						genericResourceXML[i] = this.openSearchOperator.retrieveGenericResource(genericResourceIds.get(i), scope).toString();
					}catch(Exception e) {
						logger.warn("Failed to retrieve generic resource with id " + genericResourceIds.get(i) + ". Skipping cache refreshing for this resource", e);
						genericResourceFail = true;
						break;
					}
				}
				if(genericResourceFail == true)
					continue;
				
				synchronized(cache) {
					ISOpenSearchResource[] savedResources = new ISOpenSearchResource[providerCount];
					String[] savedDdUris = new String[providerCount];
					for(int i = 0; i < providerCount; i++) {
						savedResources[i] = ((OpenSearchDataSourceResource)resource).openSearchGenericResources[i];
						savedDdUris[i] = ((OpenSearchDataSourceResource)resource).getDescriptionDocumentURI().get(i);
					}
					cache.descriptionDocuments.clear();
					cache.resources.clear();
					cache.resourcesXML.clear();
					cache.XSLTs.clear();
					for(int i = 0; i < providerCount; i++) {
						
						boolean fail = false;
						try {
							((OpenSearchDataSourceResource)resource).openSearchGenericResources[i] = new ISOpenSearchResource(genericResourceXML[i], cache.descriptionDocuments, 
									cache.resourcesXML, cache.XSLTs, envHints);
						}catch(Exception e) {
							logger.warn("Could not create OpenSearch resource instance during cache refreshing. Using old instance.", e);
							fail = true;
						}
						cache.resources.put(ddURIs.get(i), ((OpenSearchDataSourceResource)resource).openSearchGenericResources[i]);
						if(!fail) {
							DescriptionDocument dd = null;
							
							ddURIs.add(((OpenSearchDataSourceResource)resource).openSearchGenericResources[i].getDescriptionDocURL());
							
							try {
								dd = new DescriptionDocument(((OpenSearchDataSourceResource)resource).openSearchGenericResources[i].getDescriptionDocument(), new BasicURLElementFactory(), new BasicQueryElementFactory());
							}catch(Exception e) {
								logger.warn("Could not create Description Document instance during cache refreshing. Using old OpenSearch resource instance.", e);
								fail = true;
							}
							if(!fail) {
	//							String[] templates = null;
	//							try {
	//								templates = OpenSearchDataSourceResource.retrieveTemplates(dd, ((OpenSearchDataSourceResource)resource).openSearchGenericResource);
	//							}catch(Exception e) {
	//								logger.warn("Could not retrieve templates from Description Document. Using old OpenSearch resource instance.", e);
	//								fail = true;
	//							}
								if(!fail) {
									try {
										if(i == 0)((OpenSearchDataSourceResource)resource).setDescriptionDocumentURI(ddURIs.get(i));
										else ((OpenSearchDataSourceResource)resource).getDescriptionDocumentURI().add(ddURIs.get(i));
									}catch(Exception e) {
										logger.warn("Could not update WS resource with the new Description Document URI. Using old OpenSearch resource instance", e);
										fail = true;
									}
	//								if(!fail) {
	//									try {
	//										((OpenSearchDataSourceResource)resource).setTemplates(templates);
	//									}catch(Exception e) {
	//										logger.warn("Could not update WS resource with the new templates. Using old OpenSearch resource instance", e);
	//										try {
	//											((OpenSearchDataSourceResource)resource).setDescriptionDocumentURI(savedDdUri); //First step toward reverting to old state
	//										}catch(Exception ee) {
	//											logger.error("Could not revert WS resource to its old state. Resource will remain inconsistent!", ee);
	//											fail = true;
	//										}
	//									}
	//								}
								}
							} 
							if(fail) { //Revert to old state on failure		
								Exception ex = null;
								try {
									cache.resources.clear();
									for(int j =0; j < providerCount; j++) {
										((OpenSearchDataSourceResource)resource).openSearchGenericResources[j] = savedResources[j];
										((OpenSearchDataSourceResource)resource).setDescriptionDocumentURI(Arrays.asList(savedDdUris));
										cache.resources.put(savedDdUris[j], savedResources[j]);
									}
								}
								catch(Exception e) {
									logger.warn("Failed to revert to old state after failure of cache refresh for WS resource bound to generic resources with ids " + (genericResourceIds));
									ex = e;
								}
								if(ex == null)
									logger.warn("Failed to refresh cache for WS resource bound to generic resources with ids " + (genericResourceIds));
							}else
								refreshCount++;
						}else
							failed++;
					}
						logger.debug("Refreshed cache and updated state of WS resource bound to generic resources with ids " + (genericResourceIds));
			    }
	    	}
			if(explicitRefreshRequested == false) {
				logger.debug("Ended generic resource cache refresh cycle #" + cycleCount);
				cycleCount++;
			}else
				logger.debug("Ended forced generic resource cache refreshing");
			logger.debug("Total generic resource caches refreshed: " + refreshCount);
			logger.debug("Total generic resource cache refresh operations failed: " + failed);
		}
	}
		
	/**
	 * Schedules a forced cache refresh to be performed as soon as possible
	 * The scheduled activation time of the next cache refresh cycle is not affected
	 */
	public void forceRefresh() {
		synchronized(synchMe) {
			forceRefresh = true;
			synchMe.notify();
		}
	}

}
