package org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps;

import java.io.File;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.BatchGeneratorI;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.EnvironmentalLogicManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.AquaMapsObjectExecutionRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig.ConfigurationManager;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Area;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Perturbation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.EnvelopeFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HSPECFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ObjectType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobWorker extends Thread{

	final static Logger logger= LoggerFactory.getLogger(JobWorker.class);



	private Job current;
	private Submitted tableReference;

	public JobWorker(Job toExecute,Submitted submittedReference) {
		current=toExecute;
		tableReference=submittedReference;
	}

	@Override
	public void run() {
		logger.trace("Starting execution for job : "+tableReference.getSearchId());
		try{		
			SubmittedManager.setStartTime(tableReference.getSearchId());

			current.setId(tableReference.getSearchId());


			current=JobManager.insertNewJob(current);

			if(current.getStatus().equals(SubmittedStatus.Completed)){
				logger.debug("No need to generate for job "+tableReference.getSearchId()+", publisher returned Complete.");
			}else{
				generateHSPEC(current);
				//				 blockedJobs.put(key, value)
				logger.debug("Generating requests for related objects..");
				ArrayList<AquaMapsObjectExecutionRequest> toSubmitRequests=new ArrayList<AquaMapsObjectExecutionRequest>();

				for(AquaMapsObject object : current.getAquaMapsObjectList()){
					if(!object.getStatus().equals(SubmittedStatus.Completed))
						if(object.getType().equals(ObjectType.Biodiversity)){

							//************** BIODIVERSITY ***************************** 

							Map<String,Map<String,Perturbation>> envelopeCustomization = new HashMap<String, Map<String,Perturbation>>();
							for(String id:current.getEnvelopeCustomization().keySet()){
								Species spec=new Species(id);
								if(object.getSelectedSpecies().contains(spec)) envelopeCustomization.put(id, current.getEnvelopeCustomization().get(id));
							}

							Map<String,Map<EnvelopeFields,Field>> envelopeWeights=new HashMap<String, Map<EnvelopeFields,Field>>();
							for(String id:current.getEnvelopeWeights().keySet()){
								Species spec=new Species(id);
								if(object.getSelectedSpecies().contains(spec)) envelopeWeights.put(id, current.getEnvelopeWeights().get(id));
							}

							toSubmitRequests.add(new BiodiversityObjectExecutionRequest(SubmittedManager.getSubmittedById(object.getId()), 
									current.getSelectedAreas(), object.getBoundingBox(), object.getThreshold(), 
									object.getSelectedSpecies(), envelopeCustomization, envelopeWeights));
						}
						else {
							Species selected=object.getSelectedSpecies().iterator().next();
							Map<String,Perturbation> envelopeCustomization =
								(current.getEnvelopeCustomization().containsKey(selected.getId()))? current.getEnvelopeCustomization().get(selected.getId()):new HashMap<String,Perturbation>();

								Map<EnvelopeFields,Field> envelopeWeights=
									(current.getEnvelopeWeights().containsKey(selected.getId()))? current.getEnvelopeWeights().get(selected.getId()):new HashMap<EnvelopeFields,Field>();

									toSubmitRequests.add(new DistributionObjectExecutionRequest(SubmittedManager.getSubmittedById(object.getId()), 
											current.getSelectedAreas(), object.getBoundingBox(), object.getSelectedSpecies(), envelopeCustomization	, envelopeWeights,object.getAlgorithmType()));
						}
				}

				SubmittedManager.updateStatus(tableReference.getSearchId(), SubmittedStatus.Generating);
				logger.debug("Job "+tableReference.getSearchId()+" must wait for "+toSubmitRequests.size()+" object to complete..");
				if(toSubmitRequests.size()>0)
					JobExecutionManager.insertAquaMapsObjectExecutionRequest(toSubmitRequests);
				//				while(!JobManager.isJobComplete(tableReference.getSearchId())){
				//					logger.trace("Job [ID : "+tableReference.getSearchId()+"] was not finished, forcing generation for skipped objects..");
				//					toSubmitRequests=new ArrayList<AquaMapsObjectExecutionRequest>();
				//					for(Submitted submitted:JobManager.getObjects(tableReference.getSearchId())){
				//						if(!submitted.getStatus().equals(SubmittedStatus.Error)&&!submitted.getStatus().equals(SubmittedStatus.Completed)){
				//							logger.trace("Found object "+submitted.getSearchId()+" with status "+submitted.getStatus()+" to resubmit..");
				//							toSubmitRequests.add((AquaMapsObjectExecutionRequest) AquaMapsXStream.deSerialize(submitted.getSerializedRequest()));
				//						}
				//					}
				//					logger.debug("Job "+tableReference.getSearchId()+" must wait for "+toSubmitRequests.size()+" object to complete..");
				//					if(toSubmitRequests.size()>0)
				//						JobExecutionManager.insertAquaMapsObjectExecutionRequest(toSubmitRequests);
				//					else throw new Exception("No object to resubmit for Job [ID : "+tableReference.getSearchId()+"]");
				//				}



				//				if(tableReference.getGisEnabled()){
				//					String wmsID=ServiceContext.getContext().getPublisher().store(WMSContext.class,  new Generator<WMSContext>(new WMSGenerationRequest(tableReference.getSearchId())){
				//						@Override
				//						public WMSContext generate() throws Exception {
				//							return generateWMSContext(((WMSGenerationRequest)request).getJobId());
				//						}
				//					}, new StoreConfiguration(StoreMode.USE_EXISTING, 
				//							new UpdateConfiguration(true, true, true))).getStoredId().getId();
				//					SubmittedManager.setGisPublishedId(tableReference.getSearchId(), wmsID);
				//				}
				SubmittedManager.updateStatus(tableReference.getSearchId(), SubmittedStatus.Completed);

			}


			//check if data available (store Job into publisher and check status)
			//Y update job references and insert obj references

			//N prepare HSPEC
			// per ogni Object call ObjectRequestManager.submitRequest
			// wait for completion semaphore


			// update job references and published JOB 

		}catch(Exception e){
			logger.error("Failed Job execution "+tableReference.getSearchId(),e);
			try {
				SubmittedManager.updateStatus(tableReference.getSearchId(), SubmittedStatus.Error);

			} catch (Exception e1) {
				logger.error("Unexpected Error ",e1);
			}
		}
		finally{
			JobExecutionManager.cleanReferences(tableReference);
		}
	}



	private static void generateHSPEC(Job toExecute)throws Exception{
		Map<String,Map<EnvelopeFields,Field>> weights=toExecute.getEnvelopeWeights();
		Map<String,Map<String,Perturbation>> envelopePert=toExecute.getEnvelopeCustomization();
		int jobId=toExecute.getId();
		Set<Area> area=toExecute.getSelectedAreas();




		boolean needToGenerate=((weights.size()>0)||(envelopePert.size()>0));

		boolean filteredArea=(area.size()>0);

		if(filteredArea&&needToGenerate){
			logger.debug(" jobId "+jobId+" : Filter By Area and Re-generate");
			String filteredHcaf=filterByArea(jobId, area, ResourceType.HCAF, JobManager.getHCAFTableId(jobId));
			JobManager.setWorkingHCAF(jobId,filteredHcaf);
			String generatedHSPEC=generateHSPEC(jobId,toExecute.getSelectedSpecies(),toExecute.getSourceHSPEN().getTableName(),toExecute.getSourceHSPEC().getAlgorithm(),weights);
			String toUseHSPEC=filterByArea(jobId,area,ResourceType.HSPEC,generatedHSPEC);
			JobManager.setWorkingHSPEC(jobId,toUseHSPEC);	
			JobManager.addToDropTableList(jobId, generatedHSPEC);
			JobManager.addToDropTableList(jobId, toUseHSPEC);
		}else if (filteredArea){
			logger.debug(" jobId "+jobId+" : Filter By Area");
			String table=filterByArea(jobId, area, ResourceType.HSPEC, SubmittedManager.getHSPECTableId(jobId));
			JobManager.setWorkingHSPEC(jobId,table);
			JobManager.addToDropTableList(jobId, table);
		}else if (needToGenerate){				
			logger.debug(" jobId "+jobId+" : Re-generate");
			String generatedHSPEC=generateHSPEC(jobId,toExecute.getSelectedSpecies(),toExecute.getSourceHSPEN().getTableName(),toExecute.getSourceHSPEC().getAlgorithm(),weights);
			JobManager.setWorkingHSPEC(jobId,generatedHSPEC);
			JobManager.addToDropTableList(jobId, generatedHSPEC);
		}else{
			logger.debug(" jobId "+jobId+" no needs");
			JobManager.setWorkingHSPEC(jobId, SourceManager.getSourceName(JobManager.getHSPECTableId(jobId)));
		}

	}



	private static String filterByArea(int jobId,Set<Area> areaSelection,ResourceType tableType,int sourceId)throws Exception{

		return filterByArea(jobId,areaSelection,tableType,SourceManager.getSourceName(sourceId));

	}
	@Deprecated
	private static String filterByArea(int jobId,Set<Area> areaSelection,ResourceType tableType,String sourceTable)throws Exception{
		logger.trace(" filtering on area selection for jobId:"+jobId);

		DBSession conn =null;
		try{

			conn = DBSession.getInternalDBSession();
			String filteredTable=ServiceUtils.generateId(tableType.toString().toLowerCase(), "");
			String sourceTableName=sourceTable;
			conn.createLikeTable(filteredTable, sourceTableName);

			JobManager.addToDropTableList(jobId, filteredTable);


			PreparedStatement psFAO=conn.getFilterCellByAreaQuery(HSPECFields.faoaream,sourceTableName, filteredTable);
			PreparedStatement psLME=conn.getFilterCellByAreaQuery(HSPECFields.lme,sourceTableName, filteredTable);
			PreparedStatement psEEZ=conn.getFilterCellByAreaQuery(HSPECFields.eezall,sourceTableName, filteredTable);


			for(Area area: areaSelection){
				switch(area.getType()){
				case LME: 	{psLME.setInt(1, Integer.parseInt(area.getCode()));
				psLME.executeUpdate();
				break;
				}
				case FAO:   {psFAO.setInt(1, Integer.parseInt(area.getCode()));
				psFAO.executeUpdate();
				break;
				}
				case EEZ:	{psEEZ.setInt(1, Integer.parseInt(area.getCode()));
				psEEZ.executeUpdate();
				break;
				}
				default: logger.warn(" Invalid area type , skipped selection : code = "+area.getCode()+"; type = "+area.getType()+"; name = "+area.getName());
				}
			}

			return filteredTable;
		}catch (Exception e){
			throw e;
		}finally{
			if((conn!=null)&&(!conn.getConnection().isClosed())){
				conn.close();
			}
		}
	}

	/**
	 * 
	 * @param jobId
	 * @param weights
	 * @param makeTemp
	 * @return
	 * @throws Exception
	 */
	private static String generateHSPEC(int jobId, Set<Species> selection,String sourceHspen,AlgorithmType algorithm, Map<String,Map<EnvelopeFields,Field>> weights)throws Exception{

		String filteredHSPEN=SpeciesManager.getFilteredHSPEN(JobManager.getWorkingHSPEN(jobId), selection);
		JobManager.setWorkingHSPEN(jobId, filteredHSPEN);
		JobManager.addToDropTableList(jobId, filteredHSPEN);
		BatchGeneratorI generator=null;
		try{
			logger.trace("Requesting batch generator for custom HSPEC");
			generator=EnvironmentalLogicManager.getBatch(ServiceContext.getContext().getName());			
			generator.setConfiguration(ServiceContext.getContext().getEcoligicalConfigDir()+File.separator, ConfigurationManager.getVODescriptor().getInternalDB());
			return generator.generateHSPECTable(JobManager.getWorkingHCAF(jobId),
					JobManager.getWorkingHSPEN(jobId), "maxminlat_"+sourceHspen, algorithm,false, "");
		}catch(Exception e){
			throw e;
		}finally{
			if(generator!=null) EnvironmentalLogicManager.leaveBatch(generator);
		}
	}


}
