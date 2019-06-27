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
public class OrganizationTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(OrganizationTest.class);
	
	private static final String NAME_KEY = "name";
	private static final String NAME_VALUE = "00";
	
	private static final String TITLE_KEY = "title";
	private static final String TITLE_VALUE = "00 Title";
	
	private static final String DISPLAY_NAME_KEY = "display_name";
	private static final String DISPLAY_NAME_VALUE = "0000 Display Name";
	
	// @Test
	public void completeTest() throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String,Object> map = new HashMap<>();
		map.put(NAME_KEY, NAME_VALUE);
		map.put(TITLE_KEY, TITLE_VALUE);
		map.put(DISPLAY_NAME_KEY, DISPLAY_NAME_VALUE);
		
		JavaType arrayType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class);
		
		Organization organization = new Organization();
		String json = mapper.writeValueAsString(map);
		logger.debug("Going to create {}", json);
		try {
			organization.create(json);
			
			String organizationsString = organization.list(10, 0);
			logger.debug("Got Organizations {}", organizationsString);
			
			List<String> organizations = mapper.readValue(organizationsString, arrayType);
			String name = organizations.get(0);
			Assert.assertTrue(name.compareTo(NAME_VALUE) == 0);
		} catch(AssertionError e) {
			organization.delete(NAME_VALUE);
		}
		
		organization.delete(NAME_VALUE);
		String organizationsString = organization.list(10, 0);
		logger.debug("Got Organizations {}", organizationsString);
		List<String> organizations = mapper.readValue(organizationsString, arrayType);
		String name = organizations.get(0);
		Assert.assertTrue(name.compareTo(NAME_VALUE) != 0);
	}
	
	@Test
	public void safeTest() throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		JavaType arrayType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class);
		
		Organization organization = new Organization();
		String organizationsString = organization.list(10, 0);
		logger.debug("Got Organizations {}", organizationsString);
		
		List<String> listArray = mapper.readValue(organizationsString, arrayType);
		String name = listArray.get(0);
		
		String ret = organization.read(name);
		logger.debug("Got Organization {}", ret);
		JsonNode jsonNode = mapper.readTree(ret);
		
		String gotName = jsonNode.get(NAME_KEY).asText();
		Assert.assertTrue(name.compareTo(gotName) == 0);
	}
}
