/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.server.job;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.data.spd.model.service.types.CompleteJobStatus;
import org.gcube.data.spd.model.service.types.JobStatus;
import org.gcube.portlets.user.speciesdiscovery.server.GisInfoServiceImpl;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.GisLayerJobPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.service.SpeciesService;
import org.gcube.portlets.user.speciesdiscovery.server.util.DateUtil;
import org.gcube.portlets.user.speciesdiscovery.shared.DownloadState;
import org.gcube.portlets.user.speciesdiscovery.shared.GisLayerJob;
import org.gcube.portlets.user.speciesdiscovery.shared.JobGisLayerModel;
import org.gcube.portlets.user.speciesdiscovery.shared.OccurrencesJob;


/**
 * The Class GisLinkJobUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 9, 2017
 */
public class GisLayerJobUtil {

	protected static Logger logger = Logger.getLogger(GisLayerJobUtil.class);


	/**
	 * Creates the gis layer job by occurrence keys.
	 *
	 * @param occurrenceKeys the occurrence keys
	 * @param taxonomyService the taxonomy service
	 * @param layerTitle the layer title
	 * @param layerDescr the layer descr
	 * @param author the author
	 * @param credits the credits
	 * @param gisLayerJob the gis layer job
	 * @return the job gis layer model
	 * @throws Exception the exception
	 */
	public static JobGisLayerModel createGisLayerJobByOccurrenceKeys(List<String> occurrenceKeys, SpeciesService taxonomyService, String layerTitle, String layerDescr, String author, String credits, long totalPoints, GisLayerJobPersistence gisLayerJob) throws Exception {

		try {
			logger.trace("Generating Map form Occurrence Keys selected: "+occurrenceKeys.size());
			String jobId = taxonomyService.generateGisLayerFromOccurrenceKeys(occurrenceKeys,layerTitle,layerDescr,author,credits);
			logger.info("generated gis layer jobID: "+jobId);
			Date submitTime = DateUtil.getDateFormat(Calendar.getInstance());
			GisLayerJob gisLJ = new GisLayerJob(jobId, layerTitle, 0, submitTime.getTime(), 0, 0, layerDescr, DownloadState.PENDING.toString(), null, totalPoints, 0);
			gisLayerJob.insert(gisLJ);
			JobGisLayerModel jobModel =  new JobGisLayerModel(jobId, layerTitle, DownloadState.PENDING,null, submitTime, null, null, layerDescr, 0, totalPoints, null, null);
			logger.info("Returning job: "+jobModel);
			return jobModel;
		} catch (Exception e) {
			logger.error("An error occurred creating the map", e);
			throw new Exception(e.getMessage());
		}
	}



