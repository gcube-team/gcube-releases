package org.gcube.searchsystem.environmentadaptor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Classes that implement this interface provide the basic functionality
 * needed by the search system, in order to discover the sources that could 
 * be part of a search operation
 * 
 * @author vasilis verroios
 *
 */
public interface EnvironmentAdaptor {
	
	/**
	 * Get the fields projected by each of the sources of the input. 
	 * For including a field in the output(for a specific source) this 
	 * field must be presentable for all the collection-language pairs
	 * specified in the input(for this specific source) 
	 * @param sources - the sources of interest
	 * @param projectionsNeeded - the fields of interest
	 * @param colLangs - the collection-language pairs of interest
	 * @return a map containing the fields for each source
	 */
	public HashMap<String, HashSet<String>> getProjectionsPerSource(
			Set<String> sources, Set<String> projectionsNeeded,
			HashMap<String, HashSet<String>> colLangs) throws Exception;
	
	/**
	 * Get the collection-languages pairs for a number of criteria. 
	 * The criteria are specified by field-relation pairs. The return 
	 * map will have all the collection-language pairs for which there 
	 * are sources for all the searchable field - supported relation pairs 
	 * specified. The projections is an optional field, which specifies
	 * which are the presentable fields that must be published by sources 
	 * for a collection-language pair
	 * @param fieldRelationMap - the search criteria specified
	 * @param projections - the presentation criteria specified
	 * @return the collection-language pairs satisfying the criteria
	 */
	public Map<String, Set<String>> getCollectionLangsByFieldRelation(
			Map<String, List<String>> fieldRelationMap, 
			List<String> projections) throws Exception;
	
	/**
	 * Get the collections for a number of criteria and a specific language. 
	 * The criteria are specified by field-relation pairs. The return 
	 * map will have all the collections for which there 
	 * are sources for all the (language+searchable)(meaning that the source publishes
	 * this field as searchable for this specific language and a 
	 * collection X) field - supported relation pairs 
	 * specified. The projections is an optional field, which specifies
	 * which are the (language+presentable) fields that must be published by sources 
	 * for a collection
	 * @param fieldRelationMap - the search criteria specified
	 * @param language - the language specifed
	 * @param projections - the presentation criteria specified
	 * @return the list of collections satisfying the criteria for the language specified
	 */
	public Set<String> getCollectionByFieldRelationLang(
			Map<String, List<String>> fieldRelationMap, 
			String language, List<String> projections) throws Exception;
	
	/**
	 * Get the languages for a number of criteria and a specific collection. 
	 * The criteria are specified by field-relation pairs. The return 
	 * map will have all the languages for which there 
	 * are sources for all the (collection+searchable)(meaning that the source publishes
	 * this field as searchable for this specific collection and a 
	 * language X) field - supported relation pairs 
	 * specified. The projections is an optional field, which specifies
	 * which are the (collection+presentable) fields that must be published by sources 
	 * for a language
	 * @param fieldRelationMap - the search criteria specified
	 * @param collection - the collection specified
	 * @param projections - the presentation criteria specified
	 * @return the list of languages satisfying the criteria for the collection specified
	 */
	public Set<String> getLanguageByFieldRelationCol(
			Map<String, List<String>> fieldRelationMap, 
			String collection, List<String> projections) throws Exception;
	
	/**
	 * Get source identifiers for all the sources that publish a searchable field
	 * for a specific collection and language AND they also support the specified
	 * relation, and they provide the capability(e.g. rank) specified in the indication argument.
	 * @param field - the searchable field specified
	 * @param relation - the relation specified
	 * @param collection - the collection specified
	 * @param language - the language specified
	 * @param indication - the capability specified
	 * @return the set of source identifiers - note that these identifiers can be 
	 * internal to the adaptor and in the next search stages(e.g. workflow), the same adaptor 
	 * must be used.
	 */
	public Set<String> getSourceIdsForFieldRelationCollectionLanguage(
			String field, String relation, String collection, 
			String language, String indication) throws Exception;
	
	public long getLastUpdate();

}
