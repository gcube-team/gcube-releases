package org.gcube.portlets.user.statisticalalgorithmsimporter.server.annotation;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.InputOutputVariables;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class WPSAlgorithmInfo implements Serializable {

	private static final long serialVersionUID = 6333710590825979561L;
	private String description;
	private String algorithmName;
	private String version;
	private ArrayList<InputOutputVariables> inputOutputVariables;

	public WPSAlgorithmInfo() {
		super();
	}

	public WPSAlgorithmInfo(String description, String algorithmName,
			String version, ArrayList<InputOutputVariables> inputOutputVariables) {
		super();
		this.description = description;
		this.algorithmName = algorithmName;
		this.version = version;
		this.inputOutputVariables = inputOutputVariables;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ArrayList<InputOutputVariables> getInputOutputVariables() {
		return inputOutputVariables;
	}

	public void setInputOutputVariables(
			ArrayList<InputOutputVariables> inputOutputVariables) {
		this.inputOutputVariables = inputOutputVariables;
	}

	@Override
	public String toString() {
		return "WPSAlgorithmInfo [description=" + description
				+ ", algorithmName=" + algorithmName + ", version=" + version
				+ ", inputOutputVariables=" + inputOutputVariables + "]";
	}

	

}
