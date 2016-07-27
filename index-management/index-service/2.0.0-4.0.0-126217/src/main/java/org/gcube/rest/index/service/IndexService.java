package org.gcube.rest.index.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.gcube.elasticsearch.FullTextNode;
import org.gcube.elasticsearch.entities.ClusterResponse;
import org.gcube.rest.commons.filter.IResourceFilter;
import org.gcube.rest.commons.helpers.JSONConverter;
import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.gcube.rest.commons.resourcefile.IResourceFileUtils;
import org.gcube.rest.index.client.IndexClient;
import org.gcube.rest.index.common.Constants;
import org.gcube.rest.index.common.apis.IndexServiceAPI;
import org.gcube.rest.index.common.discover.IndexDiscovererAPI;
import org.gcube.rest.index.common.helpers.ResultReader;
import org.gcube.rest.index.common.resources.IndexResource;
import org.gcube.rest.index.service.resources.IndexResourceFactory;
import org.gcube.rest.resourceawareservice.ResourceAwareService;
import org.gcube.rest.resourceawareservice.exceptions.ResourceAwareServiceException;
import org.gcube.rest.resourceawareservice.exceptions.ResourceNotFoundException;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Path("/")
@Singleton
public class IndexService extends ResourceAwareService<IndexResource> implements
		IndexServiceAPI {

	private final IndexDiscovererAPI<IndexResource> indexDiscoverer;
	private final Provider<IndexClient.Builder> clientProvider;
	final IndexResourceFactory factory;
	static final Logger logger = LoggerFactory.getLogger(IndexService.class);

	private String scope;
	
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	@Override
	public String getScope() {
		return scope;
	}

	@Override
	public String getResourceClass() {
		return Constants.RESOURCE_CLASS;
	}

	@Override
	public String getResourceNamePref() {
		return Constants.RESOURCE_NAME_PREF;
	}
	

	@Inject
	public IndexService(
			IndexResourceFactory factory,
			ResourcePublisher<IndexResource> publisher,
			IndexDiscovererAPI<IndexResource> indexDiscoverer,
			Provider<IndexClient.Builder> clientProvider,
			IResourceFilter<IndexResource> resourceFilter,
			IResourceFileUtils<IndexResource> resourceFileUtils)
			throws ResourceAwareServiceException {
		super(factory, publisher, resourceFilter, resourceFileUtils);
		
		this.factory = factory;
		this.indexDiscoverer = indexDiscoverer;
		this.clientProvider = clientProvider;
		logger.info("In IndexService constructor");
		
	}

	@GET
	@Path(value = "/{id}/feedLocator")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response feedLocator(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) final String scope,
			@PathParam("id") String resourceID,
			@QueryParam("rsuri") final String resultSetLocation,
			@QueryParam("indexName") final String indexName,
			@DefaultValue("false") @QueryParam("activate") final Boolean activate,
			@QueryParam("sids") final Set<String> sids,
			@DefaultValue("false") @QueryParam("block") final Boolean block) {

		logger.info("in feed locator");
		logger.info("feedLocator activate : " + activate);
		logger.info("feedLocator resultSetLocation : " + resultSetLocation);
		logger.info("feedLocator activate : " + activate);

		String msg = null;
		Response.Status status = null;

		IndexResource resource = null;
		try {
			resource = this.getResource(resourceID);
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found");
			status = Response.Status.NOT_FOUND;
			return Response.status(status).entity(msg).build();
		}

		final String clusterID = resource.getClusterID();
		logger.info("clusterID = " + clusterID);
		final String key = resourceID;
		logger.info("resource key in feeding : " + key);

		final IndexDiscovererAPI<IndexResource> idxDiscoverer = this.indexDiscoverer;
		final IndexResource fresource = resource;
		final IndexClient.Builder indexClientBuilder = this.clientProvider.get();
		final String serviceScope = this.scope;
		try {
			Thread feedThread = new Thread() {
				final FullTextNode fulltextnode = factory.getIndexNode(fresource);
				@Override
				public void run() {
					try {
						Boolean result = null;
						if (indexName != null)
							result = fulltextnode.feedLocator(resultSetLocation,
									indexName, sids);
						else
							result = fulltextnode.feedLocator(resultSetLocation,
									sids);
						logger.info("Feeding completed, result was: " + result);
						if (result) {
							if (activate) {
								boolean activateResult = fulltextnode
										.activateIndex(indexName);
								logger.info("Activate result was: "
										+ activateResult);
								updateAllResourcesCollectionsAndFields(
										fulltextnode, 
										indexClientBuilder,
										fresource.getClusterID(), 
										idxDiscoverer,
										serviceScope);
							}
						} else {
							logger.warn("feeding failed. deleting temp index with name : " + indexName);
							
							try {
								boolean deleteResult = fulltextnode.deleteTempIndex(indexName);
								if (deleteResult == false){
									logger.warn("error while deleting index : " + indexName);	
								}
							} catch (Exception ex) {
								logger.warn("error while deleting index : " + indexName, ex);
							}
						}
						logger.info("Properties updating completed");
					} catch (Exception e) {
						logger.error("error while feeding", e);
					}
				}
			};
			
			feedThread.start();
			
			logger.info("will feed block? " + block);
			
			if (block){
				try {
					feedThread.join();
					
//					logger.info("feed locator thread completed. Will now sleep for 20 minutes");
//					Thread.sleep(20 * 60 * 1000);
//					logger.info("feed locator woke up after 20 minutes of sleep");
					
				} catch (InterruptedException e) {
					logger.warn("error while waiting feed to complete", e);
					msg = JSONConverter.convertToJSON("Error", "error while waiting feed to complete ");
					status = Response.Status.INTERNAL_SERVER_ERROR;
					
					return Response.status(status).entity(msg).build();
				}
			}
			
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found");
			status = Response.Status.NOT_FOUND;
			return Response.status(status).entity(msg).build();
		}
		logger.info("feed locator completed");
		
		msg = JSONConverter.convertToJSON("result", Boolean.TRUE);
		status = Response.Status.OK;
		
		Response resp = Response
				.status(status)
				.entity(msg)
				.build();
		logger.info("feed block? " + block + " will return " + msg);

		return resp;
	}

	@POST
	@Path(value = "/{id}/query")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED+";charset=UTF-8")
	@GZIP
	public Response query(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@FormParam("queryString") String queryString,
			@DefaultValue("0") @FormParam("from") Integer from,
			@DefaultValue("-1") @FormParam("count") Integer count,
			@FormParam("sids") Set<String> sids,
			@DefaultValue("true") @FormParam("useRR") Boolean useRR,
			@DefaultValue("false") @FormParam("result") Boolean result,
			@DefaultValue("false") @FormParam("stream") Boolean stream,
			@DefaultValue("false") @FormParam("pretty") final Boolean pretty) {

		Object msg = null;
		Response.Status status = null;

		try {
			logger.info("queryString : " + queryString);
			logger.info("from : " + from);
			logger.info("count : " + count);
			logger.info("sids : " + sids);

			final IndexResource resource = this.getResource(resourceID);
			final String grslocator = this.factory.getIndexNode(resource)
					.query(queryString, from, count, sids, useRR);

			if (result) {

				if (stream) {
					StreamingOutput stm = new StreamingOutput() {
						@Override
						public void write(OutputStream output)
								throws IOException, WebApplicationException {
							Writer writer = new BufferedWriter(
									new OutputStreamWriter(output));

							try {
								ResultReader.streamResultSetToJsonRecords(
										writer, grslocator, pretty);
							} catch (Exception e) {
								e.printStackTrace();
								throw new WebApplicationException(e);
							}
							writer.flush();
							writer.close();
						}
					};
					msg = stm;
					status = Response.Status.OK;
				} else {
					msg = ResultReader.resultSetToJsonRecords(grslocator,
							pretty);
					status = Response.Status.OK;
				}
			} else {
				msg = JSONConverter.convertToJSON("grslocator", grslocator);
				status = Response.Status.OK;
			}
		} catch (Exception e) {
			logger.error("error while querying", e);
			msg = JSONConverter.convertToJSON("Error",
					"error while querying : " + e.getMessage());
			status = Response.Status.INTERNAL_SERVER_ERROR;

		}
		return Response.status(status).entity(msg).build();
	}
	
	@GET
	@Path(value = "/{id}/frequentTerms")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response frequentTerms(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("queryString") String queryString,
			@DefaultValue("10") @QueryParam("maxTerms") Integer maxTerms,
			@QueryParam("sids") Set<String> sids,
			@DefaultValue("true") @QueryParam("useRR") Boolean useRR,
			@DefaultValue("false") @QueryParam("pretty") final Boolean pretty){
		
		String msg = null;
		Response.Status status = null;

		IndexResource resource = null;
		try {
			resource = this.getResource(resourceID);
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found");
			status = Response.Status.NOT_FOUND;
			return Response.status(status).entity(msg).build();
		}
		
		try{
			Map<String, Integer> hits = this.factory.getIndexNode(resource).frequentTerms(queryString, maxTerms, sids, useRR);
	
			msg = JSONConverter.convertToJSON(hits, pretty);
			status = Status.OK;
		} catch (Exception e) {
			logger.error("error while getting frequent terms for query : " + queryString, e);
			
			msg = JSONConverter.convertToJSON("Error",
					"error while querying : " + e.getMessage());
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}
		
		return Response.status(status).entity(msg).build();
	}
	
	@GET
	@Path(value = "/{id}/clustering")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response clustering(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("queryString") String queryString,
			@DefaultValue("") @QueryParam("queryHint") String queryHint,
			@QueryParam("clustersCount") Integer clustersCount,
			@QueryParam("urlField") String urlField,
			@QueryParam("titleFields") List<String> titleFields,
			@QueryParam("contentFields") List<String> contentFields,
			@QueryParam("languageFields") List<String> languageFields,
			@DefaultValue("kmeans") @QueryParam("algorithm") String algorithm,
			@DefaultValue("100") @QueryParam("searchHits") Integer searchHits,
			@QueryParam("sids") Set<String> sids,
			@DefaultValue("true") @QueryParam("useRR") Boolean useRR,
			@DefaultValue("false") @QueryParam("pretty") final Boolean pretty){
		
		String msg = null;
		Response.Status status = null;

		IndexResource resource = null;
		try {
			resource = this.getResource(resourceID);
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found");
			status = Response.Status.NOT_FOUND;
			return Response.status(status).entity(msg).build();
		}
		
		try{
			List<ClusterResponse> hits = this.factory
					.getIndexNode(resource)
					.cluster(queryString, queryHint, clustersCount, urlField, titleFields, contentFields, languageFields, sids, algorithm, searchHits);
	
			msg = JSONConverter.convertToJSON(hits, pretty);
			status = Status.OK;
		} catch (Exception e) {
			logger.error("error while getting cluster for query : " + queryString, e);
			
			msg = JSONConverter.convertToJSON("Error",
					"error while querying : " + e.getMessage());
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}
		
		return Response.status(status).entity(msg).build();
		
	}

	@GET
	@Path(value = "/{id}/refresh")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response refresh(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID) {
		String msg = null;
		Response.Status status = null;

		IndexResource resource = null;
		try {
			resource = this.getResource(resourceID);
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found");
			status = Response.Status.NOT_FOUND;
			return Response.status(status).entity(msg).build();
		}

		try {
			logger.info("invalidating tha cache");
			this.factory.getIndexNode(resource).invalidateCache();
			logger.info("recreating meta-index");
			this.factory.getIndexNode(resource).recreateMetaIndex();
			logger.info("refreshing index types of index");
			this.factory.getIndexNode(resource).refreshIndexTypesOfIndex();

			
			logger.info("getting meta index values for existing (recreated) meta-index");
			List<String> collectionsOfMetaIndex = null;
			List<String> fieldsOfMetaIndex = null;
			try {
				collectionsOfMetaIndex = this.factory.getIndexNode(resource).getCollectionsFromMeta();
				fieldsOfMetaIndex = this.factory.getIndexNode(resource).getFieldsFromMeta();
				
			} catch (Exception e) {
				logger.warn("query to the meta index failed. Collection and fields will be considered null and will be updated be the next update", e);
			}
			
			logger.info("resource will have collections : " + collectionsOfMetaIndex);
			logger.info("resource will have fields      : " + fieldsOfMetaIndex);
			
			if (collectionsOfMetaIndex != null && fieldsOfMetaIndex != null) {
				resource.setCollections(collectionsOfMetaIndex);
				resource.setFields(fieldsOfMetaIndex);
			} else {
				resource.setCollections(Collections.<String> emptyList());
				resource.setFields(Collections.<String> emptyList());
			}

			logger.info("saving resource " + resourceID + "...");
			this.saveResource(resourceID);
			logger.info("saving resource " + resourceID + "...OK");
			
			msg = JSONConverter.convertToJSON("response", Boolean.TRUE);
			status = Response.Status.OK;
		} catch (Exception e) {
			logger.error("error while refershing the index", e);
			msg = JSONConverter.convertToJSON("response", Boolean.FALSE);
			status = Response.Status.BAD_REQUEST;
		}
		return Response.status(status).entity(msg).build();
	}

	@GET
	@Path(value = "/{id}/rebuildMetaIndex")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response rebuildMetaIndex(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID) {

		String msg = null;
		Response.Status status = null;

		IndexResource resource = null;
		try {
			resource = this.getResource(resourceID);
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found");
			status = Response.Status.NOT_FOUND;
			return Response.status(status).entity(msg).build();
		}

		try {

			List<String> collections = resource.getCollections();
			List<String> fields = resource.getFields();

			this.factory.getIndexNode(resource).rebuildMetaIndex(collections,
					fields);
			
			logger.info("saving resource");
			this.saveResource(resourceID);

			msg = JSONConverter.convertToJSON("response", Boolean.TRUE);
			status = Response.Status.OK;
		} catch (Exception e) {
			logger.error("error while rebuilding the meta index", e);
			msg = JSONConverter.convertToJSON("response", Boolean.FALSE);
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}
		return Response.status(status).entity(msg).build();
	}

	@GET
	@Path(value = "/{id}/activateIndex")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response activateIndex(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("indexName") String indexName) {

		String msg = null;
		Response.Status status = null;

		IndexResource resource = null;
		try {
			resource = this.getResource(resourceID);
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found");
			status = Response.Status.NOT_FOUND;
			return Response.status(status).entity(msg).build();
		}

		try {

			boolean activateResult = this.factory.getIndexNode(resource)
					.activateIndex(indexName);
			logger.info("Activate result was: " + activateResult);

			if (activateResult) {
				try {
					updateAllResourcesCollectionsAndFields(
							this.factory.getIndexNode(resource),
							this.clientProvider.get(), 
							resource.getClusterID(),
							this.indexDiscoverer,
							this.scope);
					status = Response.Status.OK;
					msg = JSONConverter.convertToJSON("response", Boolean.TRUE);
				} catch (Exception e) {
					logger.error("Error while updating manager properties", e);
					status = Response.Status.INTERNAL_SERVER_ERROR;
					msg = JSONConverter
							.convertToJSON("response", Boolean.FALSE);
				}
			} else {
				status = Response.Status.OK;
				msg = JSONConverter.convertToJSON("response", Boolean.FALSE);
			}

		} catch (Exception e) {
			logger.error("error while activating the index with name : "
					+ indexName, e);

			msg = JSONConverter.convertToJSON("response", Boolean.FALSE);
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}
		return Response.status(status).entity(msg).build();
	}
	
	@GET
	@Path(value = "/{id}/deactivateIndex")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response deactivateIndex(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("indexName") String indexName) {

		String msg = null;
		Response.Status status = null;

		IndexResource resource = null;
		try {
			resource = this.getResource(resourceID);
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found");
			status = Response.Status.NOT_FOUND;
			return Response.status(status).entity(msg).build();
		}

		try {

			boolean deactivateResult = this.factory.getIndexNode(resource)
					.deactivateIndex(indexName);
			logger.info("Deactivate result was: " + deactivateResult);

			if (deactivateResult) {
				try {
					updateAllResourcesCollectionsAndFields(
							this.factory.getIndexNode(resource),
							this.clientProvider.get(), 
							resource.getClusterID(),
							this.indexDiscoverer,
							this.scope);
					status = Response.Status.OK;
					msg = JSONConverter.convertToJSON("response", Boolean.TRUE);
				} catch (Exception e) {
					logger.error("Error while updating manager properties", e);
					status = Response.Status.INTERNAL_SERVER_ERROR;
					msg = JSONConverter
							.convertToJSON("response", Boolean.FALSE);
				}
			} else {
				status = Response.Status.OK;
				msg = JSONConverter.convertToJSON("response", Boolean.FALSE);
			}

		} catch (Exception e) {
			logger.error("error while activating the index with name : "
					+ indexName, e);

			msg = JSONConverter.convertToJSON("response", Boolean.FALSE);
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}
		return Response.status(status).entity(msg).build();
	}
	
	@GET
	@Path(value = "/{id}/flush")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response flush(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID) {

		String msg = null;
		Response.Status status = null;

		IndexResource resource = null;
		try {
			resource = this.getResource(resourceID);
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found");
			status = Response.Status.NOT_FOUND;
			return Response.status(status).entity(msg).build();
		}

		try {
			try {
				updateAllResourcesCollectionsAndFields(
						this.factory.getIndexNode(resource),
						this.clientProvider.get(), 
						resource.getClusterID(),
						this.indexDiscoverer,
						this.scope);
				status = Response.Status.OK;
				msg = JSONConverter.convertToJSON("response", Boolean.TRUE);
			} catch (Exception e) {
				logger.error("Error while updating manager properties", e);
				status = Response.Status.INTERNAL_SERVER_ERROR;
				msg = JSONConverter
						.convertToJSON("response", Boolean.FALSE);
			}

		} catch (Exception e) {
			logger.error("error while flushing", e);

			msg = JSONConverter.convertToJSON("response", Boolean.FALSE);
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}
		return Response.status(status).entity(msg).build();
	}

	@GET
	@Path(value = "/{id}/deleteIndex")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response deleteIndex(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("indexName") String indexName) {

		String msg = null;
		Response.Status status = null;

		IndexResource resource = null;
		try {
			resource = this.getResource(resourceID);
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found");
			status = Response.Status.NOT_FOUND;
			return Response.status(status).entity(msg).build();
		}

		try {

			boolean deleteIndexResult = this.factory.getIndexNode(resource)
					.deleteIndex(indexName);
			logger.info("DeleteIndex result was: " + deleteIndexResult);

			if (deleteIndexResult) {
				try {
					updateAllResourcesCollectionsAndFields(
							this.factory.getIndexNode(resource),
							this.clientProvider.get(), resource.getClusterID(),
							this.indexDiscoverer,
							this.scope);
					status = Response.Status.OK;
					msg = JSONConverter.convertToJSON("response", Boolean.TRUE);
				} catch (Exception e) {
					logger.error("Error while updating manager properties", e);
					status = Response.Status.INTERNAL_SERVER_ERROR;
					msg = JSONConverter
							.convertToJSON("response", Boolean.FALSE);
				}
			} else {
				status = Response.Status.OK;
				msg = JSONConverter.convertToJSON("response", Boolean.FALSE);
			}

		} catch (Exception e) {
			logger.error("error while deleting the index with name : "
					+ indexName, e);

			msg = JSONConverter.convertToJSON("response", Boolean.FALSE);
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}
		return Response.status(status).entity(msg).build();
	}

	@GET
	@Path(value = "/{id}/collectionCount")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response collectionCount(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("collectionID") String collectionID) {

		String msg = null;
		Response.Status status = null;
		IndexResource resource = null;

		Long collectionCount = null;
		try {
			resource = this.getResource(resourceID);
			collectionCount = this.factory.getIndexNode(resource)
					.getCollectionDocumentsCount(collectionID);
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found");
			status = Response.Status.NOT_FOUND;
			return Response.status(status).entity(msg).build();
		}

		logger.info("collectionCount for collection :  " + collectionID
				+ " returned : " + collectionCount);

		msg = JSONConverter.convertToJSON("response",
				collectionCount.toString());
		status = Response.Status.OK;
		return Response.status(status).entity(msg).build();
	}

	@GET
	@Path(value = "/{id}/collectionsOfIndex")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response collectionsOfIndex(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("indexName") String indexName) {

		String msg = null;
		Response.Status status = null;

		IndexResource resource = null;
		Set<String> collections = null;
		try {
			resource = this.getResource(resourceID);
			collections = this.factory.getIndexNode(resource)
					.getCollectionsOfIndex(indexName);
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found");
			status = Response.Status.NOT_FOUND;
			return Response.status(status).entity(msg).build();
		}

		logger.info("collections of index : " + indexName + " found : "
				+ collections);

		msg = JSONConverter.convertToJSON("response", collections);
		status = Response.Status.OK;
		return Response.status(status).entity(msg).build();
	}

	@GET
	@Path(value = "/{id}/indicesOfCollection")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response indicesOfCollection(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("collectionID") String collectionID) {

		String msg = null;
		Response.Status status = null;

		IndexResource resource = null;
		Set<String> indices = null;
		try {
			resource = this.getResource(resourceID);
			indices = this.factory.getIndexNode(resource)
					.getIndicesOfCollection(collectionID);
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found");
			status = Response.Status.NOT_FOUND;
			return Response.status(status).entity(msg).build();
		}

		logger.info("indices of collection : " + collectionID + " found : "
				+ indices);

		msg = JSONConverter.convertToJSON("response", indices);
		status = Response.Status.OK;
		return Response.status(status).entity(msg).build();
	}

	@POST
	@Path(value = "/{id}/setCollectionsAndFields")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response setCollectionsAndFields(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@FormParam("collections") List<String> collections,
			@FormParam("fields") List<String> fields) {
		String msg = null;
		Response.Status status = null;
		try {
			logger.info("collections : " + collections);
			logger.info("fields : " + fields);

			final IndexResource resource = this.getResource(resourceID);
			if (resource == null) {
				msg = JSONConverter.convertToJSON("Error",
						"Resource with ID : " + resourceID + " not found");
				status = Response.Status.NOT_FOUND;

			} else {

				resource.setCollections(collections);
				resource.setFields(fields);

				this.saveResource(resourceID);

				msg = JSONConverter.convertToJSON("Status",
						"Resource with ID : " + resourceID + " updated");
				status = Response.Status.OK;
			}
		} catch (Exception e) {
			logger.error(
					"error while setting collections and fields at resource with id : "
							+ resourceID, e);
			msg = JSONConverter.convertToJSON("Error",
					"Collections and field could not be set in resource with ID : "
							+ resourceID);
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}
		return Response.status(status).entity(msg).build();
	}

	@GET
	@Path(value = "/{id}/shutdown")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response shutdown(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@DefaultValue("false") @QueryParam("delete") Boolean delete) {
		String msg = null;
		Response.Status status = null;

		IndexResource resource = null;
		try {
			resource = this.getResource(resourceID);
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found");
			status = Response.Status.NOT_FOUND;
			return Response.status(status).entity(msg).build();
		}

		try {
			if (delete) {
				logger.error("shutting down + delete resource with ID : "
						+ resourceID);
				this.factory.getIndexNode(resource).deleteAll();

				logger.error("updating resource collections and fields resource with ID : "
						+ resourceID);
				clearResourcesCollectionsAndFields(this.clientProvider.get(),
						resource.getClusterID(), this.indexDiscoverer, scope);
			}

			this.factory.getIndexNode(resource).close();

			msg = JSONConverter.convertToJSON("response", Boolean.TRUE);
			status = Response.Status.OK;

		} catch (Exception e) {
			logger.error("error while shutting down node with resource ID : "
					+ resourceID, e);

			msg = JSONConverter.convertToJSON("response", Boolean.FALSE);
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}

		return Response.status(status).entity(msg).build();
	}

	@GET
	@Path(value = "/{id}/destroyCluster")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response destroyCluster(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID) {
		String msg = null;
		Response.Status status = null;

		IndexResource resource = null;
		Boolean delete = null;
		try {
			resource = this.getResource(resourceID);
			delete = this.factory.getIndexNode(resource).deleteAll();
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found");
			status = Response.Status.NOT_FOUND;
			return Response.status(status).entity(msg).build();
		}

		logger.info("deleting all indices...");
		if (delete == false) {
			status = Response.Status.INTERNAL_SERVER_ERROR;
			msg = JSONConverter.convertToJSON("response", false);

			return Response.status(status).entity(msg).build();
		}

		logger.info("deleting all indices...OK");

		String currentEndpoint = null;

		status = Response.Status.OK;
		boolean hasFailed = false;

		try {
			Map<String, Set<String>> resources = this.indexDiscoverer
					.discoverFulltextIndexNodes(resource.getClusterID(), null, null, this.scope);
			
			logger.info("will destroy the resources in : " + resources);

			for (Entry<String, Set<String>> resourceEnpdoint : resources
					.entrySet()) {
				String endpoint = resourceEnpdoint.getKey();
				for (String rID : resourceEnpdoint.getValue()) {
					if (resourceID.equalsIgnoreCase(rID)) {
						currentEndpoint = endpoint;
						continue;
					}

					logger.info("Calling destroy on resource ID : " + rID
							+ "...");
					IndexClient client = this.clientProvider
							.get()
							.scope(resource.getScope())
							.endpoint(endpoint)
							.resourceID(rID)
							.build();
					try {
						if (client.destroy() == false) {
							hasFailed = true;
							logger.warn("destroy on resource ID : " + rID + "failed");
						} else{
							logger.info("Calling destroy on resource ID : " + rID
								+ "...OK");
						}
					} catch (Exception e){
						hasFailed = true;
						logger.warn("destroy on resource ID : " + rID + "failed", e);
					}
				}
				status = Response.Status.OK;
			}
		} catch (Exception e) {
			logger.error(
					"error while destroying resources of the cluster with id : "
							+ resource.getClusterID(), e);
			hasFailed = true;
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}

		if (hasFailed)
			status = Response.Status.INTERNAL_SERVER_ERROR;
		else {
			try {
				if (currentEndpoint == null) {
					logger.warn("current endpoing not found. cant call destroy");
				} else {
					IndexClient client = this.clientProvider
							.get()
							.scope(resource.getScope())
							.endpoint(currentEndpoint)
							.resourceID(resourceID)
							.build();
					
					
					if (client.destroy() == false) {
						hasFailed = true;
						status = Response.Status.INTERNAL_SERVER_ERROR;
					}
				}
			} catch (Exception e) {
				logger.error("error while destroying local resource with id : "
						+ resourceID, e);
				hasFailed = true;
				status = Response.Status.INTERNAL_SERVER_ERROR;
			}
		}

		msg = JSONConverter.convertToJSON("response", !hasFailed);

		return Response.status(status).entity(msg).build();
	}
	
	@GET
	@Path(value = "/{id}/deleteDocuments")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response deleteDocuments(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("docIDs") List<String> docIDs){
		
		String msg = null;
		Response.Status status = null;

		IndexResource resource = null;
		Boolean delete = null;
		try {
			resource = this.getResource(resourceID);
			delete = this.factory.getIndexNode(resource).deleteDocuments(docIDs);
			
			if (delete){
				status = Response.Status.OK;
				msg = JSONConverter.convertToJSON("response", Boolean.TRUE);
			} else {
				status = Response.Status.INTERNAL_SERVER_ERROR;
				msg = JSONConverter.convertToJSON("response", Boolean.FALSE);
			}
			
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found");
			status = Response.Status.NOT_FOUND;
			return Response.status(status).entity(msg).build();
		}
		
		return Response.status(status).entity(msg).build();
	}

	// ////////////////////////
	// static methods
	// ////////////////////////

	public static void updateAllResourcesCollectionsAndFields(
			final FullTextNode fulltextnode, final IndexClient.Builder clientBuilder,
			String clusterID, IndexDiscovererAPI<IndexResource> indexDiscoverer, String scope)
			throws Exception {

//		logger.info("will update all the available resources from the (current) meta index");
//		List<String> collectionsOfIndex = fulltextnode.getCollectionsFromMeta();
//		List<String> fieldsOfIndex = fulltextnode.getFieldsFromMeta();
//
//		if (collectionsOfIndex == null || fieldsOfIndex == null) {
//			throw new Exception("no meta-index document found");
//		}
//
//		logger.info("fields to be added: " + fieldsOfIndex);
//		logger.info("Collections to be added: " + collectionsOfIndex);
		
		logger.info("will discover index nodes with clusterID : " + clusterID);

		Map<String, Set<String>> endpoints = indexDiscoverer
				.discoverFulltextIndexNodes(clusterID, null, null, scope);

		logger.info("found endpoints : " + endpoints);

		if (endpoints == null || endpoints.size() == 0) {
			logger.warn("no endpoints found to update");
			return;
		}

		for (String endpoint : endpoints.keySet()) {
			for (String key : endpoints.get(endpoint)) {
				logger.info("Recreating resource " + endpoint + " " + key);
				try {
					IndexClient client = clientBuilder
						.scope(fulltextnode.getScope())
						.endpoint(endpoint)
						.resourceID(key)
						.build();
					
//					client.setCollectionsAndFields(collectionsOfIndex,
//							fieldsOfIndex);
					
					client.refresh();

				} catch (Exception e) {
					logger.error("Exception", e);
					throw e;
				}
			}
		}

	}

	private static void clearResourcesCollectionsAndFields(
			final IndexClient.Builder clientBuilder, String clusterID,
			IndexDiscovererAPI<IndexResource> indexDiscoverer, String scope) throws Exception {

		List<String> collectionsOfIndex = new ArrayList<String>();
		List<String> fieldsOfIndex = new ArrayList<String>();

		logger.info("fields to be added: " + fieldsOfIndex);
		logger.info("Collections to be added: " + collectionsOfIndex);

		Map<String, Set<String>> endpoints = indexDiscoverer
				.discoverFulltextIndexNodes(clusterID, null, null, scope);

		logger.info("found endpoints : " + endpoints);

		if (endpoints == null || endpoints.size() == 0) {
			logger.warn("no endpoints found to clear");
			return;
		}

		for (String endpoint : endpoints.keySet()) {
			for (String key : endpoints.get(endpoint)) {
				logger.info("Recreating resource " + endpoint + " " + key);
				try {
					IndexClient client = clientBuilder
							.scope(scope)
							.endpoint(endpoint)
							.resourceID(key)
							.build();
					
					client.setCollectionsAndFields(collectionsOfIndex,
							fieldsOfIndex);
					
					//client.refresh();
				} catch (Exception e) {
					logger.error("Exception", e);
					throw e;
				}
			}
		}

	}
	
}
