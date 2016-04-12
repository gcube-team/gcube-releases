package org.gcube.portal.ldapexport;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.gcube.common.portal.PortalContext;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

public class LDAPSync implements Runnable {
	private static final Logger _log = LoggerFactory.getLogger(LDAPSync.class);

	private static final String LDAP_ORG_FILTER = "(objectClass=organizationalUnit)";
	private static final String LDAP_GROUP_FILTER = "(objectClass=posixGroup)";
	private static final String USER_CONTEXT = ",ou=People,o=D4Science,ou=Organizations,dc=d4science,dc=org";
	private static final String DEFAULT_GID_NUMBER = "1000";

	private String ldapUrl;
	private String filter; 
	private String principal;
	private String pwd;


	public LDAPSync(String ldapUrl, String filter, String principal, String pwd) {
		this.ldapUrl = ldapUrl;
		this.filter = filter;
		this.principal = principal;
		this.pwd = pwd;
		_log.info("Starting LDAPSync over " + ldapUrl);
	}

	/**
	 * 
	 * @return the Liferay mapped as Root Organization
	 */
	private Organization getRootVO() {
		String rootVoName = PortalContext.getConfiguration().getInfrastructureName();
		_log.debug("Root organization name found: " + rootVoName);

		//start of iteration of the actual groups
		List<Organization> organizations;
		try {
			organizations = OrganizationLocalServiceUtil.getOrganizations(0, OrganizationLocalServiceUtil.getOrganizationsCount());
			for (Organization organization : organizations) {
				if (organization.getName().equals(rootVoName)) {
					return organization;
				}
			}
		} 
		catch (SystemException e) {
			_log.error("There were problems retrieving root organization", e);
		}
		_log.error("Could not find any root organization");
		return null;
	}




	@Override
	public void run() {
		_log.debug("Reading Portal Users ...");
		List<User> users = null; 
		try {
			users = getAllLiferayUsers();
			_log.debug("\n***Read " + users.size() + " from LR DB\n");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		_log.debug("Reading Portal Organizations ...");
		Organization rootVO = getRootVO();

		_log.debug("Initializing LDAP exporter ...");

		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapUrl);
		env.put(Context.SECURITY_PRINCIPAL, principal);
		env.put(Context.SECURITY_CREDENTIALS, pwd);

		try {
			DirContext ctx = new InitialDirContext(env);
			_log.debug("Initiating LDAP Sync ...");
			createUsersOrganizationalUnit(ctx);

			//crate or update the whole list of organizations (objectClass=organizationalUnit, ou="+orgName+",dc=d4science,dc=org) and groups ( objectClass=top and POSIXGroup)
			
			updateGroups(ctx, rootVO);
			//and update the users list
			exportSingleUsers(ctx, env, users);
		} catch (NamingException e) {
			_log.error("Something went Wrong during LDAP Sync in Exporting to LDAP");
			e.printStackTrace();
		} catch (SystemException es) {
			_log.error("Something went Wrong during LDAP Sync in retrieving Liferay Organization");
			es.printStackTrace();
		}
	}
	
	/**
	 * create the following: ou=People,o=D4Science,ou=Organizations,dc=d4science,dc=org
	 * @param ctx
	 * @throws NamingException
	 */
	private void createUsersOrganizationalUnit(DirContext ctx) throws NamingException 	{
		if (!checkIfLDAPOrganizationalUnitExists(ctx, "ou=Organizations,dc=d4science,dc=org")) {
			Attributes attributes = new BasicAttributes();
			Attribute objectClass = new BasicAttribute("objectClass");
			objectClass.add("organizationalUnit");
			attributes.put(objectClass);

			Attribute description = new BasicAttribute("description");
			description.add("Where to find users");		
			attributes.put(description);		
			//	private static final String USER_CONTEXT = ",";
			ctx.createSubcontext("ou=Organizations,dc=d4science,dc=org", attributes);

			attributes = new BasicAttributes();
			objectClass = new BasicAttribute("objectClass");
			objectClass.add("Organization");
			attributes.put(objectClass);
			description.add("Default Organization");		
			ctx.createSubcontext("o=D4Science,ou=Organizations,dc=d4science,dc=org", attributes);	

			attributes = new BasicAttributes();
			objectClass = new BasicAttribute("objectClass");
			objectClass.add("organizationalUnit");
			attributes.put(objectClass);
			description.add("People Org Unit");		
			ctx.createSubcontext("ou=People,o=D4Science,ou=Organizations,dc=d4science,dc=org", attributes);	
		}
		else
			_log.info("ou=Organizations,dc=d4science,dc=org already present, skip");
	}

