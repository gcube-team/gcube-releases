package org.gcube.portlets.user.statisticalmanager.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.statisticalmanager.client.bean.ComputationStatus;
import org.gcube.portlets.user.statisticalmanager.client.bean.CsvMetadata;
import org.gcube.portlets.user.statisticalmanager.client.bean.FileMetadata;
import org.gcube.portlets.user.statisticalmanager.client.bean.ImportStatus;
import org.gcube.portlets.user.statisticalmanager.client.bean.JobItem;
import org.gcube.portlets.user.statisticalmanager.client.bean.Operator;
import org.gcube.portlets.user.statisticalmanager.client.bean.OperatorsClassification;
import org.gcube.portlets.user.statisticalmanager.client.bean.ResourceItem;
import org.gcube.portlets.user.statisticalmanager.client.bean.TableItemSimple;
import org.gcube.portlets.user.statisticalmanager.client.bean.output.ImagesResource;
import org.gcube.portlets.user.statisticalmanager.client.bean.output.MapResource;
import org.gcube.portlets.user.statisticalmanager.client.bean.output.Resource;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.Parameter;

import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface StatisticalManagerPortletServiceAsync {
	
	void getOperatorsClassifications(
			AsyncCallback<List<OperatorsClassification>> callback);
	
	/**
	 * @param operator
	 * @return
	 */
	void getParameters(Operator operator, AsyncCallback<List<Parameter>> callback);

	void startComputation(Operator op, String computationTitle,
			String computationDescription, AsyncCallback<String> asyncCallback);

	/**
	 * @param computationId
	 * @param asyncCallback
	 */
	void getComputationStatus(String computationId, AsyncCallback<ComputationStatus> asyncCallback);

	void getTableItems(List<String> templates, AsyncCallback<List<TableItemSimple>> callback);

	void getFileItems(List<String> templates, AsyncCallback<List<TableItemSimple>> callback);

	void getResourcesItems(AsyncCallback<ListLoadResult<ResourceItem>> callback);


	void getListJobs(AsyncCallback<ListLoadResult<JobItem>> callback);

	void getResourceByJobId(String jobId, AsyncCallback<Resource> callback);

	//	void getJobOutput(String jobId, AsyncCallback<JobOutput> callback);

	void importTable(CsvMetadata csvMetadata, AsyncCallback<String> callback);

	void getImportsStatus(Date todayDate, AsyncCallback<List<ImportStatus>> callback);

	void getImportStatusById(String id, AsyncCallback<ImportStatus> callback);

	void removeComputation(String id, AsyncCallback<Void> callback);

	void getMapFromMapResource(MapResource mapResource,
			AsyncCallback<Map<String, Resource>> callback);

	void getImagesInfoFromImagesResource(ImagesResource imgsRes,
			AsyncCallback<Map<String, String>> callback);

	void getParametersMapByJobId(String jobId,
			AsyncCallback<Map<String, String>> callback);

	void saveImages(String computationId, Map<String, String> mapImages, AsyncCallback<String> callback);

	void removeResource(String id, AsyncCallback<Void> callback);

	void removeImport(String id, AsyncCallback<Void> callback);

	void getCsvMetadataFromCsvImporterWizard(AsyncCallback<CsvMetadata> callback);

	void exportResource(String folderId, String fileName, ResourceItem resourceItem, AsyncCallback<String> callback);

	void resubmit(JobItem jobItem, AsyncCallback<String> callback);

	 void getFilePathFromImporterWizard(AsyncCallback<FileMetadata> callback);


	 void importFile(FileMetadata fileMetadata,AsyncCallback<String> callback);

	void checkSession(AsyncCallback<Void> callback);

}
