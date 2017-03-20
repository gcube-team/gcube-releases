package org.gcube.rest.index.service.helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.global.GlobalBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.gcube.rest.index.common.Constants;
import org.gcube.rest.index.common.entities.CollectionInfo;
import org.gcube.rest.index.common.entities.fields.config.FacetType;
import org.gcube.rest.index.common.entities.fields.config.FieldConfig;
import org.gcube.rest.index.service.elements.IndexFacet;

public class IndexFacetFunctions {
	
	public static IndexFacet buildFacetRequest(Map<String, CollectionInfo> collectionInfoMap) {
		
		IndexFacet indexFacet = new IndexFacet();
//		GlobalBuilder aggregation = AggregationBuilders.global("aggregations");
		List<TermsBuilder> aggregation = new ArrayList<TermsBuilder>();
		Set<String> alreadyAdded = new HashSet<String>();
		boolean noneFacetType = true;
		
		for(CollectionInfo colInfo : collectionInfoMap.values()) {
			
			Map<String, FieldConfig> fieldsConfig = colInfo.getCollectionFieldsConfigs();
	    	
	    	for (Map.Entry<String, FieldConfig> entry : fieldsConfig.entrySet())
	    	{
	    	    FacetType fieldFacetType = entry.getValue().getFacetType();
	    	    
				if(fieldFacetType != FacetType.NONE) {
					noneFacetType = false;
					String fieldName = entry.getKey();
					if(!alreadyAdded.contains(fieldName)){
//					aggregation.subAggregation(AggregationBuilders.terms(fieldName+"."+fieldFacetType.getText()).field(fieldName+"."+fieldFacetType.getText()).size(Constants.HOW_MANY));
						aggregation.add(AggregationBuilders.terms(fieldName+"."+fieldFacetType.getText()).field(fieldName+"."+fieldFacetType.getText()).size(Constants.HOW_MANY));

						alreadyAdded.add(fieldName);
					}
					
				}
				
	    	}
		}
		
		
//		indexFacet.setGlobalbuilder(aggregation);
		indexFacet.setListOfAggregations(aggregation);
		indexFacet.setNoneFacetType(noneFacetType);
		return indexFacet;
	}

}
