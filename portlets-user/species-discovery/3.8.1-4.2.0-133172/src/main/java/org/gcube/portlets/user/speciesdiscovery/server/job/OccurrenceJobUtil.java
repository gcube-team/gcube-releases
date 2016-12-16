package org.gcube.portlets.user.speciesdiscovery.server.job;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.data.spd.stubs.types.Status;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.DaoSession;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.OccurrenceJobPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.ResultRowPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.service.SpeciesService;
import org.gcube.portlets.user.speciesdiscovery.server.session.FetchingSession;
import org.gcube.portlets.user.speciesdiscovery.server.session.SelectableFetchingBuffer;
import org.gcube.portlets.user.speciesdiscovery.server.stream.StreamExtend;
import org.gcube.portlets.user.speciesdiscovery.server.util.DateUtil;
import org.gcube.portlets.user.speciesdiscovery.server.util.XStreamUtil;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSource;
import org.gcube.portlets.user.speciesdiscovery.shared.DownloadState;
import org.gcube.portlets.user.speciesdiscovery.shared.JobOccurrencesModel;
import org.gcube.portlets.user.speciesdiscovery.shared.OccurrencesJob;
import org.gcube.portlets.user.speciesdiscovery.shared.OccurrencesSaveEnum;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.SaveFileFormat;

public class OccurrenceJobUtil {

	//STATE RETURNED BY STATUS RESPONSE
	public static final String COMPLETED = "COMPLETED";
	public static final String FAILED = "FAILED";
	public static final String RUNNING = "RUNNING";
	public static final String PENDING = "PENDING";
	
	private static final String ALIASKEY = "key";

	protected static Logger logger = Logger.getLogger(OccurrenceJobUtil.class);

	public static JobOccurrencesModel convertJob(OccurrencesJob job, Status statusResponse, OccurrenceJobPersistence occurrencesJobDao) {
	 	//TODO SET END TIME 
		JobOccurrencesModel jobOccurrenceModel;
		DownloadState downloadState = null;
		long endTime = 0;

		String status = statusResponse.getStatus(); 
		downloadState = getDownloadState(status);
		logger.trace("jobId: "+job.getId() +" download state: " + downloadState);
		
		//FOR DEBUG
//		System.out.println("jobId: "+job.getId() +" download state: " + downloadState);
		
		if(downloadState==null) //Case with exception
			return null;

    	//SET SUBMIT TIME
		long submitTime = job.getSubmitTime();
		Date submit = DateUtil.millisecondsToDate(submitTime);
//		jobSpeciesModel.setStartTime(DateUtil.dateToDateFormatString(start));
	
		int completedEntry = 0;
		if(statusResponse.getCompletedEntries()>0)
			completedEntry = statusResponse.getCompletedEntries();
		
		boolean changeStatus = false;
		
		//if status is completed and job was saved, update status as saved
		if(downloadState.equals(DownloadState.COMPLETED)){
			if(job.getState().compareTo(DownloadState.SAVED.toString())==0){
				downloadState = DownloadState.SAVED;
				changeStatus = true;
			}
		}
		
		jobOccurrenceModel = new JobOccurrencesModel(job.getId(), job.getName(), job.getDescription(), downloadState, job.getScientificName(), job.getDataSources(), submit, null,completedEntry, job.getExpectedOccurrence());

		try{

			boolean changeEndTime = false;
			
			//UPDATE END TIME
			if(downloadState.equals(DownloadState.FAILED) || downloadState.equals(DownloadState.COMPLETED)){
	
//				if(job.getEndTime()==0){ //UPDATE end time first time only
//					
//					logger.trace("UPDATE end time first time only - " + downloadState);
//					endTime = Calendar.getInstance().getTimeInMillis();
//					job.setEndTime(endTime);
//					changeEndTime = true;
//				}
//				System.out.println("job "+job);
//				System.out.println("statusResponse.getEndDate() "+statusResponse.getEndDate());
//				System.out.println("job.getEndTime() "+job.getEndTime());
				
				if(statusResponse.getEndDate()!=null && job.getEndTime()==0){ //UPDATE end time first time only
					
					logger.trace("UPDATE end time first time only - " + downloadState);
//					endTime = Calendar.getInstance().getTimeInMillis();
					endTime = statusResponse.getEndDate().getTimeInMillis();
					job.setEndTime(endTime);
					changeEndTime = true;
		//			speciesJobDao.update(job);
		
				}
			}
			
			
			boolean changeStartTime = false;
			
		   	//SET START TIME
			long startTime = job.getStartTime();
			if(statusResponse.getStartDate()!=null && startTime==0){ //UPDATE start time first time only
				Date start = DateUtil.millisecondsToDate(statusResponse.getStartDate().getTimeInMillis());
//					jobSpeciesModel.setStartTime(DateUtil.dateToDateFormatString(start));
				jobOccurrenceModel.setStartTime(start);
				changeStartTime = true;
			}

			
			//UPDATE DAO
			if(changeStatus || changeEndTime || changeStartTime){
				job.setState(downloadState.toString());
				occurrencesJobDao.update(job);
			}

		}catch (Exception e) {
			logger.error("An error occurred on update the  occurrencesJobDao ", e);
		}
		
		
		endTime = job.getEndTime();
		long elapsedTime = 0;
		
		//SET END TIME, BECAUSE IT IS CHANGED
		if(endTime!=0){
			Date end = DateUtil.millisecondsToDate(endTime);
			jobOccurrenceModel.setEndTime(end);
			elapsedTime = endTime;
		}
		else
			elapsedTime = Calendar.getInstance().getTimeInMillis();
		

		//SET ELAPSED TIME
		jobOccurrenceModel.setElapsedTime(DateUtil.getDifference(submitTime, elapsedTime));
		
		
		//OTHERS SET
		jobOccurrenceModel.setFileFormat(converFileFormat(job.getFileFormat()));
		jobOccurrenceModel.setCsvType(convertCsvType(job.getCsvType()));
		jobOccurrenceModel.setByDataSource(job.isByDataSource());
		
		return jobOccurrenceModel;
	}
	
