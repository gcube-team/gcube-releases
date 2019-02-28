package org.gcube.gcat.rest;

import java.io.StringWriter;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.datacatalogue.metadatadiscovery.reader.MetadataFormatDiscovery;
import org.gcube.datacatalogue.metadatadiscovery.reader.QueryForResourceUtil;
import org.gcube.gcat.ResourceInitializer;
import org.gcube.gcat.profile.MetadataUtility;
import org.gcube.gcat.utils.Constants;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Path(Profile.PROFILES)
public class Profile extends BaseREST implements org.gcube.gcat.api.interfaces.Profile<Response,Response> {
	
	private static Logger logger = LoggerFactory.getLogger(Profile.class);
	
	public static final String PROFILE_NAME_PARAMETER = "PROFILE_NAME";
	
	@Context
	private UriInfo uriInfo;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String list() {
		setCalledMethod("GET /" + PROFILES);
		
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayNode = mapper.createArrayNode();
		
		try {
			Set<String> names = MetadataUtility.getInstance().getProfilesNames();
			for(String name : names) {
				arrayNode.add(name);
			}
			return mapper.writeValueAsString(arrayNode);
		} catch(Exception e) {
			throw new InternalServerErrorException(e.getMessage());
		}
	}
	
	public static int PRETTY_PRINT_INDENT_FACTOR = 4;
	
	@GET
	@Path("/{" + PROFILE_NAME_PARAMETER + "}")
	@Produces({MediaType.APPLICATION_XML, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8})
	public String read(@PathParam(PROFILE_NAME_PARAMETER) String name,
			@DefaultValue(MediaType.APPLICATION_XML) @HeaderParam("Accept") String accept) {
		setCalledMethod("GET /" + PROFILES + "/{" + PROFILE_NAME_PARAMETER + "}");
		try {
			String profile = MetadataUtility.getInstance().getMetadataFormat(name).getMetadataSource();
			if(profile != null) {
				if(accept.startsWith(MediaType.APPLICATION_XML)) {
					return profile;
				} else {
					JSONObject xmlJSONObj = XML.toJSONObject(profile);
					String jsonString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
					return jsonString;
				}
			} else {
				throw new NotFoundException("Profile with name " + name + " not found");
			}
		} catch(WebApplicationException e) {
			throw e;
		} catch(Exception e) {
			throw new InternalServerErrorException(e.getMessage());
		}
	}
	
	/*
	public static void appendXmlFragment(org.gcube.common.resources.gcore.GenericResource.Profile profile, String xml) throws Exception {
		try {
			DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Element elem = profile.newBody();
			Node fragmentNode = docBuilder.parse(new InputSource(new StringReader(xml))).getDocumentElement();
			fragmentNode = elem.getOwnerDocument().importNode(fragmentNode, true);
			elem.appendChild(fragmentNode);
		} catch (Exception e) {
			profile.newBody(xml);
		}
	
	}
	*/
	
	/*
	 *  TODO Check the Queries because the name in the Profile differs from the name in
	 *  <metadataformat type="Dataset">
	 *   
	 */
	protected GenericResource instantiateGenericResource(String name, String xml) throws Exception {
		GenericResource genericResource = new GenericResource();
		org.gcube.common.resources.gcore.GenericResource.Profile profile = genericResource.newProfile();
		profile.type(MetadataFormatDiscovery.DATA_CATALOGUE_METADATA_SECONDARY_TYPE);
		profile.name(name);
		profile.description("Profile create using " + Constants.CATALOGUE_NAME);
		// appendXmlFragment(profile, xml);
		profile.newBody(xml);
		StringWriter stringWriter = new StringWriter();
		Resources.marshal(genericResource, stringWriter);
		logger.debug("The generated {} is\n{}", GenericResource.class.getSimpleName(), stringWriter.toString());
		return genericResource;
	}
	
	protected void createGenericResource(String name, String xml) throws Exception {
		GenericResource genericResource = instantiateGenericResource(name, xml);
		RegistryPublisher registryPublisher = RegistryPublisherFactory.create();
		genericResource = registryPublisher.create(genericResource);
		StringWriter stringWriter = new StringWriter();
		Resources.marshal(genericResource, stringWriter);
		logger.trace("The {} with ID {} has been created \n{}", GenericResource.class.getSimpleName(),
				genericResource.id(), stringWriter.toString());
	}
	
