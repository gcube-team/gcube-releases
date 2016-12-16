package org.gcube.common.core.resources;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.resources.common.PlatformDescription;
import org.gcube.common.core.resources.runtime.AccessPoint;

/**
 *  Specifies the behavior of a Runtime Resource
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public abstract class GCUBERuntimeResource extends GCUBEResource {

	/**
	 * The type of the resource.
	 */
	public static final String TYPE="RuntimeResource";
	
	/** The description of the instance.*/
	private String description;
	
	/** The identifier of the GHN on which the instance is deployed.*/
	private String ghn;
	
	/** The machine on which the instance is deployed.*/
	private String hostedOn;
	
	/** The status of the resource.*/
	private String status;
	
	/**The name of the instance's service.*/
	private String name;
	
	/** Data specific to the instance's service.*/
	protected List<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
	
	/** The version of the instance's service.*/
	private String version;
	
	private PlatformDescription platform;
	
	private String category;

	/** Creates a new instance. */
	public GCUBERuntimeResource() {this.type = TYPE;this.logger.setPrefix(TYPE);}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the ghn
	 */
	public String getGHN() {
		return ghn;
	}

	/**
	 * @param ghn the ghn to set
	 */
	public void setGHN(String ghn) {
		this.ghn = ghn;
	}

	/**
	 * @return the hostedOn
	 */
	public String getHostedOn() {
		return hostedOn;
	}

	/**
	 * @param hostedOn the hostedOn to set
	 */
	public void setHostedOn(String hostedOn) {
		this.hostedOn = hostedOn;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the accessPoints
	 */
	public List<AccessPoint> getAccessPoints() {
		return accessPoints;
	}

	/**
	 * @param accessPoints the accessPoints to set
	 */
	public void setAccessPoints(List<AccessPoint> accessPoints) {
		this.accessPoints = accessPoints;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the platform
	 */
	public PlatformDescription getPlatform() {
		return platform;
	}

	/**
	 * @param platform the platform to set
	 */
	public void setPlatform(PlatformDescription platform) {
		this.platform = platform;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

}
