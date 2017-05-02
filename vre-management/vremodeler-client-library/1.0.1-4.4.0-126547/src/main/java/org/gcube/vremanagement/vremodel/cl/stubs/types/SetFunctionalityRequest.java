package org.gcube.vremanagement.vremodel.cl.stubs.types;

import static org.gcube.vremanagement.vremodel.cl.Constants.TYPES_NAMESPACE;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="SetFunctionality", namespace=TYPES_NAMESPACE)
public class SetFunctionalityRequest {

	@XmlElement(namespace=TYPES_NAMESPACE)
	List<Integer> functionalityIds;
	
	@XmlElement(name="resourcesDescription", namespace=TYPES_NAMESPACE)
	List<SelectedResourceDescriptionType> resourceDescriptions;

	
	
	public SetFunctionalityRequest(List<Integer> functionalityIds,
			List<SelectedResourceDescriptionType> resourcesDescription) {
		super();
		this.functionalityIds = functionalityIds;
		this.resourceDescriptions = resourcesDescription;
	}

	protected SetFunctionalityRequest() {
		super();
	}

	/**
	 * @return the functionalityIds
	 */
	public List<Integer> functionalityIds() {
		return functionalityIds;
	}

	/**
	 * @param functionalityIds the functionalityIds to set
	 */
	public void functionalityIds(List<Integer> functionalityIds) {
		this.functionalityIds = functionalityIds;
	}

	/**
	 * @return the resourcesDescription
	 */
	public List<SelectedResourceDescriptionType> resourceDescriptions() {
		return resourceDescriptions;
	}

	/**
	 * @param resourcesDescription the resourcesDescription to set
	 */
	public void resourceDescriptions(
			List<SelectedResourceDescriptionType> resourceDescriptions) {
		this.resourceDescriptions = resourceDescriptions;
	}

	
	
}