	/**
	 * 
	 * @param ctx
	 * @param root
	 * @throws NamingException
	 * @throws SystemException
	 */
	private void updateGroups(DirContext ctx, Organization root) throws NamingException, SystemException {
		String subCtx = getOrgSubContext(root.getName());
		if (!checkIfLDAPOrganizationalUnitExists(ctx, subCtx))
			createOrganizationalUnit(ctx, subCtx);
		for (Organization org : root.getSuborganizations()) {
			String orgSubCtx = "ou="+org.getName()+","+subCtx;
			if (!checkIfLDAPOrganizationalUnitExists(ctx, orgSubCtx))
				createOrganizationalUnit(ctx, orgSubCtx);
			for (Organization vre : org.getSuborganizations()) {
				String vreSubCtx = "cn="+vre.getName()+","+orgSubCtx;
				if (!checkIfLDAPGroupExists(ctx, vreSubCtx))
					createGroupVRE(ctx, vreSubCtx, vre.getName());
				//update the list of users in such VRE
				updateUsersInGroup(ctx, vreSubCtx, vre);
			}
		}			
	}
	/**
	 * 
	 * @param ctx
	 * @param vreSubCtx
	 * @param vre
	 * @throws NamingException
	 * @throws SystemException
	 */
	private void updateUsersInGroup(DirContext ctx, String vreSubCtx, Organization vre) throws NamingException, SystemException {
		List<User> users = UserLocalServiceUtil.getOrganizationUsers(vre.getOrganizationId());
		for (User userObj : users) {
			String user = userObj.getScreenName();
			try {				
				Attribute memberUid = new BasicAttribute("memberUid");
				memberUid.add(user);
				Attributes attributes = new BasicAttributes();
				attributes.put(memberUid);					
				ctx.modifyAttributes(vreSubCtx, DirContext.ADD_ATTRIBUTE, attributes);
				_log.info("Adding user: " + user);
			}
			catch (javax.naming.directory.AttributeInUseException ex) {
				_log.trace("Not adding already existing user: " + user);
			}

		}

	}

	private void exportSingleUsers(DirContext ctx, Properties env, List<User> users) throws NamingException {
		for (User user : users) {
			updateUserInLDAP(user.getScreenName(), user.getFirstName(), user.getLastName(), user.getFullName(), user.getEmailAddress(), "{SHA}"+user.getPassword(), ctx, filter);
			//_log.debug("Updated " + user.getScreenName());
		}			
		_log.debug("LDAP Users Sync cycle done");
		if (! users.isEmpty())
			_log.info("LDAP Users Sync Completed OK!");
		else
			_log.warn("LDAP Users Sync cycle skipped this time");
	}
	/**
	 * 
	 * @param ctx
	 * @param subContext
	 * @throws NamingException
	 */
	private void createOrganizationalUnit(DirContext ctx, String subContext) throws NamingException {
		Attributes attributes = new BasicAttributes();
		Attribute objectClass = new BasicAttribute("objectClass");
		objectClass.add("organizationalUnit");
		attributes.put(objectClass);

		Attribute description = new BasicAttribute("description");
		description.add("Liferay Organization");		
		attributes.put(description);		
		ctx.createSubcontext(subContext, attributes);
		_log.info("Added " + subContext);
	}
	/**
	 * 
	 * @param ctx
	 * @param subContext
	 * @param vreName
	 * @throws NamingException
	 */
	private void createGroupVRE(DirContext ctx, String subContext, String vreName) throws NamingException {		
		Attributes attributes = new BasicAttributes();

		Attribute objectClass = new BasicAttribute("objectClass");
		objectClass.add("top");
		objectClass.add("posixGroup");
		//		objectClass.add("researchProject");
		//		objectClass.add("groupOfMembers");		
		attributes.put(objectClass);

		Attribute cn = new BasicAttribute("cn");
		cn.add(vreName);
		attributes.put(cn);

		Attribute gidNumber = new BasicAttribute("gidNumber");
		gidNumber.add(String.valueOf(getRandomPOSIXidentifier()));
		attributes.put(gidNumber);

		ctx.createSubcontext(subContext, attributes);
		_log.info("Added " + subContext);
	}

