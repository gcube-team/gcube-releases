package org.gcube.data.spd.gbif;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;

import org.gcube.data.spd.gbifplugin.search.OccurrenceSearch;
import org.gcube.data.spd.gbifplugin.search.ResultItemSearch;
import org.gcube.data.spd.gbifplugin.search.Utils;
import org.gcube.data.spd.gbifplugin.search.query.MappingUtils;
import org.gcube.data.spd.gbifplugin.search.query.PagedQueryIterator;
import org.gcube.data.spd.gbifplugin.search.query.PagedQueryObject;
import org.gcube.data.spd.gbifplugin.search.query.QueryCondition;
import org.gcube.data.spd.gbifplugin.search.query.ResultType;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;


public class QueryTest {
	
	public static final String BASE_URL = "http://api.gbif.org/v1";
	
	@Test
	public void query4Occurrence(){
		PagedQueryObject qo = new PagedQueryObject("http://api.gbif.org/v1/", ResultType.Occurrence, 50);
		qo.setConditions(QueryCondition.cond("scientificName","Palinurus%20elephas"), QueryCondition.cond("hasCoordinate","true"));
		PagedQueryIterator<String> pagedQuery = new PagedQueryIterator<String>(qo) {

			@Override
			protected String getObject(Map<String, Object> mappedObject)
					throws Exception {
				System.out.println(mappedObject.toString());
				return mappedObject.toString();
			}
		};
		
		while (pagedQuery.hasNext())
			System.out.println(pagedQuery.next());
		
	}
	
	@Test
	public void query4Taxon() throws JsonParseException, JsonMappingException, IOException{
		ClientConfig clientConfig = new DefaultClientConfig();
		Client client = Client.create(clientConfig);
		
		WebResource target = client.resource("http://api.gbif.org/v1/species/search/?limit=1&offset=0&q=sarda%20sarda");
		
		//NameUsageWsClient nuws = new NameUsageWsClient(target);
		String response = target.type(MediaType.APPLICATION_JSON).acceptLanguage(Locale.ENGLISH).get(String.class);
		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		Map<String,Object> userData = mapper.readValue(new StringReader(response), Map.class);
		for (Entry<String, Object> entry : userData.entrySet())
			System.out.println(String.format("entry name %s class value %s", entry.getKey(), entry.getValue().getClass().getSimpleName()));
		
	}
	
	@Test
	public void searchItems() throws Exception{
		
		ResultItemSearch searcher = new ResultItemSearch(BASE_URL,"Limanda limanda");
		
		searcher.search(new ObjectWriter<ResultItem>() {
			
			int i =0;
			
			@Override
			public boolean write(StreamException error) {
				error.printStackTrace();
				return false;
			}
			
			@Override
			public boolean write(ResultItem t) {
				System.out.println("written element "+(++i));
				return true;
			}
			
			@Override
			public boolean isAlive() {
				return true;
			}
		}, 50);
	}
	
	@Test
	public void searchOccurrences() throws Exception{
		
		OccurrenceSearch searcher = new OccurrenceSearch(BASE_URL);
		
		searcher.search(new ObjectWriter<OccurrencePoint>() {
			
			int i =0;
			
			@Override
			public boolean write(StreamException error) {
				error.printStackTrace();
				return false;
			}
			
			@Override
			public boolean write(OccurrencePoint t) {
				System.out.println("written element "+(++i));
				return true;
			}
			
			@Override
			public boolean isAlive() {
				return true;
			}
		}, "Limanda limanda", 50);
	}
	
	@Test
	public void dataTest() throws Exception{
		Utils.elaborateProductsKey("197908d0-5565-11d8-b290-b8a03c50a862^^Fishbase^^FishBase: Fishbase^^192a9ab0-5565-11d8-b290-b8a03c50a862^^FishBase||5208593");
		
	}
	
}
