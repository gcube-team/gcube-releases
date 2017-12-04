package org.gcube.common.informationsystem.publisher.testsuite;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.publisher.ISPublisher;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.common.core.utils.logging.GCUBEClientLog;

/**
 * A tester which unpublishes {@link GCUBEResource} from the IS.
 * 
 * The test requires the following command-line inputs:
 * <p>
 * <OL>
 * <LI>the resource ID
 * <LI>the resource TYPE
 * <LI>the unpublishing scope
 * </OL>
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class GCUBEPublisherRemoveTest {

    protected static final GCUBEClientLog logger = new GCUBEClientLog(GCUBEPublisherStressTest.class);

    /**
     * @param args
     *            <OL>
     *            <LI>the resource ID
     *            <LI>the resource TYPE
     *            <LI>the unpublishing scope
     *            </OL>
     */
    public static void main(String[] args) {

	if (args.length == 3) {

	    GCUBESecurityManagerImpl managerSec = new GCUBESecurityManagerImpl() {
		public boolean isSecurityEnabled() {
		    return false;
		}
	    };
	    ISPublisher publisher = null;
	    try {
		publisher = GHNContext.getImplementation(ISPublisher.class);
	    } catch (Exception e1) {
		System.err.println("Unable to get te ISPublisher implementation");
		System.exit(0);
	    }

		try {
		    publisher.removeGCUBEResource(args[1], args[1], GCUBEScope.getScope(args[2]), managerSec);
		    System.out.println("Resource with ID " + args[1] + " removed");
		} catch (Exception e) {
		    System.err.println("Unable to remmove resource with ID " + args[1]);
		    e.printStackTrace();
		}
	    
	} else {
	    logger.error("USAGE: GCUBEPublisherRemoveTest <ID> <Resource TYPE> <callerScope>");
	    System.err.println("USAGE: GCUBEPublisherRemoveTest <ID> <Resource TYPE> <callerScope>");
	    System.exit(0);
	}
    }
}
