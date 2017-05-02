package org.gcube.dataanalysis.executor.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.interfaces.ActorNode;
import org.gcube.dataanalysis.ecoengine.interfaces.Generator;
import org.gcube.dataanalysis.ecoengine.interfaces.GenericAlgorithm;
import org.gcube.dataanalysis.executor.job.management.DistributedProcessingAgentWPS;

public class D4ScienceDistributedProcessing implements Generator {
	
	public static int maxMessagesAllowedPerJob = 20;
	public static boolean forceUpload = true;
	public static String defaultContainerFolder = "PARALLEL_PROCESSING";
	protected AlgorithmConfiguration config;
	protected ActorNode distributedModel;
	protected String mainclass;
	DistributedProcessingAgentWPS agent;
	
	public D4ScienceDistributedProcessing(){
	}
	
	public D4ScienceDistributedProcessing(AlgorithmConfiguration config) {
		this.config = config;
	
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
	}

	public void compute() throws Exception {
		try {
			agent.compute();
			distributedModel.postProcess(agent.hasResentMessages(),false);
		} catch (Exception e) {
			try{distributedModel.postProcess(false,true);}catch(Exception ee){}
			AnalysisLogger.getLogger().error("ERROR: An Error occurred ", e);
			throw e;
		} finally {
			shutdown();
		}
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		
		List<StatisticalType> distributionModelParams = new ArrayList<StatisticalType>();
		distributionModelParams.add(new ServiceType(ServiceParameters.USERNAME,"ServiceUserName","The final user Name"));
		
		return distributionModelParams;
	}


	@Override
	public String getResources() {
		return agent.getResources();
	}

	@Override
	public float getStatus() {
		return agent.getStatus();
	}

	@Override
	public StatisticalType getOutput() {
		return distributedModel.getOutput();
	}

	@Override
	public ALG_PROPS[] getSupportedAlgorithms() {
		ALG_PROPS[] p = { ALG_PROPS.PHENOMENON_VS_PARALLEL_PHENOMENON};
		return p;
	}

	@Override
	public INFRASTRUCTURE getInfrastructure() {
		return INFRASTRUCTURE.D4SCIENCE;
	}

	@Override
	public void init() throws Exception {

		Properties p = AlgorithmConfiguration.getProperties(config.getConfigPath() + AlgorithmConfiguration.nodeAlgorithmsFile);
		String model = config.getModel();
		String algorithm = null;
		if ((model!=null) && (model.length()>0))
			algorithm = model;
		else
			algorithm=config.getAgent();
		
		mainclass = p.getProperty(algorithm);
		distributedModel = (ActorNode) Class.forName(mainclass).newInstance();
		distributedModel.setup(config);
		String scope = config.getGcubeScope();
		AnalysisLogger.getLogger().info("Using the following scope for the computation:"+scope);
		String owner = config.getGcubeUserName();
		int leftNum = distributedModel.getNumberOfLeftElements();
		int rightNum = distributedModel.getNumberOfRightElements();
		if (config.getTaskID()==null || config.getTaskID().length()==0)
			config.setTaskID(""+UUID.randomUUID());
		
		agent =  new DistributedProcessingAgentWPS(config, scope, owner, mainclass, config.getPersistencePath(), algorithm, defaultContainerFolder, maxMessagesAllowedPerJob, forceUpload, leftNum, rightNum,config.getTaskID());
		agent.setLogger(AnalysisLogger.getLogger());
	}

	@Override
	public void setConfiguration(AlgorithmConfiguration config) {
		this.config = config;
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
	}

	@Override
	public void shutdown() {
		try {
			agent.shutdown();
		} catch (Exception e) {
		}
		try {
			distributedModel.stop();
		} catch (Exception e) {
		}
	}

	@Override
	public String getLoad() {
		return agent.getLoad();
	}

	@Override
	public String getResourceLoad() {
		return agent.getResourceLoad();
	}
	
	
	@Override
	public GenericAlgorithm getAlgorithm() {
		return distributedModel;
	}

	@Override
	public String getDescription() {
		return "A D4Science Cloud Processor";
	}

}
