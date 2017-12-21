package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.descriptor;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SAIDescriptor implements Serializable {

	private static final long serialVersionUID = 8083363401840308985L;
	private PoolManagerConfig poolManagerConfig;
	private String remoteTemplateFile;
	private ArrayList<ProjectSetup> availableProjectConfigurations;

	public SAIDescriptor() {
		super();
	}

	public SAIDescriptor(PoolManagerConfig poolManagerConfig, String remoteTemplateFile,
			ArrayList<ProjectSetup> availableProjectConfigurations) {
		super();
		this.poolManagerConfig = poolManagerConfig;
		this.remoteTemplateFile = remoteTemplateFile;
		this.availableProjectConfigurations = availableProjectConfigurations;

	}

	public PoolManagerConfig getPoolManagerConfig() {
		return poolManagerConfig;
	}

	public void setPoolManagerConfig(PoolManagerConfig poolManagerConfig) {
		this.poolManagerConfig = poolManagerConfig;
	}

	public String getRemoteTemplateFile() {
		return remoteTemplateFile;
	}

	public void setRemoteTemplateFile(String remoteTemplateFile) {
		this.remoteTemplateFile = remoteTemplateFile;
	}

	public ArrayList<ProjectSetup> getAvailableProjectConfigurations() {
		return availableProjectConfigurations;
	}

	public void setAvailableProjectConfigurations(ArrayList<ProjectSetup> availableProjectConfigurations) {
		this.availableProjectConfigurations = availableProjectConfigurations;
	}

	@Override
	public String toString() {
		return "SAIDescriptor [poolManagerConfig=" + poolManagerConfig + ", remoteTemplateFile=" + remoteTemplateFile
				+ ", availableProjectConfigurations=" + availableProjectConfigurations + "]";
	}

}
