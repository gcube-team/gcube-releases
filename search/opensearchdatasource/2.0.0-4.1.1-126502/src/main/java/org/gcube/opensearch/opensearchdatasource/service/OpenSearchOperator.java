package org.gcube.opensearch.opensearchdatasource.service;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.utils.Locators;
import gr.uoa.di.madgik.rr.element.search.Field;

import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.opensearch.opensearchdatasource.processor.FieldDefinitionInfo;
import org.gcube.opensearch.opensearchdatasource.processor.GCQLNodeAnnotation;
import org.gcube.opensearch.opensearchdatasource.processor.OpenSearchCollectionEnricher;
import org.gcube.opensearch.opensearchdatasource.processor.OpenSearchGcqlAnnotator;
import org.gcube.opensearch.opensearchdatasource.processor.OpenSearchGcqlProcessor;
import org.gcube.opensearch.opensearchdatasource.processor.OpenSearchGcqlQueryContainer;
import org.gcube.opensearch.opensearchdatasource.processor.OpenSearchProjector;
import org.gcube.opensearch.opensearchdatasource.service.helpers.OpenSearchDataSourceConfig;
import org.gcube.opensearch.opensearchlibrary.DescriptionDocument;
import org.gcube.opensearch.opensearchlibrary.OpenSearchDataSourceConstants;
import org.gcube.opensearch.opensearchlibrary.queryelements.BasicQueryElementFactory;
import org.gcube.opensearch.opensearchlibrary.urlelements.BasicURLElementFactory;
import org.gcube.opensearch.opensearchlibrary.utils.FactoryClassNamePair;
import org.gcube.opensearch.opensearchoperator.OpenSearchOp;
import org.gcube.opensearch.opensearchoperator.OpenSearchOpConfig;
import org.gcube.opensearch.opensearchoperator.resource.ISOpenSearchResource;
import org.gcube.opensearch.opensearchoperator.resource.ISOpenSearchResourceCache;
import org.gcube.opensearch.opensearchoperator.resource.OpenSearchResource;
import org.gcube.rest.commons.resourceawareservice.resources.Resource;
import org.gcube.rest.opensearch.common.resources.OpenSearchDataSourceResource;
import org.gcube.rest.opensearch.common.resources.OpenSearchDataSourceResource.FixedParam;
import org.gcube.rest.resourcemanager.discovery.InformationCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.library.util.cql.query.tree.GCQLNode;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Singleton
public class OpenSearchOperator {
	
	private static final Logger logger = LoggerFactory.getLogger(OpenSearchOperator.class);
	
	private final InformationCollector icollector;

	private OpenSearchOpConfig openSearchOpConfig;
	
	@Inject
	public OpenSearchOperator(InformationCollector icollector){
		this.icollector = icollector;
	}
	
	public static String[] retrieveTemplates(DescriptionDocument dd, OpenSearchResource res) throws Exception {
    	Map<String, List<String>> uniqueTemplates = dd.getUniqueTemplates();	
//    	List<String> specifiedMimeTypes =  res.getTransformationTypes();
    	List<String> supportedTemplates = new ArrayList<String>();
//    	for(String key : uniqueTemplates.keySet()) {
//    		if(specifiedMimeTypes.contains(key))
//    			supportedTemplates.addAll(uniqueTemplates.get(key));
//    	}
//    	return supportedTemplates.toArray(new String[1]);
    	for(List<String> templates : uniqueTemplates.values())
    		supportedTemplates.addAll(templates);
    	return supportedTemplates.toArray(new String[1]);
    }
	
	protected static void filterFieldInfo(List<String> fields, List<String> presentableFields, List<String> searchableFields){
		
    	//first clear the arrays
    	presentableFields.clear();
    	searchableFields.clear();
    	
    	//to ensure each field is added only once
    	HashSet<String> searchableSet = new HashSet<String>();
    	HashSet<String> presentableSet =  new HashSet<String>();
    	
    	//create the arrays of presentable and searchable fields
		for(String field : fields)
		{
			//add the field to the related array, depending on its type
			String fieldName;
			if((fieldName = getFieldName(field, OpenSearchDataSourceConstants.PRESENTABLE_TAG)) != null)
			{
				if(presentableSet.add(fieldName)) {
					logger.trace("added presentable field: " + fieldName);
					presentableFields.add(fieldName);
				}
				
			} else {
				fieldName = getFieldName(field, OpenSearchDataSourceConstants.SEARCHABLE_TAG);
				if(searchableSet.add(fieldName)) {
					logger.trace("added searchable field: " + fieldName);
					searchableFields.add(fieldName);
				}
			}
		}
	}
	
