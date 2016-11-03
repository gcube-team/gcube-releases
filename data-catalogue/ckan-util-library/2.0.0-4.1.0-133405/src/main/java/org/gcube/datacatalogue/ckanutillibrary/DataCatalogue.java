package org.gcube.datacatalogue.ckanutillibrary;

import java.util.List;
import java.util.Map;

import org.gcube.datacatalogue.ckanutillibrary.models.CKanUserWrapper;
import org.gcube.datacatalogue.ckanutillibrary.models.CkanDatasetRelationship;
import org.gcube.datacatalogue.ckanutillibrary.models.DatasetRelationships;
import org.gcube.datacatalogue.ckanutillibrary.models.ResourceBean;
import org.gcube.datacatalogue.ckanutillibrary.models.RolesCkanGroupOrOrg;

import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanGroup;
import eu.trentorise.opendata.jackan.model.CkanLicense;
import eu.trentorise.opendata.jackan.model.CkanOrganization;

/**
 * This is the data-catalogue-library interface that shows the utility methods.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public interface DataCatalogue {

	/**
	 * Retrieve the API_KEY given the username (only if it is active).
	 * @param username
	 * @return an API_KEY string on success, null otherwise
	 */
	String getApiKeyFromUsername(String username);

	/**
	 * Retrieve the user given the API_KEY (the user is retrieved if it is active).
	 * @param the user api key
	 * @return an API_KEY string
	 */
	CKanUserWrapper getUserFromApiKey(String apiKey);

	/**
	 * Returns the list of organizations to whom the user belongs (with any role)
	 * @param username
	 * @return a list of organizations
	 */
	List<CkanOrganization> getOrganizationsByUser(String username);

	/**
	 * Returns the list of organizations' names to whom the user belongs (with any role)
	 * @param username
	 * @return a list of organizations
	 */
	List<String> getOrganizationsNamesByUser(String username);

	/**
	 * Given a username and a list of roles to be matched, find the organizations in which the user has these roles.
	 * Please note that the role SYSADMIN is infra-organizations, so won't be considered (use the method isSysAdmin(String username, String apiKey))
	 * @param username
	 * @param rolesToMatch
	 * @return a list (orgsName, roles in this organization), null on error
	 */
	Map<String, List<RolesCkanGroupOrOrg>> getOrganizationsAndRolesByUser(String username, List<RolesCkanGroupOrOrg> rolesToMatch);

	/**
	 * Return the ckan catalogue url in this scope.
	 * @return the catalogue url
	 */
	String getCatalogueUrl();

	/**
	 * Get the list of licenses' titles.
	 * @return the list of licenses' titles
	 */
	List<String> getLicenseTitles();

	/**
	 * Retrieve ckan licenses
	 * @return
	 */
	List<CkanLicense> getLicenses();

	/**
	 * Retrieve the list of organizations ids
	 * @return
	 */
	List<String> getOrganizationsIds();

	/**
	 * Retrieve the list of organizations names
	 * @return
	 */
	List<String> getOrganizationsNames();

	/**
	 * Finds the id associated to the chosen license
	 * @param chosenLicense
	 * @return the id on success, null otherwise
	 */
	String findLicenseIdByLicenseTitle(String chosenLicense);

	/**
	 * Add a resource described by the bean to the dataset id into resource.datasetId
	 * @param resource
	 * @param apiKey the user api key
	 * @return String the id of the resource on success, null otherwise
	 */
	String addResourceToDataset(ResourceBean resource, String apiKey);

	/**
	 * Remove the resource with id resourceId from the dataset in which it is.
	 * @param resourceId
	 * @param apiKey the user's api key
	 * @return true on success, false otherwise.
	 */
	boolean deleteResourceFromDataset(String resourceId, String apiKey);

	/**
	 * Create a dataset with those information.
	 * @param apiKey
	 * @param title
	 * @param organizationNameOrId
	 * @param author
	 * @param authorMail
	 * @param maintainer
	 * @param maintainerMail
	 * @param version
	 * @param description
	 * @param licenseId
	 * @param tags
	 * @param customFields
	 * @param resources
	 * @param setPublic (manage visibility: Admin role is needed)
	 * @return the id of the dataset on success, null otherwise
	 */
	String createCKanDataset(String apiKey, String title, String organizationNameOrId, String author,
			String authorMail, String maintainer, String maintainerMail, long version, String description, String licenseId,
			List<String> tags, Map<String, String> customFields, List<ResourceBean> resources, boolean setPublic);

	/**
	 * Given the id or the name of the dataset it returns its current url (e.g., http://ckan-catalogue-address.org/dataset/dataset-name)
	 * @param apiKey
	 * @param datasetId
	 * @param withoutHost if the host part is not needed (e.g. it returns only /dataset/dataset-name)
	 * @return The url of the dataset on success, null otherwise
	 */
	String getUrlFromDatasetIdOrName(String apiKey, String datasetIdOrName, boolean withoutHost);

	/**
	 * Check if this user is a sysadmin. The api key is used to authorize this call.
	 * @param username
	 * @return true on success, false otherwise
	 */
	boolean isSysAdmin(String username);

	/**
	 * Check if this role is present for this user in the organization. If he/she is not present we need to add it with the given role.
	 * @param username
	 * @param organizationName
	 * @param correspondentRoleToCheck
	 * @return true if the role can be set, false if it cannot
	 */
	boolean checkRoleIntoOrganization(String username, String organizationName, RolesCkanGroupOrOrg correspondentRoleToCheck);

	/**
	 * Check if this role is present for this user in the group. If he/she is not present we need to add it with the given role.
	 * @param username
	 * @param organizationName
	 * @param correspondentRoleToCheck
	 * @return true if the role can be set, false if it cannot
	 */
	boolean checkRoleIntoGroup(String username, String groupName, RolesCkanGroupOrOrg correspondentRoleToCheck);

	/**
	 * Return the catalogue portlet for this context(i.e. scope)
	 * @return
	 */
	String getPortletUrl();

	/**
	 * Sets a relationship between a "subject" dataset and an "object" dataset
	 * Please note that according to ckan apis the following must be valid:
	 * <blockquote>To create a relationship between two datasets (packages),
	 * You must be authorized to edit both the subject and the object datasets.</blockquote>
	 * The relationship can be one of the following:
	 * <ul>
	 * <li> depends_on
	 * <li> dependency_of
	 * <li> derives_from
	 * <li> has_derivation
	 * <li> child_of
	 * <li> parent_of
	 * <li> links_to
	 * <li> linked_from
	 * </ul>
	 * @param datasetIdSubject
	 * @param datasetIdObject
	 * @param relation
	 * @param relationComment a comment about the relation
	 * @param apiKey the user api key
	 * @return <b>true</b> on success, <b>false</b> otherwise.
	 */
	boolean createDatasetRelationship(String datasetIdSubject, String datasetIdObject, DatasetRelationships relation, String relationComment, String apiKey);

	/**
	 * Deletes a relationship between a "subject" dataset and an "object" dataset
	 * Please note that according to ckan apis the following must be valid:
	 * <blockquote>You must be authorised to delete dataset relationships, and to edit both the subject and the object datasets.</blockquote>
	 * The relationship can be one of the following:
	 * <ul>
	 * <li> depends_on
	 * <li> dependency_of
	 * <li> derives_from
	 * <li> has_derivation
	 * <li> child_of
	 * <li> parent_of
	 * <li> links_to
	 * <li> linked_from
	 * </ul>
	 * @param datasetIdSubject
	 * @param datasetIdObject
	 * @param relation
	 * @param apiKey the user api key
	 * @return <b>true</b> on success, <b>false</b> otherwise.
	 */
	boolean deleteDatasetRelationship(String datasetIdSubject, String datasetIdObject, DatasetRelationships relation, String apiKey);

	/**
	 * Returns the list of relationships between dataset datasetIdSubject and dataset datasetIdObject. If datasetIdObject is missing, the whole list of 
	 * relationships for dataset datasetIdSubject is returned.
	 * @param datasetIdSubject
	 * @param datasetIdObject
	 * @param apiKey
	 * @return list of CkanDatasetRelationship objects or null if an error occurs
	 */
	List<CkanDatasetRelationship> getRelationshipDatasets(String datasetIdSubject, String datasetIdObject, String apiKey);

	/**
	 * Checks if a product with such name already exists.
	 * Please remember that the name is unique.
	 * @param nameOrId the name or the id of the dataset to check
	 * @return
	 */
	boolean existProductWithNameOrId(String nameOrId);

	/**
	 * Create a CkanGroup.
	 * @param nameOrId a unique id for the group
	 * @param title a title  for the group
	 * @param description a description for the group
	 * @return the created CkanGroup on success, null otherwise
	 */
	CkanGroup createGroup(String nameOrId, String title, String description);

	/**
	 * Returns a Map with key 'capacity' and as value a list of users with that capacity into the organization organizationName.
	 * @return 
	 */
	Map<String, List<String>> getRolesAndUsersOrganization(String organizationName);

	/**
	 * Returns a Map with key 'capacity' and as value a list of users with that capacity into the group groupName.
	 * @return 
	 */
	Map<String, List<String>> getRolesAndUsersGroup(String groupName);

	/**
	 * Given the username and the organization name the method retrieves the role of the user (i.e. his/her 'capacity')
	 * @param username
	 * @param orgName
	 * @param apiKeyFromUsername
	 * @return the capacity of the user into this organization or null
	 */
	String getRoleOfUserInOrganization(String username, String orgName, String apiKeyFromUsername);

	/**
	 * Given the username and the group name the method retrieves the role of the user (i.e. his/her 'capacity')
	 * @param username
	 * @param groupName
	 * @param apiKeyFromUsername
	 * @return the capacity of the user into this group or null
	 */
	String getRoleOfUserInGroup(String username, String groupName, String apiKey);

	/**
	 * Assign a dataset to a group.
	 * @param groupNameOrId the id or the name of the destination group.
	 * @param datasetNameOrId the id or the name of the dataset
	 * @param apiKey (the apiKey should belong to someone that has the role of editor/admin of the organization in which
	 * 		  the dataset is placed, plus the admin role into the destination group.
	 * @return true on success, false otherwise
	 */
	boolean assignDatasetToGroup(String groupNameOrId, String datasetNameOrId,
			String apiKey);

	/**
	 * Delete the dataset with id datasetId. If purge is true, the product will be purged too.
	 * @param datasetId
	 * @param apiKey
	 * @param purge
	 * @return
	 */
	boolean deleteProduct(String datasetId, String apiKey, boolean purge);
	
	/**
	 * Retrieve a ckan dataset given its id
	 * @param datasetId
	 * @return
	 */
	CkanDataset getDataset(String datasetId, String apiKey);
}
