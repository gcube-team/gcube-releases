package org.gcube.gcat.client;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.NotFoundException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ProfileTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(ProfileTest.class);
	
	@Test
	public void safeTest() throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		JavaType arrayType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class);
		
		Profile profile = new Profile();
		String profilesString = profile.list();
		logger.debug("Got Profiles {}\n", profilesString);
		
		List<String> profiles = mapper.readValue(profilesString, arrayType);
		for(String name : profiles) {
			String xml = profile.read(name);
			logger.debug("Got XML Profile {}", xml);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    InputSource is = new InputSource(new StringReader(xml));
		    dBuilder.parse(is);
		    
		    String retAsXML = profile.read(name, false);
			logger.debug("Got XML (explicit) Profile {}", retAsXML);
			InputSource is2 = new InputSource(new StringReader(retAsXML));
		    dBuilder.parse(is2);
			
			//Assert.assertTrue(xml.compareTo(retAsXML) == 0);
			
			String json = profile.read(name, true);
			logger.debug("Got JSON Profile {}\n", json);
			 mapper.readTree(json);
		}
		
	}
	
	public static final String PROFILE_NAME = "TestEmptyProfile";
	public static final String PROFILE_XML = "<metadataformat type=\"%s\"><metadatafield><fieldName>test</fieldName><mandatory>false</mandatory><dataType>String</dataType><maxOccurs>1</maxOccurs><note>Test Field</note></metadatafield></metadataformat>";
	
	// @Test
	public void delete() throws Exception {
		Profile profile = new Profile();
		profile.delete(PROFILE_NAME);
	}
	
	@Test
	public void list() throws Exception {
		Profile profile = new Profile();
		String list = profile.list();
		logger.debug("Got Profiles {}\n", list);
	}
	
	@Test
	public void createDeleteTest() throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		JavaType arrayType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class);
		
		Profile profile = new Profile();
		profile.create(PROFILE_NAME, String.format(PROFILE_XML, PROFILE_NAME));
		
		
		String list = profile.list();
		logger.debug("Got Profiles {}\n", list);
		
		boolean found = false;
		int count = 1;
		
		while(!found) {
			List<String> profiles = mapper.readValue(list, arrayType);
			if(profiles.contains(PROFILE_NAME)) {
				found = true;
			}
			if(count >= 5) {
				throw new NotFoundException(String.format("%s not found after %d retries", PROFILE_NAME, count));
			}
			if(!found) {
				Thread.sleep(TimeUnit.SECONDS.toMillis(2*count));
				++count;
			}
		}
		
		String xml = profile.read(PROFILE_NAME);
		
		profile.delete(PROFILE_NAME);
		
		while(found) {
			List<String> profiles = mapper.readValue(list, arrayType);
			if(!profiles.contains(PROFILE_NAME)) {
				found = false;
			}
			if(count >= 5) {
				throw new Exception(String.format("%s still found after %d retries", PROFILE_NAME, count));
			}
			if(found) {
				Thread.sleep(TimeUnit.SECONDS.toMillis(2*count));
				++count;
			}
		}
		
	}
	
	
}