	private static String getFieldName(String field, String type) {
		//get the string after the second seperator, which indicates the type
		int from = field.indexOf(OpenSearchDataSourceConstants.FIELD_SEPARATOR, 
				field.indexOf(OpenSearchDataSourceConstants.FIELD_SEPARATOR) + 1) + 1;
		String t = field.substring(from, from + type.length());
		if(!t.equals(type))
			return null;
		//get the string after the third seperator, which indicates the fieldName
		from = field.indexOf(OpenSearchDataSourceConstants.FIELD_SEPARATOR, from) + 1;
		int to = field.indexOf(OpenSearchDataSourceConstants.FIELD_SEPARATOR, from);
		return field.substring(from, to);		
	}
	
	
	public void addProviders(OpenSearchDataSourceResource resource, List<String> fields, List<String> collectionIds, List<String> openSearchResourceIDs, List<FixedParam> fixedParams) throws Exception {
		
		logger.info("addProviders is called!");
		
		logger.info("fields                : " + fields);
		logger.info("collectionIds         : " + collectionIds);
		logger.info("openSearchResourceIDs : " + openSearchResourceIDs);
		logger.info("fixedParams           : " + fixedParams);
		
		
		int providerToAddCount = collectionIds.size();
		
		List<FixedParam> savedFixedParams = resource.getFixedParameters();
		List<ISOpenSearchResource> savedOpenSearchResources = Lists.newArrayList(resource.openSearchGenericResources);
		List<ISOpenSearchResource> openSearchResourcesToAdd = Lists.newArrayList();
		
		List<String> openSearchResourceXML = Lists.newArrayList();
		for(int i = 0; i < providerToAddCount; i++) {
			
			String resXML = retrieveGenericResource(openSearchResourceIDs.get(i), resource.getScope());
			
			if (resource.getOpenSearchResourceXML().contains(resXML)){
				logger.info("resource : " + openSearchResourceIDs.get(i) + " already added");
				continue;
			}
			
			openSearchResourceXML.add(resXML);

			synchronized(resource.cache) {
				
				logger.info("Initializing with  : \n" +  openSearchResourceXML.get(i));
				
				openSearchResourcesToAdd.add(new ISOpenSearchResource(openSearchResourceXML.get(i), resource.cache.descriptionDocuments, resource.cache.resourcesXML, resource.cache.XSLTs, resource.getEnvHints()));
			}
		}
		
		logger.info("providerToAddCount was         : " + providerToAddCount);
		providerToAddCount = openSearchResourceXML.size();
		logger.info("providerToAddCount changed to  : " + providerToAddCount);
		
		resource.setFields(Lists.newArrayList(fields));
		
		logger.info("Just added to the resource property set fields : " + resource.getFields());

		resource.allPresentableNames.clear();
		
		for (String field : resource.getFields()){
			String[] splitField = field.split(":");
			
			logger.info("splitField : " + Arrays.toString(splitField));
			
			if(splitField[splitField.length-2].equals("p")) {
				List<Field> f = Field.getFieldsWithName(false, splitField[splitField.length-1]);
				if(!f.isEmpty()) {
					resource.allPresentableNames.add(f.get(0).getName());
				}
			}
		}
		
		filterFieldInfo(resource.getFields(), resource.presentableFields, resource.searchableFields);
		
		try {
			int providerCount = resource.getCollections().size();
			
			resource.setFixedParameters(new ArrayList<FixedParam>());
			
			resource.openSearchGenericResources = new ISOpenSearchResource[providerCount+providerToAddCount];
			int i = 0;
			for(i = 0; i < providerCount; i++) {
				resource.getFixedParameters().add(savedFixedParams.get(i));
				resource.openSearchGenericResources[i] = savedOpenSearchResources.get(i);
			}
			
			logger.info("openSearchResourcesToAdd : " + openSearchResourcesToAdd);
			
			for(int j = 0; j < providerToAddCount; j++) {
				
				FixedParam fixedParam = fixedParams.get(j);
				
				resource.getFixedParameters().add(fixedParam);
				
				resource.openSearchGenericResources[i+j] = openSearchResourcesToAdd.get(j);
				
				resource.getCollections().add(collectionIds.get(j));
				
		    	logger.debug("Just added to the resource property set collections : " + resource.getCollections());
		    	
		    	resource.getOpenSearchResource().add(openSearchResourceIDs.get(j));
		    	
		    	logger.debug("Just added to the resource property set openSearchResources : " + resource.getOpenSearchResource());
		    	
		    	String descriptionDoc = resource.openSearchGenericResources[i+j].getDescriptionDocURL();
		    	logger.info("description doc to be added : " + descriptionDoc);
		    	
		    	resource.getDescriptionDocumentURI().add(resource.openSearchGenericResources[i+j].getDescriptionDocURL());
		    	logger.debug("Just added to the resource property set RP_DESCRIPTION_DOCUMENT_URI : " + resource.getDescriptionDocumentURI());
			}
		}catch(Exception e) {
			resource.setFixedParameters(savedFixedParams);
			resource.openSearchGenericResources = Iterables.toArray(savedOpenSearchResources, ISOpenSearchResource.class);
			throw e;
		}
	}
	
	
	/**
	 * Retrieves a generic resource using the IS
	 * 
	 * @param id The id of the generic resource to retrieve
	 * @param scope The scope to use
	 * @return The generic resource expressed as an XMLResult
	 * @throws Exception In case of error
	 */
	public String retrieveGenericResource(String resourceID, String scope) throws Exception {
		List<Resource> resources = this.icollector.getGenericResourcesByID(resourceID, scope);
		
		String resourceBody = resources.get(0).getBodyAsString();
		
		return resourceBody;
	}
	
