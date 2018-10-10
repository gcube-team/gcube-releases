package org.gcube.portlets.user.joinvre.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Invite;
import org.gcube.portal.databook.shared.InviteStatus;
import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portal.tou.TermsOfUse;
import org.gcube.portal.tou.TermsOfUseImpl;
import org.gcube.portal.tou.exceptions.ToUNotFoundException;
import org.gcube.portal.tou.model.ToU;
import org.gcube.portlets.user.joinvre.client.JoinService;
import org.gcube.portlets.user.joinvre.shared.TabbedPage;
import org.gcube.portlets.user.joinvre.shared.UserBelonging;
import org.gcube.portlets.user.joinvre.shared.VRE;
import org.gcube.portlets.user.joinvre.shared.VRECategory;
import org.gcube.portlets.user.joinvre.shared.VreMembershipType;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeMembershipRequest;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vomanagement.usermanagement.model.GroupMembershipType;
import org.gcube.vomanagement.usermanagement.model.MembershipRequestStatus;
import org.gcube.vomanagement.usermanagement.model.VirtualGroup;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.VirtualHost;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.VirtualHostLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

/**
 * @author Massimiliano Assante, CNR_ISTI
 */
@SuppressWarnings("serial")
public class JoinServiceImpl extends RemoteServiceServlet implements JoinService {
	private static Log _log = LogFactoryUtil.getLog(JoinServiceImpl.class);
	private static DatabookStore store;
	public static final String PREFIX_PUBLIC_URL = "/web";
	//tell whether the 
	public static final String TABBED_LAYOUT_ATTRIBUTE = "TabbedLayout";
	public static final String TAB_NAMES_ATTRIBUTE = "TabName";
	public static final String ORGANIZATION_NAMES_ATTRIBUTE = "OrganisationName";	

	public static final String ALLVRES_SESSION_ATTRIBUTE = "ALLVRES_SESSION";

	private static GroupManager groupsManager;

	public void init() {
		groupsManager = new LiferayGroupManager();
	}
	/**
	 * 
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} catch (com.liferay.portal.kernel.bean.BeanLocatorException ex) {			
			_log.trace("Development Mode ON");
			return false;
		}			
	}
	@Override
	public String joinVRE(Long vreID) {
		try {
			return PortalContext.getConfiguration().getSiteLandingPagePath(getThreadLocalRequest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public UserInfo readInvite(final String inviteId, final long siteId) {
		initStore();		
		try {
			Invite invite = store.readInvite(inviteId);
			GCubeUser inviter = new LiferayUserManager().getUserByUsername(invite.getSenderUserId());
			return new UserInfo(
					inviter.getUsername(),
					inviter.getFullname(), 
					inviter.getUserAvatarURL(), 
					"", 
					getTermsOfUse(siteId), // we use accountURL for the terms of use in this case
					true, false, null); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	/**
	 * check if a terms of use exists for the given Site (VRE), returns null is non existent
	 * @return a String containing the ToU to accept or null if no ToU exists 
	 */
	@Override
	public String getTermsOfUse(long siteId) {

		TermsOfUse tou = new TermsOfUseImpl();
		try {
			long groupId = groupsManager.getGroup(siteId).getGroupId();
			ToU terms = tou.getToUGroup(groupId);
			return terms.getContent();
		}
		catch (ToUNotFoundException ex) {
			_log.debug("Terms of Use not found for this VRE id " + siteId); 
			return null;
		}
		catch (Exception e) {
			_log.error("An error occurred while trying to fetch the ToU for VRE id " + siteId); 
			return null;
		}
	}
	/**
	 * returns null if is not a tabbedpanel, the tab names as list of Strings otherwise
	 */
	public List<TabbedPage> isTabbedPanel() {
		if (isWithinPortal()) {
			_log.info("check if isTabbedPanel "); 
			Object tabbedLayoutObj = null;
			try {
				long currentSiteGroupId = getSiteFromServletRequest(getThreadLocalRequest()).getGroupId();
				tabbedLayoutObj = groupsManager.readCustomAttr(currentSiteGroupId, TABBED_LAYOUT_ATTRIBUTE);
				Boolean isTabbedLayout = false;
				if (tabbedLayoutObj != null) {
					isTabbedLayout = (Boolean) tabbedLayoutObj;
					if (isTabbedLayout)
						return getTabNames();
				}	
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}	
			return null;
		}
		else {
			return Arrays.asList(
					new TabbedPage[]{new TabbedPage("Products", "designed to apply Data Mining techniques to biological data. "), 
							new TabbedPage("Portfolio", "Portfolio description The algorithms are executed in ..")}
					);
		}
	}
	/**
	 * 
	 * @return the list containing the names of the Tabs to show in the correct order.
	 * @throws Exception
	 */
	private List<TabbedPage> getTabNames() throws Exception {
		List<TabbedPage> toReturn = new ArrayList<TabbedPage>();
		long currentSiteGroupId = getSiteFromServletRequest(getThreadLocalRequest()).getGroupId();	
		String[] values = (String[]) groupsManager.readCustomAttr(currentSiteGroupId, TAB_NAMES_ATTRIBUTE);
		TabbedPage toAdd = new TabbedPage();
		if (values != null && values.length > 0) {
			for (int i = 0; i < values.length; i++) {
				toAdd = new TabbedPage();
				String[] splits = values[i].split("\\|");
				if (splits.length > 1) {
					toAdd.setName(splits[0]);
					toAdd.setDescription(splits[1]);
				} else {
					_log.warn("I could not find the Tab Description in the Custom field, is it separated by the pipe? getting all the value ...");
					toAdd.setName(values[i]);
					toAdd.setDescription("");
				}
				toReturn.add(toAdd);
			}					
		} else {
			toAdd.setName("NoTabbedPageAssigned");
			toAdd.setDescription("NoTabbedPageDescription");
		}
		return toReturn;  
	}

