package org.gcube.data.spd.gbifplugin.search;

import static org.gcube.data.spd.gbifplugin.search.query.MappingUtils.getAsString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.data.spd.gbifplugin.Constants;
import org.gcube.data.spd.gbifplugin.search.query.MappingUtils;
import org.gcube.data.spd.gbifplugin.search.query.PagedQueryIterator;
import org.gcube.data.spd.gbifplugin.search.query.PagedQueryObject;
import org.gcube.data.spd.gbifplugin.search.query.QueryByIdentifier;
import org.gcube.data.spd.gbifplugin.search.query.QueryCondition;
import org.gcube.data.spd.gbifplugin.search.query.QueryCount;
import org.gcube.data.spd.gbifplugin.search.query.QueryType;
import org.gcube.data.spd.gbifplugin.search.query.ResultType;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.products.DataSet;
import org.gcube.data.spd.model.products.Product;
import org.gcube.data.spd.model.products.Product.ProductType;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.Taxon;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultItemSearch {

	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
	private static Logger log = LoggerFactory.getLogger(ResultItemSearch.class);
	
	private List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
	
	private String baseURL;
	
	String searchQuery;
	
	public ResultItemSearch(String baseURL, String searchQuery, Condition ... conditions){
		this.baseURL = baseURL;
		this.searchQuery = searchQuery.replaceAll(" ", "%20");
		try{
			this.queryConditions = Utils.elaborateConditions(conditions);
		}catch(Exception e){
			log.error("error elaborating conditions",e);
		}
	}
	
	public void search(ObjectWriter<ResultItem> writer,  int limit){
		PagedQueryObject queryObject = new PagedQueryObject(baseURL, ResultType.Occurrence,limit);
		queryObject.setConditions(QueryCondition.cond("scientificName",searchQuery));
		try{
			PagedQueryIterator<ResultItem> pagedIterator = new PagedQueryIterator<ResultItem>(queryObject) {

				Set<String> alreadyVisited =new HashSet<String>();
				
				@Override
				protected ResultItem getObject(Map<String, Object> mappedObject) throws Exception {
					return buildResult(mappedObject);
				}

				@Override
				protected boolean useIt(Map<String, Object> mappedObject) {
					String datasetKey = getAsString(mappedObject,"datasetKey");
					Integer taxonId = (Integer)mappedObject.get("taxonKey");
					String key = datasetKey+"|"+taxonId;
					if (alreadyVisited.contains(key)) 
						return false;
					alreadyVisited.add(key);
					return true;
				}
				
			};

			while (pagedIterator.hasNext() && writer.isAlive())
				writer.write(pagedIterator.next());
			
		}catch(Exception e){
			log.error("error writing resultItems",e);
			writer.write(new StreamBlockingException(Constants.REPOSITORY_NAME));
		}
		

	}

	ResultItem buildResult(Map<String,Object> singleObject) throws Exception{
		long start = System.currentTimeMillis();
		Integer taxonId = (Integer)singleObject.get("taxonKey");
		String scientificName = getAsString(singleObject,"species");
		ResultItem resItem = new ResultItem(taxonId.toString(), scientificName );
		
		resItem.setParent(retrieveTaxon(taxonId.toString()));
		
		resItem.setScientificNameAuthorship(retrieveAuthorship(taxonId.toString()));
		
		resItem.setRank(getAsString(singleObject, "taxonRank"));		
		
		resItem.setCitation(getAsString(singleObject,"institutionCode"));
		
		DataSet dataset = DataSetRetreiver.get(getAsString(singleObject,"datasetKey"), baseURL);
		resItem.setDataSet(dataset);	

		List<Product> products = retrieveProducts(taxonId.toString(), dataset);
		resItem.setProducts(products);

		String credits = "Biodiversity occurrence data published by: "+dataset.getDataProvider().getName()+" (Accessed through GBIF Data Portal, data.gbif.org, "+format.format(Calendar.getInstance().getTime())+")";
		resItem.setCredits(credits);
		log.trace("[Benchmark] time to retrieve ResultItem is "+(System.currentTimeMillis()-start));
		return resItem;
	}

	private String retrieveAuthorship(String taxonId) throws Exception {
		QueryByIdentifier query = new QueryByIdentifier(baseURL, taxonId, QueryType.Taxon);
		Map<String, Object> mapping = MappingUtils.getObjectMapping(query.build());
		if (mapping.containsKey("authorship"))
			return getAsString(mapping, "authorship");
		else return "";
	}

	private Taxon retrieveTaxon(String taxonId) throws Exception {
		long start = System.currentTimeMillis();
		QueryByIdentifier query = new QueryByIdentifier(baseURL, taxonId, QueryType.Taxon);
		query.addPath("parents");
		LinkedList<HashMap<String, Object>> parentsList = MappingUtils.getObjectList(query.build());
		Taxon parentTaxon = null;
		for(HashMap<String, Object> mappedObject : parentsList){
			Taxon taxon = new Taxon(((Integer)mappedObject.get("key")).toString(), getAsString(mappedObject, "scientificName"));
			taxon.setCitation(getAsString(mappedObject, "accordingTo"));
			taxon.setRank(getAsString(mappedObject, "rank"));
			if (parentTaxon!=null)
				taxon.setParent(parentTaxon);
			parentTaxon = taxon;
		}
		log.trace("[Benchmark] time to retrieve taxon is "+(System.currentTimeMillis()-start));
		return parentTaxon;
	}

	private List<Product> retrieveProducts( String taxonId,  DataSet dataset,  Condition ... properties) throws Exception{
		long start = System.currentTimeMillis();
		QueryCount occurrencesQuery = new QueryCount(baseURL, ResultType.Occurrence);
		occurrencesQuery.setConditions(QueryCondition.cond("taxonKey",taxonId), QueryCondition.cond("datasetKey", dataset.getId()), QueryCondition.cond("hasCoordinate","true"));
		String productId = Utils.createProductsKey(Utils.getDataSetAsString(dataset), taxonId, this.queryConditions);
		Product product = new Product(ProductType.Occurrence, productId);
		product.setCount(occurrencesQuery.getCount());
		log.trace("[Benchmark] time to retrieve product is "+(System.currentTimeMillis()-start));
		return Arrays.asList(product);
	}

	
}