	public static DownloadState getDownloadState(String status){
		
		if(status!=null){		
			if(status.compareToIgnoreCase(PENDING)==0){
				return DownloadState.PENDING;
			}else if(status.compareToIgnoreCase(RUNNING)==0){
				return DownloadState.ONGOING;
			}else if(status.compareToIgnoreCase(FAILED)==0){
				return DownloadState.FAILED;
			}else if(status.compareToIgnoreCase(COMPLETED)==0){
				return DownloadState.COMPLETED;
			}
		}
		return null;
		
	}

	public static OccurrencesSaveEnum convertCsvType(String csvType) {
		
		if(csvType!=null){
			if(csvType.compareToIgnoreCase(OccurrencesSaveEnum.STANDARD.toString())==0){
				return OccurrencesSaveEnum.STANDARD;
			}else if(csvType.compareToIgnoreCase(OccurrencesSaveEnum.OPENMODELLER.toString())==0){
				return OccurrencesSaveEnum.OPENMODELLER;
			}
		}
		return null;
	}


	public static SaveFileFormat converFileFormat(String fileFormat) {
		
		if(fileFormat!=null){
			if(fileFormat.compareToIgnoreCase(SaveFileFormat.CSV.toString())==0){
				return SaveFileFormat.CSV;
			}else if(fileFormat.compareToIgnoreCase(SaveFileFormat.DARWIN_CORE.toString())==0){
				return SaveFileFormat.DARWIN_CORE;
			}
		}
		return null;

	}


