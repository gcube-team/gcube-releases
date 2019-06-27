package org.gcube.gcat.profile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.gcube.gcat.ContextTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class ProfileTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(ProfileTest.class);
	
	@Test
	public void list() throws Exception {
		ISProfile profile = new ISProfile();
		ArrayNode arrayNode = profile.list();
		logger.debug("{}", arrayNode);
	}
	
	
	@Test
	public void read() throws Exception {
		String profileID = "EmptyProfile";
		ISProfile profile = new ISProfile();
		boolean xml = true;
		String ret = profile.read(profileID, xml);
		logger.debug("XML :\n{}", ret);
		xml = false;
		ret = profile.read(profileID, xml);
		logger.debug("JSON : \n{}", ret);
	}
	
	@Test
	public void listRead() throws Exception {
		ISProfile profile = new ISProfile();
		ArrayNode arrayNode = profile.list();
		logger.debug("Found {} profiles", arrayNode.size());
		Iterator<JsonNode> iterator = arrayNode.iterator();
		while(iterator.hasNext()) {
			String profileID = iterator.next().asText();
			boolean xml = true;
			String ret = profile.read(profileID, xml);
			logger.debug("XML :\n{}", ret);
			xml = false;
			ret = profile.read(profileID, xml);
			logger.debug("JSON : \n{}", ret);
		}
	}
	
	@Test
	public void testCreateOrUpdate() throws Exception {
		String xml = "<metadataformat type=\"TestProfile\"><metadatafield><fieldName>test</fieldName><mandatory>false</mandatory><dataType>String</dataType><maxOccurs>1</maxOccurs><note>Test Field</note></metadatafield><metadatafield><fieldName>Population</fieldName><mandatory>false</mandatory><dataType>String</dataType><maxOccurs>*</maxOccurs><defaultValue/><note>The population of the model</note><tagging create=\"true\" separator=\"_\">onValue</tagging><grouping create=\"true\">onFieldName_onValue</grouping></metadatafield></metadataformat>";
		ISProfile profile = new ISProfile();
		profile.createOrUpdate("TestProfile", xml);
	}
	
	@Test
	public void testDelete() throws Exception {
		ISProfile profile = new ISProfile();
		profile.delete("TestProfile");
	}
	
	public static String PROFILE_EXAMPLE_FILENAME = "EmptyProfileExample.xml";
	
	public static String PROFILE_NAME_EXAMPLE = "EmptyProfile";
	
	@Test
	public void testCreateUpdateDeleteGenericResource() throws Exception {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(PROFILE_EXAMPLE_FILENAME);
		String xml = new BufferedReader(new InputStreamReader(inputStream)).lines()
				.collect(Collectors.joining("\n"));
		logger.debug("Body\n{}", xml);
		ISProfile profile = new ISProfile();
		profile.createOrUpdate(PROFILE_NAME_EXAMPLE, xml);
		/*
		Thread.sleep(TimeUnit.SECONDS.toMillis(30));
		profile.createOrUpdate(PROFILE_NAME_EXAMPLE, "<metadataformat type=\"" + PROFILE_NAME_EXAMPLE + "\" />");
		Thread.sleep(TimeUnit.SECONDS.toMillis(30));
		profile.delete(PROFILE_NAME_EXAMPLE);
		*/
	}
	
}
