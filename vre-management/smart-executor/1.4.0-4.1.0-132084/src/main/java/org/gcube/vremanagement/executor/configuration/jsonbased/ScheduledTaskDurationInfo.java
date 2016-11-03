/**
 * 
 */
package org.gcube.vremanagement.executor.configuration.jsonbased;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class ScheduledTaskDurationInfo {

	public static final String LAST = "last";
	public static final String AVERAGE = "avg";
	public static final String MIN = "min";
	public static final String MAX = "max";
	
	protected long last;
	protected long avg;
	protected long min;
	protected long max;
	
	public ScheduledTaskDurationInfo(long last, long avg, long min, long max){
		this.last = last;
		this.avg = avg;
		this.min = min;
		this.max = max;
	}

	public ScheduledTaskDurationInfo(JSONObject jsonObject) throws JSONException{
		this.last = jsonObject.getLong(LAST);
		this.avg = jsonObject.getLong(AVERAGE);
		this.min = jsonObject.getLong(MIN);
		this.max = jsonObject.getLong(MAX);
	}
	
	/**
	 * @return the last
	 */
	public long getLast() {
		return last;
	}

	/**
	 * @param last the last to set
	 */
	public void setLast(long last) {
		this.last = last;
	}

	/**
	 * @return the avg
	 */
	public long getAvg() {
		return avg;
	}

	/**
	 * @param avg the avg to set
	 */
	public void setAvg(long avg) {
		this.avg = avg;
	}

	/**
	 * @return the min
	 */
	public long getMin() {
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(long min) {
		this.min = min;
	}

	/**
	 * @return the max
	 */
	public long getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(long max) {
		this.max = max;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put(LAST, last);
		obj.put(AVERAGE, avg);
		obj.put(MIN, min);
		obj.put(MAX, max);
		return obj;
	}
	
}
