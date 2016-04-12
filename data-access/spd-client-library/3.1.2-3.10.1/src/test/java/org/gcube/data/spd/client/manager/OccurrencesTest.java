package org.gcube.data.spd.client.manager;

import static org.gcube.data.spd.client.plugins.AbstractPlugin.*;
import static org.gcube.data.streams.dsl.Streams.convert;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.client.proxies.Manager;
import org.gcube.data.spd.client.proxies.Occurrence;
import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.model.PointInfo;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccurrencesTest {

	private static Logger logger = LoggerFactory.getLogger(OccurrencesTest.class);
	
	public static void main(String[] args) throws Exception{
		getDWCFileByIds();
	}
	
	private static List<String> occurrenceIds=Arrays.asList("GBIF:sarda||130||11956||57744173||","GBIF:sarda||82||400||50917042||", 
															"GBIF:sarda||427||14113||60499431||");
	
	
	
	public static void getOccurencesByKeys() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		Occurrence occurrences = occurrence().
				build();
		
		Stream<String> ids =convert(occurrenceIds);
				
		Stream<OccurrencePoint> stream =  occurrences.getByKeys(ids);
		
		int i =0;
		while (stream.hasNext())
			logger.trace(i+++")"+stream.next());
			
			
		
	}
	
	public static void getLayerByKeys() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		Occurrence occurrences = occurrence().
				withTimeout(2, TimeUnit.MINUTES)
				.build();
		
		List<PointInfo> coords = Arrays.asList(new PointInfo(2.3,7.2), new PointInfo(6.3,7.2), new PointInfo(10.3,9.2));
		
		Stream<PointInfo> ids =convert(coords);
			
		
		String group =  occurrences.createLayer(ids);
		
		System.out.println(group);
			
			
		
	}
	
	
	public static void getDWCFileByIds() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		Manager manager = manager()
				.build();
		List<PluginDescription> descriptions =  manager.getPluginsDescription();
		
		for (PluginDescription desc: descriptions)
			logger.trace(desc.getName());
	}
	

}
