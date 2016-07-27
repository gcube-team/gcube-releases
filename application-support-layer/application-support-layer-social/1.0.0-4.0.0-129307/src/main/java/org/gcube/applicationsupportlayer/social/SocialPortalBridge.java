package org.gcube.applicationsupportlayer.social;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.applicationsupportlayer.social.ex.ApplicationProfileNotFoundException;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingUser;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.ApplicationProfile;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 * superclass for notifications, posting news and so on
 */
public class SocialPortalBridge {

	private static final Logger _log = LoggerFactory.getLogger(SocialPortalBridge.class);
	
	protected String currScope;
	protected String currGroupName;
	protected SocialNetworkingUser currUser;
	protected ApplicationProfile applicationProfile;
	//unique instance
	private static DatabookStore store;
	/**
	 * 
	 * @param scope the current scope
	 * @param currUser an instance of {@link SocialNetworkingUser} filled with the required user data
	 */
	public SocialPortalBridge(String scope, SocialNetworkingUser currUser) { 
		this.currScope = scope;
		this.currGroupName = extractGroupNameFromScope(currScope);
		this.currUser = currUser;
		this.applicationProfile = null;
		_log.debug("ASLSocial instanciated with current scope=" + currScope + " currentGroup name="+currGroupName);
	}
	/**
	 * 
	 * @param scope the current scope
	 * @param currUser an instance of {@link SocialNetworkingUser} filled with the required user data
	 * @param portletClassName your application unique identifier registered in the infrastructure
	 */
	public SocialPortalBridge(String scope, SocialNetworkingUser currUser, String portletClassName) { 
		this(scope, currUser);
		this.applicationProfile = getProfileFromInfrastrucure(portletClassName);
	}
	
	/**
	 * 
	 * @return the unique instance of the store
	 */
	public static synchronized DatabookStore getStoreInstance() {
		if (store == null) {
			store = new DBCassandraAstyanaxImpl();
		}
		return store;
	}

	protected String getScopeByVREId(long vreid) {
		GroupManager gm = new LiferayGroupManager();
		try {
			return gm.getInfrastructureScope(vreid);
		} catch (Exception e) {
			_log.error("Could not find a scope for this VREid: " + vreid);
			return null;
		} 
	}
	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	protected String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
	/**
	 * @return the applicationProfile profile among the ones available in the infrastructure
	 */
	public ApplicationProfile getApplicationProfile() {
		return applicationProfile;
	}
	/**
	 * this method looks up the applicationProfile profile among the ones available in the infrastructure
	 * @param portletClassName your servlet class name will be used ad unique identifier for your applicationProfile
	 * @return the applicationProfile profile
	 */
	private ApplicationProfile getProfileFromInfrastrucure(String portletClassName) {
		ScopeBean scope =  new ScopeBean(currScope);
		_log.debug("Trying to fetch applicationProfile profile from the infrastructure for " + portletClassName + " scope: " +  scope);
		try {
			ApplicationProfile toReturn = new ApplicationProfile();
			Query q = new QueryBox("for $profile in collection('/db/Profiles/GenericResource')//Resource " +
					"where $profile/Profile/SecondaryType/string() eq 'ApplicationProfile' and  $profile/Profile/Body/AppId/string() " +
					" eq '" + portletClassName + "'" +
					"return $profile");
			


			String currScope = ScopeProvider.instance.get();
			String scopeToQuery = PortalContext.getConfiguration().getInfrastructureName();
			ScopeProvider.instance.set("/"+scopeToQuery);
			 
			DiscoveryClient<String> client = client();
		 	List<String> appProfile = client.submit(q);
			
			if (appProfile == null || appProfile.size() == 0) 
				throw new ApplicationProfileNotFoundException("Your applicationProfile is not registered in the infrastructure");
			else {
				String elem = appProfile.get(0);
				DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);
				
				List<String> currValue = null;
				currValue = helper.evaluate("/Resource/Profile/Name/text()");
				if (currValue != null && currValue.size() > 0) {
					toReturn.setName(currValue.get(0));
				} 
				else throw new ApplicationProfileNotFoundException("Your applicationProfile NAME was not found in the profile");

				currValue = helper.evaluate("/Resource/Profile/Description/text()");
				if (currValue != null && currValue.size() > 0) {
					toReturn.setDescription(currValue.get(0));
				} 
				else _log.warn("No Description exists for " + toReturn.getName());

				currValue = helper.evaluate("/Resource/Profile/Body/AppId/text()");
				if (currValue != null && currValue.size() > 0) {
					toReturn.setKey(currValue.get(0));
				}
				else throw new ApplicationProfileNotFoundException("Your applicationProfile ID n was not found in the profile, consider adding <AppId> element in <Body>");

				currValue = helper.evaluate("/Resource/Profile/Body/ThumbnailURL/text()");
				if (currValue != null && currValue.size() > 0) {
					toReturn.setImageUrl(currValue.get(0));
				}
				else throw new ApplicationProfileNotFoundException("Your applicationProfile Image Url was not found in the profile, consider adding <ThumbnailURL> element in <Body>");
				currValue = helper.evaluate("/Resource/Profile/Body/EndPoint/Scope/text()");
				if (currValue != null && currValue.size() > 0) {
					List<String> scopes = currValue;
					boolean foundUrl = false;
					for (int i = 0; i < scopes.size(); i++) {
						if (currValue.get(i).trim().compareTo(scope.toString()) == 0) {								
							toReturn.setUrl(helper.evaluate("/Resource/Profile/Body/EndPoint/URL/text()").get(i));
							toReturn.setScope(scope.toString());
							foundUrl = true;
							break;
						}						
					}
					if (! foundUrl)
						throw new ApplicationProfileNotFoundException("Your applicationProfile URL was not found in the profile for Scope: " + scope.toString());
				}
				else throw new ApplicationProfileNotFoundException("Your applicationProfile EndPoint was not found in the profile, consider adding <EndPoint><Scope> element in <Body>");
				_log.debug("Returning " + toReturn);
				ScopeProvider.instance.set(currScope);
				return toReturn;
			}

		} catch (Exception e) {
			_log.error("Error while trying to fetch applicationProfile profile from the infrastructure");
			e.printStackTrace();
			return null;
		} 

	}
	
	private String extractGroupNameFromScope(String scope) throws IllegalArgumentException {
		if (scope.indexOf('/') > -1) {
			String[] splits = scope.split("/");
			String groupName = splits[splits.length-1];
			return groupName;
		}
		else {
			throw new IllegalArgumentException("Scope " + scope + " is not a vaild scope, it should start with '/' ");
		}
	}
}
