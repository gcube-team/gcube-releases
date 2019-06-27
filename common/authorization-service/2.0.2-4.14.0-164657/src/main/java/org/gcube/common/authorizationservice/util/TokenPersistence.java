package org.gcube.common.authorizationservice.util;

import java.util.List;
import java.util.Map;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.policies.Policy;
import org.gcube.common.authorization.library.policies.PolicyType;
import org.gcube.common.authorization.library.provider.ClientInfo;

public interface TokenPersistence {
	
	void saveAuthorizationEntry(String token, String context, ClientInfo info, String tokenQualifier, String generateBy);
	
	void removeAllAuthorizationsEntryForClientId(String context, String clientId);
	
	AuthorizationEntry getAuthorizationEntry(String token);
			
	String getExistingToken(String clientId, String context, String tokenQualifier);
	
	Map<String, String> getExistingApiKeys(String clientId, String context);
	
	void removeApiKey(String token);
	
	void addPolicies(List<Policy> polices);
	
	void removePolicy(long policyId);
	
	List<Policy> getPolices(String context);

	List<Policy> getPolicesByType(String context, PolicyType type);

	List<Policy> getPolicesByTypeAndClientId(String context, PolicyType type,
			String clientId);

	Map<String, String> getExistingExternalServices(String generatorId,
			String context);


}
