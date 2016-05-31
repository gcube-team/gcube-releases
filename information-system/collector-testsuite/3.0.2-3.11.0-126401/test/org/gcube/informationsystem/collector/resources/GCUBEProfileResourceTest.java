package org.gcube.informationsystem.collector.resources;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.gcube.informationsystem.collector.impl.resources.GCUBEProfileResource;
import org.gcube.informationsystem.collector.impl.resources.DAIXResource.MalformedResourceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

public class GCUBEProfileResourceTest extends TestCase {

    private GCUBEProfileResource resource;
    
    @Before
    public void setUp() throws Exception {
	resource = new GCUBEProfileResource();
    }

    @After
    public void tearDown() throws Exception {
    }
    

    @Test
    public void testSetContentString() {
	StringBuilder builder = new StringBuilder();
	builder.append("<Resource version=\"0.4.x\">");		
	builder.append("<ID>3bb6e850-94d2-11df-8d06-8e825c7c7b8d</ID>");	
	builder.append("<Type>MetadataCollection</Type>");
	builder.append("<Scopes>");
	builder.append("<Scope>/d4science.research-infrastructures.eu/FARM/FCPPS</Scope>");
	builder.append("</Scopes>");
	builder.append("<Profile>");
	builder.append("<Description />");
	builder.append("</Profile>");
	builder.append("</Resource>");
	
	try {
	    resource.setContent(builder.toString());	    
	} catch (MalformedResourceException e) {
	    e.printStackTrace();
	}
    }    
    
   
    @Test
    public void testBaseDAIXResource() {
	//fail("Not yet implemented");
    }

    @Test
    public void testGetResourceName() {
	//fail("Not yet implemented");
    }

    @Test
    public void testSetResourceName() {
	resource.setResourceName("TestResouceName");
    }

    @Test
    public void testGetResourceURI() {
	//fail("Not yet implemented");
    }

    @Test
    public void testSetResourceURI() {
	//fail("Not yet implemented");
    }

    @Test
    public void testGetContent() {
	//fail("Not yet implemented");
    }

    @Test
    public void testSetContentDocument() {
	try {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    StringReader reader = new StringReader("<Profile><Resource><ID/><Type>GHN</Type><xsd:schema xmlns:q=\"http://gcube.org/searchservice/queryobject\" "+
							"xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>"+
							"</Resource></Profile>");
	    InputSource source = new InputSource(reader);
	    //Document doc = builder.parse(source);
	    resource.setContent(builder.parse(source));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }


    @Test
    public void testSetCollectionName() {
	resource.setResourceType("testCollection");
    }
    
    @Test
    public void testGetCollectionName() {
	this.testSetContentString();
	try {
	    System.out.println("Resource collection is " + resource.getCollectionName());
	} catch (MalformedResourceException e) {
	    e.printStackTrace();
	}
    }

    @Test
    public void testToString() {
	this.testSetContentDocument();
	System.out.println("Resource from DOM is: " + resource.toString());
	//resource.setContent(null);
	this.testSetContentString();
	System.out.println("Resource from String is: " + resource.toString());
    }

}
