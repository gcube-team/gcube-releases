package org.gcube.dataanalysis.ecoengine.spatialdistributions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.models.cores.neuralnetworks.Neural_Network;
import org.hibernate.SessionFactory;

public class AquamapsNN extends AquamapsNative{

	private Neural_Network neuralnet;

	@Override
	public ALG_PROPS[] getProperties() {
		ALG_PROPS [] p = {ALG_PROPS.SPECIES_VS_CSQUARE_FROM_DATABASE};
		return p;
	}
	
	@Override
	public String getName() {
		return "AQUAMAPS_NATIVE_NEURAL_NETWORK";
	}

	@Override
	public String getDescription() {
		return "Aquamaps Native Algorithm calculated by a Neural Network. A distribution algorithm that relies on Neural Networks and AquaMaps data for native distributions to generate a table containing species distribution probabilities on half-degree cells.";
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = super.getInputParameters();
		
		PrimitiveType p1 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, "SpeciesName","Name of the Species for which the distribution has to be produced","Fis-30189");
//		PrimitiveType p2 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, "NeuralNetworkName","The name of the Neural Network","neuralnet_");
		PrimitiveType p2 = new PrimitiveType(File.class.getName(), null, PrimitiveTypes.FILE, "NeuralNetworkName","The file containing the Neural Network", "neuralnet_");
		ServiceType p3 = new ServiceType(ServiceParameters.USERNAME, "UserName","LDAP username");
	
		parameters.add(p1);
		parameters.add(p2);
		parameters.add(p3);
		
		return parameters;
	}

	@Override
	public void init(AlgorithmConfiguration config, SessionFactory dbHibConnection) {
		super.init(config,dbHibConnection);
		AnalysisLogger.getLogger().debug("Initializing ANNs");
		String persistencePath = config.getPersistencePath();
//		String filename = persistencePath + "neuralnetwork_" + config.getParam("SpeciesName") + "_" + config.getParam("UserName")+"_"+config.getParam("NeuralNetworkName").replace(" ", "");
		
		String nnname = config.getParam("NeuralNetworkName");
		AnalysisLogger.getLogger().debug("Init ANN in projection mode with filename: "+nnname);
		String filename = new File(nnname).getAbsolutePath();
		AnalysisLogger.getLogger().debug("ANN: using file name: "+filename);
		if (filename!=null)
			neuralnet = loadNN(filename);
	}

	@Override
	public float calcProb(Object mainInfo, Object area) {
		String species = getMainInfoID(mainInfo);
		String csquarecode = (String) ((Object[]) area)[0];
		Object[] wholevector = (Object[]) area;
		Object[] inputvector = new Object[wholevector.length - 6];
		for (int i = 0; i < inputvector.length; i++) {
			inputvector[i] = wholevector[i + 1];
//			AnalysisLogger.getLogger().debug(i+": "+inputvector[i]);
		}
//		AnalysisLogger.getLogger().debug("species vs csquare:" + species + " , " + csquarecode);
		float probability = 0;

//		if (csquarecode.equals("1000:102:2"))
			probability = propagate(inputvector);

		return probability;
	}

	private synchronized float propagate(Object[] inputvector) {
		double[] output = new double[1];

		try {
			output = neuralnet.propagate(Neural_Network.preprocessObjects(inputvector));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// double [] output = new double[1];
		float probability = (float) output[0];
		/*
		if (probability>0.1)
			AnalysisLogger.getLogger().debug(" Probability " + probability);
		*/
//		System.exit(0);
		return probability;
	}

	@Override
	public float getInternalStatus() {
		return 100;
	}

	public static synchronized Neural_Network loadNN(String nomeFile) {

		Neural_Network nn = null;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(nomeFile);
			ObjectInputStream ois = new ObjectInputStream(stream);
			nn = (Neural_Network) ois.readObject();
		} catch (Exception ex) {
			ex.printStackTrace();
			AnalysisLogger.getLogger().debug("Error in reading the object from file " + nomeFile + " .");
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}

		return nn;
	}

}
