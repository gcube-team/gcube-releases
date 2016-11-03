package org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.gcube.application.aquamaps.aquamapsservice.impl.CommonServiceLogic;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationRequestsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.BatchGeneratorI;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.EnvironmentalLogicManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.TableGenerationConfiguration;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig.ConfigurationManager;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.LogicType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SourceGenerationPhase;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.MetaSourceFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SourceGenerationRequestFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HSPECGroupWorker extends Thread {

	private SourceGenerationRequest request;
	final static Logger logger= LoggerFactory.getLogger(HSPECGroupWorker.class);

	final Semaphore blocking=new Semaphore(0);
	final List<String> exceptions=new ArrayList<String>();
	public HSPECGroupWorker(SourceGenerationRequest request) {
		this.request=request;
	}

	@Override
	public void run() {

		try{
			logger.trace("Starting execution for request ID "+request.getId());
			logger.debug("Request is "+request.toXML());
			SourceGenerationRequestsManager.setStartTime(request.getId());




			SourceGenerationRequestsManager.setPhasePercent(0d, request.getId());

			//Generate subsets of sources selection for multiple generations
			ArrayList<ArrayList<Resource>> sourcesSubsets=getComplexGenerationSubSets(request);
			if(sourcesSubsets.size()==0) throw new Exception("No valid sources subset for generation request "+request.getId());
			//Evaluate to generate Table Count
			int Ntabs_per_step=1;
			if(request.getLogic().equals(LogicType.HCAF))
				for(Field f:request.getGenerationParameters()) 
					if(f.name().equals(SourceGenerationRequest.NUM_INTERPOLATIONS))
						Ntabs_per_step=f.getValueAsInteger()-2;

			SourceGenerationRequestsManager.setEvaluatedComputationCount(sourcesSubsets.size()*request.getAlgorithms().size(), request.getId());
			SourceGenerationRequestsManager.setToGenerateTableCount(sourcesSubsets.size()*Ntabs_per_step*request.getAlgorithms().size(), request.getId());





			for(List<Resource> sourcesSubset:sourcesSubsets)
				for(AlgorithmType algorithm:request.getAlgorithms()){
					BatchGeneratorI batch=null;
					try{
						batch =EnvironmentalLogicManager.getBatch(request.getSubmissionBackend());
						logger.debug("Got batch Id "+batch.getReportId());
						SourceGenerationRequestsManager.addReportId(batch.getReportId(), request.getId());
						try{
							simpleExecution(algorithm,request, sourcesSubset, batch,this);
						}catch(Exception e){
							logger.error("Failed single execution step for "+request.getId()+", sources subset was "+Arrays.toString(sourcesSubset.toArray()));
						}
					}catch(Exception e){
						logger.error("Unexpected Exception ",e);
						if (batch!=null)release(batch);
					}
				}


			//******************* BLOCKING waiting for executions to complete
			logger.debug("Going to wait for generations, acquiring "+sourcesSubsets.size()*request.getAlgorithms().size()+" permits, currently available : "+blocking.availablePermits());

			blocking.acquire(sourcesSubsets.size()*request.getAlgorithms().size());


			ArrayList<Integer> generatedSources=SourceGenerationRequestsManager.getById(request.getId()).getGeneratedSources();

			logger.debug("Awaken process, generated "+generatedSources.size());

			if(generatedSources.size()==0) throw new Exception("No sources were generated/ registered. Check previous log.");

			////******* SINGLE EXECUTION


			if(request.getLogic().equals(LogicType.HSPEC)){
				boolean forceRegeneration=false;
				boolean generateMaps=false;
				boolean generateGis=false;

				for(Field f:request.getExecutionParameters()){
					if(f.name().equals(SourceGenerationRequest.FORCE_MAPS_REGENERATION)) forceRegeneration=f.getValueAsBoolean();
					else if(f.name().equals(SourceGenerationRequest.GENERATE_MAPS)) generateMaps=f.getValueAsBoolean();
					else if(f.name().equals(SourceGenerationRequest.GIS_ENABLED)) generateGis=f.getValueAsBoolean();
				}
				if(generateMaps){

					logger.trace("Generating jobs for request "+request.getId());
					ArrayList<Integer> jobIds=new ArrayList<Integer>();
					SourceGenerationRequestsManager.setPhase(SourceGenerationPhase.mapgeneration,request.getId());
					for(Integer hspecId:generatedSources){
						Resource hspec=SourceManager.getById(hspecId);
						logger.trace("Requesting job for hspec "+hspec.getTitle()+", ID :"+hspec.getSearchId());
						int jobID=CommonServiceLogic.generateMaps_Logic(hspec.getSearchId(), new ArrayList<Field>(), request.getAuthor(), generateGis,forceRegeneration);
						logger.trace("Job is "+jobID);
						SourceGenerationRequestsManager.addJobIds(jobID, request.getId());
						jobIds.add(jobID);
					}

					if(jobIds.size()>0){
						logger.trace("Generation "+request.getId()+" : submitted  jobIds : "+jobIds);
						//						Boolean completed=false;
						//						while(!completed){
						//							for(Integer id:jobIds){
						//								Submitted submittedJob=SubmittedManager.getSubmittedById(id);
						//								if(!submittedJob.getStatus().equals(SubmittedStatus.Completed)&&!submittedJob.getStatus().equals(SubmittedStatus.Error)){
						//									completed=false;
						//									break;
						//								}else completed=true;
						//							}
						//							if(!completed)
						//								try{
						//									Thread.sleep(10*1000);
						//								}catch(InterruptedException e){}
						//						}
					}
				}else SourceGenerationRequestsManager.setPhase(SourceGenerationPhase.completed,request.getId()); // Only non map generating jobs complete here, others will be set to completed by updater thread 
			}else SourceGenerationRequestsManager.setPhase(SourceGenerationPhase.completed,request.getId()); // Only non HSPEC jobs complete here  
		}catch(Exception e){
			logger.error("Unexpected Exception while executing request "+request.getId()+", execution error messages were :", e);
			for(String msg:exceptions) logger.error(msg);
			try{
				SourceGenerationRequestsManager.setPhase(SourceGenerationPhase.error,request.getId());
			}catch(Exception e1){
				logger.error("Unable to update phase , request was "+request,e);
			}
		}
	}





	private static Set<Resource> getExisting(AlgorithmType algorithm, List<Resource> sources, String currentRequestId, LogicType logic, ArrayList<Field> generationParameters)throws Exception{
		//		logger.trace("Request "+currentRequestId+" looking for existing generated Table [Algorith : "+algorithm+" ; HSPEN ID : "+hspenId+" ; HCAF ID : "+hcafId+"]");
		ArrayList<Field> requestFilter= new ArrayList<Field>();

		SourceGenerationRequest requestFilterModel=new SourceGenerationRequest();
		//		requestFilterModel.getAlgorithms().add(algorithm);
		//		for(Resource r: sources)requestFilterModel.addSource(r);
		requestFilterModel.setGenerationParameters(generationParameters);

		requestFilterModel.setLogic(logic);


		requestFilter.add(requestFilterModel.getField(SourceGenerationRequestFields.logic));
		requestFilter.add(requestFilterModel.getField(SourceGenerationRequestFields.generationparameters));



		for(SourceGenerationRequest request: SourceGenerationRequestsManager.getList(requestFilter)){
			if(!request.getId().equals(currentRequestId)&&request.getAlgorithms().contains(algorithm)){
				if(request.getPhase().equals(SourceGenerationPhase.pending)||request.getPhase().equals(SourceGenerationPhase.datageneration)){
					//check subset 
					ArrayList<ArrayList<Resource>> subsets=getComplexGenerationSubSets(request);

					boolean found=false;
					for(ArrayList<Resource> subset:subsets){						
						if(subset.size()==sources.size()){
							for(int i=0;i<subset.size();i++){
								found=subset.get(i).getSearchId()==sources.get(i).getSearchId();
								if(!found)break;
							}
						}
						if(found)break;
					}

					if(found){						
						//Found subset
						logger.trace("Found existing request [PHASE : "+request.getPhase()+" ; ID : "+request.getId()+"], waiting for generation");
						int[] sourcesIds=new int[sources.size()];
						for(int i=0;i<sourcesIds.length;i++)sourcesIds[i]=sources.get(i).getSearchId();
						TableGenerationExecutionManager.signForGeneration(new Execution(algorithm, logic, sourcesIds,requestFilterModel.getField(SourceGenerationRequestFields.generationparameters).value()));
					}
				}
			}
		}

		Resource filterModel=new Resource(ResourceType.valueOf(logic+""),0);
		filterModel.setAlgorithm(algorithm);
		filterModel.setParameters(generationParameters);
		for(Resource r: sources)filterModel.addSource(r);

		ArrayList<Field> resourceFilter= new ArrayList<Field>();
		resourceFilter.add(filterModel.getField(MetaSourceFields.algorithm));
		resourceFilter.add(filterModel.getField(MetaSourceFields.sourcehcafids));
		resourceFilter.add(filterModel.getField(MetaSourceFields.sourcehspecids));
		resourceFilter.add(filterModel.getField(MetaSourceFields.sourcehspenids));
		resourceFilter.add(filterModel.getField(MetaSourceFields.sourceoccurrencecellsids));
		resourceFilter.add(filterModel.getField(MetaSourceFields.parameters));
		return SourceManager.getList(resourceFilter);

	}





	private static void simpleExecution(final AlgorithmType algorithmType,final SourceGenerationRequest theRequest, final List<Resource> sourcesSubSet,BatchGeneratorI batch,final HSPECGroupWorker worker)throws Exception{




		batch.setConfiguration(ServiceContext.getContext().getFile("generator", false).getAbsolutePath()+File.separator, 
				ConfigurationManager.getVODescriptor().getInternalDB());

		//********** START PROCESS INIT




		try{

			//************* CHECK EXISTING
			Set<Resource> existing=getExisting(algorithmType, sourcesSubSet,theRequest.getId(),theRequest.getLogic(),theRequest.getGenerationParameters());

			if(existing.size()==0){
				logger.trace("No Resources found, submitting generation..");	
				//*********** Submitting generation for one algorithm
				batch.generateTable(
						new TableGenerationConfiguration(
								theRequest.getLogic(), 
								algorithmType, 
								sourcesSubSet,
								theRequest.getSubmissionBackend(), 
								theRequest.getExecutionEnvironment(), 
								theRequest.getBackendURL(), 
								theRequest.getEnvironmentConfiguration(),
								theRequest.getNumPartitions(), 
								theRequest.getAuthor(),theRequest.getGenerationParameters(),worker){

							@Override
							public void registerGeneratedSourcesCallback(
									List<String> toRegisterTables) throws Exception{
								//Register sources
								List<Resource> toReturn=registerSources(toRegisterTables,
										ResourceType.valueOf(this.getLogic()+""), 
										this.getAlgorithm(), this.getAuthor(), theRequest.getDescription(), this.getExecutionEnvironment(),
										sourcesSubSet, theRequest.getGenerationname()+"_"+this.getAlgorithm(),getAdditionalParameters());

								if (toReturn.size()==0) throw new Exception("No tables were generated");
								//Notify Pending generations
								int[] sourcesIds=new int[sourcesSubSet.size()];
								for(int i=0;i<sourcesIds.length;i++)sourcesIds[i]=sourcesSubSet.get(i).getSearchId();
								TableGenerationExecutionManager.notifyGeneration(
										new Execution(this.getAlgorithm(), theRequest.getLogic(), sourcesIds,theRequest.getField(SourceGenerationRequestFields.generationparameters).value()));
								worker.notifyGenerated(toReturn);

							}
							@Override
							public void notifyError(Exception e) {
								logger.warn("Unexpected Exception", e);


								//Notify Pending generations
								int[] sourcesIds=new int[sourcesSubSet.size()];
								for(int i=0;i<sourcesIds.length;i++)sourcesIds[i]=sourcesSubSet.get(i).getSearchId();
								try {
									TableGenerationExecutionManager.notifyGeneration(
											new Execution(this.getAlgorithm(), theRequest.getLogic(), sourcesIds,theRequest.getField(SourceGenerationRequestFields.generationparameters).value()));
								} catch (Exception e2) {
									logger.warn("Unable to notify pending generations... ",e2);
								}


								try{
									worker.notifyException(e, 
											new Execution(this.getAlgorithm(), theRequest.getLogic(), sourcesIds,theRequest.getField(SourceGenerationRequestFields.generationparameters).value()));
								}catch(Exception e1){
									//Exception only if empty sources 
								}
							}
							@Override
							public void release(
									BatchGeneratorI batch) {
								worker.release(batch);
							}
						});						

			}else{
				logger.trace("Found "+existing.size()+" existing sources ");
				ArrayList<Resource> generated=new ArrayList<Resource>(); 
				for(Resource r:existing)generated.add(r);
				worker.notifyGenerated(generated);
				worker.release(batch);
			}
		}catch(Exception e){
			logger.error("Unable to generate data for  algorithm "+algorithmType+" subset was : "+Arrays.toString(sourcesSubSet.toArray()),e);
			worker.release(batch);
		}
	}


	private static ArrayList<ArrayList<Resource>> getComplexGenerationSubSets(SourceGenerationRequest request)throws Exception{
		ArrayList<ArrayList<Resource>> toReturn=new ArrayList<ArrayList<Resource>>();
		switch(request.getLogic()){
		case HSPEC : {
			boolean combineMatching=true;
			for(Field f:request.getExecutionParameters())				
				if(f.name().equals(SourceGenerationRequest.COMBINE_MATCHING)) combineMatching=f.getValueAsBoolean();

			if(request.getHcafIds().size()==0) throw new Exception ("No HCAF resources found for request "+request.getId()+", Logic was "+request.getLogic());
			if(request.getHspenIds().size()==0) throw new Exception ("No HSPEN resources found for request "+request.getId()+", Logic was "+request.getLogic());
			for(Integer hcafId:request.getHcafIds()){
				Resource hcaf=SourceManager.getById(hcafId);
				if(hcaf==null) logger.warn("Selected HCAF Resource [ID = "+hcafId+" ] was null , skipping..");
				else for(Integer hspenId:request.getHspenIds()){
					Resource hspen=SourceManager.getById(hspenId);
					if(combineMatching&&!hspen.getSourceHCAFIds().contains(hcafId)) {
						logger.debug("HSPEN ("+hspenId+")doesn't match current hcaf ("+hcafId+"), hspen sources where "+hspen.getSourceHCAFIds());										
					}else{
						if(hcaf==null) logger.warn("Selected HSPEN Resource [ID = "+hspenId+" ] was null , skipping..");
						else toReturn.add(new ArrayList<Resource>(Arrays.asList(new Resource[]{hcaf,hspen})));
					}
				}
			}
			break;
		}
		case HSPEN : {
			if(request.getHcafIds().size()==0) throw new Exception ("No HCAF resources found for request "+request.getId()+", Logic was "+request.getLogic());
			if(request.getHspenIds().size()==0) throw new Exception ("No HSPEN resources found for request "+request.getId()+", Logic was "+request.getLogic());
			if(request.getOccurrenceCellIds().size()==0) throw new Exception ("No Occurrence Cells resources found for request "+request.getId()+", Logic was "+request.getLogic());
			for(Integer hcafId:request.getHcafIds()){
				Resource hcaf=SourceManager.getById(hcafId);
				if(hcaf==null) logger.warn("Selected HCAF Resource [ID = "+hcafId+" ] was null , skipping..");
				else for(Integer hspenId:request.getHspenIds()){
					Resource hspen=SourceManager.getById(hspenId);
					if(hspen==null) logger.warn("Selected HSPEN Resource [ID = "+hspenId+" ] was null , skipping..");
					else for(Integer occurId:request.getOccurrenceCellIds()){
						Resource occurrence=SourceManager.getById(occurId);
						if(occurrence==null) logger.warn("Selected Occurrence Cells Resource [ID = "+occurId+" ] was null , skipping..");
						else toReturn.add(new ArrayList<Resource>(Arrays.asList(new Resource[]{hcaf,hspen,occurrence})));
					}
				}
			}
			break;
		}
		case HCAF : {
			if(request.getHcafIds().size()==0) throw new Exception ("No HCAF resources found for request "+request.getId()+", Logic was "+request.getLogic());
			ArrayList<Resource> sources=new ArrayList<Resource>();
			for(Integer hcafId:request.getHcafIds()){
				Resource hcaf=SourceManager.getById(hcafId);
				if(hcaf==null) throw new Exception("Selected HCAF Resource [ID = "+hcafId+" ] was null , skipping..");
				else sources.add(hcaf);
			}
			toReturn.add(sources);
			break;
		}
		}
		return toReturn;
	}


	public static List<Resource> registerSources(List<String> toRegister,
			ResourceType type,AlgorithmType alg,String author,String description,String environment,List<Resource> sources,String titlePattern,ArrayList<Field> additionalParameters) throws Exception{
		ArrayList<String> unregisteredTables=new ArrayList<String>(); 
		ArrayList<Resource> toReturn=new ArrayList<Resource>();
		for(int i=0;i<toRegister.size();i++){
			String generatedTable=toRegister.get(i);
			try{
				Resource toRegisterSource=new Resource(type,0);
				toRegisterSource.setAlgorithm(alg);
				toRegisterSource.setAuthor(author);
				toRegisterSource.setGenerationTime(System.currentTimeMillis());
				toRegisterSource.setDescription(description);
				toRegisterSource.setProvenance("Generated on AquaMaps VRE, submitted on "+environment);

				for(Resource r:sources)
					toRegisterSource.addSource(r);

				toRegisterSource.setStatus(ResourceStatus.Completed);
				toRegisterSource.setTableName(generatedTable);
				toRegisterSource.setParameters(additionalParameters);
				String title=(toRegister.size()>1)?titlePattern+"_step"+i:titlePattern;
				toRegisterSource.setTitle(title);

				//************* Checking row count
				Long count=0l;
				DBSession session=null;
				try{
					session=DBSession.getInternalDBSession();
					if(session.checkTableExist(generatedTable))
						count=session.getTableCount(generatedTable);
					else throw new Exception("TABLE "+generatedTable+" NOT FOUND!!");
				}catch(Exception e){								
					throw e;
				}finally{if (session!=null) session.close();}
				toRegisterSource.setRowCount(count);
				toRegisterSource=SourceManager.registerSource(toRegisterSource);
				logger.trace("Registered Resource with id "+toRegisterSource.getSearchId());

				toReturn.add(toRegisterSource);
			}catch(Exception e){
				//Registration failure, need to delete table
				unregisteredTables.add(generatedTable);								
				logger.error("Unable to register source table "+generatedTable,e);
				DBSession session=null;
				try{
					session=DBSession.getInternalDBSession();
					if(session.checkTableExist(generatedTable))session.dropTable(generatedTable);
				}catch(Exception e1){
					logger.warn("Unable to delete table "+generatedTable,e);
				}finally{if (session!=null) session.close();}
			}
		}
		return toReturn; 
	}

	public void notifyGenerated(List<Resource> generated) throws Exception{		
		for(Resource r:generated) 
			SourceGenerationRequestsManager.addGeneratedResource(r.getSearchId(), request.getId());
		blocking.release();
	}

	public void notifyException(Exception e,Execution exec){
		exceptions.add("Execution "+exec+" threw exception : "+e.getMessage());
		blocking.release();
	}


	public void release(BatchGeneratorI batch){
		logger.debug("leaving batch ID "+batch.getReportId());
		try{						
			if(batch!=null) {
				EnvironmentalLogicManager.leaveBatch(batch);
				SourceGenerationRequestsManager.removeReportId(batch.getReportId(), request.getId());
			}
		}catch(Exception e){
			logger.error("Unable to leave generator",e);
		}
	}
}
