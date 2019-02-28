package org.gcube.gcat.social;

import org.gcube.gcat.ContextTest;
import org.gcube.gcat.social.SocialService;
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
	
}
