package org.gcube.datacatalogue.catalogue.entities;

import org.gcube.datacatalogue.catalogue.ScopedTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalogueItemTest extends ScopedTest {

	private static final Logger logger = LoggerFactory.getLogger(CatalogueItemTest.class);
	
	@Test
	public void read() {
		String id = "3e7b6924-7851-49fc-a08a-bd8a86d4af62";
		CatalogueItem catalogueItem = new CatalogueItem();
		catalogueItem.setId(id);
		
		String res = catalogueItem.read();
		logger.debug(res);
		
	}
	
}