	private String getOrgSubContext(String orgName) {
		return "ou="+orgName+",dc=d4science,dc=org";
	}
	/**
	 * 
	 * @param ctx
	 * @param orgSubctx
	 * @return true if exists
	 */
	private boolean checkIfLDAPOrganizationalUnitExists(DirContext ctx, String orgSubctx) {
		SearchControls ctls = new SearchControls();
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<SearchResult> answer;
		try {
			answer = ctx.search(orgSubctx, LDAP_ORG_FILTER, ctls);
		} catch (NamingException e) {
			_log.debug("not found in LDAP (will add it): Organization: " + orgSubctx);
			return false;
		}
		boolean toReturn = answer.hasMoreElements();
		_log.debug("Organization: " + orgSubctx + " exists? " + toReturn);
		return toReturn;
	}
	/**
	 * 
	 * @param ctx
	 * @param groupSubctx
	 * @return true if exists
	 */
	private boolean checkIfLDAPGroupExists(DirContext ctx, String groupSubctx) {
		SearchControls ctls = new SearchControls();
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<SearchResult> answer;
		try {
			answer = ctx.search(groupSubctx, LDAP_GROUP_FILTER, ctls);
		} catch (NamingException e) {
			_log.debug("not found in LDAP (will add it): Group: " + groupSubctx);
			return false;
		}
		boolean toReturn = answer.hasMoreElements();
		_log.debug("Group: " + groupSubctx + " exists? " + toReturn);
		return toReturn;
	}
	/**
	 * 
	 * @param username
	 * @return the single user subContext
	 */
	private String getSubContext(String username) {
		return "uid="+username+USER_CONTEXT;
	}
	/**
	 * 
	 * @param username
	 * @param ctx
	 * @param filter
	* @return true if exists
	 */
	private boolean checkIfLDAPUserExists(String username, DirContext ctx, String filter) {
		SearchControls ctls = new SearchControls();
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<SearchResult> answer;
		try {
			answer = ctx.search(getSubContext(username), filter, ctls);
		} catch (NamingException e) {
			_log.info("user: " + username + " not found in LDAP, trying to export it");
			return false;
		}
		return answer.hasMoreElements();
	}
	/**
	 * 
	 * @param username
	 * @param name
	 * @param lastName
	 * @param email
	 * @param passwd
	 * @param ctx
	 * @throws NamingException
	 */
	private void updateUserInLDAP(String username, String name, String lastName, String fullName, String email, String passwd, DirContext ctx, String filter) throws NamingException {
		Attributes attributes=new BasicAttributes();
		Attribute objectClass=new BasicAttribute("objectClass");
		objectClass.add("inetOrgPerson");
		objectClass.add("posixAccount");
		attributes.put(objectClass);

		//the main ldap server uses 'givenName' for the First name, 'cn' for "first name last name', 'sn' for the last name
		Attribute givenName = new BasicAttribute("givenName");
		Attribute cn = new BasicAttribute("cn");
		Attribute sn = new BasicAttribute("sn");		
		Attribute mail = new BasicAttribute("mail");
		Attribute userPassword = new BasicAttribute("userPassword");
		Attribute gidNumber = new BasicAttribute("gidNumber");
		Attribute homeDirectory = new BasicAttribute("homeDirectory");
	

		givenName.add(name);
		cn.add(fullName);
		sn.add(lastName);		
		mail.add(email);
		userPassword.add(passwd);
		gidNumber.add(DEFAULT_GID_NUMBER);
		homeDirectory.add("/home/"+username);
		
		attributes.put(givenName);
		attributes.put(cn);
		attributes.put(sn);		
		attributes.put(mail);
		attributes.put(userPassword);
		attributes.put(gidNumber);
		attributes.put(homeDirectory);
	


		if (checkIfLDAPUserExists(username, ctx, filter)) {
			//_log.debug("User " +  username + " already exists, replacing attributes");
			ctx.modifyAttributes(getSubContext(username), DirContext.REPLACE_ATTRIBUTE, attributes);
			_log.debug("Updated attributes for already existing user with uid=" +  username);
		}
		else {
			int n = getRandomPOSIXidentifier();
			while (checkIfPosixUidNumberExists(ctx, n)) {
				_log.info("Found collision on UidNumber="+n);
				n = getRandomPOSIXidentifier();
				_log.info("Trying newone="+n);
			}
			Attribute uidNumber = new BasicAttribute("uidNumber");
			attributes.put(uidNumber);
			uidNumber.add(String.valueOf(n));
			ctx.createSubcontext(getSubContext(username),attributes);
			_log.debug("New User Found with uid=" +  username + " created");
		}
	}
	/**
	 * 
	 * @return the whole list of users 
	 */
	private List<User> getAllLiferayUsers() {
		String infraName = PortalContext.getConfiguration().getInfrastructureName();		
		_log.info("TRY Reading non chached users belonging to: /" + infraName);

		List<User> toReturn = new ArrayList<User>();
		Organization rootInfra;
		try {
			CacheRegistryUtil.clear(); //needed to avoid cache use by liferay API
			rootInfra = OrganizationLocalServiceUtil.getOrganization(OrganizationsUtil.getCompany().getCompanyId(), infraName);
			toReturn = UserLocalServiceUtil.getOrganizationUsers(rootInfra.getOrganizationId());
		} catch (PortalException | SystemException e) {
			_log.error("Error during LDAP Sync, could not retrieve users from LR DB: " + e.getMessage());
		}
		return toReturn;
	}
	/**
	 * 
	 * @return an integer between 1000 and 2147483647
	 */
	private int getRandomPOSIXidentifier() {
		final int Low = 1000;
		final int High = 2147483647;
		Random r = new Random();
		int toReturn = r.nextInt(High-Low) + Low;
		return toReturn;
	}

	
	private boolean checkIfPosixUidNumberExists(DirContext ctx, int numberToCheck) {
		SearchControls ctls = new SearchControls();
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<SearchResult> answer;
		try {
			answer = ctx.search("ou=People,o=D4Science,ou=Organizations,dc=d4science,dc=org", "(uidNumber="+numberToCheck+")", ctls);
		} catch (NamingException e) {
			_log.info("exception");
			return false;
		}

		boolean toReturn = answer.hasMoreElements();
		_log.info("return " + toReturn);
		return toReturn;
	}
}
