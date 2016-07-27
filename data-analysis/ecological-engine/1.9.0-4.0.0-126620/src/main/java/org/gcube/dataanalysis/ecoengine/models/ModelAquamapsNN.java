package org.gcube.dataanalysis.ecoengine.models;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.DatabaseParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.Model;
import org.gcube.dataanalysis.ecoengine.models.cores.neuralnetworks.Neural_Network;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.hibernate.SessionFactory;

public class ModelAquamapsNN implements Model {

	@Override
	public ALG_PROPS[] getProperties() {
		ALG_PROPS[] props = { ALG_PROPS.SPECIES_MODEL };
		return props;
	}

	@Override
	public String getName() {
		return "AQUAMAPSNN";
	}

	@Override
	public String getDescription() {
		return "The AquaMaps model trained using a Feed Forward Neural Network. This is a method to train a generic Feed Forward Artifical Neural Network to be used by the AquaMaps Neural Network algorithm. Produces a trained neural network in the form of a compiled file which can be used later.";
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		List<TableTemplates> templatesOccurrences = new ArrayList<TableTemplates>();
		templatesOccurrences.add(TableTemplates.HCAF);
		
		InputTable p1 = new InputTable(templatesOccurrences,"AbsenceDataTable","A Table containing absence points");
		InputTable p2 = new InputTable(templatesOccurrences,"PresenceDataTable","A Table containing positive occurrences");
		PrimitiveType p3 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, "SpeciesName","Species Code of the fish the NN will correspond to","Fis-30189");
		PrimitiveTypesList p4 = new PrimitiveTypesList(Integer.class.getName(), PrimitiveTypes.NUMBER,"LayersNeurons","a list of neurons number for each inner layer",false);
		PrimitiveType p11 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, "NeuralNetworkName","The name of this Neural Network - insert without spaces","neuralnet_");
		
		DatabaseType p5 = new DatabaseType(DatabaseParameters.DATABASEUSERNAME, "DatabaseUserName", "db user name");
		DatabaseType p6 = new DatabaseType(DatabaseParameters.DATABASEPASSWORD, "DatabasePassword", "db password");
		DatabaseType p7 = new DatabaseType(DatabaseParameters.DATABASEDRIVER, "DatabaseDriver", "db driver");
		DatabaseType p8 = new DatabaseType(DatabaseParameters.DATABASEURL, "DatabaseURL", "db url");
		DatabaseType p9 = new DatabaseType(DatabaseParameters.DATABASEDIALECT, "DatabaseDialect", "db dialect");
		
		ServiceType p10 = new ServiceType(ServiceParameters.USERNAME, "UserName","LDAP username");
		
		parameters.add(p1);
		parameters.add(p2);
		parameters.add(p3);
		parameters.add(p4);
		parameters.add(p5);
		parameters.add(p6);
		parameters.add(p7);
		parameters.add(p8);
		parameters.add(p9);
		parameters.add(p10);
		parameters.add(p11);
		
		return parameters;
	}

	@Override
	public float getVersion() {
		return 0;
	}

	@Override
	public void setVersion(float version) {
	}

	protected SessionFactory connection;
	protected String fileName;
	protected String presenceTable;
	protected String absenceTable;
	protected float status;
	protected int[] layersNeurons = {100, 2};
	
	@Override
	public void init(AlgorithmConfiguration Input, Model previousModel) {
		AnalysisLogger.setLogger(Input.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		// init the database
		String defaultDatabaseFile = Input.getConfigPath() + AlgorithmConfiguration.defaultConnectionFile;

		Input.setDatabaseDriver(Input.getParam("DatabaseDriver"));
		Input.setDatabaseUserName(Input.getParam("DatabaseUserName"));
		Input.setDatabasePassword(Input.getParam("DatabasePassword"));
		Input.setDatabaseURL(Input.getParam("DatabaseURL"));

		try {
			connection = DatabaseFactory.initDBConnection(defaultDatabaseFile, Input);
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().trace("ERROR initializing connection");
		}

		fileName = Input.getPersistencePath() + "neuralnetwork_" + Input.getParam("SpeciesName") + "_" + Input.getParam("UserName")+"_"+Input.getParam("NeuralNetworkName").replace(" ", "");
		
		presenceTable = Input.getParam("PresenceDataTable");
		absenceTable = Input.getParam("AbsenceDataTable");

		String layersNeurons$ = Input.getParam("LayersNeurons");
		if ((layersNeurons$!=null)&&(layersNeurons$.length()>0))
		{
			String [] split = layersNeurons$.split(AlgorithmConfiguration.getListSeparator());
			layersNeurons = new int[split.length];
			for (int i = 0;i<split.length;i++){
				layersNeurons[i] = Integer.parseInt(split[i]);
			}
		}
				
	}

	@Override
	public String getResourceLoad() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResources() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getStatus() {
		return status;
	}



	@Override
	public void postprocess(AlgorithmConfiguration Input, Model previousModel) {
		AnalysisLogger.getLogger().debug("Closing DB Connection");
		try{
			connection.close();
		}catch(Exception e){
			AnalysisLogger.getLogger().debug("Error in Closing DB Connection "+e.getLocalizedMessage());
		}
	}

//	private String takeElementsQuery = "select depthmean,depthmax,depthmin, sstanmean,sbtanmean,salinitymean,salinitybmean, primprodmean,iceconann,landdist,oceanarea,centerlat,centerlong from %1$s d where oceanarea>0 limit 100";
	private String takeElementsQuery = "select depthmean,depthmax,depthmin, sstanmean,sbtanmean,salinitymean,salinitybmean, primprodmean,iceconann,landdist,oceanarea from %1$s d where oceanarea>0 limit 449";
//	private String takeAElementsQuery = "select depthmean,depthmax,depthmin, sstanmean,sbtanmean,salinitymean,salinitybmean, primprodmean,iceconann,landdist,oceanarea from %1$s d where oceanarea>0 limit 100";
	
	@Override
	public void train(AlgorithmConfiguration Input, Model previousModel) {

		try {
			// take all presence inputs
			List<Object> presences = DatabaseFactory.executeSQLQuery(String.format(takeElementsQuery, presenceTable), connection);
			// take all absence inputs
//			AnalysisLogger.getLogger().trace("presence "+String.format(takeElementsQuery, presenceTable));
//			AnalysisLogger.getLogger().trace("absence "+String.format(takeElementsQuery, absenceTable));
			List<Object> absences = DatabaseFactory.executeSQLQuery(String.format(takeElementsQuery, absenceTable), connection);
			int numbOfPresence = presences.size();
			int numbOfAbsence = absences.size();

			// setup Neural Network
			int numberOfInputNodes = 11;
			int numberOfOutputNodes = 1;
			int[] innerLayers = Neural_Network.setupInnerLayers(layersNeurons);
			Neural_Network nn = new Neural_Network(numberOfInputNodes, numberOfOutputNodes, innerLayers, Neural_Network.ACTIVATIONFUNCTION.SIGMOID);

			
			int numberOfInputs = numbOfPresence + numbOfAbsence;
			double[][] in = new double[numberOfInputs][];
			double[][] out = new double[numberOfInputs][];
			// build NN input
			for (int i = 0; i < numbOfPresence; i++) {
				in[i] = Neural_Network.preprocessObjects((Object[]) presences.get(i));
				out[i] = nn.getPositiveCase();
			}
			for (int i = numbOfPresence; i < numberOfInputs; i++) {
				in[i] = Neural_Network.preprocessObjects((Object[]) absences.get(i-numbOfPresence));
				out[i] = nn.getNegativeCase();
			}
			
			// train the NN
			nn.train(in, out);
			learningscore=nn.en;
			AnalysisLogger.getLogger().error("Final learning error: "+nn.en);
			save(fileName, nn);
			
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().error("ERROR during training");
		}
		status = 100f;
	}

	
	double learningscore =0;
	@Override
	public StatisticalType getOutput() {
		LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
		PrimitiveType p = new PrimitiveType(File.class.getName(), new File(fileName), PrimitiveTypes.FILE, "NeuralNetwork","Trained Neural Network");
		map.put("Learning", new PrimitiveType(String.class.getName(), "" + learningscore, PrimitiveTypes.STRING, "Learning Score", ""));
		map.put("NeuralNetwork", p);
		PrimitiveType outputm = new PrimitiveType(LinkedHashMap.class.getName(), map, PrimitiveTypes.MAP, "ResultsMap", "Results Map");
		return outputm;
	}
	
	@Override
	public void stop() {

	}

	
	public static void save(String nomeFile, Neural_Network nn) {

		File f = new File(nomeFile);
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(stream);
			oos.writeObject(nn);
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().error("ERROR in writing object on file: " + nomeFile);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
		AnalysisLogger.getLogger().trace("OK in writing object on file: " + nomeFile);
	}
	
}
