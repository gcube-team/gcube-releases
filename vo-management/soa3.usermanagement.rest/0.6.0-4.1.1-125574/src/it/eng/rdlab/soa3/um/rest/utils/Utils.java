package it.eng.rdlab.soa3.um.rest.utils;

import it.eng.rdlab.soa3.um.rest.bean.UserModel;
import it.eng.rdlab.soa3.um.rest.conf.ConfigurationManager;
import it.eng.rdlab.um.beans.GenericModelWrapper;
import it.eng.rdlab.um.ldap.configuration.LdapConfiguration;
import it.eng.rdlab.um.ldap.service.LdapManager;
import it.eng.rdlab.um.ldap.user.bean.LdapUserModel;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

/**
 * This class provides utilities to manage LDAP's features and object mapping
 * 
 * @author Ermanno Travaglino
 * @version 1.0
 *
 */
public class Utils 
{
	static Logger logger = Logger.getLogger(Utils.class.getName());
	
	/**
	 * Builds the organization's distinguished name
	 * 
	 * @param organizationName String
	 * @return organizationDN String
 	 */
	public static String organizationDNBuilder (String organizationName)
	{
		if (organizationName == null || organizationName.trim().length() == 0) return ConfigurationManager.getInstance().getLdapBase();
		else
		{
			StringBuilder distinguishedNameBuilder = new StringBuilder("dc=");
			distinguishedNameBuilder.append(organizationName).append(",").append(ConfigurationManager.getInstance().getLdapBase());
			logger.debug("dn = "+distinguishedNameBuilder.toString());
			return distinguishedNameBuilder.toString();
		}

	}
	
	/**
	 * Builds the user's distinguished name
	 * 
	 * @param userId String
	 * @param organizationName String
	 * @return userDN String
 	 */
	public static String userDNBuilder (String userId, String organizationName)
	{
		StringBuilder distinguishedNameBuilder = new StringBuilder("uid=");
		distinguishedNameBuilder.append(userId).append(",ou=").append(Constants.OU_PEOPLE).append(",").append(organizationDNBuilder (organizationName));
		logger.debug("dn = "+distinguishedNameBuilder.toString());
		return distinguishedNameBuilder.toString();

	}
	
	/**
	 * Builds the role's distinguished name
	 * 
	 * @param roleId String
	 * @param organizationName String
	 * @return roleDN String
 	 */
	public static  String roleDNBuilder (String roleId, String organizationName)
	{
		StringBuilder distinguishedNameBuilder = new StringBuilder("cn=");
		distinguishedNameBuilder.append(roleId).append(",ou=").append(Constants.OU_ROLES).append(",").append(organizationDNBuilder(organizationName));
		logger.debug("dn = "+distinguishedNameBuilder.toString());
		return distinguishedNameBuilder.toString();

	}
	
	/**
	 * Builds the group's distinguished name
	 * 
	 * @param groupId String
	 * @param organizationName String
	 * @return groupDN String
 	 */
	public static  String groupDNBuilder (String groupId, String organizationName)
	{
		StringBuilder distinguishedNameBuilder = new StringBuilder("cn=");
		distinguishedNameBuilder.append(groupId).append(",ou=").append(Constants.OU_GROUPS).append(",").append(organizationDNBuilder(organizationName));
		logger.debug("dn = "+distinguishedNameBuilder.toString());
		return distinguishedNameBuilder.toString();

	}

	/**
	 * Gets the organization's name from distinguished name
	 * 
	 * @param organizationDn String
	 * @return organizationName String
 	 */
	public static String getOrganizationNameFromDN (String organizationDn)
	{
		try 
		{
			String organizationName = organizationDn.substring(organizationDn.indexOf('=')+1, organizationDn.indexOf(","));
			logger.debug("Organization name = "+organizationName);
			return organizationName;
		}
		catch (Exception e)
		{
			logger.error("Invalid organization DN", e);
			return null;
		}

	}
	
	/**
	 * Initializes the LDAP server with admin's credential and LDAP URL
	 * 
	 * @param adminUsername String
	 * @param adminPassword String
	 * @param ldapUrl String
	 * @return organizationName String
 	 */
	public static  void initLdap (String adminUsername, String adminPassword,String ldapUrl) throws NamingException
	{
		LdapConfiguration configuration = new LdapConfiguration();
		configuration.setUrl(ldapUrl);
		configuration.setUserDn(adminUsername);
		configuration.setPassword(adminPassword);
		LdapManager.initInstance(configuration);
	
	}
	
	/**
	 * Converts the LDAPUserModel to UserModel
	 * 
	 * @param ldapUserModel LdapUserModel
	 * @return userModel UserModel
 	 */
	public static  UserModel convertUserModel (LdapUserModel ldapUserModel)
	{
		UserModel model = new UserModel();
		model.setUserId(ldapUserModel.getUserId());
		String email = new GenericModelWrapper(ldapUserModel).getStringParameter(LdapUserModel.EMAIL); 
		
		if (email.length() >0 ) model.setEmail(email);
		
		String certDN = new GenericModelWrapper(ldapUserModel).getStringParameter(LdapUserModel.CERTIFICATE); 
		
		if (certDN.length() >0 ) model.setCertDN(certDN);
		
		model.setFirstname(ldapUserModel.getCN());
		model.setLastname(ldapUserModel.getSN());
		
		if (ldapUserModel.getPassword() != null) model.setPassword(new String(ldapUserModel.getPassword()));
		
		return model;
	}
	
	/**
	 * Generates the people's distinguished name under an organization
	 * 
	 * @param organizationName String
	 * @return peopleDN String
 	 */
	public static String generatePeopleDN (String organizationName)
	{
		StringBuilder builder = new StringBuilder("ou=");
		builder.append(Constants.OU_PEOPLE).append(",").append(organizationDNBuilder(organizationName));
		return builder.toString();
	}
	
	/**
	 * Generates the groups' distinguished name under an organization
	 * 
	 * @param organizationName String
	 * @return groupsDN String
 	 */
	public static String generateGroupsDN (String organizationName)
	{
		StringBuilder builder = new StringBuilder("ou=");
		builder.append(Constants.OU_GROUPS).append(",").append(organizationDNBuilder(organizationName));
		return builder.toString();
	}

	/**
	 * Generates the roles' distinguished name under an organization
	 * 
	 * @param organizationName String
	 * @return rolesDN String
 	 */
	public static String generateRolesDN(String organizationName) {
		StringBuilder builder = new StringBuilder("ou=");
		builder.append(Constants.OU_ROLES).append(",").append(organizationDNBuilder(organizationName));
		return builder.toString();
	}
	
	/**
	  * Chooses JAXB as the annotation for the serialization/deserialization
	  *  
	  * @return mapper ObjectMapper
	  * 
	  */
	public static ObjectMapper getMapper(){
		ObjectMapper mapper = new ObjectMapper();

		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
		mapper.getDeserializationConfig().setAnnotationIntrospector(
				introspector);
		mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
		return mapper;
	}
}
