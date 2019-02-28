package org.gcube.gcat.rest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.gcube.gcat.ContextTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class ProfileTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(ProfileTest.class);
	
	@Test
	public void list() throws Exception {
		Profile profile = new Profile();
		String ret = profile.list();
		logger.debug("{}", ret);
	}
	
	
	@Test
	public void read() throws Exception {
		String profileID = "SoBigData.eu: Dataset Metadata NextNext";
		Profile profile = new Profile();
		String ret = profile.read(profileID, MediaType.APPLICATION_XML);
		logger.debug("XML :\n{}", ret);
		ret = profile.read(profileID, MediaType.APPLICATION_JSON);
		logger.debug("JSON : \n{}", ret);
	}
	
	@Test
	public void listRead() throws Exception {
		Profile profile = new Profile();
		String ret = profile.list();
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayNode = (ArrayNode) mapper.readTree(ret);
		logger.debug("Found {} profiles", arrayNode.size());
		Iterator<JsonNode> iterator = arrayNode.iterator();
		while(iterator.hasNext()) {
			String profileID = iterator.next().asText();
			ret = profile.read(profileID, MediaType.APPLICATION_XML);
			logger.debug("XML :\n{}", ret);
			ret = profile.read(profileID, MediaType.APPLICATION_JSON);
			logger.debug("JSON : \n{}", ret);
		}
	}
	
	public static String PROFILE_EXAMPLE_FILENAME = "EmptyProfileExample.xml";
	
	public static String PROFILE_NAME_EXAMPLE = "EmptyProfile";
	
	@Test
	public void testCreateUpdateDeleteGenericResource() throws Exception {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(PROFILE_EXAMPLE_FILENAME);
		String xml = new BufferedReader(new InputStreamReader(inputStream)).lines()
				.collect(Collectors.joining("\n"));
		logger.debug("Body\n{}", xml);
		Profile profile = new Profile();
		profile.createOrUpdate(PROFILE_NAME_EXAMPLE, xml);
		/*
		Thread.sleep(TimeUnit.SECONDS.toMillis(30));
		profile.createOrUpdate(PROFILE_NAME_EXAMPLE, "<metadataformat type=\"" + PROFILE_NAME_EXAMPLE + "\" />");
		Thread.sleep(TimeUnit.SECONDS.toMillis(30));
		profile.delete(PROFILE_NAME_EXAMPLE);
		*/
	}
	
}
