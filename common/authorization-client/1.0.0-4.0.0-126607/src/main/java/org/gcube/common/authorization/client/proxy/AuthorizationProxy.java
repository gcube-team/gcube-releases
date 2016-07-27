package org.gcube.common.authorization.client.proxy;

import java.util.List;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.BannedService;

public interface AuthorizationProxy {

	String generate(String userName, List<String> roles);
	
	AuthorizationEntry get(String token) throws ObjectNotFound;

	BannedService deny(String userName, String serviceClass, String serviceName);

	void allow(String userName, String serviceClass, String serviceName);
	
	List<BannedService> getBannedServices(String userName);
}
