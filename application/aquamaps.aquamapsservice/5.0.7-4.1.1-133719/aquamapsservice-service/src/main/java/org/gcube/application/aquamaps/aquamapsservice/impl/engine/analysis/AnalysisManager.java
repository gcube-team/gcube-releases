package org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext.FOLDERS;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.AnalysisTableManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.MyPooledExecutor;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Analysis;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.AnalysisFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SourceGenerationRequestFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;
import org.gcube_system.namespaces.application.aquamaps.types.PagedRequestSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalysisManager {

	final static Logger logger= LoggerFactory.getLogger(AnalysisManager.class);
	
	private static ExecutorService pool=null;
	private static Semaphore insertedRequests=null;
	
	
	public static void init(boolean purgeInvalid,int monitorInterval)throws Exception{
		logger.trace("Initializing pools..");
		pool=MyPooledExecutor.getExecutor("ANALYSIS_WORKER", 
				//					ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.JOB_PRIORITY),
				ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.ANALYSIS_MAX_WORKERS));
//				ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.HSPEC_GROUP_MIN_WORKERS),
//				ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.HSPEC_GROUP_INTERVAL_TIME));
		
		logger.trace("Storing into "+ServiceContext.getContext().getFolderPath(FOLDERS.ANALYSIS));
		
		if(purgeInvalid){
			int count=0;
			for(Analysis a:AnalysisTableManager.getList(new ArrayList<Field>())){
				if(!a.getStatus().equals(SubmittedStatus.Completed)&&
						!a.getStatus().equals(SubmittedStatus.Error)&&
						!a.getStatus().equals(SubmittedStatus.Pending)){
					AnalysisTableManager.setStatus(SubmittedStatus.Error, a.getId());
					count++;
				}
			}
			logger.trace("Purged "+count+" requests");
		}
		
		
		logger.trace("Looking for existing obj requests...");	
		
		List<Field> pendingObjFilter=new ArrayList<Field>();		
		pendingObjFilter.add(new Field(AnalysisFields.status+"",SubmittedStatus.Pending+"",FieldType.STRING));
		
		Integer analCount=AnalysisTableManager.getCount(pendingObjFilter).intValue();
		insertedRequests=new Semaphore(analCount);
		logger.trace("Found "+analCount+" requests");
		
		
		AnalysisUpdaterThread updater=new AnalysisUpdaterThread(monitorInterval*1000);
		updater.start();
		AnalysisMonitor monitor=new AnalysisMonitor();
		monitor.start();
		logger.trace("Monitor started");
	}
	
	public static List<Analysis> getAvailableRequests()throws Exception {
		insertedRequests.acquire();
		List<Field> filter=new ArrayList<Field>();		
		filter.add(new Field(AnalysisFields.status+"",SubmittedStatus.Pending+"",FieldType.STRING));
		PagedRequestSettings settings=new PagedRequestSettings(1,0,OrderDirection.DESC,SourceGenerationRequestFields.submissiontime+"");
		return AnalysisTableManager.getList(filter,settings);
	}
	public static void start(Analysis request)throws Exception{
		AnalysisTableManager.setStatus(SubmittedStatus.Generating, request.getId());
		AnalysisWorker thread=new AnalysisWorker(request);
		pool.execute(thread);
	}
	public static String insertRequest(Analysis request)throws Exception{
		String toReturn=AnalysisTableManager.insertRequest(request);
		insertedRequests.release();
		return toReturn;
	}
}
