package org.gcube.gcat.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ResourceTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(ResourceTest.class);
	
	private static final String NAME_KEY = "name";
	private static final String NAME_VALUE = "00";
	
	private static final String TITLE_KEY = "title";
	private static final String TITLE_VALUE = "00 Title";
	
	private static final String PRIVATE_KEY = "private";
	private static final boolean PRIVATE_VALUE = false;
	
	private static final String LICENSE_ID_KEY = "license_id";
	
	private static final String TAGS_KEY = "tags";
	private static final String EXTRAS_KEY = "extras";
	
	private static final String KEY_KEY = "key";
	private static final String VALUE_KEY = "value";
	
	private static final String TYPE_KEY_VALUE = "system:type";
	private static final String TYPE_VALUE_VALUE = "EmptyProfile";
	
	private static final String TAG_VALUE = "MyTag";
	
	private static final String ID_KEY = "id";
	
	
	private static final String RESOURCE_NAME_VALUE = "Resource Name";
	
	private static final String URL_KEY = "url";
	private static final String URL_VALUE = "https://goo.gl/bFME6Q";
	
	@Test
	public void completeTest() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JavaType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class);
		
		JavaType jsonArrayType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, JsonNode.class);
		License license = new License();
		String licencesStrings = license.list();
		logger.debug("Got licenses {}", licencesStrings);
		List<JsonNode> licences = mapper.readValue(licencesStrings, jsonArrayType);
		JsonNode licenseJsonNode = licences.get(0);
		String licenseID = licenseJsonNode.get(ID_KEY).asText();
		
		
		Map<String,Object> map = new HashMap<>();
		map.put(NAME_KEY, NAME_VALUE);
		map.put(TITLE_KEY, TITLE_VALUE);
		map.put(PRIVATE_KEY, PRIVATE_VALUE);
		map.put(LICENSE_ID_KEY, licenseID);
		
		List<Map<String,Object>> tags = new ArrayList<>();
		Map<String,Object> tag = new HashMap<>();
		tag.put(NAME_KEY, TAG_VALUE);
		tags.add(tag);
		map.put(TAGS_KEY, tags);
		
		
		List<Map<String,Object>> extras = new ArrayList<>();
		Map<String,Object> type = new HashMap<>();
		type.put(KEY_KEY, TYPE_KEY_VALUE);
		type.put(VALUE_KEY, TYPE_VALUE_VALUE);
		extras.add(type);
		map.put(EXTRAS_KEY, extras);
		
		
		Item item = new Item();
		String json = mapper.writeValueAsString(map);
		logger.debug("Going to create {}", json);
		String createdItemString = item.create(json);
		try {
			String itemsString = item.list(10, 0);
			logger.debug("Got items {}", itemsString);
			
			List<String> items = mapper.readValue(itemsString, listType);
			String name = items.get(0);
			Assert.assertTrue(name.compareTo(NAME_VALUE) == 0);
		} catch(AssertionError e) {
			item.delete(NAME_VALUE, true);
			throw e;
		}
		
		try {
			JsonNode createdItem = mapper.readTree(createdItemString);
			String itemID = createdItem.get(ID_KEY).asText();
			
			Resource resource = new Resource();
			String resourcesString = resource.list(itemID);
			logger.debug("Got resources {}", resourcesString);
			List<JsonNode> resources = mapper.readValue(resourcesString, jsonArrayType);
			Assert.assertTrue(resources.size()==0);
			
			Map<String,Object> resourceMap = new HashMap<>();
			resourceMap.put(NAME_KEY, RESOURCE_NAME_VALUE);
			resourceMap.put(URL_KEY, URL_VALUE);
			
			String resourceJson = mapper.writeValueAsString(resourceMap);
			String createdResourceString = resource.create(itemID, resourceJson);
			logger.debug("Created Resource {}", createdResourceString);
			JsonNode createdResource = mapper.readTree(createdResourceString);
			String resourceID = createdResource.get(ID_KEY).asText();
			
			resource = new Resource();
			resourcesString = resource.list(itemID);
			logger.debug("Got resources {}", resourcesString);
			resources = mapper.readValue(resourcesString, jsonArrayType);
			Assert.assertTrue(resources.size()==1);
			
			JsonNode gotResource = resources.get(0);
			String gotResourceID = gotResource.get(ID_KEY).asText();
			Assert.assertTrue(gotResourceID.compareTo(resourceID)==0);
			
			String readResourceString = resource.read(itemID, resourceID);
			JsonNode readResource =  mapper.readTree(readResourceString);
			String readResourceID = readResource.get(ID_KEY).asText();
			Assert.assertTrue(readResourceID.compareTo(resourceID)==0);
			
			resource.delete(itemID, resourceID);
			
			resourcesString = resource.list(itemID);
			logger.debug("Got resources {}", resourcesString);
			resources = mapper.readValue(resourcesString, jsonArrayType);
			Assert.assertTrue(resources.size()==0);
			
		}catch (Throwable e) {
			item.delete(NAME_VALUE, true);
			throw e;
		}
		
		item.delete(NAME_VALUE, true);
		
		String itemsString = item.list(10, 0);
		logger.debug("Got list", itemsString);
		List<String> items = mapper.readValue(itemsString, listType);
		String name = items.get(0);
		Assert.assertTrue(name.compareTo(NAME_VALUE) != 0);
		
	}
	
	@Test
	public void safeTest() throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		JavaType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class);
		
		Item item = new Item();
		String itemsString = item.list(10, 0);
		logger.debug("Got items {}", itemsString);
		
		List<String> items = mapper.readValue(itemsString, listType);
		for(String name : items) {
		
			String ret = item.read(name);
			logger.debug("Got item {}", ret);
			JsonNode jsonNode = mapper.readTree(ret);
			
			String itemName = jsonNode.get(NAME_KEY).asText();
			Assert.assertTrue(name.compareTo(itemName) == 0);
			String itemID = jsonNode.get(ID_KEY).asText();
			
			Resource resource = new Resource();
			String resourcesString = resource.list(itemID);
			logger.debug("Got resources {}", resourcesString);
			
			List<String> resources = mapper.readValue(resourcesString, listType);
			for(String resourceID : resources) {
				String resourceString = resource.read(resourceID);
				logger.debug("Got resource {}", resourceString);
				mapper.readTree(resourceString);
			}
		}
		
	}
}
