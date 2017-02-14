package org.gcube.data.spd.search;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.ObjectExistsException;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.caching.MyCacheEventListener;
import org.gcube.data.spd.gbifplugin.GBIFPlugin;
import org.gcube.data.spd.manager.ResultItemWriterManager;
import org.gcube.data.spd.manager.search.Search;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.Searchable;
import org.gcube.data.spd.plugin.fwk.readers.LocalReader;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.LocalWrapper;
import org.gcube.dataaccess.spql.SPQLQueryParser;
import org.gcube.dataaccess.spql.model.Query;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchFlowTest {

	private Logger logger = LoggerFactory.getLogger(SearchFlowTest.class);
	
	private static final AbstractPlugin gbif= new GBIFPlugin();
	
	//private static final AbstractPlugin worms = new WormsPlugin();
	
	//private static final AbstractPlugin obis = new ObisPlugin();
	
	//private static final AbstractPlugin brazilianFlora = new FloraPlugin();
	
	//private static final AbstractPlugin catalogueOfLife = new CatalogueOfLifePlugin();
	
	private static final AbstractPlugin[] plugins = new AbstractPlugin[]{gbif/*, worms, obis, brazilianFlora, catalogueOfLife*/ };
	
	@SuppressWarnings({ "unchecked", "unused" })
	private static final Searchable<ResultItem>[] searchables = new Searchable[]{gbif/*brazilianFlora, obis, catalogueOfLife*/};
	
	
	private static CacheManager cacheManager;
	
	private static final String scope ="/gcube/devsec";
	
	@Before
	public  void initPlugins() throws Exception{
		cacheManager= CacheManager.getInstance();
		ScopeProvider.instance.set(scope);
		for (AbstractPlugin plugin : plugins){
			plugin.initialize(retrieveRR(plugin.getRepositoryName()));
			if (plugin.isUseCache())
				createCache(plugin.getRepositoryName(), cacheManager);
		}
			
	}
	
	@Test
	public void customSearch() throws Exception{
		ScopeProvider.instance.set(scope);
		search("SEARCH BY CN 'shark' RESOLVE WITH WoRMS EXPAND RETURN Product");
	}
	
	@Test
	public void simpleSearchTest() throws Exception{
		ScopeProvider.instance.set(scope);
		search("SEARCH BY SN 'sarda sarda' RETURN Product");
	}
	
	@Test
	public void mapping() throws Exception{
		ScopeProvider.instance.set(scope);
		search("SEARCH BY CN 'shark' RESOLVE in OBIS, GBIF RETURN Product");
	}
	
	@Test
	public void expander() throws Exception{
		ScopeProvider.instance.set(scope);
		search("SEARCH BY SN 'Gadus morhua' EXPAND RETURN Product");
	}
	
	@Test
	public void expandAndMap() throws Exception{
		ScopeProvider.instance.set(scope);
		search("SEARCH BY CN 'atlantic cod' RESOLVE WITH OBIS EXPAND WITH WoRMS RETURN Product");
	}
	
	@Test
	public void mappingWithHaving() throws Exception{
		ScopeProvider.instance.set(scope);
		search("SEARCH BY SN 'Carcharhinus leucas' RETURN Product HAVING exl(\"rank=~'[sS]pecies'\")");
	}
	
	@Test
	public void havingOnlySubspecies() throws Exception{
		ScopeProvider.instance.set(scope);
		search("SEARCH BY SN 'sarda sarda' EXPAND, CN 'shark' RESOLVE RETURN Product HAVING exl(\"rank=~'[sS]pecies'\")");
	}
	
	
	@Test
	public void havingSomethingAsParent() throws Exception{
		ScopeProvider.instance.set(scope);
		search("SEARCH BY SN 'sarda sarda' EXPAND, CN 'shark' RESOLVE RETURN Product HAVING xpath(\"//parent[translate(rank, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='genus' and translate(scientificName, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='scombridae']\")");
	}
	
	@Test
	public void queryWithUnfold() throws Exception{
		ScopeProvider.instance.set(scope);
		search("SEARCH BY SN 'cervidae' UNFOLD WITH CatalogueOfLife return Taxon");
	}
		
	private void search(String queryString) throws Exception{
		DBSession.initialize("org.gcube.dbinterface.h2", "sa", "", "file:/tmp/spd-cache");
		
		LocalWrapper<ResultItem> wrapper = new LocalWrapper<ResultItem>();
		Search<ResultItem> search =new Search<ResultItem>(wrapper, Executors.newCachedThreadPool(), cacheManager, ResultItemWriterManager.class);
		search.setSystemPlugins(getSystemPlugins());
		Query query = parse(queryString);
		
		//Collections.singletonMap(gbif.getRepositoryName(), (Searchable<ResultItem>)gbif)
		search.search(getSearchables(), query);
		LocalReader<ResultItem> reader = new LocalReader<ResultItem>(wrapper);
		int i=0;
		HashMap<String, Integer> repos = new HashMap<String, Integer>();
		while (reader.hasNext()){
			ResultItem re = (ResultItem)reader.next();
			//System.out.println(re.toString());
			if (repos.containsKey(re.getProvider())){
				Integer size =repos.get(re.getProvider());
				repos.put(re.getProvider(), size+1);
			}else repos.put(re.getProvider(), 1);
			//System.out.println("("+value+")"+re);
			i++;
			if (i==10) {
				logger.trace("|||||||||||||||||||||||CLOSING READER|||||||||||||||||||");
				reader.close();
				logger.trace("|||||||||||||||||||||||WRAPPER CLOSED?|||||||||||||||||||"+wrapper.isClosed());
				break;
			}
			logger.debug(re.getParent()!=null?re.getScientificName():" parent is null");
		}
		

		for (Entry<String, Integer> entry: repos.entrySet())
			logger.debug(entry.getKey()+" are "+entry.getValue());

		logger.debug("total is "+i);
		
		System.in.read();
		
		cacheManager.shutdown();
	}
	
	private ServiceEndpoint retrieveRR(String name){
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository'");
		query.addCondition("$resource/Profile/Name/text() eq '"+name+"'");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);
		if (resources.size()>0)return resources.get(0);
		else return null;
	}
	
	private Map<String, AbstractPlugin> getSystemPlugins(){
		HashMap<String, AbstractPlugin> pluginMap = new HashMap<String, AbstractPlugin>();
		for (AbstractPlugin plugin : plugins)
			pluginMap.put(plugin.getRepositoryName(), plugin);
		return pluginMap;
		
	}
	
	private Map<String, Searchable<ResultItem>> getSearchables(){
		HashMap<String,Searchable<ResultItem>> pluginMap = new HashMap<String, Searchable<ResultItem>>();
		for (AbstractPlugin plugin : plugins)
			pluginMap.put(plugin.getRepositoryName(), plugin);
		return pluginMap;
		
	}
	
	private  void createCache(String pluginName, CacheManager cacheManager){
		try{

			Cache pluginCache = new Cache( new CacheConfiguration(pluginName, 10)
			.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
			.overflowToDisk(false)
			.eternal(false)
			.timeToLiveSeconds(60*60*24*7)
			.timeToIdleSeconds(0)
			.diskPersistent(true)
			.diskExpiryThreadIntervalSeconds(0)
			.diskStorePath("/tmp"));

			pluginCache.getCacheEventNotificationService().registerListener(new MyCacheEventListener());

			cacheManager.addCache(pluginCache);
			logger.trace("cache created for plugin "+ pluginName);
		}catch (ObjectExistsException e) {
			logger.warn("the cache for plugin "+pluginName+" already exists");
		}
	}
	
	private Query parse(String query) throws Exception{
		Query  result;
		try{
			result = SPQLQueryParser.parse(query);
		}catch (Exception e) {
			throw e;
		}
		return result;
	}
}
