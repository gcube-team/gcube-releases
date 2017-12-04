package org.gcube.dataanalysis.ecoengine.models.cores.neuralnetworks.neurosolutions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

public class NeuralNet implements Serializable {

	Neuron[] neurons;
	Synapse[] synapses;
	int nolayers; // no of layers, inc. input and output layers
	Layer[] layers;
	private Randomizer randomizer;
	
	// constructor
	// opens the configuration file and creates a net according to it.
	public NeuralNet (String path, Randomizer randomizer) {
		this.randomizer = randomizer;
		LineReader linereader = new LineReader(path);
		while (linereader.NextLineSplitted()){
			// if it declares # of objects, dimension the appropriate array
			if (linereader.column[0].compareTo("#neurons") == 0) { neurons = new Neuron[Integer.parseInt(linereader.column[1])]; }
			if (linereader.column[0].compareTo("#synapses") == 0) { synapses = new Synapse[Integer.parseInt(linereader.column[1])]; }
			// if it represents an input neuron, create a neuron object
			if (linereader.column[0].compareTo("i") == 0) { neurons[Integer.parseInt(linereader.column[1])] = new Neuron(Integer.parseInt(linereader.column[1])); }
			// if it represents a neuron, create a neuron object
			if (linereader.column[0].compareTo("n") == 0) { neurons[Integer.parseInt(linereader.column[1])] = new Neuron( Integer.parseInt(linereader.column[1]), Integer.parseInt(linereader.column[2]), Double.parseDouble(linereader.column[3]), linereader.column[4].charAt(0), Double.parseDouble(linereader.column[5]), Double.parseDouble(linereader.column[6]), randomizer ); }
			// if it represents a synapse, create a synapse object
			if (linereader.column[0].compareTo("s") == 0) { synapses[Integer.parseInt(linereader.column[1])] = 
				new Synapse(
				 	neurons[Integer.parseInt(linereader.column[2])], 
					neurons[Integer.parseInt(linereader.column[3])],
					randomizer
				); }
		}
		linereader = null;
		// first find out how many layers there are
		int temp_maxlayer = 0;
		for (int i = 0; i < neurons.length; i++) {
			if (neurons[i].layer > temp_maxlayer) {temp_maxlayer = neurons[i].layer;}
		}
		nolayers = temp_maxlayer+1;
		// then create layer objects
		layers = new Layer[nolayers];
		for (int i = 0; i < nolayers; i++) {layers[i] = new Layer(i);}
		NeuronsInOut();
	}

	public static double[] preprocessObjects(Object[] vector) {

		double[] out = new double[vector.length];

		for (int i = 0; i < vector.length; i++) {
			double element = 0;
			if (vector[i] != null)
				element = Double.parseDouble("" + vector[i]);
			
			out[i] = element;
		}

		return out;
	}
	
	
	public double[] getNegativeCase() {
		double[] out = new double[0];

		if (topology.length > 0) {
			out = new double[topology[topology.length - 1]];
			for (int i = 0; i < out.length; i++) {
				out[i] = -1f;
			}
		}

		return out;
	}
	
	public double[] getPositiveCase() {
		double[] out = new double[0];

		if (topology.length > 0) {
			out = new double[topology[topology.length - 1]];
			for (int i = 0; i < out.length; i++) {
				out[i] = 1f;
			}
		}

		return out;
	}
	
	public static int[] setupInnerLayers(int... numberOfNeurons) {
		int[] layers = null;

		if (numberOfNeurons.length > 0) {
			layers = new int[numberOfNeurons.length];

			for (int i = 0; i < numberOfNeurons.length; i++) {
				layers[i] = numberOfNeurons[i];
			}
		}
		return layers;
	}
	
