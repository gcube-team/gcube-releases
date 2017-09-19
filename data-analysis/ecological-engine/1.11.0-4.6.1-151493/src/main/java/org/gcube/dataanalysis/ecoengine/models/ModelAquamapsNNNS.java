package org.gcube.dataanalysis.ecoengine.models;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.DatabaseParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.Model;
import org.gcube.dataanalysis.ecoengine.models.cores.neuralnetworks.neurosolutions.NeuralNet;
import org.gcube.dataanalysis.ecoengine.models.cores.neuralnetworks.neurosolutions.Pattern;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.hibernate.SessionFactory;

public class ModelAquamapsNNNS implements Model {

	@Override
	public ALG_PROPS[] getProperties() {
		ALG_PROPS[] props = { ALG_PROPS.SPECIES_MODEL };
		return props;
	}

	@Override
	public String getName() {
		return "AQUAMAPSNNNS";
	}

	@Override
	public String getDescription() {
		return "Aquamaps Trained using Neural Networks";
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		List<TableTemplates> templatesOccurrences = new ArrayList<TableTemplates>();
		templatesOccurrences.add(TableTemplates.OCCURRENCE_AQUAMAPS);
		
		InputTable p1 = new InputTable(templatesOccurrences,"AbsenceDataTable","A Table containing absence points");
		InputTable p2 = new InputTable(templatesOccurrences,"PresenceDataTable","A Table containing positive occurrences");
		PrimitiveType p3 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, "SpeciesName","Species Code of the fish the NN will correspond to","Fis-30189");
		PrimitiveType p4 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, "LayersNeurons","a list of neurons number for each inner layer separated by comma","100,2");

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
		return parameters;
	}
	@Override
	public float getVersion() {
		return 0;
	}

	@Override
	public void setVersion(float version) {
	}

	SessionFactory connection;
	String fileName;
	String presenceTable;
	String absenceTable;
	float status;
	
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

		fileName = Input.getPersistencePath() + "neuralnetwork_" + Input.getParam("SpeciesName") + "_" + Input.getParam("UserName");
		presenceTable = Input.getParam("PresenceDataTable");
		absenceTable = Input.getParam("AbsenceDataTable");

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
		connection.close();
	}

	private String takeElementsQuery = "select depthmean,depthmax,depthmin, sstanmean,sbtanmean,salinitymean,salinitybmean, primprodmean,iceconann,landdist,oceanarea from %1$s d where oceanarea>0 limit 449";

	@Override
	public void train(AlgorithmConfiguration Input, Model previousModel) {

		try {
			// take all presence inputs
			List<Object> presences = DatabaseFactory.executeSQLQuery(String.format(takeElementsQuery, presenceTable), connection);
			// take all absence inputs
			List<Object> absences = DatabaseFactory.executeSQLQuery(String.format(takeElementsQuery, absenceTable), connection);
			int numbOfPresence = presences.size();
			int numbOfAbsence = absences.size();

			// setup Neural Network
			int numberOfInputNodes = 11;
			int numberOfOutputNodes = 1;
//			int[] innerLayers = Neural_Network.setupInnerLayers(100,30,10);
//			int[] innerLayers = NeuralNet.setupInnerLayers(100,10,30);
			int[] innerLayers = NeuralNet.setupInnerLayers(140);
			NeuralNet nn = new NeuralNet(numberOfInputNodes, numberOfOutputNodes, innerLayers);

			
			int numberOfInputs = numbOfPresence + numbOfAbsence;
			double[][] in = new double[numberOfInputs][];
			double[][] out = new double[numberOfInputs][];
			// build NN input
			for (int i = 0; i < numbOfPresence; i++) {
				in[i] = NeuralNet.preprocessObjects((Object[]) presences.get(i));
				out[i] = nn.getPositiveCase();
				Pattern pattern = new Pattern(in[i], out[i]);
				nn.IncrementalTrain(.2, pattern);
				AnalysisLogger.getLogger().debug("-> "+i);
			}
			for (int i = numbOfPresence; i < numberOfInputs; i++) {
				in[i] = NeuralNet.preprocessObjects((Object[]) absences.get(i-numbOfPresence));
				out[i] = nn.getNegativeCase();
				Pattern pattern = new Pattern(in[i], out[i]);
				nn.IncrementalTrain(.2, pattern);
				AnalysisLogger.getLogger().debug("-> "+i);
			}
			 
			
			/*
			int numberOfInputs = numbOfPresence;
			double[][] in = new double[numberOfInputs][];
			double[][] out = new double[numberOfInputs][];
			// build NN input
			for (int i = 0; i < numbOfPresence; i++) {
				in[i] = Neural_Network.preprocessObjects((Object[]) presences.get(i));
				out[i] = nn.getPositiveCase();
			}
			*/
			
			// train the NN
			save(fileName, nn);
			
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().error("ERROR during training");
		}
		status = 100f;
	}

	@Override
	public StatisticalType getOutput() {
		PrimitiveType p = new PrimitiveType(File.class.getName(), new File(fileName), PrimitiveTypes.FILE, "NeuralNetwork","Trained Neural Network");
		return p;
	}
	

	@Override
	public void stop() {

	}

	
	public static void save(String nomeFile, NeuralNet nn) {

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
