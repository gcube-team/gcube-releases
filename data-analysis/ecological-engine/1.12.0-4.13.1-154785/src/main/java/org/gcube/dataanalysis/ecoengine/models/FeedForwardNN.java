package org.gcube.dataanalysis.ecoengine.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.Model;
import org.gcube.dataanalysis.ecoengine.models.cores.neuralnetworks.Neural_Network;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;

public class FeedForwardNN extends ModelAquamapsNN{
	
	
	@Override
	public String getName() {
		return "FEED_FORWARD_ANN";
	}

	@Override
	public String getDescription() {
		return "A method to train a generic Feed Forward Artifical Neural Network in order to simulate a function from the features space (R^n) to R. Uses the Back-propagation method. Produces a trained neural network in the form of a compiled file which can be used in the FEED FORWARD NEURAL NETWORK DISTRIBUTION algorithm.";
	}
	
	
	@Override
	public float getStatus() {
		if (status==100)
			return status;
		else if ((nn!=null) && (nn.status>0))
			return nn.status*100f;
		else
		return status;
	}
	
	protected static String TrainingDataSet = "TrainingDataSet";
	protected String trainingDataSet;
	protected static String TrainingDataSetColumns = "TrainingColumns";
	protected String trainingDataSetColumns;
	protected static String TrainingDataSetTargetColumn = "TargetColumn";
	protected String trainingColumn;
	protected String dbcolumns;
	protected String[] dbcolumnsList;
	protected static String LayersNeurons = "LayersNeurons";
	protected static String Reference = "Reference";
	protected static String LearningThreshold = "LearningThreshold";
	protected static String MaxIterations = "MaxIterations";
	protected static String ModelName = "ModelName";
	protected static String UserName= "UserName";
	protected float learningThr;
	protected int maxiter; 
			
	@Override
	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		List<TableTemplates> templatesOccurrences = new ArrayList<TableTemplates>();
		templatesOccurrences.add(TableTemplates.GENERIC);
		
