package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class JobTaxonomyModel implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -8242850808034222413L;

	public static final String JOBINDENTIFIER = "jobIdentifier";
	public static final String JOBNAME = "Name";
	public static final String STARTTIME = "Start Time";
	public static final String SUBMITTIME = "Submit Time";
	public static final String PROGRESS = "Progress";
	public static final String ENDTIME = "End Time";
	public static final String ELAPSEDTIME = "Elapsed Time";
	public static final String INFO = "info";
	public static final String STATUS = "Status";
	public static final String DATASOURCE = "Data Source";
	public static final String SCIENTIFICNAME = "Scientific Name";
	public static final String RANK = "Rank";

	public static final String ITEMSNUMBER = "Number of Items";
	
	private String jobIdentifier;
	private String currentJob;
	private DownloadState downloadState;
	private int failuresNumbers;
	protected Date startTime;
	protected Date submitTime;
	protected Date endTime;
	protected String elapsedTime;
	private List<JobTaxonomyModel> listChildStatus = new ArrayList<JobTaxonomyModel>();
	private String dataSource;
	private String scientificName;
	private String rank;
	

	public JobTaxonomyModel(){
	}

	private JobTaxonomyModel(String jobIdentifier, String currentJobName) {
		setJobsIdentifier(jobIdentifier);
		setCurrentJobName(currentJobName);
	}


	//USED FOR CHILDREN
	
	/**
	 * 
	 * @param jobIdentifier
	 * @param currentJobName
	 * @param state
	 */
	public JobTaxonomyModel(String jobIdentifier, String currentJobName, DownloadState state) {
		this(jobIdentifier,currentJobName);
		setDownloadState(state);
		setScientificName(scientificName);
		setDataSource(dataSource);
	}
	
	//USED FOR GET LIST JOBS
	/**
	 * 
	 * @param jobIdentifier
	 * @param currentJobName
	 * @param state
	 * @param listStatusChild
	 * @param scientificName
	 * @param dataSource
	 */
	public JobTaxonomyModel(String jobIdentifier, String currentJobName, DownloadState state, List<JobTaxonomyModel> listStatusChild, String scientificName, String dataSource, String rank) {
		this(jobIdentifier,currentJobName);
		setDownloadState(state);
		setListChildJobs(listStatusChild);
		setScientificName(scientificName);
		setDataSource(dataSource);
		setRank(rank);
	}

	public void setListChildJobs(List<JobTaxonomyModel> listStatusChild) {
		this.listChildStatus = listStatusChild;
	}

	public String getIdentifier() {
		return jobIdentifier;
	}

	public void setJobsIdentifier(String identifier) {
		this.jobIdentifier = identifier;
	}

	public String getName() {
		return currentJob;
	}

	public void setCurrentJobName(String name) {
		this.currentJob = name;
	}

	public DownloadState getDownloadState() {
		return downloadState;
	}

	public void setDownloadState(DownloadState downloadState) {
		this.downloadState = downloadState;
	}

	public int getFailuresNumbers() {
		return failuresNumbers;
	}

	public void setFailuresNumbers(int failuresNumbers) {
		this.failuresNumbers = failuresNumbers;
	}

	public Date getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Date startTime) {
		this.submitTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(String elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public List<JobTaxonomyModel> getListChildStatus() {
		return listChildStatus;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getScientificName() {
		return scientificName;
	}

	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JobTaxonomyModel [jobIdentifier=");
		builder.append(jobIdentifier);
		builder.append(", currentJob=");
		builder.append(currentJob);
		builder.append(", downloadState=");
		builder.append(downloadState);
		builder.append(", failuresNumbers=");
		builder.append(failuresNumbers);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", submitTime=");
		builder.append(submitTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", elapsedTime=");
		builder.append(elapsedTime);
		builder.append(", listChildStatus=");
		builder.append(listChildStatus);
		builder.append(", dataSource=");
		builder.append(dataSource);
		builder.append(", scientificName=");
		builder.append(scientificName);
		builder.append(", rank=");
		builder.append(rank);
		builder.append("]");
		return builder.toString();
	}

}
