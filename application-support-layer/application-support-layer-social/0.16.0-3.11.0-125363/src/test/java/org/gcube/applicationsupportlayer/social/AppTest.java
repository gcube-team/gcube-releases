package org.gcube.applicationsupportlayer.social;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;

/**
 * Unit test for simple App.
 */
public class AppTest 
extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AppTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( AppTest.class );
	}


	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getTestSession() {

		ASLSession toReturn = SessionManager.getInstance().getASLSession("11", "andrea.rossi");
		toReturn.setScope("/gcube/devsec/devVRE");
		toReturn.setUserFullName("Andrea Rossi");
		toReturn.setUserEmailAddress("m.assante@gmail.com");
		toReturn.setGroupModelInfos("TheGroup", 123L);

		return toReturn;
	}
	/**
	 * @throws Exception 
	 * 
	 */
	public void testApp() throws Exception { 
		
	}
}