	public NeuralNet(int N, int M, int[] t) {
		
		Randomizer randomizer = new Randomizer();
		int[] noofneurons = null;
		double[] learnratecoeff = null;
		char[] axonfamily = null;
		double[] momentumrate = null;
		double[] flatness = null;
		
		int len = 2; 
		if (t!=null){
			len = len+t.length;
		}
			noofneurons = new int[len];
			learnratecoeff = new double[len];
			axonfamily = new char[len];
			momentumrate = new double[len];
			flatness = new double[len];
		
		noofneurons[0] = N;
		learnratecoeff[0]=1;
		axonfamily[0] = 't';
		momentumrate[0] = 0;
		flatness[0] = 1;
		for (int i=1;i<len-1;i++){
			noofneurons[i]=t[i-1];
			learnratecoeff[i]=1;
			axonfamily[i] = 't';
			momentumrate[i] = 0.4;
			flatness[i] = 1;
		}
		
		noofneurons[len-1] = M;
		learnratecoeff[len-1]=1;
		axonfamily[len-1] = 't';
		momentumrate[len-1] = 0.4;
		flatness[len-1] = 1;
		
		init(noofneurons, learnratecoeff, axonfamily, momentumrate, flatness, randomizer);
		
	}
	
	int[] topology;
	
	public void init (int[] noofneurons, double[] learningratecoefficient, char[] axonfamily, double[] momentumrate, double[] axonfuncflatness, Randomizer randomizer) {
		this.randomizer = randomizer;
		this.topology = noofneurons;
		int temp_nooflayers = noofneurons.length;
		nolayers = noofneurons.length;
		// determine the no of neurons and create the array
		int temp_noofneurons = 0;
		for ( int i = 0; i < temp_nooflayers; i++ ) {
			temp_noofneurons += noofneurons[i];
		}
		neurons = new Neuron[temp_noofneurons];
		// determine the no of synapses and create the array
		int temp_noofsynapses = 0;
		for ( int i = 0; i < temp_nooflayers-1; i++ ) {
			temp_noofsynapses += noofneurons[i] * noofneurons[i+1];
		}
		synapses = new Synapse[temp_noofsynapses];
		// instantiate neurons:
		int temp_neuronidcounter = 0;
		// first instantiate input neurons
		for ( int i = 0; i < noofneurons[0]; i++ ) {
			neurons[temp_neuronidcounter] = new Neuron(temp_neuronidcounter);
			temp_neuronidcounter++;
		}
		// then instantiate hidden and output neurons
		for ( int i = 1; i < temp_nooflayers; i++ ) {
			for ( int j = 0; j < noofneurons[i]; j++ ) {
				neurons[temp_neuronidcounter] = new Neuron(temp_neuronidcounter, i, axonfuncflatness[i], axonfamily[i], momentumrate[i], learningratecoefficient[i], randomizer);
				temp_neuronidcounter++;
			}
		}
		// then create layer objects
		layers = new Layer[temp_nooflayers];
		for (int i = 0; i < temp_nooflayers; i++) {layers[i] = new Layer(i);}
		// instantiate synapses
		int temp_synapseidcounter = 0;
		for ( int i = 0; i < temp_nooflayers-1; i++) {
			for ( int j = 0; j < layers[i].neurons.length; j++ ) {
				for ( int k = 0; k < layers[i+1].neurons.length; k++ ) {
					synapses[temp_synapseidcounter++] = new Synapse(layers[i].neurons[j], layers[i+1].neurons[k], randomizer);
				}
			}
		}
		NeuronsInOut();
	}	
	
	// another constructor. creates a MULTILAYER PERCEPTRON with 
	// given no. of layers, no of neurons, learning rates, momentum parameters,
	// axonfamilies, flatness. except for noofneurons, all parameters are
	// ineffectual for the first layer.
	public NeuralNet (int[] noofneurons, double[] learningratecoefficient, char[] axonfamily, double[] momentumrate, double[] axonfuncflatness, Randomizer randomizer) {
		init( noofneurons, learningratecoefficient, axonfamily, momentumrate, axonfuncflatness, randomizer);
	}	