	//first method called to get VREs and their categories
	@Override
	public LinkedHashMap<VRECategory, ArrayList<VRE>> getVREs() {
		
		LinkedHashMap<VRECategory, ArrayList<VRE>> toReturn = new LinkedHashMap<VRECategory, ArrayList<VRE>>();
		try {			
			if (isWithinPortal()) {
				if (isTabbedPanel() != null) {
					String firstTabName = getTabNames().get(0).getName();
					return getPortalSitesMappedToVRE(firstTabName);
				} else {
					toReturn = getPortalSitesMappedToVRE();
					setVREsInSession(toReturn);
				}
			} else {
				toReturn = getFakePortalVREs();	
			}
		} catch (Exception e) {
			_log.error("Error getting VREs", e);
		}
		return toReturn;
	}

	/**
	 * 
	 * @param tabName
	 * @return the list of VREs given a tabName
	 */
	@Override
	public LinkedHashMap<VRECategory, ArrayList<VRE>> getPortalSitesMappedToVRE(String tabName) {
		tabName = tabName.trim();
		_log.debug("Asked for vres of Tab " + tabName);
		LinkedHashMap<VRECategory, ArrayList<VRE>> tabVREs = new LinkedHashMap<VRECategory, ArrayList<VRE>>();			
		try {
			LinkedHashMap<VRECategory, ArrayList<VRE>> allVREs = getPortalSitesMappedToVRE();
			setVREsInSession(allVREs); 

			for (VRECategory cat : allVREs.keySet()) {
				ArrayList<VRE> toAdd = new ArrayList<VRE>();
				tabVREs.put(cat, toAdd);						
				for (VRE vre : allVREs.get(cat)) {
					_log.debug("getting selected tab for " + vre.getName());
					String[] vreTabNames = (String[]) groupsManager.readCustomAttr(vre.getId(), TAB_NAMES_ATTRIBUTE);
					if (vreTabNames != null && vreTabNames.length > 0) {
						String vreTabName = vreTabNames[0];
						if (tabName.equals(vreTabName)) {
							toAdd.add(vre);
							_log.debug("Added " + vre.getName() + " as it belongs to " + vreTabName);
						}
					}else {
						_log.warn("Spotted vre without tab assigned: " + vre.getName() + " skipping it ...");
					}
				}
			}
		}
		catch (Exception e) {
			_log.error("Error getting VREs by Category", e);
		}
		return tabVREs;
	}
	/**
	 * @param organisationName
	 * @return the list of VREs given a organisation Name
	 */
	@Override
	public LinkedHashMap<VRECategory, ArrayList<VRE>> getVREsByOrganisation(String organisationName) {
		LinkedHashMap<VRECategory, ArrayList<VRE>> organizationVREs = new LinkedHashMap<VRECategory, ArrayList<VRE>>();			
		try {
			LinkedHashMap<VRECategory, ArrayList<VRE>> allVREs = getPortalSitesMappedToVRE();

			for (VRECategory cat : allVREs.keySet()) {
				ArrayList<VRE> toAdd = new ArrayList<VRE>();
				organizationVREs.put(cat, toAdd);						
				for (VRE vre : allVREs.get(cat)) {
					String[] vreOrgNames = (String[]) groupsManager.readCustomAttr(vre.getId(), ORGANIZATION_NAMES_ATTRIBUTE);
					String vreOrgName = vreOrgNames[0];
					if (organisationName.equals(vreOrgName)) {
						toAdd.add(vre);
						_log.debug("Added " + vre.getName() + " as it belongs to organisation " + organisationName);
					}
				}
			}
		}
		catch (Exception e) {
			_log.error("Error getting VREs by Organization", e);
		}
		return organizationVREs;
	}

