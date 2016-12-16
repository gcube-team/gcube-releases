package org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationRequestsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.BatchGeneratorObjectFactory;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SourceGenerationPhase;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SourceGenerationRequestFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentalStatusUpdateThread extends Thread {


	final static Logger logger= LoggerFactory.getLogger(EnvironmentalStatusUpdateThread.class);


	private long millis;


	public EnvironmentalStatusUpdateThread(long millis) {
		super("Environmental status updater");
		this.millis=millis;
	}

	@Override
	public void run() {
		while(true){
			try{

				//Updating data generation percent...
				ArrayList<Field> filter=new ArrayList<Field>();
				filter.add(new Field(SourceGenerationRequestFields.phase+"",SourceGenerationPhase.datageneration+"",FieldType.STRING));
				for(SourceGenerationRequest request : SourceGenerationRequestsManager.getList(filter)){
					try{
						int completedSteps=request.getGeneratedSources().size()/(request.getToGenerateTableCount()/request.getEvaluatedComputationCount());
						Double percent=((double)completedSteps/request.getEvaluatedComputationCount())*100;
						StringBuilder logBuilder=new StringBuilder("completed steps = "+completedSteps);
						logBuilder.append("computation count = "+request.getEvaluatedComputationCount());
						for(Integer reportId:request.getReportID()){
							Double reportStatus=BatchGeneratorObjectFactory.getReport(reportId,false).getPercent();
							percent+=reportStatus/request.getEvaluatedComputationCount();
							logBuilder.append("reportId "+reportId+" status "+reportStatus);
						}
						logger.trace("Updateing reference "+request.getId()+", percent "+percent+", formula details "+logBuilder);
						SourceGenerationRequestsManager.setPhasePercent(percent, request.getId());
					}catch(Exception e){
						logger.warn("Skipping percent update for execution id "+request.getId()+", report id was "+request.getReportID(),e);
						logger.debug("Request data were : "
								+"Generated sources size : "+request.getGeneratedSources().size()
								+"To generate count : "+request.getToGenerateTableCount()
								+"Computation count : "+request.getEvaluatedComputationCount());
						}
				}
				//Updating map generation percent

					filter=new ArrayList<Field>();
					filter.add(new Field(SourceGenerationRequestFields.phase+"",SourceGenerationPhase.mapgeneration+"",FieldType.STRING));
					for(SourceGenerationRequest request : SourceGenerationRequestsManager.getList(filter)){
						try{
							//checking jobIds
							boolean completed=true;
							SourceGenerationPhase toSetCompletedPhase=SourceGenerationPhase.completed;
							for(Integer id:request.getJobIds()){
								Submitted job=SubmittedManager.getSubmittedById(id);
								if(!job.getStatus().equals(SubmittedStatus.Completed)&&!job.getStatus().equals(SubmittedStatus.Error)) completed=false;
								if(job.getStatus().equals(SubmittedStatus.Error)) toSetCompletedPhase=SourceGenerationPhase.error;
							}
							if(completed){
								logger.info("All jobs completed for source generation "+request.getId()+", to set phase : "+toSetCompletedPhase);
								SourceGenerationRequestsManager.setPhase(toSetCompletedPhase,request.getId());
							}else{
								//percent calculation		[SUM(completedObj/totalObjCount/NJob)]*100d						
								double sumPartials=0;
								ArrayList<Field> completedFilter=null;
								ArrayList<Field> toCompleteFilter=null;
								StringBuilder logBuilder=new StringBuilder();
								for(Integer id:request.getJobIds()){
									completedFilter=new ArrayList<Field>();
									completedFilter.add(new Field(SubmittedFields.jobid+"",id+"",FieldType.INTEGER));
									completedFilter.add(new Field(SubmittedFields.status+"",SubmittedStatus.Completed+"",FieldType.STRING));									
									toCompleteFilter=new ArrayList<Field>();
									toCompleteFilter.add(new Field(SubmittedFields.jobid+"",id+"",FieldType.INTEGER));
									long totalObjectCount=SubmittedManager.getCount(toCompleteFilter);
									if(totalObjectCount>0){
										long completedObjects = SubmittedManager.getCount(completedFilter);
										double partial=((double)completedObjects/totalObjectCount/request.getJobIds().size());
										logBuilder.append("Job ["+id+"] partial progress "+partial+" ("+completedObjects+"/"+totalObjectCount+")");
										sumPartials+=partial;									
									}
								}
								Double percent=100d*sumPartials;
								logger.debug("Progress for "+request.getId()+" : "+percent+" forumla details : "+logBuilder);
								SourceGenerationRequestsManager.setPhasePercent(percent, request.getId());
							}
						}catch(Exception e){logger.warn("Skipping percent update for execution id "+request.getId(),e);}
					}
			}catch(Exception e){
				logger.error("Unexpected exception ",e);
			}finally{
				try{					
					Thread.sleep(millis);
				}catch(InterruptedException e){
					//Woken up
				}
			}
		}
	}
}
