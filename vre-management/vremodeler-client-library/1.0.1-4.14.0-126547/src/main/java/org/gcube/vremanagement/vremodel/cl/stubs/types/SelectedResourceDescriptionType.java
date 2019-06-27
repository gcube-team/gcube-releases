package org.gcube.vremanagement.vremodel.cl.stubs.types;

import static org.gcube.vremanagement.vremodel.cl.Constants.TYPES_NAMESPACE;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=TYPES_NAMESPACE)
public class SelectedResourceDescriptionType {

	@XmlElement(namespace=TYPES_NAMESPACE)
	private String descriptionId;
	
	@XmlElement(namespace=TYPES_NAMESPACE)
	private List<String> resourceId;
	/**
	 * @return the descriptionId
	 */
	public String getDescriptionId() {
		return descriptionId;
	}
	/**
	 * @param descriptionId the descriptionId to set
	 */
	public void descriptionId(String descriptionId) {
		this.descriptionId = descriptionId;
	}
	/**
	 * @return the resourceId
	 */
	public List<String> resourceIds() {
		return resourceId;
	}
	/**
	 * @param resourceId the resourceId to set
	 */
	public void resourceIds(List<String> resourceIds) {
		this.resourceId = resourceIds;
	}
	
	
	
}
