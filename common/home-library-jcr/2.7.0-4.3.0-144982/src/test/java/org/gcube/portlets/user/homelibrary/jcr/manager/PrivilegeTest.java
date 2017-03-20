package org.gcube.portlets.user.homelibrary.jcr.manager;

import javax.jcr.security.Privilege;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.privilegemanager.PrivilegeManager;
import org.gcube.common.scope.api.ScopeProvider;

public class PrivilegeTest {
	static PrivilegeManager am = null;
	/**
	 * @param args
	 * @throws InternalErrorException 
	 */
	public static void main(String[] args) throws InternalErrorException {
		
//		ScopeProvider.instance.set("/CNR.it");
//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
//		ScopeProvider.instance.set("/gcube/devsec");
		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		 am = HomeLibrary
				.getHomeManagerFactory().getPrivilegeManager();
		
		 
//		 String[] privileges = null ;
//			if (privileges == null)
//				privileges = new String[] {};
//		System.out.println( privileges.length);
		am.createCostumePrivilege("hl:writeOwner", new String[] {Privilege.JCR_MODIFY_PROPERTIES, Privilege.JCR_ADD_CHILD_NODES, Privilege.JCR_REMOVE_NODE, Privilege.JCR_REMOVE_CHILD_NODES});
		 am.createCostumePrivilege("hl:removeSharedRoot", new String[] {});
	 
		 am.createCostumePrivilege("hl:noOwnershipLimit", new String[] {});
		 am.createCostumePrivilege("hl:writeAll", new String[] {"jcr:write", "hl:noOwnershipLimit"});
		 am.createCostumePrivilege("hl:removeSharedRoot", new String[] {});
		 am.createCostumePrivilege("hl:noRights", new String[] {});

		
//		 am.createCostumePrivilege("hl:administerNode", new String[] {"jcr:all", "hl:removeSharedRoot"});

		
		 
	}

}
