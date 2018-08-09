package org.gcube.common.informationsystem.publisher.impl.resources;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.informationsystem.publisher.impl.generic.WSDAIXResource;
import org.xml.sax.InputSource;


public final class ProfileResource extends WSDAIXResource {

       
    @Override
    public ISRESOURCETYPE getType() {
	return ISRESOURCETYPE.PROFILE;
    }
    
    @Override
    public void setType(ISRESOURCETYPE type) {
	//cant' change the type
    }   
    
    /**
     * Creates an instance resource from an {@link WSRPDocument} instance
     * @param rpd the RPD
     * @return the new resource
     * @throws Exception if it is unable to query the RPD
     */
    static protected ProfileResource fromGCUBEResource(GCUBEResource resource) throws Exception {
	ProfileResource newresource = new ProfileResource();	
	newresource.setID(resource.getID());	
	StringWriter writer = new StringWriter();
	resource.store(writer);
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	factory.setNamespaceAware(true);
	DocumentBuilder builder = factory.newDocumentBuilder();
	StringReader reader = new StringReader(writer.toString());	
	newresource.setDocument(builder.parse(new InputSource(reader)));
	newresource.setCollection("Profiles/" + resource.getType());
	return newresource;	
    }

}
