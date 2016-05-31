package org.gcube.informationsystem.collector.impl.porttypes.wsdaix;

import java.rmi.RemoteException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.URI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmldb.api.base.XMLDBException;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.collector.impl.contexts.ICServiceContext;
import org.gcube.informationsystem.collector.impl.resources.BaseDAIXResource;
import org.gcube.informationsystem.collector.impl.resources.GCUBEInstanceStateResource;
import org.gcube.informationsystem.collector.impl.resources.GCUBEProfileResource;
import org.gcube.informationsystem.collector.impl.resources.GCUBEXMLResource;
import org.gcube.informationsystem.collector.impl.resources.DAIXResource.MalformedResourceException;
import org.gcube.informationsystem.collector.impl.resources.GCUBEXMLResource.MalformedXMLResourceException;
import org.gcube.informationsystem.collector.impl.utils.MetadataReader;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.State;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.XMLStorage.XMLStorageNotAvailableException;
import org.gcube.informationsystem.collector.stubs.wsdai.DataResourceUnavailableFaultType;
import org.gcube.informationsystem.collector.stubs.wsdai.GetDataResourcePropertyDocumentRequest;
import org.gcube.informationsystem.collector.stubs.wsdai.InvalidResourceNameFaultType;
import org.gcube.informationsystem.collector.stubs.wsdai.NotAuthorizedFaultType;
import org.gcube.informationsystem.collector.stubs.wsdai.ServiceBusyFaultType;
import org.gcube.informationsystem.collector.stubs.wsdaix.AddDocumentRequestWrapper;
import org.gcube.informationsystem.collector.stubs.wsdaix.AddDocumentResponseWrapper;
import org.gcube.informationsystem.collector.stubs.wsdaix.AddDocumentResponseWrapperResponse;
import org.gcube.informationsystem.collector.stubs.wsdaix.AddDocumentsRequest;
import org.gcube.informationsystem.collector.stubs.wsdaix.AddDocumentsResponse;
import org.gcube.informationsystem.collector.stubs.wsdaix.AddSchemaRequest;
import org.gcube.informationsystem.collector.stubs.wsdaix.AddSchemaResponse;
import org.gcube.informationsystem.collector.stubs.wsdaix.CollectionAlreadyExistsFaultType;
import org.gcube.informationsystem.collector.stubs.wsdaix.CreateSubcollectionRequest;
import org.gcube.informationsystem.collector.stubs.wsdaix.CreateSubcollectionResponse;
import org.gcube.informationsystem.collector.stubs.wsdaix.GetDocumentResponseWrapper;
import org.gcube.informationsystem.collector.stubs.wsdaix.GetDocumentResponseWrapperResponse;
import org.gcube.informationsystem.collector.stubs.wsdaix.GetDocumentsRequest;
import org.gcube.informationsystem.collector.stubs.wsdaix.GetDocumentsResponse;
import org.gcube.informationsystem.collector.stubs.wsdaix.GetSchemaRequest;
import org.gcube.informationsystem.collector.stubs.wsdaix.GetSchemaResponse;
import org.gcube.informationsystem.collector.stubs.wsdaix.InvalidCollectionNameFaultType;
import org.gcube.informationsystem.collector.stubs.wsdaix.RemoveDocumentRequestWrapper;
import org.gcube.informationsystem.collector.stubs.wsdaix.RemoveDocumentResponseWrapper;
import org.gcube.informationsystem.collector.stubs.wsdaix.RemoveDocumentResponseWrapperResponse;
import org.gcube.informationsystem.collector.stubs.wsdaix.XMLWrapperType;

