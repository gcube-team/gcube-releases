/**
 * 
 */
package org.gcube.common.homelibrary.performance.tool;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class MeasurementData implements Comparable<MeasurementData>{
	
	protected long time;
	protected long value;
	
	/**
	 * @param time the time.
	 * @param value the value.
	 */
	public MeasurementData(long time, long value) {
		this.time = time;
		this.value = value;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * @return the value
	 */
	public long getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(long value) {
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Data [time=");
		builder.append(time);
		builder.append(", value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(MeasurementData o) {
		if (value>o.value) return 1;
		if (value<o.value) return -1;
		return 0;
	}
}
