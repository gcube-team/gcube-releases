package org.gcube.portlets.widgets.ckandatapublisherwidget.server.threads;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.mailing.EmailNotification;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.CatalogueUtilMethods;
import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.OrganizationBean;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import eu.trentorise.opendata.jackan.model.CkanGroup;

/**
 * Associate the dataset to a group and send notifications to group's admins.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class AssociationToGroupAndNotifyThread extends Thread {

	//private static final Logger logger = LoggerFactory.getLogger(AssociationToGroupAndNotifyThread.class);
	private static final Log logger = LogFactoryUtil.getLog(AssociationToGroupAndNotifyThread.class);
	private static final String PRODUCT_ASSOCIATED_TO_GROUP_SUBJECT = "Item $TITLE added to group $GROUP";
	private static final String PRODUCT_ASSOCIATED_TO_GROUP_BODY = "Dear user,<br> a new item named '<b>$TITLE</b>' has been "
			+ "just published by $USER_FULLNAME in <b>$GROUP</b> .<br>"
			+ "You can find it here $DATASET_URL";

	private String groupTitle;
	private String datasetId;
	private String username;
	private String datasetTitle;
	private String userFullName;
	private DataCatalogue catalogue;
	//	private String organization;
	private List<OrganizationBean> groups;
	private HttpServletRequest request;
	private String datasetUrl;
	private List<OrganizationBean> groupsForceCreation;

	/**
	 * @param list 
	 * @param groupTitle
	 * @param datasetId
	 * @param username
	 * @param catalogue
	 */
	public AssociationToGroupAndNotifyThread(List<OrganizationBean> groups, List<OrganizationBean> groupsForceCreation, String groupTitle, String datasetUrl, String datasetId, String datasetTitle, String userFullName,
			String username, DataCatalogue catalogue, String organization, HttpServletRequest request) {
		this.request = request;
		this.groups = groups == null ? new ArrayList<OrganizationBean>() : groups;
		this.groupsForceCreation = groupsForceCreation;
		this.groupTitle = groupTitle;
		this.datasetId = datasetId;
		this.username = username;
		this.catalogue = catalogue;
		//		this.organization = organization;
		this.datasetTitle = datasetTitle;
		this.userFullName = userFullName;
		this.datasetUrl = datasetUrl;
	}

	@Override
	public void run() {

		logger.info("Association thread started to put the dataset with id = "+ datasetId + " into group with title " + groupTitle + " for user " + username);

		// force creation of groups if needed
		if(groupsForceCreation != null){
			logger.info("Groups that must be created before association are  " + groupsForceCreation);
			for (OrganizationBean groupToForce : groupsForceCreation) {
				try{
				CkanGroup group = catalogue.createGroup(groupToForce.getName(), groupToForce.getTitle(), "");
				if(group == null)
					logger.error("Unable to retrieve or create group with name " + groupToForce);
				else
					groups.add(new OrganizationBean(group.getTitle(), group.getName(), false, groupToForce.isPropagateUp()));
				}catch(Exception e){
					logger.error("Failed to check if a group with this info " + groupToForce + " already exists or can be created");
				}
			}

		}

		logger.info("Other groups to which the product should be associate are " + groups);

		if(groups != null)
			for (OrganizationBean groupBean : groups) {
				boolean putIntoGroup = catalogue.assignDatasetToGroup(groupBean.getName(), datasetId, catalogue.getApiKeyFromUsername(username), groupBean.isPropagateUp());
				logger.info("Was product put into group" + groupBean.getTitle() + "? " + putIntoGroup);
				if(putIntoGroup)
					notifyGroupAdmins(catalogue, groupBean.getName() ,groupBean.getTitle(), username);
			}

	}

	/**
	 * Send a notification to the group admin(s) about the just added product
	 * @param username 
	 * @param groupTitle 
	 * @param catalogue 
	 */
	private void notifyGroupAdmins(DataCatalogue catalogue, String groupName, String groupTitle, String username){

		// get the groups admin
		Map<RolesCkanGroupOrOrg, List<String>> userAndRoles = catalogue.getRolesAndUsersGroup(groupName);

		if(userAndRoles.containsKey(RolesCkanGroupOrOrg.ADMIN)){

			List<String> admins = userAndRoles.get(RolesCkanGroupOrOrg.ADMIN);
			List<String> adminsEmails = new ArrayList<String>();

			for(int i = 0; i < admins.size(); i++){
				String convertedName = CatalogueUtilMethods.fromCKanUsernameToUsername(admins.get(i));
				admins.set(i, convertedName);
			}

			// remove the same user who published the product if he/she is an admin of the group
			int indexOfUser = admins.indexOf(username);
			if(indexOfUser >= 0)
				admins.remove(indexOfUser);

			// further cleaning of the list (for users that are only in ckan... sysadmin for example)
			UserManager um = new LiferayUserManager();
			Iterator<String> adminIt = admins.iterator();

			while (adminIt.hasNext()) {
				String admin = (String) adminIt.next();
				try{
					adminsEmails.add(um.getUserByUsername(admin).getEmail());
				}catch(Exception e){
					logger.error("User with username " + admin + " doesn't exist in Liferay");
					adminIt.remove();
				}
			}

			logger.info("The list of admins for group " + groupTitle + " is " + admins);

			if(admins.isEmpty())
				return;

			// send the email
			EmailNotification mailToSend = new EmailNotification(
					adminsEmails, 
					PRODUCT_ASSOCIATED_TO_GROUP_SUBJECT.replace("$TITLE", datasetTitle).replace("$GROUP", groupTitle), 
					PRODUCT_ASSOCIATED_TO_GROUP_BODY.replace("$TITLE", datasetTitle).replace("$GROUP", groupTitle).replace("$USER_FULLNAME", userFullName).replace("$DATASET_URL", datasetUrl), 
					request);
			mailToSend.sendEmail();

		}else
			logger.warn("It seems there is no user with role Admin in group " + groupTitle);
	}
}
