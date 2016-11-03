package org.gcube.informationsystem.collector;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.gcube.informationsystem.collector.impl.resources.GCUBEInstanceStateResource;
import org.gcube.informationsystem.collector.impl.resources.GCUBEProfileResource;
import org.gcube.informationsystem.collector.impl.resources.GCUBEXMLResource;
import org.gcube.informationsystem.collector.impl.resources.DAIXResource.MalformedResourceException;
import org.gcube.informationsystem.collector.impl.resources.GCUBEXMLResource.MalformedXMLResourceException;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.XMLStorage;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.XMLStorage.XMLStorageNotAvailableException;
import org.junit.Assert;
import org.xmldb.api.base.XMLDBException;

import junit.framework.TestCase;

public class XMLStorageManagerTest extends TestCase {

    private static final String resourceProfileName = "profileName";
	
    private static final String resourceInstanceStateName = "instanceStateName";
    
    XMLStorage storage;
    
    protected void setUp() throws Exception {
	super.setUp();
	
	storage = new XMLStorage();
	storage.initialize(1000);
    }

    protected void tearDown() throws Exception {
	super.tearDown();
	storage.shutdown(true);
    }        

    public void testCreateCollection() {
	try {
	    storage.createCollection("Profiles/RunningInstance");
	} catch (XMLDBException e) {
	    Assert.fail("XMLDB failure" + e.getMessage());
	} catch (XMLStorageNotAvailableException e) {
	    Assert.fail("Storage not available " + e.getMessage());
	}
    }

    public void testShutdown() {
	//fail("Not yet implemented");
    }

    public void testLoadAllCollections() {
	
    }

   public void testStoreResource() {
	
	try {
	    GCUBEXMLResource XMLpresource;
	    GCUBEProfileResource presource = new GCUBEProfileResource();
	    presource.setResourceName(resourceProfileName);
	    presource.setContent("<Resource><ID/><Type>GHN</Type><Profile></Profile></Resource>");
	    XMLpresource = new GCUBEXMLResource(presource);
	    System.out.println("testStoreResource : storing \n"+ XMLpresource.toString()+ "\n in collection " + XMLpresource.getCollectionName());
	    storage.storeResource(XMLpresource);
	    System.out.println("testStoreResource : profile "+ XMLpresource.getResourceName()+ " successfully stored");
	    GCUBEInstanceStateResource iresource = new GCUBEInstanceStateResource();
	    iresource.setResourceName(resourceInstanceStateName);
	    //iresource.setContent("<"+ GCUBEInstanceStateResource.INSTANCESTATE_ROOT_ELEMENT + "><ns1:Task xmlns:ns1=\"http://gcube-system.org/namespaces/vremanagement/executor\"/> <ns9:Task xmlns:ns9=\"http://gcube-system.org/namespaces/vremanagement/executor\"/> </"+ GCUBEInstanceStateResource.INSTANCESTATE_ROOT_ELEMENT + ">");
	    
	    iresource.setContent(convertStreamToString(this.getClass().getResourceAsStream("samplestate.xml")));
	    GCUBEXMLResource XMLiresource = new GCUBEXMLResource(iresource);
	    System.out.println("testStoreResource : storing \n"+ XMLiresource.toString()+ "\n in collection " + XMLiresource.getCollectionName());
	    storage.storeResource(XMLiresource);
	    System.out.println("testStoreResource : instance state "+ XMLiresource.getResourceName()+ " successfully stored");
	} catch (MalformedResourceException e) {
	    Assert.fail("Malformed Profile resource");
	} catch (XMLStorageNotAvailableException e) {
	    Assert.fail("Storage not available " + e.getMessage());
	} catch (MalformedXMLResourceException e) {
	    Assert.fail("Malformed XML resource");
	} catch (IOException e) {
	    Assert.fail("Malformed XML resource");
	}	
	
    } 

    public void testIsLocked() {
    }

    public void testRetrieveResourceContent() {
	//this.testStoreResource();
	GCUBEXMLResource XMLresource;
	GCUBEProfileResource presource = new GCUBEProfileResource();
	presource.setResourceName(resourceProfileName);
	presource.setResourceType("GHN");	
	try {
	    XMLresource = new GCUBEXMLResource(presource);
	    storage.retrieveResourceContent(XMLresource);
	    System.out.println("testRetrieveResourceContent: Retrieved content " + XMLresource.toString());
	} catch (Exception e) {
	    e.printStackTrace();
	    Assert.fail("Failed to retrieve resource's content");
	}
	GCUBEInstanceStateResource iresource = new GCUBEInstanceStateResource();
	iresource.setResourceName(resourceInstanceStateName);
	try {
	    XMLresource = new GCUBEXMLResource(iresource);
	    storage.retrieveResourceContent(XMLresource);
	    System.out.println("testRetrieveResourceContent: Retrieved content " + XMLresource.toString());
	} catch (Exception e) {
	    e.printStackTrace();
	    Assert.fail("Failed to retrieve resource's content");
	}
    }

    public void testExecuteXPathQuery() {
	
    }
    

    public void testListAllCollectionResourceIDs() {
	GCUBEProfileResource presource = new GCUBEProfileResource();
	presource.setResourceType("GHN");
	try {
	    System.out.println("testListAllCollectionResourceIDs: Looking in collection " + presource.getCollectionName());
	    for (String id : storage.listAllCollectionResourceIDs(presource.getCollectionName()))
		    System.out.println("\ttestListAllCollectionResourceIDs: Found Resource ID " + id);
	    
	    System.out.println("testListAllCollectionResourceIDs: Looking in collection " + new GCUBEInstanceStateResource().getCollectionName());
	    for (String id : storage.listAllCollectionResourceIDs(new GCUBEInstanceStateResource().getCollectionName()))
		    System.out.println("\ttestListAllCollectionResourceIDs: Found Resource ID " + id);
	
	} catch (MalformedResourceException e) {
	    e.printStackTrace();
	} catch (XMLStorageNotAvailableException e) {
	    e.printStackTrace();
	}
		
	
    }

   public void testDeleteResource() {
	GCUBEProfileResource presource = new GCUBEProfileResource();
	presource.setResourceType("GHN");
	try {
	    for (String id : storage.listAllCollectionResourceIDs(presource.getCollectionName())) {
		presource.setResourceName(id);
		storage.deleteResource(new GCUBEXMLResource(presource));
		System.out.println("testDeleteResource: resource " + id + " deleted");		
	    }
	    this.testListAllCollectionResourceIDs();
	} catch (MalformedResourceException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    
	    e.printStackTrace();
	}	
    }
   
    public void testDeleteAllResourcesFromCollection() {
	
    }


    public void testGetStatus() {
	
    }

    public void testSetStatus() {
	
    }
    
    private String convertStreamToString(java.io.InputStream is)
           throws IOException {
            if (is != null) {
                Writer writer = new StringWriter();
    
                char[] buffer = new char[1024];
                try {
                    Reader reader = new BufferedReader(
                            new InputStreamReader(is, "UTF-8"));
                    int n;
                    while ((n = reader.read(buffer)) != -1) {
                        writer.write(buffer, 0, n);
                    }
                } finally {
                    is.close();
                }
                return writer.toString();
            } else {       
                return "";
            }
        }

}
