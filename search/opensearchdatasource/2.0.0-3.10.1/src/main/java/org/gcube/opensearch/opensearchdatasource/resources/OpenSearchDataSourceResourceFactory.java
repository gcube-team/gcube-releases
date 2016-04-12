package org.gcube.opensearch.opensearchdatasource.resources;

import gr.uoa.di.madgik.environment.hint.EnvHint;
import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.search.Field;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.gcube.opensearch.opensearchdatasource.service.OpenSearchOperator;
import org.gcube.opensearch.opensearchdatasource.service.helpers.PropertiesFileConstants;
import org.gcube.opensearch.opensearchoperator.resource.ISOpenSearchResource;
import org.gcube.rest.commons.helpers.JSONConverter;
import org.gcube.rest.commons.resourceawareservice.resources.ResourceFactory;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;
import org.gcube.rest.opensearch.common.Constants;
import org.gcube.rest.opensearch.common.resources.OpenSearchDataSourceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.inject.Inject;

public class OpenSearchDataSourceResourceFactory extends ResourceFactory<OpenSearchDataSourceResource>  {

	private static final Logger logger = LoggerFactory
			.getLogger(OpenSearchDataSourceResourceFactory.class);

	private final OpenSearchOperator operator;
	
	@Inject
	public OpenSearchDataSourceResourceFactory(OpenSearchOperator operator) {
		this.operator = operator;
	}
	
	
	@Override
	public OpenSearchDataSourceResource createResource(String resourceID,
			String params) throws StatefulResourceException {
		
		OpenSearchDataSourceResource resource = new Gson().fromJson(params, OpenSearchDataSourceResource.class);
		
		
		if (resource.getScope() != null
				&& resource.getScope().equalsIgnoreCase(this.getScope()) == false) {
			logger.error("scope set to : " + resource.getScope()
					+ " but different to : " + this.getScope());
			throw new StatefulResourceException("scope set to : "
					+ resource.getScope() + " but different to : "
					+ this.getScope());
		}
		
		
		logger.info("resource after deserialization 1.");
		logger.info(JSONConverter.convertToJSON(resource, true));
		
		

		resource.setResourceID(resourceID);
		
		resource.getEnvHints().AddHint(new NamedEnvHint("GCubeActionScope", new EnvHint(resource.getScope())));
		
		int providerCount = 0;
		
		if (resource.getOpenSearchResourceXML() != null && resource.getOpenSearchResourceXML().size() > 0){
			providerCount = resource.getOpenSearchResourceXML().size();
		}
		
		resource.openSearchGenericResources = new ISOpenSearchResource[providerCount];
		
		
		if (resource.getOpenSearchResource() == null){
			List<String> l = Lists.newArrayList();
			resource.setOpenSearchResource(l);
		}
		
		List<String> emptyList = Lists.newArrayList();;
		resource.setDescriptionDocumentURI(emptyList);
		
		
		for(int i = 0; i < providerCount; i++) {
			try {	
				synchronized(resource.cache) {
					resource.openSearchGenericResources[i] = new ISOpenSearchResource(resource.getOpenSearchResourceXML().get(i).toString(), resource.cache.descriptionDocuments, resource.cache.resourcesXML, resource.cache.XSLTs, resource.getEnvHints());
				}
			}catch(Exception e) {
				logger.error("Could not create ISOpenSearchResource :", e);
				throw new StatefulResourceException("Could not create ISOpenSearchResource :", e);
			}
			
			try {
				//resource.getOpenSearchResource().add(resource.openSearchGenericResources[i].getDescriptionDocURL().toString());
		    	
				resource.getDescriptionDocumentURI().add(resource.openSearchGenericResources[i].getDescriptionDocURL());
			} catch (Exception e) {
				throw new StatefulResourceException("error creating description document and retrieving templates", e);
			}
		}
		
		logger.info("openSearchGenericResources size : " + resource.openSearchGenericResources.length);
		
		Map<String, String> parameters = new HashMap<String, String>();
        for(int i = 0; i < providerCount; i++)
        	parameters.putAll(resource.openSearchGenericResources[i].getParameters()); 
        
        
        logger.info("parameters : " + parameters);
        
        List<String> presentables = Lists.newArrayList();
        //List<String> fields = Lists.newArrayList();
        
        List<String> newFields = Lists.newArrayList();
        for (String field : resource.getFields()){
        	String[] splitField = field.split(":");
        	newFields.add(field + ":" + parameters.get(splitField[splitField.length-1]));
        }
        
        resource.setFields(newFields);
        
//		for (String field : resource.getFields()){
//			String[] splitField = field.split(":");
//			if(splitField[splitField.length-2].equals("p")) {
//				List<Field> f = null;
//				try {
//					String fieldName = splitField[splitField.length-1];
//					f = Field.getFieldsWithName(false, fieldName);
//					logger.info(" ~> field with name : " + fieldName + " : " + f);
//				} catch (ResourceRegistryException e) {
//					throw new StatefulResourceException("error getting field from RR", e);
//				}
//    			if(!f.isEmpty()) {
//    				presentables.add(f.get(0).getName());
//    			}
//			}
//		}
		
		resource.allPresentableNames = resource.presentableFields;
		
		
		logger.info("openSearchResource : " + resource.getOpenSearchResource());
		
		logger.info("resource before addProviders : " + JSONConverter.convertToJSON(resource, true));
		
		try {
			operator.addProviders(resource, resource.getFields(), resource.getCollectionID(), resource.getOpenSearchResource(), resource.getFixedParameters());
		} catch (Exception e) {
			throw new StatefulResourceException("error in addProviders", e);
		}

		logger.info("resource after addProviders : " + JSONConverter.convertToJSON(resource, true));

		
		resource.setSupportedRelations(OpenSearchDataSourceResource.getSupportedRelationsSet());
		
		resource.setHostname(getHostname());
		
		return resource;
	}
	
