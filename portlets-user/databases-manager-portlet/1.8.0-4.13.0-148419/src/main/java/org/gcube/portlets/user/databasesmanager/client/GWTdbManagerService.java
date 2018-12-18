package org.gcube.portlets.user.databasesmanager.client;

import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.portlets.user.databasesmanager.client.datamodel.FileModel;
import org.gcube.portlets.user.databasesmanager.client.datamodel.GeneralOutputFromServlet;
import org.gcube.portlets.user.databasesmanager.client.datamodel.Result;
import org.gcube.portlets.user.databasesmanager.client.datamodel.Row;
import org.gcube.portlets.user.databasesmanager.client.datamodel.SamplingResultWithFileFromServlet;
import org.gcube.portlets.user.databasesmanager.client.datamodel.SubmitQueryResultWithFileFromServlet;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

//interface that defines RPC methods
@RemoteServiceRelativePath("dbManagerService")
public interface GWTdbManagerService extends RemoteService {

	List<FileModel> getResource() throws Exception;

	LinkedHashMap<String, FileModel> getDBInfo(String resourceName)
			throws Exception;

	List<FileModel> getDBSchema(LinkedHashMap<String, String> dataInput)
			throws Exception;

	SubmitQueryResultWithFileFromServlet submitQuery(LinkedHashMap<String, String> dataDB,
			String query, boolean valueReadOnlyQuery,
			boolean smartCorrectionQuery, String language, String UID)
			throws Exception;

	SamplingResultWithFileFromServlet sample(LinkedHashMap<String, String> dataInput, String elementType)
			throws Exception;

	SamplingResultWithFileFromServlet smartSample(LinkedHashMap<String, String> dataInput, String elementType)
			throws Exception;

	SamplingResultWithFileFromServlet randomSample(LinkedHashMap<String, String> dataInput, String elementType)
			throws Exception;

	List<Row> parseCVSString(List<Result> result, List<String> attrNames)
			throws Exception;

	LinkedHashMap<String, FileModel> getTableDetails(
			LinkedHashMap<String, String> dataInput) throws Exception;

	PagingLoadResult<Result> LoadTables(PagingLoadConfig config,
			LinkedHashMap<String, String> dataInput,  String elementType, boolean SearchTable,
			String keyword) throws Exception;

	PagingLoadResult<Row> loadSubmitResult(PagingLoadConfig config,
			List<String> listAttributes, String UID) throws Exception;

	Boolean removeComputation(String submitQueryUID) throws Exception;
	
	void refreshDataOnServer(String submitQueryUID) throws Exception;

	GeneralOutputFromServlet refreshDataTree(String ElementType,
			LinkedHashMap<String, String> inputData, FileModel element) throws Exception;
	
}
