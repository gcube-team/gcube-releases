package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.ResourceArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.StringArray;

@XmlRootElement
public class RetrieveMapsByCoverageRequestType {

	@XmlElement
	private StringArray speciesList;
	@XmlElement
	private ResourceArray resourceList;
	@XmlElement
	private boolean includeCustomMaps;
	@XmlElement
	private boolean includeGisLayers;
	
	public RetrieveMapsByCoverageRequestType() {
		// TODO Auto-generated constructor stub
	}


	public RetrieveMapsByCoverageRequestType(StringArray speciesList,
			ResourceArray resourceList, boolean includeCustomMaps,
			boolean includeGisLayers) {
		super();
		this.speciesList = speciesList;
		this.resourceList = resourceList;
		this.includeCustomMaps = includeCustomMaps;
		this.includeGisLayers = includeGisLayers;
	}


	/**
	 * @return the speciesList
	 */
	public StringArray speciesList() {
		return speciesList;
	}


	/**
	 * @param speciesList the speciesList to set
	 */
	public void speciesList(StringArray speciesList) {
		this.speciesList = speciesList;
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


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RetrieveMapsByCoverageRequestType [speciesList=");
		builder.append(speciesList);
		builder.append(", resourceList=");
		builder.append(resourceList);
		builder.append(", includeCustomMaps=");
		builder.append(includeCustomMaps);
		builder.append(", includeGisLayers=");
		builder.append(includeGisLayers);
 		builder.append("]");
		return builder.toString();
	}

	
	
	
}
