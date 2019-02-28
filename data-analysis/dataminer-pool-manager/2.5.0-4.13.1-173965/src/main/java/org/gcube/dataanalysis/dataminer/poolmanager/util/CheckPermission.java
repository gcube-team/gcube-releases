package org.gcube.dataanalysis.dataminer.poolmanager.util;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CheckPermission {

	private static final Logger logger = LoggerFactory.getLogger(CheckPermission.class);
	


	public static boolean apply(String VREToken, String vre) throws ObjectNotFound, Exception 
	{
		AuthorizationEntry entry = authorizationService().get(VREToken);
		if (entry.getContext().equals(vre)) {
			logger.info("Authorization OK!");
			return true;
		}
		logger.info("Not a valid token recognized for the VRE: "+vre);
		return false;

	}
}
