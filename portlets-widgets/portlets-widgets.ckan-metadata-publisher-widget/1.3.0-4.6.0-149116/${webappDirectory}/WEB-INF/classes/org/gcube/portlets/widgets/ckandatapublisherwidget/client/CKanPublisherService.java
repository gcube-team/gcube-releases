package org.gcube.portlets.widgets.ckandatapublisherwidget.client;

import java.util.List;

import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.DatasetBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.OrganizationBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceElementBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.licenses.LicenseBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata.MetaDataProfileBean;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * CKAN publisher services.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
@RemoteServiceRelativePath("ckanservices")
public interface CKanPublisherService extends RemoteService {

	/**
	 * Retrieve the list of licenses to show to the user.
	 * @return a LicenseBean on success, <b>null</b> on error.
	 */
	List<LicenseBean> getLicenses();

	/**
	 * Retrieve the list of profiles for a given organization name .
	 * @return a List<MetaDataProfileBean> on success, <b>null</b> on error.
	 */
	List<MetaDataProfileBean> getProfiles(String orgName) throws Exception;

	/**
	 * Retrieve a partially filled bean given a folder id/file id and its owner.
	 * @param folderIdOrFileId the id of the folder of file to publish
	 * @return @return a DatasetMetadataBean on success, <b>null</b> on error.
	 * @throws Exception 
	 */
	DatasetBean getDatasetBean(String folderIdOrFileId) throws Exception;

	/**
	 * Try to create such dataset starting from the information contained into the toCreate bean.
	 * @param toCreate
	 * @return the sent bean filled with the needed information
	 */
	DatasetBean createCKanDataset(DatasetBean toCreate) throws Exception;

	/**
	 * Add this resource to the dataset whose id is datasetId
	 * @param resource
	 * @param datasetId
	 */
	ResourceElementBean addResourceToDataset(ResourceElementBean resource, String datasetId) throws Exception;

	/**
	 * Delete this resource from the dataset with id datasetId
	 * @param resource
	 * @param datasetId
	 * @return <b>true</b> on success, false otherwise
	 * @throws Exception 
	 */
	boolean deleteResourceFromDataset(ResourceElementBean resource) throws Exception;

	/**
	 * Given the title the user wants to give to the new product to create, a check is performed
	 * to understand if a dataset with the proposed title (and so the id generated at server side) already exists
	 * @param title
	 * @return true if it exists, false otherwise
	 * @throws Exception 
	 */
	boolean datasetIdAlreadyExists(String title, String orgName) throws Exception;

	/**
	 * Retrieve the list of groups the user can choose to associate this product with.
	 * @param orgName retrieve the groups in the context linked to this name. If null, returns
	 * the one in the current context.
	 * @return a list of groups' beans
	 */
	List<OrganizationBean> getUserGroups(String orgName);
	
	/**
	 * The method checks if the user is a publisher or he/she doesn't have the rights to publish
	 * @return true if he/she can publish, false otherwise
	 * @throws Exception 
	 */
	boolean isPublisherUser(boolean isWorkspaceRequest) throws Exception;
	
	/**
	 * Get the list of vocabulary tags for this scope
	 * @param orgName
	 * @return
	 * @throws Exception 
	 */
	List<String> getTagsForOrganization(String orgName) throws Exception;
	
	/**
	 * Validate a geo json field
	 * @param json
	 * @return
	 */
	boolean isGeoJSONValid(String json) throws Exception;
}
