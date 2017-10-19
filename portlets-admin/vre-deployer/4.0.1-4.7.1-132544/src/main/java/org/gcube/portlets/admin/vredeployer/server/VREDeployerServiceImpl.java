package org.gcube.portlets.admin.vredeployer.server;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.vremanagement.vremanagement.impl.VREGeneratorEvo;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingSite;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingUser;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.portal.custom.communitymanager.SiteManagerUtil;
import org.gcube.portal.custom.communitymanager.ThemesIdManager;
import org.gcube.portal.custom.communitymanager.components.GCUBESiteLayout;
import org.gcube.portal.custom.communitymanager.impl.GCubeSiteManagerImpl;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.vredeployer.client.VredeployerService;
import org.gcube.portlets.admin.vredeployer.client.model.VREFunctionalityModel;
import org.gcube.portlets.admin.vredeployer.shared.GHNMemory;
import org.gcube.portlets.admin.vredeployer.shared.GHNProfile;
import org.gcube.portlets.admin.vredeployer.shared.GHNSite;
import org.gcube.portlets.admin.vredeployer.shared.ResourceCategory;
import org.gcube.portlets.admin.vredeployer.shared.ResourceCategoryItem;
import org.gcube.portlets.admin.vredeployer.shared.RunningInstance;
import org.gcube.portlets.admin.vredeployer.shared.VREDeployerStatusType;
import org.gcube.portlets.admin.vredeployer.shared.VREDescrBean;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientCloudReport;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientDeployReport;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientFunctionalityDeployReport;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientFunctionalityReport;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientResource;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientResourceManagerDeployingReport;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientResourcesDeployReport;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientServiceReport;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.DeployStatus;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vremanagement.vremodel.cl.stubs.types.FunctionalityItem;
import org.gcube.vremanagement.vremodel.cl.stubs.types.GHN;
import org.gcube.vremanagement.vremodel.cl.stubs.types.GHNsPerFunctionality;
import org.gcube.vremanagement.vremodel.cl.stubs.types.ResourceDescriptionItem;
import org.gcube.vremanagement.vremodel.cl.stubs.types.ResourceItem;
import org.gcube.vremanagement.vremodel.cl.stubs.types.RunningInstanceMessage;
import org.gcube.vremanagement.vremodel.cl.stubs.types.VREDescription;
import org.gcube.vremanagement.vremodeler.utils.Utils;
import org.gcube.vremanagement.vremodeler.utils.reports.DeployReport;
import org.gcube.vremanagement.vremodeler.utils.reports.FunctionalityReport;
import org.gcube.vremanagement.vremodeler.utils.reports.GHNonCloudReport;
import org.gcube.vremanagement.vremodeler.utils.reports.Resource;
import org.gcube.vremanagement.vremodeler.utils.reports.ServiceReport;
import org.gcube.vremanagement.vremodeler.utils.reports.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Theme;
import com.liferay.portal.model.User;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.ThemeLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;



