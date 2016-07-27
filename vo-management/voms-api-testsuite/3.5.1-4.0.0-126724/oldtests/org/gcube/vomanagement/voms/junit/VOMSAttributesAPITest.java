/**
 * 
 */
package org.gcube.vomanagement.voms.junit;

import java.rmi.RemoteException;

import junit.framework.TestCase;

import org.gcube.vomanagement.voms.attributes.VOMSAttributesAPI;
import org.gcube.vomanagement.vomsAdmin.impl.VOMSAdminImpl;
import org.glite.wsdl.services.org_glite_security_voms.User;
import org.glite.wsdl.services.org_glite_security_voms.VOMSException;
import org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeValue;

/**
 * @author turli
 *
 */
public class VOMSAttributesAPITest extends TestCase {

    VOMSAttributesAPI vomsAttributes = null;
    
	String groupName = "/grifin/test";
	String roleName = "Role=aclRole";
	String testDescription = "testDescription";
	String testName = "testName";

	String userName = "test";
	String mail = "test@eng.it";

	String userCA = "/O=Engineering/OU=gridUnit/CN=GriFin CA";
	String DN = userName;
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
	super.setUp();
	this.vomsAttributes = new VOMSAttributesAPI();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
	super.tearDown();
	this.vomsAttributes = null;
    }

    /**
     * Test method for {@link org.gcube.vomanagement.voms.attributes.VOMSAttributesAPI#createAttributeClass(java.lang.String)}.
     */
    public void testCreateAttributeClassString() {
	try {
	    vomsAttributes.createAttributeClass(testName);
	    assertEquals(testName, this.vomsAttributes.getAttributeClass(testName).getName());
	    vomsAttributes.deleteAttributeClass(testName);
	} catch (VOMSException e) {
	    e.printStackTrace();
	} catch (RemoteException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Test method for {@link org.gcube.vomanagement.voms.attributes.VOMSAttributesAPI#createAttributeClass(java.lang.String, java.lang.String)}.
     */
    public void testCreateAttributeClassStringString() {
	try {
	    vomsAttributes.createAttributeClass(testName, testDescription);
	    assertEquals(testName, this.vomsAttributes.getAttributeClass(testName).getName());
	    vomsAttributes.deleteAttributeClass(testName);
	} catch (VOMSException e) {
	    e.printStackTrace();
	} catch (RemoteException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Test method for {@link org.gcube.vomanagement.voms.attributes.VOMSAttributesAPI#createAttributeClass(java.lang.String, java.lang.String, boolean)}.
     */
    public void testCreateAttributeClassStringStringBoolean() {
	try {
	    vomsAttributes.createAttributeClass(testName, testDescription);
	    assertEquals(testName, this.vomsAttributes.getAttributeClass(testName).getName());
	    vomsAttributes.deleteAttributeClass(testName);
	} catch (VOMSException e) {
	    e.printStackTrace();
	} catch (RemoteException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Test method for {@link org.gcube.vomanagement.voms.attributes.VOMSAttributesAPI#setGroupAttribute(java.lang.String, org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeValue)}.
     */
    public void testSetGroupAttribute() {
	try {
	    vomsAttributes.createAttributeClass(testName, testDescription);
	    AttributeValue attributeValue = new AttributeValue();
	    attributeValue.setValue("testValue");
	    attributeValue.setAttributeClass(vomsAttributes.getAttributeClass(testName));
	    int lenght = 0;
	    if(this.vomsAttributes.listGroupAttributes(groupName) != null)
		lenght = this.vomsAttributes.listGroupAttributes(groupName).length;
	    vomsAttributes.setGroupAttribute(groupName, attributeValue);
	    assertEquals(lenght + 1, this.vomsAttributes.listGroupAttributes(groupName).length);
	    vomsAttributes.deleteGroupAttribute(groupName, attributeValue);
	    vomsAttributes.deleteAttributeClass(testName);
	} catch (VOMSException e) {
	    e.printStackTrace();
	} catch (RemoteException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Test method for {@link org.gcube.vomanagement.voms.attributes.VOMSAttributesAPI#setRoleAttribute(java.lang.String, java.lang.String, org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeValue)}.
     */
    public void testSetRoleAttribute() {
	try {
	    vomsAttributes.createAttributeClass(testName, testDescription);
	    AttributeValue attributeValue = new AttributeValue();
	    attributeValue.setValue("testValue");
	    attributeValue.setAttributeClass(vomsAttributes.getAttributeClass(testName));
	    int lenght = 0;
	    if(this.vomsAttributes.listRoleAttributes(groupName, roleName) != null)
		lenght = this.vomsAttributes.listRoleAttributes(groupName, roleName).length;
	    vomsAttributes.setRoleAttribute(groupName, roleName, attributeValue);
	    assertEquals(lenght + 1, this.vomsAttributes.listRoleAttributes(groupName, roleName).length);
	    vomsAttributes.deleteRoleAttribute(groupName, roleName, attributeValue);
	    vomsAttributes.deleteAttributeClass(testName);
	} catch (VOMSException e) {
	    e.printStackTrace();
	} catch (RemoteException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Test method for {@link org.gcube.vomanagement.voms.attributes.VOMSAttributesAPI#setUserAttribute(org.glite.wsdl.services.org_glite_security_voms.User, org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeValue)}.
     */
    public void testSetUserAttribute() {
	   User user = new User();
	    user.setCN(userName);
	    user.setDN(userName);
	    user.setCA(userCA);
	    user.setMail(mail);

	try {
	    VOMSAdminImpl vomsAdmin = new VOMSAdminImpl();
	    vomsAdmin.getPortType().createUser(user);
	    vomsAttributes.createAttributeClass(testName, testDescription);
	    AttributeValue attributeValue = new AttributeValue();
	    attributeValue.setValue("testValue");
	    attributeValue.setAttributeClass(vomsAttributes.getAttributeClass(testName));
	    int lenght = 0;
	    if(this.vomsAttributes.listRoleAttributes(groupName, roleName) != null)
		lenght = this.vomsAttributes.listUserAttributes(user).length;
	    vomsAttributes.setUserAttribute(user, attributeValue);
	    assertEquals(lenght + 1, this.vomsAttributes.listUserAttributes(user).length);
	    vomsAttributes.deleteUserAttribute(user, attributeValue);
	    vomsAttributes.deleteAttributeClass(testName);
	    vomsAdmin.getPortType().deleteUser(userName, userCA);
	} catch (VOMSException e) {
	    e.printStackTrace();
	} catch (RemoteException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    /**
     * Test method for {@link org.gcube.vomanagement.voms.attributes.VOMSAttributesAPI#listAttributeClasses()}.
     */
    public void testListAttributeClasses() {
	try {
	   
	    int lenght = 0;
	    if(this.vomsAttributes.listAttributeClasses() != null)
		lenght = this.vomsAttributes.listAttributeClasses().length;
	    
	    vomsAttributes.createAttributeClass(testName, testDescription);
	    assertEquals(lenght + 1, this.vomsAttributes.listAttributeClasses().length);
	    vomsAttributes.deleteAttributeClass(testName);
	} catch (VOMSException e) {
	    e.printStackTrace();
	} catch (RemoteException e) {
	    e.printStackTrace();
	}

    }

}