	// This method is used by constructors only.
	// It determines the incoming and outgoing neurons / synapses for each neuron 
	// and set them in the neuron. This information is to be used later during feed forward and back propagation.
	private void NeuronsInOut () {
		// and then create neuronsin, neuronsout, synapsesin, synapsesout arrays in the neuron objects
		// in order to determine relationships between neurons
		Neuron[] temp_neuronsin;
		Neuron[] temp_neuronsout;
		Synapse[] temp_synapsesin;
		Synapse[] temp_synapsesout;
		
		int incounter; int outcounter;
		for (int i = 0; i < neurons.length; i++) {
			// first determine the dimension of the arrays
			temp_neuronsin = null;
			temp_neuronsout = null;
			incounter = 0; outcounter = 0;
			for (int j = 0; j < synapses.length; j++) {
				if (synapses[j].sourceunit == neurons[i]) {outcounter++;}
				if (synapses[j].targetunit == neurons[i]) {incounter++;}
			}
			temp_neuronsin = new Neuron[incounter];
			temp_synapsesin = new Synapse[incounter];
			temp_neuronsout = new Neuron[outcounter];
			temp_synapsesout = new Synapse[outcounter];
			// then fill each array
			incounter = 0; outcounter = 0;
			for (int j = 0; j < synapses.length; j++) {
				if (synapses[j].sourceunit == neurons[i]) {
					temp_neuronsout[outcounter] = synapses[j].targetunit;
					temp_synapsesout[outcounter++] = synapses[j];
				}
				if (synapses[j].targetunit == neurons[i]) {
					temp_neuronsin[incounter] = synapses[j].sourceunit;
					temp_synapsesin[incounter++] = synapses[j];
				}
			}
			// set them in the neuron
			neurons[i].InsOuts(temp_neuronsin, temp_neuronsout, temp_synapsesin, temp_synapsesout);
		}
	}

	// saves the configuration of the net to a file
	public void SaveConfig (String path) throws IOException {
		File outputFile = new File(path);
		FileWriter out = new FileWriter(outputFile);
		out.write("// Input units:\n");
		// no of neurons
		out.write("#neurons;"+neurons.length+"\n");
		out.write("// type;ID;layer;flatness;axonfamily;momentum;learningrate\n");
		// neurons
		for (int i = 0; i < neurons.length; i++) {
			if (neurons[i].layer == 0) {
				out.write("i;"+i+";0\n");
			}
			else {
				out.write("n;"+i+";"+neurons[i].layer+";"+neurons[i].axonfuncflatness+";"+neurons[i].axonfamily+";"+neurons[i].momentumrate+";"+neurons[i].learningratecoefficient+"\n");
			}
		}
		// synapses
		out.write("#synapses;"+synapses.length+"\n");
		out.write("// type; ID; sourceunit; targetunit\n");
		for (int i = 0; i < synapses.length; i++) {
			out.write("s;"+i+";"+synapses[i].sourceunit.id+";"+synapses[i].targetunit.id+"\n");
		}
		out.close();
		
	}

	// loads weights of the net from a file
	public void LoadWeights (String path) {
		LineReader linereader = new LineReader(path);
		while (linereader.NextLineSplitted()) {
			// if it's a synapse weight 
			if (linereader.column[0].compareTo("w") == 0) { synapses[Integer.parseInt(linereader.column[1])].weight = Double.parseDouble(linereader.column[2]); }
			// if it's a neuron threshold
			if (linereader.column[0].compareTo("t") == 0) { neurons[Integer.parseInt(linereader.column[1])].threshold = Double.parseDouble(linereader.column[2]); }
		}
		linereader = null;
	}
	
	// saves weights to a file
	public void SaveWeights (String path) throws IOException {
		File outputFile = new File(path);
		FileWriter out = new FileWriter(outputFile);
		// first write weight of each synapse
		for (int i = 0; i < synapses.length; i++) {
			out.write("w; "+i+"; "+synapses[i].weight+"\n");
		}
		out.write("\n");
		// then threshold of each neuron
		for (int i = 0; i < neurons.length; i++) {
			out.write("t; "+i+"; "+neurons[i].threshold+"\n");
		}
		out.close();
	}

	// feeds the network forward and updates all the neurons.
	public void FeedForward (double[] inputs) {
		// feed input values
		for (int i = 0; i < layers[0].neurons.length; i++) {
			layers[0].neurons[i].output = inputs[i];
		}
		// begin from the first layer and propagate through layers.
		for (int i = 1; i < nolayers; i++) {
			// update the output of each neuron in this layer
			for (int j = 0; j < layers[i].neurons.length; j++) {
				layers[i].neurons[j].UpdateOutput();
			}
		}
	}
	
