package org.gcube.gcat.social;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.gcube.gcat.ContextTest;
import org.gcube.gcat.utils.Constants;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SocialServiceTest extends ContextTest {
	
	private static final Logger logger = LoggerFactory.getLogger(SocialServiceTest.class);
	
	@Test
	public void testGetUserProfile() throws Exception {
		SocialService socialService = new SocialService();
		JsonNode jsonNode = socialService.getGCubeUserProfile();
		ObjectMapper objectMapper = new ObjectMapper();
		logger.debug("gCube User Profile is {}", objectMapper.writeValueAsString(jsonNode));
	}
	
	@Test
	public void testToken() throws Exception {
		ContextTest.setContext(Constants.getCatalogueApplicationToken());
	}
	
	@Test
	public void testSendPost() throws Exception {
		SocialService socialService = new SocialService();
		socialService.setItemID(UUID.randomUUID().toString());
		socialService.setItemTitle("Test Item");
		socialService.setItemURL("http://www.d4science.org");
		List<String> tags = new ArrayList<>();
		tags.add("Test");
		tags.add("ThisIsATest");
		tags.add("IgnoreIt");
		socialService.setTags(tags);
		
		socialService.sendSocialPost(false);
	}
	
}
