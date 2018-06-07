/**
 * 
 */
package org.gcube.common.homelibrary.performance.tool;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class MeasurementSession {
	
	protected String name;
	protected Map<String, MeasurementChannel> channels;
	protected Map<Long, Map<String, MeasurementData>> measureData;
	protected long currentValue;
	
	
	/**
	 * @param name the session name.
	 */
	public MeasurementSession(String name) {
		this.name = name;
		this.channels = new LinkedHashMap<String, MeasurementChannel>();
		this.measureData = new LinkedHashMap<Long, Map<String,MeasurementData>>();
	}
	
	protected long getCurrentValue()
	{
		return currentValue;
	}
	
	protected void addData(String channelName, MeasurementData data)
	{
		Map<String, MeasurementData> measure = getCurrentValueData();
		measure.put(channelName, data);
	}
	
	protected Map<String, MeasurementData> getCurrentValueData()
	{
		Map<String, MeasurementData> measure = null;
		if (!measureData.containsKey(currentValue)){
			measure = new LinkedHashMap<String, MeasurementData>();
			measureData.put(currentValue, measure);
		} else measure = measureData.get(currentValue);
		
		return measure;
	}
	
	/**
	 * @param value the value.
	 */
	public void startMeasure(long value)
	{
		this.currentValue = value;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @param name the channel name.
	 * @return the measure channel.
	 */
	public MeasurementChannel getChannel(String name)
	{
		if (channels.containsKey(name)) return channels.get(name);
		MeasurementChannel channel = new MeasurementChannel(name, this);
		channels.put(name, channel);
		return channel;
	}

	/**
	 * @return the channels
	 */
	public List<MeasurementChannel> getChannels() {
		return new LinkedList<MeasurementChannel>(channels.values());
	}

	/**
	 * @param channels the channels to set
	 */
	public void setChannels(Map<String, MeasurementChannel> channels) {
		this.channels = channels;
	}
	
	/**
	 * @return the session values.
	 */
	public Set<Long> getValues()
	{
		return measureData.keySet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MeasurementSession [channels=");
		builder.append(channels);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}

}
