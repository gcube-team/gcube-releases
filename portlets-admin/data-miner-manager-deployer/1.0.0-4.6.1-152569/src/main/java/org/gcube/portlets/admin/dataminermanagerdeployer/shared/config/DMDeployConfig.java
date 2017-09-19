package org.gcube.portlets.admin.dataminermanagerdeployer.shared.config;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class DMDeployConfig implements Serializable {

	private static final long serialVersionUID = 4251753074961060428L;
	private String targetVRE;
	private String algorithmPackageURL;
	private String algorithmCategory;
	private String deployType;

	public DMDeployConfig() {
		super();
	}

	public DMDeployConfig(String targetVRE, String algorithmPackageURL, String algorithmCategory, String deployType) {
		super();
		this.targetVRE = targetVRE;
		this.algorithmPackageURL = algorithmPackageURL;
		this.algorithmCategory = algorithmCategory;
		this.deployType = deployType;
	}

	public String getTargetVRE() {
		return targetVRE;
	}

	public void setTargetVRE(String targetVRE) {
		this.targetVRE = targetVRE;
	}

	public String getAlgorithmPackageURL() {
		return algorithmPackageURL;
	}

	public void setAlgorithmPackageURL(String algorithmPackageURL) {
		this.algorithmPackageURL = algorithmPackageURL;
	}

	public String getAlgorithmCategory() {
		return algorithmCategory;
	}

	public void setAlgorithmCategory(String algorithmCategory) {
		this.algorithmCategory = algorithmCategory;
	}

	public String getDeployType() {
		return deployType;
	}

	public void setDeployType(String deployType) {
		this.deployType = deployType;
	}

	@Override
	public String toString() {
		return "DMDeployConfig [targetVRE=" + targetVRE + ", algorithmPackageURL=" + algorithmPackageURL
				+ ", algorithmCategory=" + algorithmCategory + ", deployType=" + deployType + "]";
	}

}
