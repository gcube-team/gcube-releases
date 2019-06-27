package org.gcube.datatransfer.resolver.services;

import java.net.URL;
import java.util.concurrent.ExecutionException;

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

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.datatransfer.resolver.ConstantsResolver;
import org.gcube.datatransfer.resolver.caches.LoadingMapOfScopeCache;
import org.gcube.datatransfer.resolver.catalogue.CatalogueRequest;
import org.gcube.datatransfer.resolver.catalogue.ItemCatalogueURLs;
import org.gcube.datatransfer.resolver.catalogue.ResourceCatalogueCodes;
import org.gcube.datatransfer.resolver.catalogue.resource.CkanCatalogueConfigurationsReader;
import org.gcube.datatransfer.resolver.catalogue.resource.GatewayCKANCatalogueReference;
import org.gcube.datatransfer.resolver.catalogue.resource.GetAllInfrastructureScopes;
import org.gcube.datatransfer.resolver.services.error.ExceptionManager;
import org.gcube.datatransfer.resolver.util.Util;
import org.gcube.smartgears.utils.InnerMethodName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheLoader.InvalidCacheLoadException;

import eu.trentorise.opendata.jackan.model.CkanDataset;

/**
 * The Class CatalogueResolver.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Nov 16, 2018
 */
@Path("{entityContext:ctlg(-(o|g|p|d))?}")
public class CatalogueResolver {


	private static Logger logger = LoggerFactory.getLogger(CatalogueResolver.class);
	private static String helpURI = "https://wiki.gcube-system.org/gcube/URI_Resolver#CATALOGUE_Resolver";

