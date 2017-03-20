package org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext.FOLDERS;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.AquaMapsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.AquaMapsObjectExecutionRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.Generator;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ExtendedExecutor;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.MyPooledExecutor;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;
import org.gcube_system.namespaces.application.aquamaps.types.PagedRequestSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobExecutionManager {

	final static Logger logger= LoggerFactory.getLogger(JobExecutionManager.class);


	private static ExtendedExecutor jobPool=null;
	private static ExtendedExecutor aqPool=null;


	private static final ConcurrentHashMap<Integer, Semaphore> blockedJobs=new ConcurrentHashMap<Integer, Semaphore>();

//	private static Semaphore insertedJobs=null;
//	private static Semaphore insertedObjects=null;

	public static void init(boolean purgeInvalid)throws Exception{

		logger.trace("Initializing pools..");
		jobPool=MyPooledExecutor.getExecutor("JOB_WORKER", 
				ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.JOB_MAX_WORKERS)
				);

		aqPool=MyPooledExecutor.getExecutor("AQ_WORKER", 
				ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.AQUAMAPS_OBJECT_MAX_WORKERS)
				);



		logger.trace("Storing into "+ServiceContext.getContext().getFolderPath(FOLDERS.SERIALIZED));
		
		
		if(purgeInvalid){
			logger.trace("Purging orphan objects requests...");
			long orphanObjectCount=0;
			SubmittedStatus[] invalidObjStatus=new SubmittedStatus[]{
				SubmittedStatus.Generating,
				SubmittedStatus.Pending,
				SubmittedStatus.Publishing,
				SubmittedStatus.Simulating,
			};
			for(SubmittedStatus invalid:invalidObjStatus){
				orphanObjectCount+=invalidReference(true, invalid);
			}			
			logger.trace("Purged "+orphanObjectCount+" objects");
			
			
					
			logger.trace("Purging orphan jobs requests...");
			long orphanJobCount=0;			
			SubmittedStatus[] invalidJobStatus=new SubmittedStatus[]{
					SubmittedStatus.Generating,
//					SubmittedStatus.Pending,
					SubmittedStatus.Publishing,
					SubmittedStatus.Simulating,
				};
			for(SubmittedStatus invalid:invalidJobStatus){
				orphanJobCount+=invalidReference(false, invalid);
			}			
			logger.trace("Purged "+orphanJobCount+" jobs");
			
			
		}



//		logger.trace("Looking for existing obj requests...");			
//		Integer objCount=SubmittedManager.getCount(pendingObjFilter).intValue();
////		insertedObjects=new Semaphore(objCount);
//		logger.trace("Found "+objCount+" requests");


