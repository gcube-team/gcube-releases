package org.gcube.data_catalogue.grsf_publish_ws.utils.threads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanGroup;

/**
 * Associate the dataset to a group.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class AssociationToGroupThread extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(AssociationToGroupThread.class);

	private List<String> groupsTitles;
	private String datasetId;
	private String username;
	private DataCatalogue catalogue;
	private String organizationId;
	private String apiKey;

	/**
	 * 
	 * @param groupsTitles
	 * @param datasetId
	 * @param organizationId
	 * @param username
	 * @param catalogue
	 * @param apiKey
	 */
	public AssociationToGroupThread(List<String> groupsTitles, String datasetId, String organizationId,
			String username, DataCatalogue catalogue, String apiKey) {
		this.groupsTitles = groupsTitles;
		this.datasetId = datasetId;
		this.username = username;
		this.catalogue = catalogue;
		this.organizationId = organizationId;
		this.apiKey = apiKey;
	}

	@Override
	public void run() {

		try{

			logger.info("Association thread started to put the dataset with id="+ datasetId + " into group with title(s) " + groupsTitles + " for user " + username);

			// find parents' groups
			findHierarchy(groupsTitles, catalogue, apiKey);

			Set<String> uniqueGroups = new HashSet<String>(groupsTitles);

			logger.info("Full set of groups is " + uniqueGroups);

			// retrieve the role to be assigned according the one the user has into the organization of the dataset
			RolesCkanGroupOrOrg role = RolesCkanGroupOrOrg.valueOf(catalogue.getRoleOfUserInOrganization(username, organizationId, apiKey).toUpperCase());

			if(!role.equals(RolesCkanGroupOrOrg.ADMIN))
				role = RolesCkanGroupOrOrg.MEMBER; // decrease the role to member if it is not an admin

			for (String groupTitle : uniqueGroups) {

				logger.debug("Setting role " + role + " into group " + groupTitle + " to user " + username);
				boolean assigned = catalogue.checkRoleIntoGroup(username, groupTitle, role);

				if(!assigned){
					logger.warn("The user " + username + " has not enough privileges to associate the dataset into group OR the group " + groupTitle + " doesn't exist ");
					continue;
				}
				else{
					boolean putIntoGroup = catalogue.assignDatasetToGroup(groupTitle, datasetId, apiKey);
					logger.info("Was product put into group " + groupTitle + "? " + putIntoGroup);
				}
			}
			logger.info("The Association Group thread ended correctly");
		}catch(Exception e){
			logger.error("Exception follows ", e);
		}
	}

	/**
	 * Find the hierarchy of trees
	 * @param uniqueGroups
	 * @param catalogue
	 * @param user's api key
	 */
	public static void findHierarchy(
			List<String> groupsTitles,
			DataCatalogue catalogue, 
			String apiKey) {

		ListIterator<String> iterator = groupsTitles.listIterator();

		while (iterator.hasNext()) {
			String group = (String) iterator.next();

			List<CkanGroup> parents = catalogue.getParentGroups(group, apiKey);

			if(parents == null || parents.isEmpty())
				continue;

			for (CkanGroup ckanGroup : parents) {
				List<String> parentsList = new ArrayList<String>(Arrays.asList(ckanGroup.getName()));
				findHierarchy(parentsList, catalogue, apiKey);

				for (String parent : parentsList) {
					iterator.add(parent);	
				}
			}
		}

	}

}