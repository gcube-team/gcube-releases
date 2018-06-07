package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.DM_target_namespace;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GetGenerationLiveReportResponseType {
	
	
	@XmlElement
	private double percent;
	@XmlElement
	private String resourceMap;
	@XmlElement
	private String resourceLoad;
	@XmlElement
	private String elaboratedSpecies;
	
	
	public GetGenerationLiveReportResponseType() {
		// TODO Auto-generated constructor stub
	}
	
	
	public GetGenerationLiveReportResponseType(double percent, String resourceMap,
			String resourceLoad, String elaboratedSpecies) {
		super();
		this.percent = percent;
		this.resourceMap = resourceMap;
		this.resourceLoad = resourceLoad;
		this.elaboratedSpecies = elaboratedSpecies;
	}
	/**
	 * @return the percent
	 */
	public double percent() {
		return percent;
	}
	/**
	 * @param percent the percent to set
	 */
	public void percent(double percent) {
		this.percent = percent;
	}
	/**
	 * @return the resourceMap
	 */
	public String resourceMap() {
		return resourceMap;
	}
	/**
	 * @param resourceMap the resourceMap to set
	 */
	public void resourceMap(String resourceMap) {
		this.resourceMap = resourceMap;
	}
	/**
	 * @return the resourceLoad
	 */
	public String resourceLoad() {
		return resourceLoad;
	}
	/**
	 * @param resourceLoad the resourceLoad to set
	 */
	public void resourceLoad(String resourceLoad) {
		this.resourceLoad = resourceLoad;
	}
	/**
	 * @return the elaboratedSpecies
	 */
	public String elaboratedSpecies() {
		return elaboratedSpecies;
	}
	/**
	 * @param elaboratedSpecies the elaboratedSpecies to set
	 */
	public void elaboratedSpecies(String elaboratedSpecies) {
		this.elaboratedSpecies = elaboratedSpecies;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GenerationLiveReport [percent=");
		builder.append(percent);
		builder.append(", resourceMap=");
		builder.append(resourceMap);
		builder.append(", resourceLoad=");
		builder.append(resourceLoad);
		builder.append(", elaboratedSpecies=");
		builder.append(elaboratedSpecies);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}
