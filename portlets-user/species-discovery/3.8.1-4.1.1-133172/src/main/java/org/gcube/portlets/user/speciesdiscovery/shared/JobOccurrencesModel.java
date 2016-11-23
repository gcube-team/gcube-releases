package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class JobOccurrencesModel implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String JOBINDENTIFIER = "jobIdentifier";
	public static final String JOBNAME = "Name";
	public static final String STARTTIME = "Start Time";
	public static final String SUBMITTIME = "Submit Time";
	public static final String PROGRESS = "Progress";
	public static final String ENDTIME = "End Time";
	public static final String ELAPSEDTIME = "Elapsed Time";
	public static final String DATASOURCESASSTRING = "Data Sources";
	public static final String SCIENTIFICNAME = "Scientific Name";
	public static final String PERCENTAGE = "Percentage";
	public static final String DESCRIPTION = "Description";
	public static final String STATUS = "Status";
	public static final String FILEFORMAT = "File format"; //CSV o DARWIN_CORE
	public static final String CSVTYPE = "CSV type"; //STANDARD o OPENMODELLER
	public static final String BYDATASOURCE = "byDataSource";
	public static final String COMPLETEDENTRY = "Completed Entry";
	public static final String ITEMSNUMBER = "Number of items";
	
	private String scientificName;
	private String jobIdentifier;
	private String jobName;
	private DownloadState downloadState;
	protected Date startTime;
	protected Date submitTime;
	protected Date endTime;
	protected String elapsedTime;
	private List<DataSource> dataSources;
	private String description;
	private float percentage;
	private int nodeCompleted;
	
	private int totalOccurrences;
	
	private SaveFileFormat fileFormat;
	private OccurrencesSaveEnum csvType;
	
	private boolean byDataSource;

	public JobOccurrencesModel(){
	}

	/**
	 * 
	 * @param jobIdentifier
	 * @param currentJobName
	 */
	public JobOccurrencesModel(String jobIdentifier, String jobName) {
		setId(jobIdentifier);
		setJobName(jobName);
	}


	public void setJobName(String jobName) {
		this.jobName  = jobName;
		
	}


	/**
	 * USED FROM GET LIST ON SERVER
	 * @param jobIdentifier
	 * @param jobName
	 * @param description
	 * @param state
	 * @param scientificName
	 * @param dataSources
	 * @param submitTime
	 * @param endTime
	 * @param nodeCompleted
	 * @param totalOccurrence
	 */
	public JobOccurrencesModel(String jobIdentifier, String jobName, String description, DownloadState state, String scientificName, List<DataSource> listDataSource, Date submitTime, Date endTime, int nodeCompleted, int totalOccurrence) {
		this(jobIdentifier,jobName);
		setScientificName(scientificName);
		setSubmitTime(submitTime);
		setState(state);
		setDataSources(listDataSource);
		setEndTime(endTime);
		setDescription(description);
		setNodeCompleted(nodeCompleted);
		setTotalOccurrences(totalOccurrence);
		
		setPercentage(nodeCompleted*100/totalOccurrence);
	}
	
	/**
	 * USED FROM CREATE JOB ON CLIENT
	 * @param jobIdentifier
	 * @param jobName2
	 * @param scientificName2
	 * @param dataSourceList
	 * @param fileFormat
	 * @param saveEnum
	 * @param byDataSource
	 */
	public JobOccurrencesModel(String jobIdentifier,String jobName2,String scientificName2, List<DataSource> dataSourceList,SaveFileFormat fileFormat, OccurrencesSaveEnum csvType,boolean byDataSource, int totalOccurrence) {
		this(jobIdentifier,jobName2);
		setScientificName(scientificName2);
		setDataSources(dataSourceList);
		setFileFormat(fileFormat);
		setCsvType(csvType);
		setByDataSource(byDataSource);
		setTotalOccurrences(totalOccurrence);
	}
	
	//By datasource
	public JobOccurrencesModel(String jobIdentifier, String jobName2,
			String scientificName2, List<DataSource> dataSourceList,
			SaveFileFormat fileFormat2, OccurrencesSaveEnum csvType,
			boolean byDataSource2) {
		this(jobIdentifier,jobName2);
		setScientificName(scientificName2);
		setDataSources(dataSourceList);
		setFileFormat(fileFormat2);
		setCsvType(csvType);
		setByDataSource(byDataSource2);
	}

	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
		
	}

	public void setId(String jobId){
		this.jobIdentifier = jobId;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public String getJobIdentifier(){
		return jobIdentifier;
	}
	
	public String getJobName(){
		return jobName;
	}
	
	public String getScientificName(){
		return scientificName;
	}
	
	public void setDataSources(List<DataSource> dataSources){
		this.dataSources = dataSources;
	}
	
	public List<DataSource> getDataSources(){
		return this.dataSources;
	}
	
	public Date getSubmitTime(){
		return submitTime;
	}
	
	public void setSubmitTime(Date startTime){
		this.submitTime = startTime;
	}
	
	public void setEndTime(Date endTime){
		this.endTime = endTime;
	}
	
	public Date getEndTime(){
		return endTime;
	}
	
	public void setPercentage(float percentage){
		this.percentage = percentage;
	}
	public float getPercentage(){
		return percentage;
	}
	
	public void setState(DownloadState state){
		this.downloadState = state;
	}
	
	public DownloadState getDownloadState(){
		return downloadState;
	}
	
	public String getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(String elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	
	public SaveFileFormat getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(SaveFileFormat fileFormat) {
		this.fileFormat = fileFormat;
	}

	public OccurrencesSaveEnum getCsvType() {
		return csvType;
	}

	public void setCsvType(OccurrencesSaveEnum csvType) {
		this.csvType = csvType;
	}

	public boolean isByDataSource() {
		return byDataSource;
	}

	public void setByDataSource(boolean byDataSource) {
		this.byDataSource = byDataSource;
	}
	
	public int getNodeCompleted() {
		return nodeCompleted;
	}

	public void setNodeCompleted(int nodeCompleted) {
		this.nodeCompleted = nodeCompleted;
	}
	
	public List<String> getDataSourcesNameAsString(){
		List<String> listDataSourceName = new ArrayList<String>();
		
		if(dataSources!=null){
			for (DataSource dataSource : dataSources) 
				listDataSourceName.add(dataSource.getName());
		}
		
		return listDataSourceName;
	}

	public int getTotalOccurrences() {
		return totalOccurrences;
	}

	public void setTotalOccurrences(int totalOccurrences) {
		this.totalOccurrences = totalOccurrences;
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
		builder.append("JobOccurrencesModel [scientificName=");
		builder.append(scientificName);
		builder.append(", jobIdentifier=");
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
		builder.append(", dataSources=");
		builder.append(dataSources);
		builder.append(", description=");
		builder.append(description);
		builder.append(", percentage=");
		builder.append(percentage);
		builder.append(", nodeCompleted=");
		builder.append(nodeCompleted);
		builder.append(", totalOccurrences=");
		builder.append(totalOccurrences);
		builder.append(", fileFormat=");
		builder.append(fileFormat);
		builder.append(", csvType=");
		builder.append(csvType);
		builder.append(", byDataSource=");
		builder.append(byDataSource);
		builder.append("]");
		return builder.toString();
	}
	
}
