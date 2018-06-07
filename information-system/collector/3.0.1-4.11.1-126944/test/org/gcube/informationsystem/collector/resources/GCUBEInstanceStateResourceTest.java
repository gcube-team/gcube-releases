package org.gcube.informationsystem.collector.resources;

import org.gcube.informationsystem.collector.impl.resources.GCUBEInstanceStateResource;
import org.gcube.informationsystem.collector.impl.resources.DAIXResource.MalformedResourceException;

import junit.framework.TestCase;

public class GCUBEInstanceStateResourceTest extends TestCase {

    private GCUBEInstanceStateResource resource;
    
    protected void setUp() throws Exception {
	super.setUp();	
	resource = new GCUBEInstanceStateResource();
	
    }

    protected void tearDown() throws Exception {
	super.tearDown();
    }


    public void testSetContentString() {
	try {
	    resource.setContent("<ns1:ID xmlns:ns1=\"http://gcube-system.org/namespaces/vremanagement/executor\"/> <ns9:Task xmlns:ns9=\"http://gcube-system.org/namespaces/vremanagement/executor\">");
	} catch (MalformedResourceException e) {
	    e.printStackTrace();
	}
    }
    
    public void testGetCollectionName() {
	assertTrue(resource.getCollectionName() == "Properties");
    }

    public void testSetCollectionName() {
	fail("Not yet implemented");
    }

    public void testGetContent() {
	fail("Not yet implemented");
    }

    public void testSetContentDocument() {
	//resource.setContent(null);
    }


    public void testSetResourceName() {
	resource.setResourceName("InstanceStateTest");
    }
    
    public void testGetResourceName() {
	this.testSetResourceName();
	System.out.println("Resource name is " + resource.getResourceName());
    }


    public void testGetResourceURI() {
	fail("Not yet implemented");
    }

    public void testSetResourceURI() {
	fail("Not yet implemented");
    }


    public void testToString() {
	this.testSetContentString();
	System.out.println("Resource content is " + resource.toString());
    }

}
