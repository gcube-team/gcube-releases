package org.gcube.datacatalogue.ckanutillibrary;

import java.util.List;
import java.util.Map;

import org.gcube.datacatalogue.ckanutillibrary.models.CKanUserWrapper;
import org.gcube.datacatalogue.ckanutillibrary.models.ResourceBean;
import org.gcube.datacatalogue.ckanutillibrary.models.RolesIntoOrganization;

import eu.trentorise.opendata.jackan.model.CkanLicense;
import eu.trentorise.opendata.jackan.model.CkanOrganization;

/**
 * This is the ckan-util-library interface that shows the utility methods.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public interface CKanUtils {

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
	Map<String, List<RolesIntoOrganization>> getGroupsAndRolesByUser(String username, List<RolesIntoOrganization> rolesToMatch);

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
	String findLicenseIdByLicense(String chosenLicense);

	/**
	 * Set dataset private
	 * @param priv
	 * @param organizationId
	 * @param datasetId
	 * @param apiKey the user's api key
	 * @return true on success, false otherwise
	 */
	boolean setDatasetPrivate(boolean priv, String organizationId, String datasetId, String apiKey);

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
	 * @param setPublic (manage visibility)
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
	 * @param apiKey the current user's api key
	 * @return true on success, false otherwise
	 */
	boolean isSysAdmin(String username, String apiKey);

	/**
	 * Check if this role is present for this user in that organization. If it is not present we need to add it.
	 * @param username
	 * @param organizationName
	 * @param correspondentRoleToCheck
	 * @return true if the role can be set, false if it cannot
	 */
	boolean checkRole(String username, String organizationName,
			RolesIntoOrganization correspondentRoleToCheck);

	/**
	 * The method tries to create an organization with name orgName
	 * @param orgName the name of the organization
	 * @param token the gcube token to perform the operation
	 * @return true on success, false otherwise
	 * @throws Exception 
	 */
	boolean createOrganization(String orgName, String token) throws Exception;

	/**
	 * Return the catalogue portlet for this url
	 * @return
	 */
	String getPortletUrl();
}
