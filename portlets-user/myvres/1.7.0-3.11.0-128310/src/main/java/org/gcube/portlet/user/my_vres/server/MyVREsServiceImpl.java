package org.gcube.portlet.user.my_vres.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.communitymanager.impl.OrganizationManagerImpl;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlet.user.my_vres.client.MyVREsService;
import org.gcube.portlet.user.my_vres.shared.UserBelonging;
import org.gcube.portlet.user.my_vres.shared.VO;
import org.gcube.portlet.user.my_vres.shared.VRE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.servlet.ImageServletTokenUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * The server side implementation of the RPC service.
 * @author Massimiliano Assante - ISTI CNR
 * @version 1.0 Jun 2012
 */
@SuppressWarnings("serial")
public class MyVREsServiceImpl extends RemoteServiceServlet implements	MyVREsService {

	private static final Logger _log = LoggerFactory.getLogger(MyVREsServiceImpl.class);
	/**
	 * 
	 */
	public static final String CACHED_VOS = "CACHED_VRES";
	/**
	 * 
	 */
	public static final String ROOT_ORG = "rootorganization";

	private VO rootVO = new VO();



	/**
	 * the current ASLSession
	 * @return the session
	 */
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
				User currUser = OrganizationsUtil.validateUser(username);			

				ThemeDisplay themeDisplay = (ThemeDisplay) this.getThreadLocalRequest().getSession().getAttribute(WebKeys.THEME_DISPLAY);

				LinkedHashMap<String, ArrayList<VRE>> toReturn = new LinkedHashMap<String, ArrayList<VRE>>();
				//before iterating the actual groups create the virtualGroups in the correct order
				List<String> virtualGroups = OrganizationManagerImpl.getVirtualGroups();
				for (String vg : virtualGroups) {
					String[] splits = vg.split("\\|");
					String gName = splits[0];
					ArrayList<VRE> toCreate = new ArrayList<VRE>();
					String cat = gName;
					toReturn.put(cat, toCreate);
				}

				//start of iteration of the actual groups
				List<Organization> organizations = OrganizationLocalServiceUtil.getOrganizations(0, OrganizationLocalServiceUtil.getOrganizationsCount());
				Organization rootOrganization = null;
				for (Organization organization : organizations) {
					if (organization.getName().equals(PortalContext.getConfiguration().getInfrastructureName())) {
						rootOrganization = organization;
						break;
					}
				}

				try {
					_log.debug("root: " + rootOrganization.getName() );
				} catch (NullPointerException e) {
					_log.error("Cannot find root organziation, please check gcube-data.properties file in $CATALINA_HOME/conf folder, unless your installing the Bundle");
					return toReturn;
				}

				//for each root sub organizations (VO)
				for (Organization vOrg : rootOrganization.getSuborganizations()) {
						for (Organization vre : vOrg.getSuborganizations()) {
						VRE vreToAdd = new VRE();
						vreToAdd.setName(vre.getName());
						vreToAdd.setGroupName("/"+vOrg.getParentOrganization().getName()+"/"+vOrg.getName()+"/"+vre.getName());

						long logoId = vre.getLogoId();
						String logoURL =  themeDisplay.getPathImage()+"/organization_logo?img_id="+ logoId +"&t" + ImageServletTokenUtil.getToken(logoId);
						vreToAdd.setImageURL(logoURL);

						String vreUrl = vre.getGroup().getPathFriendlyURL(true, themeDisplay) + vre.getGroup().getFriendlyURL();
						vreToAdd.setFriendlyURL(vreUrl);

						//check if the user belongs to it
						if (currUser.getOrganizations().contains(vre)) {
							vreToAdd.setUserBelonging(UserBelonging.BELONGING);
							
							String catName = OrganizationManagerImpl.getVirtualGroupName(vre);
							String[] splits = catName.split("\\|");
							catName = splits[0];
							
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
	 * read the root VO name from a property file and retuns it
	 */
	protected static String getRootOrganizationName() {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "gcube";

		try {
			String propertyfile = OrganizationsUtil.getTomcatFolder()+"conf/gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn = props.getProperty(ROOT_ORG);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			toReturn = "gcube";
			_log.error("gcube-data.properties file not found under $CATALINA_HOME/conf dir, returning default VO Name " + toReturn);
			return toReturn;
		}
		_log.debug("Returning Root VO Name: " + toReturn );
		return toReturn;
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