package org.gcube.portlets.user.joinvre.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.communitymanager.impl.OrganizationManagerImpl;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.joinvre.client.JoinService;
import org.gcube.portlets.user.joinvre.shared.UserBelonging;
import org.gcube.portlets.user.joinvre.shared.VRE;
import org.gcube.portlets.user.joinvre.shared.VRECategory;
import org.gcube.portlets.user.joinvre.shared.VRECustomAttributes;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ImageServletTokenUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@SuppressWarnings("serial")
public class JoinServiceImpl extends RemoteServiceServlet implements JoinService {

	private static Log _log = LogFactoryUtil.getLog(JoinServiceImpl.class);
	private static final String REQUEST_BASED_GROUP = "Requestbasedgroup";
	private static final String IS_EXTERNAL = "Isexternal";
	private static final String URL_IF_EXTERNAL = "Url";

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
			SessionManager.getInstance().getASLSession(sessionID, user).setScope("/gcube");
		}		
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

	/**
	 * when packaging test will fail if the user is not set to test.user
	 * @return .
	 */
	public String getDevelopmentUser() {
		String user = "test.user";
		return user;
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
	/**
	 * 
	 * @param session the Asl Session
	 * @param withinPortal true when is on Liferay portal
	 * @return the users belonging to the current organization (scope)
	 */
	@Override
	public LinkedHashMap<VRECategory, ArrayList<VRE>> getVREs() {
		LinkedHashMap<VRECategory, ArrayList<VRE>> toReturn = new LinkedHashMap<VRECategory, ArrayList<VRE>>();

		try {
			if (isWithinPortal()) {
				toReturn = getPortalOrganizationMappedToVRE();
			} else {
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
						+ "", "", "http://placehold.it/200x200", "/group/devsec", UserBelonging.NOT_BELONGING, false));
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
						+ "", "", "http://placehold.it/200x200", "/group/devsec", UserBelonging.NOT_BELONGING, true));
				toReturn.put(devsecCategory, vres);

				devsecCategory = new VRECategory(2, "Sailing", "Sailing prod desc");
				vres = new ArrayList<VRE>();
				vres.add(new VRE(1, "PerformanceEvaluationInAquaculture", "devVRE VRE description", "http://placehold.it/200x100", "http://placehold.it/200x100", "/group/devVRE", UserBelonging.NOT_BELONGING, false, true, "http://i-marine.d4science.org"));
				vres.add(new VRE(2, "devmode", "devmode VRE description", "http://placehold.it/200x100", "https://placeholdit.imgix.net/~text?txtsize=19&txt=200%C3%97100&w=200&h=100", "/group/devmode", UserBelonging.NOT_BELONGING, true));
				vres.add(new VRE(1, "StrategicInvestmentAnalysis", "devVRE VRE description", "", "https://placeholdit.imgix.net/~text?txtsize=19&txt=200%C3%97100&w=200&h=100", "/group/devVRE", UserBelonging.NOT_BELONGING, false));
				vres.add(new VRE(2, "devmode2", "devmode VRE description", "http://placehold.it/200x100", "", "/group/devmode", UserBelonging.NOT_BELONGING, true));
				vres.add(new VRE(1, "devVR3E", "devVRE VRE description", "http://placehold.it/200x200", "aaaa", "/group/devVRE", UserBelonging.NOT_BELONGING, false));
				vres.add(new VRE(2, "devmode3", "devmode VRE description", "http://placehold.it/200x200", "", "/group/devmode", UserBelonging.NOT_BELONGING, true));
				vres.add(new VRE(1, "devVRE4", "devVRE VRE description", "", "http://placehold.it/200x200", "/group/devVRE", UserBelonging.NOT_BELONGING, false));
				vres.add(new VRE(2, "devmode4", "devmode VRE description", "", "http://placehold.it/200x200", "/group/devmode", UserBelonging.NOT_BELONGING, true));
				vres.add(new VRE(1, "devVRE5", "devVRE VRE description", "", "http://placehold.it/200x200", "/group/devVRE", UserBelonging.NOT_BELONGING, false));
				vres.add(new VRE(2, "devmode5", "devmode VRE description", "", "http://placehold.it/200x200", "/group/devmode", UserBelonging.NOT_BELONGING, true));
				vres.add(new VRE(1, "devVRE6", "devVRE VRE description", "", "http://placehold.it/200x200", "/group/devVRE", UserBelonging.NOT_BELONGING, false));
				vres.add(new VRE(2, "devmode6", "devmode VRE description", "", "http://placehold.it/200x200", "/group/devmode", UserBelonging.NOT_BELONGING, true));
				vres.add(new VRE(1, "devVRE7", "devVRE VRE description", "", "http://placehold.it/200x200", "/group/devVRE", UserBelonging.NOT_BELONGING, false));
				vres.add(new VRE(2, "devmod76", "devmode VRE description", "", "http://placehold.it/200x200", "/group/devmode", UserBelonging.NOT_BELONGING, true));
				toReturn.put(devsecCategory, vres);
			}
		} catch (Exception e) {
			_log.error("Error getting VREs", e);
		}


		return toReturn;
	}

	private String getPortalBasicUrl() {
		HttpServletRequest request = this.getThreadLocalRequest();
		String protocol = (request.isSecure()) ? "https://" : "http://" ;
		String port = (request.getServerPort() == 80) ? "" : String.format(":%d", request.getServerPort());
		String portalBasicUrl = String.format("%s%s%s", protocol, request.getServerName(), port);
		_log.debug(String.format("getPortalBasicUrl : %s",  portalBasicUrl));
		return portalBasicUrl;
	}

	public VRECustomAttributes getVRECustomAttr(Organization organization) throws PortalException, SystemException {
		VRECustomAttributes toReturn = new VRECustomAttributes();
		
		try {
			long companyId = OrganizationsUtil.getCompany().getCompanyId();
			_log.trace("Setting Thread Permission");
			User user = UserLocalServiceUtil.getUserByScreenName(companyId, ScopeHelper.getAdministratorUsername());
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(user, false);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			_log.trace("Setting Permission ok!");

			if (organization.getExpandoBridge().getAttribute(REQUEST_BASED_GROUP) == null || organization.getExpandoBridge().getAttribute(REQUEST_BASED_GROUP).equals("")) {
				_log.trace(String.format("Attribute %s not initialized. In this case by default Access Grant is permitted", REQUEST_BASED_GROUP));
				toReturn.setUponRequest(true);
			} else {
				String attributeValue = (String) organization.getExpandoBridge().getAttribute(REQUEST_BASED_GROUP);
				toReturn.setUponRequest(attributeValue.compareTo("true") == 0);
			}			
			
			if (organization.getExpandoBridge().getAttribute(IS_EXTERNAL) == null || organization.getExpandoBridge().getAttribute(IS_EXTERNAL).equals("")) {
				_log.trace(String.format("Attribute %s not initialized. In this case by default we assume it is an internal VRE", IS_EXTERNAL));
				toReturn.setExternal(false);
			} else {
				Boolean attributeValue = (Boolean) organization.getExpandoBridge().getAttribute(IS_EXTERNAL);
				toReturn.setExternal(attributeValue);
				if (attributeValue) { //we read the custom attr URL if and only if the VRE is External, in the other case is useless
						String url = (String) organization.getExpandoBridge().getAttribute(URL_IF_EXTERNAL);
						toReturn.setUrlIfAny(url);
				}		
			}
		} catch (Exception e) {
			_log.error("Something went wrong when trying to read VRE Custom Attr, " + e);
			return toReturn;
		}	
		_log.trace("RETURNING VRECustomAttributes:\n" + toReturn.toString());
		return toReturn;
	}
	



	/**
	 * 
	 * @return the Virtual groups with their VREs in the order estabilished in the LR Control Panel
	 * @throws SystemException
	 * @throws PortalException
	 */
	public LinkedHashMap<VRECategory, ArrayList<VRE>> getPortalOrganizationMappedToVRE() throws SystemException, PortalException {

		LinkedHashMap<VRECategory, ArrayList<VRE>> toReturn = new LinkedHashMap<VRECategory, ArrayList<VRE>>();
		//before iterating the actual groups create the virtualGroups in the correct order
		List<String> virtualGroups = OrganizationManagerImpl.getVirtualGroups();
		for (String vg : virtualGroups) {
			String[] splits = vg.split("\\|");
			String gName = splits[0];
			String gDescription = splits[1];
			ArrayList<VRE> toCreate = new ArrayList<VRE>();
			VRECategory cat = new VRECategory(1L, gName, gDescription);
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

		ThemeDisplay themeDisplay = (ThemeDisplay) this.getThreadLocalRequest().getSession().getAttribute(WebKeys.THEME_DISPLAY);
		String imagePath = "/image";
		if(themeDisplay!=null){
			imagePath = themeDisplay.getPathImage();
		} 
		
		//for each root sub organizations (VO)
		for (Organization vOrg : rootOrganization.getSuborganizations()) {
			for (Organization vreOrganization : vOrg.getSuborganizations()) {
				long vreID =  vreOrganization.getOrganizationId();
				String vreName = vreOrganization.getName();
				String vreDescription = (vreOrganization.getComments()!=null) ? vreOrganization.getComments() : "";

				long logoId = vreOrganization.getLogoId();
				String vreLogoURL = String.format("%s/organization_logo?img_id=%s&t=%s", imagePath, logoId, ImageServletTokenUtil.getToken(logoId));
				String groupName = String.format("/%s/%s/%s", vOrg.getParentOrganization().getName(), vOrg.getName(), vreName);
				Group vreGroup = vreOrganization.getGroup();
				String friendlyURL = vreGroup.getPathFriendlyURL(true, themeDisplay) + vreGroup.getFriendlyURL();
				friendlyURL = String.format("%s%s", getPortalBasicUrl(), friendlyURL);

				VRECustomAttributes attrs = getVRECustomAttr(vreOrganization);
				
				boolean requireAccessGrant = attrs.isUponRequest();
				boolean isExternal = attrs.isExternal();
				String urlIfAny = attrs.getUrlIfAny();

				String catName = OrganizationManagerImpl.getVirtualGroupName(vreOrganization);
				String[] splits = catName.split("\\|");
				catName = splits[0];
								
				VRECategory toLookFor = null;
				for (VRECategory vre : toReturn.keySet()) {
					if (vre.getName().compareTo(catName)==0)
						toLookFor = vre;
				}
				if (toLookFor != null) {
					ArrayList<VRE> toUpdate = toReturn.get(toLookFor);
					toUpdate.add(new VRE(vreID,vreName, vreDescription, vreLogoURL, groupName,friendlyURL, UserBelonging.NOT_BELONGING, requireAccessGrant, isExternal, urlIfAny));
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

	
	@Override
	public Boolean joinVRE(Long vreID) {
		// Here for future improvement
		return new Boolean(true);
	}
}
