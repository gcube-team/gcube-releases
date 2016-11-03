package org.gcube.common.couchdbconnector;

import java.util.UUID;

import org.gcube.common.couchdb.connector.HttpCouchClient;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallTest {

	private static Logger logger = LoggerFactory.getLogger(CallTest.class);
	
	private final String URL = "http://couchdb01-d-d4s.d4science.org:5984";
	private final String TEST_DB = "test_db";
	private final String TEST_USER_USERNAME = "";
	private final String TEST_USER_PASSWORD = "";
	
	private final String DESIGN_NAME = "testDesign";
	private final String VIEW_NAME = "testView";
	
	@Test
	public void get() throws Exception{
		HttpCouchClient httpCouch =new HttpCouchClient(URL,TEST_DB, TEST_USER_USERNAME, TEST_USER_PASSWORD);	
		logger.trace(httpCouch.getDoc("hello"));
	}
	
	@Test
	public void getAll() throws Exception{
		HttpCouchClient httpCouch =new HttpCouchClient(URL,TEST_DB, TEST_USER_USERNAME, TEST_USER_PASSWORD);	
		logger.trace(httpCouch.getAllDocs());
	}
	
	@Test
	public void getFiltered() throws Exception{
		HttpCouchClient httpCouch =new HttpCouchClient(URL,TEST_DB, TEST_USER_USERNAME, TEST_USER_PASSWORD);
		logger.trace(httpCouch.getFilteredDocs(DESIGN_NAME, VIEW_NAME, "foo"));
	}

	
	@Test
	public void putGetDelete() throws Exception{
		HttpCouchClient httpCouch =new HttpCouchClient(URL,TEST_DB, TEST_USER_USERNAME, TEST_USER_PASSWORD);
		String randomID = UUID.randomUUID().toString();
		String jsonString = "{ \"_id\" : \"" + randomID +"\", \"property\" : \"test\" }";
		httpCouch.put(jsonString, randomID);
		String doc = httpCouch.getDoc(randomID);
		logger.trace("The created document is  : {}", doc);
		String revision = HttpCouchClient.getRevision(doc);
		logger.trace("Going to deleted doc {}", doc);
		httpCouch.delete(randomID, revision);
	}
}
