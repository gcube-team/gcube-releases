/**
 * 
 */
package org.gcube.common.informationsystem.publisher.impl;

import static org.junit.Assert.*;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.publisher.ISPublisher;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class GCUBEPublisherTest {
    
    private GCUBEScope scope;
    private GCUBERunningInstance resource;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
	scope = GCUBEScope.getScope("/gcube/devNext");
	resource = GHNContext.getImplementation(GCUBERunningInstance.class);
	resource.setID("06ecc470-9bb9-11e0-a3b0-f16fbdb40244");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link org.gcube.common.informationsystem.publisher.impl.GCUBEPublisher#GCUBEPublisher()}.
     */
    @Test
    public void testGCUBEPublisher() {
	fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.gcube.common.informationsystem.publisher.impl.GCUBEPublisher#registerGCUBEResource(org.gcube.common.core.resources.GCUBEResource, org.gcube.common.core.scope.GCUBEScope, org.gcube.common.core.security.GCUBESecurityManager)}.
     */
    @Test
    public void testRegisterGCUBEResource() {
	fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.gcube.common.informationsystem.publisher.impl.GCUBEPublisher#removeGCUBEResource(java.lang.String, java.lang.String, org.gcube.common.core.scope.GCUBEScope, org.gcube.common.core.security.GCUBESecurityManager)}.
     */
    @Test
    public void testRemoveGCUBEResource() {
	 GCUBESecurityManagerImpl managerSec = new GCUBESecurityManagerImpl() {
		public boolean isSecurityEnabled() {
		    return false;
		}
	    };
	    ISPublisher publisher = null;
	    try {
		publisher = GHNContext.getImplementation(ISPublisher.class);
	    } catch (Exception e1) {
		System.err.println("Unable to get te ISPublisher implementation");
		System.exit(0);
	    }

		try {
		    publisher.removeGCUBEResource(resource.getID(), resource.getType(), scope, managerSec);
		    System.out.println("Resource with ID " + resource.getID() + " removed");
		} catch (Exception e) {
		    System.err.println("Unable to remmove resource with ID " + resource.getID());
		    e.printStackTrace();
		}
    }

    /**
     * Test method for {@link org.gcube.common.informationsystem.publisher.impl.GCUBEPublisher#updateGCUBEResource(org.gcube.common.core.resources.GCUBEResource, org.gcube.common.core.scope.GCUBEScope, org.gcube.common.core.security.GCUBESecurityManager)}.
     */
    @Test
    public void testUpdateGCUBEResource() {
	fail("Not yet implemented");
    }

}