	@Override
	public void loadResource(OpenSearchDataSourceResource resource)
			throws StatefulResourceException {
		super.loadResource(resource);
		
		resource.setHostname(getHostname());
		resource.getEnvHints().AddHint(new NamedEnvHint("GCubeActionScope", new EnvHint(resource.getScope())));
		
		int providerCount = 0;
		
		if (resource.getOpenSearchResourceXML() != null && resource.getOpenSearchResourceXML().size() > 0){
			providerCount = resource.getOpenSearchResourceXML().size();
		}
		
		resource.openSearchGenericResources = new ISOpenSearchResource[providerCount];
		
		
		if (resource.getOpenSearchResource() == null){
			List<String> l = Lists.newArrayList();
			resource.setOpenSearchResource(l);
		}
		
		List<String> emptyList = Lists.newArrayList();;
		resource.setDescriptionDocumentURI(emptyList);
		
		
		for(int i = 0; i < providerCount; i++) {
			try {	
				synchronized(resource.cache) {
					resource.openSearchGenericResources[i] = new ISOpenSearchResource(resource.getOpenSearchResourceXML().get(i).toString(), resource.cache.descriptionDocuments, resource.cache.resourcesXML, resource.cache.XSLTs, resource.getEnvHints());
				}
			}catch(Exception e) {
				logger.error("Could not create ISOpenSearchResource :", e);
				throw new StatefulResourceException("Could not create ISOpenSearchResource :", e);
			}
			
			try {
				//resource.getOpenSearchResource().add(resource.openSearchGenericResources[i].getDescriptionDocURL().toString());
		    	
				resource.getDescriptionDocumentURI().add(resource.openSearchGenericResources[i].getDescriptionDocURL());
			} catch (Exception e) {
				throw new StatefulResourceException("error creating description document and retrieving templates", e);
			}
		}
		
		logger.info("openSearchGenericResources size : " + resource.openSearchGenericResources.length);
		
		Map<String, String> parameters = new HashMap<String, String>();
        for(int i = 0; i < providerCount; i++)
        	parameters.putAll(resource.openSearchGenericResources[i].getParameters()); 
        
        
        logger.info("parameters : " + parameters);
        
        List<String> presentables = Lists.newArrayList();
        //List<String> fields = Lists.newArrayList();
        
        List<String> newFields = Lists.newArrayList();
        for (String field : resource.getFields()){
        	String[] splitField = field.split(":");
        	newFields.add(field/* + ":" + parameters.get(splitField[splitField.length-1])*/);
        }
        
        resource.setFields(newFields);
        
//      logger.info(" ~> fields : " + resource.getFields());
//        
//		for (String field : resource.getFields()){
//			logger.info(" ~> field : " + field);
//			
//			String[] splitField = field.split(":");
//			
//			logger.info(" ~> splitField : " + splitField[splitField.length-3]);
//			
//			if(splitField[splitField.length-3].equals("p")) {
//				List<Field> f = null;
//				try {
//					String fieldName = splitField[splitField.length-2];
//					f = Field.getFieldsWithName(false, fieldName);
//					logger.info(" ~> field with name : " + fieldName + " : " + f);
//				} catch (ResourceRegistryException e) {
//					throw new StatefulResourceException("error getting field from RR", e);
//				}
//				
//				logger.info(" ~> f.isEmpty() : " + f.isEmpty());
//				
//    			if(!f.isEmpty()) {
//    				presentables.add(f.get(0).getName());
//    			}
//			}
//		}
		
		resource.allPresentableNames = resource.presentableFields;
		
		
		logger.info("openSearchResource" + resource.getOpenSearchResource());
		
		logger.info("resource before addProviders : " + JSONConverter.convertToJSON(resource, true));
		
		try {
			operator.addProviders(resource, resource.getFields(), resource.getCollectionID(), resource.getOpenSearchResource(), resource.getFixedParameters());
		} catch (Exception e) {
			throw new StatefulResourceException("error in addProviders", e);
		}

		logger.info("resource after addProviders : " + JSONConverter.convertToJSON(resource, true));

	}
	
	
	@Override
	public String getScope() {
		final Properties properties = new Properties();
		try (InputStream is = Resources.getResource(Constants.PROPERTIES_FILE)
				.openStream()) {
			properties.load(is);
			
			return properties.getProperty(PropertiesFileConstants.SCOPE_PROP);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"could not load property file  : "
							+ Constants.PROPERTIES_FILE);
		}
	}
	
	public String getHostname() {
		final Properties properties = new Properties();
		try (InputStream is = Resources.getResource(Constants.PROPERTIES_FILE)
				.openStream()) {
			properties.load(is);
			
			return properties.getProperty(PropertiesFileConstants.HOSTNAME_PROP);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"could not load property file  : "
							+ Constants.PROPERTIES_FILE);
		}
	}
	
}
