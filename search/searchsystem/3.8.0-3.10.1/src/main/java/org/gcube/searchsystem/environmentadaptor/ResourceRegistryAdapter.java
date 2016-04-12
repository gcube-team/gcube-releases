package org.gcube.searchsystem.environmentadaptor;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.bridge.RegistryBridge;
import gr.uoa.di.madgik.rr.element.query.QueryHelper;
import gr.uoa.di.madgik.rr.element.search.Field;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ResourceRegistryAdapter implements EnvironmentAdaptor {

	//the working scope for the ResourceRegistry
	private String scope;
	
	/**
	 * the logger for this class
	 */
	private static Logger logger = LoggerFactory.getLogger(ResourceRegistryAdapter.class.getName());
	
	public static boolean initializeAdapter() throws Exception{
		try{
			ResourceRegistry.startBridging();
		}catch (Exception e) {
			logger.error("Could not start bridging", e);
			return false;
		}
		return true;
	}
	
	/**
	 * Default constructor which takes the working scope for the ResourceRegistry
	 * @param scope - the working scope
	 */
	public ResourceRegistryAdapter(EnvHintCollection hints) {
		if(hints.HintExists("GCubeActionScope"))
			this.scope = hints.GetHint("GCubeActionScope").Hint.Payload;
	}
	
	public Map<String, String> getFieldsMapping(String scope) throws Exception {
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
	
	public String getFieldIDFromName(String fieldName) throws Exception{
		List<Field> fields = Field.getFieldsWithName(false, fieldName);
		if(fields == null || fields.size() == 0) {
			throw new Exception("Could not find fieldId for fieldName: " + fieldName);
		}
		return fields.get(0).getID();
	}

	public Map<String, String> getAllCollections(String scope) throws Exception {
		return QueryHelper.getAllSearchableCollections(scope);
	}
	
	public Map<String, String> getAllCollectionsTypes(String scope) throws Exception {
		return QueryHelper.getAllCollectionsTypes(scope);
	}
	
	public Map<String, Set<String>> getAllSearchableFieldsPerCollection(String scope) throws Exception {
		return QueryHelper.getAllSearchableFieldsPerCollection(scope);
	}
	
	public Map<String, Set<String>> getAllPresentableFieldsPerCollection(String scope) throws Exception {
		return QueryHelper.getAllPresentableFieldsPerCollection(scope);
	}
	
	public HashMap<String, HashSet<String>> getProjectionsPerSource(Set<String> sources, Set<String> projectionsNeeded,
			HashMap<String, HashSet<String>> colLangs) throws Exception {
		return QueryHelper.getProjectionsPerSource(sources, projectionsNeeded, colLangs, scope);
	}

	public Map<String, Set<String>> getCollectionLangsByFieldRelation(Map<String, List<String>> fieldRelationMap,
			List<String> projections) throws Exception {
		return QueryHelper.getCollectionLangsByFieldRelation(fieldRelationMap, projections, scope);
	}

	public Set<String> getCollectionByFieldRelationLang(Map<String, List<String>> fieldRelationMap, String language,
			List<String> projections) throws Exception {
		return QueryHelper.getCollectionByFieldRelationLang(fieldRelationMap, language, projections, scope);
	}

	public Set<String> getLanguageByFieldRelationCol(Map<String, List<String>> fieldRelationMap, String collection,
			List<String> projections) throws Exception {
		return QueryHelper.getLanguageByFieldRelationCol(fieldRelationMap, collection, projections, scope);
	}

	public Set<String> getSourceIdsForFieldRelationCollectionLanguage(String field, String relation, String collection,
			String language, String indication) throws Exception {
		return new HashSet<String>(QueryHelper.getSourceIdsForFieldRelationCollectionLanguage(field, relation, collection, language, scope));
	}

	@Override
	public long getLastUpdate() {
		return RegistryBridge.getLastUpdate();
	}

}
