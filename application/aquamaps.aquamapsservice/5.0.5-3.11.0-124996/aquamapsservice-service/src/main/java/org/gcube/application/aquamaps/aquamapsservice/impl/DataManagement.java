package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.AnalysisTableManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.CustomQueryManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.ExportManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationRequestsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis.AnalysisManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.BatchGeneratorObjectFactory;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables.TableGenerationExecutionManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.stubs.CustomQueryDescriptorStubs;
import org.gcube.application.aquamaps.aquamapsservice.stubs.DataManagementPortType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ExportTableRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ExportTableStatusType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GenerateMapsRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetGenerationLiveReportResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.HspecGroupGenerationRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ImportResourceRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.RemoveHSPECGroupGenerationRequestResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.SetUserCustomQueryRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ViewCustomQueryRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ViewTableRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Analysis;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.LogicType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SourceGenerationPhase;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SourceGenerationRequestFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.utils.Storage;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.types.StringArray;
import org.gcube.common.core.types.VOID;
import org.gcube.dataanalysis.ecoengine.utils.ResourceFactory;
import org.gcube_system.namespaces.application.aquamaps.types.FieldArray;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;
import org.gcube_system.namespaces.application.aquamaps.types.PagedRequestSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataManagement extends GCUBEPortType implements DataManagementPortType{

	private static Logger logger = LoggerFactory.getLogger(DataManagement.class);
	
	@Override
	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

	@Override
	public org.gcube_system.namespaces.application.aquamaps.types.Resource getResourceInfo(org.gcube_system.namespaces.application.aquamaps.types.Resource myResource) throws GCUBEFault{
		Resource toReturn=PortTypeTranslations.fromStubs(myResource);		
		
		try{
		
		return PortTypeTranslations.toStubs(SourceManager.getById(toReturn.getSearchId()));
		}catch(Exception e){
			logger.error("Unable to load source details. id: "+myResource.getSearchId(), e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	@Override
	public int generateMaps(GenerateMapsRequestType arg0) throws RemoteException,GCUBEFault{

		try{
			return CommonServiceLogic.generateMaps_Logic(arg0.getHSPECId(),PortTypeTranslations.fromStubs(arg0.getSpeciesFilter()),arg0.getAuthor(),arg0.isGenerateLayers(),arg0.isForceRegeneration());
		}catch(Exception e){
			logger.error("Unable to execute request ", e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	@Override
	public String generateHSPECGroup(HspecGroupGenerationRequestType arg0)
	throws RemoteException, GCUBEFault {
		try{//Inserting request into db
			long availableSpace=GHNContext.getContext().getFreeSpace(GHNContext.getContext().getLocation());
			long threshold=ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.MONITOR_FREESPACE_THRESHOLD);
			if(availableSpace<threshold)throw new Exception("NOT ENOUGH SPACE, REMAINING : "+availableSpace+", THRESHOLD : "+threshold);

			logger.trace("Received hspec group generation request, title : "+arg0.getGenerationName());
			
			SourceGenerationRequest request=PortTypeTranslations.fromStubs(arg0);
			
			for(Integer resId:request.getHcafIds()){
				org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource r=SourceManager.getById(resId);
				if(r==null||!r.getType().equals(ResourceType.HCAF))throw new Exception("Invalid HCAF id "+resId);
			}
			for(Integer resId:request.getHspenIds()){
				org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource r=SourceManager.getById(resId);
				if(r==null||!r.getType().equals(ResourceType.HSPEN))throw new Exception("Invalid HSPEN id "+resId);
			}
			for(Integer resId:request.getOccurrenceCellIds()){
				org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource r=SourceManager.getById(resId);
				if(r==null||!r.getType().equals(ResourceType.OCCURRENCECELLS))throw new Exception("Invalid Occurrence Cells id "+resId);
			}			
			
			return TableGenerationExecutionManager.insertRequest(request);
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


//	@Override
//	public String getJSONSubmittedHSPECGroup(
//			PagedRequestSettings arg0) throws RemoteException,
//			GCUBEFault {
//		try{
//			
//			return SourceGenerationRequestsManager.getJSONList(new ArrayList<Field>(), arg0);
//
//		}catch(Exception e){
//			logger.error("Unable to execute request ",e);
//			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
//		}
//	}

	@Override
	public GetGenerationLiveReportResponseType getGenerationLiveReportGroup(
			StringArray ids) throws RemoteException, GCUBEFault {
		try{			
			if(ids==null||ids.getItems()==null||ids.getItems().length==0) throw new Exception ("INVALID REQUEST : No Report ID specified");
			List<String> mapLoad=new ArrayList<String>();
			List<String> resourceMap=new ArrayList<String>();
			Double percent=0d;
			for(String idString:ids.getItems()){
				try{
					Integer id=Integer.parseInt(idString);
					EnvironmentalExecutionReportItem report=BatchGeneratorObjectFactory.getReport(id,true);
					if(report==null) throw new Exception("Execution finished or not yet started");
					mapLoad.add(report.getResourceLoad());
					resourceMap.add(report.getResourcesMap());
					percent+=report.getPercent()/ids.getItems().length;
				}catch(Exception e){
					logger.warn("Unable to get report, id "+idString, e);
				}
			}
			
			
			
			
			//FIXME elaborated species not reported
			return new GetGenerationLiveReportResponseType(
					null, percent, ResourceFactory.getOverallResourceLoad(mapLoad), ResourceFactory.getOverallResources(resourceMap));
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


	@Override
	public RemoveHSPECGroupGenerationRequestResponseType removeHSPECGroup(
			RemoveHSPECGroupGenerationRequestResponseType arg0)
	throws RemoteException, GCUBEFault {
		try{
			ArrayList<Field> filter=new ArrayList<Field>();
			filter.add(new Field(SourceGenerationRequestFields.id+"",arg0.getRequestId(),FieldType.STRING));
			SourceGenerationRequest request= SourceGenerationRequestsManager.getList(filter).get(0);
			//TODO complete method
			if(arg0.isRemoveTables()) throw new Exception("REMOVE TABLES NOT YET IMPLEMENTED");
			if(arg0.isRemoveJobs()) throw new Exception("REMOVE JOBS NOT YET IMPLEMENTED");
			SourceGenerationRequestsManager.delete(filter);
			return new RemoveHSPECGroupGenerationRequestResponseType(false,false,request.getId());
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	@Override
	public FieldArray getDefaultSources(VOID arg0) throws RemoteException,
	GCUBEFault {
		try{
			ArrayList<Field> toReturn=new ArrayList<Field>();
			for(ResourceType type:ResourceType.values())
				try{
					toReturn.add(new Field(type+"",SourceManager.getDefaultId(type)+"",FieldType.INTEGER));
				}catch(Exception e){
					logger.warn("Unable to locate default table for "+type,e);
				}
				return PortTypeTranslations.toFieldArray(toReturn);
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	@Override
	public org.gcube_system.namespaces.application.aquamaps.types.Resource editResource(org.gcube_system.namespaces.application.aquamaps.types.Resource arg0) throws RemoteException,
	GCUBEFault {
		try{
			logger.trace("Editing resource "+arg0.getSearchId());
			Resource request=PortTypeTranslations.fromStubs(arg0);
			Resource toEdit=SourceManager.getById(request.getSearchId());
			toEdit.setTitle(request.getTitle());
			toEdit.setDescription(request.getDescription());
			toEdit.setDefaultSource(request.getDefaultSource());
			toEdit.setDisclaimer(request.getDisclaimer());
			toEdit.setProvenance(request.getProvenance());
			if(toEdit.getType().equals(ResourceType.HSPEC)&&request.getSourceHSPENIds().size()>0){
				
				toEdit.setSourceHSPENIds(request.getSourceHSPENIds());
				toEdit.setSourceHSPENTables(request.getSourceHSPENTables());
			}
			if((toEdit.getType().equals(ResourceType.HSPEC)||toEdit.getType().equals(ResourceType.HSPEN))&&request.getSourceHSPENIds().size()>0){
				toEdit.setSourceHCAFIds(request.getSourceHCAFIds());
				toEdit.setSourceHCAFTables(request.getSourceHCAFTables());
			}
				
			SourceManager.update(toEdit);
			return arg0;
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}
	@Override
	public VOID editHSPECGroupDetails(HspecGroupGenerationRequestType arg0)
			throws RemoteException, GCUBEFault {
		try{
			//TODO implment
			throw new Exception("NOT YET IMPLEMENTED");
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


	
	@Override
	public String exportTableAsCSV(ExportTableRequestType request)
			throws RemoteException, GCUBEFault {
		try{
			logger.debug("Received export request, settings [Del : "+request.getCsvSettings().getDelimiter()
					+", ENC : "+request.getCsvSettings().getEncoding()
					+", Mask : "+request.getCsvSettings().getFieldsMask());
			return ExportManager.submitExportOperation(
					request.getTableName(), request.getUser(), request.getBasketId(),request.getToSaveName(),request.getOperationType(),request.getCsvSettings());
		}catch(Exception e){
			logger.debug("Unable to export table "+request.getTableName(),e);
			throw new GCUBEFault("Unable to export table "+request.getTableName()+", cause : "+e.getLocalizedMessage());
		}
	}
	
	@Override
	public VOID removeResource(int arg0) throws RemoteException, GCUBEFault {
		try{
			logger.trace("Removing resource "+arg0);
			org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource resource=SourceManager.getById(arg0);
			if(resource!=null){
				List<Submitted> relatedJobs=null;
				List<SourceGenerationRequest> relatedGenerations=null;
				if(!resource.getType().equals(ResourceType.OCCURRENCECELLS)){
					logger.trace("Resource type is "+resource.getType()+", gathering related jobs...");
					List<Field> jobFilter=new ArrayList<Field>();

					if(resource.getType().equals(ResourceType.HCAF))
						jobFilter.add(new Field(SubmittedFields.sourcehcaf+"",resource.getSearchId()+"",FieldType.INTEGER));
					else if(resource.getType().equals(ResourceType.HSPEN))
						jobFilter.add(new Field(SubmittedFields.sourcehspen+"",resource.getSearchId()+"",FieldType.INTEGER));
					else if(resource.getType().equals(ResourceType.HSPEC))
						jobFilter.add(new Field(SubmittedFields.sourcehspec+"",resource.getSearchId()+"",FieldType.INTEGER));
					
					jobFilter.add(new Field(SubmittedFields.isaquamap+"",false+"",FieldType.BOOLEAN));
					
					relatedJobs=SubmittedManager.getList(jobFilter);
					
					logger.trace("Found "+relatedJobs.size()+" related jobs..");
					logger.trace("Checking jobs status..");
					for(Submitted j:relatedJobs){
						if(!j.getStatus().equals(SubmittedStatus.Completed)&&!j.getStatus().equals(SubmittedStatus.Error)) 
							throw new Exception("Found pending related jobs [ID : "+j.getSearchId()+"], unable to continue..");
					}
					logger.trace("OK");
				}
				
				if(!resource.getType().equals(ResourceType.HSPEC)){
					logger.trace("Checking for pending table generation..");
					
					ArrayList<Field> generationFilter=new ArrayList<Field>();
					
					SourceGenerationRequest sourceReq=new SourceGenerationRequest();
					sourceReq.addSource(resource);
					
					if(resource.getType().equals(ResourceType.HCAF))
						generationFilter.add(sourceReq.getField(SourceGenerationRequestFields.sourcehcafids));
					else if(resource.getType().equals(ResourceType.HSPEN)){
						generationFilter.add(sourceReq.getField(SourceGenerationRequestFields.sourcehspenids));
					}
					else if(resource.getType().equals(ResourceType.OCCURRENCECELLS)){
						sourceReq.setLogic(LogicType.HSPEN);
						generationFilter.add(sourceReq.getField(SourceGenerationRequestFields.logic));
						generationFilter.add(sourceReq.getField(SourceGenerationRequestFields.sourceoccurrencecellsids));
					}
					relatedGenerations=SourceGenerationRequestsManager.getList(generationFilter);

					logger.trace("Found "+relatedGenerations.size()+" related generations..");
					logger.trace("Checking status..");
					for(SourceGenerationRequest r:relatedGenerations){
						if(!r.getPhase().equals(SourceGenerationPhase.completed)&&!r.getPhase().equals(SourceGenerationPhase.error)) 
							throw new Exception("Found pending related requests [ID : "+r.getId()+"], unable to continue..");
					}
					logger.trace("OK");
				}
								
				logger.trace("Checks completed");
				
				logger.trace("Unregistering..");
				
				SourceManager.deleteSource(arg0,true);
				
				
				
				logger.trace("Removing Jobs.. ");
				if(relatedJobs!=null){
					for(Submitted toDelete:relatedJobs)
						try{
							SubmittedManager.delete(toDelete.getSearchId());
						}catch(Exception e){
							logger.warn("Unable to delete related job "+toDelete.getSearchId(),e);
						}
				}
				logger.trace("Removing generation Requests .. ");
				if(relatedGenerations!=null){
					for(SourceGenerationRequest toDelete:relatedGenerations)
						try{
							SourceGenerationRequestsManager.delete(toDelete.getId());
						}catch(Exception e){
							logger.warn("Unable to delete related generation request "+toDelete.getId(),e);
						}
				}
				logger.trace("Complete");
			}else throw new Exception("Resource not found, ID was "+arg0);
			return new VOID();
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
		
	}
	

	
	
	@Override
	public String setCustomQuery(SetUserCustomQueryRequestType arg0)
			throws RemoteException, GCUBEFault {
		try{
			logger.trace("Setting custom query, user : "+arg0.getUser()+", query : "+arg0.getQueryString());
			return CustomQueryManager.setUserCustomQuery(arg0.getUser(), arg0.getQueryString());
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}
	
	
	
	@Override
	public int importResource(ImportResourceRequestType arg0)
			throws RemoteException, GCUBEFault {
		try{
			logger.trace("Importing resource , user : "+arg0.getUser()+", locator :"+arg0.getRsLocator());
			
			return SourceManager.importFromCSVFile(arg0);
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}
	
	@Override
	public String viewCustomQuery(ViewCustomQueryRequestType arg0)
			throws RemoteException, GCUBEFault {
		try{			
			
			return CustomQueryManager.getPagedResult(arg0.getUser(),arg0.getPagedRequestSettings());
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}
	
	@Override
	public String analyzeTables(org.gcube_system.namespaces.application.aquamaps.types.Analysis arg0) throws RemoteException,
			GCUBEFault {
		try{
			return AnalysisManager.insertRequest(PortTypeTranslations.fromStubs(arg0));
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

//	@Override
//	public String getJSONSubmittedAnalysis(
//			PagedRequestSettings arg0) throws RemoteException,
//			GCUBEFault {
//		try{
//			return AnalysisTableManager.getJSONList(new ArrayList<Field>(), arg0);
//		}catch(Exception e){
//			logger.error("Unable to execute request ",e);
//			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
//		}
//	}

	@Override
	public String loadAnalysis(String arg0) throws RemoteException, GCUBEFault {
		try{
			Analysis analysis=AnalysisTableManager.getById(arg0);
			GCUBEScope scope=ServiceContext.getContext().getScope();
			logger.trace("Caller scope is "+scope);
			String id=Storage.storeFile(analysis.getArchiveLocation(), false);
			
			
			
			logger.trace("Storage id: "+id);
			return id;
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	@Override
	public String resubmitGeneration(String arg0) throws RemoteException,
			GCUBEFault {
		try{//Inserting request into db
			long availableSpace=GHNContext.getContext().getFreeSpace(GHNContext.getContext().getLocation());
			long threshold=ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.MONITOR_FREESPACE_THRESHOLD);
			if(availableSpace<threshold)throw new Exception("NOT ENOUGH SPACE, REMAINING : "+availableSpace+", THRESHOLD : "+threshold);

			
			logger.debug("Resubmitting request "+arg0);

			SourceGenerationRequest request=new SourceGenerationRequest();
			request.setId(arg0);
			request=SourceGenerationRequestsManager.getList(
					Arrays.asList(new Field[]{request.getField(SourceGenerationRequestFields.id)}),
					new PagedRequestSettings(1, 0, OrderDirection.ASC, SourceGenerationRequestFields.id+"")).get(0);
			
			for(Integer resId:request.getHcafIds()){
				org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource r=SourceManager.getById(resId);
				if(r==null||!r.getType().equals(ResourceType.HCAF))throw new Exception("Invalid HCAF id "+resId);
			}
			for(Integer resId:request.getHspenIds()){
				org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource r=SourceManager.getById(resId);
				if(r==null||!r.getType().equals(ResourceType.HSPEN))throw new Exception("Invalid HSPEN id "+resId);
			}
			for(Integer resId:request.getOccurrenceCellIds()){
				org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource r=SourceManager.getById(resId);
				if(r==null||!r.getType().equals(ResourceType.OCCURRENCECELLS))throw new Exception("Invalid Occurrence Cells id "+resId);
			}			
			request.setGenerationname(request.getGenerationname()+"_reLaunch");
			return TableGenerationExecutionManager.insertRequest(request);
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}
	
	@Override
	public VOID deleteAnalysis(String arg0) throws RemoteException, GCUBEFault {
		try{
			AnalysisTableManager.delete(arg0);
			return new VOID();
		}catch(Exception e){
			logger.error("Unable to delete Analysis "+arg0,e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}
	
	@Override
	public CustomQueryDescriptorStubs getCustomQueryDescriptor(String request)
			throws RemoteException, GCUBEFault {
		try{
			return PortTypeTranslations.toStubs(CustomQueryManager.getDescriptor(request));
		}catch(Exception e){
			logger.error("Unable to get descriptor for custom query "+request,e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}
	
	@Override
	public ExportTableStatusType getExportStatus(String request)
			throws RemoteException, GCUBEFault {
		try{
			return ExportManager.getStatus(request);
		}catch(Exception e){
			logger.error("Unable to get status for export operation "+request,e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}
	
	@Override 
	public String viewTable(ViewTableRequestType request)throws RemoteException, GCUBEFault{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			ArrayList<Field> filter=PortTypeTranslations.fromStubs(request.getFilter());
			PagedRequestSettings settings=request.getSettings();
			return DBUtils.toJSon(session.executeFilteredQuery(filter, request.getTablename(), settings.getOrderField(), settings.getOrderDirection()),settings.getOffset(),settings.getOffset()+settings.getLimit());
		}catch(Exception e){
			logger.error("view Table "+request.getTablename(),e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}
}
