package org.gcube.portlets.user.speciesdiscovery.server.job;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import org.apache.log4j.Logger;
import org.gcube.data.spd.stubs.types.NodeStatus;
import org.gcube.data.spd.stubs.types.Status;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.TaxonomyJobPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.util.DateUtil;
import org.gcube.portlets.user.speciesdiscovery.shared.DownloadState;
import org.gcube.portlets.user.speciesdiscovery.shared.JobTaxonomyModel;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyJob;

public class TaxonomyJobUtil {

	//STATE RETURNED BY STATUS RESPONSE
	public static final String COMPLETED = "COMPLETED";
	public static final String FAILED = "FAILED";
	public static final String RUNNING = "RUNNING";
	public static final String PENDING = "PENDING";

	protected static Logger logger = Logger.getLogger(TaxonomyJobUtil.class);


	public static JobTaxonomyModel convertJob(TaxonomyJob job, Status statusResponse, TaxonomyJobPersistence speciesJobDao) throws SQLException{
		
    	//TODO SET END TIME 
		JobTaxonomyModel jobSpeciesModel;
		String status = statusResponse.getStatus(); 
		DownloadState downloadState = null;
		long endTime = 0;
		
		downloadState = getDownloadState(status);
		logger.trace("download state: " + downloadState);
		
		
		if(downloadState==null){ //Case with exception
			logger.warn("download state is null, returning");
			return null;
		}
		
		boolean onSaving = true;
		
		//GET CHILDREN
		ArrayList<JobTaxonomyModel> listChildJob = new ArrayList<JobTaxonomyModel>();

		logger.trace("status response subnodes is != null? " + (statusResponse.getSubNodes()!=null));
		if(statusResponse.getSubNodes()!=null){
			logger.trace("subNodes size is: " + statusResponse.getSubNodes().size());
			
			for (NodeStatus nodeStatus : statusResponse.getSubNodes()){
				
				logger.trace("node status " + nodeStatus);
				
				DownloadState downloadStateChildren =  getDownloadState(nodeStatus.getStatus());
				if(!downloadStateChildren.equals(DownloadState.COMPLETED))
					onSaving=false;
				
				listChildJob.add(new JobTaxonomyModel(UUID.randomUUID().toString(), nodeStatus.getScientificName(),downloadStateChildren));
			}
		}else{
			logger.trace("status response subnodes is null");
			onSaving=false;
		}
		boolean changeStatus = false;
		
		//If status of children is completed and job status is not completed (the file is generated) or failed, set download state on saving
		if(onSaving &&(!downloadState.equals(DownloadState.COMPLETED) && !downloadState.equals(DownloadState.FAILED))){
			downloadState = DownloadState.SAVING;
			changeStatus = true;
			
		//if status is completed and job was saved, update status as saved
		}else if(downloadState.equals(DownloadState.COMPLETED)){
			if(job.getState().compareTo(DownloadState.SAVED.toString())==0){
				downloadState = DownloadState.SAVED;
				changeStatus = true;
			}
		}
		
		jobSpeciesModel = new JobTaxonomyModel(job.getId(), job.getDescriptiveName(), downloadState, null, job.getScientificName(), job.getDataSourceName(), job.getRank());
		
		jobSpeciesModel.setListChildJobs(listChildJob);
		
		boolean changeEndTime = false;
		
		//UPDATE END TIME
		if(downloadState.equals(DownloadState.FAILED) || downloadState.equals(DownloadState.COMPLETED)){

//			if(job.getEndTime()==0){ //UPDATE end time first time only
//				
//				logger.trace("UPDATE end time first time only - " + downloadState);
//				endTime = Calendar.getInstance().getTimeInMillis();
//				job.setEndTime(endTime);
//				changeEndTime = true;
////				speciesJobDao.update(job);
//		
//			}
			
//			System.out.println("job "+job);
//			System.out.println("statusResponse.getEndDate() "+statusResponse.getEndDate());
//			System.out.println("job.getEndTime() "+job.getEndTime());
			
			if(statusResponse.getEndDate()!=null && job.getEndTime()==0){ //UPDATE end time first time only
				
				logger.trace("UPDATE end time first time only - " + downloadState);
//				endTime = Calendar.getInstance().getTimeInMillis();
				endTime = statusResponse.getEndDate().getTimeInMillis();
				job.setEndTime(endTime);
				changeEndTime = true;
	//			speciesJobDao.update(job);
	
			}
			
		}
		
		boolean changeStartTime = false;
		
	   	//SET START TIME
		long startTime = job.getStartTime();
		
//		System.out.println("statusResponse.getStartDate(): "+statusResponse.getStartDate());
//		System.out.println("startTime: "+startTime);
		
		if(statusResponse.getStartDate()!=null && startTime==0){ //UPDATE start time first time only
			Date start = DateUtil.millisecondsToDate(statusResponse.getStartDate().getTimeInMillis());
//				jobSpeciesModel.setStartTime(DateUtil.dateToDateFormatString(start));
			jobSpeciesModel.setStartTime(start);
			changeStartTime = true;
		}
		
		
		try{
			//UPDATE DAO
			if(changeStatus || changeEndTime || changeStartTime){
				job.setState(downloadState.toString());
				speciesJobDao.update(job);
			}	
		}catch (Exception e) {
			logger.trace("An error occurred when update dao: ",e);
		}

    	//SET SUBMIT TIME
		long submitTime = job.getSubmitTime();
		Date submit = DateUtil.millisecondsToDate(submitTime);
//		jobSpeciesModel.setStartTime(DateUtil.dateToDateFormatString(start));
		jobSpeciesModel.setSubmitTime(submit);

		
		endTime = job.getEndTime();
		long elapsedTime = 0;
		
		//SET END TIME, BECAUSE IT IS CHANGED
		if(endTime!=0){
			Date end = DateUtil.millisecondsToDate(endTime);
//			jobSpeciesModel.setEndTime(DateUtil.dateToDateFormatString(end));
			jobSpeciesModel.setEndTime(end);
			elapsedTime = endTime;
		}
		else
			elapsedTime = Calendar.getInstance().getTimeInMillis();
		

		//SET ELAPSED TIME
		jobSpeciesModel.setElapsedTime(DateUtil.getDifference(submitTime, elapsedTime));
		
		return jobSpeciesModel;
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
	
	public static int deleteTaxonomyJobById(String jobIdentifier, TaxonomyJobPersistence taxonomyJobDao) throws SQLException{
		logger.trace("Delete taxonomy job id: " + jobIdentifier);
		try{

			int removed = taxonomyJobDao.deleteItemByIdField(jobIdentifier);
			return 1;

		}catch (Exception e) {
			logger.error("An error occured deleteTaxonomyJobById  " + jobIdentifier + " exception: "+e, e);
			e.printStackTrace();
		}
		
		return 0;
	}


	public static int changeStatusTaxonomyJobById(String jobIdentifier,DownloadState state, TaxonomyJobPersistence taxonomyJobDAO) {
		logger.trace("Change status taxonomy job id: " + jobIdentifier);
//		System.out.println("Delete job id: " + jobIdentifier);
		
		int result = 0;
		
		try{
		
			CriteriaBuilder queryBuilder = taxonomyJobDAO.getCriteriaBuilder();
			CriteriaQuery<Object> cq = queryBuilder.createQuery();
			Predicate pr1 =  queryBuilder.equal(taxonomyJobDAO.rootFrom(cq).get(TaxonomyJob.ID_FIELD), jobIdentifier);
			cq.where(pr1);
			
			Iterator<TaxonomyJob> iterator = taxonomyJobDAO.executeCriteriaQuery(cq).iterator();
			
			TaxonomyJob job;
			
			if(iterator.hasNext())
				 job = iterator.next();
			else
				return 0;
			
			job.setState(state.toString());
			
			taxonomyJobDAO.update(job);
		
		}catch (Exception e) {
			logger.error("An error occured in change status  jobId: " + jobIdentifier + " exception: "+e, e );
		}
		
		return result;
	}
}
