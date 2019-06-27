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
	private String ecologicalEngineJarUrl;
	private String ecologicalEngineSmartExecutorJarUrl;

	private ArrayList<ProjectSetup> availableProjectConfigurations;

	public SAIDescriptor() {
		super();
	}

	public SAIDescriptor(PoolManagerConfig poolManagerConfig, String remoteTemplateFile, String ecologicalEngineJarUrl,
			String ecologicalEngineSmartExecutorJarUrl, ArrayList<ProjectSetup> availableProjectConfigurations) {
		super();
		this.poolManagerConfig = poolManagerConfig;
		this.remoteTemplateFile = remoteTemplateFile;
		this.ecologicalEngineJarUrl = ecologicalEngineJarUrl;
		this.ecologicalEngineSmartExecutorJarUrl = ecologicalEngineSmartExecutorJarUrl;
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

	public String getEcologicalEngineJarUrl() {
		return ecologicalEngineJarUrl;
	}

	public void setEcologicalEngineJarUrl(String ecologicalEngineJarUrl) {
		this.ecologicalEngineJarUrl = ecologicalEngineJarUrl;
	}

	public String getEcologicalEngineSmartExecutorJarUrl() {
		return ecologicalEngineSmartExecutorJarUrl;
	}

	public void setEcologicalEngineSmartExecutorJarUrl(String ecologicalEngineSmartExecutorJarUrl) {
		this.ecologicalEngineSmartExecutorJarUrl = ecologicalEngineSmartExecutorJarUrl;
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
				+ ", ecologicalEngineJarUrl=" + ecologicalEngineJarUrl + ", ecologicalEngineSmartExecutorJarUrl="
				+ ecologicalEngineSmartExecutorJarUrl + ", availableProjectConfigurations="
				+ availableProjectConfigurations + "]";
	}

}
