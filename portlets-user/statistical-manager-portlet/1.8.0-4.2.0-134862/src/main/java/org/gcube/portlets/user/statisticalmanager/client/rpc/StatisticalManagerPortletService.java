package org.gcube.portlets.user.statisticalmanager.client.rpc;

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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("statman")
public interface StatisticalManagerPortletService extends RemoteService {
	
	// get all operators and categories
	List<OperatorsClassification> getOperatorsClassifications() throws Exception;
	
	// get parameters for a given operator
	List<Parameter> getParameters(Operator operator) throws Exception;
	
	String startComputation(Operator op, String computationTitle, String computationDescription) throws Exception;
	
	// get a status for a computation
	ComputationStatus getComputationStatus(String computationId) throws Exception;

	// get tables info belongs to a given templates set (for relative user)
	List<TableItemSimple> getTableItems(List<String> templates) throws Exception;

	List<TableItemSimple> getFileItems(List<String> templates) throws Exception;

	ListLoadResult<ResourceItem> getResourcesItems() throws Exception;

	ListLoadResult<JobItem> getListJobs() throws Exception;
	
	Resource getResourceByJobId(String jobId);
	
	Map<String, String> getParametersMapByJobId(String jobId) throws Exception;

	String importTable(CsvMetadata csvMetadata) throws Exception;
	
	CsvMetadata getCsvMetadataFromCsvImporterWizard() throws Exception;
	
	List<ImportStatus> getImportsStatus(Date todayDate);
	
	// get a status for an import
	ImportStatus getImportStatusById(String id);
	
	void removeComputation(String id) throws Exception;
	
	Map<String, Resource> getMapFromMapResource(MapResource mapResource) throws Exception;
	
	Map<String, String> getImagesInfoFromImagesResource(ImagesResource imgsRes) throws Exception;
	
	String saveImages(String computationId, Map<String, String> mapImages) throws Exception;

	void removeResource(String id) throws Exception;

	void removeImport(String id) throws Exception;

	String exportResource(String folderId, String fileName, ResourceItem resourceItem) throws Exception;

	String resubmit(JobItem jobItem) throws Exception;
	
	
	public FileMetadata getFilePathFromImporterWizard() throws Exception;
	public String importFile(FileMetadata fileMetadata) throws Exception;

	//void checkSession();
	
}
