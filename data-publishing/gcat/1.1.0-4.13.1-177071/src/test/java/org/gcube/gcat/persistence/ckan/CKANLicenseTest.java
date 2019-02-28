package org.gcube.gcat.persistence.ckan;

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
		String ret = license.list(-1,-1);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode gotList = mapper.readTree(ret);
		Assert.assertTrue(gotList instanceof ArrayNode);
		logger.debug("List :\n{}", mapper.writeValueAsString(gotList));
	}
	
}
