package org.gcube.rest.index.common.apis;

import java.util.List;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.rest.commons.resourceawareservice.ResourceAwareServiceRestAPI;
import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.jboss.resteasy.annotations.GZIP;

public interface IndexServiceAPI extends ResourceAwareServiceRestAPI {

	@GET
	@Path(value = "/{id}/feedLocator")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response feedLocator(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("rsuri") final String resultSetLocation,
			@QueryParam("indexName") final String indexName,
			@DefaultValue("false") @QueryParam("activate") final Boolean activate,
			@QueryParam("sids") final Set<String> sids,
			@DefaultValue("false") @QueryParam("block") final Boolean block);

	@POST
	@Path(value = "/{id}/query")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; " + "charset=UTF-8")
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
			@DefaultValue("false") @FormParam("pretty") final Boolean pretty);
	
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
			@DefaultValue("false") @QueryParam("pretty") final Boolean pretty);

	
	@GET
	@Path(value = "/{id}/frequentTerms")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response frequentTerms(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("queryString") String queryString,
			@DefaultValue("0") @QueryParam("maxTerms") Integer maxTerms,
			@QueryParam("sids") Set<String> sids,
			@DefaultValue("true") @QueryParam("useRR") Boolean useRR,
			@DefaultValue("false") @QueryParam("pretty") final Boolean pretty);

	@GET
	@Path(value = "/{id}/refresh")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response refresh(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID);

	@GET
	@Path(value = "/{id}/rebuildMetaIndex")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response rebuildMetaIndex(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID);

	@GET
	@Path(value = "/{id}/activateIndex")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response activateIndex(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("indexName") String indexName);
	
	@GET
	@Path(value = "/{id}/deactivateIndex")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response deactivateIndex(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("indexName") String indexName);

	@GET
	@Path(value = "/{id}/deleteDocuments")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response deleteDocuments(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("docIDs") List<String> docIDs);
	
	@GET
	@Path(value = "/{id}/deleteIndex")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response deleteIndex(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("indexName") String indexName);

	@GET
	@Path(value = "/{id}/collectionCount")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response collectionCount(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("collectionID") String collectionID);

	@GET
	@Path(value = "/{id}/collectionsOfIndex")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response collectionsOfIndex(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("indexName") String indexName);

	@GET
	@Path(value = "/{id}/indicesOfCollection")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response indicesOfCollection(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("collectionID") String collectionID);

	@POST
	@Path(value = "/{id}/setCollectionsAndFields")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response setCollectionsAndFields(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@FormParam("collections") List<String> collections,
			@FormParam("fields") List<String> fields);

	@GET
	@Path(value = "/{id}/shutdown")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response shutdown(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@DefaultValue("false") @QueryParam("delete") Boolean delete);

	@GET
	@Path(value = "/{id}/destroyCluster")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response destroyCluster(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID);

	@GET
	@Path(value = "/{id}/flush")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response flush(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID);
	// @DELETE
	// @Path(value = RESOURCES_SERVLET_PATH + "/{id}")
	// @Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	// public Response destroy(
	// @HeaderParam(SCOPE_HEADER) String scope,
	// @PathParam("id") String resourceID);
}
