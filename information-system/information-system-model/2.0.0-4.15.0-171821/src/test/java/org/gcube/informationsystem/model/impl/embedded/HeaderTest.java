package org.gcube.informationsystem.model.impl.embedded;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.gcube.informationsystem.model.impl.embedded.HeaderImpl;
import org.gcube.informationsystem.model.impl.utils.ISMapper;
import org.gcube.informationsystem.model.reference.embedded.Header;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderTest {

	private static Logger logger = LoggerFactory.getLogger(HeaderTest.class);
	
	@Test
	public void headerTest() throws Exception {
		HeaderImpl header = new HeaderImpl(UUID.randomUUID());
		Date date = Calendar.getInstance().getTime();
		header.creationTime = date;
		header.lastUpdateTime = date;
		header.creator = Header.UNKNOWN_USER;
		
		String json = ISMapper.marshal(header);
		logger.debug(json);
		
		Header h = ISMapper.unmarshal(Header.class, json);
		
		Assert.assertTrue(h.getCreationTime().compareTo(date)==0);
		Assert.assertTrue(h.getLastUpdateTime().compareTo(date)==0);
		
	}

}
