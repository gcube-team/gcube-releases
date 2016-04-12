package org.gcube.data.spd.gbifplugin.search;

import static org.gcube.data.spd.gbifplugin.search.query.MappingUtils.*;

import java.util.Map;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.gbifplugin.search.query.MappingUtils;
import org.gcube.data.spd.gbifplugin.search.query.QueryByIdentifier;
import org.gcube.data.spd.gbifplugin.search.query.QueryType;
import org.gcube.data.spd.model.products.DataProvider;
import org.gcube.data.spd.model.products.DataSet;

public class DataSetRetreiver {

	private static GCUBELog log = new GCUBELog(DataSetRetreiver.class);
	
	@SuppressWarnings("unchecked")
	public static DataSet get(String key, String baseURL) throws Exception{
		long start = System.currentTimeMillis();
		QueryByIdentifier datasetQuery = new QueryByIdentifier(baseURL, key, QueryType.Dataset);
		Map<String, Object> mapping = MappingUtils.getObjectMapping(datasetQuery.build());
		DataSet dataset = new DataSet(key);
		dataset.setName(getAsString(mapping,"title"));
		dataset.setCitation(getAsString((Map<String, Object>)mapping.get("citation"),"text"));
		String providerKey = getAsString(mapping,"publishingOrganizationKey");
		dataset.setDataProvider(getDataProvider(providerKey, baseURL));
		log.trace("[Benchmark] time to retrieve dataset is "+(System.currentTimeMillis()-start));
		return dataset;
	}
	
	private static DataProvider getDataProvider(String key, String baseURL) throws Exception{
		QueryByIdentifier datasetQuery = new QueryByIdentifier(baseURL, key, QueryType.Organization);
		Map<String, Object> mapping = MappingUtils.getObjectMapping(datasetQuery.build());
		
		DataProvider dataProvider = new DataProvider(key);
		dataProvider.setName(getAsString(mapping, "title"));
		return dataProvider;
		
	}
	
}
