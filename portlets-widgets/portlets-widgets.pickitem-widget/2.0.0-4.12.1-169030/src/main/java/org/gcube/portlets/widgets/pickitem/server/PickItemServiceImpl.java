package org.gcube.portlets.widgets.pickitem.server;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.portlets.widgets.pickitem.client.bundle.CssAndImages;
import org.gcube.portlets.widgets.pickitem.client.rpc.PickItemService;
import org.gcube.portlets.widgets.pickitem.shared.ItemBean;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.util.ManagementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.model.Team;
import com.liferay.portal.model.User;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

@SuppressWarnings("serial")
public class PickItemServiceImpl extends RemoteServiceServlet implements PickItemService {
	private static final Logger _log = LoggerFactory.getLogger(PickItemServiceImpl.class);
	//this map is used as cache containing the association between a context and a GroupId so that we don't ask everytime. 
	private static HashMap<String, Long> contextToGroupIdMap = new HashMap<>();
	/**
	 * 
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (com.liferay.portal.kernel.bean.BeanLocatorException ex) {			
			_log.trace("Development Mode ON");
			return false;
		}			
	}

	private long getGroupIdFromContext(String context) {
		if (contextToGroupIdMap.containsKey(context))
			return contextToGroupIdMap.get(context);
		try {
			long groupId = new LiferayGroupManager().getGroupIdFromInfrastructureScope(context);
			contextToGroupIdMap.put(context, new Long(groupId));
			return groupId;
		} catch (IllegalArgumentException | UserManagementSystemException | GroupRetrievalFault e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	@Override
	public ArrayList<ItemBean> searchEntities(String keyword, String context) {
		ArrayList<ItemBean> toReturn = new ArrayList<>();
		if (isWithinPortal()) {
			OrderByComparator comparator = OrderByComparatorFactoryUtil.create("User_", "screenname", true);
			LinkedHashMap<String, Object> params = new LinkedHashMap<>();
			long currentGroupId = getGroupIdFromContext(context);
			params.put("usersGroups", currentGroupId); 
						try {
				_log.debug("Searching " + keyword + " on " + context);
				List<User> usersByGroup = UserLocalServiceUtil.search(
						ManagementUtils.getCompany().getCompanyId(), keyword, 0, params, QueryUtil.ALL_POS, QueryUtil.ALL_POS, comparator);
				for (User user : usersByGroup) {
					toReturn.add(new ItemBean(""+user.getUserId(), user.getScreenName(), user.getFullName(), getUserImagePortraitUrlLocal(user.getScreenName())));
				}
				
				OrderByComparator teamComparator = OrderByComparatorFactoryUtil.create("team", "name", true);
				List<Team> teams = TeamLocalServiceUtil.search(currentGroupId, keyword, "", null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, teamComparator);
				for (Team team : teams) {
					ItemBean iBean = new ItemBean(team.getTeamId()+"", team.getName());
					iBean.setItemGroup(true);
					toReturn.add(iBean);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else { //development
			for (int i = 0; i < 10; i++) {
				toReturn.add(new ItemBean("id", "andrea.rossi", "Andrea Rossi", "email"));
				if (i % 2 == 0)
					toReturn.add(new ItemBean(""+i, "username"+i, "userGetFullname()"+i, "user.getEmail()"+i));
				else {
					ItemBean groupBean = new ItemBean("idG"+i, "Group "+i);
					groupBean.setItemGroup(true);
					toReturn.add(new ItemBean(""+i, "Group "+i));
				}
			}		
		}
		return toReturn;
	}
	/**
	 * this method is needed because user images portrait change id depending on the portal instance
	 * e.g. a post made from iMarine portal would not show the avatarIMage in D4Science.org
	 * @param screenname
	 * @return the url of the image portrait for this portal instance
	 */
	private String getUserImagePortraitUrlLocal(String screenName) {
		if (! isWithinPortal()) {
			return "";
		}
		String thumbnailURL = "";
		try {
			thumbnailURL = new LiferayUserManager().getUserByUsername(screenName).getUserAvatarURL();
		} catch (UserManagementSystemException | UserRetrievalFault e) {
			e.printStackTrace();
		}
		return thumbnailURL;
	}

}
