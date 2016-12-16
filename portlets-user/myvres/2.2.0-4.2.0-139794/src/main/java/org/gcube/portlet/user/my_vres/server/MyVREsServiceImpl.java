package org.gcube.portlet.user.my_vres.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.portlet.user.my_vres.client.MyVREsService;
import org.gcube.portlet.user.my_vres.shared.UserBelonging;
import org.gcube.portlet.user.my_vres.shared.VRE;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vomanagement.usermanagement.model.VirtualGroup;
import org.gcube.vomanagement.usermanagement.util.ManagementUtils;
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
	
	public static final String ADD_MORE_CATEGORY = "Add More";
	public static final String ADD_MORE_IMAGE_PATH= "images/More.png";
	
	
	@Override
	public String getSiteLandingPagePath() {
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
		//_log.trace("getInfrastructureVOs method called");
		if (!isWithinPortal())
			return getFakeVREs();
		//return new ArrayList<VO>();
		else 
			try {
				PortalContext context = PortalContext.getConfiguration();
				String username = context.getCurrentUser(getThreadLocalRequest()).getUsername();
				GroupManager gm = new LiferayGroupManager();

				LinkedHashMap<String, ArrayList<VRE>> toReturn = new LinkedHashMap<String, ArrayList<VRE>>();

			
				List<VirtualGroup> currentSiteVGroups = gm.getVirtualGroups(ManagementUtils.getSiteGroupIdFromServletRequest(getThreadLocalRequest().getServerName()));
				for (VirtualGroup vg : currentSiteVGroups) {
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
						String logoURL = gm.getGroupLogoURL(logoId);
						vreToAdd.setImageURL(logoURL);
						String vreUrl = GCubePortalConstants.PREFIX_GROUP_URL+vre.getFriendlyURL();
						vreToAdd.setFriendlyURL(vreUrl);
						
						vreToAdd.setUserBelonging(UserBelonging.NOT_BELONGING);
						GCubeUser currUser = new LiferayUserManager().getUserByUsername(username);
						
						if (gm.listGroupsByUser(currUser.getUserId()).contains(vre)) {
							vreToAdd.setUserBelonging(UserBelonging.BELONGING);
							
							List<VirtualGroup> vreGroups =  gm.getVirtualGroups(vre.getGroupId());
							for (VirtualGroup vreGroup : vreGroups) {
								for (String category : toReturn.keySet()) {
									//for preserving order we inserted the keys before
									if (vreGroup.getName().compareTo(category)==0) {
										ArrayList<VRE> toUpdate = toReturn.get(category);
										toUpdate.add(vreToAdd);
									}				
								}
							}
							
							
							
														
						}		
					}
				}
				
				//sort the vres in the groups
				for (String cat : toReturn.keySet()) {
					ArrayList<VRE> toSort = toReturn.get(cat);
					Collections.sort(toSort);
				}
				HttpServletRequest request = getThreadLocalRequest();
				String gatewayURL = context.getGatewayURL(request);
				String exploreURL = gatewayURL+context.getSiteLandingPagePath(request)+GCubePortalConstants.VRES_EXPLORE_FRIENDLY_URL;
				VRE addMore = new VRE("", "", "", "", exploreURL, UserBelonging.BELONGING);
				//add a fake category and addMoreVRE
				ArrayList<VRE> addMoreVREs = new ArrayList<VRE>();
				addMoreVREs.add(addMore);
				toReturn.put(ADD_MORE_CATEGORY, addMoreVREs);
				
				
				return toReturn;

			} 
		catch (Exception e) {			
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * simply returns fake VREs for development purpose
	 * @return
	 */
	protected static LinkedHashMap<String, ArrayList<VRE>> getFakeVREs() {
		LinkedHashMap<String, ArrayList<VRE>> toReturn = new LinkedHashMap<String, ArrayList<VRE>>();
		
		final String categoryNameOne = "gCubeApps";	
		final String categoryNameTwo = "BlueBRIDGE";	
		final String categoryNameThree = "GEMex";	
		//			
		VRE cool_EM_VRE = new VRE();
		cool_EM_VRE.setName("BiodiversityResearchEnvironment");
		cool_EM_VRE.setGroupName("/d4science.research-infrastructures.eu/EM/COOLEMVRE");
		cool_EM_VRE.setDescription("cool_EM_VRE VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");
		cool_EM_VRE.setImageURL("https://placehold.it/150x150");
		cool_EM_VRE.setUserBelonging(UserBelonging.BELONGING);
	

		VRE cool_EM_VRE2 = new VRE();
		cool_EM_VRE2.setName("COOL VRE 2");
		cool_EM_VRE2.setGroupName("/d4science.research-infrastructures.eu/EM/COOLEMVRE2");
		cool_EM_VRE2.setDescription("Cool VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");

		cool_EM_VRE2.setImageURL("https://placehold.it/150x150");
		cool_EM_VRE2.setUserBelonging(UserBelonging.NOT_BELONGING);

		VRE cool_EM_VRE3 = new VRE();
		cool_EM_VRE3.setName("COOL EM VRE TRE");
		cool_EM_VRE3.setGroupName("/d4science.research-infrastructures.eu/EM/COOlVRE3");
		cool_EM_VRE3.setDescription("Cool VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");

		cool_EM_VRE3.setImageURL("https://placehold.it/150x150");
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
		vreGCM.setImageURL("https://placehold.it/150x150");
		vreGCM.setUserBelonging(UserBelonging.BELONGING);

		ArrayList<VRE> toAdd2 = new ArrayList<VRE>();
		toAdd2.add(demo);
		toAdd2.add(vreGCM);
		toAdd2.add(cool_EM_VRE3);
		toAdd2.add(cool_EM_VRE);
		toAdd2.add(cool_EM_VRE2);
		toAdd2.add(cool_EM_VRE3);
		
		ArrayList<VRE> toAdd3 = new ArrayList<VRE>();
		toAdd3.add(demo);
		toAdd3.add(vreGCM);
		toAdd3.add(cool_EM_VRE2);
		toAdd3.add(cool_EM_VRE3);
		toAdd3.add(cool_EM_VRE);
		toAdd3.add(cool_EM_VRE2);
		toAdd3.add(cool_EM_VRE3);
		
		toReturn.put(categoryNameOne, toAdd);
		toReturn.put(categoryNameTwo, toAdd2);
		toReturn.put(categoryNameThree, toAdd3);
		
		return toReturn;
	}
	
	
}