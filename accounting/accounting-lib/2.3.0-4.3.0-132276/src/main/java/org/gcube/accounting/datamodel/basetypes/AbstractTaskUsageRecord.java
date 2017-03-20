/**
 * 
 */
package org.gcube.accounting.datamodel.basetypes;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
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
public abstract class AbstractTaskUsageRecord extends BasicUsageRecord {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -2208425042550641240L;

	@RequiredField @NotEmpty
	public static final String TASK_ID = "taskId";
	@NotEmptyIfNotNull
	public static final String REF_JOB_ID = "refJobId";
	
	@NotEmptyIfNotNull
	public static final String HOST = "host";
	@NotEmptyIfNotNull
	public static final String REF_HOSTING_NODE_ID = "refHostingNodeId";
	
	@ValidLong
	public static final String TASK_START_TIME = "taskStartTime";
	@ValidLong
	public static final String TASK_END_TIME = "taskEndTime";

	@RequiredField @ComputedField(action=CalculateTaskWallDurationAction.class) @ValidLong
	public static final String WALL_DURATION = "wallDuration";
	
	@NotEmptyIfNotNull
	public static final String INPUT_PARAMETERS = "inputParameters";
	
	public AbstractTaskUsageRecord(){
		super();
	}
	
	public AbstractTaskUsageRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException {
		super(properties);
	}

	private static final String ABSTRACT_TO_REPLACE = "Abstract";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String giveMeRecordType() {
		return AbstractTaskUsageRecord.class.getSimpleName().replace(ABSTRACT_TO_REPLACE, "");
	}
	
	/**
	 * @return the Task Id
	 */
	public String getTaskId() {
		return (String) this.resourceProperties.get(TASK_ID);
	}

	/**
	 * @param taskId Task Id
	 * @throws InvalidValueException if fails
	 */
	public void setTaskId(String taskId) throws InvalidValueException {
		setResourceProperty(TASK_ID, taskId);
	}
	
	/**
	 * @return the Referenced Job Id
	 */
	public String getRefJobId() {
		return (String) this.resourceProperties.get(REF_JOB_ID);
	}

	/**
	 * @param refJobId Referenced Job Id
	 * @throws InvalidValueException if fails
	 */
	public void setRefJobId(String refJobId) throws InvalidValueException {
		setResourceProperty(REF_JOB_ID, refJobId);
	}
	
	public String getHost() {
		return (String) this.resourceProperties.get(HOST);
	}

	public void setHost(String host) throws InvalidValueException {
		setResourceProperty(HOST, host);
	}

	public String getRefHostingNodeId() {
		return (String) this.resourceProperties.get(REF_HOSTING_NODE_ID);
	}

	public void setRefHostingNodeId(String refHostingNodeId) throws InvalidValueException {
		setResourceProperty(REF_HOSTING_NODE_ID, refHostingNodeId);
	}

	public Calendar getTaskStartTime() {
		long millis = (Long) this.resourceProperties.get(TASK_START_TIME);
		return timestampToCalendar(millis);
	}

	public void setTaskStartTime(Calendar startTime) throws InvalidValueException {
		setResourceProperty(TASK_START_TIME, startTime.getTimeInMillis());
	}

	public Calendar getTaskEndTime() {
		long millis = (Long) this.resourceProperties.get(TASK_END_TIME);
		return timestampToCalendar(millis);
	}

	public void setTaskEndTime(Calendar endTime) throws InvalidValueException {
		setResourceProperty(TASK_END_TIME, endTime.getTimeInMillis());
	}
	
	protected long calculateWallDuration() throws InvalidValueException {
		try {
			long endTime = (Long) this.resourceProperties.get(TASK_END_TIME);
			long startTime = (Long) this.resourceProperties.get(TASK_START_TIME);
			long wallDuration = endTime - startTime;
			return wallDuration;
		}catch(Exception e){
			throw new InvalidValueException(String.format("To calculate Wall Duration both %s and %s must be set", 
					TASK_START_TIME, TASK_END_TIME), e);
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
	
	@SuppressWarnings("unchecked")
	public Map<String, Serializable> getInputParameters(){
		return (HashMap<String, Serializable>) getResourceProperty(INPUT_PARAMETERS);
	}
	
	public void setInputParameters(HashMap<String, Serializable> inputParameters) throws InvalidValueException{
		setResourceProperty(INPUT_PARAMETERS, inputParameters);
	}
	
}
