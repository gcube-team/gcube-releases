package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */

public class DataSourceRepositoryInfo implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String logoUrl;
	private String pageUrl;
	private Map<String, String> properties;
	private String name;
	private String description;

	public DataSourceRepositoryInfo(String logoUrl, String pageUrl, Map<String,String> properties, String description) {
		this.logoUrl = logoUrl;
		this.pageUrl = pageUrl;
		this.properties = properties;
		this.description = description;
	}
	public DataSourceRepositoryInfo() {}
	
	public String getLogoUrl() {
		return logoUrl;
	}
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}
	public String getPageUrl() {
		return pageUrl;
	}
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	public Map<String, String> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataSourceRepositoryInfo [logoUrl=");
		builder.append(logoUrl);
		builder.append(", pageUrl=");
		builder.append(pageUrl);
		builder.append(", properties=");
		builder.append(properties);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}
	
}
