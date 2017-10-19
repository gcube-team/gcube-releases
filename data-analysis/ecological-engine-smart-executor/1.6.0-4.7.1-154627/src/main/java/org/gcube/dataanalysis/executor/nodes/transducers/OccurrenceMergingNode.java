package org.gcube.dataanalysis.executor.nodes.transducers;

import java.io.File;
import java.util.List;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ActorNode;
import org.gcube.dataanalysis.ecoengine.transducers.OccurrencePointsMerger;
import org.gcube.dataanalysis.ecoengine.utils.Transformations;
import org.gcube.dataanalysis.executor.generators.D4ScienceDistributedProcessing;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccurrenceMergingNode extends ActorNode {

	private static Logger logger = LoggerFactory.getLogger(OccurrenceMergingNode.class);
	
	// variables
	protected AlgorithmConfiguration currentconfig;
	protected SessionFactory dbHibConnection;
	OccurrencePointsMerger processor;
	public int prevbroadcastTimePeriod;
	public int prevmaxNumberOfStages;
	public int prevmaxMessages;
	float status;

	@Override
	public ALG_PROPS[] getProperties() {
		ALG_PROPS[] p = { ALG_PROPS.PHENOMENON_VS_PARALLEL_PHENOMENON};
		return p;
	}

	@Override
	public String getName() {
		return "OCCURRENCE_PROCESSOR";
	}

	@Override
	public String getDescription() {
		return processor.getDescription();
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		return processor.getInputParameters();
	}

	@Override
	public StatisticalType getOutput() {
		return processor.getOutput();
	}

	@Override
	public void initSingleNode(AlgorithmConfiguration config) {
		
	}

	@Override
	public float getInternalStatus() {
		return 0;
	}

	public OccurrenceMergingNode() {
		processor = new OccurrencePointsMerger();
	}
	
	@Override
	public int executeNode(int leftStartIndex, int numberOfLeftElementsToProcess, int rightStarIndex, int numberOfRightElementsToProcess, boolean duplicate, String sandboxFolder, String nodeConfigurationFileObject, String logfileNameToProduce) {
		
		try{
			status = 0;
			String configFileAbsolutePath = new File(sandboxFolder,nodeConfigurationFileObject).getAbsolutePath();
			logger.debug("config file absolute path is {}", configFileAbsolutePath);
			AlgorithmConfiguration config = Transformations.restoreConfig(configFileAbsolutePath);
			config.setConfigPath(sandboxFolder);
			processor.setConfiguration(config);
			logger.info("Initializing variables");
			processor.init();
			logger.info("Initializing DB");
			processor.initDB(false);
			status = 0.5f;
			processor.takeRange(leftStartIndex, numberOfLeftElementsToProcess, rightStarIndex, numberOfRightElementsToProcess);
			processor.computeRange();
			status = 1f;
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("warning: error in node execution "+e.getLocalizedMessage());
			return -1;
		}
		finally{
			stop();
		}
		return 0;
	}

	@Override
	public void setup(AlgorithmConfiguration config) throws Exception {
		processor.setConfiguration(config);
		processor.init();
		processor.initDB(true);
		processor.takeFullRanges();
		prevmaxMessages=D4ScienceDistributedProcessing.maxMessagesAllowedPerJob;
		D4ScienceDistributedProcessing.maxMessagesAllowedPerJob=100;
	}

	@Override
	public int getNumberOfRightElements() {
		return processor.getNumRightObjects();
	}

	@Override
	public int getNumberOfLeftElements() {
		return processor.getNumLeftObjects();
	}

	@Override
	public void stop() {
		processor.shutdown();
	}

	@Override
	public void postProcess(boolean manageDuplicates, boolean manageFault) {
		
		processor.shutdown();
		try {
			processor.postProcess();
		} catch (Exception e) {
			logger.info("Postprocessing Inapplicable");
		}
		
	}


}
