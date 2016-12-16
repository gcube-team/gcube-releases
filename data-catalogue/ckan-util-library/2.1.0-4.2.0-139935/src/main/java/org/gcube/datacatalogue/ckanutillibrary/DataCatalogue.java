package org.gcube.datacatalogue.ckanutillibrary;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.gcube.datacatalogue.ckanutillibrary.models.CKanUserWrapper;
import org.gcube.datacatalogue.ckanutillibrary.models.CkanDatasetRelationship;
import org.gcube.datacatalogue.ckanutillibrary.models.DatasetRelationships;
import org.gcube.datacatalogue.ckanutillibrary.models.ResourceBean;
import org.gcube.datacatalogue.ckanutillibrary.models.RolesCkanGroupOrOrg;
import org.json.simple.JSONObject;

import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanGroup;
import eu.trentorise.opendata.jackan.model.CkanLicense;
import eu.trentorise.opendata.jackan.model.CkanOrganization;
import eu.trentorise.opendata.jackan.model.CkanResource;

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
	 * Returns the list of groups to whom the user belongs (with any role)
	 * @param username
	 * @return a list of groups
	 */
	List<CkanGroup> getGroupsByUser(String username);

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
	 * Return the manage product property
	 * @return the manage product property
	 */
	boolean isManageProductEnabled();

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
	 * @param name (unique identifier)
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
	String createCKanDataset(String apiKey, String title, String name, String organizationNameOrId, String author,
			String authorMail, String maintainer, String maintainerMail, long version, String description, String licenseId,
			List<String> tags, Map<String, String> customFields, List<ResourceBean> resources, boolean setPublic);

	/**
	 * Create a dataset with those information. The method allows to have multiple values for the same custom field key.
	 * NOTE: unfortunately java doesn't support overload in java interface methods (that's way I cannot use the same name
	 * for the method)
	 * @param apiKey
	 * @param title
	 * @param name (unique identifier)
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
	String createCKanDatasetMultipleCustomFields(String apiKey, String title, String name, String organizationNameOrId, String author,
			String authorMail, String maintainer, String maintainerMail, long version, String description, String licenseId,
			List<String> tags, Map<String, List<String>> customFields, List<ResourceBean> resources, boolean setPublic);

	/**
	 * Given the id or the name of the dataset it returns its current url by contacting the uri resolver.
	 * If no uri resolver is available, an url that is not guaranteed to be long term valid will be generated.
	 * @param datasetId
	 * @return The url of the dataset on success, null otherwise
	 */
	String getUrlFromDatasetIdOrName(String datasetIdOrName);

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
	 * Associate a group with its parent.s
	 * @param parentName
	 * @param group
	 * @return
	 */
	boolean setGroupParent(String parentName, String groupName);

	/**
	 * Returns a Map with key 'capacity' and as value a list of users with that capacity into the organization organizationName.
	 * @return 
	 */
	Map<String, List<String>> getRolesAndUsersOrganization(String organizationName);

	/**
	 * Returns a Map with key 'capacity' and as value a list of users with that capacity into the group groupNameOrTitle.
	 * @return 
	 */
	Map<RolesCkanGroupOrOrg, List<String>> getRolesAndUsersGroup(String groupNameOrTitle);

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
	 * Remove a dataset from a group.
	 * @param groupNameOrId the id or the name of the group.
	 * @param datasetNameOrId the id or the name of the dataset
	 * @param apiKey (the apiKey should belong to someone that has the role of editor/admin of the organization in which
	 * 		  the dataset is placed, plus the admin role into the destination group.
	 * @return true on success, false otherwise
	 */
	boolean removeDatasetFromGroup(String groupNameOrId, String datasetNameOrId,
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

	/**
	 * Set searchable field
	 * The field, if set to true, allows organization's users to view private products also in groups
	 * @param datasetId the id of the dataset to update
	 * @param searchable the value to assign to the field
	 * @return true if the field is set, false otherwise
	 */
	boolean setSearchableField(String datasetId, boolean searchable);

	/**
	 * Retrieve the list of groups in this instance
	 * @return a list of groups
	 */
	List<CkanGroup> getGroups();

	/**
	 * Upload a file to ckan and attach it as resource.
	 * @param file
	 * @param packageId
	 * @param apiKey
	 * @param fileName
	 * @param description
	 * @return 
	 */
	CkanResource uploadResourceFile(File file, String packageId, String apiKey, String fileName, String description);

	/**
	 * Allows to change the url, the name and the description of a resource.
	 * @param resourceId
	 * @param url
	 * @param name
	 * @param description
	 * @param urlType
	 * @param apiKey
	 * @return
	 */
	boolean patchResource(String resourceId, String url, String name, String description, String urlType, String apiKey);

	/**
	 * Patch a product with product id productId by using the couples in toChange.
	 * NOTE: only the specified custom fields will be changed. If a custom field with a given key
	 * already exists, the new values are added at the end of the list.
	 * @param productId
	 * @param apiKey
	 * @param toChange
	 * @return true on success, false otherwise
	 */
	boolean patchProductCustomFields(String productId, String apiKey, Map<String, List<String>> customFieldsToChange);
	
	/**
	 * Remove a custom field in the product that has a given key and value. If more than ones are present, the first one is removed.
	 * @return true on success, false otherwise.
	 */
	boolean removeCustomField(String productId, String key, String value, String apiKey);

	/**
	 * Remove a tag from a product
	 * @param productId
	 * @param apiKey
	 * @param tagToRemove
	 * @return true on success, false otherwise
	 */
	boolean removeTag(String productId, String apiKey, String tagToRemove);

	/**
	 * Add a tag from a product
	 * @param productId
	 * @param apiKey
	 * @param tagToAdd
	 * @return true on success, false otherwise
	 */
	boolean addTag(String productId, String apiKey, String tagToAdd);

	/**
	 * Get the parent groups of this group
	 * @return the group parent, if any
	 */
	List<CkanGroup> getParentGroups(String groupName, String apiKey);
	
	/**
	 * Check if a dataset is into the given group
	 * @param groupName
	 * @param datasetId
	 * @return true if it belongs to the group, false otherwise
	 */
	boolean isDatasetInGroup(String groupName, String datasetId);
	
	/**
	 * Retrieve products in a group. Please note that at most 1000 datasets are returned.
	 * @return a list of datasets in a group
	 */
	List<CkanDataset> getProductsInGroup(String groupName);

	/**
	 * Retrieve the url of the uri resolver for this catalogue instance/scope
	 * @return
	 */
	String getUriResolverUrl();
	
	/**
	 * Require to patch a product according to the content of the parameter jsonRequest
	 * @param productId
	 * @param jsonRequest
	 * @param apiKey
	 * @return error message if any, null otherwise
	 */
	String patchProductWithJSON(String productId, JSONObject jsonRequest, String apiKey);
}
