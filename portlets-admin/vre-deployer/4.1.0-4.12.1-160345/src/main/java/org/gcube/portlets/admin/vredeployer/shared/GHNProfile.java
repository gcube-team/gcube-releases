package org.gcube.portlets.admin.vredeployer.shared;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class GHNProfile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String host;
	private List<RunningInstance> runningInstances;
	private boolean security;
	private GHNMemory memory;
	private GHNSite site;
	private List<String> libraries;
	private boolean isSelected;
	public GHNProfile() {
		super();
	}
	public GHNProfile(String id, String host,
			List<RunningInstance> runningInstances, boolean security,
			GHNMemory memory, GHNSite site,
			List<String> libraries, boolean isSelected) {
		super();
		this.id = id;
		this.host = host;
		this.runningInstances = runningInstances;
		this.security = security;
		this.memory = memory;
		this.site = site;
		this.libraries = libraries;
		this.isSelected = isSelected;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public List<RunningInstance> getRunningInstances() {
		return runningInstances;
	}
	public void setRunningInstances(List<RunningInstance> runningInstances) {
		this.runningInstances = runningInstances;
	}
	public boolean isSecure() {
		return security;
	}
	public void setSecurity(Boolean securityEnabled) {
		this.security = securityEnabled;
	}
	public GHNMemory getMemory() {
		return memory;
	}
	public void setMemory(GHNMemory memory) {
		this.memory = memory;
	}
	public GHNSite getSite() {
		return site;
	}
	public void setSite(GHNSite site) {
		this.site = site;
	}
	public List<String> getLibraries() {
		return libraries;
	}
	public void setLibraries(List<String> libraries) {
		this.libraries = libraries;
	}

	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
