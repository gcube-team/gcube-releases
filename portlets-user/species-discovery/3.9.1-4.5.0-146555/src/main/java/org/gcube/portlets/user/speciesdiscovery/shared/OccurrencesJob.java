package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
@Entity
public class OccurrencesJob implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public final static String ID_FIELD = "id";
	public final static String NAME = "name";
	public final static String DESCRIPTION = "description";
	public final static String STARTTIME = "startTime";
	public final static String SUBMITTIME = "submitTime";
	public final static String ENDTIME = "endTime";
	public final static String SCIENTIFICNAME = "scientificName";
	public static final String STATE = "state";
	public static final String REFSTORAGE = "refStorage";
	public static final String FILEFORMAT = "fileFormat"; //CSV o DARWIN_CORE
	public static final String CSVTYPE = "csvType"; //STANDARD o OPENMODELLER
	public static final String BYDATASOURCE = "byDataSource";
	public static final String EXPECTEDOCCURRENCE = "expectedOccurrence";
	public static final String RESULTROW_KEYS_AS_XML = "resultRowKeysAsXml";
	
//	//@DatabaseField(id = true, columnName = ID_FIELD)
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int internalId;
	
	protected String id;
	protected String name;
	protected long startTime;
	protected long submitTime;
	protected long endTime;
	protected String scientificName;
	protected String resultRowKeysAsXml;
	protected String description;
	protected List<DataSource> dataSources;
	protected String state;
	protected String refStorage;
	private String fileFormat;
	private String csvType;
	private boolean byDataSource;
	private int expectedOccurrence;
	
	
	public OccurrencesJob() {
	}
	
	public OccurrencesJob(String id) {
		this.id = id;
	}


	/**
	 * 
	 * @param jobId
	 * @param jobName
	 * @param jobDescription
	 * @param scientificName
	 * @param dataSourcesAsXml
	 * @param state
	 * @param storage
	 * @param submitTime
	 * @param endTime
	 * @param fileFormat
	 * @param csvType
	 * @param isByDataSource
	 * @param resultRowKeys
	 * @param expectedOccurrence
	 */
	public OccurrencesJob(String jobId, String jobName, String jobDescription, String scientificName, List<DataSource> dataSources, String state, String storage, long submitTime, long startTime, long endTime, String fileFormat, String csvType, boolean isByDataSource, String resultRowKeys, int expectedOccurrence) {
		this.id = jobId;
		this.name = jobName;
		this.startTime = startTime;
		this.submitTime = submitTime;
		this.endTime = endTime;
		this.state = state;
		this.dataSources = dataSources;
		this.scientificName = scientificName;
		this.description = jobDescription;
		this.csvType = csvType;
		this.fileFormat = fileFormat;
		this.byDataSource = isByDataSource;
		this.resultRowKeysAsXml = resultRowKeys;
		this.expectedOccurrence = expectedOccurrence;
	}


	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public long getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(long submitTime) {
		this.submitTime = submitTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getScientificName() {
		return scientificName;
	}

	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getRefStorage() {
		return refStorage;
	}

	public void setRefStorage(String refStorage) {
		this.refStorage = refStorage;
	}

	public String getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	public String getCsvType() {
		return csvType;
	}

	public void setCsvType(String csvType) {
		this.csvType = csvType;
	}

	public boolean isByDataSource() {
		return byDataSource;
	}

	public void setByDataSource(boolean byDataSource) {
		this.byDataSource = byDataSource;
	}

	public String getResultRowKeysAsXml() {
		return resultRowKeysAsXml;
	}

	public void setResultRowKeysAsXml(String resultRowKeysAsXml) {
		this.resultRowKeysAsXml = resultRowKeysAsXml;
	}

	public int getExpectedOccurrence() {
		return expectedOccurrence;
	}

	public void setExpectedOccurrence(int expectedOccurrence) {
		this.expectedOccurrence = expectedOccurrence;
	}

	public List<DataSource> getDataSources() {
		return dataSources;
	}

	public void setDataSources(List<DataSource> dataSources) {
		this.dataSources = dataSources;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OccurrencesJob [internalId=");
		builder.append(internalId);
		builder.append(", id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", submitTime=");
		builder.append(submitTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", scientificName=");
		builder.append(scientificName);
		builder.append(", resultRowKeysAsXml=");
		builder.append(resultRowKeysAsXml);
		builder.append(", description=");
		builder.append(description);
		builder.append(", dataSources=");
		builder.append(dataSources);
		builder.append(", state=");
		builder.append(state);
		builder.append(", refStorage=");
		builder.append(refStorage);
		builder.append(", fileFormat=");
		builder.append(fileFormat);
		builder.append(", csvType=");
		builder.append(csvType);
		builder.append(", byDataSource=");
		builder.append(byDataSource);
		builder.append(", expectedOccurrence=");
		builder.append(expectedOccurrence);
		builder.append("]");
		return builder.toString();
	}
	
	
}