	/**
	 * Convert job.
	 *
	 * @param job the job
	 * @param statusResponse the status response
	 * @param gisLayerJobP the gis layer job p
	 * @param aslSession
	 * @return the job gis layer model
	 */
	public static JobGisLayerModel convertJob(GisLayerJob job, CompleteJobStatus statusResponse, GisLayerJobPersistence gisLayerJobP,  SpeciesService taxonomyService, ASLSession aslSession){

	 	//TODO SET END TIME
		JobGisLayerModel jobGisModel;
		DownloadState downloadState = null;
		long endTime = 0;

		JobStatus status = statusResponse.getStatus();
		downloadState = getDownloadState(status);
		logger.trace("gis layer jobId: "+job.getId() +" download state: " + downloadState);

		if(downloadState==null) //Case with exception
			return null;

	    	//SET SUBMIT TIME
		long submitTime = job.getSubmitTime();
		Date submit = DateUtil.millisecondsToDate(submitTime);
//		int completedEntry = 0;
//		if(statusResponse.getCompletedEntries()>0)
//			completedEntry = statusResponse.getCompletedEntries();

		boolean changeStatus = false;

		//if status is completed and job was saved, update status as saved
		if(downloadState.equals(DownloadState.COMPLETED)){
			if(job.getState().compareTo(DownloadState.SAVED.toString())==0){
				downloadState = DownloadState.SAVED;
				changeStatus = true;
			}
		}

		//TODO
		int completedEntries = 0;
		boolean completedEntriesChanged = false;
		if(statusResponse.getCompletedEntries()>0){
			completedEntries = statusResponse.getCompletedEntries();
			job.setCompletedEntries(completedEntries);
			completedEntriesChanged = true;
		}

		jobGisModel = new JobGisLayerModel(job.getId(), job.getName(), downloadState, completedEntries, job.getTotalPoints());
		jobGisModel.setSubmitTime(submit);
		jobGisModel.setLayerUUID(job.getLayerUUID());
		jobGisModel.setGisViewerAppLink(job.getGisViewerAppLink());
		jobGisModel.setCompletedPoints(completedEntries);
		try{

			boolean changeEndTime = false;
			boolean filledAppLink = false;

			//UPDATE END TIME
			if(downloadState.equals(DownloadState.FAILED) || downloadState.equals(DownloadState.COMPLETED)){
				String layerUUID = taxonomyService.getGisLayerResultLinkByJobId(job.getId());
				if(statusResponse.getEndDate()!=null && job.getEndTime()==0){ //UPDATE end time first time only
					logger.trace("UPDATE end time first time only - " + downloadState);
					endTime = statusResponse.getEndDate().getTimeInMillis();
					job.setLayerUUID(layerUUID);
					jobGisModel.setLayerUUID(layerUUID);
					job.setEndTime(endTime);
					changeEndTime = true;
				}

				if(downloadState.equals(DownloadState.COMPLETED) && jobGisModel.getGisViewerAppLink()==null){
					logger.debug("UUID is "+layerUUID);
					String gisLink=GisInfoServiceImpl.getPublicLinkByUUID(layerUUID);
					logger.debug("public link is "+gisLink);
					job.setGisViewerAppLink(gisLink);
					jobGisModel.setGisViewerAppLink(gisLink);
					filledAppLink = true;
				}
			}

			boolean changeStartTime = false;
		   	//SET START TIME
			long startTime = job.getStartTime();
			if(statusResponse.getStartDate()!=null && startTime==0){ //UPDATE start time first time only
				Date start = DateUtil.millisecondsToDate(statusResponse.getStartDate().getTimeInMillis());
				jobGisModel.setStartTime(start);
				changeStartTime = true;
			}

			//UPDATE DAO
			if(completedEntriesChanged || changeStatus || changeEndTime || changeStartTime || filledAppLink){
				job.setState(downloadState.toString());
				gisLayerJobP.update(job);
			}

		}catch (Exception e) {
			logger.error("An error occurred on update the  occurrencesJobDao ", e);
		}


		endTime = job.getEndTime();
		long elapsedTime = 0;

		//SET END TIME, BECAUSE IT IS CHANGED
		if(endTime!=0){
			Date end = DateUtil.millisecondsToDate(endTime);
			jobGisModel.setEndTime(end);
			elapsedTime = endTime;
		}
		else
			elapsedTime = Calendar.getInstance().getTimeInMillis();

		//SET ELAPSED TIME
		jobGisModel.setElapsedTime(DateUtil.getDifference(submitTime, elapsedTime));
		return jobGisModel;
	}

	/**
	 * Gets the download state.
	 *
	 * @param status the status
	 * @return the download state
	 */
	public static DownloadState getDownloadState(JobStatus status){

		if(status!=null){
			switch (status) {
			case COMPLETED:
				return DownloadState.COMPLETED;
			case FAILED:
				return DownloadState.FAILED;
			case PENDING:
				return DownloadState.PENDING;
			case RUNNING:
				return DownloadState.ONGOING;
			default:
				return null;
			}
		}

		return null;
	}


	/**
	 * Delete gis layer job by id.
	 *
	 * @param jobIdentifier the job identifier
	 * @param gisLayerJobDao the gis layer job dao
	 * @return the int
	 */
	public static int deleteGisLayerJobById(String jobIdentifier, GisLayerJobPersistence gisLayerJobDao){
		logger.trace("Delete gis layer job id: " + jobIdentifier);

		try{
			int removed = gisLayerJobDao.deleteItemByIdField(jobIdentifier);
			return 1;

		}catch (Exception e) {
			logger.error("An error occured deleteGisLayerJobById  jobId: " + jobIdentifier + " exception: "+e, e);

		}

		logger.trace("job not exists : " +jobIdentifier);
		return 0;
	}



	/**
	 * @param jobId
	 * @param state
	 * @param gisLayerDAO
	 * @return
	 */
	public static int changetStatusGisLayerJob(String jobId, DownloadState state, GisLayerJobPersistence gisLayerDAO) {
		logger.trace("Changing status for Gis Layer job id: " + jobId);
//		System.out.println("Delete job id: " + jobIdentifier);
		int result = 0;
		try{

			CriteriaBuilder queryBuilder = gisLayerDAO.getCriteriaBuilder();
			CriteriaQuery<Object> cq = queryBuilder.createQuery();
			Predicate pr1 =  queryBuilder.equal(gisLayerDAO.rootFrom(cq).get(OccurrencesJob.ID_FIELD), jobId);
			cq.where(pr1);

			Iterator<GisLayerJob> iterator = gisLayerDAO.executeCriteriaQuery(cq).iterator();
			GisLayerJob job;
			if(iterator.hasNext())
				 job = iterator.next();
			else
				return 0;

			job.setState(state.toString());
			gisLayerDAO.update(job);

		}catch (Exception e) {
			logger.error("An error occured in updating status for jobId: " + jobId + " exception: "+e, e );
		}

		return result;
	}


}
