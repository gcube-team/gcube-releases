package org.gcube.common.authorization.library;

import org.gcube.common.authorization.library.policies.ServiceAccess;
import org.gcube.common.authorization.library.provider.ServiceIdentifier;

/**
 * 
 * @author lucio lelii
 *
 */
public class PolicyUtils {

	public static boolean isPolicyValidForClient(ServiceAccess serviceAccess, ServiceIdentifier serviceId) {
		String policyAsString = serviceAccess.getAsString();
		return policyAsString.equals("*") || policyAsString.equals(serviceId.getServiceClass()+":*") ||
				policyAsString.equals(serviceId.getServiceClass()+":"+serviceId.getServiceName()+":*") ||
				policyAsString.equals(serviceId.getFullIdentifier());
	}
}
