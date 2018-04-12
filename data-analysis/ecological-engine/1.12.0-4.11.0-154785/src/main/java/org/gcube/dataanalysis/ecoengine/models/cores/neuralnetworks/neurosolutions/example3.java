package org.gcube.dataanalysis.ecoengine.models.cores.neuralnetworks.neurosolutions;

import java.io.*;
  
public class example3 {
	public static void main(String args[]) {

		Randomizer randomizer = new Randomizer();

		// create a multilayer perceptron with three layers:
		// one input layer with two units; one hidden layer 
		// with three neurons, using tanh function;
		// one output layer with one neuron using linear function.
		// except for noofneurons, all parameters for the input layer
		// are ineffectual.
		
		int[] noofneurons = {2,3,1};
		
		double[] learnratecoeff = {1, 1, 1};
		char[] axonfamily = {'t', 't', 'l'};
		double[] momentumrate = {0, .4, .4};
		double[] flatness = {1, 1, 1};
		
		System.out.println("Creating the net");
		
		NeuralNet mynet = new NeuralNet(noofneurons, learnratecoeff, axonfamily, momentumrate, flatness, randomizer);

		// train the net using incremental training
		System.out.println("Beginning incremental training");
		Pattern pattern;
		double[] inputs = new double[2];
		double[] outputs = new double[1];
		for (int i = 0; i < 3000; i++) {
			inputs[0] = randomizer.Uniform(-1,1);
			inputs[1] = randomizer.Uniform(-1,1);
			outputs[0] = Math.sin(inputs[0] + inputs[1]);
			pattern = new Pattern(inputs, outputs);
			mynet.IncrementalTrain(.2, pattern);
		}
		System.out.println("Training is over");
		
		// test it
		inputs[0] = -0.5;
		inputs[1] = 0.3;
		System.out.println("Feeding the net");
		outputs = mynet.Output(inputs);
		System.out.println("Sin ( -0.5 + 0.3 ) = "+outputs[0]);
		
		mynet = null;
	}
}