	protected GenericResource getGenericResource(String name) {
		String query = QueryForResourceUtil.getGcubeGenericQueryStringForSecondaryTypeAndName(name,
				MetadataFormatDiscovery.DATA_CATALOGUE_METADATA_SECONDARY_TYPE);
		Query q = new QueryBox(query);
		DiscoveryClient<GenericResource> client = ICFactory.clientFor(GenericResource.class);
		List<GenericResource> resources = client.submit(q);
		
		if(resources == null || resources.size() == 0) {
			throw new InternalServerErrorException(
					"No Resources with secondaryType '" + MetadataFormatDiscovery.DATA_CATALOGUE_METADATA_SECONDARY_TYPE
							+ "' and name '" + name + "' exists in the current context");
		} else {
			if(resources.size() == 1) {
				GenericResource genericResource = resources.get(0);
				return genericResource;
			} else {
				throw new InternalServerErrorException("More than one Resource with secondaryType '"
						+ MetadataFormatDiscovery.DATA_CATALOGUE_METADATA_SECONDARY_TYPE + "' and name '" + name
						+ "' exists in the current context");
			}
		}
	}
	
	protected void updateGenericResource(String name, String xml) {
		
		GenericResource genericResource = getGenericResource(name);
		logger.info("The {} with ID {} is going to be updated", GenericResource.class.getSimpleName(),
				genericResource.id());
		
		genericResource.profile().newBody(xml);
		RegistryPublisher registryPublisher = RegistryPublisherFactory.create();
		registryPublisher.update(genericResource);
		
		StringWriter stringWriter = new StringWriter();
		Resources.marshal(genericResource, stringWriter);
		logger.trace("The {} with ID {} has been updated to \n{}", GenericResource.class.getSimpleName(),
				genericResource.id(), stringWriter.toString());
		
	}
	
	protected void removeGenericResource(String name) {
		GenericResource genericResource = getGenericResource(name);
		RegistryPublisher registryPublisher = RegistryPublisherFactory.create();
		registryPublisher.remove(genericResource);
	}
	
	@PUT
	@Path("/{" + PROFILE_NAME_PARAMETER + "}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Response createOrUpdate(@PathParam(PROFILE_NAME_PARAMETER) String name, String xml) {
		setCalledMethod("PUT /" + PROFILES + "/{" + PROFILE_NAME_PARAMETER + "}");
		try {
			MetadataUtility metadataUtility = MetadataUtility.getInstance();
			metadataUtility.getDataCalogueMetadataFormatReader().validateProfile(xml);
			if(metadataUtility.getMetadataFormat(name) == null) {
				createGenericResource(name, xml);
				ResponseBuilder responseBuilder = Response.status(Status.CREATED).entity(xml);
				responseBuilder.header(LOCATION_HEADER, uriInfo.getAbsolutePath());
				return responseBuilder.type(MediaType.APPLICATION_XML).build();
			} else {
				updateGenericResource(name, xml);
				ResponseBuilder responseBuilder = Response.status(Status.OK).entity(xml);
				return responseBuilder.type(MediaType.APPLICATION_XML).build();
			}
		} catch(WebApplicationException e) {
			throw e;
		} catch(Exception e) {
			throw new InternalServerErrorException(e.getMessage());
		}finally {
			// TOOD Actually Cache has been removed. Remove the following code if it will not be re-introduced
			// Cleaning the cache 
			MetadataUtility.clearCache();
		}
	}
	
	@DELETE
	@Path("/{" + PROFILE_NAME_PARAMETER + "}")
	public Response delete(@PathParam(PROFILE_NAME_PARAMETER) String name) {
		setCalledMethod("DELETE /" + PROFILES + "/{" + PROFILE_NAME_PARAMETER + "}");
		try {
			MetadataUtility metadataUtility = MetadataUtility.getInstance();
			if(metadataUtility.getMetadataFormat(name) == null) {
				throw new NotFoundException("Profile with name " + name + " not found");
			} else {
				removeGenericResource(name);
				return Response.status(Status.NO_CONTENT).build();
			}
		} catch(WebApplicationException e) {
			throw e;
		} catch(Exception e) {
			throw new InternalServerErrorException(e.getMessage());
		} finally {
			// Cleaning the cache 
			MetadataUtility.clearCache();
		}
	}

	@Override
	public Response create(String name, String xml) {
		return createOrUpdate(name, xml);
		
	}
	
	@Override
	public String read(String name) {
		return read(name, MediaType.APPLICATION_XML);
	}
	
	@Override
	public String update(String name, String xml) {
		return createOrUpdate(name, xml).getEntity().toString();
	}

}