//		logger.trace("Looking for existing job requests...");
//		List<Field> jobfilter=new ArrayList<Field>();
//		jobfilter.add(new Field(SubmittedFields.isaquamap+"",false+"",FieldType.BOOLEAN));
//		jobfilter.add(new Field(SubmittedFields.status+"",SubmittedStatus.Pending+"",FieldType.STRING));
//		int jobCount=SubmittedManager.getCount(jobfilter).intValue();
////		insertedJobs=new Semaphore(jobCount);
//		logger.trace("Found "+jobCount+" requests");






		RequestsMonitor jobMonitor=RequestsMonitor.get(false);
		jobMonitor.start();
		RequestsMonitor objMonitor=RequestsMonitor.get(true);
		objMonitor.start();


		logger.trace("Monitors started");
	}


	public static int insertJobExecutionRequest(Job toExecute,boolean forceRegeneration)throws Exception{
		try{
			SourceManager.getById(toExecute.getSourceHSPEC().getSearchId());
		}catch(Exception e){
			logger.error("Unable to load selected HSPEC "+toExecute.getSourceHSPEC().getSearchId(),e);
			throw new Exception("Unable to load HSPEC "+toExecute.getSourceHSPEC().getSearchId());
			
		}
		
		String file=ServiceContext.getContext().getFolderPath(FOLDERS.SERIALIZED)+File.separator+ServiceUtils.generateId("Job", ".xml");
		logger.debug("Serializing job "+toExecute.getName()+" to "+file);
		AquaMapsXStream.serialize(file, toExecute);
		Submitted toInsert=new Submitted(0);
		toInsert.setAuthor(toExecute.getAuthor());
		toInsert.setSubmissionTime(System.currentTimeMillis());
		toInsert.setGisEnabled(toExecute.getIsGis());
		toInsert.setIsAquaMap(false);
		toInsert.setJobId(0);
		toInsert.setSaved(false);
		toInsert.setSelectionCriteria("");
		toInsert.setSerializedRequest(file);
		toInsert.setSourceHCAF(toExecute.getSourceHCAF().getSearchId());
		toInsert.setSourceHSPEC(toExecute.getSourceHSPEC().getSearchId());
		toInsert.setSourceHSPEN(toExecute.getSourceHSPEN().getSearchId());
		toInsert.setStatus(SubmittedStatus.Pending);
		toInsert.setTitle(toExecute.getName());	
		toInsert.setSpeciesCoverage(toExecute.getCompressedCoverage());
		toInsert.setForceRegeneration(forceRegeneration);
		toInsert=SubmittedManager.insertInTable(toInsert);
		logger.trace("Assigned id "+toInsert.getSearchId()+" to Job "+toInsert.getTitle()+" [ "+toInsert.getAuthor()+" ]");


//		insertedJobs.release(1);

		return toInsert.getSearchId();
	}

	public static void insertAquaMapsObjectExecutionRequest(List<AquaMapsObjectExecutionRequest> requests)throws Exception{
		for(AquaMapsObjectExecutionRequest request:requests){
			String file=ServiceContext.getContext().getFolderPath(FOLDERS.SERIALIZED)+File.separator+ServiceUtils.generateId("AQ", ".xml");
			logger.debug("Serializing object "+request.getObject().getTitle()+" to "+file);
			request.getObject().setSerializedRequest(file);
			request.getObject().setStatus(SubmittedStatus.Generating);
			AquaMapsXStream.serialize(file, request);
			SubmittedManager.update(request.getObject());
		}
		int jobId=requests.get(0).getObject().getJobId();
		logger.trace("Creating "+requests.size()+" requests for objects execution for job "+jobId);
		blockedJobs.put(jobId, new Semaphore(-(requests.size()-1)));
		
		
		AquaMapsManager.insertRequests(requests);
//		insertedObjects.release(requests.size());
//		logger.debug("Increment available object sempahore by "+requests.size()+", total available "+insertedObjects.availablePermits());

		//************* BLOCKS current job
		((Semaphore)blockedJobs.get(jobId)).acquireUninterruptibly();
		blockedJobs.remove(jobId);
	}

	private static void startJob(Submitted job)throws Exception{
		SubmittedManager.updateStatus(job.getSearchId(), SubmittedStatus.Simulating);
		Submitted submittedJob=SubmittedManager.getSubmittedById(job.getSearchId());
		Job toExecute=(Job) AquaMapsXStream.deSerialize(submittedJob.getSerializedRequest());
		JobWorker worker=new JobWorker(toExecute,submittedJob);
		jobPool.execute(worker);
	}

	private static void startAquaMapsObject(Submitted object)throws Exception{
		SubmittedManager.updateStatus(object.getSearchId(), SubmittedStatus.Publishing);
		Submitted submittedObject=SubmittedManager.getSubmittedById(object.getSearchId());		
		AquaMapsObjectExecutionRequest toExecute=(AquaMapsObjectExecutionRequest) AquaMapsXStream.deSerialize(submittedObject.getSerializedRequest());
		AquaMapsObjectWorker worker=new AquaMapsObjectWorker(toExecute);
		aqPool.execute(worker);
	}


	public static void start(Submitted toStart)throws Exception{
		if(toStart.getIsAquaMap()) startAquaMapsObject(toStart);
		else startJob(toStart);
	}



	public static void cleanReferences(Submitted toClean){
		try{
			if(!toClean.getIsAquaMap()){
				JobManager.cleanTemp(toClean.getSearchId());
			}else Generator.cleanData(toClean);
		}catch(Exception e){
			logger.error("Unexpected Error while trying to clean up submitted "+toClean.getSearchId()+" ["+(toClean.getIsAquaMap()?"OBJECT":"JOB")+"]",e);
		}
	}





	public static void alertJob(int objId,int jobId){
		try{
			if(blockedJobs.containsKey(jobId)){
				Semaphore sem=((Semaphore) blockedJobs.get(jobId));
				sem.release();
				logger.trace("Object "+objId+" released lock for job "+jobId+", still waiting for "+sem.availablePermits());
			}else logger.warn("Unable to find queued job "+jobId+", object was "+objId);
		}catch(Exception e){
			logger.warn("UNABLE TO RELEASE LOCK FOR JOB [ID : "+jobId+"]",e);
		}
	}


	

	public static List<Submitted> getAvailableRequests(boolean object,int maxSize)throws Exception {
//		if(object) {
//			logger.debug("Requesting available objects, sempahore value is "+insertedObjects.availablePermits());
//			insertedObjects.acquireUninterruptibly();
//		}
//		else {
//			logger.debug("Requesting available jobs, sempahore value is "+insertedObjects.availablePermits());
//			insertedJobs.acquireUninterruptibly();
//		}
		

		List<Field> filter=new ArrayList<Field>();
		filter.add(new Field(SubmittedFields.isaquamap+"",object+"",FieldType.BOOLEAN));
		filter.add(new Field(SubmittedFields.status+"",(object?SubmittedStatus.Generating:SubmittedStatus.Pending)+"",FieldType.STRING));
		PagedRequestSettings settings= new PagedRequestSettings(maxSize,0,OrderDirection.ASC,SubmittedFields.submissiontime+"");
		return SubmittedManager.getList(filter, settings);
	}

	private static long invalidReference(boolean isAquaMapObject,SubmittedStatus status) throws SQLException, IOException, Exception{
		List<Field> invalidFilter=new ArrayList<Field>();
		invalidFilter.add(new Field(SubmittedFields.isaquamap+"",isAquaMapObject+"",FieldType.BOOLEAN));
		invalidFilter.add(new Field(SubmittedFields.status+"",status+"",FieldType.STRING));
		long count=0;
		for(Submitted submitted:SubmittedManager.getList(invalidFilter)){
			SubmittedManager.updateStatus(submitted.getSearchId(), SubmittedStatus.Error);
			count++;
		}
		return count;
	}
}
