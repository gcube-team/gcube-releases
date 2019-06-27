package org.gcube.informationsystem.collector.stubs.testsuite.wsdaix;


import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import org.apache.axis.message.MessageElement;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBEClientLog;
import org.gcube.informationsystem.collector.stubs.metadata.MetadataWriter;
import org.gcube.informationsystem.collector.stubs.metadata.MetadataRecord.TYPE;
import org.gcube.informationsystem.collector.stubs.wsdai.DataResourceUnavailableFaultType;
import org.gcube.informationsystem.collector.stubs.wsdai.InvalidResourceNameFaultType;
import org.gcube.informationsystem.collector.stubs.wsdai.NotAuthorizedFaultType;
import org.gcube.informationsystem.collector.stubs.wsdai.ServiceBusyFaultType;
import org.gcube.informationsystem.collector.stubs.wsdaix.AddDocumentRequestWrapper;
import org.gcube.informationsystem.collector.stubs.wsdaix.AddDocumentsRequest;
import org.gcube.informationsystem.collector.stubs.wsdaix.AddDocumentsResponse;
import org.gcube.informationsystem.collector.stubs.wsdaix.InvalidCollectionNameFaultType;
import org.gcube.informationsystem.collector.stubs.wsdaix.XMLCollectionAccessPT;
import org.gcube.informationsystem.collector.stubs.wsdaix.XMLWrapperType;
import org.gcube.informationsystem.collector.stubs.wsdaix.service.WsdaixServiceAddressingLocator;
import org.w3c.dom.Document;

/**
 * 
 * Tester for XMLCollectionAccess::addDocuments
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class AddDocumentsTester {

    
    private static GCUBEClientLog logger = new GCUBEClientLog(AddDocumentsTester.class);

    
    /**
     * @param args
     *  0 - host
     *  1 - port
     *  2 - scope  
     *  3 - document name
     *  4 - filename
     *  5 - type (Properties/Profile/Daix)
     *  6 - collection name 
     */
    public static void main(String[] args) {
	
	final String portTypeURI = "http://" + args[0] + ":" + args[1] + "/wsrf/services/gcube/informationsystem/collector/wsdaix/XMLCollectionAccess";
	
	try {
            AddDocumentsResponse r = addDocuments(portTypeURI,GCUBEScope.getScope(args[2]),
        	    new org.apache.axis.types.URI ("gcube://testResourceName"), 
        	    new org.apache.axis.types.URI("gcube://InstanceState"), args[5],
        	    new String[] { args[3] }, 
        	    new Document[] {TestDocuments.loadDocument(args[4])} );
            logger.info("Number of response wrappers "+ r.getAddDocumentResponseWrapper().length );
            String response = null;
            for(int i=0;i<r.getAddDocumentResponseWrapper().length;i++) {
                if ( r.getAddDocumentResponseWrapper()[i].getDocumentName().equals(args[3]) ) {
                    response = r.getAddDocumentResponseWrapper()[i].getResponse().toString();
                    logger.info("Add response " + response);
                }                
            }
               } catch (Exception e ) {
            logger.error("",e);    
        }

    }
    
    /**
     * Execute the AddDocuments operation
     * 
     * @param serviceURL the URL of the data service
     * @param scope the target scope
     * @param resourceName the abstract name of the data resource
     * @param collectionURI The URI of the collection to which the documents should be added
     * @param documentNames An array of document names to be added
     * @param documents The corresponding content of each document
     */
    public static AddDocumentsResponse addDocuments(String serviceURL, GCUBEScope scope,  
	    org.apache.axis.types.URI resourceName, 
	    org.apache.axis.types.URI collectionURI, String type, String[] documentNames, Document[] documents) 
    	throws DataResourceUnavailableFaultType, MalformedURLException, RemoteException, 
    		ServiceBusyFaultType, InvalidResourceNameFaultType, InvalidCollectionNameFaultType, NotAuthorizedFaultType {
        
	AddDocumentsRequest request = new AddDocumentsRequest();
        request.setDataResourceAbstractName(resourceName); 
        AddDocumentRequestWrapper[] wrappers = new AddDocumentRequestWrapper[documents.length];
        for(int i=0;i<wrappers.length;i++) {
            wrappers[i] = new AddDocumentRequestWrapper();
            wrappers[i].setDocumentName(documentNames[i]); //document name
            XMLWrapperType wrapper = new XMLWrapperType();
            MessageElement msgElement = new MessageElement(documents[i].getDocumentElement());           
            MessageElement msgElement2;
            MetadataWriter writer = new MetadataWriter(TYPE.INSTANCESTATE, "http://source",
		    600, "MyGroupKey", "MyKey", "MyEntryKey","","push");
	    try {
		msgElement2 = new MessageElement(writer.getRecord().getAsDocument().getDocumentElement());
	    } catch (Exception e) {
		logger.error("Unable to add document " + documentNames[i], e);
		continue;
	    }
	    logger.info("Dump before set_any \n"+ msgElement); 
            wrapper.set_any(new MessageElement[] {msgElement, msgElement2} );           
            wrappers[i].setData(wrapper);
        }
        request.setAddDocumentRequestWrapper(wrappers);
        request.setCollectionName(collectionURI);
        XMLCollectionAccessPT stubs = null;
	try {
	    stubs = new WsdaixServiceAddressingLocator().getXMLCollectionAccessPTPort(new URL(serviceURL));
	    stubs = GCUBERemotePortTypeContext.getProxy(stubs, scope);
	} catch (Exception e) {
	    logger.error("Failed to add documentes", e);
	}
                
        return stubs.addDocuments(request);
    }
    

}
