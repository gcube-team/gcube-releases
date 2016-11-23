package org.gcube.informationsystem.impl.facet;

import java.io.IOException;

import org.gcube.informationsystem.impl.entity.facet.ContactFacetImpl;
import org.gcube.informationsystem.model.entity.facet.ContactFacet;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class ContactFacetImplTest {

	private static Logger logger = LoggerFactory.getLogger(ContactFacetImplTest.class);
	
	@Test
	public void serializeDeserialize() throws IOException {
		
		ContactFacetImpl contactFacet = new ContactFacetImpl();
		
		contactFacet.setName("luca.frosini");
		contactFacet.setEMail("test@d4science.org");
		
		ObjectMapper mapper = new ObjectMapper();
		String unmarshalled = mapper.writeValueAsString(contactFacet);
		logger.debug(unmarshalled);
		
		ContactFacetImpl cf = mapper.readValue(unmarshalled, ContactFacetImpl.class);
		String reUnmarshalled = mapper.writeValueAsString(cf);
		logger.debug(reUnmarshalled);
		
	}
	
	@Test
	public void test() throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		
		
		String jSONString = "{ "
			+ "\"header\" : {"
				+ "\"id\":\"ABCDEF\","
				+ "\"creator\":\"luca.frosini\","
				+ "\"name\":\"" + ContactFacet.NAME + "\","
				+ "\"description\":\"" + ContactFacet.DESCRIPTION + "\","
				+ "\"version\":\"" + ContactFacet.VERSION + "\""
			+ "},"
			+ "\"name\":\"Luca\","
			+ "\"eMail\":\"test@d4science.org\","
			+ "\"website\":\"http://www.d4science.org\","
			+ "\"address\":\"Via G. Moruzzi, 1\","
			+ "\"phoneNumber\":\"123456789\""
		+ "}";
		ContactFacetImpl contactFacet = mapper.readValue(jSONString, ContactFacetImpl.class);
		
		
		
		String unmarshalled = mapper.writeValueAsString(contactFacet);
		logger.debug(unmarshalled);
	}
	
	
	@Test
	public void testRegex(){
		
	}
	
	
}