/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class VREDeployerServiceImpl  extends RemoteServiceServlet implements VredeployerService {

	private static final Logger log = LoggerFactory.getLogger(VREDeployerServiceImpl.class);

	/**
	 * during the vre phase creation we need to update the Application Profile of Calendar and News feed
	 */
	private static final String CALENDAR_APPID = "org.gcube.portal.calendarwrapper.GCubeCalendarHandler";
	private static final String NEWS_FEED_APPID = "org.gcube.portlets.user.newsfeed.server.NewsServiceImpl";

	protected static final String ORGANIZATION_DEFAULT_LOGO = "/org/gcube/portal/custom/communitymanager/resources/default_logo.png";
	protected static final String ORGANIZATION_DEFAULT_LOGO_URL = "http://ftp.d4science.org/apps/profiles/d4slogo.png";

	private static final int LIFERAY_REGULAR_ROLE_ID = 1;
	/**
	 * 
	 */
	private static final String VRE_GENERATOR_ATTRIBUTE = "VREGenerator";
	private static final String GHN_PER_FUNC_ATTRIBUTE = "GHN_PER_FUNC_ATTRIBUTE";

	private static final String HARD_CODED_VO_NAME = "/gcube/devsec";
	private static final String APPROVING_VRE = "approvingVRE";
	public static final String APPROVE_MODE = "approve";
	public static final String EDIT_MODE = "edit";
	public static final String REEDIT_TYPE_ATTRIBUTE = "reeditType";
	public static final String MODE_ATTRIBUTE = "mode";
	private static final String DESCRIPTION = "DESCRIPTION";
	private static final String DEPLOYING = "DEPLOYING";

	private static final String DESIGNER = "Designer";
	private static final String MANAGER = "Manager";

	private static final String VRE_MANAGER = "VRE-Manager";
	private static final String VRE_DESIGNER = "VRE-Designer";

	/**
	 * 
	 */
	private boolean isTesting = true;
	String customEPR = "1d648460-2d05-11e3-9f6f-a17a856bd44f";
	/**
	 * 
	 * @return
	 */
	private VREGeneratorEvo getVREGeneratorEvo(ASLSession aslSession){
		log.info("getVREGeneratorEvo called with scope: " + aslSession.getScopeName());
		String vreid = (String) aslSession.getAttribute(VRE_GENERATOR_ATTRIBUTE);
		if(vreid==null){
			return null;
		}
		return new VREGeneratorEvo(aslSession, vreid); //cannot cache VREGeneretor instance;
	}

	private void setDeployingStatusOn() {
		getASLSession().setAttribute(DEPLOYING, "ON");
	}

	private void setDeployingStatusOff() {
		getASLSession().setAttribute(DEPLOYING, null);
	}

	private boolean isDeploying() {
		return getASLSession().getAttribute(DEPLOYING) != null;
	}

	/**
	 * 
	 * @return
	 */
	private ASLSession getASLSession() {
		log.info("getVREGeneratorEvo  getASLSession() : ");
		HttpSession session = this.getThreadLocalRequest().getSession();
		String username = (String) session.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (username == null) {
			username = "massimiliano.assante";
			SessionManager.getInstance().getASLSession(session.getId(), username).setScope(HARD_CODED_VO_NAME);
			SessionManager.getInstance().getASLSession(session.getId(), username).setAttribute(REEDIT_TYPE_ATTRIBUTE, APPROVE_MODE);
		}
		else {
			isTesting = false;
		}
		return SessionManager.getInstance().getASLSession(session.getId(), username);
	}

	/**
	 * {@inheritDoc}
	 */
	public VREDeployerStatusType isApprovingModeEnabled() {
		if (isTesting) {
			setDeployingStatusOff();
			return VREDeployerStatusType.APPROVE;

		}

		if (isDeploying())
			return VREDeployerStatusType.DEPLOYING;

		log.debug("---  isApprovingModeEnabled in Log   ---");

		ASLSession aslSession = getASLSession();
		aslSession.setAttribute(MODE_ATTRIBUTE, null);

		String reeditType = (String) aslSession.getAttribute(REEDIT_TYPE_ATTRIBUTE);
		log.debug("REEDIT_TYPE_ATTRIBUTE = " + reeditType);
		aslSession.setAttribute(REEDIT_TYPE_ATTRIBUTE, null);

		if ( reeditType != null && reeditType.compareTo(APPROVE_MODE) == 0) {
			System.out.println(APPROVING_VRE + " = " + true);
			aslSession.setAttribute(MODE_ATTRIBUTE,APPROVING_VRE);
			return VREDeployerStatusType.APPROVE;
		}
		else{
			/* This is needed to avoid to log out and log in again to create a new VRE */
			aslSession.setAttribute(VRE_GENERATOR_ATTRIBUTE, null);
		}
		aslSession.setAttribute(REEDIT_TYPE_ATTRIBUTE, null);

		return VREDeployerStatusType.NON_APPROVE;
	}


	/**
	 * return the VRE Overall Information
	 */
	public VREDescrBean getVRE() throws NullPointerException {
		VREGeneratorEvo vreGenerator = null;
		if (isTesting) {
			vreGenerator = new VREGeneratorEvo(getASLSession(), customEPR);
		}
		else {
			ASLSession aslSession = getASLSession();
			vreGenerator = getVREGeneratorEvo(aslSession);
			log.info("VRE EPR: " + vreGenerator.getVREepr());
		}		

		VREDescrBean vreDescBean = null;
		log.debug("---   Getting VRE Model   ---");
		try {
			VREDescription sd = vreGenerator.getVREModel();
			vreDescBean = new VREDescrBean(sd.name(), sd.description(), sd.designer(), sd.manager(), sd.startTime().getTime(), sd.endTime().getTime());
			getASLSession().setAttribute(DESIGNER,  sd.designer());
			getASLSession().setAttribute(MANAGER,   sd.manager());
		} catch (RemoteException e) {
			e.printStackTrace();
		}	
		log.debug("Model: " + vreDescBean.getName());
		log.debug("--- END Getting VRE Model ---");
		return vreDescBean;

	}



	/**
	 * return the name and last name of the user
	 * @param username
	 * @return
	 */
	private String getFullname(String screenName) {
		UserManager um = new LiferayUserManager();
		GCubeUser user = null;
		try {
			user = um.getUserByScreenName(screenName);
		} catch (UserManagementSystemException e) {
			e.printStackTrace();
		} catch (UserRetrievalFault e) {
			e.printStackTrace();
		} 
		return user.getFullname();
	}




	/**
	 * read the available functionality from the service through ASL extension
	 */
	@Override
	public VREFunctionalityModel getFunctionality()  {

		VREFunctionalityModel toReturn = new VREFunctionalityModel("","selected functionality", "", "",false);

		HashMap<String, ArrayList<ResourceCategory>> funCategories = new HashMap<String, ArrayList<ResourceCategory>>();

		List<FunctionalityItem> list = null;
		VREGeneratorEvo vreGenerator = null;
		try {
			if (isTesting) 
				vreGenerator = new VREGeneratorEvo(getASLSession(), customEPR);
			else {
				ASLSession aslSession = getASLSession();
				vreGenerator = getVREGeneratorEvo(aslSession);
				log.info("VRE EPR: " + vreGenerator.getVREepr());
			}		
			list = vreGenerator.getFunctionality();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (list == null) {
			log.warn("FunctionalityList NULL");
			return null;
		} 

		for (FunctionalityItem fi :list){
			log.info(fi.id()+"-"+fi.name());

			VREFunctionalityModel toAdd = new VREFunctionalityModel(Integer.toString(fi.id()),
					fi.name(), fi.description(), "",fi.selected());

			if (fi.children() != null) {
				List<FunctionalityItem> children = fi.children();
				ArrayList<VREFunctionalityModel> newchildrens = new ArrayList<VREFunctionalityModel>();
				//creating node children
				for (FunctionalityItem child : children) {
					log.info(child.name() + " is " + child.selected());
					if (child.selected()) {
						VREFunctionalityModel subFunc = new VREFunctionalityModel(Integer.toString(child.id()),
								child.name(), child.description(),
								"functionality-add-icon",child.selected());
						newchildrens.add(subFunc);

						ArrayList<VREFunctionalityModel> resourceChildren = new ArrayList<VREFunctionalityModel>();
						if ( child.selectableResourcesDescription()!=null) {
							for (ResourceDescriptionItem category:  child.selectableResourcesDescription()) {
								if (category.resources()!=null) 
									for (ResourceItem resource : category.resources()) 
										if (resource.selected()) 
											resourceChildren.add(new VREFunctionalityModel(resource.id(), resource.name(), resource.description(), "extres-icon", resource.selected()));
							}
							//subFunc.addChildren(resourceChildrens.toArray(new VREFunctionalityModel[resourceChildrens.size()]));
						}
						AdditionalFuncInfo addInfo = getServicesAndGHNs(subFunc.getId());

						if (addInfo != null) {
							RunningInstanceMessage[] ris = addInfo.getMissingServices();

							for (int j = 0; j < ris.length; j++) {
								RunningInstanceMessage ri = ris[j];
								resourceChildren.add(new VREFunctionalityModel("", ri.serviceName() + " (" + ri.serviceClass()+")", "", "missing-ri", true));
							}
							ris = addInfo.getFoundServices();
							for (int j = 0; j < ris.length; j++) {
								RunningInstanceMessage ri = ris[j];
								resourceChildren.add(new VREFunctionalityModel("", ri.serviceName() + " (" + ri.serviceClass()+")", "", "runninginstance-icon", true));
							}

							GHN[] relGHNs = addInfo.getGhns();
							for (int j = 0; j < relGHNs.length; j++) {
								GHN ghn = relGHNs[j];
								resourceChildren.add(new VREFunctionalityModel("", ghn.host() + " (" + ghn.site().domain()+")", "", "architecture-icon", true));
							}
						}
						else
							log.error("getServicesAndGHNs per subfunctionality returns NULL");
						subFunc.addChildren(resourceChildren.toArray(new VREFunctionalityModel[resourceChildren.size()]));
					}
				}
				if (newchildrens.size() > 0) {
					toAdd.addChildren(newchildrens.toArray(new VREFunctionalityModel[newchildrens.size()]));
					toReturn.add(toAdd);
				}
			}	
		}

		for (String func : funCategories.keySet()) {
			System.out.println("-"+func);
			for(ResourceCategory category : funCategories.get(func)) {
				System.out.println("--"+category.getName());
				for(ResourceCategoryItem rc: category.getItems()) {
					System.out.println("----"+rc.getName() + " : " + rc.isSelected());
				}
			}
		}
		return toReturn;
	}

	@Override
	public void getGHNPerFunctionality(String funcId) {
		VREGeneratorEvo vreGenerator = null;
		if (isTesting) 
			vreGenerator = new VREGeneratorEvo(getASLSession(), customEPR);
		else  {
			ASLSession aslSession = getASLSession();
			vreGenerator = getVREGeneratorEvo(aslSession);
		}
		int funcToLookFor = Integer.parseInt(funcId);

		try {
			GHNsPerFunctionality[] ghnsPerFunc = vreGenerator.getGHNsPerFunctionality();
			GHNsPerFunctionality toWorkWith = null;
			for (int i = 0; i < ghnsPerFunc.length; i++) 
				if (funcToLookFor == ghnsPerFunc[i].id()) {
					toWorkWith = ghnsPerFunc[i];
					break;
				}
			if (toWorkWith == null) 
				return;
			List<RunningInstanceMessage> services =  toWorkWith.foundServices();
			if (services != null) {
				for (RunningInstanceMessage se : services) {
					System.out.println(se.serviceName() + " - " + se.serviceClass());
				}
			}

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}


	private void setGHNsPerFunctionalityInSession(GHNsPerFunctionality[] ghnsPerFunc) {
		getASLSession().setAttribute(GHN_PER_FUNC_ATTRIBUTE, ghnsPerFunc);
	}

	private GHNsPerFunctionality[] getGHNsPerFunctionalityFromSession() {
		return (GHNsPerFunctionality[]) getASLSession().getAttribute(GHN_PER_FUNC_ATTRIBUTE);
	}
	/**
	 * get the list of running instances associated to a service
	 * @param funcId
	 * @return
	 */
	private AdditionalFuncInfo getServicesAndGHNs(String funcId) {
		AdditionalFuncInfo toReturn = new AdditionalFuncInfo();
		try {
			GHNsPerFunctionality[] ghnsPerFunc = null;

			if (getGHNsPerFunctionalityFromSession() == null) { //avoid multiple calls to the service
				VREGeneratorEvo vreGenerator = null;
				if (isTesting) 
					vreGenerator = new VREGeneratorEvo(getASLSession(), customEPR);
				else {
					ASLSession aslSession = getASLSession();
					vreGenerator = getVREGeneratorEvo(aslSession);
				}
				ghnsPerFunc = vreGenerator.getGHNsPerFunctionality();
				setGHNsPerFunctionalityInSession(ghnsPerFunc);
			}
			else {
				ghnsPerFunc = getGHNsPerFunctionalityFromSession();
			}
			int funcToLookFor = Integer.parseInt(funcId);
			GHNsPerFunctionality ghnPF = null;
			for (int i = 0; i < ghnsPerFunc.length; i++) {
				if (funcToLookFor == ghnsPerFunc[i].id()) {	
					ghnPF = ghnsPerFunc[i];	
					break;
				}
			}
			if (ghnPF == null || ghnPF.missingServices() == null) 
				toReturn.setMissingServices(new RunningInstanceMessage[0]);
			else {
				toReturn.setMissingServices(ghnPF.missingServices().toArray(new RunningInstanceMessage[0]));
			}
			if (ghnPF == null || ghnPF.foundServices() == null) 
				toReturn.setFoundServices(new RunningInstanceMessage[0]);
			else {
				toReturn.setFoundServices(ghnPF.foundServices().toArray(new RunningInstanceMessage[0]));
			}
			if (ghnPF == null || ghnPF.ghns() == null) 
				toReturn.setGhns(new GHN[0]);
			else
				toReturn.setGhns(ghnPF.ghns().toArray(new GHN[0]));
			return toReturn;
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * return the ghn available list
	 * @return
	 * @throws RemoteException 
	 */
	@Override
	public List<GHNProfile> getAvailableGHNs() {
		List<GHNProfile> toReturn = new ArrayList<GHNProfile>();
		VREGeneratorEvo vreGenerator = null;
		if (isTesting) {
			vreGenerator = new VREGeneratorEvo(getASLSession(), customEPR);
		}
		else {
			ASLSession aslSession = getASLSession();
			vreGenerator = getVREGeneratorEvo(aslSession);
		}	

		List<GHN> ghns = null;
		try {
			log.debug("Asking gHN list");
			ghns = vreGenerator.getGHNs();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (ghns == null)
			return toReturn;
		log.debug("got gHN list, menbers: " + ghns.size());
		for (GHN ghn : ghns) {
			List<RunningInstance> ris = new ArrayList<RunningInstance>();
			if (ghn.relatedRIs() != null) {
				for (int i = 0; i < ghn.relatedRIs().size(); i++) {
					ris.add(new RunningInstance(ghn.relatedRIs().get(i).serviceName(), ghn.relatedRIs().get(i).serviceClass()));
				}
			}

			toReturn.add(new GHNProfile(ghn.id(), ghn.host(), ris, ghn.securityEnabled(),
					new GHNMemory(ghn.memory().memorySize()+"", ghn.memory().diskSpace()+""),	
					new GHNSite(ghn.site().location(), ghn.site().country(), ghn.site().domain()), null,
					ghn.selected()) );

		}			
		return toReturn;

	}


	/**
	 * 
	 * @param selectedGHNIds
	 * @param idCandidateGHN
	 * @return
	 */
	public boolean setGHNsSelected(String[] selectedGHNIds) {
		VREGeneratorEvo vreGenerator = null;
		if (isTesting) {
			vreGenerator = new VREGeneratorEvo(getASLSession(), customEPR);
		}
		else {
			ASLSession aslSession = getASLSession();
			vreGenerator = getVREGeneratorEvo(aslSession);
		}	

		try {
			log.debug(" selectedIDs");
			for (int i = 0; i < selectedGHNIds.length; i++) {
				System.out.println(" id: " + selectedGHNIds[i].toString());
			}
			vreGenerator.setGHNs(selectedGHNIds);
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @return
	 */
	public int isCloudSelected() {

		VREGeneratorEvo vreGenerator = null;
		if (isTesting) {
			vreGenerator = new VREGeneratorEvo(getASLSession(), customEPR);
		}
		else {
			ASLSession aslSession = getASLSession();
			vreGenerator = getVREGeneratorEvo(aslSession);
		}	
		if (vreGenerator.isCloudSelected())
			return vreGenerator.getCloudVMSelected();
		else
			return -1;
	}


	/**
	 * 	
	 */
	public int getCloudVMSelected() {

		VREGeneratorEvo vreGenerator = null;
		if (isTesting) {
			vreGenerator = new VREGeneratorEvo(getASLSession(), customEPR);
		}
		else {
			ASLSession aslSession = getASLSession();
			vreGenerator = getVREGeneratorEvo(aslSession);
		}	
		return vreGenerator.getCloudVMSelected();
	}

	/**
	 * 
	 * @param virtualMachines
	 * @return
	 */
	public boolean setCloudDeploy(int virtualMachines) {
		VREGeneratorEvo vreGenerator = null;
		if (isTesting) {
			vreGenerator = new VREGeneratorEvo(getASLSession(), customEPR);
		}
		else {
			ASLSession aslSession = getASLSession();
			vreGenerator = getVREGeneratorEvo(aslSession);
		}	

		return vreGenerator.setCloudDeploy(virtualMachines);
	}


	public boolean deployVRE() {
		log.info("--- deployVRE started ---");
		VREGeneratorEvo vreGenerator = null;
		if (isTesting) {
			vreGenerator = new VREGeneratorEvo(getASLSession(), customEPR);
		}
		else {
			ASLSession aslSession = getASLSession();
			vreGenerator = getVREGeneratorEvo(aslSession);
		}	
		try {
			vreGenerator.deployVRE();
			//need time to prepare report
			log.info("--- SLEEP 2 seconds ---");
			Thread.sleep(2500);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		//TODO: re enable in the future it gives exception		
		//		AccessLogger log = AccessLogger.getAccessLogger();
		//		CreatedVRELogEntry logEntry;
		//		try {
		//			logEntry = new CreatedVRELogEntry(
		//					vreGenerator.getVREModel().name(),
		//					vreGenerator.getVREepr(),
		//					vreGenerator.getVREModel().designer(),
		//					vreGenerator.getVREModel().manager());
		//			log.logEntry(getASLSession().getUsername(), getASLSession().getScopeName(), logEntry);
		//		} catch (RemoteException e) {
		//			e.printStackTrace();
		//		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public ClientDeployReport checkCreateVRE() {
		log.info("--- check Create VRE started ---");

		VREGeneratorEvo vreGenerator = null;
		if (isTesting) {
			try {
				vreGenerator = new VREGeneratorEvo(getASLSession(), customEPR);
			}
			catch (NullPointerException e) {
				log.error("Error while trying to contact VRE Modeler service with TEST ID: " + customEPR +
						" Probly does not exist anymore, request from " + this.getThreadLocalRequest().getRemoteHost() + "("+this.getThreadLocalRequest().getRemoteAddr()+")");
				return new ClientDeployReport();
			}
		}
		else {
			ASLSession aslSession = getASLSession();
			vreGenerator = getVREGeneratorEvo(aslSession);
		}	
		DeployReport report = null;

		try {
			report = vreGenerator.checkVREStatus();
		} catch (Exception e) {
			log.error("Error while trying to retrieve VRE Status, return Empty Report" + e.getMessage() + "\n, " +
					"request from " + this.getThreadLocalRequest().getRemoteHost() + "("+this.getThreadLocalRequest().getRemoteAddr()+")");
			return new ClientDeployReport();
		}

		if (report == null || report.getStatus() == null) {
			log.error("--- DeployReport is NULL or Status is null, return Empty Report, " +
					"request from " + this.getThreadLocalRequest().getRemoteHost() + "("+this.getThreadLocalRequest().getRemoteAddr()+")");
			return new ClientDeployReport();
		}

		if (report.getStatus() == Status.Finished) {
			log.info("--- Create VRE COMPLETED, CREATING LAYOUTS AND COMMUNITY ... ");
			String name = "";
			String description = "";
			try {
				VREDescription de = vreGenerator.getVREModel();
				name = de.name();
				description = de.description();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			String designer = (String) getASLSession().getAttribute(DESIGNER);
			String manager =  (String) getASLSession().getAttribute(MANAGER);

			log.info("ADDING user designer and manager, found designer: " + designer + " found manager: "+manager);

			long vreCreatedId = createCommunityAndLayoutAndHLFolder(name, description);

			log.info("---  createCommunityAndLayout COMPLETED, vreCreatedId="+vreCreatedId);

			if (vreCreatedId > 0) {
				try {
					org.gcube.vomanagement.usermanagement.model.GCubeGroup vreCreated = new LiferayGroupManager().getGroup(vreCreatedId);
					log.info("---  CREATED LAYOUTS AND COMMUNITY OK, updating Calendar Application Profile.");
					String vreScope = new LiferayGroupManager().getInfrastructureScope(vreCreatedId);
					String vreUrl = "/group/"+vreCreated.getGroupName().toLowerCase();
					boolean calandarAppProfileUpdated = false;
					try {
						calandarAppProfileUpdated = updateApplicationProfile(CALENDAR_APPID, vreScope, vreUrl+"/calendar");
					} 
					catch (Exception e) {
						log.error("Something wrong in updateApplicationProfile for " + CALENDAR_APPID);
					}
					boolean newsFeedAppProfileUpdated = false;
					try {
						newsFeedAppProfileUpdated = updateApplicationProfile(NEWS_FEED_APPID, vreScope, vreUrl);  //assumes it is deployed in the home of the VRE
					}
					catch (Exception e) {
						log.error("Something wrong in updateApplicationProfile for " + NEWS_FEED_APPID);
					}
					log.info("updateApplicationProfile for " + CALENDAR_APPID + "="+calandarAppProfileUpdated);
					log.info("updateApplicationProfile for " + NEWS_FEED_APPID + "="+newsFeedAppProfileUpdated);

					UserManager um = new LiferayUserManager();
					GCubeUser userDesigner = um.getUserByUsername(designer);
					GCubeUser userManager = um.getUserByUsername(manager);

					Workspace workspace = HomeLibrary.getUserWorkspace(getASLSession().getUsername());
					ArrayList<String> toSend = new ArrayList<String>();
					toSend.add(designer);
					String subject = "Definition approved and deployed";
					String body = "Dear "+userDesigner.getFirstName()+", \n\n" + userManager.getFullname() + " has approved the deployment of the group you requested: " + name +".";
					body+=".\n\nThis group has been deployed successfully and is already available for you on this portal. Please, check your list.";
					String messageId = workspace.getWorkspaceMessageManager().sendMessageToPortalLogins(subject, body, new ArrayList<String>(), toSend);
					NotificationsManager nnm = new ApplicationNotificationsManager(
							new SocialNetworkingSite(getThreadLocalRequest()),							
							getASLSession().getScope(),
							new SocialNetworkingUser(getASLSession().getUsername(), getASLSession().getUserEmailAddress(), getASLSession().getUserFullName(), "")
							);
					if (nnm.notifyMessageReceived(designer, messageId, subject, body))
						log.trace("Sending Definition create notification: " + subject + " OK");
				} catch (Exception e) {
					e.printStackTrace();
				}
				//log.info("---  Trying to share a news for this VRE");
				//shareCreatedVRENews(designer, manager, name, description);




			} else
				log.error("---  DANGER DANGER DANGER!!!!! -> CREATED LAYOUTS AND COMMUNITY WITH ERRORS");

		}
		else 
			setDeployingStatusOn();

		//jsut for testing
		//		if (isTesting)
		//			return convertServiceDeployReport(simulateReport());

		log.debug("---Sending Report, globalState --- " + report.getStatus() );
		return convertServiceDeployReport(report);
	}
	/**
	 * update the ApplicationProfile of the appid for this scope
	 * @param appId the portlet class name as indicated in the generic resource published at root level 
	 * @param vreScope the scope to add
	 * @param vreURL the absolute URL of the portlet
	 */
	private boolean updateApplicationProfile(String appId, String vreScope, String vreURL) {

		String currScope = ScopeProvider.instance.get();
		String scopeToQuery = PortalContext.getConfiguration().getInfrastructureName();
		ScopeProvider.instance.set("/"+scopeToQuery);

		String endpoint2Add = "<EndPoint><Scope>"+vreScope+"</Scope><URL>"+vreURL+"</URL></EndPoint>";

		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq 'ApplicationProfile'");
		query.addCondition("$resource/Profile/Body/AppId/text() eq '" + appId + "'");

		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);

		List<GenericResource> list = client.submit(query);
		if (list == null || list.isEmpty()) {
			log.warn("Cannot retrieve the ApplicationProfile from IS for generic resource having  <Body><AppId> = " + appId);
			log.info("Triggering Creation of ApplicationProfile for " + appId);
			GenericResource toCreate = new GenericResource();
			toCreate.newProfile().name("Application Profile for " + appId);
			toCreate.profile().type("ApplicationProfile");
			toCreate.profile().description("Application Profile description for " + appId);
			toCreate.profile().newBody("<AppId>"+appId+"</AppId><ThumbnailURL>No thumbnail</ThumbnailURL>"+endpoint2Add);
			RegistryPublisher rp=RegistryPublisherFactory.create();
			rp.create(toCreate);
			log.info("Creation of ApplicationProfile for " + appId + " OK!");
			return false;
		}

		GenericResource gr = list.get(0);
		log.debug("updating ApplicationProfile for " + gr.profile().name());

		Node fragmentNode;
		try {
			DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Element elem = gr.profile().body();
			fragmentNode = docBuilder.parse(new InputSource(new StringReader(endpoint2Add))).getDocumentElement();
			fragmentNode = elem.getOwnerDocument().importNode(fragmentNode, true);
			elem.appendChild(fragmentNode);
		} catch (Exception e) {
			//in case no xml is entered, just text
			log.error("error");
		}
		RegistryPublisher rp=RegistryPublisherFactory.create();
		rp.update(gr);
		ScopeProvider.instance.set(currScope);
		return true;


	}
	/**
	 * this method is in charge of reflecting the VRE deployed in a gCube Portal context in the HomeLibrary JackRabbit Repository.
		It also creates shared folders for these groups and assign VRE-Managers as Administrators of such foldes 
	 * @param vreName
	 * @param vreScope
	 * @param designer the username of the designer
	 * @param manager the username of the manager
	 * @param description
	 * @throws GroupRetrievalFault 
	 * @throws UserManagementSystemException 
	 */
	private void createVRESharedGroupFolder(org.gcube.vomanagement.usermanagement.model.GCubeGroup vreCreated, String designer, String manager, String description) throws Exception {

		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set("/"+getRootOrganizationName());
		GroupManager gm = new LiferayGroupManager();
		UserManager um = new LiferayUserManager();

		org.gcube.vomanagement.usermanagement.model.GCubeGroup group = vreCreated;
		long groupId = group.getGroupId();

		String vreName = group.getGroupName();
		String vreScope = gm.getInfrastructureScope(groupId);

		List<GCubeUser> users = um.listUsersByGroup(group.getGroupId());
		String vreDesignerUserName = designer;		
		if (vreDesignerUserName != null) {
			Workspace ws = HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome(vreDesignerUserName)
					.getWorkspace();

			GCubeGroup gGroup = createGroup(vreScope, users);
			String groupid = (gGroup == null) ? vreScope :  gGroup.getName();
			WorkspaceSharedFolder wSharedFolder =createVREFolder(vreScope, vreName, groupid, ws);

			List<String> groups = new ArrayList<String>();
			groups.add(gGroup.getName());
			wSharedFolder.setACL(groups, ACLType.WRITE_OWNER);

		} else {
			log.error("NO VRE-MANAGER FOUND IN THIS VRE");			
		}
		ScopeProvider.instance.set(currScope);
	}

	/**
	 * return the infrastructure name 
	 */
	private static String getRootOrganizationName() {
		return PortalContext.getConfiguration().getInfrastructureName();
	}
	/**
	 * 
	 * @param vreScope
	 * @param vreName
	 * @param groupId
	 * @param ws
	 * @return
	 * @throws Exception
	 */
	private static WorkspaceSharedFolder createVREFolder(String vreScope, String vreName, String groupId, Workspace ws) throws Exception {		

		WorkspaceSharedFolder folder = ws.createSharedFolder(vreScope, "Special Shared folder for VRE " + vreName, groupId, ws.getRoot().getId(), vreName, true);
		System.out.println(folder.getPath());
		return folder;

	}
	/**
	 * Create the group in HL and assign users to it
	 * @param vreScope the scope of the vre as name
	 * @param usersToAdd the listo of users to add
	 * @throws InternalErrorException
	 */
	private static GCubeGroup createGroup(String vreScope, List<GCubeUser> usersToAdd) throws InternalErrorException {
		org.gcube.common.homelibrary.home.workspace.usermanager.UserManager gm = HomeLibrary
				.getHomeManagerFactory().getUserManager();
		GCubeGroup group = gm.createGroup(vreScope);
		for (GCubeUser user : usersToAdd) {
			group.addMember(user.getUsername());
		}
		return group;
	}





	/**
	 * 
	 * @return the html representation of the report
	 */
	public String getHTMLReport() {
		log.info("--- getHTMLReport VRE  ---");

		VREGeneratorEvo vreGenerator = null;
		if (isTesting) {
			vreGenerator = new VREGeneratorEvo(getASLSession(), customEPR);
		}
		else {
			ASLSession aslSession = getASLSession();
			vreGenerator = getVREGeneratorEvo(aslSession);
		}	

		String  report = null;

		try {
			report = Utils.toXML(vreGenerator.checkVREStatus());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		String startDirectory = this.getServletContext().getRealPath("");
		String xslFileLocation = startDirectory + "/styles/report.xsl";

		String transformed = "";
		if (isTesting) {

			String pathXML = startDirectory + "/xml/report.xml";			
			log.info("--- pathXML: " + pathXML);
			log.info("--- xsl: " + xslFileLocation);
			transformed = transformToHtml(fileToString(pathXML), xslFileLocation);
		}
		else {
			transformed = transformToHtml(report, xslFileLocation);
		}

		return transformed;	
	}




	/**
	 * convert the service report to a report client sendable
	 * @param toConvert
	 * @return
	 */
	private ClientDeployReport convertServiceDeployReport(DeployReport toConvert) {

		ClientDeployReport toReturn = new ClientDeployReport();

		//*** Overall deploy status report part
		toReturn.setGlobalStatus(convStatus(toConvert.getStatus()));


		//*** Cloud deploy report part
		GHNonCloudReport cDeploy = toConvert.getCloudDeployingReport();

		if (isCloudSelected() != -1) {
			System.out.println("***** GHNonCloudReportSelected ******");
			List<DeployStatus> singleCloudStatus = new LinkedList<DeployStatus>();
			for (int i = 0; i < cDeploy.getDeployingState().length; i++) {
				singleCloudStatus.add(convStatus(cDeploy.getDeployingState()[i]));
			}

			toReturn.setCloudReport(new ClientCloudReport(convStatus(cDeploy.getStatus()), singleCloudStatus));
		}
		else {
			System.out.println("***** GHNonCloudReport Not Selected ******");
			ClientCloudReport cdp = new ClientCloudReport();
			cdp.setStatus(DeployStatus.SKIP);
			toReturn.setCloudReport(cdp);
		}
		//*** ResourceManager deploy report part
		toReturn.setResourceManagerReport(
				new ClientResourceManagerDeployingReport(convStatus(toConvert.getResourceDeployingReport().getStatus()), toConvert.getResourceDeployingReport().toString()));

		//*** Functionality deploy report part
		ClientFunctionalityDeployReport cfDeployReport = new ClientFunctionalityDeployReport();


		Hashtable<FunctionalityReport,List<ServiceReport>> table = toConvert.getFunctionalityDeployingReport().getFunctionalityTable();
		HashMap<ClientFunctionalityReport, List<ClientServiceReport>> newTable = new HashMap<ClientFunctionalityReport, List<ClientServiceReport>>(); 

		//creating new hashtable
		for (FunctionalityReport fr : table.keySet()) {
			List<ClientServiceReport> theList = new LinkedList<ClientServiceReport>();			
			for (ServiceReport sr : table.get(fr)) 
				theList.add(new ClientServiceReport(sr.getServiceName(), sr.getServiceClass(), sr.getServiceVersion()));
			//adding new key and payload
			newTable.put(new ClientFunctionalityReport(fr.getFunctionalityId(), fr.getFunctionalityName(), convStatus(fr.getState())), theList);
		}

		cfDeployReport.setStatus(convStatus(toConvert.getFunctionalityDeployingReport().getStatus()));
		cfDeployReport.setReportXML(toConvert.getFunctionalityDeployingReport().getResourceManagerReport());
		cfDeployReport.setFunTable(newTable);
		toReturn.setFunctionalityReport(cfDeployReport);

		//*** Resource deploy report part

		List<ClientResource> newResources = new LinkedList<ClientResource>();	
		for (Resource res : toConvert.getResourceDeployingReport().getResources()) {
			newResources.add(new ClientResource(res.getResourceId(), res.getResourceType(), convStatus(res.getStatus())));
		}

		toReturn.setResourcesReport(new ClientResourcesDeployReport(
				convStatus(toConvert.getResourceDeployingReport().getStatus()), newResources));

		return toReturn;
	}


	/**
	 * 
	 * @param status to convert
	 * @return
	 */
	private DeployStatus convStatus(Status status) {
		switch (status) {
		case Failed:
			return DeployStatus.FAIL;
		case Finished:
			return DeployStatus.FINISH;
		case Pending:
			return DeployStatus.PENDING;
		case Running:
			return DeployStatus.RUN;
		case Skipped:
			return DeployStatus.SKIP;
		case Waiting:
			return DeployStatus.WAIT;
		default:
			return DeployStatus.FAIL;
		}
	}

	/**
	 * 
	 * @param vreName
	 * @return
	 */
	private boolean vreExists(String vreName) {
		GroupManager gm = new LiferayGroupManager();
		try {
			return gm.getGroupId(vreName) >= 0;
		} catch (UserManagementSystemException | GroupRetrievalFault e) {
			return false;
		}
	}

	/**
	 * Creates the community and its layout in Liferay
	 */
	private long createCommunityAndLayoutAndHLFolder(String vreName, String description) {

		if (vreExists(vreName)) {
			log.warn("VRE Exists already");
			return -1;
		}

		ASLSession session =  getASLSession();
		log.info("TRYING READING CURRENT VO ID");
		GroupManager gm = new LiferayGroupManager();
		long parentVirtualOrganizationId = -1;
		try {
			parentVirtualOrganizationId = gm.getGroupIdFromInfrastructureScope(session.getScope());
		} catch (IllegalArgumentException | UserManagementSystemException
				| GroupRetrievalFault e1) {
			e1.printStackTrace();
		}
		log.info("CURRENT VO ID="+parentVirtualOrganizationId);

		String desc = (session.getAttribute(DESCRIPTION) == null) ? "No Description found": session.getAttribute(DESCRIPTION).toString(); //desc


		String designer = (String) session.getAttribute(DESIGNER);
		log.info("Designer found Name : " + designer);	
		String manager =  (String) session.getAttribute(MANAGER);
		log.info("Manager found Name : " + manager);

		try {

			String themid = "";
			themid = SiteManagerUtil.getgCubeThemeId(ThemesIdManager.GCUBE_LOGGEDIN_THEME);

			log.info("Assigning theme with id: " + themid + " to VRE in creation");

			GCUBESiteLayout siteLayout = GCubeSiteManagerImpl.getBaseLayout(vreName, false, session.getUsername());
			org.gcube.vomanagement.usermanagement.model.GCubeGroup vreCreated =  gm.createVRE(vreName, parentVirtualOrganizationId, desc);
			
			long groupId = vreCreated.getGroupId();			
			Group lrGroup = GroupLocalServiceUtil.getGroup(groupId);
			GCubeUser userBean = new LiferayUserManager().getUserByUsername(session.getUsername());
			User lrUser = UserLocalServiceUtil.getUser(userBean.getUserId());
			
			SiteManagerUtil.createLayout(lrGroup, lrUser, siteLayout);
			
			Theme themeToApply = ThemeLocalServiceUtil.getTheme(SiteManagerUtil.getCompany().getCompanyId(), themid, false);
			//update theme
			LayoutSetLocalServiceUtil.updateLookAndFeel(groupId, themeToApply.getThemeId(), "", "", false);
			log.debug("LayoutSet Theme with id " + themid +  " Applied Correctly");
			
			log.info("Assigned theme with id: " + themid + " to groupid: " + groupId);

			//the method above create a VRE and assign the manager Role to the person that triggers the creation
			//however the VRE-Designer and the VRE-Manager persons of the VRE could be different and need to be created too
			UserManager uman = new LiferayUserManager();
			//if the manager is not the one who triggered the creation
			String currUser = session.getUsername();
			log.info("***Username of who triggered the creation is: " + currUser);			
			
			log.info("Trying to create VRE Group Folder through HomeLibrary ...");
			createVRESharedGroupFolder(vreCreated, designer, manager, description);

			log.info("--- createVRESharedGroupFolder OK, sending Message to designer.");

			RoleManager rm = new LiferayRoleManager();

			if (manager.compareTo(currUser) != 0) {
				//add the role VRE-Manager
				long userId = uman.getUserId(manager);
				uman.assignUserToGroup(groupId, userId);
				log.info("Added manager " + manager + " to group " + vreName + " with Success");	

				log.info("Assigning Role:  VRE-Manager");
				long roleId = rm.getRoleIdByName(VRE_MANAGER);
				rm.assignRoleToUser(userId, groupId, roleId);
				log.info("Admin Role VRE-Manager Associated to user " + manager +  " .... returning ...");

			}
			//if the designer is different
			if (designer.compareTo(manager) != 0) {
				//add the role VRE-Designer
				long userId = uman.getUserId(designer);		
				uman.assignUserToGroup(groupId, userId);
				log.info("Added designer " + designer + " to group " + vreName + " with Success");	
				long roleId = rm.getRoleIdByName(VRE_DESIGNER);
				rm.assignRoleToUser(userId, groupId, roleId);
				log.info("Admin Role VRE-Designer Associated to user " + designer +  " .... returning ...");						
			}

			return groupId;

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

	}

	/**
	 * CHECK IF THE VRE DEPLOYMENT IS FINISHED
	 * @param report
	 * @return
	 */
	private String getGlobalDeploymentStatus(String report) {
		String ret = "NOT FINISHED";

		return ret;
	}


	/**
	 * 
	 * @param profile
	 * @param xslFile
	 * @return
	 */
	private String transformToHtml(String profile, String xslFile){


		File stylesheet  =  new File(xslFile);

		TransformerFactory tFactory = TransformerFactory.newInstance();

		StreamSource stylesource = new StreamSource(stylesheet);

		Transformer transformer = null;
		try {
			transformer = tFactory.newTransformer(stylesource);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			return "";
		}


		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return "";
		}


		StringReader reader = new StringReader(profile);
		InputSource inputSource = new InputSource(reader);

		log.debug("***** --- Reading **** ");


		try {
			document = builder.parse(inputSource);
		} catch (SAXException e) {
			log.error("***** --- ERROR PARSING REPORT SAXException--- **** ");
			log.error("CHECK THIS: \n" + profile);

			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}


		DOMSource source = new DOMSource(document);

		ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(resultStream);

		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
			return "";
		}

		return resultStream.toString();
	}


	/**
	 * simulate a report
	 * @return
	 */
	private DeployReport simulateReport() {
		DeployReport reportToReturn = new DeployReport();
		return reportToReturn;
	}

	/**
	 * 
	 * @param path
	 * @return
	 */
	private String fileToString(String path) {
		BufferedReader filebuf = null;
		String nextStr = null;
		StringBuilder ret = new StringBuilder();
		try {
			filebuf = new BufferedReader(new FileReader(path));
			nextStr = filebuf.readLine(); // Read a line from file
			while (nextStr != null) {
				ret.append(nextStr);
				nextStr = filebuf.readLine(); // Read the next line
			}
			filebuf.close(); // close the file 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return ret.toString();
	}



}

