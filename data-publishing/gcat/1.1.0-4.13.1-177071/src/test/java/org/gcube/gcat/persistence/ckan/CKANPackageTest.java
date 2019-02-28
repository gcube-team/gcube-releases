package org.gcube.gcat.persistence.ckan;

import java.util.HashMap;
import java.util.Map;

import org.gcube.gcat.ContextTest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class CKANPackageTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(CKANPackageTest.class);
	
	private static final String NOTES_KEY = "notes";
	private static final String URL_KEY = "url";
	private static final String PRIVATE_KEY = "private";
	
	private static final String ITEM_NAME_VALUE = "restful_transaction_model";
	private static final String LICENSE_VALUE = "CC-BY-SA-4.0";
	private static final String EXTRAS_TYPE_VALUE_VALUE = "EmptyProfile";
	
	
	@Test
	public void list() throws Exception {
		CKANPackage ckanPackage = new CKANPackage();
		ObjectMapper mapper = new ObjectMapper();
		String ret = ckanPackage.list(-1,0);
		JsonNode gotList = mapper.readTree(ret);
		Assert.assertTrue(gotList instanceof ArrayNode);
		logger.debug("List :\n{}", mapper.writeValueAsString(gotList));
	}
	
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
	public void testNameRegex() {
		Map<String, Boolean> stringsToTest = new HashMap<>();
		stringsToTest.put("Test", false); // Fails for T
		stringsToTest.put("test-test+test-test", false); // Fails for +
		stringsToTest.put("t", false); // Fails because is too short. Min length is 2 characters
		stringsToTest.put("te", true);
		stringsToTest.put("test-test_test-test", true);
		stringsToTest.put("test-test_test-test_test-test_test-test_test-test_test-test_test-test_test-test_test-test_test-test_", true);
		// // Fails because is too long. Max length is 100 characters
		stringsToTest.put("test-test_test-test_test-test_test-test_test-test_test-test_test-test_test-test_test-test_test-test_t", false);
		
		for(String testString : stringsToTest.keySet()) {
			boolean match = testString.matches(CKANPackage.NAME_REGEX);
			logger.debug("'{}' does {}match the regex {}", testString, match ? "" : "NOT ", CKANPackage.NAME_REGEX);
			Assert.assertEquals(stringsToTest.get(testString), match);
		}
		
	}
	
	
	protected CKANPackage createPackage(ObjectMapper mapper) throws Exception {
		
		ObjectNode itemObjectNode = mapper.createObjectNode();
		itemObjectNode.put(CKAN.NAME_KEY, ITEM_NAME_VALUE);
		itemObjectNode.put(CKANPackage.TITLE_KEY, "RESTful Transaction Model");
		itemObjectNode.put(CKANPackage.LICENSE_KEY, LICENSE_VALUE);
		itemObjectNode.put(PRIVATE_KEY, false);
		itemObjectNode.put(NOTES_KEY, "A research of Luca Frosini");
		itemObjectNode.put(URL_KEY, "http://www.d4science.org");
		
		ArrayNode tagArrayNode = itemObjectNode.putArray(CKANPackage.TAGS_KEY);
		ObjectNode tagNode = mapper.createObjectNode();
		tagNode.put(CKANPackage.NAME_KEY, "REST");
		tagArrayNode.add(tagNode);
		
		ArrayNode resourceArrayNode = itemObjectNode.putArray(CKANPackage.RESOURCES_KEY);
		ObjectNode resourceNode = mapper.createObjectNode();
		resourceNode.put(CKANResource.NAME_KEY, "RESTful Transaction Model");
		// Workspace(luca.frosini) > RESTful Transaction Model v 1.0.pdf
		resourceNode.put(CKANResource.URL_KEY, "https://goo.gl/J8AwQW");
		resourceArrayNode.add(resourceNode);
		
		ArrayNode extraArrayNode = itemObjectNode.putArray(CKANPackage.EXTRAS_KEY);
		ObjectNode extraNode = mapper.createObjectNode();
		extraNode.put(CKANPackage.EXTRAS_KEY_KEY, CKANPackage.EXTRAS_KEY_VALUE_SYSTEM_TYPE);
		extraNode.put(CKANPackage.EXTRAS_VALUE_KEY, EXTRAS_TYPE_VALUE_VALUE);
		extraArrayNode.add(extraNode);
		
		CKANPackage ckanPackage = new CKANPackage();
		ckanPackage.setName(ITEM_NAME_VALUE);
		//ckanPackage.setApiKey(CKANUtility.getSysAdminAPI());
		String createdItem = ckanPackage.create(mapper.writeValueAsString(itemObjectNode));
		logger.debug(createdItem);
		
		return ckanPackage;
	}
	
	@Test
	public void create() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		createPackage(mapper);
	}
	
	@Test
	public void createReadUpdateUpdatePurge() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		createPackage(mapper);
		
		CKANPackage ckanPackage = new CKANPackage();
		ckanPackage.setName(ITEM_NAME_VALUE);
		String readItem = ckanPackage.read();
		
		JsonNode readItemObjectNode = mapper.readTree(readItem);
		String readID = readItemObjectNode.get(CKANPackage.NAME_KEY).asText();
		Assert.assertNotNull(readID);
		
		Assert.assertTrue(ITEM_NAME_VALUE.compareTo(readID)==0);
		
		String updatedNotes = "A research of Luca Frosini made during the PhD";
		((ObjectNode) readItemObjectNode).put(NOTES_KEY, updatedNotes);
		
		ArrayNode resources = (ArrayNode) readItemObjectNode.get(CKANPackage.RESOURCES_KEY);
		ObjectNode objectNode = (ObjectNode) resources.get(0);
		// Workspace(luca.frosini) > RESTful Transaction Model v 1.1.pdf
		objectNode.put(CKANResource.URL_KEY, "https://goo.gl/78ViuR");
		resources.set(0, objectNode);
		
		((ObjectNode) readItemObjectNode).replace(CKANPackage.RESOURCES_KEY, resources);
		
		ckanPackage =  new CKANPackage();
		ckanPackage.setName(ITEM_NAME_VALUE);
		String updatedItem = ckanPackage.update(mapper.writeValueAsString(readItemObjectNode));
		logger.trace(updatedItem);
		JsonNode updatedItemObjectNode = mapper.readTree(updatedItem);
		String gotUpdatedNotes = updatedItemObjectNode.get(NOTES_KEY).asText();
		Assert.assertTrue(gotUpdatedNotes.compareTo(updatedNotes)==0);
	
		
		ckanPackage =  new CKANPackage();
		ckanPackage.setName(ITEM_NAME_VALUE);
		((ObjectNode) updatedItemObjectNode).remove(CKANPackage.RESOURCES_KEY);
		String secondUpdateItem = ckanPackage.update(mapper.writeValueAsString(updatedItemObjectNode));
		logger.trace(secondUpdateItem);
		
		/*
		ObjectNode patchObjectNode = mapper.createObjectNode();
		String patchedNotes = updatedNotes + " in October 2018";
		patchObjectNode.put(NOTES_KEY, patchedNotes);
		patchObjectNode.put(CKANPackage.NAME_KEY, ITEM_NAME_VALUE);
		
		ckanPackage =  new CKANPackage();
		ckanPackage.setName(ITEM_NAME_VALUE);
		String patchedItem = ckanPackage.patch(mapper.writeValueAsString(patchObjectNode));
		logger.trace(patchedItem);
		JsonNode patchedItemObjectNode = mapper.readTree(patchedItem);
		String gotPatchedNotes = patchedItemObjectNode.get(NOTES_KEY).asText();
		Assert.assertTrue(gotPatchedNotes.compareTo(patchedNotes)==0);
		*/
		
		ckanPackage =  new CKANPackage();
		ckanPackage.setName(ITEM_NAME_VALUE);
		ckanPackage.purge();
		logger.debug("Item {} purge successfully", ITEM_NAME_VALUE);
		
	}
	
	@Test
	//(expected = NotFoundException.class)
	public void read() throws Exception {
		CKANPackage ckanPackage = new CKANPackage();
		ckanPackage.setName(ITEM_NAME_VALUE);
		String ret = ckanPackage.read();
		logger.debug(ret);
	}
	
	@Test
	//(expected = NotFoundException.class)
	public void delete() throws Exception {
		CKANPackage ckanPackage = new CKANPackage();
		ckanPackage.setName(ITEM_NAME_VALUE);
		ckanPackage.delete(true);
	}
	
}
