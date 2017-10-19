package org.gcube.portlets.user.td.gwtservice.shared.tr.resources;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SDMXResourceTD extends ResourceTD {

	private static final long serialVersionUID = -3075957767979840537L;

	private String name;
	private String agency;
	private String primaryMeasure;
	private String resourceUrl;
	private String registryUrl;
	private String type;
	private String version;

	public SDMXResourceTD() {
		super();
	}

	public SDMXResourceTD(String name, String agency, String primaryMeasure, String resourceUrl, String registryUrl,
			String stringValue, String type, String version) {
		super(stringValue);
		this.name = name;
		this.agency = agency;
		this.primaryMeasure = primaryMeasure;
		this.resourceUrl = resourceUrl;
		this.registryUrl = registryUrl;
		this.type = type;
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public String getPrimaryMeasure() {
		return primaryMeasure;
	}

	public void setPrimaryMeasure(String primaryMeasure) {
		this.primaryMeasure = primaryMeasure;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	public String getRegistryUrl() {
		return registryUrl;
	}

	public void setRegistryUrl(String registryUrl) {
		this.registryUrl = registryUrl;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "SDMXResourceTD [name=" + name + ", agency=" + agency + ", primaryMeasure=" + primaryMeasure
				+ ", resourceUrl=" + resourceUrl + ", registryUrl=" + registryUrl + ", type=" + type + ", version="
				+ version + "]";
	}

}
