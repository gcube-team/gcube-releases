/**
 * 
 */
package org.gcube.common.homelibrary.performance.tool;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class MeasurementChannel {
	
	protected String name;
	protected List<MeasurementData> data;
	protected long startTime = 0;
	protected MeasurementSession session;
	
	
	/**
	 * @param name
	 */
	protected MeasurementChannel(String name, MeasurementSession session) {
		this.name = name;
		this.session = session;
		data = new LinkedList<MeasurementData>();
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the data
	 */
	public List<MeasurementData> getData() {
		return data;
	}
	
	/**
	 * 
	 */
	public void startMeasure()
	{
		startTime = System.nanoTime();
	}
	
	/**
	 * 
	 */
	public void stopMeasure()
	{
		long stopTime = System.nanoTime();
		long time = stopTime-startTime;
		MeasurementData data = new MeasurementData(time, session.getCurrentValue());
		addData(data);
		startTime = 0;
		session.addData(name, data);
	}
	
	/**
	 * @param data data to add.
	 */
	public void addData(MeasurementData data)
	{
		this.data.add(data);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Channel [data=");
		builder.append(data);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
