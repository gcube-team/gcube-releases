package org.gcube.gcat.client;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
	public void safeTest() throws IOException, ParserConfigurationException, SAXException {
		
		ObjectMapper mapper = new ObjectMapper();
		JavaType arrayType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class);
		
		Profile profile = new Profile();
		String profilesString = profile.list();
		logger.debug("Got Profiles {}", profilesString);
		
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
			is = new InputSource(new StringReader(retAsXML));
		    dBuilder.parse(is);
			
			Assert.assertTrue(xml.compareTo(retAsXML) == 0);
			
			String json = profile.read(name, true);
			logger.debug("Got JSON Profile {}", json);
			 mapper.readTree(json);
		}
		
	}
	
}
