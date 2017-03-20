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
import org.gcube.data_catalogue.grsf_publish_ws.json.input.Common;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.DeleteProductBean;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.FisheryRecord;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.RefersToBean;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.Resource;
import org.gcube.data_catalogue.grsf_publish_ws.json.output.ResponseBean;
import org.gcube.data_catalogue.grsf_publish_ws.json.output.ResponseCreationBean;
import org.gcube.data_catalogue.grsf_publish_ws.utils.HelperMethods;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Product_Type;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Sources;
import org.gcube.data_catalogue.grsf_publish_ws.utils.threads.AssociationToGroupThread;
import org.gcube.data_catalogue.grsf_publish_ws.utils.threads.ManageTimeSeriesThread;
import org.gcube.data_catalogue.grsf_publish_ws.utils.threads.WritePostCatalogueManagerThread;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.models.ResourceBean;
import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanDataset;

/**
 * Fishery web service methods
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("{source:firms|FIRMS|ram|RAM|grsf|GRSF|FishSource|fishsource}/fishery/")
public class GrsfPublisherFisheryService {

	// the default license for these records
	private static final String DEFAULT_FISHERY_LICENSE = "CC-BY-SA-4.0";

	// item url property
	private static final String ITEM_URL_FIELD = "Item URL";

	// the context
	@Context ServletContext contextServlet;

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GrsfPublisherFisheryService.class);

	@GET
	@Path("hello")
	@Produces(MediaType.TEXT_PLAIN)
	public Response hello(){
		return Response.ok("Hello.. Fishery service is here").build();
	}

	@GET
	@Path("get-licenses")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLicenses(){

		Status status = Status.OK;
		Map<String, String> licenses = CommonServiceUtils.getLicenses();
		if(licenses == null)
			status = Status.INTERNAL_SERVER_ERROR;
		return Response.status(status).entity(licenses).build();

	}

	@POST
	@Path("publish-product")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response publishFishery(
			@NotNull(message="record cannot be null") 
			@Valid FisheryRecord record,
			@PathParam("source") String source) 
					throws ValidationException{

		// retrieve context and username
		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();
		String token = SecurityTokenProvider.instance.get();

		logger.info("Incoming request for creating a fishery record = " + record);
		logger.info("Request comes from user " + username + " in context " + context);

		ResponseCreationBean responseBean = new ResponseCreationBean();
		Status status = Status.INTERNAL_SERVER_ERROR;
		String id = "";

		try{

			// Cast the source to the accepted ones
			Sources sourceInPath = Sources.onDeserialize(source);

			DataCatalogue catalogue = HelperMethods.getDataCatalogueRunningInstance(context);
			if(catalogue == null){

				status = Status.INTERNAL_SERVER_ERROR;
				throw new Exception("There was a problem while serving your request. No catalogue instance was found!");

			}else{

				String apiKey = catalogue.getApiKeyFromUsername(username);
				String organization =  HelperMethods.retrieveOrgNameFromScope(context); //"grsf_admin";
				String role = catalogue.getRoleOfUserInOrganization(username, organization, apiKey);

				logger.info("Role of the user " + username + " is " + role);
				
				if(role == null || role.isEmpty())
					throw new Exception("You are not authorized to create a product. Please check you have the Catalogue-Administrator role!");

				if(!role.equalsIgnoreCase(RolesCkanGroupOrOrg.ADMIN.toString())){
					status = Status.FORBIDDEN;
					throw new Exception("You are not authorized to create a product. Please check you have the Catalogue-admin role!");
				}

				// The name of the product will be the uuid of the kb. The title will be the fishery's fishery_name. Fishery has also the constraint that
				// fishing area and jurisdiction area cannot be empty at the same time
				String futureName = record.getUuid();
				String futureTitle = record.getFisheryName();

				if(!HelperMethods.isNameValid(futureName)){

					status = Status.BAD_REQUEST;
					throw new Exception("The 'uuid_knowledge_base' must contain only alphanumeric characters, and symbols like '.' or '_', '-'");

				}else{

					logger.debug("Checking if such name [" + futureName + "] doesn't exist yet...");
					boolean alreadyExists = catalogue.existProductWithNameOrId(futureName);

					if(alreadyExists){

						logger.debug("A product with 'uuid_knowledge_base' " + futureName + " already exists");
						status = Status.CONFLICT;
						throw new Exception("A product with 'uuid_knowledge_base' " + futureName + " already exists");

					}else{

						// validate the record if it is a GRSF one and set the record type and in manage context
						// Status field is needed only in the Manage context for GRSF records
						if(context.equals((String)contextServlet.getInitParameter(HelperMethods.MANAGE_CONTEX_KEY))){
							if(sourceInPath.equals(Sources.GRSF)){
								//Evaluate the sources
								List<Resource<Sources>> recordSources = record.getDatabaseSources();
								String sources = "";
								for (Resource<Sources> resource : recordSources) {
									sources += resource.getName() + ", ";
								}

								sources = sources.endsWith(", ") ? sources.substring(0, sources.length() -2) : sources;
								record.setSourceType(sources);
								CommonServiceUtils.validateAggregatedRecord(record);

							}
						}

						// set the grsf type
						record.setGrsfType(Product_Type.FISHERY.getOrigName());

						// product system type is a list of values for sources records, so remove it (so that no group is generated)
						//						if(!sourceInPath.equals(Sources.GRSF))
						//							record.setProductionSystemType(null);

						// evaluate the custom fields/tags, resources and groups
						Map<String, List<String>> customFields = record.getExtrasFields();
						Set<String> tags = new HashSet<String>();						
						Set<String> groups = new HashSet<String>();
						groups.add(sourceInPath.getOrigName().toLowerCase() + "-" + Product_Type.FISHERY.getOrigName().toLowerCase()); //e.g. grsf-fishery
						List<ResourceBean> resources = record.getExtrasResources();
						boolean skipTags = !sourceInPath.equals(Sources.GRSF); // no tags for the Original records
						CommonServiceUtils.getTagsGroupsResourcesExtrasByRecord(tags, skipTags, groups, resources, customFields, record, username, sourceInPath);

						// manage the refers to
						if(sourceInPath.equals(Sources.GRSF)){
							List<RefersToBean> refersTo = record.getRefersTo();
							for (RefersToBean refersToBean : refersTo) {
								resources.add(new ResourceBean(refersToBean.getUrl(), "Source of item " + futureTitle + " in the catalogue has id: "
										+ refersToBean.getId(), "Information of a source of the item " + futureTitle, null, username, null, null));
							}
						}

						// retrieve the user's email and fullname
						String authorMail = HelperMethods.getUserEmail(context, token);
						String authorFullname = HelperMethods.getUserFullname(context, token);

						if(authorMail == null || authorFullname == null){

							logger.debug("Author fullname or mail missing, cannot continue");
							status = Status.INTERNAL_SERVER_ERROR;
							throw new Exception("Sorry but there was not possible to retrieve your fullname/email!");

						}else{

							// check the license id
							String license = null;
							if(record.getLicense() == null || record.getLicense().isEmpty())
								license = DEFAULT_FISHERY_LICENSE;
							else
								if(HelperMethods.existsLicenseId(record.getLicense(), catalogue))
									license = record.getLicense();
								else throw new Exception("Please check the license id!"); 

							long version = record.getVersion() == null ? 1 : record.getVersion();

							// set the visibility of the datatest according the context
							boolean publicDataset = context.equals((String)contextServlet.getInitParameter(HelperMethods.PUBLIC_CONTEX_KEY));

							logger.info("Invoking creation method..");

							// create the product 
							id = catalogue.createCKanDatasetMultipleCustomFields(
									apiKey, 
									futureTitle, 
									futureName,
									organization, 
									authorFullname, 
									authorMail, 
									record.getMaintainer(), 
									record.getMaintainerContact(), 
									version, 
									HelperMethods.removeHTML(record.getDescription()), 
									license, 
									new ArrayList<String>(tags), 
									customFields, 
									resources, 
									publicDataset); 

							if(id != null){

								logger.info("Item created! Id is " + id);
								responseBean.setId(id);
								status = Status.CREATED;
								String itemUrl = catalogue.getUnencryptedUrlFromDatasetIdOrName(futureName);
								responseBean.setItemUrl(itemUrl);
								responseBean.setKbUuid(record.getUuid());

								// add the "Product URL" to the field
								Map<String, List<String>> addField = new HashMap<String, List<String>>();
								addField.put(ITEM_URL_FIELD, Arrays.asList(itemUrl));
								catalogue.patchProductCustomFields(id, apiKey, addField);

								if(!groups.isEmpty()){
									logger.info("Launching thread for association to the list of groups " + groups);
									AssociationToGroupThread threadGroups = new AssociationToGroupThread(new ArrayList<String>(groups), id, organization, username, catalogue);
									threadGroups.start();
									logger.info("Waiting association thread to die..");
									threadGroups.join();
									logger.debug("Groups-Thread died");
								}

								// manage time series
								logger.info("Launching thread for time series handling");
								new ManageTimeSeriesThread(record, futureName, username, catalogue, context, token).start();

								// write a post if the product has been published in grsf context
								if(context.equals((String)contextServlet.getInitParameter(HelperMethods.PUBLIC_CONTEX_KEY))){
									new WritePostCatalogueManagerThread(
											context, 
											token, 
											futureTitle, 
											itemUrl, 
											false, 
											new ArrayList<String>(),  
											authorFullname).start();

									logger.info("Thread to write a post about the new product has been launched");
								}

							}else{

								throw new Exception("There was an error during the product generation, sorry");

							}
						}
					}
				}
			}
		}catch(Exception e){
			logger.error("Failed to create fishery record" + e);
			responseBean.setError(e.getMessage());
		}

		return Response.status(status).entity(responseBean).build();
	} 

	@DELETE
	@Path("delete-product")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteFishery(
			@NotNull(message="input value is missing") 
			@Valid DeleteProductBean recordToDelete,
			@PathParam("source") String source) throws ValidationException{

		// retrieve context and username
		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();

		ResponseCreationBean responseBean = new ResponseCreationBean();
		Status status = Status.INTERNAL_SERVER_ERROR;

		// check it is a fishery ...
		logger.info("Received call to delete product with id " + recordToDelete.getId() + ", checking if it is a fishery");
		try{

			DataCatalogue catalogue = HelperMethods.getDataCatalogueRunningInstance(context);
			if(catalogue == null){

				status = Status.INTERNAL_SERVER_ERROR;
				throw new Exception("There was a problem while serving your request");

			}

			// Cast the source to the accepted ones
			Sources sourceInPath = Sources.onDeserialize(source);
			logger.info("The request is to delete a fishery object of source " + sourceInPath);

			// retrieve the catalogue instance
			CkanDataset fisheryInCkan = catalogue.getDataset(recordToDelete.getId(), catalogue.getApiKeyFromUsername(username));

			if(fisheryInCkan == null){

				status = Status.NOT_FOUND;
				throw new Exception("There was a problem while serving your request. This item was not found");

			}

			// check it is in the right source and it is a fishery
			String grsfType = fisheryInCkan.getExtrasAsHashMap().get(Common.GRSF_TYPE_KEY);
			String groupToCheck = sourceInPath.equals(Sources.GRSF) ? "grsf-group" : sourceInPath.getOrigName().toLowerCase();
			
			if(catalogue.isDatasetInGroup(groupToCheck, recordToDelete.getId()) && Product_Type.FISHERY.getOrigName().equals(grsfType)){
				
				logger.warn("Ok, this is a fishery of the right source, removing it");
				boolean deleted = catalogue.deleteProduct(fisheryInCkan.getId(), catalogue.getApiKeyFromUsername(username), true);
				
				if(deleted){
					
					logger.info("Fishery DELETED AND PURGED!");
					status = Status.OK;
					responseBean.setId(fisheryInCkan.getId());
					
				}
				else{

					status = Status.INTERNAL_SERVER_ERROR;
					throw new Exception("Request failed, sorry. Unable to delete/purge the fishery");

				}

			}else{

				status = Status.BAD_REQUEST;
				throw new Exception("The id you are using doesn't belong to a Fishery item having source " + source + "!");

			}
		}catch(Exception e){

			logger.error("Failed to delete this", e);
			status = Status.INTERNAL_SERVER_ERROR;
			responseBean.setError(e.getMessage());

		}

		return Response.status(status).entity(responseBean).build();
	}

	@GET
	@Path("get-fisheries-ids")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFisheriesIds(
			@PathParam("source") String source){

		// retrieve context and username
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		String username = caller.getClient().getId();

		ResponseBean responseBean = new ResponseBean();
		Status status = Status.INTERNAL_SERVER_ERROR;

		logger.info("Received call to get fisheries with source " + source);

		List<String> datasetsIds = new ArrayList<String>();

		try{

			// Cast the source to the accepted ones
			Sources sourceInPath = Sources.onDeserialize(source);

			DataCatalogue catalogue = HelperMethods.getDataCatalogueRunningInstance(context);
			if(catalogue == null){

				status = Status.INTERNAL_SERVER_ERROR;
				throw new Exception("There was a problem while serving your request");

			}

			// if it is a request for GRSF records, we have Fishery - Stock groups, so it is easy.
			// For other cases, records needs to be parsed
			if(sourceInPath.equals(Sources.GRSF))
				datasetsIds = HelperMethods.getProductsInGroup(source + "-" + "fishery", catalogue);
			else{

				List<String> fullGroupListIds = HelperMethods.getProductsInGroup(source, catalogue);

				for (String id : fullGroupListIds) {

					CkanDataset dataset = catalogue.getDataset(id, catalogue.getApiKeyFromUsername(username));
					if(dataset != null){
						String grsfType = dataset.getExtrasAsHashMap().get(Common.GRSF_TYPE_KEY);
						if(grsfType.equals(Product_Type.FISHERY.getOrigName()))
							datasetsIds.add(id);
					}

				}

			}

			responseBean.setResult(datasetsIds);
			responseBean.setSuccess(true);

		}catch(Exception e){

			logger.error("Failed to fetch this list of ids ", e);
			status = Status.INTERNAL_SERVER_ERROR;
			responseBean.setMessage(e.getMessage());

		}

		return Response.status(status).entity(responseBean).build();
	}

	@GET
	@Path("get-catalogue-id-and-url-from-name")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCatalogueIdAndUrlFromKBID(
			@QueryParam("name") String name){

		// retrieve context and username
		String context = ScopeProvider.instance.get();
		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();

		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		logger.info("Received call to get the catalogue identifier for the product with name " + name);

		try{

			DataCatalogue catalogue = HelperMethods.getDataCatalogueRunningInstance(context);
			if(catalogue == null){
				throw new Exception("There was a problem while serving your request");
			}

			CkanDataset dataset = catalogue.getDataset(name, catalogue.getApiKeyFromUsername(username));
			if(dataset != null){

				Map<String, String> result = new HashMap<String, String>();
				result.put("id", dataset.getId());

				// retrieve the product url
				Map<String, String> customFields = dataset.getExtrasAsHashMap();
				if(customFields.containsKey(ITEM_URL_FIELD))
					result.put("url", customFields.get(ITEM_URL_FIELD));
				else
					result.put("url", catalogue.getUnencryptedUrlFromDatasetIdOrName(dataset.getId()));

				responseBean.setResult(result);
				responseBean.setSuccess(true);
			}else{
				responseBean.setMessage("Unable to retrieve a catalogue item with name " + name);
			}

		}catch(Exception e){

			logger.error("Failed to retrieve this product", e);
			status = Status.INTERNAL_SERVER_ERROR;
			responseBean.setMessage(e.getMessage());

		}

		return Response.status(status).entity(responseBean).build();
	}


}
