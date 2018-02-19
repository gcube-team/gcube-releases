package org.gcube.dataanalysis.ecoengine.interfaces;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;

public abstract class ActorNode implements GenericAlgorithm{

	// initialization of ta single node
	public abstract void initSingleNode(AlgorithmConfiguration config);

	// get the internal processing status for the single step calculation
	public abstract float getInternalStatus();

	// execute a single node
	public abstract int executeNode(int leftStartIndex, int numberOfLeftElementsToProcess, int rightStartIndex, int numberOfRightElementsToProcess, boolean duplicate, String sandboxFolder, String nodeConfigurationFileObject, String logfileNameToProduce);

	// An initialization phase in which the inputs are initialized
	public abstract void setup(AlgorithmConfiguration config) throws Exception;

	// get overall number of species to process
	public abstract int getNumberOfRightElements();

	// get overall number of geographical information to process
	public abstract int getNumberOfLeftElements();

	// stop the sexecution of the node
	public abstract void stop();

	// prostprocess after the whole calculation : reduce operation
	public abstract void postProcess(boolean manageDuplicates, boolean manageFault);

}