	public static int deleteOccurrenceJobById(String jobIdentifier, OccurrenceJobPersistence occurrencesJobDao){
		logger.trace("Delete occurrence job id: " + jobIdentifier);

		try{
			int removed = occurrencesJobDao.deleteItemByIdField(jobIdentifier);
			return 1;

		}catch (Exception e) {
			logger.error("An error occured deleteOccurrenceJobById  jobId: " + jobIdentifier + " exception: "+e, e);
			
		}
		
		logger.trace("job not exists : " +jobIdentifier);
		return 0;
	}
	
	
	public static int changeStatusOccurrenceJobById(String jobIdentifier, DownloadState state, OccurrenceJobPersistence occurrencesJobDao){
		logger.trace("Change status occurrence job id: " + jobIdentifier);
//		System.out.println("Delete job id: " + jobIdentifier);
		
		int result = 0;
		
		try{
			
			CriteriaBuilder queryBuilder = occurrencesJobDao.getCriteriaBuilder();
			CriteriaQuery<Object> cq = queryBuilder.createQuery();
			Predicate pr1 =  queryBuilder.equal(occurrencesJobDao.rootFrom(cq).get(OccurrencesJob.ID_FIELD), jobIdentifier);
			cq.where(pr1);
			
			Iterator<OccurrencesJob> iterator = occurrencesJobDao.executeCriteriaQuery(cq).iterator();
			
			OccurrencesJob job;
			
			if(iterator.hasNext())
				 job = iterator.next();
			else
				return 0;
			
			job.setState(state.toString());
			
			occurrencesJobDao.update(job);
		
		}catch (Exception e) {
			logger.error("An error occured in change status  jobId: " + jobIdentifier + " exception: "+e, e );
		}
		
		return result;
	}
	
	
	public static List<String> getListOfSelectedKey(FetchingSession<ResultRow> searchSession) throws Exception{
		
		Collection<ResultRow> selectedRows = ((SelectableFetchingBuffer<ResultRow>) searchSession.getBuffer()).getSelected();
		logger.trace("found "+selectedRows.size()+" selected rows");
		
		int count = 0;
		
		List<String> keys = new ArrayList<String>(selectedRows.size());

		for (ResultRow row:selectedRows) {
			//ADD KEY ONLY IF IS NOT EQUAL NULL AND SIZE IS > 0
			if(row.getOccurencesKey()!=null && row.getOccurencesKey().length()>0){
				keys.add(row.getOccurencesKey());
				count += row.getOccurencesCount();
			}
		}
		
		logger.trace("found "+count+" occurrence points");
	
		return keys;
		
	}
	
	
	public static OccurrenceKeys getListOfSelectedKeyByDataSource(String dataSource, ASLSession session) {
		logger.trace("getListOfSelectedKeyByDataSource...");
		
		OccurrenceKeys occurrenceKeys = new OccurrenceKeys();
		
		List<String> keys = new ArrayList<String>();
		
		Iterator<ResultRow> resulRowIt = null;
		
		int count = 0;
		
		try{
			
//			System.out.println("dasource name: "+dataSource);
			logger.trace("datasource name: "+dataSource);
			
			ResultRowPersistence resultRowDao = DaoSession.getResultRowDAO(session);
			
			/*CriteriaBuilder cb = resultRowDao.getCriteriaBuilder();
			
			CriteriaQuery<Object> cq = cb.createQuery();
			
			Predicate pr1 =  cb.equal(resultRowDao.rootFrom(cq).get(ResultRow.DATASOURCE_NAME), dataSource);
			
			Predicate pr2 = cb.equal(resultRowDao.rootFrom(cq).get(ResultRow.SELECTED), true);
			
			cq.where(cb.and(pr1,pr2));
			
			Iterator<ResultRow> resulRowIt = resultRowDao.executeCriteriaQuery(cq).iterator();
			*/
			
			EntityManager em = resultRowDao.createNewManager();
			try {

				Query query = em.createQuery("select t from ResultRow t where t."+ResultRow.DATASOURCE_NAME +" = '"+dataSource+ "' AND t."+ResultRow.SELECTED+" = true");

				resulRowIt = query.getResultList().iterator();

				
			} catch (Exception e) {
				logger.error("Error in update: "+e.getMessage(), e);
				return null;
			 }
			  finally {
			      em.close();
			 }

			while(resulRowIt.hasNext()){
				
				ResultRow row = resulRowIt.next();
				
				if(row.getOccurencesKey()!=null && row.getOccurencesKey().length()>0){
					keys.add(row.getOccurencesKey());
					count += row.getOccurencesCount();
				}
	
			}
			
			occurrenceKeys.setListKey(keys);
			occurrenceKeys.setTotalOccurrence(count);
		
		}catch (Exception e) {
			logger.error("error in getListOfSelectedKeyByDataSource "+ e);
		}
		
		logger.trace("found "+count+" occurrence points");
		
		return occurrenceKeys;
	
	}


