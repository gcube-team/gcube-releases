//package org.gcube.rest.index.common.apis;
//
//
//import javax.ws.rs.Consumes;
//import javax.ws.rs.DELETE;
//import javax.ws.rs.DefaultValue;
//import javax.ws.rs.FormParam;
//import javax.ws.rs.GET;
//import javax.ws.rs.HeaderParam;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//
//@Path("/")
//public interface IndexServiceI {
//
//	
//	@GET
//    @Path("/listCollections")
//	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
//	public Response listCollections(@QueryParam("collectionDomain") String collectionDomain, @HeaderParam("gcube-scope") String gcubeScope) ;
//	
//	
//	@GET
//    @Path("/getAllCollectionFields")
//	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
//	public Response getAllCollectionFields(@DefaultValue("true") @QueryParam("aliasFields") boolean aliasFields, @HeaderParam("gcube-scope") String gcubeScope);
//	
//	/**
//	 * Inserts a document in the specified index (collectionID). If recordID is null or empty, it will be assigned an auto-generated
//	 * 
//	 * @param collectionID the name of the collection - it will map to the index name
//	 * @param recordID  if null or empty, index assigns it an auto-generated 
//	 * @param recordJSON  the document in json format
//	 * @return true if successfull, false otherwise
//	 */
//	@POST
//	@Path("/insert/{collectionID}/{recordID}")
//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//    public Response insert(
//    		@PathParam("collectionID") String collectionID,
//    		@PathParam("recordID") String recordID,
//    		@FormParam(value = "recordJSON") String recordJSON,
//    		@HeaderParam("gcube-scope") String gcubeScope);
//	
//	
//	/**
//	 * Inserts a document in the specified index (collectionID). The new document will be assigned an auto-generated ID
//	 * 
//	 * @param collectionID the name of the collection - it will map to the index name
//	 * @param recordJSON  the document in json format
//	 * @return true if successfull, false otherwise
//	 */
//	@POST
//	@Path("/insert/{collectionID}")
//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//    public Response insert(
//    		@PathParam("collectionID") String collectionID,
//    		@FormParam(value = "recordJSON") String recordJSON,
//    		@HeaderParam("gcube-scope") String gcubeScope);
//	
//	
//	@DELETE
//	@Path("/delete/{collectionID}/{recordID}")
//    public Response delete(
//    		@PathParam("collectionID") String collectionID,
//    		@PathParam("recordID") String recordID ,
//    		@HeaderParam("gcube-scope") String gcubeScope);
//	
//	
//	@DELETE
//	@Path("/dropCollection/{collectionID}")
//    public Response dropCollection(@PathParam("collectionID") String collectionID,@HeaderParam("gcube-scope") String gcubeScope);
//	
//	@GET
//    @Path("/listFulltextEndpoints")
//	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
//    public Response listFulltextEndpoints(@QueryParam("scope") String scope,@HeaderParam("gcube-scope") String gcubeScope);
//	
//	
//	
//	@GET
//    @Path("/getCollectionFieldsAlias")
//	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
//	public Response getCollectionFieldsAlias(@QueryParam("collectionID") String collectionID, @DefaultValue("true") @QueryParam("fromIndexToView") boolean fromIndexToView, @HeaderParam("gcube-scope") String gcubeScope);
//	
//	@POST
//    @Path("/setCollectionFieldsAlias")
//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
//    public Response setCollectionFieldsAlias(@QueryParam("collectionID") String collectionID, @FormParam(value = "mappingsJSON") String mappingsJSON, @HeaderParam("gcube-scope") String gcubeScope);
//	
//	@DELETE
//    @Path("/deleteCollectionFieldsAlias")
//	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
//    public Response deleteCollectionFieldsAlias(@QueryParam("collectionID") String collectionID, @HeaderParam("gcube-scope") String gcubeScope);
//	
//	
//	@GET
//    @Path("/getJSONTransformer")
//	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
//    public Response getJSONTransformer(@QueryParam("collectionID") String collectionID, @HeaderParam("gcube-scope") String gcubeScope);
//	
//	
//	@POST
//    @Path("/setJSONTransformer")
//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
//    public Response setJSONTransformer(
//    		@QueryParam("collectionID") String collectionID,
//    		@FormParam(value = "transformerJSON") String transformerJSON,
//    		@HeaderParam("gcube-scope") String gcubeScope);
//	
//	@DELETE
//    @Path("/deleteJSONTransformer")
//	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
//    public Response deleteJSONTransformer(@QueryParam("collectionID") String collectionID, @HeaderParam("gcube-scope") String gcubeScope);
//
//	
//	@GET
//    @Path("/getCollectionInfo")
//	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
//    public Response getCollectionInfo(@QueryParam("collectionID") String collectionID, @HeaderParam("gcube-scope") String gcubeScope);
//	
//	
//	@POST
//    @Path("/setCollectionInfo")
//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
//    public Response setCollectionInfo(
//    		@QueryParam("collectionID") String collectionID,
//    		@FormParam(value = "infoJSON") String infoJSON,
//    		@HeaderParam("gcube-scope") String gcubeScope);
//	
//	@DELETE
//    @Path("/deleteCompleteCollectionInfo")
//	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
//    public Response deleteCompleteCollectionInfo(@QueryParam("collectionID") String collectionID, @HeaderParam("gcube-scope") String gcubeScope);
//	
//	
//	@POST
//    @Path("/search")
//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
//    public Response search(@FormParam(value = "query") String queryJson, @HeaderParam("gcube-scope") String gcubeScope);
//	
//}
