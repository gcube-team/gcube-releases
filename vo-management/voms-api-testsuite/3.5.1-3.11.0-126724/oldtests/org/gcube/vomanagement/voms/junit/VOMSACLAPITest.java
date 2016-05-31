/**
 * 
 */
package org.gcube.vomanagement.voms.junit;

import java.rmi.RemoteException;

import junit.framework.TestCase;

import org.gcube.vomanagement.voms.acl.VOMSACLAPI;
import org.glite.wsdl.services.org_glite_security_voms.VOMSException;
import org.glite.wsdl.services.org_glite_security_voms_service_acl.ACLEntry;

/**
 * @author turli
 *
 */
public class VOMSACLAPITest extends TestCase {
    
    VOMSACLAPI vomsacl = null;
    String groupName = "/grifin/alice";

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
	super.setUp();
	this.vomsacl = new VOMSACLAPI();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
	super.tearDown();
	this.vomsacl = null;
    }

    /**
     * Test method for {@link org.gcube.vomanagement.voms.acl.VOMSACLAPI#addACLEntry(java.lang.String, org.glite.wsdl.services.org_glite_security_voms_service_acl.ACLEntry, boolean)}.
     */
    public void testAddACLEntry() {
	ACLEntry entry = new ACLEntry();
	entry.setAdminIssuer("/O=Engineering/OU=gridUnit/CN=GriFin CA");
	entry.setAdminSubject(groupName+"/Role=aclRole");
	entry.setVomsPermissionBits(7);
	try {
	    int lenght = this.vomsacl.getACL(groupName).length;
	    this.vomsacl.addACLEntry(groupName, entry, true);
	    assertEquals(lenght+1, this.vomsacl.getACL(groupName).length);
	    this.vomsacl.removeACLEntry(groupName, entry, true);
	} catch (VOMSException e) {
	    // TODO_ANDREA Auto-generated catch block
	    e.printStackTrace();
	} catch (RemoteException e) {
	    // TODO_ANDREA Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * Test method for {@link org.gcube.vomanagement.voms.acl.VOMSACLAPI#addDefaultACLEntry(java.lang.String, org.glite.wsdl.services.org_glite_security_voms_service_acl.ACLEntry)}.
     */
    public void testAddDefaultACLEntry() {
	ACLEntry entry = new ACLEntry();
	entry.setAdminIssuer("/O=Engineering/OU=gridUnit/CN=GriFin CA");
	entry.setAdminSubject(groupName+"/Role=aclRole");
	entry.setVomsPermissionBits(7);
	try {
	    int lenght = this.vomsacl.getDefaultACL(groupName).length;
	    this.vomsacl.addDefaultACLEntry(groupName, entry);
	    assertEquals(lenght+1, this.vomsacl.getDefaultACL(groupName).length);
	    this.vomsacl.removeDefaultACLEntry(groupName, entry);
	} catch (VOMSException e) {
	    // TODO_ANDREA Auto-generated catch block
	    e.printStackTrace();
	} catch (RemoteException e) {
	    // TODO_ANDREA Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
