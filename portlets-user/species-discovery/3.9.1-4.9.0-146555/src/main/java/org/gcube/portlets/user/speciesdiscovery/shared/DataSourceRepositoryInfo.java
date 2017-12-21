package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * The Class DataSourceRepositoryInfo.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 10, 2017
 */
public class DataSourceRepositoryInfo implements IsSerializable, Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -2778021508260771963L;
	private String logoUrl;
	private String pageUrl;
	private Map<String, String> properties;
	private String name;
	private String description;

	/**
	 * Instantiates a new data source repository info.
	 */
	public DataSourceRepositoryInfo() {}

	/**
	 * Instantiates a new data source repository info.
	 *
	 * @param logoUrl the logo url
	 * @param pageUrl the page url
	 * @param properties the properties
	 * @param description the description
	 */
	public DataSourceRepositoryInfo(String logoUrl, String pageUrl, Map<String,String> properties, String description) {
		this.logoUrl = logoUrl;
		this.pageUrl = pageUrl;
		this.properties = properties;
		this.description = description;
	}

	/**
	 * Gets the logo url.
	 *
	 * @return the logo url
	 */
	public String getLogoUrl() {
		return logoUrl;
	}

	/**
	 * Sets the logo url.
	 *
	 * @param logoUrl the new logo url
	 */
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	/**
	 * Gets the page url.
	 *
	 * @return the page url
	 */
	public String getPageUrl() {
		return pageUrl;
	}

	/**
	 * Sets the page url.
	 *
	 * @param pageUrl the new page url
	 */
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	public Map<String, String> getProperties() {
		return properties;
	}

	/**
	 * Sets the properties.
	 *
	 * @param properties the properties
	 */
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
