package org.gcube.portlets.user.accountingdashboard.server.accounting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.accounting.accounting.summary.access.impl.ContextTreeProvider;
import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vomanagement.usermanagement.model.VirtualGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.VirtualHost;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.VirtualHostLocalServiceUtil;

public class PortalContextTreeProvider implements ContextTreeProvider {

	private static GroupManager groupsManager;

	private static Logger log = LoggerFactory.getLogger(PortalContextTreeProvider.class);

	static {
		groupsManager = new LiferayGroupManager();
	}

	@Override
	public ScopeDescriptor getTree(Object context) throws Exception {
		if (context == null)
			throw new Exception("Unable to get tree, Request is null.");
		if (!(context instanceof HttpServletRequest))
			throw new Exception("Invalid request object : " + context);
		HttpServletRequest request = (HttpServletRequest) context;

		// PARSE TREE
		LinkedHashMap<VRECategory, ArrayList<VRE>> gatewayTree = getPortalSitesMappedToVRE(request);

		log.debug("Parsing tree from gateway. Size {} ", gatewayTree.size());

		LinkedList<ScopeDescriptor> rootChildren = new LinkedList<>();
		for (Entry<VRECategory, ArrayList<VRE>> entry : gatewayTree.entrySet()) {
			ScopeDescriptor rootChild = new ScopeDescriptor(entry.getKey().name, entry.getKey().categoryID + "");
			for (VRE vre : entry.getValue())
				rootChild.getChildren().add(new ScopeDescriptor(vre.name, vre.scope));
			rootChildren.add(rootChild);
		}

		Group rootGroup = getSiteFromServletRequest(request);
		ScopeDescriptor root = new ScopeDescriptor(rootGroup.getDescriptiveName(), rootGroup.getGroupId() + "");
		root.setChildren(rootChildren);
		log.debug("TREE IS {} ", root);

		return root;
	}

	/**
	 *
	 * @return the Virtual groups with their VREs in the order estabilished in
	 *         the LR Control Panel
	 * @throws SystemException
	 * @throws PortalException
	 */
	private LinkedHashMap<VRECategory, ArrayList<VRE>> getPortalSitesMappedToVRE(HttpServletRequest request)
			throws Exception {

		LinkedHashMap<VRECategory, ArrayList<VRE>> toReturn = new LinkedHashMap<VRECategory, ArrayList<VRE>>();

		long currentSiteGroupId = getSiteFromServletRequest(request).getGroupId();
		List<VirtualGroup> currentSiteVGroups = groupsManager.getVirtualGroups(currentSiteGroupId);

		for (VirtualGroup vg : currentSiteVGroups) {
			ArrayList<VRE> toCreate = new ArrayList<VRE>();
			VRECategory cat = new VRECategory(1L, vg.getName(), vg.getDescription());
			toReturn.put(cat, toCreate);
		}

		GCubeGroup rootGroupVO = groupsManager.getRootVO();

		try {
			log.debug("root: " + rootGroupVO.getGroupName());
		} catch (NullPointerException e) {
			log.error(
					"Cannot find root organziation, please check gcube-data.properties file in $CATALINA_HOME/conf folder, unless your installing the Bundle");
			return toReturn;
		}
		PortalContext pContext = PortalContext.getConfiguration();

		@SuppressWarnings("unused")
		List<GCubeGroup> currUserGroups = new ArrayList<GCubeGroup>();
		GCubeUser currUser = pContext.getCurrentUser(request);
		if (currUser != null) {
			currUserGroups = groupsManager.listGroupsByUser(currUser.getUserId());
		}

		// for each root sub organizations (VO)
		for (GCubeGroup vOrg : rootGroupVO.getChildren()) {
			for (GCubeGroup vreSite : vOrg.getChildren()) {
				long vreID = vreSite.getGroupId();
				String vreName = vreSite.getGroupName();
				String vreDescription = vreSite.getDescription();

				long logoId = vreSite.getLogoId();
				@SuppressWarnings("unused")
				String vreLogoURL = groupsManager.getGroupLogoURL(logoId);
				String infraScope = groupsManager.getInfrastructureScope(vreSite.getGroupId());
				String friendlyURL = GCubePortalConstants.PREFIX_GROUP_URL + vreSite.getFriendlyURL();

				List<VirtualGroup> vreGroups = groupsManager.getVirtualGroups(vreID);
				for (VirtualGroup vreGroup : vreGroups) {
					for (VRECategory vre : toReturn.keySet()) {
						if (vre.getName().compareTo(vreGroup.getName()) == 0) {
							ArrayList<VRE> toUpdate = toReturn.get(vre);
							// UserBelonging belongs =
							// UserBelonging.NOT_BELONGING;
							// VRE toAdd = new VRE(vreID,vreName,
							// vreDescription, vreLogoURL, groupName,
							// friendlyURL, belongs,
							// getVREMembershipType(vreSite.getMembershipType()));
							VRE toAdd = new VRE(vreName, vreDescription, vreID, friendlyURL, infraScope);
							// if
							// (GroupLocalServiceUtil.getGroup(vreID).getPublicLayoutsPageCount()
							// > 0) {
							// String publicURL =
							// PREFIX_PUBLIC_URL+vreSite.getFriendlyURL();
							// toAdd.setPublicURL(publicURL);
							// }
							// if (currUser != null) {
							// //check if the user belongs to it
							// if (currUserGroups.contains(vreSite)) {
							// toAdd.setUserBelonging(UserBelonging.BELONGING);
							// }
							// else if (checkPending(currUser.getUsername(),
							// vreSite.getGroupId()))
							// toAdd.setUserBelonging(UserBelonging.PENDING);
							// }
							toUpdate.add(toAdd);
						}
					}
				}
			}
		}

		// sort the vres in the groups
		for (VRECategory cat : toReturn.keySet()) {
			ArrayList<VRE> toSort = toReturn.get(cat);
			Collections.sort(toSort);
		}
		return toReturn;
	}

