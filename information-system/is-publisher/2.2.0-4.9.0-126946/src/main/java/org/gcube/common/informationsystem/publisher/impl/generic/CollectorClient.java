package org.gcube.common.informationsystem.publisher.impl.generic;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.informationsystem.publisher.ISGenericPublisher;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.publisher.impl.context.ISPublisherContext;
import org.gcube.informationsystem.collector.stubs.wsdai.DataResourceUnavailableFaultType;
import org.gcube.informationsystem.collector.stubs.wsdai.InvalidResourceNameFaultType;
import org.gcube.informationsystem.collector.stubs.wsdai.NotAuthorizedFaultType;
import org.gcube.informationsystem.collector.stubs.wsdai.ServiceBusyFaultType;
import org.gcube.informationsystem.collector.stubs.wsdaix.AddDocumentRequestWrapper;
import org.gcube.informationsystem.collector.stubs.wsdaix.AddDocumentsRequest;
import org.gcube.informationsystem.collector.stubs.wsdaix.AddDocumentsResponse;
import org.gcube.informationsystem.collector.stubs.wsdaix.InvalidCollectionNameFaultType;
import org.gcube.informationsystem.collector.stubs.wsdaix.RemoveDocumentRequestWrapper;
import org.gcube.informationsystem.collector.stubs.wsdaix.RemoveDocumentResponseWrapper;
import org.gcube.informationsystem.collector.stubs.wsdaix.RemoveDocumentsRequest;
import org.gcube.informationsystem.collector.stubs.wsdaix.RemoveDocumentsResponse;
import org.gcube.informationsystem.collector.stubs.wsdaix.XMLCollectionAccessPT;
import org.gcube.informationsystem.collector.stubs.wsdaix.XMLWrapperType;
import org.gcube.informationsystem.collector.stubs.wsdaix.service.WsdaixServiceAddressingLocator;
import org.globus.wsrf.Constants;
import org.w3c.dom.Document;

class CollectorClient {
    
    protected static final GCUBELog logger = new GCUBELog(ISGenericPublisher.class);

    protected static final int DEFAULT_CALL_TIMEOUT = 120000;

