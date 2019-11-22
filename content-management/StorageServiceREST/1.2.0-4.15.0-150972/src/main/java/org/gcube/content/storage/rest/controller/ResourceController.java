package org.gcube.content.storage.rest.controller;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.gcube.content.storage.rest.service.ResourceService;
import org.gcube.content.storage.rest.utils.Utils;

/**
 * 
 * @author Roberto Cirillo (ISTI-CNR) 2017
 *
 */

@Path("/resources")
public class ResourceController {
	
	
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
	public List<String> getResources(@QueryParam("gcube_token") String token)
	{
    	ResourceService resourceService=initService(token);
		List<String> listOfResources=resourceService.getAllResources();
		return listOfResources;
	}

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_OCTET_STREAM)
	public String getResourceById(@PathParam("id") String id, @QueryParam("gcube_token") String token)
	{
    	ResourceService resourceService=initService(token);
		return resourceService.getResource(id);
	}
    

    @GET
    @Path("/ts/range")
    @Produces(MediaType.APPLICATION_JSON)
	public List<String> getResourcesRange(@QueryParam("gcube_token") String token, @QueryParam("timestamp") long ts, @QueryParam("range") int range)
	{
    	ResourceService resourceService=initService(token);
		List<String> listOfResources=resourceService.getRange(ts, range);
		return listOfResources;
	}

    
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
////    @Produces(MediaType.APPLICATION_OCTET_STREAM)
//	public String getResourceById(@QueryParam("gcube_token") String token, @QueryParam("gcube_token") String name, @QueryParam("gcube_token") String value)
//	{
//    	ResourceService resourceService=initService(token);
//		return resourceService.getResource(name, value);
//	}

    
    /**
     * Method used for adding a new json document
     * @param resource json document
     * @return
     */
   
    @POST
    @Produces(MediaType.APPLICATION_JSON)
	public String addResource(String resource, @QueryParam("gcube_token") String token)
	{
    	ResourceService resourceService=initService(token);
		return resourceService.addResource(resource);
	}
    
    /**
     * Used for updating an existing json document
     * @param resource json document
     * @return
     */
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public String updateResource(String resource, @PathParam("id") String id, @QueryParam("gcube_token") String token)
	{
    	ResourceService resourceService=initService(token);
		return resourceService.updateResource(id, resource);
	}

	/**
	 * used for deleting a json document by id
	 * @param id document's identifier
	 */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public void deleteResource(@PathParam("id") String id, @QueryParam("gcube_token") String token)
	{
    	ResourceService resourceService=initService(token);
		 resourceService.deleteResource(id);
	}

    
	/**
	 * used for deleting a json document by id
	 * @param id document's identifier
	 */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
	public void deleteResources(@QueryParam("gcube_token") String token)
	{
    	ResourceService resourceService=initService(token);
		resourceService.deleteAllResources();
		
	}

    
	private ResourceService initService(String token) {
		if(Utils.isValid(token)){
			ResourceService resourceService=new ResourceService( token);
			return resourceService;
		}else{
			throw new RuntimeException("You are not allowed to execute this method. Please contact the system administrator.");
		}
    	
	}

	
}
