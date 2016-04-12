/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util;

import java.rmi.Remote;

//import org.gcube.application.framework.core.security.PortalSecurityManager;
import org.gcube.application.framework.core.session.ASLSession;
//import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
//import org.gcube.common.core.security.GCUBESecurityManager;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class PortTypeUtil {

	/**
     * Creates a stub proxy given a port-type object.
     * @param <PORTTYPE> the type of the port-type stub
     * @param portTypeStub the original port-type to generate a stub proxy for
     * @return the stub proxy
     * @throws Exception an error occured
     */
    public static <PORTTYPE extends Remote> PORTTYPE getStubProxy(ASLSession session, PORTTYPE portTypeStub) throws Exception {
		
//		/* Create the security manager */
//    	GCUBESecurityManager secManager = (GCUBESecurityManager) new PortalSecurityManager(session);
//		if(secManager.isSecurityEnabled())
//		{
//			try {
//				secManager.useCredentials(session.getCredential());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		
//		/* Get the sub proxy */
//		try {
//			return GCUBERemotePortTypeContext.getProxy( portTypeStub, GCubeScopeManager, secManager);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
    	return null;
    }
}
