/**
 *
 */
package org.gcube.datatransfer.resolver.services;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.gcube.datatransfer.resolver.catalogue.ItemCatalogueURLs;
import org.gcube.datatransfer.resolver.catalogue.ResourceCatalogueCodes;
import org.gcube.datatransfer.resolver.init.UriResolverSmartGearManagerInit;
import org.gcube.datatransfer.resolver.parthenos.ParthenosRequest;
import org.gcube.datatransfer.resolver.services.error.ExceptionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class PartheosRegistryResolver.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 16, 2018
 */
@Path("parthenos_registry")
public class PartheosRegistryResolver {

	private static Logger logger = LoggerFactory.getLogger(PartheosRegistryResolver.class);
	private static String helpURI = "https://wiki.gcube-system.org/gcube/URI_Resolver#Parthenos_URL_Resolver";

	/**
	 * Resolve parthenos url.
	 *
	 * @param req the req
	 * @param provider the provider
	 * @param path the path
	 * @param remainPath the remain path
	 * @return the response
	 */
	@GET
	@Path("/{provider}/{path}{remainPath:(/[^?$]+)?}")
	public Response resolveParthenosURL(@Context HttpServletRequest req,
		@PathParam("provider") String provider,
		@PathParam("path") String path,
		@PathParam("remainPath") String remainPath) throws WebApplicationException {

		logger.info(this.getClass().getSimpleName()+" GET starts...");
		String remainPathParthenosURL = null;

		try {

			logger.debug("provider is: "+provider);
			logger.debug("path is: "+path);
			logger.debug("remainPath is: "+remainPath);

			remainPathParthenosURL = String.format("%s/%s",provider,path);

			if(remainPath!=null && !remainPath.isEmpty()){
				remainPathParthenosURL+=remainPath.startsWith("/")?remainPath:"/"+remainPath;
			}

			logger.info("Resolving parthenos URL: "+remainPathParthenosURL);
			//APPLYING URL DECODING
			remainPathParthenosURL = URLDecoder.decode(remainPathParthenosURL, "UTF-8");
			//APPLYING NAME TRANSFORMATION
			String normalizedEntityName = toNameForCatalogue(remainPathParthenosURL);
			logger.info("Trying to resolve with Catalogue EntityName: "+normalizedEntityName);
			ItemCatalogueURLs itemCatalogueURLs = CatalogueResolver.getItemCatalogueURLs(req, UriResolverSmartGearManagerInit.getParthenosVREName(), ResourceCatalogueCodes.CTLGD.getId(), normalizedEntityName);
			return Response.seeOther(new URL(itemCatalogueURLs.getPrivateCataloguePortletURL()).toURI()).build();

		}catch (Exception e) {

			if(!(e instanceof WebApplicationException)){
				//UNEXPECTED EXCEPTION managing it as WebApplicationException
				String error = "Error occurred on resolving the path "+remainPathParthenosURL+". Please, contact the support!";
				if(e.getCause()!=null)
					error+="\n\nCaused: "+e.getCause().getMessage();
				throw ExceptionManager.internalErrorException(req, error, this.getClass(), helpURI);
			}
			//ALREADY MANAGED AS WebApplicationException
			logger.error("Exception:", e);
			throw (WebApplicationException) e;
		}

	}


	/**
	 * Post catalogue.
	 *
	 * @param req the req
	 * @param jsonRequest the json request
	 * @return the response
	 * @throws Exception the exception
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response postCatalogue(@Context HttpServletRequest req, ParthenosRequest jsonRequest) throws WebApplicationException{
		logger.info(this.getClass().getSimpleName()+" POST starts...");

		try{
			String entityName = jsonRequest.getEntity_name();

			if(entityName==null || entityName.isEmpty()){
				logger.error("Entity Name Parameter like 'entity_name' not found or empty");
				throw ExceptionManager.badRequestException(req, "Mandatory body parameter 'entity_name' not found or empty", this.getClass(), helpURI);
			}

			//REMOVING FIRST '/' IF EXISTS
			entityName = entityName.startsWith("/")?entityName.substring(1,entityName.length()):entityName;

			//APPLYING NAME TRANSFORMATION
			String normalizedEntityName = toNameForCatalogue(entityName);
			ItemCatalogueURLs itemCatalogueURLs = CatalogueResolver.getItemCatalogueURLs(req, UriResolverSmartGearManagerInit.getParthenosVREName(), ResourceCatalogueCodes.CTLGD.getId(), normalizedEntityName);
			logger.info("Returining Catalogue URL: "+itemCatalogueURLs.getPrivateCataloguePortletURL());
			return Response.ok(normalizedEntityName).header("Location", itemCatalogueURLs.getPrivateCataloguePortletURL()).build();

		}catch (Exception e) {

			if(!(e instanceof WebApplicationException)){
				//UNEXPECTED EXCEPTION managing it as WebApplicationException
				String error = "Error occurred on transforming the "+jsonRequest+". Please, contact the support!";
				throw ExceptionManager.internalErrorException(req, error, this.getClass(), helpURI);
			}
			//ALREADY MANAGED AS WebApplicationException
			logger.error("Exception:", e);
			throw (WebApplicationException) e;
		}
	}


	/**
	 * To name for catalogue.
	 * this method applyes a fuction to transform a parthenos URL to acceptable catalogue name (that is URL)
	 * @param remainPathParthenosURL the remain path parthenos url
	 * @return the string
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	protected String toNameForCatalogue(final String remainPathParthenosURL) throws UnsupportedEncodingException {
		//need to double decode for URLs like: http://parthenos.d4science.org/handle/Parthenos/REG/Dataset/Appellation/Isidore%2520Dataset
		String name = StringUtils.replaceChars(URLDecoder.decode(remainPathParthenosURL,"UTF-8"),"/ .:", "_").toLowerCase().replaceAll("[^A-Za-z0-9]", "_");
		//TO LOWERCASE FOR CKAN SUPPORTING
		return name.toLowerCase();
	}

	//TO TEST
	//	public static void main(String[] args) throws UnsupportedEncodingException {
	//
	//		String remainPathParthenosURL = "Culturalitalia/unknown/Dataset/oai%3Aculturaitalia.it%3Aoai%3Aculturaitalia.it%3Amuseiditalia-mus_11953";
	//		System.out.println(URLDecoder.decode(remainPathParthenosURL, "UTF-8"));
	//
	//	}
}
