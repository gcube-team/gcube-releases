package org.gcube.informationsystem.collector.resources;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.gcube.informationsystem.collector.impl.resources.BaseDAIXResource;
import org.gcube.informationsystem.collector.impl.resources.GCUBEInstanceStateResource;
import org.gcube.informationsystem.collector.impl.resources.GCUBEProfileResource;
import org.gcube.informationsystem.collector.impl.resources.GCUBEXMLResource;
import org.gcube.informationsystem.collector.impl.resources.GCUBEXMLResource.MalformedXMLResourceException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

public class GCUBEXMLResourceTest  extends TestCase {

    private GCUBEXMLResource XMLiresource;
    private GCUBEXMLResource XMLiresourceDOM;
    private GCUBEXMLResource XMLpresource;
    private GCUBEInstanceStateResource iresourceString;
    private GCUBEInstanceStateResource iresourceDOM;
    private GCUBEProfileResource presource;

    
    @Before
    public void setUp() throws Exception {
	iresourceString = new GCUBEInstanceStateResource();
	iresourceString.setResourceName("Instance");
	iresourceString.setContent("<"+ GCUBEInstanceStateResource.INSTANCESTATE_ROOT_ELEMENT + "><ns1:Task xmlns:ns1=\"http://gcube-system.org/namespaces/vremanagement/executor\"/> <ns9:Task xmlns:ns9=\"http://gcube-system.org/namespaces/vremanagement/executor\"/></"+ GCUBEInstanceStateResource.INSTANCESTATE_ROOT_ELEMENT + ">");	
	XMLiresource = new GCUBEXMLResource(iresourceString);
	
	iresourceDOM = new GCUBEInstanceStateResource();
	iresourceDOM.setResourceName("InstanceDOM");
	iresourceDOM.setContent(this.getAsDocument("/Users/manuele/work/workspace/IS/Branches/InformationCollector.3.0/test/org/gcube/informationsystem/collector/resources/samplestate.xml"));
	
	presource = new GCUBEProfileResource();
	presource.setResourceName("Profile");
	presource.setContent("<Profile><Resource><ID/><Type>GHN</Type></Resource></Profile>");				
	XMLpresource = new GCUBEXMLResource(presource);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGCUBEXMLResourceBaseDAIXResource() {
	try {
	    XMLiresource = new GCUBEXMLResource(iresourceString);
	    XMLpresource = new GCUBEXMLResource(presource);
	    //XMLiresourceDOM = new GCUBEXMLResource(iresourceDOM);	
	    System.out.println("Resource to wrap " + iresourceDOM.toString());
	    XMLiresourceDOM = new GCUBEXMLResource(iresourceDOM);
	    System.out.println("Wrapped Resource" + XMLiresourceDOM.toString());
	
	} catch (MalformedXMLResourceException e) {
	    Assert.fail("failed to create GCUBEXMLResources");
	}	
	
    }

    @Test
    public void testGCUBEXMLResourceXMLResourceString() {
	//fail("Not yet implemented");
    }

    @Test
    public void testGetCollectionName() {
	try {
	    System.out.println("Instance collection Name " + XMLiresource.getCollectionName() );
	    System.out.println("Profile collection Name " + XMLpresource.getCollectionName() );
	} catch (MalformedXMLResourceException e) {
	    e.printStackTrace();
	}
    }
    
    @Test
    public void testdeserializeFromIndexing() {
	try {
        	String content = XMLpresource.toString();
        	System.out.println("Profile Content with envelop " + content);
        	GCUBEXMLResource temp = new GCUBEXMLResource(new BaseDAIXResource("name"));
        	temp.deserializeFromIndexing(content, true);
        	System.out.println("New Profile Content with envelop " + temp.toString());     	
        	String content2 = XMLiresource.toString();
        	System.out.println("Instance State Content with envelop " + content2);
        	GCUBEXMLResource temp2 = new GCUBEXMLResource(new BaseDAIXResource("name"));
        	temp2.deserializeFromIndexing(content2, true);
        	System.out.println("New Instance State Content with envelop " + temp2.toString());
	} catch (MalformedXMLResourceException e) {
	    Assert.fail("failed to create GCUBEXMLResources");
	}
    }

    @Test
    public void testGetResourceName() {
	//fail("Not yet implemented");
    }

    @Test
    public void testGetTerminationTime() {
	//fail("Not yet implemented");
    }

    @Test
    public void testGetLastUpdateTime() {
	//fail("Not yet implemented");
    }

    @Test
    public void testSetTerminationTime() {
	//fail("Not yet implemented");
    }

    @Test
    public void testGetLastUpdateTimeinMills() {
	//fail("Not yet implemented");
    }

    @Test
    public void testSetGroupKey() {
	//fail("Not yet implemented");
    }

    @Test
    public void testGetEntryKey() {
	//fail("Not yet implemented");
    }

    @Test
    public void testSetEntryKey() {
	//fail("Not yet implemented");
    }

    @Test
    public void testSetSource() {
	//fail("Not yet implemented");
    }

    @Test
    public void testGetSource() {
	//fail("Not yet implemented");
    }

    @Test
    public void testSetSourceKey() {
	//fail("Not yet implemented");
    }

    @Test
    public void testGetSourceKey() {
	//fail("Not yet implemented");
    }

    @Test
    public void testSetCompleteSourceKey() {
	//fail("Not yet implemented");
    }

    @Test
    public void testGetCompleteSourceKey() {
	//fail("Not yet implemented");
    }

    @Test
    public void testToString() {
	//System.out.println("Instance to string " + XMLiresource.toString() );
	//System.out.println("Instance from DOM to string " + XMLiresourceDOM.toString() );
	//System.out.println("Profile to string " + XMLpresource.toString() );
    }
    
    
    private Document getAsDocument(String filename) throws Exception {
	try {
            File file = new File(filename);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(file);
            
        } catch ( Exception e ) {
            System.out.println("Unable to load document: " + filename);    
        }
        return null;
    }


}
