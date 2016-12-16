package org.gcube.common.geoserverinterface.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class CoverageStoreRest implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2781530185311228343L;
	/**
	 * @uml.property  name="name"
	 */
	private String name = "";
	/**
	 * @uml.property  name="type"
	 */
	private String type = "";
	/**
	 * @uml.property  name="enabled"
	 */
	private boolean enabled = false;
	/**
	 * @uml.property  name="url"
	 */
	private String url = "";
	/**
	 * @uml.property  name="coverages"
	 */
	private ArrayList<String> coverages = new ArrayList<String>();
	
	public ArrayList<String> getCoverages() {
		return coverages;
	}
	public void setCoverages(ArrayList<String> coverages) {
		this.coverages = coverages;
	}
	public CoverageStoreRest() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name
	 * @uml.property  name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return
	 * @uml.property  name="type"
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type
	 * @uml.property  name="type"
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return
	 * @uml.property  name="enabled"
	 */
	public boolean isEnabled() {
		return enabled;
	}
	/**
	 * @param enabled
	 * @uml.property  name="enabled"
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	/**
	 * @return
	 * @uml.property  name="url"
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url
	 * @uml.property  name="url"
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	public CoverageStoreRest(String name, String type, boolean enabled,
			String url) {
		super();
		this.name = name;
		this.type = type;
		this.enabled = enabled;
		this.url = url;
	}
}
