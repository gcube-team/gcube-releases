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
public class ItemTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(ItemTest.class);
	
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
	
	@Test
	public void completeTest() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JavaType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class);
		
		JavaType licenseArrayType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, JsonNode.class);
		License license = new License();
		String licencesStrings = license.list();
		logger.debug("Got licenses {}", licencesStrings);
		List<JsonNode> licences = mapper.readValue(licencesStrings, licenseArrayType);
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
		item.create(json);
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
			String itemString = item.read(NAME_VALUE);
			logger.debug("Got item {}", itemString);
			JsonNode jsonNode = mapper.readTree(itemString);
			
			String gotName = jsonNode.get(NAME_KEY).asText();
			Assert.assertTrue(gotName.compareTo(NAME_VALUE) == 0);
			
			String gotTitle = jsonNode.get(TITLE_KEY).asText();
			Assert.assertTrue(gotTitle.compareTo(TITLE_VALUE) == 0);
			
			boolean privateValue = jsonNode.get(PRIVATE_KEY).asBoolean();
			Assert.assertTrue(privateValue==PRIVATE_VALUE);
			
			String gotLicenseID = jsonNode.get(LICENSE_ID_KEY).asText();
			Assert.assertTrue(gotLicenseID.compareTo(licenseID) == 0);
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
		logger.debug("Got list {}", itemsString);
		
		List<String> items = mapper.readValue(itemsString, listType);
		String name = items.get(0);
		
		String ret = item.read(name);
		logger.debug("Got item {}", ret);
		JsonNode jsonNode = mapper.readTree(ret);
		
		String gotName = jsonNode.get(NAME_KEY).asText();
		Assert.assertTrue(name.compareTo(gotName) == 0);
	}
}
