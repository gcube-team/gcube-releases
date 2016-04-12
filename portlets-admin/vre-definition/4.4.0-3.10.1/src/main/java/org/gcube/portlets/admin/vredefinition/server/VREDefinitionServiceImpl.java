package org.gcube.portlets.admin.vredefinition.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.accesslogger.library.impl.AccessLogger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.vremanagement.vremanagement.impl.VREGeneratorEvo;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.vredefinition.client.VREDefinitionService;
import org.gcube.portlets.admin.vredefinition.client.model.VREFunctionalityModel;
import org.gcube.portlets.admin.vredefinition.server.loggers.CreatedVRELogEntry;
import org.gcube.portlets.admin.vredefinition.server.loggers.OpenVREWizardLogEntry;
import org.gcube.portlets.admin.vredefinition.shared.ExternalResourceModel;
import org.gcube.portlets.admin.vredefinition.shared.ResourceCategory;
import org.gcube.portlets.admin.vredefinition.shared.ResourceCategoryItem;
import org.gcube.portlets.admin.vredefinition.shared.VRECollectionBean;
import org.gcube.portlets.admin.vredefinition.shared.VREDescriptionBean;
import org.gcube.portlets.admin.vredefinition.shared.exception.VREDefinitionException;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.UserModel;
import org.gcube.vremanagement.vremodel.cl.stubs.types.FunctionalityItem;
import org.gcube.vremanagement.vremodel.cl.stubs.types.ResourceDescriptionItem;
import org.gcube.vremanagement.vremodel.cl.stubs.types.ResourceItem;
import org.gcube.vremanagement.vremodel.cl.stubs.types.SelectedResourceDescriptionType;
import org.gcube.vremanagement.vremodel.cl.stubs.types.VREDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class VREDefinitionServiceImpl extends RemoteServiceServlet implements VREDefinitionService {
	private static final long serialVersionUID = -7565518362225100485L;

	private static final Logger _log = LoggerFactory.getLogger(VREDefinitionServiceImpl.class);

	private static final String DESIGNER = "Designer";
	private static final String MANAGER = "Manager";
	private static final String DESCRIPTION = "DESCRIPTION";


	private static String VRE_MANAGER_STRING = "VRE-Manager";


	private static final String VRE_GENERATOR_ATTRIBUTE = "VREGenerator";
	private static final String VRE_DEFINER_GENERATOR_ATTRIBUTE = "VREDefinerGenerator";
	private static final String USERNAME_ATTRIBUTE = "username";

	private static final String HARD_CODED_VO_NAME = "/gcube/devsec";
	private static final String REEDIT_TYPE_ATTRIBUTE = "reeditType";
	private static final String APPROVE_MODE = "approve";
	public static final String EDIT_MODE = "edit";

	private static final String CATEGORIES = "CATEGORIES";

	private boolean withinEclipse = false;
	/**
	 * 
	 * @return
	 */
	private ASLSession getASLSession() {
		HttpSession session = this.getThreadLocalRequest().getSession();
		String username = (String) session.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);

		if (username == null) {
			username = "massimiliano.assante";
			SessionManager.getInstance().getASLSession(session.getId(), username).setScope(HARD_CODED_VO_NAME);
			SessionManager.getInstance().getASLSession(session.getId(), username).setAttribute(REEDIT_TYPE_ATTRIBUTE, APPROVE_MODE);
			withinEclipse = true;
		}

		return SessionManager.getInstance().getASLSession(session.getId(), username);
	}

	private VREGeneratorEvo getVREGenerator(){
		ASLSession d4ScienceSession = getASLSession();
		VREGeneratorEvo vreGenerator = (VREGeneratorEvo) d4ScienceSession.getAttribute(VRE_DEFINER_GENERATOR_ATTRIBUTE);
		if(vreGenerator==null){
			vreGenerator = new VREGeneratorEvo(d4ScienceSession);
			d4ScienceSession.setAttribute(VRE_DEFINER_GENERATOR_ATTRIBUTE, vreGenerator);
		}

		AccessLogger log = AccessLogger.getAccessLogger();
		OpenVREWizardLogEntry logEntry = new OpenVREWizardLogEntry();
		log.logEntry(getASLSession().getUsername(), getASLSession().getScopeName(), logEntry);

		return vreGenerator;

	}



	private void storeResourceCategoryInSession(HashMap<String, ArrayList<ResourceCategory>> funcCategories) {
		getASLSession().setAttribute(CATEGORIES, funcCategories);
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, ArrayList<ResourceCategory>> getResourceCategoryFromSession() {
		return (HashMap<String, ArrayList<ResourceCategory>>) getASLSession().getAttribute(CATEGORIES);
	}
	/**
	 * return the categories per functionality
	 * @param func 
	 */
	public ArrayList<ExternalResourceModel> getResourceCategoryByFunctionality(String id) {
		ArrayList<ExternalResourceModel> toReturn = new ArrayList<ExternalResourceModel>();
		return (getResourceCategoryFromSession().containsKey(id) ? getExternaleResources(id) : toReturn);
	}
	/**
	 * 
	 * @param id
	 * @return
	 */
	private ArrayList<ExternalResourceModel> getExternaleResources(String id) {
		ArrayList<ExternalResourceModel> toReturn = new ArrayList<ExternalResourceModel>();		
		ArrayList<ResourceCategory> cats = getResourceCategoryFromSession().get(id);
		for (ResourceCategory rc : cats) 
			for(ResourceCategoryItem item : rc.getItems()) {
				toReturn.add(new ExternalResourceModel(item.getId(), item.getName(), item.getDescription(), item.isSelected(), rc.getName(), rc.getId()));				
			}
		return toReturn;
	}



	public Map<String,Object> isEditMode() {
		System.out.println("\n\n\n*********************  isEditMode()");
		String reeditType = (String) getASLSession().getAttribute(REEDIT_TYPE_ATTRIBUTE);
		ASLSession session = getASLSession();
		session.setAttribute(VRE_DEFINER_GENERATOR_ATTRIBUTE, null);

		if(reeditType != null && reeditType.equals(EDIT_MODE)) {

			String vreid = (String) session.getAttribute(VRE_GENERATOR_ATTRIBUTE);
			if(vreid==null){
				session.setAttribute(VRE_DEFINER_GENERATOR_ATTRIBUTE, null);
			} else {
				VREGeneratorEvo vreGenerator = 	new VREGeneratorEvo(session, vreid);
				session.setAttribute(VRE_DEFINER_GENERATOR_ATTRIBUTE, vreGenerator);

			}
			session.setAttribute(REEDIT_TYPE_ATTRIBUTE, null);

			try {
				List<VRECollectionBean> collections = getCollections();
				VREFunctionalityModel functionalities = getFunctionality(true);
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("collections", collections);
				map.put("functionalities", functionalities);
				return map;
			} catch (VREDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return null;
	}
	/**
	 * read the available functionality from the service through ASL extension
	 */

	public VREFunctionalityModel getFunctionality(boolean isEdit) throws VREDefinitionException {
		//if i am creating a new Vre I need to 'pretend' I already set the description to the service otherwise VREModeler won't return me the collections
		//if is editing instead this is not necessary 
		if (! isEdit) { 
			Calendar cal= Calendar.getInstance();
			long startTime = cal.getTimeInMillis();
			cal.add(Calendar.MONTH, 5);
			cal.add(Calendar.HOUR, 7);
			long endTime = cal.getTimeInMillis();
			try {
				//this is because Lucio is stupid
				getVREGenerator().setVREModel("notCompletedVirtualResearchEnv.", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), startTime, endTime);
				Thread.sleep(1000);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if (! getVREGenerator().isVreModelerServiceUp())
			return null;
		VREFunctionalityModel toReturn = new VREFunctionalityModel("","selected functionality", "", "",false);
		try{
			HashMap<String, ArrayList<ResourceCategory>> funCategories = new HashMap<String, ArrayList<ResourceCategory>>();

			List<FunctionalityItem> list = getVREGenerator().getFunctionality();

			if (list == null) {
				_log.warn("FunctionalityList NULL");
			}
			for (FunctionalityItem fi :list) {
				System.out.println("id:"+fi.id()+" "+fi.name());
				VREFunctionalityModel toAdd = new VREFunctionalityModel(Integer.toString(fi.id()),
						fi.name(), fi.description(), "",fi.selected());

				if (fi.children() != null && !fi.children().isEmpty()) {
					List<FunctionalityItem> children = fi.children();
					VREFunctionalityModel[] newchildrens = new VREFunctionalityModel[children.size() ];
					//creating node children
					for (int i = 0; i < children.size(); i++) {
						System.out.println(children.get(i).name() + " is " + children.get(i).selected());
						newchildrens[i] = new VREFunctionalityModel(Integer.toString(children.get(i).id()),
								children.get(i).name(), children.get(i).description(),
								"functionality-add-icon",children.get(i).selected());

						ArrayList<ResourceCategory> resCats = new ArrayList<ResourceCategory>(); 
						if ( children.get(i).selectableResourcesDescription()!=null) {
							for (ResourceDescriptionItem category:  children.get(i).selectableResourcesDescription()) {
								if (category.resources()!=null) {
									ResourceCategory rc = new ResourceCategory(category.id(), category.description());
									for (ResourceItem resource : category.resources()) {
										rc.addResourceItem(new ResourceCategoryItem(resource.id(), resource.name(), resource.description(), resource.selected()));
									}
									resCats.add(rc);
								}

							}
							funCategories.put(newchildrens[i].getId() , resCats);
						}
					}
					toAdd.addChildren(newchildrens);
					toReturn.add(toAdd);
				} else {
					System.out.println("id:"+fi.id()+" "+fi.name() + " has no children");
				}

			}
			storeResourceCategoryInSession(funCategories); 
			for (String func : funCategories.keySet()) {
				System.out.println("-"+func);
				for(ResourceCategory category : funCategories.get(func)) {
					System.out.println("--"+category.getName());
					for(ResourceCategoryItem rc: category.getItems()) {
						System.out.println("----"+rc.getName());
					}
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
			throw new VREDefinitionException("Set functionalities Error");
		}
		return toReturn;
	}


	public Map<String, Object> getVRE() throws VREDefinitionException {

		HashMap<String, Object> toReturn = new HashMap<String,Object>();

		HttpSession session = this.getThreadLocalRequest().getSession();
		String username = (String) session.getAttribute(USERNAME_ATTRIBUTE);

		List<UserModel> belongingUsers = new LinkedList<UserModel>();

		if (withinEclipse) {
			return getFakeBelongingUsers();
		}
		else {
			UserManager um = null;
			try {
				um = new LiferayUserManager();
				long currOrgid  = getASLSession().getGroupId();
				RoleManager rm = new LiferayRoleManager();

				System.out.println("Trying to get roleid of " + VRE_MANAGER_STRING + " into " + getASLSession().getGroupName());

				String roleId = "none";

				roleId = rm.getRoleId(VRE_MANAGER_STRING, getASLSession().getGroupName());
				System.out.println("Trying to get VRE-Managers of " + currOrgid + " role id=" + roleId);


				belongingUsers = um.listUsersByGroupAndRole(""+currOrgid, roleId);

			} catch (Exception e1) {

				e1.printStackTrace();
				throw new VREDefinitionException("We cannot find any VO-Admin user for this environment." +
						" There must be at least one.");
			}


			if (belongingUsers == null || belongingUsers.size() == 0)
				throw new VREDefinitionException("We cannot find any VO-Admin user for this environment." +
						" There must be at least one.");

			try {
				ArrayList<String> managers  = new ArrayList<String>();
				for (int i = 0; i < belongingUsers.size(); i++) {
					managers.add(belongingUsers.get(i).getFullname()+" ("+belongingUsers.get(i).getScreenName()+")");
				}
				toReturn.put("Manager", managers);

			} catch (Exception e) {

				e.printStackTrace();
				throw new VREDefinitionException("We cannot find any VO-Admin user for this environment." +
						" There must be at least one.");

			}	
			try {

				String fullName = um.getUserByScreenName(username).getFullname();
				toReturn.put("Designer", fullName+" ("+username+")");


				VREDescriptionBean vre = getVREInSession();
				if(vre!= null) {
					toReturn.put("vreName",vre.getName());
					toReturn.put("vreManager", vre.getManager());
					toReturn.put("vreDesigner", fullName+" ("+vre.getDesigner()+")");
					toReturn.put("vreDescription", vre.getDescription());
					toReturn.put("vreStartTime", vre.getStartTime());
					toReturn.put("vreEndTime",vre.getEndTime());
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		}

		return toReturn;
	}

	private HashMap<String, Object> getFakeBelongingUsers() {

		HashMap<String, Object> ret = new HashMap<String,Object>();

		ArrayList<String> managers = new ArrayList<String>();

		managers.add("Pasquale Pagano (pasquale.pagano)");
		managers.add("Andrea Manzi (andrea.manzi)");
		managers.add("Massimiliano Assante (massimiliano.assante)");
		ret.put("Manager",managers);

		ret.put("Designer", "Leanoardo Candela (leonardo.candela)");
		try {
			VREDescriptionBean vre = getVREInSession();
			if(vre!= null) {
				System.out.println(" ########### VRE != NULL ##########");
				ret.put("vreName",vre.getName());
				ret.put("vreManager", vre.getManager());
				ret.put("vreDesigner", vre.getDesigner());
				ret.put("vreDescription", vre.getDescription());
				ret.put("vreStartTime", vre.getStartTime());
				ret.put("vreEndTime",vre.getEndTime());
			}
		} catch (Exception e1){

		}
		return ret;
	}

	private String extractUserName(String toExtract) {
		int openBracket = toExtract.indexOf("(")+1;
		int closeBracket = toExtract.indexOf(")");
		return toExtract.substring(openBracket, closeBracket);
	}
	

	public String setVRE(VREDescriptionBean bean, String[] functionalityIDs, HashMap<String, List<ExternalResourceModel>> funcToExternalResources)
			throws VREDefinitionException {

		String completeDesigner = bean.getDesigner();
		bean.setDesigner(extractUserName(bean.getDesigner()));
		String managerUserName = extractUserName(bean.getManager());
		bean.setManager(managerUserName);

		setVREDescription(bean);
		setFunctionality(functionalityIDs, funcToExternalResources);
		try {
			getVREGenerator().setVREtoPendingState();

			getASLSession().setAttribute(VRE_DEFINER_GENERATOR_ATTRIBUTE, null);
			Workspace workspace = HomeLibrary.getUserWorkspace(getASLSession().getUsername());
			ArrayList<String> toSend = new ArrayList<String>();
			toSend.add(managerUserName);
			String subject = "New VRE Definition requires your approval";
			String body = "Dear Manager, \n\n" + completeDesigner + " has created a VRE Definition indicating you as VRE Manager on " + getASLSession().getScope();
			body+=".\n\nThe VRE Name is: " + bean.getName() +", the VRE Description is: " + bean.getDescription()+".";
			String messagedId = workspace.getWorkspaceMessageManager().sendMessageToPortalLogins(subject, body, new ArrayList<String>(), toSend);
			NotificationsManager nnm = new ApplicationNotificationsManager(getASLSession());
			if (nnm.notifyMessageReceived(managerUserName, messagedId, subject, body))
				_log.trace("Sending VRE Definition create notification: " + subject + " OK");

		} catch (Exception e) {
			e.printStackTrace();
		}
		AccessLogger log = AccessLogger.getAccessLogger();
		CreatedVRELogEntry logEntry = new CreatedVRELogEntry(bean.getName(), "unknown", bean.getDesigner(), bean.getManager());
		log.logEntry(getASLSession().getUsername(), getASLSession().getScopeName(), logEntry);
		return null;
	}

	/**
	 * 
	 * @param functionalityIDs
	 * @param funcToExternalResources
	 * @return
	 * @throws VREDefinitionException
	 */
	public String setFunctionality(String[] functionalityIDs, HashMap<String, List<ExternalResourceModel>> funcToExternalResources) throws VREDefinitionException {
		//fillling up 
		ArrayList<SelectedResourceDescriptionType> toSend = new ArrayList<SelectedResourceDescriptionType>(); 

		//for each func 
		for (int i = 0; i < functionalityIDs.length; i++) {
			if (funcToExternalResources.containsKey(functionalityIDs[i])) { //get the associated resources
				String descriptionId = "";
				ArrayList<String> resourceIds = new ArrayList<String>();		
				for (ExternalResourceModel extRes : funcToExternalResources.get(functionalityIDs[i])) {
					_log.debug("resource: " + extRes.getName() + " Selected?" + extRes.isSelected());
					if (extRes.isSelected()) {
						descriptionId = extRes.getCategoryId();
						resourceIds.add(extRes.getId());
						_log.debug("------> Added selected resource: " + extRes.getName() + " id=" + extRes.getId() + "\n");		
					}
				}
				SelectedResourceDescriptionType toAdd = new SelectedResourceDescriptionType();
				toAdd.descriptionId(descriptionId);
				toAdd.resourceIds(resourceIds);
				toSend.add(toAdd);				
			}
		}
		Integer funcIdAsInts[] = new Integer[functionalityIDs.length];
		for (int i = 0; i < functionalityIDs.length; i++) {
			try {
				funcIdAsInts[i] = Integer.parseInt(functionalityIDs[i]);
			}
			catch (ClassCastException e) {
				_log.error("Could not convert to int: " + functionalityIDs[i]);
			}
		}
		SelectedResourceDescriptionType[] selres = toSend.toArray(new SelectedResourceDescriptionType[toSend.size()]);
		System.out.println("SelectedResourceDescriptionType[] Sending to Service .... toSend size = " + toSend.size());
		for (int i = 0; i < selres.length; i++) {
			System.out.println("SelectedResourceDescriptionType DESC: " +  selres[i].getDescriptionId());
			for (int j = 0; j < selres[i].resourceIds().size(); j++) {
				System.out.println("resid= " +selres[i].resourceIds().get(j));
			}
		}

		VREGeneratorEvo vreGenerator = getVREGenerator();
		try {
			vreGenerator.setFunctionality(funcIdAsInts, selres);
			vreGenerator.setVREtoPendingState();
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new VREDefinitionException("Set functionalities Error");
		}

		return null;
	}




	private String setVREDescription(VREDescriptionBean bean) throws VREDefinitionException {
		VREGeneratorEvo vreGenerator = getVREGenerator();		
		ASLSession d4ScienceSession = getASLSession();
		d4ScienceSession.setAttribute("VREName", bean.getName());
		d4ScienceSession.setAttribute(DESCRIPTION, bean.getDescription());
		d4ScienceSession.setAttribute(DESIGNER, bean.getDesigner());
		d4ScienceSession.setAttribute(MANAGER, bean.getManager());
		try {
			vreGenerator.setVREModel(bean.getName(), bean.getDescription(), bean.getDesigner(), 
					bean.getManager(), bean.getStartTime().getTime() , bean.getEndTime().getTime());
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new VREDefinitionException("Set Description Error");
		}

		return null;
	}



	public List<VRECollectionBean> getCollections() throws VREDefinitionException {
		List<VRECollectionBean> listToReturn = new ArrayList<VRECollectionBean>();
		return listToReturn;
	}

	private VREDescriptionBean getVREInSession() throws VREDefinitionException {
		VREGeneratorEvo vreGenerator =  getVREGenerator();

		VREDescriptionBean vreDescBean = null;
		try {
			VREDescription sd = vreGenerator.getVREModel();
			vreDescBean = new VREDescriptionBean(sd.name(), sd.description(), 
					sd.designer(), sd.manager(), sd.startTime().getTime(), sd.endTime().getTime());

		} catch (RemoteException e) {
			throw new VREDefinitionException("Fail retrieve VRE");
		}	


		System.out.println("Model: " + vreDescBean.getName());
		System.out.println("--- END Getting VRE Model ---");

		return vreDescBean;

	}

	public String[] getExistingNames() {
		return null;
	}

}
