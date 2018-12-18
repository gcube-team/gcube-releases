/**
 * 
 */
package org.gcube.vremanagement.executor.api.types;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.vremanagement.executor.json.SEMapper;
import org.gcube.vremanagement.executor.utils.ObjectCompare;
import org.quartz.CronExpression;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property=SEMapper.CLASS_PROPERTY)
public class Scheduling implements Comparable<Scheduling> {
	
	@Deprecated
	public static final String CLASS_PROPERTY = "@class";
	
	/**
	 * CRON like expression for a repetitive task.
	 * This field is not valid when using delay
	 */
	@XmlElement
	@JsonProperty
	protected String cronExpression;
	
	/**
	 * Delay between subsequent execution in seconds.
	 * This field is not valid when using cronExpression
	 */
	@XmlElement
	@JsonProperty
	protected Integer delay;
	
	/**
	 * Indicates the number of times the scheduling pattern must be applied.
	 * 0 means indefinitely.
	 */
	@XmlElement
	@JsonProperty
	protected int schedulingTimes;
	
	/**
	 * The first instant when the scheduling can start
	 */
	@XmlElement
	@JsonProperty
	protected Long firstStartTime; // O or null means immediately
	
	/**
	 * Time at which the Trigger will no longer fire even if it's schedule 
	 * has remaining repeats.
	 */
	@XmlElement
	@JsonProperty
	protected Long endTime; // O or null means never
	
	/**
	 * When using cronExpression run the subsequent task only if the previous 
	 * are terminated otherwise this execution is discarded and the subsequent 
	 * execution will start when fired by the the next scheduling.
	 * The discarded execution is counted in the total number of executions
	 * happened.
	 */
	@XmlElement
	@JsonProperty
	protected boolean previuosExecutionsMustBeCompleted;
	
	/**
	 * Indicate if the Scheduled Task has to be take in charge from another
	 * SmartExecutor instance if the initial one die.
	 */
	@XmlElement
	@JsonProperty
	protected boolean global;
	
	
	protected void init(CronExpression cronExpression, Integer delay, int schedulingTimes, Long firstStartTime, Long endTime, boolean previuosExecutionsMustBeCompleted, boolean global){
		if(cronExpression!=null){
			this.cronExpression = cronExpression.getCronExpression();
		}else{
			this.cronExpression = null;
		}
		this.delay = delay;
		this.schedulingTimes = schedulingTimes;
		this.firstStartTime = firstStartTime;
		this.endTime = endTime;
		this.previuosExecutionsMustBeCompleted = previuosExecutionsMustBeCompleted;
		this.global = global;
	}
	
	protected Scheduling(){}
	
	public Scheduling(CronExpression cronExpression) {
		init(cronExpression, null, 0, null, null, false, false);
	}
	
	public Scheduling(CronExpression cronExpression, boolean previuosExecutionsMustBeCompleted) {
		init(cronExpression, null, 0, null, null, previuosExecutionsMustBeCompleted, false);
	}
	
	public Scheduling(CronExpression cronExpression, int schedulingTimes) {
		init(cronExpression, null, schedulingTimes, null, null, false, false);
	}
	
	public Scheduling(CronExpression cronExpression, int schedulingTimes, boolean previuosExecutionsMustBeCompleted ) {
		init(cronExpression, null, schedulingTimes, null, null, previuosExecutionsMustBeCompleted, false);
	}
	
	public Scheduling(CronExpression cronExpression, int schedulingTimes, Calendar firstStartTime, Calendar endTime) {
		init(cronExpression, null, schedulingTimes, firstStartTime.getTimeInMillis(), endTime.getTimeInMillis(), false, false);
	}
	
	public Scheduling(CronExpression cronExpression, int schedulingTimes, Calendar firstStartTime, Calendar endTime, boolean previuosExecutionsMustBeCompleted) {
		init(cronExpression, null, schedulingTimes, firstStartTime.getTimeInMillis(), endTime.getTimeInMillis(), previuosExecutionsMustBeCompleted, false);
	}
	
	public Scheduling(int delay) {
		init(null, delay, 0, null, null, false, false);
	}
	
