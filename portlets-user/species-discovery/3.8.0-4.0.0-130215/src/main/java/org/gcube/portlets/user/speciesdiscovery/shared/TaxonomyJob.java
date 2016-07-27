package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;

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
public class TaxonomyJob implements Serializable {
	
	
	private static final long serialVersionUID = -38475548097321689L;
	
	public final static String ID_FIELD = "jobId";
	public final static String NAME = "descriptiveName";
	
	public final static String STARTTIME = "startTime";
	public final static String SUBMITTIME = "submitTime";
	public final static String ENDTIME = "endTime";
	
	public final static String DATASOURCE_NAME = "dataSourceName";
	public final static String SCIENTIFICNAME = "scientificName";
	
	public final static String RANK = "rank";

	public static final String TAXONOMYID = "taxonomyId";

	public static final String STATE = "state";
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int internalId;
	
	protected String jobId;

	protected String descriptiveName;
	
	protected long startTime;
	
	protected long submitTime;

	protected long endTime;

	protected String scientificName;

	protected String dataSourceName;
	
	protected String rank;
	
	protected String taxonomyId;
	
	protected String state;
	


	public TaxonomyJob() {
	}
	
	public TaxonomyJob(String id) {
		this.jobId = id;
	}



	/**
	 * 
	 * @param jobId
	 * @param state
	 * @param descriptiveName
	 * @param scientificName
	 * @param dataSourceName
	 * @param rank
	 * @param startTime
	 * @param submitTime
	 * @param endTime
	 * @param taxonomyId
	 */
	public TaxonomyJob(String jobId, String state, String descriptiveName, String scientificName, String dataSourceName, String rank, long startTime, long submitTime, long endTime, String taxonomyId) {
		this.jobId = jobId;
		this.descriptiveName = descriptiveName;
		
		this.startTime = startTime;
		this.submitTime = submitTime;
		this.endTime = endTime;
		
		this.state = state;
		
		this.scientificName = scientificName;
		this.dataSourceName = dataSourceName;
		
		this.rank = rank;
		
		this.taxonomyId = taxonomyId;
	}


	public String getId() {
		return jobId;
	}
	
	public void setId(String id) {
		this.jobId = id;
	}

	public long getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(long startTime) {
		this.submitTime = startTime;
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

	public String getDescriptiveName() {
		return descriptiveName;
	}

	public void setDescriptiveName(String descriptiveName) {
		this.descriptiveName = descriptiveName;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public String getRank() {
		return rank;
	}

	public String getTaxonomyId() {
		return taxonomyId;
	}

	public void setTaxonomyId(String taxonomyId) {
		this.taxonomyId = taxonomyId;
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getStartTime() {
		return startTime;
	}

//	public void setStartTime(long startTime) {
//		this.startTime = startTime;
//	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TaxonomyJob [internalId=");
		builder.append(internalId);
		builder.append(", jobId=");
		builder.append(jobId);
		builder.append(", descriptiveName=");
		builder.append(descriptiveName);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", submitTime=");
		builder.append(submitTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", scientificName=");
		builder.append(scientificName);
		builder.append(", dataSourceName=");
		builder.append(dataSourceName);
		builder.append(", rank=");
		builder.append(rank);
		builder.append(", taxonomyId=");
		builder.append(taxonomyId);
		builder.append(", state=");
		builder.append(state);
		builder.append("]");
		return builder.toString();
	}
	
}
