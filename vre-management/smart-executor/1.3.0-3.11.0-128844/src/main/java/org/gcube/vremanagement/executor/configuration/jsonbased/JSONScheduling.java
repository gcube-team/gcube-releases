/**
 * 
 */
package org.gcube.vremanagement.executor.configuration.jsonbased;

import java.security.InvalidParameterException;
import java.text.ParseException;

import org.gcube.vremanagement.executor.api.types.Scheduling;
import org.gcube.vremanagement.executor.exception.ScopeNotMatchException;
import org.json.JSONException;
import org.json.JSONObject;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
class JSONScheduling extends Scheduling {
	
	protected static Logger logger = LoggerFactory.getLogger(JSONScheduling.class);
	
	public static final String CRON_EXPRESSION = "cronExpression";
	public static final String DELAY = "delay";
	public static final String SCHEDULING_TIMES = "schedulingTimes";
	public static final String FIRST_START_TIME = "firstStartTime";
	public static final String END_TIME = "endTime";
	public static final String PREVIUOS_EXECUTION_MUST_BE_COMPLETED = "previuosExecutionsMustBeCompleted";
	public static final String GLOBAL = "global";
	
	public JSONScheduling(Scheduling scheduling) throws ParseException {
		super();
		if(scheduling==null){
			throw new InvalidParameterException("Scheduling null");
		}
		CronExpression cronExpression = null;
		if(scheduling.getCronExpression()!=null){
			cronExpression = new CronExpression(scheduling.getCronExpression());
		}
		
		init(cronExpression, scheduling.getDelay(), 
				scheduling.getSchedulingTimes(),
				scheduling.getFirtStartTime(), scheduling.getEndTime(), 
				scheduling.mustPreviousExecutionsCompleted(), scheduling.getGlobal());
	}
	
	public JSONScheduling(JSONObject jsonObject) throws JSONException,
			ParseException, ScopeNotMatchException {
		super();

		CronExpression cronExpression = null;
		if (jsonObject.has(CRON_EXPRESSION)) {
			String cronExpressionString = jsonObject.getString(CRON_EXPRESSION);
			cronExpression = new CronExpression(cronExpressionString);
		}

		Integer delay = null;
		if (jsonObject.has(DELAY)) {
			delay = jsonObject.getInt(DELAY);
		}

		Integer schedulingTimes = 0;
		if (jsonObject.has(SCHEDULING_TIMES)) {
			schedulingTimes = jsonObject.getInt(SCHEDULING_TIMES);
		}

		Long firstStartTime = null;
		if (jsonObject.has(FIRST_START_TIME)) {
			firstStartTime = jsonObject.getLong(FIRST_START_TIME);
		}

		Long endTime = null;
		if (jsonObject.has(END_TIME)) {
			endTime = jsonObject.getLong(END_TIME);
		}

		Boolean previuosExecutionsMustBeCompleted = false;
		if (jsonObject.has(PREVIUOS_EXECUTION_MUST_BE_COMPLETED)) {
			previuosExecutionsMustBeCompleted = jsonObject
					.getBoolean(PREVIUOS_EXECUTION_MUST_BE_COMPLETED);
		}
		
		Boolean global = false;
		if (jsonObject.has(GLOBAL)) {
			global = jsonObject
					.getBoolean(PREVIUOS_EXECUTION_MUST_BE_COMPLETED);
		}
		
		init(cronExpression, delay, schedulingTimes.intValue(), firstStartTime,
				endTime, previuosExecutionsMustBeCompleted, global);

	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject obj = new JSONObject();
		if (this.cronExpression != null) {
			obj.put(CRON_EXPRESSION, this.cronExpression);
		}

		if (this.delay != null) {
			obj.put(DELAY, this.delay.intValue());
		}

		obj.put(SCHEDULING_TIMES, this.schedulingTimes);

		if (this.firstStartTime != null) {
			obj.put(FIRST_START_TIME, this.firstStartTime);
		}

		if (this.endTime != null) {
			obj.put(END_TIME, this.endTime);
		}

		obj.put(PREVIUOS_EXECUTION_MUST_BE_COMPLETED, this.previuosExecutionsMustBeCompleted);
		
		obj.put(GLOBAL, global==null ? false : global.booleanValue());
		
		return obj;
	}

}
