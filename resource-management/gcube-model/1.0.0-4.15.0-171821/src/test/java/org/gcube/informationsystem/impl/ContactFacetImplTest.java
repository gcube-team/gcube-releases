package org.gcube.informationsystem.impl;

import java.io.IOException;

import org.gcube.informationsystem.model.impl.utils.ISMapper;
import org.gcube.resourcemanagement.model.impl.entity.facet.ContactFacetImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.ContactFacet;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ContactFacetImplTest {

	private static Logger logger = LoggerFactory.getLogger(ContactFacetImplTest.class);
	
	@Test
	public void serializeDeserialize() throws IOException {
		
		ContactFacetImpl contactFacet = new ContactFacetImpl();
		
		contactFacet.setName("luca.frosini");
		contactFacet.setEMail("test@d4science.org");
		
		String marshalled = ISMapper.marshal(contactFacet);
		logger.debug(marshalled);
		
		ContactFacetImpl cf = ISMapper.unmarshal(ContactFacetImpl.class, marshalled);
		String reMarshalled = ISMapper.marshal(cf);
		logger.debug(reMarshalled);
		
	}
	
	@Test
	public void test() throws Exception{
		String json = "{ "
			+ "\"@class\":\"ContactFacet\","	
			+ "\"header\" : {"
				+ "\"@class\":\"Header\","
				+ "\"creator\":\"luca.frosini\""
			+ "},"
			+ "\"name\":\"Luca\","
			+ "\"eMail\":\"test@d4science.org\","
			+ "\"website\":\"http://www.d4science.org\","
			+ "\"address\":\"Via G. Moruzzi, 1\","
			+ "\"phoneNumber\":\"123456789\""
		+ "}";
		
		ContactFacet contactFacet = ISMapper.unmarshal(ContactFacet.class, json);
		
		String marshalled = ISMapper.marshal(contactFacet);
		logger.debug(marshalled);
	}
	
}
