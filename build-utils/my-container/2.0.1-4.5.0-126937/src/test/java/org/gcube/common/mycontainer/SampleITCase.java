/**
 * 
 */
package org.gcube.common.mycontainer;

import static junit.framework.Assert.*;
import static org.gcube.common.mycontainer.TestUtils.*;

import javax.inject.Inject;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(MyContainerTestRunner.class)
public class SampleITCase {

	@Inject
	static MyContainer container;

	static boolean beforeClassInvoked;
	boolean beforeInvoked;
	
	static boolean afterInvoked;
	
	@Deployment
	static Gar gar = syntheticGar();
	
	@BeforeClass
	public static void beforeClass() {
		
		assertNotNull(container);
		
		beforeClassInvoked=true;
	}
	
	@Before
	public void before() {
		beforeInvoked=true;
	}
	
	@Test
	public void test() {
		
		//beforeClass() has been normally invoked
		assertTrue(beforeClassInvoked);
		
		//before() has been normally invoked
		assertTrue(beforeInvoked);
		
		assertTrue(container.isRunning());
	}

	@After
	public void after() {

		afterInvoked=true;
		//container will be stopped after
		assertTrue(container.isRunning());

	}
	
	@AfterClass
	public static void afterClass() {

		assertTrue(afterInvoked);
		
		//container will be stopped after
		assertTrue(container.isRunning());

	}
}
