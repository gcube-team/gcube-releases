package org.gcube.dataanalysis.ecoengine.models.cores.neuralnetworks.neurosolutions;

import java.io.Serializable;

public class Synapse implements Serializable 
{
	public double weight;
	public double prevweight; // to be used during bp
	public double cumulweightdiff; // cumulate changes in weight here during batch training	
	public Neuron sourceunit;
	public Neuron targetunit;
	
	// constructor
	public Synapse (Neuron sourceunit, Neuron targetunit, Randomizer randomizer)
	{
		this.sourceunit = sourceunit;
		this.targetunit = targetunit;
		cumulweightdiff = 0;
		weight = randomizer.Uniform(-1,1);
		prevweight = weight;
	}
}