package org.gcube.data_catalogue.grsf_publish_ws.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.others.DeleteRecord;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.others.UpdateRecordStatus;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.record.StockRecord;
import org.gcube.data_catalogue.grsf_publish_ws.json.output.ResponseBean;
import org.gcube.data_catalogue.grsf_publish_ws.json.output.ResponseCreationBean;
import org.gcube.data_catalogue.grsf_publish_ws.utils.CommonServiceUtils;
import org.gcube.data_catalogue.grsf_publish_ws.utils.HelperMethods;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.shared.ResourceBean;
import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;
import org.gcube.datacatalogue.common.Constants;
import org.gcube.datacatalogue.common.enums.Product_Type;
import org.gcube.datacatalogue.common.enums.Sources;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanDataset;

/**
 * Stock web service methods.
 * @author Costantino Perciante (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
@Path("{source:firms|FIRMS|ram|RAM|grsf|GRSF|FishSource|fishsource}/stock/")
public class GrsfPublisherStockService {
	
	// the context
	@Context
	ServletContext contextServlet;
	
	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GrsfPublisherStockService.class);
	
	@GET
	@Path("hello")
	@Produces(MediaType.TEXT_PLAIN)
	public Response hello() {
		return Response.ok("Hello.. Stock service is here").build();
	}
	
	@GET
	@Path("get-licenses")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLicenses() {
		Status status = Status.OK;
		String context = ScopeProvider.instance.get();
		DataCatalogue catalogue;
		try {
			catalogue = HelperMethods.getDataCatalogueRunningInstance(context);
			Map<String,String> licenses = CommonServiceUtils.getLicenses(catalogue);
			if(licenses == null)
				status = Status.INTERNAL_SERVER_ERROR;
			return Response.status(status).entity(licenses).build();
		} catch(Exception e) {
			status = Status.INTERNAL_SERVER_ERROR;
			return Response.status(status)
					.entity(new ResponseBean(false, "Unable to retrieve license list " + e.getLocalizedMessage(), null))
					.build();
		}
	}
	
	@POST
	@Path("publish-product")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response publishStock(@NotNull(message = "record cannot be null") @Valid StockRecord record,
			@PathParam("source") String source) throws ValidationException {
		
		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();
		String token = SecurityTokenProvider.instance.get();
		
		logger.info("Incoming request for creating a stock record = " + record + ".\nRequest coming from user "
				+ username + " in context " + context);
		
		ResponseCreationBean responseBean = new ResponseCreationBean();
		Status status = Status.INTERNAL_SERVER_ERROR;
		String id = ""; // id of the created record, if everything went ok
		
		try {
			
			// Cast the source to the accepted ones
			Sources sourceInPath = Sources.onDeserialize(source);
			
			DataCatalogue catalogue = HelperMethods.getDataCatalogueRunningInstance(context);
			if(catalogue == null) {
				throw new Exception("There was a problem while serving your request. No catalogue instance was found!");
			} else {
				
				String apiKey = catalogue.getApiKeyFromUsername(username);
				String organization = HelperMethods.retrieveOrgNameFromScope(context); //"grsf_admin";
				
				// check it has admin role or throw exception
				CommonServiceUtils.hasAdminRole(username, catalogue, apiKey, organization);
				
				// extend this role to the other organizations in this context
				CommonServiceUtils.extendRoleToOtherOrganizations(username, catalogue, organization,
						RolesCkanGroupOrOrg.ADMIN);
				
				// retrieve the user's email and fullname
				String authorMail = HelperMethods.getUserEmail(context, token);
				String authorFullname = HelperMethods.getUserFullname(context, token);
				
				if(authorMail == null || authorFullname == null) {
					throw new Exception("Sorry but it was not possible to retrieve your fullname/email!");
				}
				
				// check the record has a name, at least
				String futureName = record.getUuid();
				String futureTitle = record.getStockName();
				
				// check name and throws exception
				CommonServiceUtils.checkName(futureName, catalogue);
				
				// load other information
				Map<String,List<String>> customFields = record.getExtrasFields();
				Set<String> tags = new HashSet<String>();
				Set<String> groups = new HashSet<String>();
				List<ResourceBean> resources = record.getExtrasResources();
				
				// validate end set sources, tags, etc
				CommonServiceUtils.validateRecordAndMapFields(apiKey, context, contextServlet, sourceInPath, record,
						Product_Type.STOCK, tags, customFields, groups, resources, username, futureTitle);
				
				// check the license id
				String license = null;
				if(record.getLicense() == null || record.getLicense().isEmpty())
					license = Constants.DEFAULT_LICENSE;
				else if(HelperMethods.existsLicenseId(record.getLicense(), catalogue))
					license = record.getLicense();
				else
					throw new Exception("Please check the license id!");
				
				// check the version
				long version = record.getVersion() == null ? 1 : record.getVersion();
				
				// set the visibility of the datatest according the context
				boolean publicDataset = context
						.equals((String) contextServlet.getInitParameter(HelperMethods.PUBLIC_CONTEX_KEY));
				
				// convert extras' keys to keys with namespace
				Map<String,String> namespaces = HelperMethods
						.getFieldToFieldNameSpaceMapping(Constants.GENERIC_RESOURCE_NAME_MAP_KEY_NAMESPACES_STOCK);
				
				if(namespaces == null)
					throw new Exception("Failed to retrieve the namespaces for the key fields!");
				
				customFields = HelperMethods.replaceFieldsKey(customFields, namespaces,
						!sourceInPath.equals(Sources.GRSF));
				
				String publishInOrganization = CommonServiceUtils.evaluateOrganization(organization, sourceInPath);
				
				logger.info("Invoking create method..");
				
				// create the product 
				id = catalogue.createCKanDatasetMultipleCustomFields(apiKey, futureTitle, futureName,
						publishInOrganization, authorFullname, authorMail,
						record.getMaintainer() == null ? authorFullname : record.getMaintainer(),
						record.getMaintainerContact() == null ? authorMail : record.getMaintainerContact(), version,
						HelperMethods.removeHTML(record.getDescription(), false), license, new ArrayList<String>(tags),
						customFields, resources, publicDataset);
				
				if(id != null) {
					
					logger.info("Product created! Id is " + id);
					String description = Constants.SHORT_NAME_CUSTOM_KEY + ": " + record.getShortName() + "\n";
					
					if(sourceInPath.equals(Sources.GRSF))
						description += Constants.GRSF_SEMANTIC_IDENTIFIER_CUSTOM_KEY + ": " + record.getStockId()
								+ "\n";
					
					CommonServiceUtils.actionsPostCreateOrUpdate(id, futureName, record, apiKey, username, organization,
							null, responseBean, catalogue, namespaces, groups, context, token, futureTitle,
							authorFullname, contextServlet, false, description);
					
					status = Status.CREATED;
					
				} else
					throw new Exception(
							"There was an error during the product generation, sorry! Unable to create the dataset");
			}
		} catch(Exception e) {
			logger.error("Failed to create stock record", e);
			status = Status.INTERNAL_SERVER_ERROR;
			responseBean.setError(e.getMessage());
		}
		return Response.status(status).entity(responseBean).build();
	}
	
	@DELETE
	@Path("delete-product")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteStock(@NotNull(message = "missing input value") @Valid DeleteRecord recordToDelete,
			@PathParam("source") String source) throws ValidationException {
		
		// retrieve context and username
		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();
		
		ResponseCreationBean responseBean = new ResponseCreationBean();
		Status status = Status.INTERNAL_SERVER_ERROR;
		
		// check it is a stock ...
		logger.info(
				"Received call to delete product with id " + recordToDelete.getId() + ", checking if it is a stock");
		try {
			
			DataCatalogue catalogue = HelperMethods.getDataCatalogueRunningInstance(context);
			if(catalogue == null) {
				status = Status.INTERNAL_SERVER_ERROR;
				throw new Exception("There was a problem while serving your request");
			}
			
			// Cast the source to the accepted ones
			Sources sourceInPath = Sources.onDeserialize(source);
			String apiKey = catalogue.getApiKeyFromUsername(username);
			logger.info("The request is to delete a stock object of source " + sourceInPath);
			
			// retrieve the catalogue instance
			CkanDataset stockInCkan = catalogue.getDataset(recordToDelete.getId(), apiKey);
			
			if(stockInCkan == null) {
				status = Status.NOT_FOUND;
				throw new Exception("There was a problem while serving your request. This product was not found");
			}
			
			// check it is in the right source and it is a stock
			String type = stockInCkan.getExtrasAsHashMap().get(Constants.DOMAIN_CUSTOM_KEY);
			if((stockInCkan.getOrganization().getName().equalsIgnoreCase(source)
					|| stockInCkan.getOrganization().getName().toLowerCase().contains(source))
					&& Product_Type.STOCK.getOrigName().equals(type)) {
				
				logger.debug("Ok, this is a stock of the right type, removing it");
				boolean deleted = catalogue.deleteProduct(stockInCkan.getId(), apiKey, true);
				if(deleted) {
					logger.info("Stock DELETED AND PURGED!");
					status = Status.OK;
					responseBean.setId(stockInCkan.getId());
				} else {
					status = Status.INTERNAL_SERVER_ERROR;
					throw new Exception("Request failed, sorry. Unable to delete/purge the stock");
				}
			} else {
				status = Status.BAD_REQUEST;
				throw new Exception(
						"The id you are using doesn't belong to a Stock product having source " + source + "!");
			}
		} catch(Exception e) {
			logger.error("Failed to delete this ", e);
			responseBean.setError(e.getMessage());
		}
		return Response.status(status).entity(responseBean).build();
	}
	
	@GET
	@Path("get-stocks-ids")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStocksIds(@PathParam("source") String source) {
		
		// retrieve context and username
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		String username = caller.getClient().getId();
		Status status = Status.INTERNAL_SERVER_ERROR;
		ResponseBean responseBean = new ResponseBean();
		
		// check it is a stock ...
		logger.info("Received call to get stocks with source " + source);
		List<String> datasetsIds = new ArrayList<String>();
		try {
			
			// Cast the source to the accepted ones
			Sources sourceInPath = Sources.onDeserialize(source);
			DataCatalogue catalogue = HelperMethods.getDataCatalogueRunningInstance(context);
			if(catalogue == null) {
				status = Status.INTERNAL_SERVER_ERROR;
				throw new Exception("There was a problem while serving your request");
			}
			
			// if it is a request for GRSF records, we have Fishery - Stock groups, so it is easy.
			// For other cases, records needs to be parsed
			if(sourceInPath.equals(Sources.GRSF))
				datasetsIds = HelperMethods.getProductsInGroup(source + "-" + "stock", catalogue);
			else {
				List<String> fullGroupListIds = HelperMethods.getProductsInOrganization(source, catalogue);
				for(String id : fullGroupListIds) {
					CkanDataset dataset = catalogue.getDataset(id, catalogue.getApiKeyFromUsername(username));
					if(dataset != null) {
						String type = dataset.getExtrasAsHashMap().get(Constants.DOMAIN_CUSTOM_KEY);
						if(Product_Type.STOCK.getOrigName().equals(type))
							datasetsIds.add(id);
					}
				}
			}
			responseBean.setResult(datasetsIds);
			responseBean.setSuccess(true);
			status = Status.OK;
		} catch(Exception e) {
			logger.error("Failed to fetch this list of ids " + source, e);
			responseBean.setMessage(e.getMessage());
		}
		return Response.status(status).entity(responseBean).build();
	}
	
	@GET
	@Path("get-catalogue-id-and-url-from-name")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCatalogueIdAndUrlFromKBID(@QueryParam("name") String name) {
		
		// retrieve context and username
		String context = ScopeProvider.instance.get();
		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.INTERNAL_SERVER_ERROR;
		logger.info("Received call to get the catalogue identifier for the product with name " + name);
		try {
			DataCatalogue catalogue = HelperMethods.getDataCatalogueRunningInstance(context);
			if(catalogue == null) {
				throw new Exception("There was a problem while serving your request");
			}
			CkanDataset dataset = catalogue.getDataset(name, catalogue.getApiKeyFromUsername(username));
			if(dataset != null) {
				Map<String,String> result = new HashMap<String,String>();
				result.put("id", dataset.getId());
				result.put("url", catalogue.getUnencryptedUrlFromDatasetIdOrName(dataset.getId()));
				responseBean.setResult(result);
				responseBean.setSuccess(true);
				status = Status.OK;
			} else {
				responseBean.setMessage("Unable to retrieve a catalogue product with name " + name);
			}
		} catch(Exception e) {
			logger.error("Failed to retrieve this product", e);
			responseBean.setMessage(e.getMessage());
		}
		return Response.status(status).entity(responseBean).build();
	}
	
	@POST
	@Path("update-product")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateStock(@NotNull(message = "record cannot be null") @Valid StockRecord record,
			@PathParam("source") String source) throws ValidationException {
		
		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();
		String token = SecurityTokenProvider.instance.get();
		
		logger.info("Incoming request for updating a stock record = " + record + ". Request comes from user " + username
				+ " in context " + context);
		
		ResponseCreationBean responseBean = new ResponseCreationBean();
		Status status = Status.INTERNAL_SERVER_ERROR;
		
		// catalog id must be reported
		String catalogId = record.getCatalogId();
		
		try {
			
			if(catalogId == null || catalogId.isEmpty()) {
				status = Status.BAD_REQUEST;
				throw new Exception("Please specify the 'catalog_id' property");
			}
			
			DataCatalogue catalogue = HelperMethods.getDataCatalogueRunningInstance(context);
			
			if(catalogue == null) {
				throw new Exception(
						"There was a problem while serving your request. No catalogue instance was found in this context!");
			} else {
				
				// get already published record and modify it
				String apiKey = catalogue.getApiKeyFromUsername(username);
				CkanDataset recordPublished = catalogue.getDataset(catalogId, apiKey);
				
				if(recordPublished == null)
					throw new Exception("A record with id " + catalogId + " does not exist!");
				
				// retrieve the user's email and fullname
				String authorMail = HelperMethods.getUserEmail(context, token);
				String authorFullname = HelperMethods.getUserFullname(context, token);
				
				if(authorMail == null || authorFullname == null) {
					logger.debug("Author fullname or mail missing, cannot continue");
					throw new Exception("Sorry but there was not possible to retrieve your fullname/email!");
				}
				String organization = HelperMethods.retrieveOrgNameFromScope(context); //"grsf_admin";
				
				// check he/she has admin role
				CommonServiceUtils.hasAdminRole(username, catalogue, apiKey, organization);
				
				// name, product url and  are going to remain unchanged (so we keep them from the publisher record);
				String name = recordPublished.getName();
				
				// The title must change if Stock Name change  
				//String title = recordPublished.getTitle();
				String title = record.getStockName();
				
				
				// Cast the source to the accepted ones
				Sources sourceInPath = Sources.onDeserialize(source);
				
				// load infos
				Map<String,List<String>> customFields = record.getExtrasFields();
				Set<String> tags = new HashSet<String>();
				Set<String> groups = new HashSet<String>();
				List<ResourceBean> resources = record.getExtrasResources();
				
				// validate end set sources
				CommonServiceUtils.validateRecordAndMapFields(apiKey, context, contextServlet, sourceInPath, record,
						Product_Type.STOCK, tags, customFields, groups, resources, username, title);
				
				// check the license id
				String license = null;
				if(record.getLicense() == null || record.getLicense().isEmpty())
					license = Constants.DEFAULT_LICENSE;
				else if(HelperMethods.existsLicenseId(record.getLicense(), catalogue))
					license = record.getLicense();
				else
					throw new Exception("Please check the license id!");
				
				long version = record.getVersion() == null ? 1 : record.getVersion();
				
				// set the visibility of the datatest according the context
				boolean publicDataset = context
						.equals((String) contextServlet.getInitParameter(HelperMethods.PUBLIC_CONTEX_KEY));
				
				// convert extras' keys to keys with namespace
				Map<String,String> namespaces = HelperMethods
						.getFieldToFieldNameSpaceMapping(Constants.GENERIC_RESOURCE_NAME_MAP_KEY_NAMESPACES_STOCK);
				
				if(namespaces == null)
					throw new Exception("Failed to retrieve the namespaces for the key fields!");
				
				// retrieve the url
				String modifiedUUIDKey = namespaces.containsKey(Constants.ITEM_URL_FIELD)
						? namespaces.get(Constants.ITEM_URL_FIELD)
						: Constants.ITEM_URL_FIELD;
				String itemUrl = recordPublished.getExtrasAsHashMap().get(modifiedUUIDKey);
				customFields.put(Constants.ITEM_URL_FIELD, Arrays.asList(itemUrl));
				
				// replace fields
				customFields = HelperMethods.replaceFieldsKey(customFields, namespaces,
						!sourceInPath.equals(Sources.GRSF));
				
				String publishInOrganization = CommonServiceUtils.evaluateOrganization(organization, sourceInPath);
				
				logger.info("Invoking update method..");
				
				// update the product 
				String id = catalogue.updateCKanDataset(apiKey, catalogId, title, name, publishInOrganization,
						authorFullname, authorMail, record.getMaintainer(), record.getMaintainerContact(), version,
						HelperMethods.removeHTML(record.getDescription(), false), license, new ArrayList<String>(tags), null, // remove any previous group 
						customFields, resources, publicDataset);
				
				if(id != null) {
					
					logger.info("Item updated!");
					
					String description = Constants.SHORT_NAME_CUSTOM_KEY + ": " + record.getShortName() + "\n";
					
					if(sourceInPath.equals(Sources.GRSF))
						description += Constants.GRSF_SEMANTIC_IDENTIFIER_CUSTOM_KEY + ": " + record.getStockId()
								+ "\n";
					CommonServiceUtils.actionsPostCreateOrUpdate(recordPublished.getId(), name, record, apiKey,
							username, organization, itemUrl, responseBean, catalogue, namespaces, groups, context,
							token, title, authorFullname, contextServlet, true, description);
					status = Status.OK;
					
				} else {
					throw new Exception("There was an error during the item updated, sorry");
				}
			}
		} catch(Exception e) {
			logger.error("Failed to update stock record", e);
			responseBean.setError(e.getMessage());
		}
		return Response.status(status).entity(responseBean).build();
	}
	
	@POST
	@Path("update-status")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateStatusStock(@Valid UpdateRecordStatus bean, @PathParam("source") String source)
			throws ValidationException {
		
		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();
		
		logger.info("Incoming request for updating a status of record = " + bean + ". Request comes from user "
				+ username + " in context " + context);
		
		Status status = Status.INTERNAL_SERVER_ERROR;
		
		ResponseCreationBean responseBean = new ResponseCreationBean();
		
		try {
			
			DataCatalogue catalogue = HelperMethods.getDataCatalogueRunningInstance(context);
			
			if(catalogue == null) {
				throw new Exception(
						"There was a problem while serving your request. No catalogue instance was found in this context!");
			} else {
				
				// catalog id must be reported
				String uuid = bean.getUuid();
				String newStatus = bean.getNewStatus().getOrigName();
				
				// get the dataset
				String apiKeyUser = catalogue.getApiKeyFromUsername(username);
				CkanDataset record = catalogue.getDataset(uuid, apiKeyUser);
				
				if(record == null)
					throw new Exception("A record with knowledge_base_id   id " + uuid + " does not exist!");
				
				// check system type
				boolean isGRSF = !record.getExtrasAsHashMap().get(Constants.SYSTEM_TYPE_CUSTOM_KEY)
						.equals(Constants.SYSTEM_TYPE_FOR_SOURCES_VALUE);
				
				if(!isGRSF)
					throw new Exception("You are trying to modify a Legacy record!");
				
				boolean rightDomain = record.getExtrasAsHashMap().get(Constants.DOMAIN_CUSTOM_KEY)
						.equals(Product_Type.STOCK.getOrigName());
				
				if(!rightDomain)
					throw new Exception("This is not a Stock record!");
				
				// update it
				Map<String,List<String>> updateStatus = new HashMap<String,List<String>>(1);
				updateStatus.put(Constants.STATUS_OF_THE_GRSF_RECORD_CUSTOM_KEY, Arrays.asList(newStatus));
				catalogue.patchProductCustomFields(uuid, apiKeyUser, updateStatus, true);
				status = Status.OK;
				responseBean.setError(null);
				responseBean.setKbUuid(uuid);
				responseBean.setId(record.getId());
				responseBean.setItemUrl(record.getExtrasAsHashMap().get(Constants.ITEM_URL_FIELD));
			}
		} catch(Exception e) {
			logger.error("Failed to update stock record's status", e);
			responseBean.setError(e.getMessage());
		}
		
		return Response.status(status).entity(responseBean).build();
		
	}
	
}