import org.gcube.informationsystem.collector.stubs.wsdaix.RemoveDocumentsRequest;
import org.gcube.informationsystem.collector.stubs.wsdaix.RemoveDocumentsResponse;
import org.gcube.informationsystem.collector.stubs.wsdaix.RemoveSchemaRequest;
import org.gcube.informationsystem.collector.stubs.wsdaix.RemoveSchemaResponse;
import org.gcube.informationsystem.collector.stubs.wsdaix.RemoveSubcollectionRequest;
import org.gcube.informationsystem.collector.stubs.wsdaix.RemoveSubcollectionResponse;
import org.gcube.informationsystem.collector.stubs.wsdaix.SchemaAdditionMakesDocumentsInvalidFaultType;
import org.gcube.informationsystem.collector.stubs.wsdaix.SchemaAlreadyExistsFaultType;
import org.gcube.informationsystem.collector.stubs.wsdaix.SchemaDoesNotExistFaultType;
import org.gcube.informationsystem.collector.stubs.wsdaix.SchemaInvalidFaultType;
import org.gcube.informationsystem.collector.stubs.wsdaix.SchemaRemovalMakesDocumentsInvalidFaultType;
import org.gcube.informationsystem.collector.stubs.wsdaix.SchemaRemovalMakesSchemaInvalidFaultType;
import org.gcube.informationsystem.collector.stubs.wsdaix.XMLCollectionPropertyDocumentType;


/**
 * 
 * Implementation of the WS-DAIX XMLCollectionAccess port type
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 *
 */
public class XMLCollectionAccess extends GCUBEPortType {