	/**
	 * @param categoryName
	 * @return the list of VREs given a category Name
	 */
	@Override
	public LinkedHashMap<VRECategory, ArrayList<VRE>> getVREsByCategory(String categoryName) {
		_log.debug("getVREsByCategory: " + categoryName);
		LinkedHashMap<VRECategory, ArrayList<VRE>> toReturn = new LinkedHashMap<VRECategory, ArrayList<VRE>>();		
		try {
			LinkedHashMap<VRECategory, ArrayList<VRE>> temp = null;
			if (getVREsFromSession() == null) {
				temp = getPortalSitesMappedToVRE();
				setVREsInSession(temp);
				_log.debug("getVREsByCategory looking in session not successful, asking to DB ... " + categoryName);
			} else {
				temp = getVREsFromSession();
				_log.debug("getVREsByCategory looking in session " + categoryName);
			}
			for (VRECategory cat : temp.keySet()) {
				if (cat.getName().equals(categoryName)) {
					toReturn.put(cat, temp.get(cat));
					_log.debug("getVREsByCategory foudn match, returning " + cat.getName());
					return toReturn;
				}					
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	@Override
	public List<String> getAllOrganisations() {
		List<String> toReturn = new ArrayList<String>();
		if (isWithinPortal()) {
			try {
				long currentSiteGroupId = getSiteFromServletRequest(getThreadLocalRequest()).getGroupId();
				Group site = GroupLocalServiceUtil.getGroup(currentSiteGroupId);		
				String[] values = (String[]) site.getExpandoBridge().getAttributeDefault(ORGANIZATION_NAMES_ATTRIBUTE);
				for (int i = 0; i < values.length; i++) {
					toReturn.add(values[i]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			return Arrays.asList(new String[]{"BlueBRIDGE", "iMarine"});
		}
		return toReturn;  
	}

	@Override
	public ArrayList<String> getAllCategories() {
		ArrayList<String> toReturn = new ArrayList<>();
		if (isWithinPortal()) {
			try {
				long currentSiteGroupId = getSiteFromServletRequest(getThreadLocalRequest()).getGroupId();
				List<VirtualGroup> currentSiteVGroups =  groupsManager.getVirtualGroups(currentSiteGroupId);

				for (VirtualGroup vg : currentSiteVGroups) {
					toReturn.add(vg.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
		else {
			LinkedHashMap<VRECategory, ArrayList<VRE>> fakes = getFakePortalVREs();
			for (VRECategory cat : fakes.keySet()) {
				toReturn.add(cat.getName());
			}
		}
		return toReturn;
	}

	/**
	 * 
	 * @return the Virtual groups with their VREs in the order estabilished in the LR Control Panel
	 * @throws SystemException
	 * @throws PortalException
	 */
	public LinkedHashMap<VRECategory, ArrayList<VRE>> getPortalSitesMappedToVRE() throws Exception {
		LinkedHashMap<VRECategory, ArrayList<VRE>> toReturn = new LinkedHashMap<VRECategory, ArrayList<VRE>>();

		long currentSiteGroupId = getSiteFromServletRequest(getThreadLocalRequest()).getGroupId();
		List<VirtualGroup> currentSiteVGroups =  groupsManager.getVirtualGroups(currentSiteGroupId);

		for (VirtualGroup vg : currentSiteVGroups) {
			ArrayList<VRE> toCreate = new ArrayList<VRE>();
			VRECategory cat = new VRECategory(1L, vg.getName(), vg.getDescription());
			toReturn.put(cat, toCreate);
		}

		GCubeGroup rootGroupVO = groupsManager.getRootVO();

		try {
			_log.debug("root: " + rootGroupVO.getGroupName() );
		} catch (NullPointerException e) {
			_log.error("Cannot find root organziation, please check gcube-data.properties file in $CATALINA_HOME/conf folder, unless your installing the Bundle");
			return toReturn;
		}
		PortalContext pContext = PortalContext.getConfiguration();

		List<GCubeGroup> currUserGroups = new ArrayList<GCubeGroup>();
		GCubeUser currUser = pContext.getCurrentUser(getThreadLocalRequest());
		if (currUser != null) {
			currUserGroups = groupsManager.listGroupsByUser(currUser.getUserId());
		}

		//for each root sub organizations (VO)
		for (GCubeGroup vOrg : rootGroupVO.getChildren()) {
			for (GCubeGroup vreSite : vOrg.getChildren()) {
				long vreID =  vreSite.getGroupId();
				String vreName = vreSite.getGroupName();
				String vreDescription = vreSite.getDescription();

				long logoId = vreSite.getLogoId();
				String vreLogoURL = groupsManager.getGroupLogoURL(logoId);
				String groupName = groupsManager.getInfrastructureScope(vreSite.getGroupId());
				String friendlyURL =  GCubePortalConstants.PREFIX_GROUP_URL+vreSite.getFriendlyURL();

				List<VirtualGroup> vreGroups =  groupsManager.getVirtualGroups(vreID);
				for (VirtualGroup vreGroup : vreGroups) {
					for (VRECategory vre : toReturn.keySet()) {
						if (vre.getName().compareTo(vreGroup.getName())==0) {
							ArrayList<VRE> toUpdate = toReturn.get(vre);
							UserBelonging belongs = UserBelonging.NOT_BELONGING;
							VRE toAdd = new VRE(vreID,vreName, vreDescription, vreLogoURL, groupName, friendlyURL, belongs, getVREMembershipType(vreSite.getMembershipType()));				
							if (GroupLocalServiceUtil.getGroup(vreID).getPublicLayoutsPageCount() > 0) {
								String publicURL = PREFIX_PUBLIC_URL+vreSite.getFriendlyURL();
								toAdd.setPublicURL(publicURL);
							}
							if (currUser != null) {
								//check if the user belongs to it
								if (currUserGroups.contains(vreSite)) {
									toAdd.setUserBelonging(UserBelonging.BELONGING);
								}
								else if (checkPending(currUser.getUsername(), vreSite.getGroupId()))
									toAdd.setUserBelonging(UserBelonging.PENDING);
							}
							toUpdate.add(toAdd);
						}
					}									
				}				
			}
		}

		//sort the vres in the groups
		for (VRECategory cat : toReturn.keySet()) {
			ArrayList<VRE> toSort = toReturn.get(cat);
			Collections.sort(toSort);
		}
		return toReturn;
	}



	/**
	 * useful method for development purpose
	 * @return
	 */
	private LinkedHashMap<VRECategory, ArrayList<VRE>> getFakePortalVREs() {
		LinkedHashMap<VRECategory, ArrayList<VRE>> toReturn = new LinkedHashMap<VRECategory, ArrayList<VRE>>();
		VRECategory devsecCategory = new VRECategory(1, "Z_Development", "designed to apply Data Mining techniques to biological data. "
				+ "The algorithms are executed in a distributed fashion on the e-Infrastructure nodes or on local multi-core machines.");
		ArrayList<VRE> vres = new ArrayList<VRE>();

		vres.add(new VRE(0, "BiodiversityLab", ""
				+ "<h2>BiodiversityLab</h2>"
				+ "The BiodiversityLab is a VRE designed to provide a collection of applications that allow scholars to perform complete experiments about "
				+ "single individuals or groups of marine species. The VRE allows to: <ul> <li> inspect species maps;<li> produce a species distribution map by means of either an expert system (AquaMaps) or a machine learning model (e.g. Neural Networks);"
				+ "<li> analyse species observation trends;"
				+ "<li> inspect species occurrence data;"
				+ "<li> inspect species descriptions and characteristics;"
				+ "<li> perform analysis of climatic changes and of their effects on species distribution;"
				+ "<li> produce GIS maps for geo-spatial datasets;"
				+ "<li> discover Taxa names;"
				+ "<li> cluster occurrence data;"
				+ "<li> estimate similarities among habitats."
				+ "</ul>"
				+ "", "", "http://placehold.it/200x200", "/group/devsec", UserBelonging.NOT_BELONGING));
		vres.add(new VRE(0, "Scalable Data", ""
				+ "<h2>Scalable Data Mining</h2>"
				+ "The Scalable Data Mining  is a VRE designed to apply Data Mining techniques to biological data. The algorithms are executed in a distributed fashion on the e-Infrastructure nodes or on local multi-core machines. Scalability is thus meant as distributed data processing but even as services dynamically provided to the users. The system is scalable in the number of users and in the size of the data to process. Statistical data processing can be applied to perform Niche Modelling or Ecological Modelling experiments. Other applications can use general purpose techniques like Bayesian models. Time series of observations can be managed as well, in order to classify trends, catch anomaly patterns and perform simulations. The idea under the distributed computation for data mining techniques is to overcome common limitations that can happen when using statistical algorithms: "
				+ "single individuals or groups of marine species. The VRE allows to: <ul> <li> inspect species maps;<li> produce a species distribution map by means of either an expert system (AquaMaps) or a machine learning model (e.g. Neural Networks);"
				+ "<li> analyse species observation trends;"
				+ "<li> inspect species occurrence data;"
				+ "<li> inspect species descriptions and characteristics;"
				+ "<li> perform analysis of climatic changes and of their effects on species distribution;"
				+ "<li> produce GIS maps for geo-spatial datasets;"
				+ "<li> discover Taxa names;"
				+ "<li> cluster occurrence data;"
				+ "<li> estimate similarities among habitats."
				+ "</ul>"
				+ "", "", "http://placehold.it/200x200", "/group/devsec", UserBelonging.NOT_BELONGING));
		toReturn.put(devsecCategory, vres);

		devsecCategory = new VRECategory(2, "Sailing", "Sailing prod desc");
		vres = new ArrayList<VRE>();
		vres.add(new VRE(2, "devmode", "devmode VRE description", "http://placehold.it/200x100", "https://placeholdit.imgix.net/~text?txtsize=19&txt=200%C3%97100&w=200&h=100", "/group/devmode", UserBelonging.NOT_BELONGING, VreMembershipType.PRIVATE));
		vres.add(new VRE(1, "StrategicInvestmentAnalysis", "devVRE VRE description", "", "https://placeholdit.imgix.net/~text?txtsize=19&txt=200%C3%97100&w=200&h=100", "/group/devVRE", UserBelonging.NOT_BELONGING, VreMembershipType.OPEN));
		vres.add(new VRE(2, "devmode2", "devmode VRE description", "http://placehold.it/200x100", "", "/group/devmode", UserBelonging.NOT_BELONGING, VreMembershipType.OPEN));
		vres.add(new VRE(1, "devVR3E", "devVRE VRE description", "http://placehold.it/200x200", "aaaa", "/group/devVRE", UserBelonging.NOT_BELONGING, VreMembershipType.PRIVATE));
		vres.add(new VRE(2, "devmode3", "devmode VRE description", "http://placehold.it/200x200", "", "/group/devmode", UserBelonging.NOT_BELONGING, VreMembershipType.PRIVATE));
		vres.add(new VRE(1, "devVRE4", "devVRE VRE description", "", "http://placehold.it/200x200", "/group/devVRE", UserBelonging.NOT_BELONGING, VreMembershipType.PRIVATE));
		vres.add(new VRE(2, "devmode4", "devmode VRE description", "", "http://placehold.it/200x200", "/group/devmode", UserBelonging.NOT_BELONGING, VreMembershipType.PRIVATE));
		vres.add(new VRE(1, "devVRE5", "devVRE VRE description", "", "http://placehold.it/200x200", "/group/devVRE", UserBelonging.NOT_BELONGING, VreMembershipType.PRIVATE));
		vres.add(new VRE(2, "devmode5", "devmode VRE description", "", "http://placehold.it/200x200", "/group/devmode", UserBelonging.NOT_BELONGING, VreMembershipType.PRIVATE));
		vres.add(new VRE(1, "devVRE6", "devVRE VRE description", "", "http://placehold.it/200x200", "/group/devVRE", UserBelonging.NOT_BELONGING, VreMembershipType.PRIVATE));
		vres.add(new VRE(2, "devmode6", "devmode VRE description", "", "http://placehold.it/200x200", "/group/devmode", UserBelonging.NOT_BELONGING, VreMembershipType.PRIVATE));
		vres.add(new VRE(1, "devVRE7", "devVRE VRE description", "", "http://placehold.it/200x200", "/group/devVRE", UserBelonging.NOT_BELONGING, VreMembershipType.PRIVATE));
		vres.add(new VRE(2, "devmod76", "devmode VRE description", "", "http://placehold.it/200x200", "/group/devmode", UserBelonging.NOT_BELONGING));
		toReturn.put(devsecCategory, vres);

		setVREsInSession(toReturn);

		return toReturn;
	}

	@Override
	public VRE getSelectedVRE(Long groupId) {
		_log.debug("*getting Selected Research Environment from referral, site id = " + groupId);	
		VRE toReturn = null;
		try {
			GroupManager gm = new LiferayGroupManager();
			GCubeGroup selectedVRE = gm.getGroup(groupId);
			String vreName = selectedVRE.getGroupName();
			String vreDescription = selectedVRE.getDescription();


			long logoId = selectedVRE.getLogoId();
			String vreLogoURL = gm.getGroupLogoURL(logoId);
			String infraScope = gm.getInfrastructureScope(selectedVRE.getGroupId());
			String friendlyURL =  GCubePortalConstants.PREFIX_GROUP_URL+selectedVRE.getFriendlyURL();


			GCubeUser currUser = PortalContext.getConfiguration().getCurrentUser(getThreadLocalRequest());
			//check if the user belongs to it
			UserBelonging belongEnum = UserBelonging.NOT_BELONGING;
			if (gm.listGroupsByUser(currUser.getUserId()).contains(selectedVRE)) 
				belongEnum = UserBelonging.BELONGING;
			else if (checkPending(currUser.getUsername(), selectedVRE.getGroupId()))
				belongEnum = UserBelonging.PENDING;
			//return the selected VRE for this user
			toReturn = new VRE(groupId, vreName, vreDescription, vreLogoURL, infraScope, friendlyURL, belongEnum, getVREMembershipType(selectedVRE.getMembershipType()));
		} catch (Exception e) {
			_log.error("Something wrong happened while trying to getSite by id, probably the group id is wrong. " + e.getMessage());
		}
		return toReturn;
	}

	/**
	 * 
	 * @param type
	 * @return the correspondent mapping to the gcube model
	 */
	private VreMembershipType getVREMembershipType(GroupMembershipType type) {
		switch (type) {
		case RESTRICTED:
			return VreMembershipType.RESTRICTED;
		case OPEN:
			return VreMembershipType.OPEN;
		default:
			return VreMembershipType.PRIVATE;
		}
	}
	/**
	 * 
	 * @param screenName
	 * @param groupId
	 * @return
	 * @throws UserRetrievalFault 
	 * @throws GroupRetrievalFault 
	 * @throws UserManagementSystemException 
	 */
	private static boolean checkPending(String screenName, long groupId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		UserManager um = new LiferayUserManager();
		List<GCubeMembershipRequest> requests  = um.listMembershipRequestsByGroup(groupId);
		for (GCubeMembershipRequest r : requests) {
			if ( r.getStatus() == MembershipRequestStatus.REQUEST && (r.getRequestingUser().getUsername().compareTo(screenName)==0))
				return true;
		}
		return false; 
	}

	@Override
	public void addMembershipRequest(VRE theVRE, String optionalMessage) {
		String scope = theVRE.getinfraScope();
		String username = PortalContext.getConfiguration().getCurrentUser(getThreadLocalRequest()).getUsername();
		if (optionalMessage == null || optionalMessage.compareTo("") == 0) 
			optionalMessage = "none";
		try {
			CacheRegistryUtil.clear();			
			long groupId = theVRE.getId();
			_log.debug("Look if a request exists already");
			List<GCubeGroup> userGroups = new LiferayGroupManager().listGroupsByUser(new LiferayUserManager().getUserId(username));
			for (GCubeGroup g : userGroups) {
				if (g.getGroupId() == groupId) {
					_log.warn("User already belongs to " + scope + " SKIP addMembershipRequest");
					return;
				}
			}	
			CacheRegistryUtil.clear();
			if (checkPending(username, groupId)) {
				_log.warn("User already asked for " + scope + " REQUEST IS IN PENDING - SKIP addMembershipRequest");
				return;
			}			
			_log.debug("Request does not exist, addMembershipRequest for user " + username);
			LoginServiceUtil.addMembershipRequest(username, scope, optionalMessage, getThreadLocalRequest());
			if (getTermsOfUse(groupId) != null) {
				new TermsOfUseImpl().setAcceptedToU(username, groupId);
				_log.info(username + "has requested and acceptedToU OK for " + scope);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	public String getPortalUrl() throws PortalException, SystemException {
		return PortalUtil.getPortalURL(this.getThreadLocalRequest());
	}

	/**
	 * the user to the VRE, plus send notifications to the vre manages of the vre
	 * in order to register a user i had to create a fake membership request because assigning a user to a group would have required
	 * the user to logout and login otherwise
	 */
	@Override
	public boolean registerUser(String scope, long groupId, boolean isInvitation) {
		UserManager um = new LiferayUserManager();
		try {			
			GCubeUser currUser = PortalContext.getConfiguration().getCurrentUser(getThreadLocalRequest());
			_log.debug("registerUser " +currUser.getUsername() + " to "+ scope);
			GroupManager gm = new LiferayGroupManager();
			um.requestMembership(currUser.getUserId(), gm.getGroupIdFromInfrastructureScope(scope), "Automatic Request at " + new Date());
			_log.debug("fakeRequest sent");
			String replierUsername = LiferayUserManager.getAdmin().getScreenName();
			_log.trace("Sleep 1 second ...");
			Thread.sleep(1000);
			um.acceptMembershipRequest(currUser.getUserId(), groupId, true, replierUsername, "Automatic acceptance request at " + new Date());
			_log.info("fakeRequest accepted");
			if (isInvitation) {
				initStore();
				String inviteId = store.isExistingInvite(scope, currUser.getEmail());
				if (inviteId != null) {
					Invite invite = store.readInvite(inviteId);
					store.setInviteStatus(scope, currUser.getEmail(), InviteStatus.ACCEPTED);
					LoginServiceUtil.notifyUserAcceptedInvite(currUser.getUsername(), scope, invite, getThreadLocalRequest());
				}
			}
			else {			
				LoginServiceUtil.notifyUserSelfRegistration(currUser.getUsername(), scope, getThreadLocalRequest());
				_log.info("notifyUserSelfRegistration sent");
			}
			if (getTermsOfUse(groupId) != null) {
				new TermsOfUseImpl().setAcceptedToU(currUser.getUsername(), groupId);
				_log.info("hasAcceptedToU OK for " + currUser.getUsername());
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @return the unique instance of the store
	 */
	public static synchronized DatabookStore initStore() {
		if (store == null) {
			store = new DBCassandraAstyanaxImpl();
		}
		return store;
	}

	@Override
	public String isExistingInvite(long groupId) {

		_log.debug("initiating Store");
		initStore();
		_log.debug("initStore OK");

		String email = PortalContext.getConfiguration().getCurrentUser(getThreadLocalRequest()).getEmail();
		String infraScope = null;
		try {
			infraScope = new LiferayGroupManager().getInfrastructureScope(groupId);
		} catch (UserManagementSystemException | GroupRetrievalFault e) {
			e.printStackTrace();
		}
		_log.debug("checking if invite exists for " + email + " on " +infraScope);
		return store.isExistingInvite(infraScope, email);
	}



	/**
	 * 
	 * @param request
	 * @return the current Group instance based on the request
	 * @throws PortalException
	 * @throws SystemException
	 */
	private Group getSiteFromServletRequest(final HttpServletRequest request) throws PortalException, SystemException {
		String serverName = request.getServerName();
		_log.debug("currentHost is " +  serverName);
		Group site = null;
		List<VirtualHost> vHosts = VirtualHostLocalServiceUtil.getVirtualHosts(0, VirtualHostLocalServiceUtil.getVirtualHostsCount());
		for (VirtualHost virtualHost : vHosts) {
			_log.debug("Found  " +  virtualHost.getHostname());
			if (virtualHost.getHostname().compareTo("localhost") != 0 && 
					virtualHost.getLayoutSetId() != 0 && 
					virtualHost.getHostname().compareTo(serverName) == 0) {
				long layoutSetId = virtualHost.getLayoutSetId();
				site = LayoutSetLocalServiceUtil.getLayoutSet(layoutSetId).getGroup();
				_log.debug("Found match! Your site is " +  site.getName());
				return site;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private LinkedHashMap<VRECategory, ArrayList<VRE>> getVREsFromSession() {
		if (getThreadLocalRequest().getSession().getAttribute(ALLVRES_SESSION_ATTRIBUTE) == null)
			return null;
		else
			return (LinkedHashMap<VRECategory, ArrayList<VRE>>) getThreadLocalRequest().getSession().getAttribute(ALLVRES_SESSION_ATTRIBUTE);
	}

	private void setVREsInSession(LinkedHashMap<VRECategory, ArrayList<VRE>> allVREs) {
		getThreadLocalRequest().getSession().setAttribute(ALLVRES_SESSION_ATTRIBUTE, allVREs);
	}

}