	// takes an array of input values, put them to the input neurons, feeds the net forward and returns the outputs of the output layer
	public double[] Output (double[] inputs) {
		FeedForward(inputs);
		double[] tempoutputs = new double[layers[nolayers-1].neurons.length];
		for(int i = 0; i < layers[nolayers-1].neurons.length; i++) {
			tempoutputs[i] = layers[nolayers-1].neurons[i].output;
		}
		return tempoutputs;
	}
	
	// calculates a std error for this net using given cross validation patterns 
	public double CrossValErrorRatio (PatternSet patternset) {
		int noofoutputunits = layers[nolayers-1].neurons.length;
		double[] abserrors = new double[noofoutputunits];
		for ( int i = 0; i < noofoutputunits; i++ ) { abserrors[i] = 0; }
		// calculate avg error for each neuron
		double errorratio = 0;
		double[] temp_output = new double[noofoutputunits];
		for (int j = 0; j < patternset.crossvalpatterns.length; j++) {
			temp_output = Output(patternset.crossvalpatterns[j].input);
			for (int i = 0; i < noofoutputunits; i++) {
				abserrors[i] += Math.abs( temp_output[i] - patternset.crossvalpatterns[j].target[i] );
			}
		}
		for (int i = 0; i < noofoutputunits; i++) {
			abserrors[i] /= patternset.crossvalpatterns.length;
			errorratio += ( abserrors[i] / patternset.crossvaldeviations[i] );
		}
		errorratio /= noofoutputunits;
		return errorratio;
	}

	// calculates a std error for this net using given test patterns
	public double TestErrorRatio (PatternSet patternset) {
		int noofoutputunits = layers[nolayers-1].neurons.length;
		double[] abserrors = new double[noofoutputunits];
		for ( int i = 0; i < noofoutputunits; i++ ) { abserrors[i] = 0; }
		// calculate avg error for each neuron
		double errorratio = 0;
		double[] temp_output = new double[noofoutputunits];
		for (int j = 0; j < patternset.testpatterns.length; j++) {
			temp_output = Output(patternset.testpatterns[j].input);
			for (int i = 0; i < noofoutputunits; i++) {
				abserrors[i] += Math.abs( temp_output[i] - patternset.testpatterns[j].target[i] );
			}
		}
		for (int i = 0; i < noofoutputunits; i++) {
			abserrors[i] /= patternset.testpatterns.length;
			errorratio += ( abserrors[i] / patternset.testdeviations[i] );
		}
		errorratio /= noofoutputunits;
		return errorratio;
	}

	// takes all patterns one by one (with random order) and trains the net
	// using each one.
	public void IncrementalTrainPatterns(Pattern[] patterns, double rate) {
		int patternsnottrained = patterns.length; // no of patterns used
		int patterntotrain;
		int indexofpatterntotrain = -1;
		int counter;
		// turn all "selected" flags off
		for (int i = 0; i < patterns.length; i++) {
			patterns[i].selected = false;
		}
		for (int i = 0; i < patterns.length; i++) {
			patterntotrain = randomizer.random.nextInt(patternsnottrained);
			// find the index of the pattern to train
			counter = 0;
			for (int j = 0; j < patterns.length; j++) {
				if (!patterns[j].selected) {
					if (counter != patterntotrain) {
						counter++;
					}
					else if (counter == patterntotrain) {
						indexofpatterntotrain = j;
						break;
					}
				}
			}
			// train the net using the selected pattern
			IncrementalTrain(rate, patterns[indexofpatterntotrain]);
			patterns[indexofpatterntotrain].selected = true;
			patternsnottrained--;
		}
		
		// turn all "selected" flags off again
		for (int i = 0; i < patterns.length; i++) {
			patterns[i].selected = false;
		}
	}

	// trains the net incrementally.
	public void IncrementalTrain(double rate, Pattern pattern) {
		// feed fw
		FeedForward(pattern.input);
		// train the output layer first
		for (int j = 0; j < layers[nolayers-1].neurons.length; j++) {
			layers[nolayers-1].neurons[j].OutputIncrementalTrain(rate, pattern.target[j]);
		}
		// train hidden layers
		for (int i = nolayers-2; i > 0; i--) {
			for (int j = 0; j < layers[i].neurons.length; j++) {
				layers[i].neurons[j].HiddenIncrementalTrain(rate);
			}
		}
	}
	