	public Scheduling(int delay, boolean previuosExecutionsMustBeCompleted) {
		init(null, delay, 0, null, null, previuosExecutionsMustBeCompleted, false);
	}
	
	public Scheduling(int delay, int schedulingTimes) {
		init(null, delay, schedulingTimes, null, null, false, false);
	}
	
	public Scheduling(int delay, int schedulingTimes, boolean previuosExecutionsMustBeCompleted ) {
		init(null, delay, schedulingTimes, null, null, previuosExecutionsMustBeCompleted, false);
	}
	
	public Scheduling(int delay, int schedulingTimes, Calendar firstStartTime, Calendar endTime) {
		init(null, delay, schedulingTimes, firstStartTime.getTimeInMillis(), endTime.getTimeInMillis(), false, false);
	}
	
	public Scheduling(int delay, int schedulingTimes, Calendar firstStartTime, Calendar endTime, boolean previuosExecutionsMustBeCompleted) {
		init(null, delay, schedulingTimes, firstStartTime.getTimeInMillis(), endTime.getTimeInMillis(), previuosExecutionsMustBeCompleted, false);
	}

	/**
	 * @return the cronExpression
	 */
	public String getCronExpression() {
		return cronExpression;
	}

	/**
	 * @return the delay
	 */
	public Integer getDelay() {
		return delay;
	}

	/**
	 * @return the schedulingTimes
	 */
	public int getSchedulingTimes() {
		return schedulingTimes;
	}

	/**
	 * @return the previuosExecutionMustBeCompleted
	 */
	public boolean mustPreviousExecutionsCompleted() {
		return previuosExecutionsMustBeCompleted;
	}

	/**
	 * @return the firstStartTime
	 */
	public Long getFirstStartTime() {
		return firstStartTime;
	}

	/**
	 * @param firstStartTime the firstStartTime to set
	 */
	public void setFirstStartTime(Long firstStartTime) {
		this.firstStartTime = firstStartTime;
	}

	/**
	 * @return the endTime
	 */
	public Long getEndTime() {
		return endTime;
	}
	
	/**
	 * @return the global
	 */
	public Boolean getGlobal() {
		return global;
	}

	/**
	 * @param global the global to set
	 */
	public void setGlobal(Boolean global) {
		this.global = global;
	}
	
	public String toString(){
		return String.format("{"
					+ "cronExpression:%s,"
					+ "delay:%d,"
					+ "schedulingTimes:%d,"
					+ "firstStartTime:%d,"
					+ "endTime:%d,"
					+ "previuosExecutionsMustBeCompleted:%b,"
					+ "global:%b"
				+ "}",
				cronExpression,
				delay,
				schedulingTimes,
				firstStartTime,
				endTime,
				previuosExecutionsMustBeCompleted, 
				global);
	}
	
	/** {@inheritDoc}} */
	@Override
	public int compareTo(Scheduling scheduling) {
		int compareResult = 0;
		
		compareResult = new ObjectCompare<String>().compare(cronExpression, scheduling.cronExpression);
		if(compareResult!=0){
			return compareResult;
		}
		
		compareResult = new ObjectCompare<Integer>().compare(delay,scheduling.delay);
		if(compareResult!=0){
			return compareResult;
		}
		
		compareResult = new ObjectCompare<Integer>().compare(new Integer(schedulingTimes),new Integer(scheduling.schedulingTimes));
		if(compareResult!=0){
			return compareResult;
		}

		compareResult = new ObjectCompare<Long>().compare(firstStartTime,scheduling.firstStartTime);
		if(compareResult!=0){
			return compareResult;
		}
		
		compareResult = new ObjectCompare<Long>().compare(endTime,scheduling.endTime);
		if(compareResult!=0){
			return compareResult;
		}
		
		compareResult = new ObjectCompare<Boolean>().compare(new Boolean(previuosExecutionsMustBeCompleted), new Boolean(scheduling.previuosExecutionsMustBeCompleted));
		if(compareResult!=0){
			return compareResult;
		}
		
		return new ObjectCompare<Boolean>().compare(new Boolean(global), new Boolean(scheduling.global));
		
	}
	
}
