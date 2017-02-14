package org.gcube.vomanagement.vomsapi.test;

import org.gcube.vomanagement.voms.acl.VOMSACLAPI;
import org.gcube.vomanagement.vomsAdmin.impl.VOMSAdminImpl;
import org.glite.wsdl.services.org_glite_security_voms_service_acl.ACLEntry;

/**
 * Test class to check interaction with VOMS server
 * 
 * @author Andrea Turli
 * 
 */
public class VOMSACLAPITest {

    /**
     * 
     * @param args args
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {

	String groupName = "/grifin/alice";
	
	try {
    
	    System.out.println("List ACL for group: " + groupName);
	    VOMSACLAPI vomsaclapi = new VOMSACLAPI();
	
	    System.out.println("--------------------------------------");
	    System.out.println("Number of root ACL: " + vomsaclapi.getACL(new VOMSAdminImpl().getPortType().getVOName()).length);
	    System.out.println("Number of " + groupName + " ACL: " + vomsaclapi.getACL(groupName).length);
	    
	    VOMSAdminImpl vomsAdmin = new VOMSAdminImpl();
	    vomsAdmin.getExtendedPortType().configureAsAdmin(groupName, "test-admin");
	    
	    System.out.println("--------------------------------------");
	    System.out.println("Number of root ACL: " + vomsaclapi.getACL(new VOMSAdminImpl().getPortType().getVOName()).length);
	    System.out.println("Number of " + groupName + " ACL: " + vomsaclapi.getACL(groupName).length);
    
	    vomsAdmin.getExtendedPortType().revokeAsAdmin(groupName, "test-admin");
	    System.out.println("--------------------------------------");
	    System.out.println("Number of root ACL: " + vomsaclapi.getACL(new VOMSAdminImpl().getPortType().getVOName()).length);
	    System.out.println("Number of " + groupName + " ACL: " + vomsaclapi.getACL(groupName).length);
	
	    
	    System.out.println("Finished");
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
