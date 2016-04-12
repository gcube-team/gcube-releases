package org.gcube.dataanalysis.ecoengine.spatialdistributions;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.models.cores.neuralnetworks.Neural_Network;

public class FeedForwardNeuralNetworkDistribution extends BayesianDistribution{
	
	private Neural_Network neuralnet;
	
	@Override
	public float calcProb(Object mainInfo, Object area) {
		
		Object[] vector = (Object[]) area;
		
		double[] features = new double[neuralnet.getNumberOfInputs()-1];
		
		for (int i=0;i<vector.length;i++){
			if (vector[i]==null)
				vector[i]=0;
			if (i<features.length)
				features[i]=Double.parseDouble(""+vector[i]);
		}
		
		return (float) neuralnet.getCorrectValueFromOutput(neuralnet.propagate(features)[0]);
//		return 0;
	}

	@Override
	public void singleStepPreprocess(Object mainInfo, Object area) {
		//load a Neural Network for this information
		String persistencePath = config.getPersistencePath();
//		String filename = persistencePath + Neural_Network.generateNNName(""+mainInfo, userName, modelName);
		String filename = modelFile.getAbsolutePath();
		neuralnet = Neural_Network.loadNN(filename);
		AnalysisLogger.getLogger().debug("Using neural network with emission range: ("+neuralnet.minfactor+" ; "+neuralnet.maxfactor+"" );
	}

	@Override
	public void singleStepPostprocess(Object mainInfo, Object allAreasInformation) {
	}
	
	@Override
	public String getName() {
		return "FEED_FORWARD_A_N_N_DISTRIBUTION";
	}

	@Override
	public String getDescription() {
		return "A Bayesian method using a Feed Forward Neural Network to simulate a function from the features space (R^n) to R. A modeling algorithm that relies on Neural Networks to simulate a real valued function. It accepts as input a table containing the training dataset and some parameters affecting the algorithm behaviour such as the number of neurons, the learning threshold and the maximum number of iterations.";
	}


}
