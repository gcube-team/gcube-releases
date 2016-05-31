package org.gcube.portlets.user.topics.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portlets.user.topics.client.TopicService;
import org.gcube.portlets.user.topics.shared.HashTagAndOccurrence;
import org.gcube.portlets.user.topics.shared.HashtagsWrapper;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;

/**
 * @author Massimiliano Assante, ISTI-CNR
 */
@SuppressWarnings("serial")
public class TopicServiceImpl extends RemoteServiceServlet implements TopicService {
	private static final Logger _log = LoggerFactory.getLogger(TopicServiceImpl.class);

	public static final String TEST_USER = "test.user";
	/**
	 * The Cassandra store interface
	 */
	private DatabookStore store;
	/**
	 * connect to cassandra at startup
	 */
	public void init() {
		store = new DBCassandraAstyanaxImpl();	
	}

	public void destroy() {
		store.closeConnection();
	}
	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			_log.warn("USER IS NULL setting test.user and Running OUTSIDE PORTAL");
			user = getDevelopmentUser();
			SessionManager.getInstance().getASLSession(sessionID, user).setScope("/gcube/devsec/devVRE");
		}		
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	/**
	 * when packaging test will fail if the user is not set to test.user
	 * @return .
	 */
	public String getDevelopmentUser() {
		String user = TEST_USER;
		//user = "massimiliano.assante";
		return user;
	}
	
	/**
	 * return the top 10 hashtag with max occurrence
	 */
	@Override
	public HashtagsWrapper getHashtags() {
		ArrayList<String> hashtagsChart = new ArrayList<>();
		ASLSession session = getASLSession();
		
		String userName = session.getUsername();
		boolean isInfrastructure = isInfrastructureScope();
		try {
			//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
			//this check just return nothing if that happens
			if (userName.compareTo("test.user") == 0) {
				_log.debug("Found " + userName + " returning nothing");
				return null;
			}
			ArrayList<HashTagAndOccurrence> toSort = new ArrayList<HashTagAndOccurrence>();
			/**
			 * this handles the case where the portlet is deployed outside of VREs (regular)
			 */

			if (isInfrastructure) {
				_log.debug("****** retrieving hashtags for user VREs");
				User currUser = OrganizationsUtil.validateUser(userName);
				GroupManager gm = new LiferayGroupManager();
				for (Organization org : currUser.getOrganizations()) {					
					if (gm.isVRE(org.getOrganizationId()+"")) {
						String vreid = gm.getScope(""+org.getOrganizationId()); //get the scope 
						Map<String, Integer> map = store.getVREHashtagsWithOccurrence(vreid);
						for (String hashtag : map.keySet()) {
							toSort.add(new HashTagAndOccurrence(hashtag, map.get(hashtag)));
						}
					}
				}				
			}
			//else must be in a VRE scope
			else {
				String scope = session.getScope();
				_log.debug("****** retrieving hashtags for scope " + scope);
				Map<String, Integer> map = store.getVREHashtagsWithOccurrence(scope);
				for (String hashtag : map.keySet()) {
					toSort.add(new HashTagAndOccurrence(hashtag, map.get(hashtag)));
				}
			}

			Collections.sort(toSort, Collections.reverseOrder());
			int i = 0;
			for (HashTagAndOccurrence wrapper : toSort) {
				String hashtag = wrapper.getHashtag();

				String href="\"?"+
						new String(Base64.encodeBase64(GCubeSocialNetworking.HASHTAG_OID.getBytes()))+"="+
						new String(Base64.encodeBase64(hashtag.getBytes()))+"\"";
				String hashtagLink = "<a class=\"topiclink\" href=" + href + ">"+hashtag+"</a>";
				hashtagsChart.add(hashtagLink);
				i++;
				if (i >= 10)
					break;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return new HashtagsWrapper(isInfrastructure, hashtagsChart);
	}


	/**
	 * Indicates whether the scope is the whole infrastructure.
	 * @return <code>true</code> if it is, <code>false</code> otherwise.
	 */
	private boolean isInfrastructureScope() {
		boolean toReturn = false;
		try {
			ScopeBean scope = new ScopeBean(getASLSession().getScope());
			toReturn = scope.is(Type.INFRASTRUCTURE);
			return toReturn;
		}
		catch (NullPointerException e) {
			_log.error("NullPointerException in isInfrastructureScope returning false");
			return false;
		}		
	}

}
