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

	public static void main(String[] args) throws Exception {
		try {
			System.out.println("Generic Node: Process Started ");
			try {
				for (int i = 0; i < args.length; i++) {
					System.out.println("Generic Node: RECEIVED INPUT " + args[i]);
				}
			} catch (Exception e) {
			}

			System.out.println("Generic Node: checking arguments from " + args[0]);
			String[] rargs = args[0].split("_");

			int order = Integer.parseInt(rargs[0]);
			System.out.println("Generic Node: order: " + order);
			int chunksize = Integer.parseInt(rargs[1]);
			System.out.println("Generic Node: chunk: " + chunksize);
			int speciesOrder = Integer.parseInt(rargs[2]);
			System.out.println("Generic Node: species: " + speciesOrder);
			int speciesChunksize = Integer.parseInt(rargs[3]);
			System.out.println("Generic Node: species chunk size: " + speciesChunksize);
			String path = rargs[4];
			System.out.println("Generic Node: path: " + path);
			String algorithmClass = rargs[5];
			System.out.println("Generic Node: algorithmClass: " + algorithmClass);
			Boolean duplicate = Boolean.parseBoolean(rargs[6]);
			System.out.println("Generic Node: duplicate message: " + duplicate);
			String nodeConfiguration = rargs[7];
			System.out.println("Generic Node: config: " + nodeConfiguration);
			String logfile = args[1];
			System.out.println("Generic Node: logfile: " + logfile);

			System.out.println("Generic Node: executing class");

			ActorNode node = (ActorNode) Class.forName(algorithmClass).newInstance();

			node.executeNode(order, chunksize, speciesOrder, speciesChunksize, duplicate, path, nodeConfiguration, logfile);
		} catch (Exception e) {
			System.out.println("ERROR " + e.getMessage());
			System.out.println(e);
		}
	}

}
