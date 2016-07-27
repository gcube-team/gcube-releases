package org.gcube.portlet.user.my_vres.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlet.user.my_vres.client.MyVREsService;
import org.gcube.portlet.user.my_vres.shared.UserBelonging;
import org.gcube.portlet.user.my_vres.shared.VO;
import org.gcube.portlet.user.my_vres.shared.VRE;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vomanagement.usermanagement.model.VirtualGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The server side implementation of the RPC service.
 * @author Massimiliano Assante - ISTI CNR
 */
@SuppressWarnings("serial")
public class MyVREsServiceImpl extends RemoteServiceServlet implements	MyVREsService {

	private static final Logger _log = LoggerFactory.getLogger(MyVREsServiceImpl.class);
	/**
	 * 
	 */
	public static final String CACHED_VOS = "CACHED_VRES";

	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			_log.warn("USER IS NULL setting test.user");
			user = "test.user";
		}
		else {
			_log.info("LIFERAY PORTAL DETECTED user=" + user);
		}
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	@Override
	public String showMoreVREs() {
		return PortalContext.getConfiguration().getSiteLandingPagePath(getThreadLocalRequest());
	}
	@Override
	public String getSiteLandingPagePath() {
		String user = getASLSession().getUsername();
		_log.debug("user=" + user + " has no VREs");
		String toReturn = PortalContext.getConfiguration().getSiteLandingPagePath(getThreadLocalRequest())+GCubePortalConstants.VRES_EXPLORE_FRIENDLY_URL;
		return toReturn;
	}
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
	/**
	 * first method called by the UI
	 */
	public LinkedHashMap<String, ArrayList<VRE>> getUserVREs() {	
		getASLSession();
		//_log.trace("getInfrastructureVOs method called");
		if (!isWithinPortal())
			return getFakeVREs();
		//return new ArrayList<VO>();
		else 
			try {
				String username = getASLSession().getUsername();
				GroupManager gm = new LiferayGroupManager();

				LinkedHashMap<String, ArrayList<VRE>> toReturn = new LinkedHashMap<String, ArrayList<VRE>>();
				//before iterating the actual groups create the virtualGroups in the correct order
				List<VirtualGroup> virtualGroups = gm.getVirtualGroups();
				for (VirtualGroup vg : virtualGroups) {
					String gName = vg.getName();
					ArrayList<VRE> toCreate = new ArrayList<VRE>();
					String cat = gName;
					toReturn.put(cat, toCreate);
				}

				GCubeGroup rootGroupVO = gm.getRootVO();
				try {
					_log.debug("root: " + rootGroupVO.getGroupName() );
				} catch (NullPointerException e) {
					_log.error("Cannot find root organziation, please check gcube-data.properties file in $CATALINA_HOME/conf folder, unless your installing the Bundle");
					return toReturn;
				}

				//for each root sub organizations (VO)
				for (GCubeGroup vOrg : rootGroupVO.getChildren()) {
					for (GCubeGroup vre : vOrg.getChildren()) {
						VRE vreToAdd = new VRE();
						vreToAdd.setName(vre.getGroupName());
						vreToAdd.setGroupName(gm.getInfrastructureScope(vre.getGroupId()));
						long logoId = vre.getLogoId();
						String logoURL = "/image/layout_set_logo?img_id="+ logoId;
						vreToAdd.setImageURL(logoURL);
						String vreUrl = GCubePortalConstants.PREFIX_GROUP_URL+vre.getFriendlyURL();
						vreToAdd.setFriendlyURL(vreUrl);
						
						vreToAdd.setUserBelonging(UserBelonging.NOT_BELONGING);
						GCubeUser currUser = new LiferayUserManager().getUserByUsername(username);
						if (gm.listGroupsByUser(currUser.getUserId()).contains(vre)) {
							vreToAdd.setUserBelonging(UserBelonging.BELONGING);
							
							String catName = gm.getVirtualGroup(vre.getGroupId()).getName();
							
							//for preserving order we inserted the keys before
							if (toReturn.containsKey(catName)) {
								ArrayList<VRE> toUpdate = toReturn.get(catName);
								toUpdate.add(vreToAdd);
							}											
						}		
					}
				}
				
				//sort the vres in the groups
				for (String cat : toReturn.keySet()) {
					ArrayList<VRE> toSort = toReturn.get(cat);
					Collections.sort(toSort);
				}
				return toReturn;

			} 
		catch (Exception e) {			
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * 
	 * @param scope
	 */
	public void loadLayout(String scope, String URL) {
		_log.trace("Calling Load Layout...");
		HttpSession session = this.getThreadLocalRequest().getSession();
		ASLSession mysession = getASLSession();
		mysession.setAttribute("loadlayout", "true");
		session.setAttribute("loadLayout", "true");
		session.setAttribute("selectedVRE", scope);
		mysession.logUserLogin(scope);
		mysession.setScope(scope);

		_log.trace("User login logged to: " + scope);
	}

	/**
	 * simply returns fake VREs for development purpose
	 * @return
	 */
	protected static LinkedHashMap<String, ArrayList<VRE>> getFakeVREs() {
		LinkedHashMap<String, ArrayList<VRE>> toReturn = new LinkedHashMap<String, ArrayList<VRE>>();
		
		final String categoryNameOne = "gCubeApps";	
		final String categoryNameTwo = "BlueBRIDGE";	
		//			
		VRE cool_EM_VRE = new VRE();
		cool_EM_VRE.setName("BiodiversityResearchEnvironment");
		cool_EM_VRE.setGroupName("/d4science.research-infrastructures.eu/EM/COOLEMVRE");
		cool_EM_VRE.setDescription("cool_EM_VRE VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");
		cool_EM_VRE.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/gcm-preview.jpg");
		cool_EM_VRE.setUserBelonging(UserBelonging.BELONGING);
	

		VRE cool_EM_VRE2 = new VRE();
		cool_EM_VRE2.setName("COOL VRE 2");
		cool_EM_VRE2.setGroupName("/d4science.research-infrastructures.eu/EM/COOLEMVRE2");
		cool_EM_VRE2.setDescription("Cool VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");

		cool_EM_VRE2.setImageURL("https://newportal.i-marine.d4science.org/image/organization_logo?img_id=13302&t1339191699773");
		cool_EM_VRE2.setUserBelonging(UserBelonging.NOT_BELONGING);

		VRE cool_EM_VRE3 = new VRE();
		cool_EM_VRE3.setName("COOL EM VRE TRE");
		cool_EM_VRE3.setGroupName("/d4science.research-infrastructures.eu/EM/COOlVRE3");
		cool_EM_VRE3.setDescription("Cool VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");

		cool_EM_VRE3.setImageURL("https://newportal.i-marine.d4science.org/image/organization_logo?img_id=13302&t1339191699773");
		cool_EM_VRE3.setUserBelonging(UserBelonging.BELONGING);

		ArrayList<VRE> toAdd = new ArrayList<VRE>();
		toAdd.add(cool_EM_VRE);
		toAdd.add(cool_EM_VRE2);
		toAdd.add(cool_EM_VRE3);
		toAdd.add(cool_EM_VRE);
		toAdd.add(cool_EM_VRE2);
		toAdd.add(cool_EM_VRE3);
		
		
		VRE demo = new VRE();
		demo.setName("Demo");
		demo.setGroupName("/d4science.research-infrastructures.eu/EM/Demo");
		demo.setDescription("Cool VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");

		demo.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/RedGrid.jpg");
		demo.setUserBelonging(UserBelonging.BELONGING);

		VRE vreGCM = new VRE();
		vreGCM.setName("GCM");
		vreGCM.setGroupName("/d4science.research-infrastructures.eu/EM/GCM");
		vreGCM.setDescription("Global Ocean Chlorophyll Monitoring (GCM) Virtual Research Environment<br />" 
				+ "The phytoplankton plays a similar role to terrestrial green plants in the photosynthetic process and are credited with removing as much carbon dioxide from the atmosphere as their earthbound counterparts, making it important to monitor and model plankton into calculations of future climate change.");
		vreGCM.setImageURL("https://newportal.i-marine.d4science.org/image/organization_logo?img_id=13302&t1339191699773");
		vreGCM.setUserBelonging(UserBelonging.BELONGING);

		ArrayList<VRE> toAdd2 = new ArrayList<VRE>();
		toAdd2.add(demo);
		toAdd2.add(vreGCM);
		
		toReturn.put(categoryNameOne, toAdd);
		toReturn.put(categoryNameTwo, toAdd2);
		
		return toReturn;
	}
	
	
}