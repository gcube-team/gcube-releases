package org.gcube.application.datamanagementfacilityportlet.client.rpc;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ClientResource;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ExecutionEnvironmentModel;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.GroupGenerationRequest;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.Response;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.SessionStatistics;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save.SaveOperationProgress;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save.SaveRequest;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientAnalysisType;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientLogicType;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientResourceType;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DataManagementFacilityServiceAsync {

	
	public void submitGroupGenerationRequest(GroupGenerationRequest request,AsyncCallback<Response> response);

	public void loadGroupGenerationRequestDetails(String id,
			AsyncCallback<GroupGenerationRequest> callback);

	public void getEnvironments(ClientLogicType type,AsyncCallback<ArrayList<ExecutionEnvironmentModel>> callback);


	public void editGeneration(GroupGenerationRequest request,
			AsyncCallback<Response> callback);

	public void removeGeneration(String id, Boolean deleteTables, Boolean deleteJobs,
			AsyncCallback<Response> callback);

	public void editSource(ClientResource request, AsyncCallback<Response> callback);

	public void dropTable(Integer sourceId, AsyncCallback<Response> callback);


	
	public void setLiveQuery(String query, AsyncCallback<List<String>> callback);

	public void setImportRequestType(ClientResourceType type, AsyncCallback<Response> callback);

	public void getSessionStatistics(AsyncCallback<SessionStatistics> callback);

	public void analyzeResources(List<ClientAnalysisType> type,String title, String description, Integer[] ids,
			AsyncCallback<Response> callback);


	public void resubmitRequest(String id, AsyncCallback<Response> callback);

	void getSaveProgress(AsyncCallback<SaveOperationProgress> callback);

	void saveOperationRequest(SaveRequest request,
			AsyncCallback<SaveOperationProgress> callback);

	void removeAnalysis(String analisysId, AsyncCallback<Response> callback);

	
}
