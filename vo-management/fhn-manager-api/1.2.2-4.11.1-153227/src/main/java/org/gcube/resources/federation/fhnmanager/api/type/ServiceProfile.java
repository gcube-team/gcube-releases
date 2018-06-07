package org.gcube.resources.federation.fhnmanager.api.type;

import java.util.Date;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ServiceProfile extends FHNResource {

	
	private String version;

	private String description;
	
	private String creationDate;
	
	private Long minRam;
	
	private int minCores;
	
	private Long suggestedRam;
	
	private int suggestedCores;

	private Set<ResourceReference<Software>> deployedSoftware;

	public ServiceProfile() {

	}
	
	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<ResourceReference<Software>> getDeployedSoftware() {
		return deployedSoftware;
	}

	@XmlElement(name = "softwarePackage")
	@XmlElementWrapper(name = "deployedSoftware")
	public void setDeployedSoftware(
			Set<ResourceReference<Software>> deployedSoftware) {
		this.deployedSoftware = deployedSoftware;
	}

	public Long getMinRam() {
		return minRam;
	}

	public void setMinRam(Long minRam) {
		this.minRam = minRam;
	}

	public int getMinCores() {
		return minCores;
	}

	public void setMinCores(int minCores) {
		this.minCores = minCores;
	}

	public Long getSuggestedRam() {
		return suggestedRam;
	}

	public void setSuggestedRam(Long suggestedRam) {
		this.suggestedRam = suggestedRam;
	}

	public int getSuggestedCores() {
		return suggestedCores;
	}

	public void setSuggestedCores(int suggestedCores) {
		this.suggestedCores = suggestedCores;
	}
}
