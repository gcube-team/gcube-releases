package org.gcube.dataanalysis.ecoengine.models.cores.neuralnetworks.neurosolutions;

// represents a pattern
public class Pattern {
	public double[] input;
	public double[] target;
	public boolean selected;
	// constructor
	public Pattern (double[] temp_inputs, double[] temp_targets) {
		input = new double[temp_inputs.length];
		target = new double[temp_targets.length];
		for ( int i = 0; i < temp_inputs.length; i++) {
			input[i] = temp_inputs[i];
		}
		for ( int i = 0; i < temp_targets.length; i++) {
			target[i] = temp_targets[i];
		}
		selected = false;
	}
}
