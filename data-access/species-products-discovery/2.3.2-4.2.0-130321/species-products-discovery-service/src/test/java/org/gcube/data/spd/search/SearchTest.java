package org.gcube.data.spd.search;

import java.util.Collections;
import java.util.concurrent.Executors;
import org.gcube.data.spd.manager.ResultItemWriterManager;
import org.gcube.data.spd.manager.search.Search;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.plugin.TestPlugin;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.Searchable;
import org.gcube.data.spd.plugin.fwk.readers.LocalReader;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.LocalWrapper;
import org.gcube.data.spd.stubs.QueryNotValidFault;
import org.gcube.dataaccess.spql.SPQLQueryParser;
import org.gcube.dataaccess.spql.model.Query;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SearchTest {

	Logger logger = LoggerFactory.getLogger(SearchTest.class);	
	

		
	@Test
	public void simpleSearchTest() throws Exception{
		search("SEARCH BY SN 'pippo' RETURN Occurrence");
	}
	
	@Test
	public void resolverSearchTest() throws Exception{
		search("SEARCH BY CN 'pluto' RESOLVE WITH first RETURN Product");
	}
	
	@Test
	public void expanderSearchTest() throws Exception{
		search("SEARCH BY SN 'pippo' EXPAND WITH first RETURN occurrence");
	}
	
	@Test
	public void resolveAndExpandSearchTest() throws Exception{
		search("SEARCH BY CN 'pluto' RESOLVE WITH first EXPAND WITH first RETURN OCCURRENCE");
	}
	
	@Test
	public void multipleNameTypesSearchTest() throws Exception{
		search("SEARCH BY CN 'pluto' RESOLVE WITH first EXPAND WITH first" +
				", SN 'pippo' EXPAND WITH first RETURN Product");
	}
	
	
	
	private Query parse(String query) throws Exception{
		Query  result;
		try{
			result = SPQLQueryParser.parse(query);
		}catch (Exception e) {
			logger.error("error parsing the query",e);
			throw new QueryNotValidFault();
		}
		return result;
	}
	
	private void search(String queryString) throws Exception{
		LocalWrapper<ResultItem> wrapper = new LocalWrapper<ResultItem>();
		AbstractPlugin plugin = new TestPlugin("first");
		//CacheManager cacheManager = CacheManager.getInstance(); 
		Search<ResultItem> search =new Search<ResultItem>(wrapper, Executors.newFixedThreadPool(10), null, ResultItemWriterManager.class);
		search.setSystemPlugins(Collections.singletonMap(plugin.getRepositoryName(), plugin));
		Query query = parse(queryString);
		search.search(Collections.singletonMap(plugin.getRepositoryName(), (Searchable<ResultItem>)plugin), query);
		LocalReader<ResultItem> reader = new LocalReader<ResultItem>(wrapper);
		logger.trace("strart reading");
		while (reader.hasNext()){
			logger.trace(reader.next().getScientificName());
		}
		reader.close();
		logger.trace("finished");
		//cacheManager.shutdown();
	}
	
}
