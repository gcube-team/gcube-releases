/**
 * 
 */
package org.gcube.vremanagement.executor.scheduledtask;


/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ScheduledTaskDurationInfo {

	protected long last;
	protected long avg;
	protected long min;
	protected long max;
	
	protected ScheduledTaskDurationInfo(){}
	
	public ScheduledTaskDurationInfo(long last, long avg, long min, long max){
		this.last = last;
		this.avg = avg;
		this.min = min;
		this.max = max;
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
	
}
