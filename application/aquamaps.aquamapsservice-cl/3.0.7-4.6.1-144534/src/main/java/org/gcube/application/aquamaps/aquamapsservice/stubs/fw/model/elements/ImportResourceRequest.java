package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.DM_target_namespace;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.ExportCSVSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;


@XmlRootElement(namespace=DM_target_namespace, name="importResourceRequestType")
public class ImportResourceRequest {
	
	@XmlElement(namespace=DM_target_namespace)
	private String rsLocator;
	@XmlElement(namespace=DM_target_namespace)
	private ExportCSVSettings csvSettings;
	@XmlElement(namespace=DM_target_namespace)
	private String user;
	@XmlElement(namespace=DM_target_namespace)
	private ResourceType resourceType;
	
	public ImportResourceRequest() {
		// TODO Auto-generated constructor stub
	}

	public ImportResourceRequest(String rsLocator, ExportCSVSettings csvSettings,
			String user, ResourceType resourceType) {
		super();
		this.rsLocator = rsLocator;
		this.csvSettings = csvSettings;
		this.user = user;
		this.resourceType = resourceType;
	}

	/**
	 * @return the rsLocator
	 */
	public String rsLocator() {
		return rsLocator;
	}

	/**
	 * @param rsLocator the rsLocator to set
	 */
	public void rsLocator(String rsLocator) {
		this.rsLocator = rsLocator;
	}

	/**
	 * @return the csvSettings
	 */
	public ExportCSVSettings csvSettings() {
		return csvSettings;
	}

	/**
	 * @param csvSettings the csvSettings to set
	 */
	public void csvSettings(ExportCSVSettings csvSettings) {
		this.csvSettings = csvSettings;
	}

	/**
	 * @return the user
	 */
	public String user() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void user(String user) {
		this.user = user;
	}

	/**
	 * @return the resourceType
	 */
	public ResourceType resourceType() {
		return resourceType;
	}

	/**
	 * @param resourceType the resourceType to set
	 */
	public void resourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ImportResourceRequest [rsLocator=");
		builder.append(rsLocator);
		builder.append(", csvSettings=");
		builder.append(csvSettings);
		builder.append(", user=");
		builder.append(user);
		builder.append(", resourceType=");
		builder.append(resourceType);
		builder.append("]");
		return builder.toString();
	}
	
	
}
