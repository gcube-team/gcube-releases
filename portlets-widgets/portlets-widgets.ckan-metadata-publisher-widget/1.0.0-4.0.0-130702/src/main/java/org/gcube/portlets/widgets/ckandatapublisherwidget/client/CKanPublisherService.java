package org.gcube.portlets.widgets.ckandatapublisherwidget.client;

import java.util.List;

import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.DatasetMetadataBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.LicensesBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.MetaDataProfileBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceBeanWrapper;

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
	LicensesBean getLicenses();
	
	/**
	 * Retrieve the list of profiles for a given organization name .
	 * @return a List<MetaDataProfileBean> on success, <b>null</b> on error.
	 */
	List<MetaDataProfileBean> getProfiles(String orgName);
	
	/**
	 * Retrieve a partially filled bean given a folder id and its owner.
	 * @param folderId
	 * @param owner
	 * @return @return a DatasetMetadataBean on success, <b>null</b> on error.
	 */
	DatasetMetadataBean getDatasetBean(String folderId, String owner);
	
	/**
	 * Try to create such dataset starting from the information contained into the toCreate bean.
	 * @param toCreate
	 * @param isWorkspaceRequest if the call comes from the workspace
	 * @return the sent bean full filled with the needed information
	 */
	DatasetMetadataBean createCKanDataset(DatasetMetadataBean toCreate, boolean isWorkspaceRequest);
	
	/**
	 * Add this resource to the dataset whose id is datasetId
	 * @param resource
	 * @param datasetId
	 * @param owner of the dataset
	 */
	ResourceBeanWrapper addResourceToDataset(ResourceBeanWrapper resource, String datasetId, String owner);
	
	/**
	 * Delete this resource from the dataset with id datasetId
	 * @param resource
	 * @param datasetId
	 * @param owner of the dataset
	 * @return <b>true</b> on success, false otherwise
	 */
	boolean deleteResourceFromDataset(ResourceBeanWrapper resource, String owner);

}