    private static GCUBELog logger = new GCUBELog(XMLCollectionAccess.class);
                   
    
    /**
     * Implementation of CollectionAccess::GetDocuments
     * 
     * @param getDocumentsRequest
     * @return
     * @throws RemoteException
     * @throws ServiceBusyFaultType the service is already processing a request and ConcurrentAccess is false.
     * @throws InvalidResourceNameFaultType the supplied resource name is not known to the service.
     * @throws InvalidCollectionNameFaultType the supplied collection name is not known to the XML resource.
     */
    public GetDocumentsResponse getDocuments(GetDocumentsRequest getDocumentsRequest) 
    	throws RemoteException, ServiceBusyFaultType, InvalidResourceNameFaultType, 
    		InvalidCollectionNameFaultType {
		
	int size = getDocumentsRequest.getGetDocumentRequestWrapper().length;
	String targetCollection = this.URItoCollection(getDocumentsRequest.getCollectionName()); 
        GetDocumentResponseWrapper[] responseWrapper = new GetDocumentResponseWrapper[size];
        for(int i=0;i<size;i++) {            
            String resourceName = getDocumentsRequest.getGetDocumentRequestWrapper(i).getDocumentName();
            responseWrapper[i] = new GetDocumentResponseWrapper();
            responseWrapper[i].setDocumentName(resourceName);
            try {        	
        	BaseDAIXResource resource;        	
        	if (targetCollection.startsWith(GCUBEProfileResource.ROOT_COLLECTION_NAME)) {
        	    resource = new GCUBEProfileResource();
        	    //extract the type from the collection name: /Profiles/Type
        	    ((GCUBEProfileResource) resource).setResourceType(targetCollection.split("/")[2]);
        	}
        	else if (targetCollection.startsWith(GCUBEInstanceStateResource.ROOT_COLLECTION_NAME)) 
        	    resource = new GCUBEInstanceStateResource();        	
        	else 
        	    resource = new BaseDAIXResource();
        	            	
        	resource.setCollectionName(targetCollection);
        	resource.setResourceName(resourceName);        	        	
        	GCUBEXMLResource xmlResource = new GCUBEXMLResource(resource);
        	logger.trace("Retrieving resource " + resourceName + " from collection " + xmlResource.getCollectionName()); 
        	State.getDataManager().retrieveResourceContent(xmlResource);
                XMLWrapperType wrapper = new XMLWrapperType();
                MessageElement msgElem = new MessageElement(xmlResource.getContent().getDocumentElement());
                wrapper.set_any(new MessageElement[] {msgElem} );
                responseWrapper[i].setData(wrapper);                
                responseWrapper[i].setResponse(GetDocumentResponseWrapperResponse.value1);
        	
            } catch ( Exception e ) {
                logger.error("Unable to get the resource: " + e);                               
                responseWrapper[i].setResponse(GetDocumentResponseWrapperResponse.value2);
            }
            
        }
        GetDocumentsResponse response = new GetDocumentsResponse(); 
        response.setGetDocumentResponseWrapper(responseWrapper);
        return response; 	
    }
    

    
    /**
     * Implementation of CollectionAccess::AddDocuments
     * 
     * @param addDocumentsRequest
     * @return the documents response
     * @throws RemoteException
     * @throws ServiceBusyFaultType
     * @throws InvalidResourceNameFaultType
     * @throws InvalidCollectionNameFaultType
     */
    public AddDocumentsResponse addDocuments(AddDocumentsRequest addDocumentsRequest) 
    	throws RemoteException, ServiceBusyFaultType, InvalidResourceNameFaultType, InvalidCollectionNameFaultType {
        
        AddDocumentRequestWrapper[] docs = addDocumentsRequest.getAddDocumentRequestWrapper();
        AddDocumentResponseWrapper[] responseWrappers = new AddDocumentResponseWrapper[docs.length];
        String targetCollection = this.URItoCollection(addDocumentsRequest.getCollectionName());
        DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
	fact.setNamespaceAware(true);
	DocumentBuilder bd;
	try {
	    bd = fact.newDocumentBuilder();
	} catch (ParserConfigurationException e3) {
	  throw new RemoteException("Unable to create a Document Factory for the incoming documents");
	}
        logger.debug("Adding " +docs.length + " documents to collection " + targetCollection);              

        //response wrapper values
        //1 - <xsd:enumeration value="Success"/> 
        //2 - <xsd:enumeration value="DocumentNotAdded-DocumentDoesNotValidate"/> 
        //3 - <xsd:enumeration value="DocumentNotAdded-SchemaDoesNotExist"/> 
        //4 - <xsd:enumeration value="DocumentNotAdded-NotAuthorized"/>      
        //5 - <xsd:enumeration value="DocumentOfSameNameOverwritten"/>
        for(int i=0;i<docs.length;i++) {
            String resourceName = docs[i].getDocumentName();            
            responseWrappers[i] = new AddDocumentResponseWrapper();
            responseWrappers[i].setDocumentName(resourceName);
            logger.info("Storing resource " + resourceName);
            //String output = "AddDocument-" + UniqueName.getInstance().getName();
            MessageElement elem = docs[i].getData().get_any()[0];            
            Document doc = null;
            BaseDAIXResource resource;
            try { 
        	Object o =  elem.getAsDocument();        	    
                if (! (o instanceof Document))                      
                    throw new Exception("Unable to add resource " + resourceName + " because of a problem deserializing the document");                
        	doc = (Document) o;        	
            } catch ( Exception e ) {
        	logger.error("Unable to read the resource", e);
        	responseWrappers[i].setResponse(AddDocumentResponseWrapperResponse.value2);
        	continue;
            }
            MetadataReader metadataReader = null;
	    try {
		//check if there exists a metadata record for this resource
		metadataReader = this.getMetadata(docs[i].getData().get_any(), resourceName);
		if (metadataReader.getType().compareToIgnoreCase(GCUBEProfileResource.TYPE) == 0) {
        		resource = new GCUBEProfileResource();
                } else if (metadataReader.getType().compareToIgnoreCase(GCUBEInstanceStateResource.TYPE) == 0) {
            		resource = new GCUBEInstanceStateResource();
                } else {
                    resource = new BaseDAIXResource();
                    resource.setCollectionName(targetCollection);
                }
	    } catch (Exception e2) {
		resource = new BaseDAIXResource();
        	resource.setCollectionName(targetCollection);
        	metadataReader = null;
	    }
            
            try {
        	resource.setResourceName(resourceName);
        	if (doc.getDocumentElement().getLocalName().equals("ISPublisher")) {
        	    	//this removes the root element introduced by the ISPublisher in ordet to put an any obj 
        	    	//in the SOAP MessageElement
			Document newDoc = bd.newDocument();
			Node node = doc.getDocumentElement().removeChild(doc.getDocumentElement().getFirstChild());
			newDoc.appendChild(newDoc.importNode(node,true));
	        	resource.setContent(newDoc);
		} else
        	    resource.setContent(doc);
	    } catch (MalformedResourceException e1) {
		logger.error("Invalid resource ", e1);
		responseWrappers[i].setResponse(AddDocumentResponseWrapperResponse.value2);
		continue;
	    } 
	    	                 
	    try {
		//logger.trace("resource to wrap " + resource.toString());
		GCUBEXMLResource xmlResource = wrap(resource, metadataReader);				
		//logger.trace("wrapped resource " + xmlResource.toString());
		boolean exist = false;
		if ( State.getDataManager().resourceExists(xmlResource) ) {
		    exist = true;
		}
		//store/update the new resource		
		State.getDataManager().storeResource(xmlResource);
		logger.info("Resource " + resource.getResourceName() + " successfully stored");
		if (exist) 
		    responseWrappers[i].setResponse(AddDocumentResponseWrapperResponse.value5);
		else
		    responseWrappers[i].setResponse(AddDocumentResponseWrapperResponse.value1);
	    } catch (MalformedXMLResourceException e) {
		logger.error("Invalid resource ", e);
		responseWrappers[i].setResponse(AddDocumentResponseWrapperResponse.value2);
		continue;
	    } catch (XMLStorageNotAvailableException e) {
		logger.error("Storage not available ", e);
		responseWrappers[i].setResponse(AddDocumentResponseWrapperResponse.value4);
		continue;
	    }            	                           
            
        }
        
        AddDocumentsResponse r = new AddDocumentsResponse();
        r.setAddDocumentResponseWrapper(responseWrappers);
        return r; 
    }
    
