package org.apache.jackrabbit.j2ee.privilegemanager;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.api.JackrabbitWorkspace;
import org.apache.jackrabbit.api.security.authorization.PrivilegeManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCRPrivilegeManager {

	private Logger logger = LoggerFactory.getLogger(JCRPrivilegeManager.class);

	private PrivilegeManager privilegeManager;

	
	public JCRPrivilegeManager(SessionImpl session) throws InternalErrorException {

		JackrabbitWorkspace jrws = (JackrabbitWorkspace) session.getWorkspace();
		try {
			privilegeManager = jrws.getPrivilegeManager();
		} catch (RepositoryException e) {
			logger.error("Error getting privilegeManager ", e );
		}
		

	}
	
	public void createCostumePrivilege(String name, String[] declaredAggregateNames) throws RepositoryException {

		//		System.out.println("Creating the costume privilege " + name + " with privileges: " + declaredAggregateNames.toString());

	
		logger.info("Create Costume Privilege " );

		Privilege[] privileges = privilegeManager.getRegisteredPrivileges();
		for (int i=0; i< privileges.length; i++){
			logger.debug(privileges[i].getName());
			//			System.out.println(privileges[i].getAggregatePrivileges().toString());
			//			System.out.println(privileges[i].getDeclaredAggregatePrivileges().toString());
		}

		logger.debug("getPrivilege " + name);
		try {
			Privilege priv = privilegeManager.getPrivilege(name);
			logger.debug("Privilege already exists: " + priv.getName());
		} catch (AccessControlException e) {
			logger.error("catch " + e);
			try{
				privilegeManager.registerPrivilege(name, false, declaredAggregateNames);
			}catch (Exception e1) {
				logger.error("Error registering privilege " + name + " - " + e1);
			}
		}
		logger.debug("getRegisteredPrivileges 2 " );
		Privilege[] privileges1 = privilegeManager.getRegisteredPrivileges();
		for (int i=0; i< privileges1.length; i++){
			logger.debug(privileges1[i].getName());
			//			System.out.println(privileges1[i].getAggregatePrivileges().toString());
			//			System.out.println(privileges1[i].getDeclaredAggregatePrivileges().toString());
		}

	}
	
	
	public List<String> getRegisteredPrivileges() throws RepositoryException {

		logger.info("Get Registered Privileges " );
		List<String> mylist = new ArrayList<String>();
		Privilege[] privileges = privilegeManager.getRegisteredPrivileges();
		for (int i=0; i< privileges.length; i++){
			logger.info(privileges[i].getName());
			mylist.add(privileges[i].getName());
		}
		return mylist;


	}
	
}
