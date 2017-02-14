package org.gcube.portlets.user.databasesmanager.client;

import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.portlets.user.databasesmanager.client.datamodel.FileModel;
import org.gcube.portlets.user.databasesmanager.client.datamodel.GeneralOutputFromServlet;
import org.gcube.portlets.user.databasesmanager.client.datamodel.Result;
import org.gcube.portlets.user.databasesmanager.client.datamodel.SamplingResultWithFileFromServlet;
import org.gcube.portlets.user.databasesmanager.client.datamodel.Row;
import org.gcube.portlets.user.databasesmanager.client.datamodel.SubmitQueryResultWithFileFromServlet;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GWTdbManagerServiceAsync {

	void getResource(AsyncCallback<List<FileModel>> asyncCallback);

	void getDBInfo(String resourceName,
			AsyncCallback<LinkedHashMap<String, FileModel>> asyncCallback);

	void getDBSchema(LinkedHashMap<String, String> dataInput,
			AsyncCallback<List<FileModel>> callback);

	void submitQuery(LinkedHashMap<String, String> dataDB, String query,
			boolean valueReadOnlyQuery, boolean smartCorrectionQuery,
			String language, String UID, AsyncCallback<SubmitQueryResultWithFileFromServlet> callback);

	void parseCVSString(List<Result> result, List<String> attrNames,
			AsyncCallback<List<Row>> callback);

	void sample(LinkedHashMap<String, String> dataInput, String elementType,
			AsyncCallback<SamplingResultWithFileFromServlet> callback);

	void getTableDetails(LinkedHashMap<String, String> dataInput,
			AsyncCallback<LinkedHashMap<String, FileModel>> callback);

	void smartSample(LinkedHashMap<String, String> dataInput, String elementType,
			AsyncCallback<SamplingResultWithFileFromServlet> callback);

	void randomSample(LinkedHashMap<String, String> dataInput, String elementType,
			AsyncCallback<SamplingResultWithFileFromServlet> callback);

	void LoadTables(PagingLoadConfig config,
			LinkedHashMap<String, String> dataInput,  String elementType, boolean SearchTable,
			String keyword, AsyncCallback<PagingLoadResult<Result>> callback);

	void removeComputation(String uidSubmitQuery,
			AsyncCallback<Boolean> callback);

	void loadSubmitResult(PagingLoadConfig config,
			List<String> listAttributes, String UID,
			AsyncCallback<PagingLoadResult<Row>> callback);

	void refreshDataOnServer(String submitQueryUID, AsyncCallback<Void> callback);
	
	void refreshDataTree(String ElementType,LinkedHashMap<String, String> inputData, FileModel element,
			AsyncCallback<GeneralOutputFromServlet> callback);
}
