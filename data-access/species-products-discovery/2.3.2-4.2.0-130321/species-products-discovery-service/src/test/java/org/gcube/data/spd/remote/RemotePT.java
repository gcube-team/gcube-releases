package org.gcube.data.spd.remote;

import static org.gcube.data.streams.dsl.Streams.convert;
import static org.gcube.data.streams.dsl.Streams.pipe;
import static org.gcube.data.streams.dsl.Streams.publishStringsIn;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.StringField;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.ObjectExistsException;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.caching.MyCacheEventListener;
import org.gcube.data.spd.gbifplugin.GBIFPlugin;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.remotedispatcher.RemoteDispatcherPT;
import org.gcube.data.spd.stubs.ExpandWithSynonymsRequest;
import org.gcube.data.spd.stubs.GetOccurrencesByProductKeysRequest;
import org.gcube.data.spd.stubs.NamesMappingRequest;
import org.gcube.data.spd.stubs.SearchCondition;
import org.gcube.data.spd.stubs.SearchRequest;
import org.gcube.data.spd.stubs.UnfoldRequest;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.exceptions.StreamSkipSignal;
import org.gcube.data.streams.exceptions.StreamStopSignal;
import org.gcube.data.streams.generators.Generator;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RemotePT {
	
	//private static final AbstractPlugin obis = new WormsPlugin();
	
	private static final AbstractPlugin gbif = new GBIFPlugin();
	
	//private static final AbstractPlugin col = new CatalogueOfLifePlugin();
	
	//private static final AbstractPlugin worms = new WormsPlugin();
	
	private static final AbstractPlugin[] plugins = new AbstractPlugin[]{  gbif };
			
	private static CacheManager cacheManager;
	
	private static final String scope ="/gcube/devsec";
	
	private static Logger logger = LoggerFactory.getLogger(RemotePT.class);
	
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
	public void searchProducts(){
		RemoteDispatcherPT dispatcher = new RemoteDispatcherPT(gbif, Executors.newCachedThreadPool());
		try {
			String result = dispatcher.search(new SearchRequest("", new SearchCondition[0], "resultItem", "sarda"));
			Assert.assertNotNull(result);
			logger.trace("resultSet is :"+result);
			Stream<String> stream = pipe(convert(new URI(result)).of(GenericRecord.class).withDefaults()).through(new Generator<GenericRecord, String>() {

				@Override
				public String yield(GenericRecord element) throws StreamSkipSignal,
						StreamStopSignal {
					try {
						return ((StringField)element.getField("result")).getPayload();
					} catch (Exception e) {
						throw new StreamStopSignal();
					} 
				}
			});
			
			while (stream.hasNext())
				logger.trace(stream.next());
			
		} catch (Exception e) {
			logger.error("errror searching with remote pt ",e);
		}
		
	}
	
	@Test
	public void searchOccurrence(){
		RemoteDispatcherPT dispatcher = new RemoteDispatcherPT(gbif, Executors.newCachedThreadPool());
		try {
			String result = dispatcher.search(new SearchRequest("", new SearchCondition[0], "occurrence", "sarda"));
			Assert.assertNotNull(result);
			logger.trace("resultSet is :"+result);
			Stream<String> stream = pipe(convert(new URI(result)).of(GenericRecord.class).withDefaults()).through(new Generator<GenericRecord, String>() {

				@Override
				public String yield(GenericRecord element) throws StreamSkipSignal,
						StreamStopSignal {
					try {
						return ((StringField)element.getField("result")).getPayload();
					} catch (Exception e) {
						throw new StreamStopSignal();
					} 
				}
			});
			
			while (stream.hasNext())
				logger.trace(stream.next());
			
		} catch (Exception e) {
			logger.error("errror searching with remote pt ",e);
		}
		
	}
	
	@Test
	public void searchTaxon(){
		RemoteDispatcherPT dispatcher = new RemoteDispatcherPT(gbif, Executors.newCachedThreadPool());
		try {
			String result = dispatcher.search(new SearchRequest("", new SearchCondition[0], "taxon", "Panulirus argus"));
			Assert.assertNotNull(result);
			logger.trace("resultSet is :"+result);
			Stream<String> stream = pipe(convert(new URI(result)).of(GenericRecord.class).withDefaults()).through(new Generator<GenericRecord, String>() {

				@Override
				public String yield(GenericRecord element) throws StreamSkipSignal,
						StreamStopSignal {
					try {
						return ((StringField)element.getField("result")).getPayload();
					} catch (Exception e) {
						throw new StreamStopSignal();
					} 
				}
			});
			
			while (stream.hasNext())
				logger.trace(stream.next());
			
		} catch (Exception e) {
			logger.error("errror searching with remote pt ",e);
		}
		
	}
	
	@Test
	public void namesMapping(){
		RemoteDispatcherPT dispatcher = new RemoteDispatcherPT(gbif, Executors.newCachedThreadPool());
		try {
			String result = dispatcher.namesMapping(new NamesMappingRequest("shark",""));
			Assert.assertNotNull(result);
			logger.trace("resultSet is :"+result);
			Stream<String> stream = pipe(convert(new URI(result)).of(GenericRecord.class).withDefaults()).through(new Generator<GenericRecord, String>() {

				@Override
				public String yield(GenericRecord element) throws StreamSkipSignal,
						StreamStopSignal {
					try {
						return ((StringField)element.getField("result")).getPayload();
					} catch (Exception e) {
						throw new StreamStopSignal();
					} 
				}
			});
			
			while (stream.hasNext())
				logger.trace(stream.next());
			
		} catch (Exception e) {
			logger.error("errror searching with remote pt ",e);
		}
		
	}
	
	@Test
	public void synonymsExpansion(){
		RemoteDispatcherPT dispatcher = new RemoteDispatcherPT(gbif, Executors.newCachedThreadPool());
		try {
			String result = dispatcher.expandWithSynonyms(new ExpandWithSynonymsRequest("", "sarda sarda"));
			Assert.assertNotNull(result);
			logger.trace("resultSet is :"+result);
			Stream<String> stream = pipe(convert(new URI(result)).of(GenericRecord.class).withDefaults()).through(new Generator<GenericRecord, String>() {

				@Override
				public String yield(GenericRecord element) throws StreamSkipSignal,
						StreamStopSignal {
					try {
						return ((StringField)element.getField("result")).getPayload();
					} catch (Exception e) {
						throw new StreamStopSignal();
					} 
				}
			});
			
			while (stream.hasNext())
				logger.trace(stream.next());
			
		} catch (Exception e) {
			logger.error("errror searching with remote pt ",e);
		}
		
	}
	
	@Test
	public void unfold(){
		RemoteDispatcherPT dispatcher = new RemoteDispatcherPT(gbif, Executors.newCachedThreadPool());
		try {
			String result = dispatcher.unfold(new UnfoldRequest("", "cervidae"));
			Assert.assertNotNull(result);
			logger.trace("resultSet is :"+result);
			Stream<String> stream = pipe(convert(new URI(result)).of(GenericRecord.class).withDefaults()).through(new Generator<GenericRecord, String>() {

				@Override
				public String yield(GenericRecord element) throws StreamSkipSignal,
						StreamStopSignal {
					try {
						return ((StringField)element.getField("result")).getPayload();
					} catch (Exception e) {
						throw new StreamStopSignal();
					} 
				}
			});
			
			while (stream.hasNext())
				logger.trace(stream.next());
			
		} catch (Exception e) {
			logger.error("errror searching with remote pt ",e);
		}
		
	}
	
	@Test
	public void getOccurrenceByKeys(){
		RemoteDispatcherPT dispatcher = new RemoteDispatcherPT(gbif, Executors.newCachedThreadPool());
		List<String> occurrenceKeys=Arrays.asList("sarda||130||11956||57744173||","sarda||82||400||50917042||", 
				"sarda||427||14113||60499431||");
		try {
			String result = dispatcher.getOccurrencesByProductKeys(new GetOccurrencesByProductKeysRequest("GBIF",  publishStringsIn(convert(occurrenceKeys)).withDefaults().toString()));
			Assert.assertNotNull(result);
			logger.trace("resultSet is :"+result);
			Stream<String> stream = pipe(convert(new URI(result)).of(GenericRecord.class).withDefaults()).through(new Generator<GenericRecord, String>() {

				@Override
				public String yield(GenericRecord element) throws StreamSkipSignal,
						StreamStopSignal {
					try {
						return ((StringField)element.getField("result")).getPayload();
					} catch (Exception e) {
						throw new StreamStopSignal();
					} 
				}
			});
			
			while (stream.hasNext())
				logger.trace("---------------"+stream.next());
			
		} catch (Exception e) {
			logger.error("errror searching with remote pt ",e);
		}
		
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
	
	private ServiceEndpoint retrieveRR(String name){
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository'");
		query.addCondition("$resource/Profile/Name/text() eq '"+name+"'");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);
		if (resources.size()>0)return resources.get(0);
		else return null;
	}
}
