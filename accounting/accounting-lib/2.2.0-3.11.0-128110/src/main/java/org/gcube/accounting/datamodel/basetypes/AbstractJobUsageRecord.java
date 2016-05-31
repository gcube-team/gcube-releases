/**
 * 
 */
package org.gcube.accounting.datamodel.basetypes;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;

import org.gcube.accounting.datamodel.BasicUsageRecord;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.implementation.ComputedField;
import org.gcube.documentstore.records.implementation.RequiredField;
import org.gcube.documentstore.records.implementation.validations.annotations.NotEmpty;
import org.gcube.documentstore.records.implementation.validations.annotations.NotEmptyIfNotNull;
import org.gcube.documentstore.records.implementation.validations.annotations.ValidLong;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public abstract class AbstractJobUsageRecord extends BasicUsageRecord {
	
	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -8648691183939346858L;
	
	@RequiredField @NotEmpty
	public static final String JOB_ID = "jobId";
	
	@NotEmptyIfNotNull
	public static final String JOB_NAME = "jobName";
	@NotEmptyIfNotNull
	public static final String JOB_QUALIFIER = "jobQualifier";
	
	@ValidLong
	public static final String JOB_START_TIME = "jobStartTime";
	@ValidLong
	public static final String JOB_END_TIME = "jobEndTime";
	
	@RequiredField @ComputedField(action=CalculateJobWallDurationAction.class) @ValidLong
	public static final String WALL_DURATION = "wallDuration";
	
	public AbstractJobUsageRecord(){
		super();
	}
	
	public AbstractJobUsageRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException{
		super(properties);
	}
	
	private static final String ABSTRACT_TO_REPLACE = "Abstract";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String giveMeRecordType() {
		return AbstractJobUsageRecord.class.getSimpleName().replace(ABSTRACT_TO_REPLACE, "");
	}
	
	/**
	 * @return the Job Id
	 */
	public String getJobId() {
		return (String) this.resourceProperties.get(JOB_ID);
	}

	/**
	 * @param jobId Job Id
	 * @throws InvalidValueException if fails
	 */
	public void setJobId(String jobId) throws InvalidValueException {
		setResourceProperty(JOB_ID, jobId);
	}
	
	public String getJobQualifier() {
		return (String) this.resourceProperties.get(JOB_QUALIFIER);
	}

	public void setJobQualifier(String jobQualifier) throws InvalidValueException {
		setResourceProperty(JOB_QUALIFIER, jobQualifier);
	}
	
	public String getJobName() {
		return (String) this.resourceProperties.get(JOB_NAME);
	}

	public void setJobName(String jobName) throws InvalidValueException {
		setResourceProperty(JOB_NAME, jobName);
	}
	
	public Calendar getJobStartTime() {
		long millis = (Long) this.resourceProperties.get(JOB_START_TIME);
		return timestampToCalendar(millis);
	}
		
	public void setJobStartTime(Calendar jobStartTime) throws InvalidValueException {
		setResourceProperty(JOB_START_TIME, jobStartTime.getTimeInMillis());
	}
	
	public Calendar getJobEndTime() {
		long millis = (Long) this.resourceProperties.get(JOB_END_TIME);
		return timestampToCalendar(millis);
	}
		
	public void setJobEndTime(Calendar jobEndTime) throws InvalidValueException {
		setResourceProperty(JOB_END_TIME, jobEndTime.getTimeInMillis());
	}
	
	protected long calculateWallDuration() throws InvalidValueException {
		try {
			long endTime = (Long) this.resourceProperties.get(JOB_END_TIME);
			long startTime = (Long) this.resourceProperties.get(JOB_START_TIME);
			long wallDuration = endTime - startTime;
			setResourceProperty(AbstractJobUsageRecord.WALL_DURATION, wallDuration);
			return wallDuration;
		}catch(Exception e){
			throw new InvalidValueException(String.format("To calculate Wall Duration both %s and %s must be set", 
					JOB_START_TIME, JOB_END_TIME), e);
		}
	}
	
	public long getWallDuration() throws InvalidValueException {
		Long wallDuration = (Long) this.resourceProperties.get(WALL_DURATION);
		if(wallDuration == null){
			try {
				wallDuration = calculateWallDuration();
			} catch(InvalidValueException e){
				throw e;
			}
		}
		return wallDuration;
	}
	
}
