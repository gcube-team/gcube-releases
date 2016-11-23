package org.gcube.portlets.user.tokengenerator.client;

import static org.junit.Assert.assertTrue;

import org.gcube.portlets.user.tokengenerator.client.ui.TokenWidget;
import org.gcube.portlets.user.tokengenerator.server.TokenServiceImpl;
import org.junit.Test;

/**
 * Test class
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class JTests {

	@Test
	public void testUsername() {
		assertTrue(new TokenServiceImpl().getTestUser().equals(TokenServiceImpl.TEST_USER));
	}
	
	@Test
	public void testRegExQualifiedToken(){
		
		
		String qualifier = "jklasdsjaioASjkhdikaus";
		assertTrue(qualifier.matches(TokenWidget.REGEX_QUALIFIER));
		
	}

}
