package org.gcube.dataanalysis.ecoengine.interfaces;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;

public abstract class SpatialProbabilityDistributionNode implements GenericAlgorithm {
			
	//initialization of the  single node
	public abstract void initSingleNode(AlgorithmConfiguration config);

	//get the internal processing status for the single step calculation
	public abstract float getInternalStatus();
	
	//execute a single node
	public abstract  int executeNode(int cellStarIndex, int numberOfCellsToProcess, int speciesStartIndex, int numberOfSpeciesToProcess, boolean duplicate, String sandboxFolder, String nodeConfigurationFileObject, String logfileNameToProduce);
	
	// An initialization phase in which the inputs are initialized
	public abstract void setup(AlgorithmConfiguration config) throws Exception;
	
	//get overall number of species to process
	public abstract  int getNumberOfSpecies();

	//get overall number of geographical information to process
	public abstract int getNumberOfGeoInfo();

	//get overall number of processed species
	public abstract int getNumberOfProcessedSpecies();

	//stop the execution of the node
	public abstract  void stop();
	
	//prostprocess after the whole calculation : reduce operation
	public abstract  void postProcess(boolean manageDuplicates, boolean manageFault);


	
}
