package org.gcube.data.spd.client.manager;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.client.plugins.AbstractPlugin;
import org.gcube.data.spd.client.proxies.ManagerClient;
import org.gcube.data.spd.client.proxies.OccurrenceClient;
import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.Product;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.streams.Stream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ManagerTest {

	private static final Logger log = LoggerFactory.getLogger(ManagerTest.class);
	
	@Test
	public void search() throws Exception{
		SecurityTokenProvider.instance.set("94a3b80a-c66f-4000-ae2f-230f5dfad793-98187548");
		ScopeProvider.instance.set("/gcube/devsec");
		ManagerClient manager = AbstractPlugin.manager().build();
		
		Stream<ResultItem> stream = manager.search("SEARCH BY SN 'cetacea' IN OBIS WHERE coordinate >= 30.0 , 20.0 ");
		
		List<String> productKeys = new ArrayList<String>(); 
		
		int i =0;
		while (stream.hasNext()){
			if (i==10) break;
			ResultItem item = stream.next();
			
			for (Product prod : item.getProducts()){
				System.out.println("type: "+prod.getType()+" - "+prod.getKey()+" - "+prod.getCount());
				productKeys.add(prod.getKey());
			}
			i++;
		}
		
		stream.close();
		
		OccurrenceClient occurrence = AbstractPlugin.occurrences().build();
		Stream<OccurrencePoint> occurrenceStream = occurrence.getByKeys(productKeys);
		
		int occIndex = 0;
		
		while (occurrenceStream.hasNext()){
			
			occIndex++;
		}
		
		System.out.println("occurrence point found : "+occIndex);
		occurrenceStream.close();
	}
	
	@Test
	public void getPluginDescription() throws Exception{
		SecurityTokenProvider.instance.set("94a3b80a-c66f-4000-ae2f-230f5dfad793-98187548");
		ScopeProvider.instance.set("/gcube/devsec");
		ManagerClient manager = AbstractPlugin.manager().build();
		List<PluginDescription> pluginDescriptions = manager.getPluginsDescription();
		System.out.println("plugin descriptions are "+pluginDescriptions.size());
		for (PluginDescription description : pluginDescriptions)
			System.out.println(description.toString());
	}

}
