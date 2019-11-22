package org.gcube.data.analysis.dataminermanagercl.server.dmservice.wps;

import java.io.Serializable;
import java.util.Arrays;

import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class ProcessInformations implements Serializable {

	private static final long serialVersionUID = 4729933672312944832L;
	private ProcessDescriptionType processDescription;
	private InputDescriptionType[] inputs;
	private OutputDescriptionType[] outputs;

	public ProcessInformations(ProcessDescriptionType processDescription) {
		super();
		this.processDescription = processDescription;
		this.inputs = new InputDescriptionType[0];
		this.outputs = new OutputDescriptionType[0];
	}

	public ProcessInformations(ProcessDescriptionType processDescription,
			InputDescriptionType[] inputs, OutputDescriptionType[] outputs) {
		super();
		this.processDescription = processDescription;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	public ProcessDescriptionType getProcessDescription() {
		return processDescription;
	}

	public void setProcessDescription(
			ProcessDescriptionType processDescription) {
		this.processDescription = processDescription;
	}

	public InputDescriptionType[] getInputs() {
		return inputs;
	}

	public void setInputs(InputDescriptionType[] inputs) {
		this.inputs = inputs;
	}

	public OutputDescriptionType[] getOutputs() {
		return outputs;
	}

	public void setOutputs(OutputDescriptionType[] outputs) {
		this.outputs = outputs;
	}

	@Override
	public String toString() {
		return "ProcessInformations [processDescription="
				+ processDescription + ", inputs="
				+ Arrays.toString(inputs) + ", outputs="
				+ Arrays.toString(outputs) + "]";
	}

}