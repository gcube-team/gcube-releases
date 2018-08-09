package it.eng.edison.usersurvey_portlet.client;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import it.eng.edison.usersurvey_portlet.server.GreetingServiceImpl;




public class GreetingServiceImplTests {
	
	
    /** The category name. */
    private static String CATEGORY_NAME = "Database";


    
	@Test
	public void getCategoryNameTest() {
		GreetingServiceImpl greetingServiceImpl = new GreetingServiceImpl();
		assertEquals("Database", greetingServiceImpl.getCATEGORY_NAME());
	}


}
