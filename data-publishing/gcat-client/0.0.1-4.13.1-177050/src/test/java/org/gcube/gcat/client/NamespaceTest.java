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
public class NamespaceTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(NamespaceTest.class);
	
	private static final String ID_KEY = "id";
	
	@Test
	public void safeTest() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		
		JavaType arrayType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, JsonNode.class);
		Namespace namespace = new Namespace();
		String namespacesStrings = namespace.list();
		logger.debug("Got namespace {}", namespacesStrings);
		List<JsonNode> namespaces = mapper.readValue(namespacesStrings, arrayType);
		Assert.assertTrue(namespaces.size()>0);
		for(JsonNode namespaceJsonNode : namespaces) {
			String namespaceID = namespaceJsonNode.get(ID_KEY).asText();
			logger.debug("Namespace : {}", namespaceID);
		}
	}
}
