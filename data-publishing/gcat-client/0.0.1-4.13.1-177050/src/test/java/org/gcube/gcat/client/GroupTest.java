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
public class GroupTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(GroupTest.class);
	
	private static final String NAME_KEY = "name";
	private static final String NAME_VALUE = "0000";
	
	private static final String TITLE_KEY = "title";
	private static final String TITLE_VALUE = "0000 Title";
	
	private static final String DISPLAY_NAME_KEY = "display_name";
	private static final String DISPLAY_NAME_VALUE = "0000 Display Name";
	
	// @Test
	public void completeTest() throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String,Object> map = new HashMap<>();
		map.put(NAME_KEY, NAME_VALUE);
		map.put(TITLE_KEY, TITLE_VALUE);
		map.put(DISPLAY_NAME_KEY, DISPLAY_NAME_VALUE);
		
		JavaType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class);
		
		Group group = new Group();
		String json = mapper.writeValueAsString(map);
		logger.debug("Going to create {}", json);
		try {
			group.create(json);
			
			String list = group.list(10, 0);
			logger.debug("Got list", list);
			
			List<String> groups = mapper.readValue(list, type);
			String name = groups.get(0);
			Assert.assertTrue(name.compareTo(NAME_VALUE) == 0);
		} catch(AssertionError e) {
			group.delete(NAME_VALUE, true);
		}
		
		group.delete(NAME_VALUE, true);
		String list = group.list(10, 0);
		logger.debug("Got list", list);
		List<String> groups = mapper.readValue(list, type);
		String name = groups.get(0);
		Assert.assertTrue(name.compareTo(NAME_VALUE) != 0);
	}
	
	@Test
	public void safeTest() throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		JavaType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class);
		
		Group group = new Group();
		String list = group.list(10, 0);
		logger.debug("Got list {}", list);
		
		List<String> groups = mapper.readValue(list, type);
		String name = groups.get(0);
		
		String ret = group.read(name);
		logger.debug("Got group {}", ret);
		JsonNode jsonNode = mapper.readTree(ret);
		
		String gotName = jsonNode.get(NAME_KEY).asText();
		Assert.assertTrue(name.compareTo(gotName) == 0);
	}
}
