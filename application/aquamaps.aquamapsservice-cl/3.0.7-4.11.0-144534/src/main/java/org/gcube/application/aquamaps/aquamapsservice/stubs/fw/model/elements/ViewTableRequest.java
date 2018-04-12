package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.DM_target_namespace;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FieldArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ViewTableFormat;

@XmlRootElement(namespace=DM_target_namespace,name="viewTableRequestType")
public class ViewTableRequest {

	@XmlElement(namespace=DM_target_namespace)
	private String tablename;
	
	@XmlElement(namespace=DM_target_namespace)
	private PagedRequestSettings settings;
	
	@XmlElement(namespace=DM_target_namespace)
	private FieldArray filter;
	
	@XmlElement(namespace=DM_target_namespace)
	private ViewTableFormat format;
	
	public ViewTableRequest() {
		// TODO Auto-generated constructor stub
	}

	public ViewTableRequest(String tablename, PagedRequestSettings settings,
			FieldArray filter, ViewTableFormat format) {
		super();
		this.tablename = tablename;
		this.settings = settings;
		this.filter = filter;
		this.format = format;
	}

	/**
	 * @return the tablename
	 */
	public String tablename() {
		return tablename;
	}

	/**
	 * @param tablename the tablename to set
	 */
	public void tablename(String tablename) {
		this.tablename = tablename;
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

	/**
	 * @return the filter
	 */
	public FieldArray filter() {
		return filter;
	}

	/**
	 * @param filter the filter to set
	 */
	public void filter(FieldArray filter) {
		this.filter = filter;
	}

	/**
	 * @return the format
	 */
	public ViewTableFormat format() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void format(ViewTableFormat format) {
		this.format = format;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ViewTableRequest [tablename=");
		builder.append(tablename);
		builder.append(", settings=");
		builder.append(settings);
		builder.append(", filter=");
		builder.append(filter);
		builder.append(", format=");
		builder.append(format);
		builder.append("]");
		return builder.toString();
	}
	
	
}
