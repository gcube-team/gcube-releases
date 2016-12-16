package org.gcube.portlets.widgets.ckandatapublisherwidget.server.threads;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.mailing.EmailNotification;
import org.gcube.datacatalogue.ckanutillibrary.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.models.RolesCkanGroupOrOrg;
import org.gcube.datacatalogue.ckanutillibrary.utils.UtilMethods;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.GroupBean;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanGroup;

/**
 * Associate the dataset to a group and send notifications to group's admins.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class AssociationToGroupAndNotifyThread extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(AssociationToGroupAndNotifyThread.class);
	private static final String PRODUCT_ASSOCIATED_TO_GROUP_SUBJECT = "Product $TITLE added to group $GROUP";
	private static final String PRODUCT_ASSOCIATED_TO_GROUP_BODY = "Dear user,<br> a new product named '<b>$TITLE</b>' has been "
			+ "just published by $USER_FULLNAME in <b>$GROUP</b> .<br>"
			+ "You can find it here $DATASET_URL";

	private String groupTitle;
	private String datasetId;
	private String username;
	private String datasetTitle;
	private String userFullName;
	private DataCatalogue catalogue;
	private String organization;
	private List<GroupBean> groups;
	private HttpServletRequest request;
	private String datasetUrl;

	/**
	 * @param list 
	 * @param groupTitle
	 * @param datasetId
	 * @param username
	 * @param catalogue
	 */
	public AssociationToGroupAndNotifyThread(List<GroupBean> groups, String groupTitle, String datasetUrl, String datasetId, String datasetTitle, String userFullName,
			String username, DataCatalogue catalogue, String organization, HttpServletRequest request) {
		this.request = request;
		this.groups = groups;
		this.groupTitle = groupTitle;
		this.datasetId = datasetId;
		this.username = username;
		this.catalogue = catalogue;
		this.organization = organization;
		this.datasetTitle = datasetTitle;
		this.userFullName = userFullName;
		this.datasetUrl = datasetUrl;
	}

	@Override
	public void run() {

		logger.info("Association thread started to put the dataset with id = "+ datasetId + " into group with title " + groupTitle + " for user " + username);

		if(groupTitle != null){
			try{

				// create the group
				CkanGroup group = catalogue.createGroup(groupTitle, groupTitle, "");

				if(group == null){

					logger.warn("The group doesn't exist! Unable to perform such association");

				}else{

					logger.debug("Group exists, going to add the user " + username + " as its admin...");

					// retrieve the role to be assigned according the one the user has into the organization of the dataset
					RolesCkanGroupOrOrg role = RolesCkanGroupOrOrg.valueOf(catalogue.getRoleOfUserInOrganization(username, organization, catalogue.getApiKeyFromUsername(username)).toUpperCase());

					if(!role.equals(RolesCkanGroupOrOrg.ADMIN))
						role = RolesCkanGroupOrOrg.MEMBER; // decrease the role to member if it is not an admin

					boolean assigned = catalogue.checkRoleIntoGroup(username, groupTitle, role);

					if(assigned){

						logger.debug("Admin/editor role was assigned for this group, going to associate the product to the group");
						boolean putIntoGroup = catalogue.assignDatasetToGroup(groupTitle, datasetId, catalogue.getApiKeyFromUsername(username));
						logger.info("Was product put into group? " + putIntoGroup);

						if(putIntoGroup)
							notifyGroupAdmins(catalogue, groupTitle, username);

					}			
				}

			}catch(Exception e){
				logger.warn("Something went wrong when tried to add the group " + groupTitle, e);
			}
		}
		logger.info("Other groups to which the product should be associate are " + groups);

		if(groups != null)
			for (GroupBean groupBean : groups) {
				boolean putIntoGroup = catalogue.assignDatasetToGroup(groupBean.getGroupTitle(), datasetId, catalogue.getApiKeyFromUsername(username));
				logger.info("Was product put into group" + groupBean.getGroupTitle() + "? " + putIntoGroup);

				if(putIntoGroup)
					notifyGroupAdmins(catalogue, groupBean.getGroupTitle(), username);
			}

	}

	/**
	 * Send a notification to the group admin(s) about the just added product
	 * @param username 
	 * @param groupTitle 
	 * @param catalogue 
	 */
	private void notifyGroupAdmins(DataCatalogue catalogue, String groupTitle, String username){

		// get the groups admin
		Map<RolesCkanGroupOrOrg, List<String>> userAndRoles = catalogue.getRolesAndUsersGroup(groupTitle);

		if(userAndRoles.containsKey(RolesCkanGroupOrOrg.ADMIN)){

			List<String> admins = userAndRoles.get(RolesCkanGroupOrOrg.ADMIN);
			List<String> adminsEmails = new ArrayList<String>();

			for(int i = 0; i < admins.size(); i++){
				String convertedName = UtilMethods.fromCKanUsernameToUsername(admins.get(i));
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
