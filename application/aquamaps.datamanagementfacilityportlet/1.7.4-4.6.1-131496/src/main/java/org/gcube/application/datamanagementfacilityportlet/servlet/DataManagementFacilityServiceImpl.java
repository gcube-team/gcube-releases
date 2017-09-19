package org.gcube.application.datamanagementfacilityportlet.servlet;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Analysis;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.ComputationalInfrastructure;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.ExecutionEnvironment;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.LogicType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.CustomQueryDescriptorStubs;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AnalysisType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ExportStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.DataManagementFacilityService;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.Tags;
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
import org.gcube.application.datamanagementfacilityportlet.servlet.computational.ComputationalInfrastructureCache;
import org.gcube.application.datamanagementfacilityportlet.servlet.save.SaveManager;
import org.gcube.application.framework.core.session.ASLSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DataManagementFacilityServiceImpl extends RemoteServiceServlet
		implements DataManagementFacilityService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7696811592976374408L;

	private static final Logger logger = LoggerFactory.getLogger(DataManagementFacilityServiceImpl.class);
	
	@Override
	public Response submitGroupGenerationRequest(GroupGenerationRequest request) throws Exception{
		logger.trace("Received Group Generation Request "+request.getGenerationname());
		Response response= new Response(true);
		try{			
			ASLSession session = Utils.getSession(this.getThreadLocalRequest().getSession());
//			Maps maps=maps().build();
			
			
			
			SourceGenerationRequest serviceRequest= new SourceGenerationRequest();
			serviceRequest.setAuthor(session.getUsername());
			serviceRequest.setGenerationname(request.getGenerationname());
			serviceRequest.setDescription(request.getDescription());
			for(Integer sourceId:request.getSourceIds())
				serviceRequest.addSource(dataManagement().build().loadResource(sourceId));
			
			
			serviceRequest.setSubmissionBackend(request.getExecutionEnvironment().getBackend());
			serviceRequest.setExecutionEnvironment(request.getExecutionEnvironment().getInfrastructure());
			String backendUrl=ComputationalInfrastructureCache.getBackendUrl(request.getExecutionEnvironment().getBackend());
			if(backendUrl==null) 
					throw new Exception("Unable to find a backend definition for "+request.getExecutionEnvironment().getName()+", Backend is "+request.getExecutionEnvironment().getBackend());
			
			serviceRequest.setBackendURL(backendUrl);
			serviceRequest.setEnvironmentConfiguration(request.getExecutionEnvironment().getConfiguration());
			serviceRequest.setLogic(LogicType.valueOf(request.getLogic()+""));
			serviceRequest.setNumPartitions(request.getExecutionEnvironment().getDefaultPartitions());
			for(String algoString:request.getAlgorithms())serviceRequest.getAlgorithms().add(AlgorithmType.valueOf(algoString));
			
			
				for(Entry<String,Object> entry:request.getAdditionalParameters().entrySet()){
					FieldType type=FieldType.STRING;
					if(entry.getValue().getClass().isAssignableFrom(Integer.class))type=FieldType.INTEGER;
					else if(entry.getValue().getClass().isAssignableFrom(Double.class))type=FieldType.DOUBLE;
					else if(entry.getValue().getClass().isAssignableFrom(Boolean.class))type=FieldType.BOOLEAN;
					else if(entry.getValue().getClass().isAssignableFrom(Long.class))type=FieldType.LONG;
					serviceRequest.addParameter(new Field(entry.getKey(),entry.getValue()+"",type));
				}
			
			String id=dataManagement().build().submitRequest(serviceRequest);
			response.getAdditionalObjects().put(Tags.responseGroupGeneration,id);
			
			
		}catch(Exception e){
			logger.error("Unable to submit group",e);
			response.setStatus(false);
			response.getAdditionalObjects().put(Tags.errorMessage, e.getMessage());
		}
		return response;
	}
	@Override
	public GroupGenerationRequest loadGroupGenerationRequestDetails(String id)throws Exception {
		try{			
			GroupGenerationRequest toReturn=new GroupGenerationRequest();
//			TODO uncomment 
//			SourceGenerationRequestRequest serviceRequest=utils.getConfig().getDMInterface(session.getScope()).getRequest(id);
//			toReturn.setAlgorithms(serviceRequest.getAlgorithms());
//			toReturn.setAuthor(serviceRequest.getAuthor());
//			toReturn.setCloudGeneration(serviceRequest.getIsCloud());
//			toReturn.setCompletedtime(serviceRequest.getCompletedtime());
//			toReturn.setCurrentphasepercent(serviceRequest.getCurrentphasepercent());
//			toReturn.setDescription(serviceRequest.getDescription());
//			toReturn.setEnableimagegeneration(serviceRequest.getEnableimagegeneration());
//			toReturn.setEnablelayergeneration(serviceRequest.getEnablelayergeneration());
//			toReturn.setGeneratedhspec(serviceRequest.getGeneratedhspec());
//			toReturn.setGenerationname(serviceRequest.getGenerationname());
//			toReturn.setHcafsearchid(serviceRequest.getHcafsearchid());
//			toReturn.setHspensearchid(serviceRequest.getHspensearchid());
//			toReturn.setId(serviceRequest.getId());
//			toReturn.setIsuploadhcaf(serviceRequest.getIsuploadhcaf());
//			toReturn.setIsuploadhspen(serviceRequest.getIsuploadhspen());
//			toReturn.setPhase(GroupGenerationRequestPhase.valueOf(serviceRequest.getPhase()+""));
//			toReturn.setSubmissiontime(serviceRequest.getSubmissiontime());
			return toReturn;
		}catch(Exception e){
			logger.error("Unable to load group",e);
			throw e;
		}
	}
	
	@Override
	public ArrayList<ExecutionEnvironmentModel> getEnvironments(ClientLogicType logic)throws Exception{
		try{			
			ASLSession session = Utils.getSession(this.getThreadLocalRequest().getSession());
			ArrayList<ExecutionEnvironmentModel> toReturn=new ArrayList<ExecutionEnvironmentModel>();
			
			List<ComputationalInfrastructure> list=ComputationalInfrastructureCache.getEnvironments();
			for(ComputationalInfrastructure inf:list)
					for(ExecutionEnvironment exec : inf.getEnvironments())
						if(exec.getLogics().contains(LogicType.valueOf(logic+"")))
							toReturn.add(new ExecutionEnvironmentModel(inf.getGroupLabel(), inf.getSubmissionBackend(),
									exec.getName(), exec.getParameters().getMax_partitions(), exec.getParameters().getMin_partitions(), exec.getParameters().getDefault_partitions(),
									exec.getLogo(), exec.getDescription(), exec.getPolicies(), exec.getDisclaimer(),exec.getConf()));
			
			logger.debug("Found "+toReturn.size()+" objects");
			return toReturn;
		}catch(Exception e){
			logger.error("Unable to get Execution Environment list",e);
			throw new Exception("No execution environments available.");
		}
	}
	@Override
	public Response removeGeneration(String id, Boolean deleteTables, Boolean deleteJobs) throws Exception {
		Response response=new Response();
		try{						
			ASLSession session = Utils.getSession(this.getThreadLocalRequest().getSession());
			response.getAdditionalObjects().put(Tags.REMOVE_REPONSE_MESSAGE,dataManagement().build().removeRequest(id, deleteTables, deleteJobs));
			response.setStatus(true);
		}catch(Exception e){
			logger.error("Unable to remove generation ",e);
			response.setStatus(false);
			response.getAdditionalObjects().put(Tags.errorMessage, e.getMessage());
		}
		return response;
	}
	@Override
	public Response editGeneration(GroupGenerationRequest request)
			throws Exception {
		logger.trace("Received Group Generation Request "+request.getGenerationname());
		Response response= new Response(true);
		try{
			throw new Exception("NOT IMPLEMENTED");
		}catch(Exception e){
			logger.error("Unable to submit group",e);
			response.setStatus(false);
			response.getAdditionalObjects().put(Tags.errorMessage, e.getMessage());
		}
		return response;
	}
	@Override
	public Response editSource(ClientResource request) throws Exception {
		logger.trace("Editing resource "+request.getSearchId());
		Response response= new Response(true);
		try{			
			ASLSession session = Utils.getSession(this.getThreadLocalRequest().getSession());
			Resource toEdit=dataManagement().build().loadResource(request.getSearchId());
			toEdit.setTitle(request.getTitle());
			toEdit.setDescription(request.getDescription());
			toEdit.setDefaultSource(request.getIsDefault());
			toEdit.setDisclaimer(request.getDisclaimer());
			toEdit.setProvenance(request.getProvenance());
			dataManagement().build().updateResource(toEdit);
			response.getAdditionalObjects().put(Tags.responseEdit,"OK");
		}catch(Exception e){
			logger.error("Unable to edit Resource ",e);
			response.setStatus(false);
			response.getAdditionalObjects().put(Tags.errorMessage, e.getMessage());
		}
		return response;
	}
	@Override
	public Response dropTable(Integer sourceId) throws Exception {
		logger.trace("Received drop resource request ID : "+sourceId);
		Response response= new Response(true);
		try{			
			ASLSession session = Utils.getSession(this.getThreadLocalRequest().getSession());
			dataManagement().build().deleteResource(sourceId);
			response.getAdditionalObjects().put(Tags.REMOVE_REPONSE_MESSAGE,"OK");
		}catch(Exception e){
			logger.error("Unable to delete resource "+sourceId,e);
			response.setStatus(false);
			response.getAdditionalObjects().put(Tags.errorMessage, e.getMessage());
		}
		return response;
	}
	
	@Override
	public List<String> setLiveQuery(String query) throws Exception {
		List<String> fields=new ArrayList<String>();		
		try{
			ASLSession session = Utils.getSession(this.getThreadLocalRequest().getSession());
			CustomQueryDescriptorStubs desc=dataManagement().build().setCustomQuery(session.getUsername(), query);			
			if(desc.status().equals(ExportStatus.ERROR)) throw new Exception("Query returned Error : "+desc.errorMessage());	
			session.setAttribute(Tags.currentDirectQuery, desc);
			for(Field f:desc.fields().theList()){
				fields.add(f.name());
			}			
		}catch(Exception e){
			logger.error("Unable to setQuery ",e);
			throw new Exception("Unable to setQuery : "+e.getMessage());
		}
		return fields;
	}
	
	@Override
	public Response setImportRequestType(ClientResourceType type) throws Exception {
		logger.trace("Received import resource type : "+type);
		Response response= new Response(true);
		try{
			ASLSession session = Utils.getSession(this.getThreadLocalRequest().getSession());
			session.setAttribute(Tags.TO_IMPORT_TYPE, ResourceType.valueOf(type+""));						
		}catch(Exception e){
			logger.error("Unable to import resource type : "+type,e);
			response.setStatus(false);
			response.getAdditionalObjects().put(Tags.errorMessage, e.getMessage());
		}
		return response;
	}
	@Override
	public SessionStatistics getSessionStatistics() throws Exception {
		try{
			ASLSession session = Utils.getSession(this.getThreadLocalRequest().getSession());
			return new SessionStatistics(session.getScope()+"", session.getUsername());
		}catch(Exception e){
			logger.error("Unable to load session statistics ",e);
			throw new Exception("Unable to load current session statistics. Session might be expired.");
		}
	}
	@Override
	public Response analyzeResources(List<ClientAnalysisType> types,String title, String description,Integer[] ids) throws Exception {
		Response response= new Response(true);
		try{
			ASLSession session = Utils.getSession(this.getThreadLocalRequest().getSession());
			Analysis toSend=new Analysis();
			toSend.setAuthor(session.getUsername());
			toSend.setDescription(description);
			toSend.setTitle(title);
			toSend.setSources(Arrays.asList(ids));
			ArrayList<AnalysisType> serverTypes=new ArrayList<AnalysisType>();
			for(ClientAnalysisType type:types) 
				serverTypes.add(AnalysisType.valueOf(type+""));
			toSend.setType(serverTypes);
			toSend.setSubmissiontime(System.currentTimeMillis());
			String id=dataManagement().build().analyzeTables(toSend);
			response.getAdditionalObjects().put(Tags.responseAnalysisSubmission, id);
		}catch(Exception e){
			logger.error("Unable to submit analysis",e);
			response.setStatus(false);
			response.getAdditionalObjects().put(Tags.errorMessage, e.getMessage());
		}
		return response;
	}

	
	
	
	
	
	@Override
	public Response resubmitRequest(String id) throws Exception {
		logger.trace("Resubmitting request : "+id);
		Response response= new Response(true);
		try{			
			ASLSession session = Utils.getSession(this.getThreadLocalRequest().getSession());
			String newerId=dataManagement().build().resubmitGeneration(id);		
			response.getAdditionalObjects().put(Tags.responseGroupGeneration,newerId);
		}catch(Exception e){
			logger.error("Unable to submit group",e);
			response.setStatus(false);
			response.getAdditionalObjects().put(Tags.errorMessage, e.getMessage());
		}
		return response;
	}
	
	@Override
	public SaveOperationProgress getSaveProgress() throws Exception {
		return SaveManager.getProgress(Utils.getSession(this.getThreadLocalRequest().getSession()));
	}
	
	@Override
	public SaveOperationProgress saveOperationRequest(SaveRequest request)
			throws Exception {
		return SaveManager.startSaving(request,Utils.getSession(this.getThreadLocalRequest().getSession()));
	}
	
	@Override
	public Response removeAnalysis(String analisysId) throws Exception {
		Response response=new Response();
		try{		
			ASLSession session = Utils.getSession(this.getThreadLocalRequest().getSession());
			dataManagement().build().deleteAnalysis(analisysId);
			response.getAdditionalObjects().put(Tags.REMOVE_REPONSE_MESSAGE,"Successfully removed");
			response.setStatus(true);
		}catch(Exception e){
			logger.error("Unable to remove analysis ",e);
			response.setStatus(false);
			response.getAdditionalObjects().put(Tags.errorMessage, e.getMessage());
		}
		return response;
	}
	
}
