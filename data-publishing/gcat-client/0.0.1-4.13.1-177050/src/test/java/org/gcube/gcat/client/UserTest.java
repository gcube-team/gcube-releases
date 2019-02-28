package org.gcube.gcat.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
public class UserTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(UserTest.class);
	
	private static final String ID_KEY = "id";
	
	@Test
	public void safeTest() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		
		JavaType arrayType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, JsonNode.class);
		User user = new User();
		String usersString = user.list();
		logger.debug("Got Users {}", usersString);
		List<JsonNode> users = mapper.readValue(usersString, arrayType);
		Assert.assertTrue(users.size() > 0);
		for(JsonNode userJsonNode : users) {
			String userID = userJsonNode.get(ID_KEY).asText();
			logger.debug("Users : {}", userID);
		}
	}
	
}
