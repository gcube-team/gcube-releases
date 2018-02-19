package org.gcube.data.spd.obisplugin.search.query;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class MappingUtils {

	private static final int TIMEOUT = 40000;
	private static final int retries = 5;
	private static final int SLEEP_TIME = 10000;

	private static Logger log = LoggerFactory.getLogger(MappingUtils.class);

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getObjectMapping(String query) throws Exception{
		
		String response = executeQuery(query);
		
		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		return mapper.readValue(new StringReader(response), Map.class);

	}

	public static List<Map<String, Object>> getObjectList(String query) throws Exception{
		String response = executeQuery(query);
		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		return mapper.readValue(new StringReader(response), new TypeReference<LinkedList<HashMap<String, Object>>>() {
		});

	}

	public static String getAsString(Map<String, Object> map, String key){
		if (!map.containsKey(key)) return null;
		return (String) map.get(key);
	}

	public static Double getAsDouble(Map<String, Object> map, String key){
		if (!map.containsKey(key)) return 0d;
		return (Double) map.get(key);
	}

	public static Integer getAsInteger(Map<String, Object> map, String key){
		if (!map.containsKey(key)) return 0;
		return (Integer) map.get(key);
	}

	public static Calendar getAsCalendar(Map<String, Object> map, String key){
		if (!map.containsKey(key)) return null;
		return parseCalendar((String) map.get(key));
	}


	public static Calendar parseCalendar(String date){
		try{
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar calendar= Calendar.getInstance();
			calendar.setTime(df.parse(date));
			return calendar;
		}catch (ParseException e) {
			log.warn("date discarded ("+date+")");
			return null;
		}
	}
	
	private static String executeQuery(String query){
		DefaultClientConfig clientConfig = new DefaultClientConfig();
		Client client = Client.create(clientConfig);
		client.setConnectTimeout(TIMEOUT);
		client.setReadTimeout(TIMEOUT);
		WebResource target = client.resource(query);
		//NameUsageWsClient nuws = new NameUsageWsClient(target);
		int tries = 1;
		String response = null;
		do {
			
			log.debug("try number {} STARTED for query {} ", tries,query);
			try{
				response = target.type(MediaType.APPLICATION_JSON).acceptLanguage(Locale.ENGLISH).get(String.class);
			}catch (Exception e) {
				log.debug("try number {} FAILED for query {} ", tries,query,e);
				try {
					if (tries<retries)Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e1) {}
			}
			tries++;
		}while (response==null && tries<=retries);	
		return response;
	}

}
