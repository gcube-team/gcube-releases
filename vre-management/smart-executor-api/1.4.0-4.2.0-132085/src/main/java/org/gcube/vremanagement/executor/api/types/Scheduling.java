/**
 * 
 */
package org.gcube.vremanagement.executor.api.types;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.vremanagement.executor.utils.ObjectCompare;
import org.quartz.CronExpression;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class Scheduling implements Comparable<Scheduling> {
	
	/**
	 * CRON like expression for a repetitive task.
	 * This field is not valid when using delay
	 */
	@XmlElement
	protected String cronExpression;
	
	/**
	 * Delay between subsequent execution in seconds.
	 * This field is not valid when using cronExpression
	 */
	@XmlElement
	protected Integer delay;
	
	/**
	 * Indicates the number of times the scheduling pattern must be applied.
	 * 0 means indefinitely.
	 */
	@XmlElement
	protected int schedulingTimes;
	
	/**
	 * The first instant when the scheduling can start
	 */
	@XmlElement
	protected Long firstStartTime; // O or null means immediately
	
	/**
	 * Time at which the Trigger will no longer fire even if it's schedule 
	 * has remaining repeats.
	 */
	@XmlElement
	protected Long endTime; // O or null means never
	
	/**
	 * When using cronExpression run the subsequent task only if the previous 
	 * are terminated otherwise this execution is discarded and the subsequent 
	 * execution will start when fired by the the next scheduling.
	 * The discarded execution is counted in the total number of executions
	 * happened.
	 */
	@XmlElement
	protected boolean previuosExecutionsMustBeCompleted;
	
	@XmlElement
	/**
	 * Indicate if the Scheduled Task has to be take in charge from another
	 * SmartExecutor instance if the initial one die.
	 */
	protected Boolean global;
	
	protected void init(CronExpression cronExpression, Integer delay, int schedulingTimes, Long firstStartTime, Long endTime, boolean previuosExecutionsMustBeCompleted, Boolean global){
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
		// TODO Set global to false when LaunchParameter.persist will be removed
		init(cronExpression, null, 0, null, null, false, null);
	}
	
	public Scheduling(CronExpression cronExpression, boolean previuosExecutionsMustBeCompleted) {
		// TODO Set global to false when LaunchParameter.persist will be removed
		init(cronExpression, null, 0, null, null, previuosExecutionsMustBeCompleted, null);
	}
	
	public Scheduling(CronExpression cronExpression, int schedulingTimes) {
		// TODO Set global to false when LaunchParameter.persist will be removed
		init(cronExpression, null, schedulingTimes, null, null, false, null);
	}
	
	public Scheduling(CronExpression cronExpression, int schedulingTimes, boolean previuosExecutionsMustBeCompleted ) {
		// TODO Set global to false when LaunchParameter.persist will be removed
		init(cronExpression, null, schedulingTimes, null, null, previuosExecutionsMustBeCompleted, null);
	}
	
	public Scheduling(CronExpression cronExpression, int schedulingTimes, Calendar firstStartTime, Calendar endTime) {
		// TODO Set global to false when LaunchParameter.persist will be removed
		init(cronExpression, null, schedulingTimes, firstStartTime.getTimeInMillis(), endTime.getTimeInMillis(), false, null);
	}
	
	public Scheduling(CronExpression cronExpression, int schedulingTimes, Calendar firstStartTime, Calendar endTime, boolean previuosExecutionsMustBeCompleted) {
		// TODO Set global to false when LaunchParameter.persist will be removed
		init(cronExpression, null, schedulingTimes, firstStartTime.getTimeInMillis(), endTime.getTimeInMillis(), previuosExecutionsMustBeCompleted, null);
	}
	
	public Scheduling(int delay) {
		// TODO Set global to false when LaunchParameter.persist will be removed
		init(null, delay, 0, null, null, false, null);
	}
	
	public Scheduling(int delay, boolean previuosExecutionsMustBeCompleted) {
		// TODO Set global to false when LaunchParameter.persist will be removed
		init(null, delay, 0, null, null, previuosExecutionsMustBeCompleted, null);
	}
	
	public Scheduling(int delay, int schedulingTimes) {
		// TODO Set global to false when LaunchParameter.persist will be removed
		init(null, delay, schedulingTimes, null, null, false, null);
	}
	
	public Scheduling(int delay, int schedulingTimes, boolean previuosExecutionsMustBeCompleted ) {
		// TODO Set global to false when LaunchParameter.persist will be removed
		init(null, delay, schedulingTimes, null, null, previuosExecutionsMustBeCompleted, null);
	}
	
	public Scheduling(int delay, int schedulingTimes, Calendar firstStartTime, Calendar endTime) {
		// TODO Set global to false when LaunchParameter.persist will be removed
		init(null, delay, schedulingTimes, firstStartTime.getTimeInMillis(), endTime.getTimeInMillis(), false, null);
	}
	
	public Scheduling(int delay, int schedulingTimes, Calendar firstStartTime, Calendar endTime, boolean previuosExecutionsMustBeCompleted) {
		// TODO Set global to false when LaunchParameter.persist will be removed
		init(null, delay, schedulingTimes, firstStartTime.getTimeInMillis(), endTime.getTimeInMillis(), previuosExecutionsMustBeCompleted, null);
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
	 * @return the firtStartTime
	 */
	public Long getFirtStartTime() {
		return firstStartTime;
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
		// TODO Remove if when LaunchParameter.persist will be removed
		if(global==null){
			return false;
		}
		return global;
	}

	/**
	 * @param global the global to set
	 */
	public void setGlobal(Boolean global) {
		this.global = global;
	}
	
	public String toString(){
		return String.format("CronExpression %s, Delay %d, SchedulingTimes %d, FirstStartTime %d, EndTime %d, PreviuosExecutionsMustBeCompleted %b, Global %b", 
				cronExpression, delay, schedulingTimes, firstStartTime, 
				endTime, previuosExecutionsMustBeCompleted, global);
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

		return new ObjectCompare<Boolean>().compare(new Boolean(global), new Boolean(scheduling.global));
		
	}
	
}
