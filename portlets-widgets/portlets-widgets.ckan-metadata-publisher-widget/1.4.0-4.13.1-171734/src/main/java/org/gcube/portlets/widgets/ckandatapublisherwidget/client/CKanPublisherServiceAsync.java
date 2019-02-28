package org.gcube.portlets.widgets.ckandatapublisherwidget.client;

import java.util.List;

import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.DatasetBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.OrganizationBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceElementBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.licenses.LicenseBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata.MetaDataProfileBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * CKAN publisher services RPC.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public interface CKanPublisherServiceAsync {

	/**
	 * Retrieve the list of licenses to show to the user.
	 * @return a LicenseBean on success, <b>null</b> on error.
	 */
	void getLicenses(AsyncCallback<List<LicenseBean>> callback);

	/**
	 * Retrieve a partially filled bean given a folder id/file id and its owner.
	 * @param folderIdOrFileId the id of the folder of file to publish
	 * @return @return a DatasetMetadataBean on success, <b>null</b> on error.
	 */
	void getDatasetBean(String folderIdOrFileId,
			AsyncCallback<DatasetBean> callback);

	/**
	 * Try to create such dataset starting from the information contained into the toCreate bean.
	 * @param toCreate
	 * @return the sent bean full filled with the needed information
	 */
	void createCKanDataset(DatasetBean toCreate,
			AsyncCallback<DatasetBean> callback);

	/**
	 * Add this resource to the dataset whose id is datasetId
	 * @param resource
	 * @param datasetId
	 * @param callback
	 */
	void addResourceToDataset(ResourceElementBean resource, String datasetId,
			AsyncCallback<ResourceElementBean> callback);

	/**
	 * Delete this resource from the dataset with id datasetId
	 * @param resource
	 * @return <b>true</b> on success, false otherwise
	 */
	void deleteResourceFromDataset(ResourceElementBean resource, AsyncCallback<Boolean> callback);

	/**
	 * Retrieve the list of profiles for a given organization name .
	 * @return a List<MetaDataProfileBean> on success, <b>null</b> on error.
	 */
	void getProfiles(String orgName, AsyncCallback<List<MetaDataProfileBean>> callback);

	/**
	 * Given the title the user wants to give to the new product to create, a check is performed
	 * to understand if a dataset with the proposed title (and so the id generated at server side) already exists
	 * @param title
	 * @return true if it exists, false otherwise
	 */
	void datasetIdAlreadyExists(String title, String orgName, AsyncCallback<Boolean> callback);

	//	/**
	//	 * Return a tree object representing the whole folder hierarchy
	//	 * @param folderId
	//	 * @return ResourceElementBean
	//	 */
	//	void getTreeFolder(String folderId,
	//			AsyncCallback<ResourceElementBean> callback);

	/**
	 * Retrieve the list of groups the user can choose to associate this product with.
	 * @param orgName retrieve the groups in the context linked to this name. If null, returns
	 * the one in the current context.
	 * @return a list of groups' beans
	 */
	void getUserGroups(String orgName, AsyncCallback<List<OrganizationBean>> callback);

	/**
	 * The method checks if the user is a publisher or he/she doesn't have the rights to publish
	 * @return true if he/she can publish, false otherwise
	 */
	void isPublisherUser(boolean isWorkspaceRequest,
			AsyncCallback<Boolean> callback);

	/**
	 * Get the list of vocabulary tags for this scope
	 * @param orgName
	 * @return
	 */
	void getTagsForOrganization(String orgName,
			AsyncCallback<List<String>> callback);

	/**
	 * Validate a geo json field
	 * @param json
	 * @return
	 */
	void isGeoJSONValid(String json, AsyncCallback<Boolean> callback);
}
