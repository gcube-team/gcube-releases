package org.gcube.gcat.profile;

import java.io.StringWriter;
import java.util.List;
import java.util.Set;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.datacatalogue.metadatadiscovery.reader.MetadataFormatDiscovery;
import org.gcube.datacatalogue.metadatadiscovery.reader.QueryForResourceUtil;
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
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class ISProfile {
	
	public static int PRETTY_PRINT_INDENT_FACTOR = 4;
	
	private static Logger logger = LoggerFactory.getLogger(ISProfile.class);
	
	protected ObjectMapper mapper;

	public ISProfile() {
		mapper = new ObjectMapper();
	}
	
	public ObjectMapper getMapper() {
		return mapper;
	}
	
	public ArrayNode list() {
		ArrayNode arrayNode = mapper.createArrayNode();
		
		try {
			Set<String> names = (new MetadataUtility()).getProfilesNames();
			for(String name : names) {
				arrayNode.add(name);
			}
			return arrayNode;
		} catch(Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
	
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
	
	public String read(String name, boolean xml) {
		try {
			String profile = (new MetadataUtility()).getMetadataFormat(name).getMetadataSource();
			if(profile != null) {
				if(xml) {
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
	
	
	public boolean createOrUpdate(String name, String xml) {
		try {
			MetadataUtility metadataUtility = new MetadataUtility();
			metadataUtility.validateProfile(xml);
			if(metadataUtility.getMetadataFormat(name) == null) {
				createGenericResource(name, xml);
				return true;
			} else {
				updateGenericResource(name, xml);
				return false;
			}
		} catch(WebApplicationException e) {
			throw e;
		} catch (SAXException e) {
			throw new BadRequestException(e);
		} catch(Exception e) {
			throw new InternalServerErrorException(e);
		} finally {
			// TOOD Actually Cache has been removed. Remove the following code if it will not be re-introduced
			// Cleaning the cache 
			// MetadataUtility.clearCache();
		}
	}
	
	public boolean delete(String name) {
		try {
			MetadataUtility metadataUtility = new MetadataUtility();
			if(metadataUtility.getMetadataFormat(name) == null) {
				throw new NotFoundException("Profile with name " + name + " not found");
			} else {
				removeGenericResource(name);
				return true;
			}
		} catch(WebApplicationException e) {
			throw e;
		} catch(Exception e) {
			throw new InternalServerErrorException(e.getMessage());
		} finally {
			// Cleaning the cache 
			// MetadataUtility.clearCache();
		}
	}
}
