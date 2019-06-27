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
public class LicenseTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(LicenseTest.class);
	
	private static final String ID_KEY = "id";
	
	@Test
	public void safeTest() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		
		JavaType licenseArrayType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, JsonNode.class);
		License license = new License();
		String licencesStrings = license.list();
		logger.debug("Got licenses {}", licencesStrings);
		List<JsonNode> licences = mapper.readValue(licencesStrings, licenseArrayType);
		Assert.assertTrue(licences.size()>0);
		for(JsonNode licenseJsonNode : licences) {
			String licenseID = licenseJsonNode.get(ID_KEY).asText();
			logger.debug("License : {}", licenseID);
		}
	}
}