	// selects patterns (quantity: nopatterns) randomly and trains the net using those patterns.
	// repeats this until all patterns in the pattern array have been used for training
	public void MinibatchTrainPatterns(Pattern[] patterns, double rate, int nopatterns) {
		int patternsnottrained = patterns.length; // no of patterns used
		if (nopatterns > patterns.length) {nopatterns = patterns.length;}
		if (nopatterns < 1) {nopatterns = 1;}
		int patterntotrain;
		int noofpatternsselected;
		Pattern[] patternsselected;
		int indexofpatterntotrain = -1;
		int[] indexesofpatternstotrain = new int[nopatterns];
		int counter;
		
		// turn all "selected" flags off
		for (int i = 0; i < patterns.length; i++) {
			patterns[i].selected = false;
		}

		while ( patternsnottrained > 0 ) {
			// choose patterns to be used for training and put them in the temp. pattern array
			noofpatternsselected = 0;
			while ( noofpatternsselected < nopatterns && patternsnottrained > 0 ) {
				patterntotrain = randomizer.random.nextInt(patternsnottrained);
				patternsnottrained--;
				// find the index of the pattern to be used
				counter = 0;
				for (int i = 0; i < patterns.length; i++) {
					if (!patterns[i].selected) {
						if (counter != patterntotrain) {
							counter++;
						}
						else if (counter == patterntotrain) {
							indexofpatterntotrain = i;
							break;
						}
					}
				}
				noofpatternsselected++;
				indexesofpatternstotrain[noofpatternsselected-1] = indexofpatterntotrain;
				patterns[indexofpatterntotrain].selected = true;
			}
			// train the net using the temp. pattern array
			patternsselected = null;
			patternsselected = new Pattern[noofpatternsselected];
			for (int i = 0; i < noofpatternsselected; i++) {
				patternsselected[i] = patterns[indexesofpatternstotrain[i]];
			}
			BatchTrainPatterns( patternsselected, rate);
		}
		// turn all "selected" flags off again
		for (int i = 0; i < patterns.length; i++) {
			patterns[i].selected = false;
		}
	}

	// trains the net using batch training
	// takes a number of patterns
	public void BatchTrainPatterns(Pattern[] patterns, double rate) {
		for (int i = 0; i < patterns.length; i++) {
			BatchTrain(rate, patterns[i]);
		}
		// update weights using cumulative values obtained during batch training
		for  ( int i = 0; i < neurons.length; i++ ) {
			if(neurons[i].layer != 0) {
				neurons[i].BatchUpdateWeights(patterns.length);
			}
		}
	}

	// trains the net using batch training
	// takes only one pattern
	public void BatchTrain(double rate, Pattern pattern) {
		// feed fw
		FeedForward(pattern.input);
		// train the output layer first
		for (int j = 0; j < layers[nolayers-1].neurons.length; j++) {
			layers[nolayers-1].neurons[j].OutputBatchTrain(rate, pattern.target[j]);
		}
		// train hidden layers
		for (int i = nolayers-2; i > 0; i--) {
			for (int j = 0; j < layers[i].neurons.length; j++) {
				layers[i].neurons[j].HiddenBatchTrain(rate);
			}
		}
	}
	

	// represents an array of neurons belonging to the same layer.
	class Layer implements Serializable {
		Neuron[] neurons;
		// constructs a layer object
		public Layer(int layerno) {
			int counter = 0;
			// see how many neurons there are in this layer
			for (int i = 0; i < NeuralNet.this.neurons.length; i++) {
				if (NeuralNet.this.neurons[i].layer == layerno) {counter++;}
			}
			// create an array of neurons
			this.neurons = new Neuron[counter];
			// place neurons
			counter = 0; 
			for (int i = 0; i < NeuralNet.this.neurons.length; i++) {
				if (NeuralNet.this.neurons[i].layer == layerno) {
					this.neurons[counter++] = NeuralNet.this.neurons[i];
				}
			}
		}
	}

}