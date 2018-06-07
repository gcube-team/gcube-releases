package org.gcube.data.spd.obisplugin.search;

import static org.gcube.data.spd.obisplugin.search.query.MappingUtils.getAsString;

import java.util.List;
import java.util.Map;

import org.gcube.data.spd.model.products.DataProvider;
import org.gcube.data.spd.model.products.DataSet;
import org.gcube.data.spd.obisplugin.Constants;
import org.gcube.data.spd.obisplugin.search.query.MappingUtils;
import org.gcube.data.spd.obisplugin.search.query.QueryByIdentifier;
import org.gcube.data.spd.obisplugin.search.query.QueryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSetRetreiver {

	private static Logger log = LoggerFactory.getLogger(DataSetRetreiver.class);

	@SuppressWarnings("unchecked")
	public static DataSet get(String key, String baseURL) throws Exception{
		long start = System.currentTimeMillis();
		QueryByIdentifier datasetQuery = new QueryByIdentifier(baseURL, key, QueryType.Dataset);
		Map<String, Object> mapping = MappingUtils.getObjectMapping(datasetQuery.build());
		DataSet dataset = new DataSet(key);
		dataset.setName(getAsString(mapping,"name"));



		String citation = getAsString(mapping,"citation");
		if (citation ==null){
			List<Map<String, Object>> institutionMapping = (List<Map<String, Object>>)mapping.get("institutes");
			if (institutionMapping.size()>0){
				if (getAsString(institutionMapping.get(0),"parent")!=null)
					citation += " - "+getAsString(institutionMapping.get(0),"parent");
				dataset.setCitation(citation);
			}
		}

		String providerKey = key;
		Map<String, Object> providerMapping = (Map<String, Object>)mapping.get("provider");
		DataProvider provider = new DataProvider(providerKey);
		if (providerMapping!=null)
			provider.setName(getAsString(providerMapping,"name"));
		else 
			provider.setName(Constants.REPOSITORY_NAME);
		
		dataset.setDataProvider(provider);
		
		log.trace("[Benchmark] time to retrieve dataset is "+(System.currentTimeMillis()-start));
		return dataset;
	}

}