	/**
	 * Resolve catalogue.
	 *
	 * @param req the req
	 * @param entityName the entity name
	 * @param vreName the vre name
	 * @param entityContext the entity context
	 * @return the response
	 */
	@GET
	@Path("/{vreName}/{entityName}")
	public Response resolveCatalogue(@Context HttpServletRequest req,
		@PathParam("entityName") String entityName,
		@PathParam("vreName") String vreName,
		@PathParam("entityContext") String entityContext) throws WebApplicationException{

		logger.info(this.getClass().getSimpleName()+" GET starts...");

		try {
			InnerMethodName.instance.set("resolveCataloguePublicLink");
			ItemCatalogueURLs itemCatalogueURLs = getItemCatalogueURLs(req, vreName, entityContext, entityName);

			String itemCatalogueURL;

			if(itemCatalogueURLs.isPublicItem()){
				itemCatalogueURL = itemCatalogueURLs.getPublicCataloguePortletURL();
				logger.info("The dataset "+itemCatalogueURLs.getItemName()+" is a public item so using public access to CKAN portlet: "+itemCatalogueURL);
			}else{
				itemCatalogueURL = itemCatalogueURLs.getPrivateCataloguePortletURL();
				logger.info("The dataset "+itemCatalogueURLs.getItemName()+" is a private item so using protected access to CKAN portlet: "+itemCatalogueURL);
			}

			return Response.seeOther(new URL(itemCatalogueURL).toURI()).build();
		}catch (Exception e) {

			if(!(e instanceof WebApplicationException)){
				//UNEXPECTED EXCEPTION managing it as WebApplicationException
				String error = "Error occurred on resolving the Catalgoue URL. Please, contact the support!";
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
	 */
	@POST
	@Path("")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response postCatalogue(@Context HttpServletRequest req, CatalogueRequest jsonRequest) throws WebApplicationException{
		logger.info(this.getClass().getSimpleName()+" POST starts...");

		try{

			InnerMethodName.instance.set("postCataloguePublicLink");
			logger.info("The body contains the request: "+jsonRequest.toString());

			//CHECK IF INPUT SCOPE IS VALID
			String scope = jsonRequest.getGcube_scope();
			if(!scope.startsWith(ConstantsResolver.SCOPE_SEPARATOR)){
				logger.info("Scope not start with char '{}' adding it",ConstantsResolver.SCOPE_SEPARATOR);
				scope+=ConstantsResolver.SCOPE_SEPARATOR+scope;
			}

			String serverUrl = Util.getServerURL(req);

			final String vreName = scope.substring(scope.lastIndexOf(ConstantsResolver.SCOPE_SEPARATOR)+1, scope.length());
			ScopeBean fullScope = null;

			//CHECK IF THE vreName has a valid scope, so it is a valid VRE
			try {
				fullScope = LoadingMapOfScopeCache.get(vreName);
			}catch(ExecutionException e){
				logger.error("Error on getting the fullscope from cache for vreName "+vreName, e);
				throw ExceptionManager.wrongParameterException(req, "Error on getting full scope for the VRE name "+vreName+". Is it registered as VRE in the D4Science Infrastructure System?", this.getClass(), helpURI);
			}

			if(fullScope==null)
				throw ExceptionManager.notFoundException(req, "The scope '"+scope+"' does not matching any scope in the infrastructure. Is it valid?", this.getClass(), helpURI);

			ResourceCatalogueCodes rc = ResourceCatalogueCodes.valueOfCodeValue(jsonRequest.getEntity_context());
			if(rc==null){
				logger.error("Entity context is null/malformed");
				throw ExceptionManager.badRequestException(req, "Entity context is null/malformed", this.getClass(), helpURI);
			}

			String linkURL = String.format("%s/%s/%s/%s", serverUrl, rc.getId(), vreName, jsonRequest.getEntity_name());
			logger.info("Returining Catalogue URL: "+linkURL);
			return Response.ok(linkURL).header("Location", linkURL).build();

		}catch (Exception e) {

			if(!(e instanceof WebApplicationException)){
				//UNEXPECTED EXCEPTION managing it as WebApplicationException
				String error = "Error occurred on resolving the Analytics URL. Please, contact the support!";
				throw ExceptionManager.internalErrorException(req, error, this.getClass(), helpURI);
			}
			//ALREADY MANAGED AS WebApplicationException
			logger.error("Exception:", e);
			throw (WebApplicationException) e;
		}
	}

	/**
	 * Gets the item catalogue UR ls.
	 *
	 * @param req the req
	 * @param scopeName the scope name
	 * @param entityContext the entity context
	 * @param entityName the entity name
	 * @return the item catalogue UR ls
	 * @throws Exception the exception
	 */
	protected static ItemCatalogueURLs getItemCatalogueURLs(HttpServletRequest req, String scopeName, String entityContext, String entityName) throws Exception{

		try {
			String entityContextValue = ResourceCatalogueCodes.valueOfCodeId(entityContext).getValue();
			ScopeBean scopeBean = null;
			try{
				scopeBean = LoadingMapOfScopeCache.get(scopeName);
			}catch(ExecutionException | InvalidCacheLoadException e){
				logger.error("Error on getting the fullscope from cache for scopeName "+scopeName, e);
				throw ExceptionManager.wrongParameterException(req, "Error on getting full scope for the scope name '"+scopeName+"'. Is it registered as a valid Scope in the D4Science Infrastructure System?", CatalogueResolver.class, helpURI);
			}
			
			String fullScope = scopeBean.toString();
			logger.info("Read fullScope: "+fullScope + " for SCOPE name: "+scopeName +" from cache created by: "+GetAllInfrastructureScopes.class.getSimpleName());
			
			if(scopeBean.is(Type.VO)) {
				logger.info("It is a {} scope", Type.VO);
				logger.warn("The Catalogue can't work at {} level, I'm overriding the scope to {} level", Type.VO, Type.INFRASTRUCTURE);
				String[] splitScope = fullScope.split(ConstantsResolver.SCOPE_SEPARATOR);
				fullScope = ConstantsResolver.SCOPE_SEPARATOR+splitScope[1]; //THIS IS THE INFRASTRUCTURE SCOPE
				logger.info("Overriden the input scope {} with {} as type: {}", scopeBean.toString(), Type.INFRASTRUCTURE, fullScope);
			}

			ScopeProvider.instance.set(fullScope);
			GatewayCKANCatalogueReference ckanCatalogueReference = CkanCatalogueConfigurationsReader.loadCatalogueEndPoints();

			logger.info("For scope "+fullScope+" loaded end points: "+ckanCatalogueReference);

			//IS THE PRODUCT PLUBLIC OR PRIVATE?
			String datasetName = entityName;
			boolean isPublicItem = false;
			if(ckanCatalogueReference.getCkanURL()!=null){
				try{
					CkanDataset dataset = CkanCatalogueConfigurationsReader.getDataset(datasetName, ckanCatalogueReference.getCkanURL());
					if(dataset!=null){
						isPublicItem = true;
						//ckanPorltetUrl = ckanCatalogueReference.getPublicPortletURL();
						logger.info("The dataset "+datasetName+" is a public item");
					}
				}catch(Exception e){
					logger.warn("Error on checking if dataset: "+datasetName+" is private or not", e);
					isPublicItem = true;
				}
			}

			String publicPorltetURL = String.format("%s?path=/%s/%s",ckanCatalogueReference.getPublicPortletURL(),entityContextValue, entityName);
			String privatePortletURL = String.format("%s?path=/%s/%s",ckanCatalogueReference.getPrivatePortletURL(),entityContextValue, entityName);
			return new ItemCatalogueURLs(entityName, isPublicItem, privatePortletURL, publicPorltetURL);
		}catch (Exception e) {
			logger.error("Error when resolving CatalogueURL:", e);
			throw e;
		}

	}


}
