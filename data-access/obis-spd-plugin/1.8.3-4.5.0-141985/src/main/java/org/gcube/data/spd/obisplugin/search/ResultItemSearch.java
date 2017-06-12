package org.gcube.data.spd.obisplugin.search;

import static org.gcube.data.spd.obisplugin.search.query.MappingUtils.getAsString;
import static org.gcube.data.spd.obisplugin.search.query.MappingUtils.getAsInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.products.DataSet;
import org.gcube.data.spd.model.products.Product;
import org.gcube.data.spd.model.products.Product.ProductType;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.Taxon;
import org.gcube.data.spd.obisplugin.Constants;
import org.gcube.data.spd.obisplugin.search.query.MappingUtils;
import org.gcube.data.spd.obisplugin.search.query.PagedQueryIterator;
import org.gcube.data.spd.obisplugin.search.query.PagedQueryObject;
import org.gcube.data.spd.obisplugin.search.query.QueryByIdentifier;
import org.gcube.data.spd.obisplugin.search.query.QueryCondition;
import org.gcube.data.spd.obisplugin.search.query.QueryCount;
import org.gcube.data.spd.obisplugin.search.query.QueryType;
import org.gcube.data.spd.obisplugin.search.query.ResultType;
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
		this.searchQuery = searchQuery.replaceAll(" ", "%20").trim();
		this.searchQuery = this.searchQuery.substring(0, 1).toUpperCase()+this.searchQuery.substring(1, this.searchQuery.length()).toLowerCase();
		try{
			this.queryConditions = Utils.elaborateConditions(conditions);
		}catch(Exception e){
			log.error("error elaborating conditions",e);
		}
	}
	
	public void search(ObjectWriter<ResultItem> writer,  int limit){
		PagedQueryObject queryObject = new PagedQueryObject(baseURL, ResultType.Occurrence,limit);
		queryObject.setConditions(QueryCondition.cond("scientificname",searchQuery));
		queryObject.getConditions().addAll(this.queryConditions);
		try{
			PagedQueryIterator<ResultItem> pagedIterator = new PagedQueryIterator<ResultItem>(queryObject) {

				Set<String> alreadyVisited =new HashSet<String>();
				
				@Override
				protected ResultItem getObject(Map<String, Object> mappedObject) throws Exception {
					log.debug("retrieved mapped object");
					return buildResult(mappedObject);
				}

				@Override
				protected boolean useIt(Map<String, Object> mappedObject) {
					String datasetKey = ((Integer)mappedObject.get("resourceID")).toString();
					Integer taxonId = (Integer)mappedObject.get("obisID");
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
		try{
		long start = System.currentTimeMillis();
		Integer taxonId = getAsInteger(singleObject,"obisID");
		String scientificName = getAsString(singleObject,"scientificName");
		ResultItem resItem = new ResultItem(taxonId.toString(), scientificName );
		
		String scientificNameAuthorship = getAsString(singleObject,"scientificNameAuthorship");
		
		QueryByIdentifier query = new QueryByIdentifier(baseURL, taxonId.toString(), QueryType.Taxon);
		Map<String, Object> singleTaxon = MappingUtils.getObjectMapping(query.build());
		
		
		
		resItem.setScientificNameAuthorship(scientificNameAuthorship);
		
		resItem.setRank(getAsString(singleTaxon, "rank_name"));		
		
		//resItem.setCitation(getAsString(singleTaxon,"tauthor"));
		
		resItem.setParent(retrieveParentTaxon(getAsInteger(singleTaxon,"parent_id")));
		
		DataSet dataset = DataSetRetreiver.get(getAsInteger(singleObject,"resourceID").toString(), baseURL);
		resItem.setDataSet(dataset);	

		List<Product> products = retrieveProducts(taxonId.toString(), dataset);
		resItem.setProducts(products);

		String credits = "Biodiversity occurrence accessed through OBIS WebService, http://api.iobis.org/, "+format.format(Calendar.getInstance().getTime())+")";
		resItem.setCredits(credits);
		log.trace("[Benchmark] time to retrieve ResultItem is "+(System.currentTimeMillis()-start));
		log.debug("found species {} with authorship {}",scientificName, scientificNameAuthorship);
		return resItem;
		}catch(Exception e){
			throw e;
		}
	}

	private Taxon retrieveParentTaxon(Integer parentTaxonId) throws Exception {
		if (parentTaxonId==0) return null;
		long start = System.currentTimeMillis();
		
		Integer taxonId = parentTaxonId;
		Taxon previousTaxon = null;
		Taxon taxonToReturn = null;
		do{
			QueryByIdentifier query = new QueryByIdentifier(baseURL, taxonId.toString(), QueryType.Taxon);
			Map<String, Object> singleTaxon = MappingUtils.getObjectMapping(query.build());
			
			Taxon taxon = new Taxon(getAsInteger(singleTaxon, "id").toString(), getAsString(singleTaxon, "tname"));
			taxon.setScientificNameAuthorship(getAsString(singleTaxon, "tauthor"));
			//taxon.setCitation(getAsString(mappedObject, "accordingTo"));
			taxon.setRank(getAsString(singleTaxon, "rank_name"));
			if (previousTaxon!=null)
				previousTaxon.setParent(taxon);
			previousTaxon = taxon;
			taxonId = getAsInteger(singleTaxon, "parent_id");
			if (taxonToReturn==null)
				taxonToReturn = taxon;
		} while (taxonId>0);
		
		log.trace("[Benchmark] time to retrieve taxon is "+(System.currentTimeMillis()-start));
		return taxonToReturn;
	}

	private List<Product> retrieveProducts( String taxonId,  DataSet dataset) throws Exception{
		long start = System.currentTimeMillis();
		QueryCount occurrencesQuery = new QueryCount(baseURL, ResultType.Occurrence);
		occurrencesQuery.setConditions(QueryCondition.cond("obisid",taxonId), QueryCondition.cond("resourceid", dataset.getId()));
		occurrencesQuery.getConditions().addAll(this.queryConditions);
		String productId = Utils.createProductsKey(Utils.getDataSetAsString(dataset), taxonId, this.queryConditions);
		Product product = new Product(ProductType.Occurrence, productId);
		product.setCount(occurrencesQuery.getCount());
		log.trace("[Benchmark] time to retrieve product is "+(System.currentTimeMillis()-start));
		return Arrays.asList(product);
	}

	
}