    /**
     * Reads the metadata reader from the given soap elements, if any
     * @param elements the elements received as part of the SOAP message
     * @param resourceName the related resource name
     * @return the metadata reader
     * @throws Exception if the reader does not exist or is invalid
     */
    private MetadataReader getMetadata(MessageElement[] elements, String resourceName) throws Exception {	
	if (elements.length > 1){
            MessageElement elem2 = elements[1];
            try {
            	Object o =  elem2.getAsDocument();
            	if (! (o instanceof Document))                      
                        throw new Exception("Unable to add resource " + resourceName + " because of a problem deserializing the metadata");
            	return new MetadataReader((Document) o);
            } catch ( Exception e ) {
        	logger.error("Unable to read resource's metadata", e);
        	throw new Exception("Unable to read resource "+ resourceName + " metadata", e);
            }            
	} else 
	    throw new Exception("Unable to find any resource's metadata record for " + resourceName);	
    }
    
    /**
     * Wraps a {@link BaseDAIXResource} into a {@link GCUBEXMLResource}
     * @param resource the resource to wrap
     * @param metadataReader the related reader, if any
     * @return the wrapped resource
     * @throws MalformedXMLResourceException
     */
    private GCUBEXMLResource wrap(BaseDAIXResource resource, MetadataReader metadataReader) throws MalformedXMLResourceException {
	 GCUBEXMLResource xmlResource = new GCUBEXMLResource(resource);		
	 if (metadataReader != null) {
        	 xmlResource.setSource(metadataReader.getSource());
        	 xmlResource.setTerminationTime(metadataReader.getTerminationTime());
        	 xmlResource.setGroupKey(metadataReader.getGroupKey());
        	 xmlResource.setEntryKey(metadataReader.getEntryKey());
        	 xmlResource.setSourceKey(metadataReader.getKey());
        	 xmlResource.setNamespace(metadataReader.getNamespace());
        	 xmlResource.setPublicationMode(metadataReader.getPublicationMode());
	 }
	return xmlResource; 
    }
    
