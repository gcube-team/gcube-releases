package org.gcube.informationsystem.collector.impl.resources;

/**
 * 
 * Represent a piece of state of a gCube service
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */

public class GCUBEInstanceStateResource extends BaseDAIXResource  {
    
    public static final String TYPE = "InstanceState";

    public static final String INSTANCESTATE_ROOT_ELEMENT = "ResourceProperties";
    
    public static final String ROOT_COLLECTION_NAME = "Properties";
    
    /**
     * {@inheritDoc}
     */
    public String getCollectionName() {
        return ROOT_COLLECTION_NAME;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setCollectionName(String collectionName) {
     //   cannot change the collection name
    }          
    
}
