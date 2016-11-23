package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FieldArray;

@XmlRootElement
public class GetJSONSubmittedByFiltersRequestType {
	@XmlElement
	private FieldArray filters;
	@XmlElement
	private PagedRequestSettings settings;
	
	public GetJSONSubmittedByFiltersRequestType() {
		// TODO Auto-generated constructor stub
	}

	
	
	public GetJSONSubmittedByFiltersRequestType(FieldArray filters,
			PagedRequestSettings settings) {
		super();
		this.filters = filters;
		this.settings = settings;
	}



	/**
	 * @return the filters
	 */
	public FieldArray filters() {
		return filters;
	}

	/**
	 * @param filters the filters to set
	 */
	public void filters(FieldArray filters) {
		this.filters = filters;
	}

	/**
	 * @return the settings
	 */
	public PagedRequestSettings settings() {
		return settings;
	}

	/**
	 * @param settings the settings to set
	 */
	public void settings(PagedRequestSettings settings) {
		this.settings = settings;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GetJSONSubmittedByFiltersRequestType [filters=");
		builder.append(filters);
		builder.append(", settings=");
		builder.append(settings);
		builder.append("]");
		return builder.toString();
	}
	
	
}