	public static JobOccurrencesModel createOccurrenceJobOnServiceByKeys(JobOccurrencesModel jobModel,SpeciesService taxonomyService, OccurrenceJobPersistence occurrencesJobDao, List<String> keys, List<DataSource> dataSources, SaveFileFormat saveFileFormat, OccurrencesSaveEnum csvType, int expectedOccurrence) {
		String serviceJobId = null;
		
		StreamExtend<String> streamKeys = new StreamExtend<String>(keys.iterator()); //convert
		
		String csvTypeString = null;
		
		try {
		
			switch (saveFileFormat) {
			
			case CSV:
				
				if(csvType.equals(OccurrencesSaveEnum.STANDARD))
					serviceJobId = taxonomyService.createOccurrenceCSVJob(streamKeys);
				else if(csvType.equals(OccurrencesSaveEnum.OPENMODELLER))
					serviceJobId = taxonomyService.createOccurrenceCSVOpenModellerJob(streamKeys);
				
				if(jobModel.getCsvType()!=null)
					csvTypeString = jobModel.getCsvType().toString(); //CASE CSV
				
				break;
				
			case DARWIN_CORE:
				
				serviceJobId = taxonomyService.createOccurrenceDARWINCOREJob(streamKeys);
				
				csvTypeString = "";
				
				break;

			default:
				serviceJobId = taxonomyService.createOccurrenceCSVJob(streamKeys);
			}

		} catch (Exception e) {
			logger.error("An error occured in create new occurrences job on server ",e);
			return null;
		}
			

		long submitTimeInMillis = Calendar.getInstance().getTimeInMillis();
		
		try {
				
			//STORE INTO DAO
			OccurrencesJob occurrenceJob = new OccurrencesJob(serviceJobId, jobModel.getJobName(), jobModel.getDescription(), jobModel.getScientificName(), dataSources, DownloadState.PENDING.toString(), "", submitTimeInMillis, 0, 0, jobModel.getFileFormat().toString(),csvTypeString, jobModel.isByDataSource(), convertListKeyIntoStoreXMLString(keys), expectedOccurrence);
			
			//for debug
//			System.out.println("INTO createOccurrenceJobOnServiceByKeys " + occurrenceJob);
			
			occurrencesJobDao.insert(occurrenceJob);
		
			Date start = DateUtil.millisecondsToDate(submitTimeInMillis);
			jobModel.setSubmitTime(start);
			
			//FILL MODEL WITH OTHER DATA
			jobModel.setId(serviceJobId);
			jobModel.setState(DownloadState.PENDING);
			jobModel.setEndTime(null);

		}catch (Exception e) {
			logger.error("An error occured in create new occurrences job on dao object " +e,e);
		}
		
		return jobModel;
		
	}
	
	
	public static String convertListKeyIntoStoreXMLString(List<String> keys){
		String storeKeys = "";
		
		KeyStringList keyStringList = new KeyStringList();
		
		XStreamUtil<KeyStringList> xstreamUtil = new XStreamUtil<KeyStringList>(ALIASKEY,KeyStringList.class);
		
		for (String key : keys) {
//			System.out.println("key :"+ key);
			logger.info("key converted: "+key);
			keyStringList.addKey(key);
		}
		
		storeKeys = xstreamUtil.toXML(keyStringList);

		//FOR DEBUG
//		System.out.println("stored keys: "+storeKeys);

		return storeKeys;
	}
	
	public static List<String> revertListKeyFromStoredXMLString(String storedKeysAsXml){
		
		List<String> listKey = new ArrayList<String>();
		
		XStreamUtil<KeyStringList> xstreamUtil = new XStreamUtil<KeyStringList>(ALIASKEY,KeyStringList.class);
		
		KeyStringList keyStringList = (KeyStringList) xstreamUtil.fromXML(storedKeysAsXml);
		
		for (String key : keyStringList.getListKeys()) {
//			for debug
//			System.out.println("key :"+ key);
			listKey.add(key);
		}

		return listKey;
	}

}
