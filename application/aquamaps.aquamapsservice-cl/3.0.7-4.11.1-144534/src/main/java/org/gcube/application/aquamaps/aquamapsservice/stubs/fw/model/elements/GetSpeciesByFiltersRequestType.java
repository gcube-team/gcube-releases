package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FilterArray;

@XmlRootElement
public class GetSpeciesByFiltersRequestType {
	
	@XmlElement
	private FilterArray genericSearchFilters;
	@XmlElement	
	private FilterArray specieficFilters;
	@XmlElement
	private PagedRequestSettings pagedRequestSettings;
	@XmlElement
	private int hspen;
	
	
	public GetSpeciesByFiltersRequestType() {
		// TODO Auto-generated constructor stub
	}


	public GetSpeciesByFiltersRequestType(FilterArray genericSearchFilters,
			FilterArray specieficFilters,
			PagedRequestSettings pagedRequestSettings, int hspen) {
		super();
		this.genericSearchFilters = genericSearchFilters;
		this.specieficFilters = specieficFilters;
		this.pagedRequestSettings = pagedRequestSettings;
		this.hspen = hspen;
	}


	/**
	 * @return the genericSearchFilters
	 */
	public FilterArray genericSearchFilters() {
		return genericSearchFilters;
	}


	/**
	 * @param genericSearchFilters the genericSearchFilters to set
	 */
	public void genericSearchFilters(FilterArray genericSearchFilters) {
		this.genericSearchFilters = genericSearchFilters;
	}


	/**
	 * @return the specieficFilters
	 */
	public FilterArray specieficFilters() {
		return specieficFilters;
	}


	/**
	 * @param specieficFilters the specieficFilters to set
	 */
	public void specieficFilters(FilterArray specieficFilters) {
		this.specieficFilters = specieficFilters;
	}


	/**
	 * @return the pagedRequestSettings
	 */
	public PagedRequestSettings pagedRequestSettings() {
		return pagedRequestSettings;
	}


	/**
	 * @param pagedRequestSettings the pagedRequestSettings to set
	 */
	public void pagedRequestSettings(PagedRequestSettings pagedRequestSettings) {
		this.pagedRequestSettings = pagedRequestSettings;
	}


	/**
	 * @return the hspen
	 */
	public int hspen() {
		return hspen;
	}


	/**
	 * @param hspen the hspen to set
	 */
	public void hspen(int hspen) {
		this.hspen = hspen;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GetSpeciesByFiltersRequestType [genericSearchFilters=");
		builder.append(genericSearchFilters);
		builder.append(", specieficFilters=");
		builder.append(specieficFilters);
		builder.append(", pagedRequestSettings=");
		builder.append(pagedRequestSettings);
		builder.append(", hspen=");
		builder.append(hspen);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
