package org.gcube.informationsystem.collector.stubs.testsuite.wsdaix;

import java.net.URL;

import org.apache.axis.types.URI;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBEClientLog;
import org.gcube.informationsystem.collector.stubs.wsdaix.CreateSubcollectionRequest;
import org.gcube.informationsystem.collector.stubs.wsdaix.XMLCollectionAccessPT;
import org.gcube.informationsystem.collector.stubs.wsdaix.service.WsdaixServiceAddressingLocator;

public class CreateSubcollectionTester {

    private static GCUBEClientLog logger = new GCUBEClientLog(CreateSubcollectionTester.class);

    /**
     * @param args
     *            0 - host 
     *            1 - port 
     *            2 - scope 
     *            3 - resource name 
     *            4 - collection name 
     *            5 - subcollection name
     */
    public static void main(String[] args) {

	final String portTypeURI = "http://" + args[0] + ":" + args[1] + "/wsrf/services/gcube/informationsystem/collector/wsdaix/XMLCollectionAccess";
	try {
	    CreateSubcollectionRequest request = new CreateSubcollectionRequest();
	    request.setDataResourceAbstractName(new URI(args[3]));
	    request.setCollectionName(new URI(args[4]));
	    request.setSubcollectionName(new URI(args[5]));
	    XMLCollectionAccessPT stubs = new WsdaixServiceAddressingLocator().getXMLCollectionAccessPTPort(new URL(portTypeURI));
	    stubs = GCUBERemotePortTypeContext.getProxy(stubs, GCUBEScope.getScope(args[2]));
	    stubs.createSubcollection(request);
	    logger.info("Subcollection successfully created");
	} catch (Exception e) {
	    logger.error("Failed to create subcollection " + args[5], e);
	}

    }

}
