package org.gcube.informationsystem.collector.stubs.testsuite.wsdaix;

import java.net.URL;

import org.apache.axis.types.URI;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBEClientLog;
import org.gcube.informationsystem.collector.stubs.wsdaix.RemoveSubcollectionRequest;
import org.gcube.informationsystem.collector.stubs.wsdaix.XMLCollectionAccessPT;
import org.gcube.informationsystem.collector.stubs.wsdaix.service.WsdaixServiceAddressingLocator;

public class RemoveSubcollectionTester {
    
    private static GCUBEClientLog logger = new GCUBEClientLog(RemoveSubcollectionTester.class);
    
    /**
     * @param args
     */
    public static void main(String[] args) {
	final String portTypeURI = "http://" + args[0] + ":" + args[1] + "/wsrf/services/gcube/informationsystem/collector/wsdaix/XMLCollectionAccess";
	try {
	    RemoveSubcollectionRequest request = new RemoveSubcollectionRequest();
	    request.setDataResourceAbstractName(new URI(args[3]));
	    request.setCollectionName(new URI(args[4]));
	    request.setSubcollectionName(new URI(args[5]));
	    XMLCollectionAccessPT stubs = new WsdaixServiceAddressingLocator().getXMLCollectionAccessPTPort(new URL(portTypeURI));
	    stubs = GCUBERemotePortTypeContext.getProxy(stubs, GCUBEScope.getScope(args[2]));
	    stubs.removeSubcollection(request);
	    logger.info("Subcollection successfully removed");
	} catch (Exception e) {
	    logger.error("Failed to create subcollection " + args[5], e);
	}
    }

}
