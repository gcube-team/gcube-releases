package org.gcube.resources.federation.fhnmanager.api.type;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * This class 
 * @author Gabriele Giammatteo
 *
 */
@XmlRootElement
public class NodeTemplate extends FHNResource implements Serializable {

	private URL script;
	
	private String osTemplateId;
	
	// an id for this OSTemplate
	//private String id;

	// the name of the operating system
	private String os;

	// the version of the operating system
	private String osVersion;

	// the name of the image
	private String name;

	// a description for this image
	private String description;

	// the version of the image
	private String version;

	// the size of the disk
	private Long diskSize;

	private Map<String, String> healthCheckMethods;
	
	private ResourceReference<ServiceProfile> serviceProfile;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Long getDiskSize() {
		return diskSize;
	}

	public void setDiskSize(Long diskSize) {
		this.diskSize = diskSize;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public URL getScript() {
		return script;
	}

	public void setScript(URL script) {
		this.script = script;
	}

	public String getOsTemplateId() {
		return osTemplateId;
	}

	public void setOsTemplateId(String osTemplateId) {
		this.osTemplateId = osTemplateId;
	}

	public ResourceReference<ServiceProfile> getServiceProfile() {
		return serviceProfile;
	}

	public void setServiceProfile(ResourceReference<ServiceProfile> serviceProfile) {
		this.serviceProfile = serviceProfile;
	}

}
