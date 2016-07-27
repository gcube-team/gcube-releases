package org.gcube.common.informationsystem.publisher.impl.instancestates;

import org.gcube.common.informationsystem.publisher.impl.generic.WSDAIXResource;

public final class InstanceResource extends WSDAIXResource {

    @Override
    public String getCollection() {
	return "Properties";
    }
   
    @Override
    public void setCollection(String collection) {
	//the collection name can't be changed
    }
    
    @Override
    public ISRESOURCETYPE getType() {
	return ISRESOURCETYPE.RPD;
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
    static protected InstanceResource fromGCUBEWSResource(WSRPDocument rpd) throws Exception {
	InstanceResource resource = new InstanceResource();	
	resource.setID(rpd.getID());	
	resource.setDocument(rpd.query());
	return resource;	
    }

}
