package org.gcube.portlets.admin.vredefinition.server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.vremanagement.vremanagement.impl.VREGeneratorEvo;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingSite;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingUser;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.vredefinition.client.VREDefinitionService;
import org.gcube.portlets.admin.vredefinition.shared.Functionality;
import org.gcube.portlets.admin.vredefinition.shared.Resource;
import org.gcube.portlets.admin.vredefinition.shared.ResourceCategory;
import org.gcube.portlets.admin.vredefinition.shared.VREDescriptionBean;
import org.gcube.portlets.admin.vredefinition.shared.exception.VREDefinitionException;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vremanagement.vremodel.cl.stubs.types.FunctionalityItem;
import org.gcube.vremanagement.vremodel.cl.stubs.types.ResourceDescriptionItem;
import org.gcube.vremanagement.vremodel.cl.stubs.types.ResourceItem;
import org.gcube.vremanagement.vremodel.cl.stubs.types.SelectedResourceDescriptionType;
import org.gcube.vremanagement.vremodel.cl.stubs.types.VREDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The VREDefinitionServiceImpl class.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class VREDefinitionServiceImpl  extends RemoteServiceServlet implements VREDefinitionService{

	private static final long serialVersionUID = -7581858549584810224L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(VREDefinitionServiceImpl.class);

	// some usefull constants
	private static final String DESIGNER = "Designer";
	private static final String MANAGER = "Manager";
	private static final String DESCRIPTION = "DESCRIPTION";
	private static final String VRE_MANAGER_STRING = "VRE-Manager";
	private static final String VRE_DEFINER_GENERATOR_ATTRIBUTE = "VREDefinerGenerator";
	private static final String VRE_GENERATOR_ATTRIBUTE = "VREGenerator";
	private static final String REEDIT_TYPE_ATTRIBUTE = "reeditType";
	private static final String EDIT_MODE = "edit";
	private static final String APPROVE_MODE = "approve";
	private static final String VRE_NAME_FIELD = "vreName";
	private static final String VRE_MANAGER_FIELD = "vreManager";
	private static final String VRE_DESIGNER_FIELD = "vreDesigner";
	private static final String VRE_DESCRIPTION_FIELD = "vreDescription";
	private static final String VRE_START_TIME_FIELD = "vreStartTime";
	private static final String VRE_END_TIME_FIELD = "vreEndTime";

	//dev user
	public static final String defaultUserId = "test.user";

	//dev vo
	private static final String voID = "/gcube/devsec";

	@Override
	public void init() {

		logger.debug("Servlet init");
	}

	@Override
	public void destroy(){

		logger.debug("Servlet destroyed");

	}

	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {

		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);

		if (user == null) {

			logger.warn("USER IS NULL setting " + defaultUserId + " and Running OUTSIDE PORTAL");
			user = getDevelopmentUser();
			SessionManager.getInstance().getASLSession(sessionID, user).setScope(voID);
			SessionManager.getInstance().getASLSession(sessionID, user).setAttribute(REEDIT_TYPE_ATTRIBUTE, APPROVE_MODE);

		}		

		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

	/**
	 * Online or in development mode?
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (com.liferay.portal.kernel.bean.BeanLocatorException ex) {			
			logger.trace("Development Mode ON");
			return false;
		}			
	}

	/**
	 * when packaging test will fail if the user is not set to test.user
	 * @return .
	 */
	public String getDevelopmentUser() {
		String user = defaultUserId;
		//		user = "costantino.perciante";
		return user;
	}

	/**
	 * When the portlet starts, this method is invoked in order to return
	 * 1) vre managers for the current VO
	 * 2) the current designer (that is the current user)
	 * 3) a VREDefinitionBean that will be filled somehow by the designer
	 */
	public Map<String, Serializable> getVRE() {

		logger.debug("########### INVOKED getVRE() METHOD ###########");

		Map<String, Serializable> toReturn = new HashMap<String, Serializable>();

		// retrieve the session
		ASLSession session = getASLSession();

		// username
		String username = session.getUsername();

		// check if development mode or portal
		if(!isWithinPortal()){

			logger.debug("You are in dev mode");
			return getFakeVreDefinition();

		}else{

			// check if we are in edit mode or not
			boolean editMode = isEditMode();

			logger.debug("EDIT MODE IS " + editMode);

			// put this information in the returned hashmap
			toReturn.put(EDIT_MODE, editMode);

			try{

				// user and role managers
				UserManager um = new LiferayUserManager();
				RoleManager rm = new LiferayRoleManager();

				// current org id of the vo
				long voOrgID  = getASLSession().getGroupId();
				long roleId = rm.getRoleId(VRE_MANAGER_STRING, getASLSession().getGroupId());

				logger.debug(VRE_MANAGER_STRING + " has role id " + roleId + " into organization " + voOrgID);

				logger.debug("Trying to get roleid of " + VRE_MANAGER_STRING + " into " + getASLSession().getGroupName());

				List<GCubeUser> belongingUsers = um.listUsersByGroupAndRole(voOrgID, roleId);

				logger.debug("Number of managers is " + belongingUsers.size());

				ArrayList<String> managers  = new ArrayList<String>();
				for (int i = 0; i < belongingUsers.size(); i++) {

					managers.add(belongingUsers.get(i).getFullname()+" ("+belongingUsers.get(i).getUsername()+")");

				}
				toReturn.put(MANAGER, managers);
				logger.debug("Managers set as " + managers);

				// set the designer
				String fullName = um.getUserByUsername(username).getFullname();
				toReturn.put(DESIGNER, fullName+" ("+username+")");
				logger.debug("Designer set as " + fullName+" ("+username+")");

				// in edit mode we need to retrieve the selected manager as well as other vre info
				if(editMode){

					try{

						VREDescriptionBean vreBean = getVREInSession();
						if(vreBean != null) {
							toReturn.put(VRE_NAME_FIELD,vreBean.getName());
							toReturn.put(VRE_MANAGER_FIELD, vreBean.getManager());
							toReturn.put(VRE_DESIGNER_FIELD, fullName+" ("+vreBean.getDesigner()+")"); // should be equal to DESIGNER
							toReturn.put(VRE_DESCRIPTION_FIELD, vreBean.getDescription());
							toReturn.put(VRE_START_TIME_FIELD, vreBean.getStartTime());
							toReturn.put(VRE_END_TIME_FIELD, vreBean.getEndTime());
						}

					}catch(Exception e){
						logger.error("Error while retrieving vre description bean", e);
					}

				}

			}catch(Exception e){
				logger.error("Error while retrieving vre information", e);
			}

		}

		return toReturn;
	}

	/**
	 * Generate random data
	 * @return
	 */
	private Map<String, Serializable> getFakeVreDefinition() {

		Map<String, Serializable> toReturn = new HashMap<String,Serializable>();
		ArrayList<String> managers = new ArrayList<String>();
		managers.add("Pasquale Pagano (pasquale.pagano)");
		managers.add("Andrea Manzi (andrea.manzi)");
		managers.add("Massimiliano Assante (massimiliano.assante)");
		toReturn.put(MANAGER, managers);

		// designer
		toReturn.put(DESIGNER, "Leonardo Candela (leonardo.candela)");

		// check if we are in edit mode or not
		boolean editMode = isEditMode();

		// put this information in the returned hashmap
		toReturn.put(EDIT_MODE, editMode);

		if(editMode){
			try {
				VREDescriptionBean vreBean = getVREInSession();
				if(vreBean != null) {
					logger.debug(" ########### VRE != NULL ##########");
					logger.debug("Bean is " + vreBean);
					toReturn.put(VRE_NAME_FIELD,vreBean.getName());
					toReturn.put(VRE_MANAGER_FIELD, vreBean.getManager());
					toReturn.put(VRE_DESIGNER_FIELD, vreBean.getDesigner());
					toReturn.put(VRE_DESCRIPTION_FIELD, vreBean.getDescription());
					toReturn.put(VRE_START_TIME_FIELD, vreBean.getStartTime().getTime());
					toReturn.put(VRE_END_TIME_FIELD, vreBean.getEndTime().getTime());
				}
			} catch (Exception e1){
				logger.error("Missing definition bean...");
			}
		}
		return toReturn;
	}

	/**
	 * Retrieve the VREDescriptionBean from the session if possible
	 * @return
	 * @throws VREDefinitionException
	 */
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

		logger.debug("Model retrieved is " + vreDescBean);
		return vreDescBean;
	}

	/**
	 * The VREGenerator is needed to retrieve VRE Bean information
	 * @return
	 */
	private VREGeneratorEvo getVREGenerator(){
		ASLSession d4ScienceSession = getASLSession();
		VREGeneratorEvo vreGenerator = (VREGeneratorEvo) d4ScienceSession.getAttribute(VRE_DEFINER_GENERATOR_ATTRIBUTE);

		// if there is not..add it
		if(vreGenerator == null){
			logger.debug("There is no VREGeneratorEvo in the session, adding it..");
			vreGenerator = new VREGeneratorEvo(d4ScienceSession);
			d4ScienceSession.setAttribute(VRE_DEFINER_GENERATOR_ATTRIBUTE, vreGenerator);
		}
		return vreGenerator;
	}

	/**
	 * Check if we are in edit mode or not
	 * @return
	 */
	private boolean isEditMode(){

		logger.debug("Checking if we are in edit mode...");

		// retrieve current session
		ASLSession session = getASLSession();

		// get this attribute
		String reeditType = (String)session.getAttribute(REEDIT_TYPE_ATTRIBUTE);

		// set to null  the VRE_DEFINER_GENERATOR_ATTRIBUTE
		session.setAttribute(VRE_DEFINER_GENERATOR_ATTRIBUTE, null);

		// if the reeditType is actually edit
		if(reeditType != null && reeditType.equals(EDIT_MODE)) {

			// retrieve the vreid (of the vre to be changed)
			String vreid = (String) session.getAttribute(VRE_GENERATOR_ATTRIBUTE);

			if(vreid != null){

				// put the generator in session
				VREGeneratorEvo vreGenerator = 	new VREGeneratorEvo(session, vreid);
				session.setAttribute(VRE_DEFINER_GENERATOR_ATTRIBUTE, vreGenerator);

			}

			// set to null the attribute REEDIT_TYPE_ATTRIBUTE
			session.setAttribute(REEDIT_TYPE_ATTRIBUTE, null);

			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public ArrayList<Functionality> getFunctionality(boolean isEdit)
			throws VREDefinitionException {
		/*
		 * if i am creating a new Vre I need to 'pretend' I already set the description to the service otherwise VREModeler won't return me the collections
		 * if is editing instead this is not necessary 
		 */
		if (!isEdit) { 
			Calendar cal= Calendar.getInstance();
			long startTime = cal.getTimeInMillis();
			cal.add(Calendar.MONTH, 5);
			cal.add(Calendar.HOUR, 7);
			long endTime = cal.getTimeInMillis();
			try {
				//a useless setVREModel() is needed to retrieve functionalities....
				getVREGenerator().setVREModel("notCompletedVirtualResearchEnv.", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), startTime, endTime);
				Thread.sleep(1000); // wait a sec
			} catch (Exception e) {
				logger.error("Error on setVREModel()", e);
			}
		}

		// if the service is not ready, return null
		if (!getVREGenerator().isVreModelerServiceUp())
			return null;

		// build up the list of functionalities
		List<Functionality> toReturn = new ArrayList<Functionality>();

		try{

			// get the list of main functionalities
			List<FunctionalityItem> listMainFunctionalities = getVREGenerator().getFunctionality();

			if(listMainFunctionalities != null && !listMainFunctionalities.isEmpty()){

				// for each functionality
				for (FunctionalityItem functionalityItem : listMainFunctionalities) {

					logger.debug("Reading FunctionalityItem with id:" + functionalityItem.id() + " and name " + functionalityItem.name());

					// add it to the returning list
					Functionality mainFunctionality = new Functionality(functionalityItem.id(), functionalityItem.name(), functionalityItem.description(), functionalityItem.selected());
					toReturn.add(mainFunctionality);

					// scan its children (if any)
					if(functionalityItem.children() == null || functionalityItem.children().isEmpty()){
						logger.debug(functionalityItem.name() + " has no children.");
					}
					else{
						// get its children
						List<FunctionalityItem> childrenFunctionalities = functionalityItem.children();

						// subfunctionalities of our model
						List<Functionality> subFunctionalities = new ArrayList<Functionality>();

						// for each subfunctionality
						for(FunctionalityItem childFunctionality: childrenFunctionalities){

							logger.debug("Reading child FunctionalityItem " + childFunctionality.name() + " of node " + functionalityItem.name());
							Functionality subFunctionality = new Functionality(childFunctionality.id(), childFunctionality.name(), childFunctionality.description(), childFunctionality.selected());

							// this subfunctionality could have resource categories
							List<ResourceDescriptionItem> resourcesDescriptionItems = childFunctionality.selectableResourcesDescription();

							if(resourcesDescriptionItems != null && !resourcesDescriptionItems.isEmpty()){

								// categories of our model
								List<ResourceCategory> categories = new ArrayList<ResourceCategory>();

								for (ResourceDescriptionItem resourceDescriptionItem : resourcesDescriptionItems) {
									logger.debug("Reading ResourceDescriptionItem " + resourceDescriptionItem.description() + " of functionality " + childFunctionality.name());

									// resource category list
									List<ResourceItem> resourceItems = resourceDescriptionItem.resources();

									if(resourceItems != null && !resourceItems.isEmpty()){

										ResourceCategory rc = new ResourceCategory(resourceDescriptionItem.id(), resourceDescriptionItem.description());
										List<Resource> resourceModels = new ArrayList<Resource>();
										for (ResourceItem resource : resourceDescriptionItem.resources()) {
											logger.debug("Reading resource " + resource.name() + " of ResourceDescriptionItem " + resourceDescriptionItem.description());
											resourceModels.add(new Resource(resource.id(), resource.name(), resource.description(), resource.selected()));

										}

										// set resources for this category
										rc.setItems((ArrayList<Resource>) resourceModels);

										// append the resource category to categories
										categories.add(rc);
									}	
								}

								// append this resources to the child functionality
								subFunctionality.setResources(categories);

							}

							// add this subfunctionality to our functionality list
							subFunctionalities.add(subFunctionality);

						}

						// add the subfunctionalities to the main functionality
						mainFunctionality.setSubFunctionalities(subFunctionalities);

					}

				}
			}

		}catch(Exception e){
			logger.error("Error while retrieving the list of functionalities", e);
			return null;
		}

		// return the list of functionalities
		return (ArrayList<Functionality>)toReturn;
	}

	/**
	 * Set the vre functionalities and resources it will use.
	 * @param functionalities
	 * @return true on success, false otherwise
	 */
	private boolean setFunctionalities(List<Functionality> functionalities){

		//filling up 
		ArrayList<SelectedResourceDescriptionType> toSend = new ArrayList<SelectedResourceDescriptionType>(); 

		// we need a list of checked subfunctionalities' ids 
		ArrayList<Integer> subfunctionalitiesIDS = new ArrayList<Integer>(); 

		// iterate over the macro functionalities
		for(Functionality macroFunctionality : functionalities){

			logger.debug("Scanning macro functionality " + macroFunctionality.getName());

			// retrieve its direct children
			List<Functionality> subfunctionalities = macroFunctionality.getSubFunctionalities();

			// iterate
			for (Functionality subfunctionality : subfunctionalities) {

				logger.debug("Scanning sub functionality " + subfunctionality.getName());

				if(subfunctionality.isSelected()){

					logger.debug("This subfunctionality was selected");

					// save its id
					subfunctionalitiesIDS.add(subfunctionality.getId());

					// retrieve its children list
					List<ResourceCategory> categories = subfunctionality.getResources();

					if(categories != null){

						for(ResourceCategory category : categories){

							// get the description id
							String descriptionId = category.getId();

							// get its resources
							ArrayList<Resource> resources = category.getItems();

							// list of ids for resources
							ArrayList<String> resourceIds = new ArrayList<String>();

							if(resources != null){

								logger.debug("Scanning resources of " + subfunctionality.getName());

								for (Resource resource : resources) {

									if(resource.isSelected()){

										logger.debug("Resource of " + resource.getName() + " was selected by the user");

										// add its id
										resourceIds.add(resource.getId());
									}

								}	
							}

							SelectedResourceDescriptionType toAdd = new SelectedResourceDescriptionType();
							toAdd.descriptionId(descriptionId);
							toAdd.resourceIds(resourceIds);
							toSend.add(toAdd);
						}

					}

				}
			}
		}

		// convert to array
		Integer funcIdAsInts[] = subfunctionalitiesIDS.toArray(new Integer[subfunctionalitiesIDS.size()]);

		// convert to array
		SelectedResourceDescriptionType[] selres = toSend.toArray(new SelectedResourceDescriptionType[toSend.size()]);
		logger.debug("SelectedResourceDescriptionType[] Sending to Service .... toSend size = " + toSend.size());
		for (int i = 0; i < selres.length; i++) {
			logger.debug("SelectedResourceDescriptionType DESC: " +  selres[i].getDescriptionId());
			for (int j = 0; j < selres[i].resourceIds().size(); j++) {
				logger.debug("resid= " +selres[i].resourceIds().get(j));
			}
		}

		// save the above information
		VREGeneratorEvo vreGenerator = getVREGenerator();
		try {
			vreGenerator.setFunctionality(funcIdAsInts, selres);
			vreGenerator.setVREtoPendingState();
			return true;
		} catch (RemoteException e) {
			logger.error("An error arises", e);
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean setVRE(VREDescriptionBean bean,
			ArrayList<Functionality> functionalities)
					throws VREDefinitionException {

		// retrieve information from the description bean
		String completeDesigner = bean.getDesigner();
		bean.setDesigner(extractUserName(completeDesigner));
		String managerUserName = extractUserName(bean.getManager());
		bean.setManager(managerUserName);

		// set this description bean
		setVREDescription(bean);

		// save this functionalities
		setFunctionalities(functionalities);
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

			ASLSession session = getASLSession();
			SocialNetworkingUser user = new SocialNetworkingUser(session.getUsername(), session.getUserEmailAddress(), session.getUserFullName(), session.getUserAvatarId());
			NotificationsManager nnm = new ApplicationNotificationsManager(new SocialNetworkingSite(getThreadLocalRequest()), session.getScope(), user);

			if (nnm.notifyMessageReceived(managerUserName, messagedId, subject, body))
				logger.trace("Sending VRE Definition create notification: " + subject + " OK");


			return true;
		} catch (Exception e) {
			logger.error("Error while creating the new VRE ", e);
		}
		return false;
	}

	/**
	 * Set the bean within the session
	 * @param bean
	 * @return
	 * @throws VREDefinitionException
	 */
	private String setVREDescription(VREDescriptionBean bean) throws VREDefinitionException {
		VREGeneratorEvo vreGenerator = getVREGenerator();		
		ASLSession d4ScienceSession = getASLSession();
		d4ScienceSession.setAttribute(VRE_NAME_FIELD, bean.getName());
		d4ScienceSession.setAttribute(DESCRIPTION, bean.getDescription());
		d4ScienceSession.setAttribute(DESIGNER, bean.getDesigner());
		d4ScienceSession.setAttribute(MANAGER, bean.getManager());
		try {
			vreGenerator.setVREModel(bean.getName(), bean.getDescription(), bean.getDesigner(), 
					bean.getManager(), bean.getStartTime().getTime() , bean.getEndTime().getTime());
		} catch (RemoteException e) {
			logger.error("An error arises", e);
			throw new VREDefinitionException("Set Description Error");
		}

		return null;
	}

	/**
	 * The original format looks like Clark Kent (clark.kent) and we need the name within brackets
	 * @param toExtract
	 * @return
	 */
	private String extractUserName(String toExtract) {
		int openBracket = toExtract.indexOf("(")+1;
		int closeBracket = toExtract.indexOf(")");
		String toReturn = toExtract.substring(openBracket, closeBracket);
		logger.debug("Extracted username is " + toReturn);
		return toReturn;
	}
}