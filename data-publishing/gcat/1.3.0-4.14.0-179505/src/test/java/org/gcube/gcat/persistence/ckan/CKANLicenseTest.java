package org.gcube.gcat.persistence.ckan;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.gcube.gcat.ContextTest;
import org.gcube.gcat.persistence.ckan.CKANLicense;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class CKANLicenseTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(CKANLicenseTest.class);
	
	@Test
	public void list() throws Exception {
		CKANLicense license = new CKANLicense();
		license.list(-1,-1);
		JsonNode gotList = license.getJsonNodeResult();
		Assert.assertTrue(gotList instanceof ArrayNode);
		ObjectMapper mapper = new ObjectMapper();
		logger.debug("List :\n{}", mapper.writeValueAsString(gotList));
	}
	
	@Test
	public void testCheckLicense() throws Exception {
		ArrayNode arrayNode = CKANLicense.getLicenses();
		for(JsonNode jsonNode : arrayNode){
			String licenseId = jsonNode.get(CKAN.ID_KEY).asText();
			assertTrue(CKANLicense.checkLicenseId(arrayNode, licenseId));
			logger.debug("'{}' is a valid License ID", licenseId);
		}
		List<String> invalidIds = new ArrayList<>();
		invalidIds.add("InvaliLicense");
		invalidIds.add("CCO");
		for(String licenseId : invalidIds) {
			assertFalse(CKANLicense.checkLicenseId(arrayNode, licenseId));
			logger.debug("As expected '{}' is an INVALID License ID", licenseId);
		}
	}
}
