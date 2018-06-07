package org.gcube.portal.plugins.util;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;
import java.util.Random;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.plugins.bean.LDAPInfo;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class LDAPUtil {
	private static Log _log = LogFactoryUtil.getLog(LDAPUtil.class);
	public  static final String LDAP_SERVER_NAME = "LDAPServer";
	public  static final String LDAP_SERVER_FILTER_NAME = "filter";
	public  static final String LDAP_SERVER_PRINCPAL_NAME = "ldapPrincipal";

	public static final String LDAP_ORG_FILTER = "(objectClass=organizationalUnit)";
	public static final String LDAP_GROUP_FILTER = "(objectClass=posixGroup)";
	public static final String USER_CONTEXT = ",ou=People,o=D4Science,ou=Organizations,dc=d4science,dc=org";
	public static final String DEFAULT_GID_NUMBER = "1000";
	/**
	 * 
	 * @param ctx
	 * @param groupSubctx
	 * @return true if exists
	 */
	public static boolean checkIfLDAPGroupExists(DirContext ctx, String groupSubctx) {
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
	 * @param ctx
	 * @param subContext
	 * @param vreName
	 * @throws NamingException
	 */
	public static void createGroupVRE(DirContext ctx, String subContext, String vreName) throws NamingException {		
		Attributes attributes = new BasicAttributes();

		Attribute objectClass = new BasicAttribute("objectClass");
		objectClass.add("top");
		objectClass.add("posixGroup");
		attributes.put(objectClass);

		Attribute cn = new BasicAttribute("cn");
		cn.add(vreName);
		attributes.put(cn);

		Attribute gidNumber = new BasicAttribute("gidNumber");
		gidNumber.add(String.valueOf(getRandomPOSIXidentifier()));
		attributes.put(gidNumber);

		ctx.createSubcontext(subContext, attributes);
		_log.info("createGroupVRE Added " + subContext);
	}
	/**
	 * ask to the Information System
	 * @return
	 */
	public static LDAPInfo getLDAPCoordinates() {
		LDAPInfo toReturn = new LDAPInfo();
		@SuppressWarnings("deprecation")
		String portalName = PortalContext.getPortalInstanceName();

		PortalContext context = PortalContext.getConfiguration();	
		String scope = "/" + context.getInfrastructureName();
		ScopeProvider.instance.set(scope);

		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq 'Portal'");
		query.addCondition("$resource/Profile/Name/text() eq '" + portalName + "'");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> list = client.submit(query);
		if (list == null || list.isEmpty()) {
			_log.error("Could not find any Service endpoint registred in the infrastructure for this portal: " + portalName);
		}
		else  if (list.size() > 1) {
			_log.warn("Found more than one Service endpoint registred in the infrastructure for this portal: " + portalName);
		}
		else {
			for (ServiceEndpoint res : list) {
				Group<AccessPoint> apGroup =  res.profile().accessPoints();
				AccessPoint[] accessPoints = (AccessPoint[]) apGroup.toArray(new AccessPoint[apGroup.size()]);
				for (int i = 0; i < accessPoints.length; i++) {
					if (accessPoints[i].name().compareTo(LDAP_SERVER_NAME) == 0) {
						_log.debug("Found credentials for " + LDAP_SERVER_NAME);
						AccessPoint found = accessPoints[i];
						String ldapUrl = found.address();
						toReturn.setLdapUrl(ldapUrl);
						String encrPassword = found.password();						
						try {
							String ldapPassword = StringEncrypter.getEncrypter().decrypt( encrPassword);
							toReturn.setLdapPassword(ldapPassword);
						} catch (Exception e) {
							_log.error("Something went wrong while decrypting password for " + LDAP_SERVER_NAME);
							e.printStackTrace();
						}
						Group<Property> propGroup =  found.properties();
						Property[] props = (Property[]) propGroup.toArray(new Property[propGroup.size()]);
						for (int j = 0; j < props.length; j++) {
							if (props[j].name().compareTo(LDAP_SERVER_FILTER_NAME) == 0) {
								_log.debug("\tFound properties of " + LDAP_SERVER_FILTER_NAME);
								String encrValue = props[j].value();			
								System.out.println("Filter encrypted = " + encrValue);
								try {
									String filter = StringEncrypter.getEncrypter().decrypt(encrValue);
									toReturn.setFilter(filter);
								} catch (Exception e) {
									_log.error("Something went wrong while decrypting value for " + LDAP_SERVER_FILTER_NAME);
									e.printStackTrace();
								}
							}
							else if (props[j].name().compareTo(LDAP_SERVER_PRINCPAL_NAME) == 0) {
								_log.debug("\tFound properties of " + LDAP_SERVER_PRINCPAL_NAME);
								String encrValue = props[j].value();						
								try {
									String principal = StringEncrypter.getEncrypter().decrypt(encrValue);
									toReturn.setPrincipal(principal);
								} catch (Exception e) {
									_log.error("Something went wrong while decrypting value for " + LDAP_SERVER_PRINCPAL_NAME);
									e.printStackTrace();
								}
							}
						}

					}
				}

			}
		}
		return toReturn;
	}
	/**
	 * 
	 * @return the Liferay mapped as Root Organization
	 */
	public static GCubeGroup getRootVO() {
		try {
			GroupManager gm = new LiferayGroupManager();
			String rootVoName = gm.getRootVOName();
			_log.debug("Root organization name found: " + rootVoName);
			return gm.getGroup(gm.getGroupIdFromInfrastructureScope("/"+rootVoName));
		}
		catch (Exception e) {
			_log.error("There were problems retrieving root VO group", e);
		}
		_log.error("Could not find any root organization");
		return null;
	}
	/***
	 * 
	 * @param orgName
	 * @return
	 */
	public static String getOrgSubContext(String orgName) {
		return "ou="+orgName+",dc=d4science,dc=org";
	}
	/**
	 * 
	 * @return an integer between 1000 and 2147483647
	 */
	public static int getRandomPOSIXidentifier() {
		final int Low = 1000;
		final int High = 2147483647;
		Random r = new Random();
		int toReturn = r.nextInt(High-Low) + Low;
		return toReturn;
	}
}
