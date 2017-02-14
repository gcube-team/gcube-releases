package org.gcube.informationsystem.collector.stubs.testsuite.wsdaix;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import org.apache.axis.types.URI;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBEClientLog;
import org.gcube.informationsystem.collector.stubs.wsdai.DataResourceUnavailableFaultType;
import org.gcube.informationsystem.collector.stubs.wsdai.InvalidResourceNameFaultType;
import org.gcube.informationsystem.collector.stubs.wsdai.NotAuthorizedFaultType;
import org.gcube.informationsystem.collector.stubs.wsdai.ServiceBusyFaultType;
import org.gcube.informationsystem.collector.stubs.wsdaix.InvalidCollectionNameFaultType;
import org.gcube.informationsystem.collector.stubs.wsdaix.RemoveDocumentRequestWrapper;
import org.gcube.informationsystem.collector.stubs.wsdaix.RemoveDocumentResponseWrapper;
import org.gcube.informationsystem.collector.stubs.wsdaix.RemoveDocumentsRequest;
import org.gcube.informationsystem.collector.stubs.wsdaix.RemoveDocumentsResponse;
import org.gcube.informationsystem.collector.stubs.wsdaix.XMLCollectionAccessPT;
import org.gcube.informationsystem.collector.stubs.wsdaix.service.WsdaixServiceAddressingLocator;


/**
 * 
 * Tester for XMLCollectionAccess::removeDocuments
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class RemoveDocumentsTester {

    private static GCUBEClientLog logger = new GCUBEClientLog(RemoveDocumentsTester.class);

    /**
     * @param args
     *  0 - host
     *  1 - port
     *  2 - scope
     *  3 - resourcename
     *  4 - collection
     *  5 - document name
     
     */
    public static void main(String[] args) {

	final String portTypeURI = "http://" + args[0] + ":" + args[1] + "/wsrf/services/gcube/informationsystem/collector/wsdaix/XMLCollectionAccess";
	try {
	    RemoveDocumentsResponse response = removeDocuments(portTypeURI, GCUBEScope.getScope(args[2]), new URI(args[3]), new URI(args[4]), new String[]{args[5]});
	    RemoveDocumentResponseWrapper[] wrappers = response.getRemoveDocumentResponseWrapper();
	    for (RemoveDocumentResponseWrapper wrapper : wrappers) {
		logger.info("Document name " + wrapper.getDocumentName());
		logger.info("Document response " + wrapper.getResponse().toString());
		//XMLWrapperType detail = wrapper.getDetail();
	    }	    
	} catch (Exception e ) {
	    logger.error(e.toString());    
	}

    }
    
    /**
     * Executes the RemoveDocuments operation
     * @param serviceURL The URL of the data service
     * @param resourceName The abstract name of the data resource
     * @param collectionURI The URI of the collection from which documents should be removed
     * @param documentNames An array of document names that should be removed
     */
    public static RemoveDocumentsResponse removeDocuments(String serviceURL, GCUBEScope scope, 
	    URI resourceName, URI collectionURI, String[] documentNames) 
    	throws DataResourceUnavailableFaultType, MalformedURLException, RemoteException, ServiceBusyFaultType, 
    		InvalidResourceNameFaultType, InvalidCollectionNameFaultType, NotAuthorizedFaultType {
        RemoveDocumentsRequest request = new RemoveDocumentsRequest();
        request.setDataResourceAbstractName(resourceName);
        RemoveDocumentRequestWrapper[] wrappers = new RemoveDocumentRequestWrapper[documentNames.length];
        for(int i=0;i<wrappers.length;i++) {
            wrappers[i] = new RemoveDocumentRequestWrapper();
            wrappers[i].setDocumentName(documentNames[i]);
        }
        request.setRemoveDocumentRequestWrapper(wrappers);
        request.setCollectionName(collectionURI);
        XMLCollectionAccessPT stubs = null;
	try {
	    stubs = new WsdaixServiceAddressingLocator().getXMLCollectionAccessPTPort(new URL(serviceURL));
	    stubs = GCUBERemotePortTypeContext.getProxy(stubs, scope);
	} catch (Exception e) {
	    logger.error("Failed to get documentes", e);
	}
        return stubs.removeDocuments(request);
    }

}
