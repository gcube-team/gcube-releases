package org.gcube.informationsystem.collector.stubs.testsuite.wsdaix;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.URI;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBEClientLog;
import org.gcube.informationsystem.collector.stubs.wsdai.DataResourceUnavailableFaultType;
import org.gcube.informationsystem.collector.stubs.wsdai.InvalidResourceNameFaultType;
import org.gcube.informationsystem.collector.stubs.wsdai.NotAuthorizedFaultType;
import org.gcube.informationsystem.collector.stubs.wsdai.ServiceBusyFaultType;
import org.gcube.informationsystem.collector.stubs.wsdaix.GetDocumentRequestWrapper;
import org.gcube.informationsystem.collector.stubs.wsdaix.GetDocumentResponseWrapper;
import org.gcube.informationsystem.collector.stubs.wsdaix.GetDocumentsRequest;
import org.gcube.informationsystem.collector.stubs.wsdaix.GetDocumentsResponse;
import org.gcube.informationsystem.collector.stubs.wsdaix.InvalidCollectionNameFaultType;
import org.gcube.informationsystem.collector.stubs.wsdaix.XMLCollectionAccessPT;
import org.gcube.informationsystem.collector.stubs.wsdaix.XMLWrapperType;
import org.gcube.informationsystem.collector.stubs.wsdaix.service.WsdaixServiceAddressingLocator;
import org.w3c.dom.Document;

/**
 * 
 * Tester for XMLCollectionAccess::getDocuments
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class GetDocumentsTester {

    private static GCUBEClientLog logger = new GCUBEClientLog(GetDocumentsTester.class);

    
    /**
     * @param args
     *  0 - host
     *  1 - port
     *  2 - scope
     *  3 - collection
     *  4 - document name
     
     */
    public static void main(String[] args) {

	final String portTypeURI = "http://" + args[0] + ":" + args[1] + "/wsrf/services/gcube/informationsystem/collector/wsdaix/XMLCollectionAccess";
	try {
	    GetDocumentsResponse response = getDocuments(portTypeURI, GCUBEScope.getScope(args[2]), 
		    new URI("gcube://" + args[4]),new URI(args[3]), new String[] {args[4]});
	    GetDocumentResponseWrapper[] wrappers = response.getGetDocumentResponseWrapper();
	    for (GetDocumentResponseWrapper wrapper : wrappers) {
		logger.info("Document name " + wrapper.getDocumentName());
		logger.info("Document response " + wrapper.getResponse().toString());	
		XMLWrapperType xmlwrapper = wrapper.getData();
		MessageElement elem = xmlwrapper.get_any()[0];
		Object o =  elem.getAsDocument();        	    
                if (! (o instanceof Document))                      
                    throw new Exception("Unable to read the resource: a problem when deserializing the document occurred");                
        	Document doc = (Document) o;
        	TransformerFactory transFactory = TransformerFactory.newInstance();
    	    	Transformer transformer = transFactory.newTransformer();
    	    	StringWriter buffer = new StringWriter();
    	    	transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    	    	transformer.transform(new DOMSource(doc), new StreamResult(buffer));
    	    	logger.info("Document returned: ");
    	    	logger.info(buffer.toString());
	    }
	} catch (Exception e ) {
	            logger.error(e.toString());    
	        }
    }

    /**
     * Executes the GetDocuments operation.
     * @param serviceURL The URL of the data service
     * @param resourceName The abstract name of the data resource
     * @param documentNames An array of document names to be retrieved
     */
    public static GetDocumentsResponse getDocuments(String serviceURL, GCUBEScope scope, URI resourceName, URI collectionURI, String[] documentNames) 
    	throws DataResourceUnavailableFaultType, MalformedURLException, RemoteException, ServiceBusyFaultType, 
    		InvalidResourceNameFaultType, InvalidCollectionNameFaultType, NotAuthorizedFaultType {
	
        GetDocumentsRequest request = new GetDocumentsRequest();
        request.setDataResourceAbstractName(resourceName);
        GetDocumentRequestWrapper[] wrappers = new GetDocumentRequestWrapper[documentNames.length];
        for(int i=0;i<wrappers.length;i++) {
            wrappers[i] = new GetDocumentRequestWrapper();
            wrappers[i].setDocumentName(documentNames[i]);
        }
        request.setGetDocumentRequestWrapper(wrappers);
        request.setCollectionName(collectionURI);
        XMLCollectionAccessPT stubs = null;
	try {
	    stubs = new WsdaixServiceAddressingLocator().getXMLCollectionAccessPTPort(new URL(serviceURL));
	    stubs = GCUBERemotePortTypeContext.getProxy(stubs, scope);
	} catch (Exception e) {
	    logger.error("Failed to get documentes", e);
	}  
	return stubs.getDocuments(request);
    }

}
