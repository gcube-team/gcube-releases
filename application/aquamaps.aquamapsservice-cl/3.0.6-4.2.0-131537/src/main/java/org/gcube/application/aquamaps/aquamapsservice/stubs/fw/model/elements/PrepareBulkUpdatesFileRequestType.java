package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.ResourceArray;

@XmlRootElement
public class PrepareBulkUpdatesFileRequestType {

	@XmlElement
	private ResourceArray resourceList;
	@XmlElement
	private boolean includeCustomMaps;
	@XmlElement
	private boolean includeGisLayers;
	@XmlElement
	private long fromTime;
	
	public PrepareBulkUpdatesFileRequestType() {
		// TODO Auto-generated constructor stub
	}

	public PrepareBulkUpdatesFileRequestType(ResourceArray resourceList,
			boolean includeCustomMaps, boolean includeGisLayers, long fromTime) {
		super();
		this.resourceList = resourceList;
		this.includeCustomMaps = includeCustomMaps;
		this.includeGisLayers = includeGisLayers;
		this.fromTime = fromTime;
	}

	/**
	 * @return the resourceList
	 */
	public ResourceArray resourceList() {
		return resourceList;
	}

	/**
	 * @param resourceList the resourceList to set
	 */
	public void resourceList(ResourceArray resourceList) {
		this.resourceList = resourceList;
	}

	/**
	 * @return the includeCustomMaps
	 */
	public boolean includeCustomMaps() {
		return includeCustomMaps;
	}

	/**
	 * @param includeCustomMaps the includeCustomMaps to set
	 */
	public void includeCustomMaps(boolean includeCustomMaps) {
		this.includeCustomMaps = includeCustomMaps;
	}

	/**
	 * @return the includeGisLayers
	 */
	public boolean includeGisLayers() {
		return includeGisLayers;
	}

	/**
	 * @param includeGisLayers the includeGisLayers to set
	 */
	public void includeGisLayers(boolean includeGisLayers) {
		this.includeGisLayers = includeGisLayers;
	}

	/**
	 * @return the fromTime
	 */
	public long fromTime() {
		return fromTime;
	}

	/**
	 * @param fromTime the fromTime to set
	 */
	public void fromTime(long fromTime) {
		this.fromTime = fromTime;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RetrieveMapsUpdatesRequestType [resourceList=");
		builder.append(resourceList);
		builder.append(", includeCustomMaps=");
		builder.append(includeCustomMaps);
		builder.append(", includeGisLayers=");
		builder.append(includeGisLayers);
		builder.append(", fromTime=");
		builder.append(fromTime);
		builder.append("]");
		return builder.toString();
	}
	
	
}
