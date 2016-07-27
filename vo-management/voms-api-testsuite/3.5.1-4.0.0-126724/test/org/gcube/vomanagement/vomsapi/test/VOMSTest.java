package org.gcube.vomanagement.vomsapi.test;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.RootLogger;
import org.gcube.common.core.security.utils.ProxyUtil;
import org.gcube.vomanagement.vomsapi.CredentialsManager;
import org.gcube.vomanagement.vomsapi.VOMSAdmin;
import org.gcube.vomanagement.vomsapi.VOMSAttributes;
import org.gcube.vomanagement.vomsapi.impl.VOMSAPIFactory;
import org.gcube.vomanagement.vomsapi.impl.VOMSAPIConfiguration;
import org.gcube.vomanagement.vomsapi.impl.VOMSAPIConfigurationProperty;
import org.gcube.vomanagement.vomsapi.impl.ssl.MySSLSocketFactory;
import org.gcube.vomanagement.vomsapi.util.CredentialsUtil;
import org.glite.security.voms.FQAN;
import org.glite.wsdl.services.org_glite_security_voms.User;
import org.glite.wsdl.services.org_glite_security_voms.VOMSException;
import org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeClass;
import org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeValue;
import org.gridforum.jgss.ExtendedGSSCredential;

/**
 * Test class for the VOMS-API library.
 * 
 * @author Paolo Roccetti
 * */
public class VOMSTest {

	/**
	 * This is the main method to test VOMS-API library functionalities
	 * 
	 * @param args currently not used
	 * @throws Exception if something very bad occurs
	 */
	public static void main(String[] args) throws Exception {

		//set logger
		BasicConfigurator.configure(new ConsoleAppender());
		RootLogger.getLogger("org.gcube").setLevel(Level.DEBUG);
		RootLogger.getLogger("org.glite").setLevel(Level.DEBUG);
		
		//create and set properties
		Properties props = new Properties();
		props.setProperty(VOMSAPIConfigurationProperty.VOMS_HOST.toString(), "voms.research-infrastructures.eu");
		props.setProperty(VOMSAPIConfigurationProperty.VO_NAME.toString(), "gCube");
		props.setProperty(VOMSAPIConfigurationProperty.CLIENT_CERT.toString(), "/home/roccetti/certs/INFNPaoloRoccetti_cert.pem");
		props.setProperty(VOMSAPIConfigurationProperty.CLIENT_KEY.toString(), "/home/roccetti/certs/INFNPaoloRoccetti_key.pem");
		props.setProperty(VOMSAPIConfigurationProperty.CLIENT_PWD.toString(), "v3nd3tt@");
		props.setProperty(VOMSAPIConfigurationProperty.RUNS_IN_WS_CORE.toString(), "false");
		props.setProperty(VOMSAPIConfigurationProperty.MYPROXY_HOST.toString(), "grids04.eng.it");

		//create the stub factory
		VOMSAPIFactory factory = new VOMSAPIFactory(new VOMSAPIConfiguration(props));

		//create CredentialsManager stubs
		CredentialsManager credManager = factory.getCredentialsManager();
		
		String userName = "Alice";
		
		//create plain credentials
		ExtendedGSSCredential creds = credManager.getPlainCredentials(userName);
				
		//print plain credentials
		System.out.println(CredentialsUtil.stringCredentials(creds));		
		
		//get DN and CA
		String userDN = CredentialsUtil.getIdentityDN(creds);
		String userCA = CredentialsUtil.getIssuerDN(creds);

		//create VOMS Admin stub
		VOMSAdmin admin = factory.getVOMSAdmin();
		
		String groupName = "/gCube/testGroup";
		String parentGroupName = "/gCube";
		String roleName = "testRole";
		
		//create Group in VOMS
		admin.createGroup(parentGroupName, groupName);
		
		//create Role in VOMS
		admin.createRole(roleName);
		
		//create VOMS user
		User user = new User();
		user.setDN(userDN);
		user.setCA(userCA);
		user.setCN(userName);
		user.setMail(userName + "@testDomain.org");
		
		admin.createUser(user);

		//add the user as a member of the group
		admin.addMember(groupName, userDN, userCA);
		
		//assign the role to the user in the group
		admin.assignRole(groupName, roleName, userDN, userCA);
		
		//This method is based upon a gCore security code that requires some fixing, thus this test has been disabled 
//		//get attributed credentials for the user
//		ExtendedGSSCredential attributedCreds = credManager.getAttributedCredentials(userName, groupName);
//		//print attributed credentials
//		System.out.println(CredentialsUtil.stringCredentials(attributedCreds));	
		
		VOMSAttributes attributes = factory.getVOMSAttributes();
		
	    attributes.createAttributeClass("testAttributeClass", "testDescription");
					
		//list attribute classes
		System.out.println("Attribute classes: ");
		AttributeClass[] classes = attributes.listAttributeClasses();
		for (AttributeClass clazz : classes) {
			System.out.println("Class[" + clazz.getName() + "," + clazz.getDescription() + ", uniqueness: " + clazz.isUniquenessChecked() + "]");
		}

		//get Attribute class
		AttributeClass attributeClass = attributes.getAttributeClass("testAttributeClass");
		
		//create attribute
		AttributeValue attributeValue = new AttributeValue(attributeClass, "this field is useless", "testAttributeValue");		
		
		//set user attribute
		attributes.setUserAttribute(user, attributeValue);
		
		//set group attribute
		attributes.setGroupAttribute(groupName, attributeValue);

		//set role attribute
		attributes.setRoleAttribute(groupName, roleName, attributeValue);

		//list user attributes
		System.out.println("Attributes of user DN=" + user.getDN() + ", CA=" + user.getCA());
		AttributeValue[] userAttributes = attributes.listUserAttributes(user);
		for (AttributeValue attribute : userAttributes) {
			System.out.println("\tAttribute[class=" + attribute.getAttributeClass().getName() + ", value=" 
				+ attribute.getValue() + ",  context=" + attribute.getContext() + "]");
		}
		System.out.println("--------------");

		//list group attributes
		System.out.println("Attributes of group " + groupName);
		AttributeValue[] groupAttributes = attributes.listGroupAttributes(groupName);
		for (AttributeValue attribute : groupAttributes) {
			System.out.println("\tAttribute[class=" + attribute.getAttributeClass().getName() + ", value=" 
				+ attribute.getValue() + ",  context=" + attribute.getContext() + "]");
		}
		System.out.println("--------------");

		//list group attributes
		System.out.println("Attributes of role " + roleName + " in group " + groupName);
		AttributeValue[] roleAttributes = attributes.listRoleAttributes(groupName, roleName);
		for (AttributeValue attribute : roleAttributes) {
			System.out.println("\tAttribute[class=" + attribute.getAttributeClass().getName() + ", value=" 
				+ attribute.getValue() + ",  context=" + attribute.getContext() + "]");
		}
		System.out.println("--------------");

		//unset user attribute
		attributes.deleteUserAttribute(user, attributeValue);
		
		//uset group attribute
		attributes.deleteGroupAttribute(groupName, attributeValue);

		//unset role attribute
		factory.getVOMSAttributes().deleteRoleAttribute(groupName, roleName, attributeValue);
				
		//delete attribute class
		factory.getVOMSAttributes().deleteAttributeClass("testAttributeClass");
		
		//delete user, group and role
		admin.deleteUser(userDN, userCA);
		admin.deleteRole(roleName);
		admin.deleteGroup(groupName);

	}
}