    /**
     * Implementation of CollectionAccess::RemoveDocuments
     * 
     * @param removeDocumentsRequest
     * @return
     * @throws RemoteException
     * @throws ServiceBusyFaultType
     * @throws InvalidResourceNameFaultType
     * @throws InvalidCollectionNameFaultType
     */
    public RemoveDocumentsResponse removeDocuments(RemoveDocumentsRequest removeDocumentsRequest) 
    	throws RemoteException, ServiceBusyFaultType, InvalidResourceNameFaultType, InvalidCollectionNameFaultType {
	
	
	RemoveDocumentsResponse response = new RemoveDocumentsResponse();
        RemoveDocumentRequestWrapper[] docs = removeDocumentsRequest.getRemoveDocumentRequestWrapper();
        RemoveDocumentResponseWrapper[] responseWrappers = new RemoveDocumentResponseWrapper[docs.length];
	String targetCollection = this.URItoCollection(removeDocumentsRequest.getCollectionName()); 
        //response wrapper values
        //v1 = Success
        //v2 = DocumentNotRemoved-NotAuthorized
        //v3 = documentDoesNotExist
        for(int i=0;i<docs.length;i++) {
            String resourceName = docs[i].getDocumentName();    
            logger.info("Removing resource " + resourceName + " from " + targetCollection);
            responseWrappers[i] = new RemoveDocumentResponseWrapper();
            responseWrappers[i].setDocumentName(resourceName);
            try {
                BaseDAIXResource resource = new BaseDAIXResource(resourceName);
                resource.setCollectionName(targetCollection);
                GCUBEXMLResource xmlResource = new GCUBEXMLResource(resource);                
                if ( ! State.getDataManager().resourceExists(xmlResource) ) {
                    responseWrappers[i].setResponse(RemoveDocumentResponseWrapperResponse.value3);
                    continue;
                }                
                State.getDataManager().deleteResource(xmlResource);
                logger.info("Resource "+ xmlResource.getResourceName() +" successfully removed");
                responseWrappers[i].setResponse(RemoveDocumentResponseWrapperResponse.value1);
            } catch ( Exception e ) {
                logger.error("Unable to remove the resource: " + e);
                responseWrappers[i].setResponse(RemoveDocumentResponseWrapperResponse.value2);
                continue;
            }
            
        }
	response.setRemoveDocumentResponseWrapper(responseWrappers);
        return response;	
    }
       
    /**
     * Implementation of CollectionAccess::createSubCollection
     * 
     * @param createSubcollectionRequest 
     * 		DataResourceAbstractName the abstract name of the data resource to which the message is directed.
		CollectionName an OPTIONAL parameter that contains the URI of the collection to which the subcollection will be added. If no collection name is provided the top level collection is assumed.
		SubcollectionName the URI of the new subcollection.
     * @return the response always indicates success. In error conditions a fault is returned
     * @throws RemoteException a generic exception not included in any fault
     * @throws CollectionAlreadyExistsFaultType The subcollection name specified already exists in the collection specified.
     * @throws ServiceBusyFaultType the service is already processing a request and ConcurrentAccess is false.
     * @throws InvalidResourceNameFaultType the supplied resource name is not known to the service.
     * @throws InvalidCollectionNameFaultType the supplied collection name is not known to the XML resource.
     * @throws NotAuthorizedFaultType the consumer is not authorized to perform this operation at this time.
     * @throws DataResourceUnavailableFaultType the specified data resource is unavailable.
     * 
     */
    public CreateSubcollectionResponse createSubcollection(CreateSubcollectionRequest createSubcollectionRequest) 
    	throws RemoteException, CollectionAlreadyExistsFaultType, ServiceBusyFaultType, InvalidResourceNameFaultType, 
	    	InvalidCollectionNameFaultType, NotAuthorizedFaultType, DataResourceUnavailableFaultType {
	
	String collectionPath = null;
        if ( createSubcollectionRequest.getCollectionName() != null ) {
            collectionPath = this.URItoCollection(createSubcollectionRequest.getCollectionName());
            if (collectionPath != null) {
        	if (! State.getDataManager().collectionExists(collectionPath)) {
        	    logger.warn("Invalid collection name");
                    throw new InvalidCollectionNameFaultType();
        	}
            } 
        } else {
    		logger.warn("Collection "+ collectionPath + " does not exist, assuming ROOT collection");        	
        }
        
        if (createSubcollectionRequest.getSubcollectionName() == null) {
            logger.warn("Invalid subcollection name");
            throw new InvalidCollectionNameFaultType();
        }
        
        String subCollectionName = this.URItoCollection(createSubcollectionRequest.getSubcollectionName());
        if (collectionPath != null)
            subCollectionName = collectionPath +"/" + subCollectionName;
        if (State.getDataManager().collectionExists(subCollectionName)) {
            logger.warn("Collection "+ subCollectionName + " already exists");
            throw new CollectionAlreadyExistsFaultType();
        } else {
            try {
		State.getDataManager().createCollection(subCollectionName);
	    } catch (XMLDBException e) {
		logger.error("Unable to create subcollection", e);
		throw new  ServiceBusyFaultType();
	    } catch (XMLStorageNotAvailableException e) {
		logger.error("Unable to create subcollection", e);
		throw new DataResourceUnavailableFaultType();
	    }
        }
        
        return new CreateSubcollectionResponse();
    }
    
    
    