		InputTable p1 = new InputTable(templatesOccurrences,TrainingDataSet,"a table containing real values colums for training the ANN");
		ColumnTypesList p2 = new ColumnTypesList(TrainingDataSet, TrainingDataSetColumns, "column names to use as features vectors", false);
		ColumnType p3 = new ColumnType(TrainingDataSet, TrainingDataSetTargetColumn, "the column to use as target", "probability", false);
		PrimitiveTypesList p4 = new PrimitiveTypesList(Integer.class.getName(), PrimitiveTypes.NUMBER,LayersNeurons,"a list of neurons number for each inner layer",true);
		PrimitiveType p5 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, Reference,"the phenomenon this ANN is trying to model - can be a generic identifier. Put 1 for not specifying","1");
		PrimitiveType p6 = new PrimitiveType(Float.class.getName(), null, PrimitiveTypes.NUMBER, LearningThreshold,"the learning threshold for this ANN","0.01");
		PrimitiveType p7 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, MaxIterations,"the maximum number of iterations in the training","100");
		PrimitiveType p11 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, ModelName,"The name of this Neural Network - insert without spaces","neuralnet_");
		ServiceType p10 = new ServiceType(ServiceParameters.USERNAME, UserName,"LDAP username");
		
		parameters.add(p1);
		parameters.add(p2);
		parameters.add(p3);
		parameters.add(p4);
		parameters.add(p5);
		parameters.add(p6);
		parameters.add(p7);
		parameters.add(p11);
		parameters.add(p10);
		
		DatabaseType.addDefaultDBPars(parameters);
		return parameters;
	}

	@Override
	public void init(AlgorithmConfiguration config, Model previousModel) {
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		// init the database
		try {
			connection = DatabaseUtils.initDBSession(config); 
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().trace("ERROR initializing connection");
		}
		fileName = config.getPersistencePath()+Neural_Network.generateNNName(config.getParam(Reference), config.getParam(UserName), config.getParam(ModelName));
		trainingDataSet = config.getParam(TrainingDataSet);
		trainingDataSetColumns = config.getParam(TrainingDataSetColumns);
		trainingColumn = config.getParam(TrainingDataSetTargetColumn);
		
		learningThr = Float.parseFloat(config.getParam(LearningThreshold));
		maxiter = Integer.parseInt(config.getParam(MaxIterations));
		
		String layersNeurons$ = config.getParam(LayersNeurons);
		if ((layersNeurons$!=null)&&(layersNeurons$.length()>0))
		{
			String [] split = layersNeurons$.split(AlgorithmConfiguration.getListSeparator());
			layersNeurons = new int[split.length];
			boolean nullhyp=true;
			for (int i = 0;i<split.length;i++){
				layersNeurons[i] = Integer.parseInt(split[i]);
				if ((layersNeurons[i]>0)&&(nullhyp))
					nullhyp=false;
			}
			
			if (nullhyp)
				layersNeurons=null;
			
				
		}
		dbcolumns = "";
		dbcolumnsList = trainingDataSetColumns.split(AlgorithmConfiguration.getListSeparator());
		for (int i=0;i<dbcolumnsList.length;i++){
			dbcolumns+=dbcolumnsList[i];
			if (i<dbcolumnsList.length-1)
				dbcolumns+=",";
		}
		
	}

	private String takeElementsQuery = "select %1$s from %2$s d order by %3$s";
	protected Neural_Network nn;
	protected double maxfactor=1;
	protected double minfactor=0;
	@Override
	public void train(AlgorithmConfiguration Input, Model previousModel) {
		try {
			// take all features input vectors
			String query = String.format(takeElementsQuery, trainingColumn+","+dbcolumns,trainingDataSet,trainingColumn);
			AnalysisLogger.getLogger().debug("Query to execute: "+query);
			List<Object> features = DatabaseFactory.executeSQLQuery(query, connection);
			int numbOfFeatures = features.size();

			//get reduction factor for normalizing the outputs
			List<Object> maxmin = DatabaseFactory.executeSQLQuery("select max("+trainingColumn+"), min("+trainingColumn+")  from "+trainingDataSet, connection);
			maxfactor = Double.parseDouble(""+((Object[])maxmin.get(0))[0]);
			minfactor = Double.parseDouble(""+((Object[])maxmin.get(0))[1]);
			AnalysisLogger.getLogger().debug("Calculated max: "+maxfactor+" min: "+minfactor);
			// setup Neural Network
			int numberOfInputNodes = dbcolumnsList.length;
			int numberOfOutputNodes = 1;
			
			AnalysisLogger.getLogger().debug("Training the ANN with "+numbOfFeatures+" training data and "+numberOfInputNodes+" inputs");
			if (layersNeurons!=null){
				int[] innerLayers = Neural_Network.setupInnerLayers(layersNeurons);
				nn = new Neural_Network(numberOfInputNodes, numberOfOutputNodes, innerLayers, Neural_Network.ACTIVATIONFUNCTION.SIGMOID);
			}
			else
				nn = new Neural_Network(numberOfInputNodes, numberOfOutputNodes, Neural_Network.ACTIVATIONFUNCTION.SIGMOID);
			
			nn.maxfactor=maxfactor;
			nn.minfactor=minfactor;
			nn.setThreshold(learningThr);
			nn.setCycles(maxiter);
			AnalysisLogger.getLogger().debug("network parameters: M: "+maxfactor+", m: "+minfactor+", lt: "+learningThr+", it: "+maxiter);
			AnalysisLogger.getLogger().debug("topology: "+nn.griglia.length+"X"+nn.griglia[0].length);
			
			AnalysisLogger.getLogger().debug("Features preprocessing");
			double[][] in = new double[numbOfFeatures][];
			double[][] out = new double[numbOfFeatures][];
			// build NN input
			for (int i = 0; i < numbOfFeatures; i++) {
//				out[i] = new double[0];
				Object[] feats = (Object[]) features.get(i);
				in[i] = Neural_Network.preprocessObjects(Arrays.copyOfRange((Object[]) features.get(i), 1, feats.length));
				out[i] = Neural_Network.preprocessObjects(Arrays.copyOfRange((Object[]) features.get(i), 0, 1));
				//apply reduction factor
//				AnalysisLogger.getLogger().debug("Output Transformed from "+out[i][0]);
				out[i][0] =nn.getCorrectValueForOutput(out[i][0]); 
//				AnalysisLogger.getLogger().debug("To "+out[i][0]);
			}
			AnalysisLogger.getLogger().debug("Features were correctly preprocessed - Training");
			// train the NN
			nn.train(in, out);
			learningscore=nn.en;
			AnalysisLogger.getLogger().error("Final learning error: "+nn.en);
			AnalysisLogger.getLogger().debug("Saving Network");
			save(fileName, nn);
			AnalysisLogger.getLogger().debug("Done");
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().error("ERROR during training");
		}
		
		status = 100f;
	}

	
}
