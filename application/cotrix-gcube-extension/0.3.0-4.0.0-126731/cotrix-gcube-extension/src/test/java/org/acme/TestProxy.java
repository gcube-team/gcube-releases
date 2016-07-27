/**
 * 
 */
package org.acme;

import java.net.MalformedURLException;
import java.util.Collection;

import org.cotrix.domain.user.Role;
import org.cotrix.gcube.extension.DefaultPortalProxyProvider;
import org.cotrix.gcube.extension.DefaultRoleMapper;
import org.cotrix.gcube.extension.PortalProxy;
import org.cotrix.gcube.stubs.PortalUser;
import org.cotrix.gcube.stubs.SessionToken;

/**
 * @author "Federico De Faveri federico.defaveri@fao.org"
 *
 */
public class TestProxy {

	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) {
		DefaultPortalProxyProvider portalProxyProvider = new DefaultPortalProxyProvider();
		PortalProxy proxy = portalProxyProvider.getPortalProxy(new SessionToken("1B23F1C3A1FB4FA80D66F978528A0DF2", "/gcube/devsec/devVRE", "https://dev.d4science.org:443"));
		PortalUser user = proxy.getPortalUser();
		System.out.println(user);
		
		DefaultRoleMapper roleMapper = new DefaultRoleMapper();
		Collection<Role> roles = roleMapper.map(user.roles());
		for (Role role:roles) System.out.println(role);
		
		//proxy.publish("This is a test");

	}

}