    /**
     * Implementation of CollectionAccess::removeSubcollection
     * 
     * @param removeSubcollectionRequest
     * @return the response always indicates success. In error conditions a fault is returned.
     * @throws RemoteException a generic exception not included in any fault
     * @throws ServiceBusyFaultType the service is already processing a request and ConcurrentAccess is false.
     * @throws InvalidResourceNameFaultType the supplied resource name is not known to the service.
     * @throws InvalidCollectionNameFaultType the supplied collection name is not known to the XML resource.
     * @throws NotAuthorizedFaultType the consumer is not authorized to perform this operation at this time.
     * @throws DataResourceUnavailableFaultType the specified data resource is unavailable.
     */
    public RemoveSubcollectionResponse removeSubcollection(RemoveSubcollectionRequest removeSubcollectionRequest) 
    	throws RemoteException, ServiceBusyFaultType, InvalidResourceNameFaultType, InvalidCollectionNameFaultType, NotAuthorizedFaultType, DataResourceUnavailableFaultType {
		
	if (removeSubcollectionRequest.getSubcollectionName() == null) 
	    throw new InvalidCollectionNameFaultType();
	
	String collectionPath = null;
        if ( removeSubcollectionRequest.getCollectionName() != null ) {
            collectionPath = this.URItoCollection(removeSubcollectionRequest.getCollectionName());
            if (collectionPath != null) {
        	if (! State.getDataManager().collectionExists(collectionPath)) {
        	    logger.warn("Invalid collection name");
                    throw new InvalidCollectionNameFaultType();
        	}
            } 
        } else {
    	   logger.warn("Collection "+ collectionPath + " does not exist, assuming ROOT collection");        	
        }
	
        String subCollectionName = this.URItoCollection(removeSubcollectionRequest.getSubcollectionName());
        if (collectionPath != null)
            subCollectionName = collectionPath +"/" + subCollectionName;
        if (! State.getDataManager().collectionExists(subCollectionName)) {
            logger.warn("Collection "+ subCollectionName + " does not exist");
            throw new InvalidCollectionNameFaultType();
        } else {
            try {
		State.getDataManager().deleteCollection(subCollectionName);
		logger.info("Collection " + subCollectionName + " successfully removed");
	    } catch (XMLStorageNotAvailableException e) {
		logger.error("Unable to create subcollection", e);
		throw new DataResourceUnavailableFaultType();
	    }
        }
        
        
	return new RemoveSubcollectionResponse();   
    }
    
    
    /**
     * Implementation of CollectionAccess::addSchema
     * 
     * @param addSchemaRequest
     * @return
     * @throws SchemaInvalidFaultType
     * @throws DataResourceUnavailableFaultType
     * @throws ServiceBusyFaultType
     * @throws SchemaAdditionMakesDocumentsInvalidFaultType
     * @throws InvalidResourceNameFaultType
     * @throws InvalidCollectionNameFaultType
     * @throws SchemaAlreadyExistsFaultType
     * @throws NotAuthorizedFaultType
     */
    public AddSchemaResponse addSchema(AddSchemaRequest addSchemaRequest) 
    	throws SchemaInvalidFaultType, DataResourceUnavailableFaultType, 
    	ServiceBusyFaultType, SchemaAdditionMakesDocumentsInvalidFaultType, InvalidResourceNameFaultType,
    	InvalidCollectionNameFaultType, SchemaAlreadyExistsFaultType, NotAuthorizedFaultType {
	throw new NotAuthorizedFaultType();
    }
    
