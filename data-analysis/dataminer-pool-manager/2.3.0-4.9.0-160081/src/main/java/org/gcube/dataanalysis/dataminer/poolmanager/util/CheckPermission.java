package org.gcube.dataanalysis.dataminer.poolmanager.util;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;


public class CheckPermission {

	public CheckPermission() {
	}

	public static boolean apply(String VREToken, String vre) throws ObjectNotFound, Exception {
		
		AuthorizationEntry entry = authorizationService().get(VREToken);
		if (entry.getContext().equals(vre)) {
			System.out.println("Authorization OK!");
			return true;
		}
		System.out.println("Not a valid token recognized for the VRE: "+vre);
		return false;

	}
}
