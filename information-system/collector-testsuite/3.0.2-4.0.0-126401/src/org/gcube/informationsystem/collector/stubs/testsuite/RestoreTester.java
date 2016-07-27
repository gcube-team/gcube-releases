package org.gcube.informationsystem.collector.stubs.testsuite;

import java.net.URL;
import java.rmi.RemoteException;

import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.types.VOID;
import org.gcube.common.core.utils.logging.GCUBEClientLog;
import org.gcube.informationsystem.collector.stubs.BackupNotAvailableFaultType;
import org.gcube.informationsystem.collector.stubs.XMLStorageAccessPortType;
import org.gcube.informationsystem.collector.stubs.XMLStorageNotAvailableFaultType;
import org.gcube.informationsystem.collector.stubs.service.XMLStorageAccessServiceLocator;

/**
 * Tester for <em>Restore</em> operation of the
 * <em>gcube/informationsystem/collector/XMLStorageAccess</em> portType
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class RestoreTester {

    private static GCUBEClientLog logger = new GCUBEClientLog(RestoreTester.class);
    
    /**
     * @param args 
     *  <ol>
     *   <li> IC host
     *   <li> IC port
     *   <li> Caller Scope
     *  </ol>
     */
    public static void main(String[] args) {
	
	if (args.length != 3) {
	    logger.fatal("Usage: RestoreTester <host> <port> <Scope>");
	    return;
	}
	final String portTypeURI = "http://" + args[0] + ":" + args[1] + "/wsrf/services/gcube/informationsystem/collector/XMLStorageAccess";
	
	
	
	XMLStorageAccessPortType port = null;
	try {
	    port = new XMLStorageAccessServiceLocator().getXMLStorageAccessPortTypePort(new URL(portTypeURI));
	    port = GCUBERemotePortTypeContext.getProxy(port, GCUBEScope.getScope(args[2]));
	} catch (Exception e) {
	    logger.error("",e);
	}

	logger.info("Submitting restore request...");
	try {
	    port.restore(new VOID());
	} catch (XMLStorageNotAvailableFaultType e) {
	    logger.error("",e);
	} catch (BackupNotAvailableFaultType e) {
	    logger.error("",e);
	} catch (RemoteException e) {
	    logger.error("",e);
	} 

    }

}
