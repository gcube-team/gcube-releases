/**
 * 
 */
package org.gcube.vremanagement.resourcemanager.impl.resources;

import static org.junit.Assert.*;

import org.gcube.common.core.scope.GCUBEScope;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author manuele
 *
 */
public class ScopedGHNTest {

	
	private static final String ghnId="bb6e2d69-4d49-42ef-9985-fae2cba85753";
	private static final String scope="/gcube";
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedGHN#find()}.
	 */
	@Test
	public final void testFind() {
		
	}
	
	
	@Test
	public void checkGhnType(){
		ScopedGHN ghnObj=new ScopedGHN(ghnId, GCUBEScope.getScope(scope));
		ghnObj.id=ghnId;
		try {
			assertTrue(ghnObj.checkGhnType(ghnId, GCUBEScope.getScope(scope)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedGHN#addToScope()}.
	 */
	@Test
	public final void testAddToScope() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedGHN#removeFromScope()}.
	 */
	@Test
	public final void testRemoveFromScope() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedGHN#ScopedGHN(java.lang.String, org.gcube.common.core.scope.GCUBEScope)}.
	 */
	@Test
	public final void testScopedGHN() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource#doAction(org.gcube.vremanagement.resourcemanager.impl.operators.Operator.ACTION)}.
	 */
	@Test
	public final void testDoAction() {
		fail("Not yet implemented");
	}

}