    /**
     * Executes the RemoveDocuments operation on the IC instance
     * @param serviceURL The URL of the data service
     * @param id The abstract name of the data resource
     * @param collectionURI The URI of the collection from which documents should be removed
     * @param documentNames An array of document names that should be removed
     */
    protected static void removeDocuments(EndpointReferenceType sink, GCUBEScope scope, 
	    URI resourceName, URI collectionURI, String[] documentNames) 
    	throws DataResourceUnavailableFaultType, MalformedURLException, RemoteException, ServiceBusyFaultType, 
    		InvalidResourceNameFaultType, InvalidCollectionNameFaultType, NotAuthorizedFaultType {
	logger.trace("Removing document from " + sink.getAddress().toString());
        RemoveDocumentsRequest request = new RemoveDocumentsRequest();
        request.setDataResourceAbstractName(resourceName);
        RemoveDocumentRequestWrapper[] wrappers = new RemoveDocumentRequestWrapper[documentNames.length];
        for (int i = 0; i < documentNames.length; i++) {
            wrappers[i] = new RemoveDocumentRequestWrapper();
            wrappers[i].setDocumentName(documentNames[i]); 
        }
        request.setRemoveDocumentRequestWrapper(wrappers);
        request.setCollectionName(collectionURI);
        XMLCollectionAccessPT stubs = null;
	try {
	    stubs = new WsdaixServiceAddressingLocator().getXMLCollectionAccessPTPort(sink);
	    stubs = GCUBERemotePortTypeContext.getProxy(stubs, scope, getTimeout());
	} catch (Exception e) {
	    logger.error("Failed to remove the resource", e);
	}
	RemoveDocumentsResponse response = stubs.removeDocuments(request);
	RemoveDocumentResponseWrapper[] rwrappers = response.getRemoveDocumentResponseWrapper();
	for (RemoveDocumentResponseWrapper wrapper : rwrappers) {
		logger.trace("Document name " + wrapper.getDocumentName());
		logger.trace("Returned response from remove operation " + wrapper.getResponse().toString());
		//XMLWrapperType detail = wrapper.getDetail();
	}
        return;
    }
    
    
    /**
     * Executes the addDocument operation on the IC instance
     * @param sink the endpoint of the IC to contact
     * @param scope the scope of the operation
     * @param resourceName the name of the resource to add
     * @param collectionURI the URI where to insert the resource 
     * @param documentNamet the resource's document name
     * @param document the payload of the resource
     * @param metadata the optional metadata to attach to the resource
     * @return the response from the IC
     * @throws Exception if the operation fails
     */
    protected static void addDocuments(EndpointReferenceType sink, GCUBEScope scope, URI resourceName, URI collectionURI,
	    String[] documentNames, Document[] documents, Document[] metadata) throws Exception {
	logger.trace("Sending resource to " + sink.getAddress().toString());
	AddDocumentsRequest request = new AddDocumentsRequest();
	request.setDataResourceAbstractName(resourceName);
	AddDocumentRequestWrapper[] wrappers = new AddDocumentRequestWrapper[documentNames.length];
	for (int i=0; i < documentNames.length; i++) {
        	wrappers[i] = new AddDocumentRequestWrapper();
        	wrappers[i].setDocumentName(documentNames[i]); 
        	XMLWrapperType wrapper = new XMLWrapperType();
        	MessageElement msgElement = new MessageElement(Constants.CORE_NS, "ISPublisher", documents[i]);
    	    	msgElement.setType(org.apache.axis.Constants.XSD_ANYTYPE);

        	//MessageElement msgElement = new MessageElement(documents[i].getDocumentElement());
        	if (metadata != null) {
                	MessageElement msgElement2;
                	try {
                	    msgElement2 = new MessageElement(Constants.CORE_NS, "ISPublisher", metadata[i].getDocumentElement());
                	    msgElement2.setType(org.apache.axis.Constants.XSD_ANYTYPE);
                	    //msgElement2 = AnyHelper.toAny(metadata[i].getDocumentElement());
                	    wrapper.set_any(new MessageElement[] { msgElement, msgElement2 });
                	} catch (Exception e) {
                	    logger.error("Unable to add the document metadata for " + documentNames[i], e);
                	    throw e;
                	}
        	} else {
        	    wrapper.set_any(new MessageElement[] { msgElement });
        	}
        	wrappers[i].setData(wrapper);
    	}
	request.setAddDocumentRequestWrapper(wrappers);
	request.setCollectionName(collectionURI);
	
	XMLCollectionAccessPT stubs = null;
	try {
	    stubs = new WsdaixServiceAddressingLocator().getXMLCollectionAccessPTPort(sink);
	    stubs = GCUBERemotePortTypeContext.getProxy(stubs, scope, getTimeout());
	} catch (Exception e) {
	    logger.error("Failed to add document " + documentNames[0], e);
	}
	AddDocumentsResponse response = stubs.addDocuments(request);
	logger.trace("Number of response wrappers " + response.getAddDocumentResponseWrapper().length);
	String sresponse = null;
	for (int i = 0; i < response.getAddDocumentResponseWrapper().length; i++) {
	    sresponse = response.getAddDocumentResponseWrapper()[i].getResponse().toString();
	    logger.trace("Returned response for " + response.getAddDocumentResponseWrapper()[i].getDocumentName() + ": " + sresponse);
	}
	return;
    }
    
    private static int getTimeout() {		
	try {
	    return (Integer)ISPublisherContext.getContext().getProperty(ISPublisherContext.COLLECTOR_CHANNEL_TIMEOUT_PROP_NAME);
	} catch (Exception e) {
	    return DEFAULT_CALL_TIMEOUT;
	}
    }
}