	public void clearCache(OpenSearchDataSourceResource resource, String scope) throws Exception {
		int providerCount = resource.getCollections().size();
		String genericResourceXML[] = new String[providerCount];
		for(int i = 0; i < providerCount; i++)
			genericResourceXML[i] = retrieveGenericResource(resource.getOpenSearchResource().get(i), scope).toString();
		
		synchronized(resource.cache) {
			ISOpenSearchResource[] savedResources = new ISOpenSearchResource[providerCount];
			List<String> savedDdUris = Lists.newArrayList();
			for(int i = 0; i < providerCount; i++) {
				savedResources[i] = resource.openSearchGenericResources[i];
				savedDdUris.add(resource.getDescriptionDocumentURI().get(i));
			}
				
			resource.cache.descriptionDocuments.clear();
			resource.cache.resources.clear();
			resource.cache.resourcesXML.clear();
			resource.cache.XSLTs.clear();
			for(int i = 0; i < providerCount; i++) {
		     	String genericResourceId = resource.getOpenSearchResource().get(i);
		     	ISOpenSearchResourceCache cache = resource.cache;
		     	String ddURI = savedDdUris.get(i);
		     	EnvHintCollection envHints = resource.getISEnvHints();
				
				Exception ex = null;
					
				boolean fail = false;
				try {
					resource.openSearchGenericResources[i] = new ISOpenSearchResource(genericResourceXML[i], cache.descriptionDocuments, 
							cache.resourcesXML, cache.XSLTs, envHints);
				}catch(Exception e) {
					logger.warn("Could not create OpenSearch resource instance during cache clearing. Using old instance.", e);
					fail = true; 
					ex = e;
				}
				cache.resources.put(ddURI, resource.openSearchGenericResources[i]);
				if(!fail) {
					DescriptionDocument dd = null;
					ddURI = resource.openSearchGenericResources[i].getDescriptionDocURL();
					try {
						dd = new DescriptionDocument(resource.openSearchGenericResources[i].getDescriptionDocument(), new BasicURLElementFactory(), new BasicQueryElementFactory());
					}catch(Exception e) {
						logger.warn("Could not create Description Document instance during cache clearing. Using old OpenSearch resource instance.", e);
						fail = true; ex = e;
					}
					if(!fail) {
	//					String[] templates = null;
	//					try {
	//						templates = OpenSearchDataSourceResource.retrieveTemplates(dd, this.openSearchGenericResource);
	//					}catch(Exception e) {
	//						logger.warn("Could not retrieve templates from Description Document. Using old OpenSearch resource instance.", e);
	//						fail = true; ex = e;
	//					}
						if(!fail) {
							try {
								if(i == 0) resource.setDescriptionDocumentURI(ddURI);
								else resource.getDescriptionDocumentURI().add(ddURI);
							}catch(Exception e) {
								logger.warn("Could not update WS resource with the new Description Document URI. Using old OpenSearch resource instance", e);
								fail = true; ex = e;
							}
	//						if(!fail) {
	//							try {
	//								this.setTemplates(templates);
	//							}catch(Exception e) {
	//								logger.warn("Could not update WS resource with the new templates. Using old OpenSearch resource instance", e);
	//								ex = e;
	//								try {
	//									this.setDescriptionDocumentURI(savedDdUri); //First step toward reverting to old state
	//								}catch(Exception ee) {
	//									logger.error("Could not revert WS resource to its old state. Resource will remain inconsistent!", ee);
	//									fail = true; ex = ee;
	//								}
	//							}
	//						}
						}
					} 
					if(fail) { //Revert to old state on failure	
						cache.resources.clear();
						for(int j =0; j < providerCount; j++) {
							resource.openSearchGenericResources[j] = savedResources[j];
							cache.resources.put(savedDdUris.get(j), savedResources[j]);
						}
						resource.setDescriptionDocumentURI(savedDdUris);
						throw ex;
					}
				}
			}
		}
	}
	
	
	/**
	 * Performs a query on the OpenSearch provider that is connected to this resource. 
	 * @param queryString <code>String</code>  - the query to be performed (using custom 
	 * syntax @see )
	 * @return <code>URI</code>  - representation of the EPR for a resultset service which holds the results of the query.
	 * @throws RemoteException In case of error
	 */
    
