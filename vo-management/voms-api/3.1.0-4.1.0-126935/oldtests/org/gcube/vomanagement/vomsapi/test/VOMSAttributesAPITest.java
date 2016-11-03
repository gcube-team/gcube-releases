package org.gcube.vomanagement.vomsapi.test;

import org.gcube.vomanagement.voms.attributes.VOMSAttributesAPI;
import org.gcube.vomanagement.vomsAdmin.impl.VOMSAdminImpl;
import org.glite.wsdl.services.org_glite_security_voms.User;
import org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeClass;
import org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeValue;

/**
 * Test class to check interaction with VOMS server
 * 
 * @author Andrea Turli
 * 
 */
public class VOMSAttributesAPITest {

    /**
     * 
     * @param args args
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {

	String groupName = "/grifin/test";
	String testDescription = "testDescription";
	String testName = "testName";

	String userName = "test";
	String mail = "test@eng.it";

	String userCA = "/O=Engineering/OU=gridUnit/CN=GriFin CA";
	String DN = userName;

	try {

	    System.out.println("List attributes:");
	    VOMSAttributesAPI vomsAttributes = new VOMSAttributesAPI();
	    AttributeClass[] attributeClasses = vomsAttributes
		    .listAttributeClasses();
	    for (int i = 0; i < attributeClasses.length; i++) {
		AttributeClass attributeClass = attributeClasses[i];
		System.out.println(attributeClasses[i].getName()
			+ attributeClasses[i].getDescription());
	    }
	    System.out.println("");

	    System.out.println("Create attribute with name " + testName
		    + " and description " + testDescription);
	    vomsAttributes.createAttributeClass(testName, testDescription);

	    System.out.println("List attributes:");
	    vomsAttributes = new VOMSAttributesAPI();
	    attributeClasses = vomsAttributes.listAttributeClasses();
	    for (int i = 0; i < attributeClasses.length; i++) {
		System.out.println(attributeClasses[i].getName() + " "
			+ attributeClasses[i].getDescription());
	    }
	    System.out.println("");

	    System.out.println("Set attribute for group:" + groupName);
	    AttributeValue attributeValue = new AttributeValue();
	    attributeValue.setValue("testValue");
	    attributeValue.setAttributeClass(vomsAttributes
		    .getAttributeClass(testName));
	    vomsAttributes.setGroupAttribute(groupName, attributeValue);
	    System.out.println("");

	    System.out.println("List attributes for group:" + groupName);
	    AttributeValue[] attributeValues = vomsAttributes
		    .listGroupAttributes(groupName);
	    if (attributeValues != null) {
		System.out.println("the group " + groupName + " has "
			+ attributeValues.length + " attributes:");
		for (int i = 0; i < attributeValues.length; i++) {
		    System.out.println("\t- attributeValue: "
			    + attributeValues[i].getValue());
		    System.out.println("\t- attributeName: "
			    + attributeValues[i].getAttributeClass().getName());
		    System.out.println("\t- attributeDescription: "
			    + attributeValues[i].getAttributeClass()
				    .getDescription());
		    System.out.println("");
		}
	    }
	    System.out.println("");

	    System.out.println("Create a user with DN: " + DN + ", CA: "
		    + userCA + ", mail: " + mail);
	    VOMSAdminImpl vomsAdmin = new VOMSAdminImpl();
	    User user = new User();
	    user.setCN(userName);
	    user.setDN(userName);
	    user.setCA(userCA);
	    user.setMail(mail);
	    vomsAdmin.getExtendedPortType().createUser(userName, DN, userCA, mail);
	    System.out.println();

	    System.out.println("Set attribute for user:" + userName);
	    vomsAttributes.setUserAttribute(user, attributeValue);
	    System.out.println("");

	    System.out.println("List attributes for user:" + userName);
	    AttributeValue[] userAttributeValues = vomsAttributes
		    .listUserAttributes(user);
	    if (attributeValues != null) {
		System.out.println("the user " + userName + " has "
			+ userAttributeValues.length + " attributes:");
		for (int i = 0; i < userAttributeValues.length; i++) {
		    System.out.println("\t- attributeValue: "
			    + userAttributeValues[i].getValue());
		    System.out.println("\t- attributeName: "
			    + userAttributeValues[i].getAttributeClass()
				    .getName());
		    System.out.println("\t- attributeDescription: "
			    + userAttributeValues[i].getAttributeClass()
				    .getDescription());
		    System.out.println("");
		}
	    }
	    System.out.println("");

	    System.out.println("Destroy attribute with name " + testName);
	    vomsAttributes.deleteAttributeClass(testName);
	    System.out.println("Delete user with DN " + DN + " and CA "
		    + userCA);
	    vomsAdmin.getPortType().deleteUser(DN, userCA);
	    System.out.println("");
	    System.out.println("");
	    System.out.println("Finished");
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
