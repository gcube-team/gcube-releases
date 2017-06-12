package org.gcube.common.authorization.client.proxy;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.enpoints.AuthorizationEndpoint;
import org.gcube.common.authorization.library.enpoints.EndpointsContainer;
import org.gcube.common.authorization.library.policies.Policy;
import org.gcube.common.authorization.library.provider.ContainerInfo;
import org.gcube.common.authorization.library.provider.ServiceInfo;
import org.gcube.common.authorization.library.provider.UserInfo;

public interface AuthorizationProxy {

	AuthorizationEndpoint getEndpoint(int infrastructureHash);
	
	void setEndpoint(EndpointsContainer endpoints);
		
	AuthorizationEntry get(String token) throws ObjectNotFound, Exception;
		
	void addPolicies(List<Policy> policies) throws Exception;

	void removePolicies(long ... ids) throws Exception;
	
	List<Policy> getPolicies(String context) throws Exception;

	String generateApiKey(String apiQualifier) throws Exception;

	String generateServiceToken(ServiceInfo client) throws Exception;

	String generateUserToken(UserInfo client, String context)
			throws Exception;

	String requestActivation(ContainerInfo container) throws Exception;
	
	String requestActivation(ContainerInfo container, String context) throws Exception;

	Map<String, String> retrieveApiKeys() throws Exception;

	File getSymmKey(String filePath) throws Exception;

	String resolveTokenByUserAndContext(String user, String context)
			throws Exception;

	String generateExternalServiceToken(String serviceId)
			throws Exception;

	Map<String, String> retrieveExternalServiceGenerated() throws Exception;
	
}