    public URI query(OpenSearchDataSourceResource resource, String cqlQuery) throws RemoteException
    {
    	return query(resource, cqlQuery, true);
    }
    
    
    
    public OpenSearchOpConfig getOpenSearchOpConfig(){
    	if (openSearchOpConfig == null){
    	
    	OpenSearchDataSourceConfig config = new OpenSearchDataSourceConfig();//(OpenSearchDataSourceConfig) StatefulContext.getPortTypeContext().getProperty("config", false);
		
    	Map<String, FactoryClassNamePair> factories = null;
    	
    	try {
    		config.initFromPropertiesFile();
    	} catch (Exception e){
    		logger.warn("error while reading from properties file", e);
    		config = null;
    	}
    	
    	if(config != null) {
			factories = config.getFactories();
			logger.debug("Read configuration property: factories = " + config.getFactories());
		}else
			logger.warn("Could not read configuration property: factories. Operating using only basic factories");
    		openSearchOpConfig = new OpenSearchOpConfig(null, null, null, factories);
    	}
    	
    	return openSearchOpConfig;
    }
    
	public URI query(OpenSearchDataSourceResource resource, String cqlQuery, boolean useRR) throws RemoteException
	{  
		try{
			OpenSearchOpConfig cfg = getOpenSearchOpConfig();
			synchronized(resource.cache) {
				cfg.ISCache = resource.cache;
		//		res = this.openSearchGenericResource;
			}
			OpenSearchGcqlProcessor processor = new OpenSearchGcqlProcessor();
			OpenSearchGcqlAnnotator annotator = new OpenSearchGcqlAnnotator();
			GCQLNode queryTree = processor.parseQuery(cqlQuery);
			GCQLNodeAnnotation annotationTree = annotator.processNode(queryTree);
			String collection = annotator.getFirstEncounteredCollectionId(annotationTree); //TODO change
			processor.setAnnotationTree(annotationTree);
			processor.setCollection(collection);
			
			if (useRR)
				processor.setFields(resource.allPresentableNames);
			else
				processor.setFields(resource.presentableFields);
			
			processor.setDataSourceLocator(resource.getResourceID());
			logger.debug("Datasource locator is " + resource.getResourceID());
			
			
			processor.setUseRR(useRR);
			
			logger.info("processor searchables         : " + resource.searchableFields);
			logger.info("processor presentables        : " + resource.presentableFields);
			logger.info("processor allPresentableNames : " + resource.allPresentableNames);
			logger.info("collections : " + resource.getCollections());
		
			OpenSearchGcqlQueryContainer query =  (OpenSearchGcqlQueryContainer)processor.processQuery(resource.presentableFields, resource.searchableFields);

			int index = -1;
			for(int i = 0; i < resource.getCollections().size(); i++) {
				if(resource.getCollections().get(i).equals(processor.getCollection())) {
					index = i;
					break;
				}
			}
			if(index == -1)
				throw new RemoteException("Could not locate provider");
			
			//List<String> custom_fixed = Lists.newArrayList("count:50");
			
			logger.info("fixedParms : " + resource.getFixedParameters());
			if (resource.getFixedParameters() == null){
				resource.setFixedParameters(new ArrayList<FixedParam>());
			}
			
			
			logger.info("this.openSearchGenericResources[index] : " + resource.openSearchGenericResources[index]);
			logger.info("this.fixedParameters.get(index)        : " + resource.getFixedParameters().get(index));
			logger.info("cfg                                    : " + cfg);
			
			String[] params = Iterables.toArray(resource.getFixedParameters().get(index).getParams(), String.class);
			
			logger.info("params                                 : " + Arrays.toString(params));
			
			OpenSearchOp op = new OpenSearchOp(resource.openSearchGenericResources[index], params, cfg, new EnvHintCollection());
			
			//OpenSearchOp op = new OpenSearchOp(this.openSearchGenericResources[index], this.fixedParameters[index], cfg, new EnvHintCollection());
			
			
			
			
			URI loc = op.query(query.queries.get(processor.getCollection()).get("*").get(0).toString());
			OpenSearchCollectionEnricher colEnricher = new OpenSearchCollectionEnricher(collection, loc);
			new Thread(colEnricher).start();
			loc = colEnricher.getLocator();
			
			FieldDefinitionInfo fieldDefInfo = createFieldDefinition(query.getProjectedFields());
			FieldDefinition[] fieldDef = fieldDefInfo.fieldDefinition;
			Map<String, Integer> fieldPositions = fieldDefInfo.fieldPositions;
			RecordDefinition[] definition = new RecordDefinition[]{new GenericRecordDefinition(fieldDef)};
			
			OpenSearchProjector projector = new OpenSearchProjector(loc, definition, processor.getProjectedFields(), fieldPositions);
			projector.setReaderTimeout(3, TimeUnit.MINUTES);
			new Thread(projector).start();
			
			return Locators.localToTCP(projector.getProjectionLocator());

		}catch(Exception e){
			logger.error("Unable to execute query: \"" + cqlQuery + "\"" , e);
			throw new RemoteException("Could not execute query", e);
		}
	}
	
