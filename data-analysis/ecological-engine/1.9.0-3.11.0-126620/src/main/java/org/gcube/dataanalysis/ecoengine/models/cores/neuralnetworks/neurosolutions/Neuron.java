package org.gcube.dataanalysis.ecoengine.models.cores.neuralnetworks.neurosolutions;

import java.io.Serializable;

public class Neuron implements Serializable {

	public int id; // to be used in saveconfig method
	public double threshold;
	private double prevthreshold;
	public int layer;
	public double output;
	public char axonfamily; // logistic? hyperbolic tangent? linear?
	protected double momentumrate;
	protected double axonfuncflatness; // if the axon func. is a curve like sigmoid, this indicates the flatness paramater.
	protected double learningratecoefficient; // i.e.: if the learning rate is .1 and this value is 1.5, actual learning rate will be .1 * 1.5 = .15
	public Neuron[] neuronsout; // array of neurons which take this neuron's output. To be used during back propagation
	public Neuron[] neuronsin; // array of neurons from which this neuron takes outputs. To be used during feedforward
	public Synapse[] synapsesout; // array of synapses which take this neuron's output. To be used during back propagation
	public Synapse[] synapsesin; // array of synapses from which this neuron takes outputs. To be used during feedforward
	protected double error; // to be used during bp.
	protected double cumulthresholddiff; // cumulate changes in threshold here during batch training

	// constructor for input neurons
	public Neuron (int id) {
		this.id = id;
		this.layer = 0;
	}
		
	// another constructor
	public Neuron (int id, int layer, double axonfuncflatness, char axonfamily, double momentumrate, double learningratecoefficient, Randomizer randomizer) {
		output = 0;
		this.axonfamily = axonfamily;
		threshold = randomizer.Uniform(-1,1);
		prevthreshold = threshold;
		this.id = id;
		this.layer = layer;
		this.momentumrate = momentumrate;
		this.axonfuncflatness = axonfuncflatness;
		this.learningratecoefficient = learningratecoefficient;
		cumulthresholddiff = 0;
	}

	// this method constructs neuronin and neuronout arrays in order to determine the relationships of this neuron with others.
	// should be called during the construction of the net
	public void InsOuts (Neuron[] neuronsin, Neuron[] neuronsout, Synapse[] synapsesin, Synapse[] synapsesout) {
		
		this.neuronsin = neuronsin;
		this.neuronsout = neuronsout;
		this.synapsesin = synapsesin;
		this.synapsesout = synapsesout;
	}

	// updates the output and the activation according to the inputs
	public void UpdateOutput () {
		// first sum inputs and find the activation
		double activation = 0;
		for (int i = 0; i < neuronsin.length; i++) {
			activation += neuronsin[i].output * synapsesin[i].weight;
		}
		activation += -1 * threshold;
		// calculate the output using the activation function of this neuron
		switch (axonfamily) {
			case 'g': // logistic
				output = 1 / ( 1 + Math.exp( - activation / axonfuncflatness ) );
				break;
			case 't': // hyperbolic tangent (tanh)
				output = ( 2 / ( 1 + Math.exp( - activation / axonfuncflatness ) ) ) - 1;
				/* // alternatively,
				double temp = Math.exp( 2 * ( activation / axonfuncflatness ) ) ; // so that the computation is faster
				output = ( temp - 1 ) / ( temp + 1 );
				*/
				break;
			case 'l': // linear
				output = activation;
				break;
		}
	}

// Incremantal train ------------------------------------------

	// trains the output neurons using incremental training
	public void OutputIncrementalTrain (double rate, double target) {
		this.error = (target - output) * Derivative();
		IncrementalUpdateWeights(rate);
	}

	// trains the hidden neurons using incremental training
	public void HiddenIncrementalTrain (double rate) {
		// first compute the error
		double temp_diff = 0;
		for (int i = 0; i < neuronsout.length; i++) {
			temp_diff += neuronsout[i].error * synapsesout[i].prevweight;
		}
		error = temp_diff * Derivative();
		IncrementalUpdateWeights(rate);
	}

	// updates weights according to the error
	private void IncrementalUpdateWeights (double rate) {
		double temp_weight;
		for (int i = 0; i < synapsesin.length; i++) {
			temp_weight = synapsesin[i].weight;
			synapsesin[i].weight += (rate * learningratecoefficient * error * neuronsin[i].output) + ( momentumrate * ( synapsesin[i].weight - synapsesin[i].prevweight ) );
			synapsesin[i].prevweight = temp_weight;
			if (synapsesin[i].cumulweightdiff != 0) {synapsesin[i].cumulweightdiff = 0;}
		}
		temp_weight = threshold;
		threshold += ( rate * learningratecoefficient * error * -1 ) + ( momentumrate * ( threshold - prevthreshold ) );
		prevthreshold = temp_weight;
		if (cumulthresholddiff != 0) {cumulthresholddiff = 0;}
	}

// Batch train ------------------------------------------------

	// trains the output neurons using batch training
	public void OutputBatchTrain (double rate, double target) {
		this.error = (target - output) * Derivative();
		BatchCumulateWeights(rate);
	}

	// trains the hidden neurons using batch training
	public void HiddenBatchTrain (double rate) {
		// first compute the error
		double temp_diff = 0;
		for (int i = 0; i < neuronsout.length; i++) {
			temp_diff += neuronsout[i].error * synapsesout[i].weight;
		}
		error = temp_diff * Derivative();
		BatchCumulateWeights(rate);
	}

	// cumulates weights according to the error
	private void BatchCumulateWeights (double rate) {
		double temp_diff;
		for (int i = 0; i < synapsesin.length; i++) {
			synapsesin[i].cumulweightdiff += rate * learningratecoefficient * error * neuronsin[i].output;
		}
		cumulthresholddiff += rate * learningratecoefficient * error * -1;
	}

	// updates weights according to the cumulated weights
	public void BatchUpdateWeights (int noofepochs) {
		double temp_weight;
		for (int i = 0; i < synapsesin.length; i++) {
			temp_weight = synapsesin[i].weight;
			synapsesin[i].weight +=  ( synapsesin[i].cumulweightdiff / noofepochs ) + ( momentumrate * ( synapsesin[i].weight - synapsesin[i].prevweight ) );
			synapsesin[i].prevweight = temp_weight;
			synapsesin[i].cumulweightdiff = 0;
		}
		temp_weight = threshold;
		threshold += ( cumulthresholddiff / noofepochs )  + ( momentumrate * ( threshold - prevthreshold ) );
		prevthreshold = temp_weight;
		cumulthresholddiff = 0;
	}
	
// ------------------------------------------------------------

	// returns the value of the derivative of the activation function for the last activation value
	public double Derivative () {
		double temp_derivative;
		switch (axonfamily) {
			case 'g': // logistic
				temp_derivative = ( output * ( 1 - output ) ) / axonfuncflatness; break;
			case 't': // hyperbolic tangent
				temp_derivative = ( 1 - Math.pow( output , 2 ) ) / ( 2 * axonfuncflatness ); break;
				// temp_derivative = Math.pow( ( 2 / ( Math.exp(activation / axonfuncflatness ) + Math.exp( - activation / axonfuncflatness ) ) ) ,2 ) / axonfuncflatness; break;
			case 'l': // linear
				temp_derivative = 1; break;
			default: temp_derivative = 0; break;
		}
		return temp_derivative;
	}

}
