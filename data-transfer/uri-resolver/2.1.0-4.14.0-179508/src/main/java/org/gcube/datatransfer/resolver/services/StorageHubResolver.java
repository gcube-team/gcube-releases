package org.gcube.datatransfer.resolver.services;

import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.plugins.AbstractPlugin;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.datatransfer.resolver.services.error.ExceptionManager;
import org.gcube.datatransfer.resolver.util.StorageHubMetadataResponseBuilder;
import org.gcube.smartgears.utils.InnerMethodName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class StorageHubResolver.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Dec 14, 2018
 */
@Path("shub")
public class StorageHubResolver {

	/**
	 *
	 */
	public static final String STORAGE_HUB_ID = "id";

	private static Logger logger = LoggerFactory.getLogger(StorageHubResolver.class);

	private String help = "https://wiki.gcube-system.org/gcube/URI_Resolver#STORAGE-HUB_Resolver";

	@RequestScoped
	@PathParam(STORAGE_HUB_ID)
	String id;


	/**
	 * Gets the metadata.
	 *
	 * @param req the req
	 * @return the metadata
	 */
	@HEAD
	@Path("/{id}")
	public Response getMetadata(@Context HttpServletRequest req) {
		logger.info(this.getClass().getSimpleName()+" HEAD getMetadata called");

		try{
			//TODO Do we need to check the token?

			//Checking mandatory parameter id
			if(id==null || id.isEmpty()){
				logger.error("Path Parameter "+STORAGE_HUB_ID+" not found");
				throw ExceptionManager.badRequestException(req, "Missing mandatory path parameter "+STORAGE_HUB_ID, this.getClass(), help);
			}

			try{

				ItemManagerClient client = AbstractPlugin.item().build();
				StreamDescriptor descriptor = client.resolvePublicLink(id);
				ResponseBuilder response = Response.noContent();
				response = new StorageHubMetadataResponseBuilder(req, response).fillMetadata(descriptor, id);
				return response.build();

			}catch(Exception e){
				logger.error("Error on getting metadata for file with "+id, e);
				String errorMsg =  "Error on getting metadata for file with hub id '"+id+"'. "+e.getMessage();
				throw ExceptionManager.internalErrorException(req, errorMsg, this.getClass(), help);
			}

		}catch (Exception e) {

			if(!(e instanceof WebApplicationException)){
				//UNEXPECTED EXCEPTION managing it as WebApplicationException
				String error = "Error occurred on resolving the StorageHub URL with id: "+id+". Please, contact the support!";
				throw ExceptionManager.internalErrorException(req, error, this.getClass(), help);
			}
			//ALREADY MANAGED as WebApplicationException
			logger.error("Exception:", e);
			throw (WebApplicationException) e;
		}
	}


	/**
	 * Download.
	 *
	 * @param req the req
	 * @return the response
	 */
	@GET
	@Path("/{id}")
	public Response download(@Context HttpServletRequest req) {
		logger.info(this.getClass().getSimpleName()+" GET download called");

		try{
			InnerMethodName.instance.set("resolveStorageHubPublicLink");

			//Checking mandatory parameter id
			if(id==null || id.isEmpty()){
				logger.error("Path Parameter "+STORAGE_HUB_ID+" not found");
				throw ExceptionManager.badRequestException(req, "Missing mandatory path parameter "+STORAGE_HUB_ID, StorageHubResolver.class, help);
			}

			try{

				ItemManagerClient client = AbstractPlugin.item().build();
				StreamDescriptor descriptor = client.resolvePublicLink(id);
				ResponseBuilder response = Response.ok(descriptor.getStream());

				response = new StorageHubMetadataResponseBuilder(req, response).fillMetadata(descriptor, id);
				return response.build();

			}catch(Exception e){
				logger.error("Error on getting file with "+id, e);
				String errorMsg =  "Error on getting file with hub id '"+id+"'. "+e.getMessage();
				throw ExceptionManager.internalErrorException(req, errorMsg, StorageHubResolver.class, help);
			}

		}catch (Exception e) {

			if(!(e instanceof WebApplicationException)){
				//UNEXPECTED EXCEPTION managing it as WebApplicationException
				String error = "Error occurred on resolving the StorageHub URL with id: "+id+". Please, contact the support!";
				throw ExceptionManager.internalErrorException(req, error, this.getClass(), help);
			}
			//ALREADY MANAGED AS WebApplicationException
			logger.error("Exception:", e);
			throw (WebApplicationException) e;
		}
	}
}