	private FieldDefinitionInfo createFieldDefinition(Map<String, String> projections) {
		
		Integer pos = 0;
		Map<String, Integer> fieldPositions = new HashMap<String, Integer>();
		List<FieldDefinition> fieldDef = new ArrayList<FieldDefinition>();
		fieldDef.add(new StringFieldDefinition(OpenSearchDataSourceConstants.COLLECTION_FIELD));
		fieldPositions.put(OpenSearchDataSourceConstants.COLLECTION_FIELD, pos++);
		fieldDef.add(new StringFieldDefinition(OpenSearchDataSourceConstants.OBJECTID_FIELD));
		fieldPositions.put(OpenSearchDataSourceConstants.OBJECTID_FIELD, pos++);
		
		//if project was specified
        if(projections.size() > 0)
        {
            //get all the projections specified in this query
        	for(Map.Entry<String, String> current : projections.entrySet()) {
        		String proj = current.getValue();
        		String fieldId = current.getKey();
        		if(proj.equalsIgnoreCase(OpenSearchDataSourceConstants.COLLECTION_FIELD)) {
            		fieldDef.add(new StringFieldDefinition(fieldId));
            		fieldPositions.put(OpenSearchDataSourceConstants.COLLECTION_FIELD, pos++);
        		}
        	}
        	for(Map.Entry<String, String> current : projections.entrySet()) {
        		String proj = current.getValue();
        		String fieldId = current.getKey();
        		if(proj.equalsIgnoreCase(OpenSearchDataSourceConstants.LANGUAGE_FIELD)) {
            		fieldDef.add(new StringFieldDefinition(fieldId));
            		fieldPositions.put(OpenSearchDataSourceConstants.LANGUAGE_FIELD, pos++);
        		}
        	}

        	
            for(Map.Entry<String, String> current : projections.entrySet()) {
            	String proj = current.getValue();
            	String fieldId = current.getKey();
                if(proj.equalsIgnoreCase(OpenSearchDataSourceConstants.LANGUAGE_FIELD) || 
                		proj.equalsIgnoreCase(OpenSearchDataSourceConstants.COLLECTION_FIELD))
                	continue;
                 
                fieldDef.add(new StringFieldDefinition(fieldId));
                fieldPositions.put(fieldId, pos);
                pos++; 
            }
            //if there is no valid projection return 
            if(fieldDef.size() == 1)
            {
            	logger.error(" no valid projection ");
            }
        }
        
        return new FieldDefinitionInfo(fieldDef.toArray(new FieldDefinition[fieldDef.size()]), fieldPositions);
	}
}
