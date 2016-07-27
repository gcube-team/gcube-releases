package org.gcube.common.vremanagement.ghnmanager.testsuite;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.common.vremanagement.ghnmanager.stubs.GHNManagerPortType;
import org.gcube.common.vremanagement.ghnmanager.stubs.ScopeRIParams;
import org.gcube.common.vremanagement.ghnmanager.stubs.service.GHNManagerServiceAddressingLocator;

public class RemoveAllRIsFromScope {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	    String[] scopesToRemove = new String[]{"/d4science.research-infrastructures.eu/FCPPS", "/d4science.research-infrastructures.eu/EM",
	    		"/d4science.research-infrastructures.eu"
	    };
	    
	    String callerScope = args[0];
	    
		GCUBESecurityManagerImpl managerSec = new GCUBESecurityManagerImpl() {  public boolean isSecurityEnabled() {return false;}};
		
		EndpointReferenceType endpoint = new EndpointReferenceType();
		try {
			endpoint.setAddress(new Address("http://"+ args[1] + ":" + args[2] +"/wsrf/services/gcube/common/vremanagement/GHNManager"));
			GHNManagerServiceAddressingLocator locator = new GHNManagerServiceAddressingLocator();
			GHNManagerPortType pt = GCUBERemotePortTypeContext.getProxy(locator.getGHNManagerPortTypePort(endpoint), 
					GCUBEScope.getScope(callerScope),managerSec);				
			for (String toRemove : scopesToRemove) {
				ScopeRIParams params = new ScopeRIParams();			
				params.setClazz(args[3]);
				params.setName(args[4]);
				params.setScope(toRemove);
				pt.removeRIFromScope(params);	
			}
			
		} catch (MalformedURIException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
