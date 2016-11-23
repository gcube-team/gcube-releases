package org.gcube.portlets.widgets.ckandatapublisherwidget.server;

import org.gcube.datacatalogue.ckanutillibrary.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.models.RolesCkanGroupOrOrg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanGroup;

/**
 * Associate the dataset to a group.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class AssociationToGroupThread extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(AssociationToGroupThread.class);

	private String groupTitle;
	private String datasetId;
	private String username;
	private DataCatalogue catalogue;
	private String organization;

	/**
	 * @param groupTitle
	 * @param datasetId
	 * @param username
	 * @param catalogue
	 */
	public AssociationToGroupThread(String groupTitle, String datasetId,
			String username, DataCatalogue catalogue, String organization) {
		this.groupTitle = groupTitle;
		this.datasetId = datasetId;
		this.username = username;
		this.catalogue = catalogue;
		this.organization = organization;
	}

	@Override
	public void run() {

		logger.info("Association thread started to put the dataset with id="+ datasetId + " into group with title " + groupTitle + " for user " + username);

		// create the group
		CkanGroup group = catalogue.createGroup(groupTitle, groupTitle, "");

		if(group == null){

			logger.warn("The group doesn't exist!!! Unable to perform such association");

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

			}			
		}

	}

}
