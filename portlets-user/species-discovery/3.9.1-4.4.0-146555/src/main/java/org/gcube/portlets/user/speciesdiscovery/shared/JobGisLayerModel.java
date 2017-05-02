/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;




/**
 * The Class JobGisLayerModel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 9, 2017
 */
public class JobGisLayerModel implements Serializable, IsSerializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -8982510983374359781L;

	public static final String JOBNAME = "Layer Title";
	public static final String JOBINDENTIFIER = "jobIdentifier";
	public static final String STARTTIME = "Start Time";
	public static final String SUBMITTIME = "Submit Time";
	public static final String PROGRESS = "Progress";
	public static final String ENDTIME = "End Time";
	public static final String ELAPSEDTIME = "Elapsed Time";
	public static final String GISVIEWERAPPLINK = "GisViewerApp Link";
	public static final String LAYERUUID = "Layer UUID";

	public static final String PERCENTAGE = "Percentage";
	public static final String DESCRIPTION = "Description";
	public static final String STATUS = "Status";

	private String jobIdentifier;
	private String jobName;
	private DownloadState downloadState;
	protected Date startTime;
	protected Date submitTime;
	protected Date endTime;
	protected String elapsedTime;
	private String layerDescription;
	private float percentage;
	private long totalPoints;
	private long completedPoints;
	private String layerUUID; //This is the result

	private String gisViewerAppLink;

	/**
	 * Instantiates a new job gis layer model.
	 */
	public JobGisLayerModel() {
	}


	/**
	 * Instantiates a new job gis layer model.
	 *
	 * @param jobIdentifier the job identifier
	 * @param layerTitle the layer title
	 * @param downloadState the download state
	 * @param completedPoints the completed points
	 * @param totalPoints the total points
	 */
	public JobGisLayerModel(String jobIdentifier, String layerTitle, DownloadState downloadState, long completedPoints, long totalPoints) {
		this.jobIdentifier = jobIdentifier;
		this.jobName = layerTitle;
		this.downloadState = downloadState;
		this.completedPoints = completedPoints;
		this.totalPoints = totalPoints;
	}



	/**
	 * Instantiates a new job gis layer model.
	 *
	 * @param jobIdentifier the job identifier
	 * @param jobName the job name
	 * @param downloadState the download state
	 * @param startTime the start time
	 * @param submitTime the submit time
	 * @param endTime the end time
	 * @param elapsedTime the elapsed time
	 * @param layerDescription the layer description
	 * @param completedPoints the completed points
	 * @param totalPoints the total points
	 * @param gisViewerAppLink the gis viewer app link
	 * @param layerUUID the layer uuid
	 */
	public JobGisLayerModel(
		String jobIdentifier, String jobName, DownloadState downloadState,
		Date startTime, Date submitTime, Date endTime, String elapsedTime,
		String layerDescription, long completedPoints,
		long totalPoints, String gisViewerAppLink, String layerUUID) {

		this.jobIdentifier = jobIdentifier;
		this.jobName = jobName;
		this.downloadState = downloadState;
		this.startTime = startTime;
		this.submitTime = submitTime;
		this.endTime = endTime;
		this.elapsedTime = elapsedTime;
		this.layerDescription = layerDescription;
		this.totalPoints = totalPoints;
		this.completedPoints = completedPoints;
		this.gisViewerAppLink = gisViewerAppLink;
		this.layerUUID = layerUUID;
		updatePercentage();
	}


	/**
	 * Update percentage.
	 */
	private void updatePercentage(){
		this.percentage = completedPoints!=0 && totalPoints!=0?completedPoints*100/totalPoints:0;
	}

	/**
	 * Gets the job identifier.
	 *
	 * @return the jobIdentifier
	 */
	public String getJobIdentifier() {

		return jobIdentifier;
	}



	/**
	 * Gets the job name.
	 *
	 * @return the jobName
	 */
	public String getJobName() {

		return jobName;
	}



	/**
	 * Gets the download state.
	 *
	 * @return the downloadState
	 */
	public DownloadState getDownloadState() {

		return downloadState;
	}



	/**
	 * Gets the start time.
	 *
	 * @return the startTime
	 */
	public Date getStartTime() {

		return startTime;
	}



	/**
	 * Gets the submit time.
	 *
	 * @return the submitTime
	 */
	public Date getSubmitTime() {

		return submitTime;
	}



	/**
	 * Gets the end time.
	 *
	 * @return the endTime
	 */
	public Date getEndTime() {

		return endTime;
	}



	/**
	 * Gets the elapsed time.
	 *
	 * @return the elapsedTime
	 */
	public String getElapsedTime() {

		return elapsedTime;
	}



	/**
	 * Gets the layer description.
	 *
	 * @return the layerDescription
	 */
	public String getLayerDescription() {

		return layerDescription;
	}



	/**
	 * Gets the percentage.
	 *
	 * @return the percentage
	 */
	public float getPercentage() {

		return percentage;
	}



	/**
	 * Gets the total points.
	 *
	 * @return the totalPoints
	 */
	public long getTotalPoints() {

		return totalPoints;
	}



	/**
	 * Gets the completed points.
	 *
	 * @return the completedPoints
	 */
	public long getCompletedPoints() {

		return completedPoints;
	}



	/**
	 * Gets the layer uuid.
	 *
	 * @return the layerUUID
	 */
	public String getLayerUUID() {

		return layerUUID;
	}



	/**
	 * Gets the gis viewer app link.
	 *
	 * @return the gisViewerAppLink
	 */
	public String getGisViewerAppLink() {

		return gisViewerAppLink;
	}



	/**
	 * Sets the job identifier.
	 *
	 * @param jobIdentifier the jobIdentifier to set
	 */
	public void setJobIdentifier(String jobIdentifier) {

		this.jobIdentifier = jobIdentifier;
	}



	/**
	 * Sets the job name.
	 *
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {

		this.jobName = jobName;
	}



	/**
	 * Sets the download state.
	 *
	 * @param downloadState the downloadState to set
	 */
	public void setDownloadState(DownloadState downloadState) {

		this.downloadState = downloadState;
	}



	/**
	 * Sets the start time.
	 *
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {

		this.startTime = startTime;
	}



	/**
	 * Sets the submit time.
	 *
	 * @param submitTime the submitTime to set
	 */
	public void setSubmitTime(Date submitTime) {

		this.submitTime = submitTime;
	}



	/**
	 * Sets the end time.
	 *
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {

		this.endTime = endTime;
	}



	/**
	 * Sets the elapsed time.
	 *
	 * @param elapsedTime the elapsedTime to set
	 */
	public void setElapsedTime(String elapsedTime) {

		this.elapsedTime = elapsedTime;
	}



	/**
	 * Sets the layer description.
	 *
	 * @param layerDescription the layerDescription to set
	 */
	public void setLayerDescription(String layerDescription) {

		this.layerDescription = layerDescription;
	}


	/**
	 * Sets the total points.
	 *
	 * @param totalPoints the totalPoints to set
	 */
	public void setTotalPoints(long totalPoints) {

		this.totalPoints = totalPoints;
	}



	/**
	 * Sets the completed points.
	 *
	 * @param completedPoints the completedPoints to set
	 */
	public void setCompletedPoints(long completedPoints) {

		this.completedPoints = completedPoints;
		updatePercentage();
	}



	/**
	 * Sets the layer uuid.
	 *
	 * @param layerUUID the layerUUID to set
	 */
	public void setLayerUUID(String layerUUID) {

		this.layerUUID = layerUUID;
	}



	/**
	 * Sets the gis viewer app link.
	 *
	 * @param gisViewerAppLink the gisViewerAppLink to set
	 */
	public void setGisViewerAppLink(String gisViewerAppLink) {

		this.gisViewerAppLink = gisViewerAppLink;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("JobGisLayerModel [jobIdentifier=");
		builder.append(jobIdentifier);
		builder.append(", jobName=");
		builder.append(jobName);
		builder.append(", downloadState=");
		builder.append(downloadState);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", submitTime=");
		builder.append(submitTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", elapsedTime=");
		builder.append(elapsedTime);
		builder.append(", layerDescription=");
		builder.append(layerDescription);
		builder.append(", percentage=");
		builder.append(percentage);
		builder.append(", totalPoints=");
		builder.append(totalPoints);
		builder.append(", completedPoints=");
		builder.append(completedPoints);
		builder.append(", layerUUID=");
		builder.append(layerUUID);
		builder.append(", gisViewerAppLink=");
		builder.append(gisViewerAppLink);
		builder.append("]");
		return builder.toString();
	}



}
