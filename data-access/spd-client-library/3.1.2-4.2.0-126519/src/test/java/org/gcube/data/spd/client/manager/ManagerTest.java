package org.gcube.data.spd.client.manager;

import static org.gcube.data.spd.client.plugins.AbstractPlugin.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.client.proxies.Manager;
import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ManagerTest {

	private static final Logger log = LoggerFactory.getLogger(ManagerTest.class);
	
	private static final int QUERY_NUMBER=1;
	
	static final String[] queries = new String[]{"SEARCH BY SN 'Sarda sarda' EXPAND IN GBIF RETURN OCCURRENCE"};
	
	/*static final String[] queries = new String[]{"SEARCH BY SN 'sarda sarda' return Taxon",
			"SEARCH BY CN 'shark' RESOLVE EXPAND return Occurrence", "SEARCH BY SN 'sarda sarda' EXPAND return Taxon"	};*/
	
	public static void main(String[] args) throws Exception{
		
		//getPluginDescription();
		
		//getProperties();
		for (int i=0; i<QUERY_NUMBER; i++){
			Thread t =ThreadFactory.get();
			t.start();
		}
		
		System.in.read();
	}
	
	
	
	
	public static void search(int value) throws Exception{
		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		Manager manager = manager().withTimeout(3, TimeUnit.MINUTES)
			.build();
		
		long start = System.currentTimeMillis();
		//HAVING xpath(\"//product[type='Occurrence' and count>0]\")
		Stream<ResultElement> stream = manager.search(queries[value]);
		
		
		HashMap<String, Integer> repos = new HashMap<String, Integer>();
		int i=0;
		if (stream==null){
			log.warn("stream is null");
			System.exit(0);
		}
		while (stream.hasNext()){
			ResultElement re = (ResultElement)stream.next();
			if (repos.containsKey(re.getProvider())){
				Integer size =repos.get(re.getProvider());
				repos.put(re.getProvider(), size+1);
			}else repos.put(re.getProvider(), 1);
			System.out.println("("+value+")"+re.getId());
			i++;
		}
		
		
		for (Entry<String, Integer> entry: repos.entrySet())
			System.out.println(entry.getKey()+" are "+entry.getValue());
		log.trace("time for query "+value+" is "+(System.currentTimeMillis()-start));
		log.trace("total is "+i);
	}
	
	
	public static void getPluginDescription(){
		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		Manager manager = manager().withTimeout(3, TimeUnit.MINUTES)
			.build();
		
		List<PluginDescription> pluginDescriptions = manager.getPluginsDescription();
		System.out.println("plugin descriptions are "+pluginDescriptions.size());
		for (PluginDescription description : pluginDescriptions)
			System.out.println(description.toString());
		
		System.out.println("worked");
			
		
	}

	static class ThreadFactory{
		private static Random rand = new Random();
		
		static Thread get(){
			final int value = rand.nextInt(queries.length);
			return new Thread(){
				public void run(){
					try {
						log.trace("starting thread with query "+value);
						search(value);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		}
	}
}
