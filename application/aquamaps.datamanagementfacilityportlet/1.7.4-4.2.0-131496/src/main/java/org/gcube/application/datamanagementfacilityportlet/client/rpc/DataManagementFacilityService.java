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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath(Tags.serviceImpl)
public interface DataManagementFacilityService extends RemoteService {

	public Response submitGroupGenerationRequest(GroupGenerationRequest request)throws Exception;
	public GroupGenerationRequest loadGroupGenerationRequestDetails(String id)throws Exception;
//	public Response uploadFile(String tag)throws Exception;
	public ArrayList<ExecutionEnvironmentModel> getEnvironments(ClientLogicType type)throws Exception;
	public Response removeGeneration(String id,Boolean deleteTables, Boolean deleteJobs)throws Exception;
	public Response editGeneration(GroupGenerationRequest request)throws Exception;
	public Response editSource(ClientResource request)throws Exception;
	public Response dropTable(Integer sourceId)throws Exception;
	public List<String> setLiveQuery(String query)throws Exception;
	public Response setImportRequestType(ClientResourceType type)throws Exception;
	public SessionStatistics getSessionStatistics()throws Exception;
	public Response analyzeResources(List<ClientAnalysisType> type, String title,
			String description, Integer[] ids) throws Exception;
	public Response resubmitRequest(String id)throws Exception;
	
	
	public SaveOperationProgress saveOperationRequest(SaveRequest request)throws Exception;
	public SaveOperationProgress getSaveProgress()throws Exception;
	
	
	public Response removeAnalysis(String analisysId)throws Exception;
	
	
}
