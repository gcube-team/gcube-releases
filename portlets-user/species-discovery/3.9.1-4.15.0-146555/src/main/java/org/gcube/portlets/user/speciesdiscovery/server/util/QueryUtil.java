package org.gcube.portlets.user.speciesdiscovery.server.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gcube.dataaccess.spql.SPQLQueryParser;
import org.gcube.dataaccess.spql.model.Query;
import org.gcube.dataaccess.spql.model.Term;
import org.gcube.dataaccess.spql.model.ret.ReturnType;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchByQueryParameter;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchFilters;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchResultType;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchServiceException;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchType;
import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class QueryUtil {

	protected static Logger logger = Logger.getLogger(QueryUtil.class);

	public static SearchByQueryParameter getQueryResultType(String queryText) throws SearchServiceException
	{
		try {
			
			Query query = SPQLQueryParser.parse(queryText);
			
			Map<SearchType, List<String>> terms = new HashMap<SearchType, List<String>>();
			
			for (Term term:query.getTerms()) {
				switch (term.getType()) {
					case COMMON_NAME: terms.put(SearchType.BY_COMMON_NAME, term.getWords()); break;
					case SCIENTIFIC_NAME: terms.put(SearchType.BY_SCIENTIFIC_NAME, term.getWords()); break;
				}
			}
			
			logger.trace("found terms: "+terms);
			
			ReturnType returnType = query.getReturnType();
			SearchResultType searchResultType = SearchResultType.SPECIES_PRODUCT;
			
			switch (returnType) {
				case OCCURRENCE: searchResultType = SearchResultType.OCCURRENCE_POINT; break;
				case PRODUCT: searchResultType = SearchResultType.SPECIES_PRODUCT; break;
				case TAXON: searchResultType = SearchResultType.TAXONOMY_ITEM; break;
			}
			
			logger.trace("found returnType: "+searchResultType);
			
			return new SearchByQueryParameter(terms, searchResultType);
			
		} catch (Exception e) {
			logger.warn("Error parsing the user query", e);
			throw new SearchServiceException("Wrong query: "+e.getMessage());
		}
	}
	
	public static SearchResultType getResultType(SearchFilters searchFilters)
	{
		
		switch (searchFilters.getResultType()) {
			case RESULTITEM: return SearchResultType.SPECIES_PRODUCT;
			case TAXONOMYITEM: return SearchResultType.TAXONOMY_ITEM;
			default: logger.error("Unknow return type: "+searchFilters.getResultType());
		}
		return null;
	}
	
	public static SpeciesCapability getResultType(SearchResultType resultType)
	{
		
		switch (resultType) {
			case SPECIES_PRODUCT: return SpeciesCapability.RESULTITEM;
			case OCCURRENCE_POINT: return SpeciesCapability.OCCURRENCESPOINTS;
			case TAXONOMY_ITEM: return SpeciesCapability.TAXONOMYITEM;
		}
		return null;
	}
}
