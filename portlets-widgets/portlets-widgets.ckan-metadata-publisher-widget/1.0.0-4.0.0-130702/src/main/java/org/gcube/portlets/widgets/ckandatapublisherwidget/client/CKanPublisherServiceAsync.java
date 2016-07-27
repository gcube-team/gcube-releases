package org.gcube.portlets.widgets.ckandatapublisherwidget.client;

import java.util.List;

import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.DatasetMetadataBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.LicensesBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.MetaDataProfileBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceBeanWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * CKAN publisher services RPC.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public interface CKanPublisherServiceAsync {

	/**
	 * Retrieve the list of licenses to show to the user.
	 * @return a LicenseBean on success, <b>null</b> on error.
	 */
	void getLicenses(AsyncCallback<LicensesBean> callback);

	/**
	 * Retrieve a partially filled bean given a folder id and its owner.
	 * @param folderId
	 * @param owner
	 * @return @return a DatasetMetadataBean on success, <b>null</b> on error.
	 */
	void getDatasetBean(String folderId, String owner,
			AsyncCallback<DatasetMetadataBean> callback);

	/**
	 * Try to create such dataset starting from the information contained into the toCreate bean.
	 * @param toCreate
	 * @param isWorkspaceRequest if the call comes from the workspace
	 * @return the sent bean full filled with the needed information
	 */
	void createCKanDataset(DatasetMetadataBean toCreate, boolean isWorkspaceRequest,
			AsyncCallback<DatasetMetadataBean> callback);

	/**
	 * Add this resource to the dataset whose id is datasetId
	 * @param resource
	 * @param datasetId
	 * @param owner of the dataset
	 * @param callback
	 */
	void addResourceToDataset(ResourceBeanWrapper resource, String datasetId, String owner,
			AsyncCallback<ResourceBeanWrapper> callback);

	/**
	 * Delete this resource from the dataset with id datasetId
	 * @param resource
	 * @param owner of the dataset
	 * @return <b>true</b> on success, false otherwise
	 */
	void deleteResourceFromDataset(ResourceBeanWrapper resource,
			String owner, AsyncCallback<Boolean> callback);

	/**
	 * Retrieve the list of profiles for a given organization name .
	 * @return a List<MetaDataProfileBean> on success, <b>null</b> on error.
	 */
	void getProfiles(String orgName, AsyncCallback<List<MetaDataProfileBean>> callback);

}