	private Group getSiteFromServletRequest(final HttpServletRequest request) throws PortalException, SystemException {
		String serverName = request.getServerName();
		log.debug("currentHost is " + serverName);
		Group site = null;
		List<VirtualHost> vHosts = VirtualHostLocalServiceUtil.getVirtualHosts(0,
				VirtualHostLocalServiceUtil.getVirtualHostsCount());
		for (VirtualHost virtualHost : vHosts) {
			log.debug("Found " + virtualHost.getHostname());
			if (virtualHost.getHostname().compareTo("localhost") != 0 && virtualHost.getLayoutSetId() != 0
					&& virtualHost.getHostname().compareTo(serverName) == 0) {
				long layoutSetId = virtualHost.getLayoutSetId();
				site = LayoutSetLocalServiceUtil.getLayoutSet(layoutSetId).getGroup();
				log.debug("Found match! Your site is " + site.getName());
				return site;
			}
		}
		return null;
	}

	private class VRECategory {
		private long categoryID;
		private String name;
		private String description;

		public VRECategory(long categoryID, String name, String description) {
			super();
			this.categoryID = categoryID;
			this.name = name;
			this.description = description;
		}

		@SuppressWarnings("unused")
		public long getCategoryID() {
			return categoryID;
		}

		@SuppressWarnings("unused")
		public String getDescription() {
			return description;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "VRECategory [categoryID=" + categoryID + ", name=" + name + ", description=" + description + "]";
		}

	}

	private class VRE implements Comparable<VRE> {
		private String name;
		private String description;
		private long id;
		private String url;
		private String scope;

		public VRE(String name, String description, long id, String url, String scope) {
			super();
			this.name = name;
			this.description = description;
			this.id = id;
			this.url = url;
			this.scope = scope;
		}

		@SuppressWarnings("unused")
		public String getDescription() {
			return description;
		}

		@SuppressWarnings("unused")
		public long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		@SuppressWarnings("unused")
		public String getScope() {
			return scope;
		}

		@SuppressWarnings("unused")
		public String getUrl() {
			return url;
		}

		@Override
		public String toString() {
			return "VRE [name=" + name + ", description=" + description + ", id=" + id + ", url=" + url + ", scope="
					+ scope + "]";
		}

		@Override
		public int compareTo(VRE vre) {
			return this.getName().compareTo(vre.getName());
		}
	}

}
