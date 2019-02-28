package org.gcube.gcat.persistence.ckan;

import java.net.URL;
import java.util.UUID;

import org.gcube.gcat.ContextTest;
import org.gcube.gcat.utils.Constants;
import org.gcube.storagehub.ApplicationMode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CKANResourceTest extends ContextTest {
	
	private static final Logger logger = LoggerFactory.getLogger(CKANResourceTest.class);
	
	/*
	 * PRE
	 * 
	 * Workspace(luca.frosini) > RESTful Transaction Model.pdf
	 * https://data1-d.d4science.org/shub/E_YjI4STdKKzRlNjgzMm9jQWxjcmtReDNwbDFYR3lpTHo3SjdtN1RDZ3c2OGk0ZHZhdE5iZElBKzNxUDAyTGFqZw==
	 * https://goo.gl/HcUWni
	 * 
	 * 
	 * Workspace(luca.frosini) > RESTful Transaction Model v 1.0.pdf
	 * https://data1-d.d4science.org/shub/E_aThRa1NpWFJpTGEydEU2bEJhMXNjZy8wK3BxekJKYnpYTy81cUkwZVdicEZ0aGFRZmY4MkRnUC8xWW0zYzVoVg==
	 * https://goo.gl/J8AwQW
	 * 
	 * 
	 * Workspace(luca.frosini) > RESTful Transaction Model v 1.1.pdf
	 * https://data1-d.d4science.org/shub/E_NkhrbVV4VTluT0RKVUtCRldobFZTQU5ySTZneFdpUzJ2UjJBNlZWNDlURDVHamo4WjY5RnlrcHZGTGNkT2prUg==
	 * https://goo.gl/78ViuR
	 * 
	 */
	@Test
	public void test() throws Exception {
		ApplicationMode applicationMode = new ApplicationMode(Constants.getCatalogueApplicationToken());
		applicationMode.start();
		// 
		applicationMode.end();
	}
	
	// @Test
	public void testCopyStorageResource() throws Exception {
		URL url = new URL("https://goo.gl/HcUWni"); 
		
		String itemID = UUID.randomUUID().toString();
		CKANResource ckanResource = new CKANResource(itemID);
		ckanResource.resourceID = UUID.randomUUID().toString();
		URL finalURL = ckanResource.copyStorageResource(url);
		logger.debug("Initial URL is  {} - Final URL is {}", url, finalURL);
		ckanResource.deleteStorageResource(finalURL, ckanResource.resourceID, ckanResource.mimeType);
	}
	
	@Test
	public void testCreate() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode objectNode = objectMapper.createObjectNode();
		objectNode.put(CKANResource.NAME_KEY, "MyTestTy_rest_upload");
		objectNode.put(CKANResource.URL_KEY, "https://data.d4science.org/shub/58a13287-3e91-4afd-bd80-cf4605a0edaa");
		objectNode.put("description", "i uploaded this file using the REST API");
		// objectNode.put(CKANResource.ID_KEY, "ba7ab7e8-c268-4219-98cd-c73470870999");
		
		CKANResource ckanResource = new CKANResource("ba7ab7e8-c268-4219-98cd-c73470870999");
		String json = ckanResource.getAsString(objectNode);
		logger.debug("Going to create Resource {}", json);
		ckanResource.create(objectNode);
	}
	
	// @Test
	public void testDelete() throws Exception {
		CKANResource ckanResource = new CKANResource("f0326fec-d8ac-42c7-abff-c7905b4d938e");
		ckanResource.setResourceID("fcf98272-41e7-4f05-9294-fdafb1a33074");
		ckanResource.delete();
	}
	
}
