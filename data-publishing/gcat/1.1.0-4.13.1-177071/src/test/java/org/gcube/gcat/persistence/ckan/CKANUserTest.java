package org.gcube.gcat.persistence.ckan;

import org.gcube.gcat.ContextTest;
import org.gcube.gcat.persistence.ckan.CKANUser;
import org.gcube.gcat.persistence.ckan.CKANUtility;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CKANUserTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(CKANUserTest.class);
	
	private static final String USERNAME = "pippo";
	
	private CKANUser getCKANUser() {
		CKANUser user = new CKANUser();
		user.setApiKey(CKANUtility.getSysAdminAPI());
		user.setName(USERNAME);
		return user;
	}

	@Test
	public void list() throws Exception {
		CKANUser ckanUser = getCKANUser();
		String ret = ckanUser.list(-1,-1);
		logger.debug("{}", ret);
	}
	
	@Test
	public void create() throws Exception {
		CKANUser ckanUser = getCKANUser();
		String ret = ckanUser.create();
		logger.debug("{}", ret);
	}
	
	@Test
	public void read() throws Exception {
		CKANUser ckanUser = getCKANUser();
		String ret = ckanUser.read();
		logger.debug("{}", ret);
	}
	
	public final static String DISPLAY_NAME = "display_name";
	
	@Test
	public void update() throws Exception {
		CKANUser ckanUser = getCKANUser();
		String ret = ckanUser.read();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode readUser = mapper.readTree(ret);
		((ObjectNode) readUser).put(CKANUser.EMAIL, USERNAME+"@gcube.ckan.org");
		((ObjectNode) readUser).put("state", "active");
		ret = ckanUser.update(ckanUser.getAsString(readUser));
		logger.debug("{}", ret);
	}
	
	@Test
	public void delete() throws Exception {
		CKANUser ckanUser = getCKANUser();
		ckanUser.delete(false);
	}
	
	
}