    /**
     * Implementation of CollectionAccess::removeSchema
     * 
     * @param getSchemaRequest
     * @return
     * @throws DataResourceUnavailableFaultType
     * @throws ServiceBusyFaultType
     * @throws SchemaDoesNotExistFaultType
     * @throws InvalidResourceNameFaultType
     * @throws InvalidCollectionNameFaultType
     * @throws NotAuthorizedFaultType
     */
    public GetSchemaResponse getSchema (GetSchemaRequest getSchemaRequest) 
    	throws DataResourceUnavailableFaultType, ServiceBusyFaultType, SchemaDoesNotExistFaultType,
    	InvalidResourceNameFaultType, InvalidCollectionNameFaultType, NotAuthorizedFaultType{
	throw new NotAuthorizedFaultType();
    }
    
    /**
     * Implementation of CollectionAccess::getSchema
     * @param removeSchemaRequest
     * @return
     * @throws DataResourceUnavailableFaultType
     * @throws ServiceBusyFaultType
     * @throws SchemaRemovalMakesDocumentsInvalidFaultType
     * @throws SchemaDoesNotExistFaultType
     * @throws InvalidResourceNameFaultType
     * @throws SchemaRemovalMakesSchemaInvalidFaultType
     * @throws InvalidCollectionNameFaultType
     * @throws NotAuthorizedFaultType
     */
    public RemoveSchemaResponse removeSchema (RemoveSchemaRequest removeSchemaRequest) 
    	throws DataResourceUnavailableFaultType, ServiceBusyFaultType, 
    	SchemaRemovalMakesDocumentsInvalidFaultType, SchemaDoesNotExistFaultType,
    	InvalidResourceNameFaultType, SchemaRemovalMakesSchemaInvalidFaultType, 
    	InvalidCollectionNameFaultType, NotAuthorizedFaultType {	
		throw new NotAuthorizedFaultType();
    }
    
    /**
     * Implementation of CollectionAccess::getCollectionPropertyDocument 
     * @param request
     * @return
     * @throws DataResourceUnavailableFaultType
     * @throws ServiceBusyFaultType
     * @throws InvalidResourceNameFaultType
     * @throws NotAuthorizedFaultType
     */
    public XMLCollectionPropertyDocumentType getCollectionPropertyDocument(GetDataResourcePropertyDocumentRequest request) 
    	throws DataResourceUnavailableFaultType, ServiceBusyFaultType, InvalidResourceNameFaultType, NotAuthorizedFaultType {
	throw new NotAuthorizedFaultType();
    }

    private String URItoCollection(URI collection) {
	if ((collection.getPath() == null) || (collection.getPath().length() == 0) )
	    return collection.getHost();
	else 
	    return collection.getHost() + "/" + collection.getPath();
    }

    /** {@inheritDoc} */
    @Override
    protected GCUBEServiceContext getServiceContext() {
	return ICServiceContext.getContext();
    }
}
