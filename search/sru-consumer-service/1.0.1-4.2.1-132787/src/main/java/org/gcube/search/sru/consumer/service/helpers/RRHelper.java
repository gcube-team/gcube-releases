package org.gcube.search.sru.consumer.service.helpers;

import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.query.QueryHelper;
import gr.uoa.di.madgik.rr.element.search.Field;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class RRHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(RRHelper.class);
	
	public static Map<String, String> getFieldsMapping(String scope) throws Exception {
		Map<String, Set<String>> searchableFieldsPerCollection = getAllSearchableFieldsPerCollection(scope);
		Map<String, Set<String>> presentableFieldsPerCollection = getAllPresentableFieldsPerCollection(scope);
		Set<String> allFields = Sets.newHashSet();
		
		for (Set<String> f : searchableFieldsPerCollection.values()){
			allFields.addAll(f);
		}
		
		for (Set<String> f : presentableFieldsPerCollection.values()){
			allFields.addAll(f);
		}
		
		Map<String, String> fieldsMapping = Maps.newHashMap();
		for (String f : allFields){
			String fId = getFieldIDFromName(f);
			fieldsMapping.put(f, fId);
		}
		
		return fieldsMapping;
	}
	
	public static String getFieldIDFromName(String fieldName) throws Exception{
		List<Field> fields = Field.getFieldsWithName(false, fieldName);
		if(fields == null || fields.size() == 0) {
			throw new Exception("Could not find fieldId for fieldName: " + fieldName);
		}
		return fields.get(0).getID();
	}
	
	public static Map<String, Set<String>> getAllSearchableFieldsPerCollection(String scope) throws Exception {
		return QueryHelper.getAllSearchableFieldsPerCollection(scope);
	}
	
	public static Map<String, Set<String>> getAllPresentableFieldsPerCollection(String scope) throws Exception {
		return QueryHelper.getAllPresentableFieldsPerCollection(scope);
	}
	
	public static Map<String, String> getAllCollectionsTypes(String scope) throws Exception {
		return QueryHelper.getAllCollectionsTypes(scope);
	}
	
	public static Map<String, Set<String>> getAllFieldsPerCollection(String scope) throws Exception {
		return QueryHelper.getAllSearchableFieldsPerCollection(scope);
	}
	
	public static void init() throws ResourceRegistryException{
		ResourceRegistry.startBridging();
	}
	
	public static void waitInit(boolean useRRAdaptor) throws ResourceRegistryException, InterruptedException{
		init();
		if (useRRAdaptor){
			logger.info("Initializing ResourceRegistry");
			try {
				ResourceRegistry.startBridging();
				TimeUnit.SECONDS.sleep(1);
				while(!ResourceRegistry.isInitialBridgingComplete()) TimeUnit.SECONDS.sleep(10);
			} catch (ResourceRegistryException e) {
				logger.error("Resource Registry could not be initialized", e);
				throw e;
			} catch (InterruptedException e) {
				logger.error("Resource Registry could not be initialized", e);
				throw e;
			}
			logger.info("Initializing ResourceRegistry is DONE");
		} else {
			logger.info("ResourceRegistry will NOT be initialized as configured");
		}
	}
}
